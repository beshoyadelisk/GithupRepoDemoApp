package com.beshoyisk.copticorphanstask.presentation.log_in

import android.content.Intent
import android.content.IntentSender
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beshoyisk.copticorphanstask.data.remote.auth.SignInResult
import com.beshoyisk.copticorphanstask.domain.model.UserData
import com.beshoyisk.copticorphanstask.domain.model.toUserData
import com.beshoyisk.copticorphanstask.domain.repository.EmailPwAuthRepository
import com.beshoyisk.copticorphanstask.domain.repository.GoogleAuthRepository
import com.beshoyisk.copticorphanstask.domain.repository.UserRepository
import com.beshoyisk.copticorphanstask.util.Resource
import com.beshoyisk.copticorphanstask.util.isGoogleSignIn
import com.facebook.AccessToken
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val googleAuthRepository: GoogleAuthRepository,
    private val emailPwAuthRepository: EmailPwAuthRepository,
    private val userRepository: UserRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _state =
        MutableStateFlow(SignInState(isSignInSuccessful = firebaseAuth.currentUser != null))
    val state = _state.asStateFlow()

    fun updateEmail(email: String) {
        _state.update {
            it.copy(
                email = email,
                isEmailError = isValidEmail(email).not()
            )
        }
    }

    fun updatePassword(password: String) {
        _state.update {
            it.copy(
                password = password,
                isPasswordError = isValidPassword(password).not()
            )
        }
    }

    fun signInByFacebook(accessToken: AccessToken) {
        viewModelScope.launch {
            val credential = FacebookAuthProvider.getCredential(accessToken.token)
            val signInResult = try {
                val result = firebaseAuth.signInWithCredential(credential).await()
                if (result.user != null) {
                    SignInResult(data = result.user!!.toUserData(), null)
                } else {
                    SignInResult(data = null, errorMessage = "Authentication failed.")
                }
            } catch (ex: FirebaseAuthUserCollisionException) {
                SignInResult(data = null, errorMessage = ex.message)
            }
            onSignInResult(signInResult)
        }


    }

    fun signByEmailAndPw() {
        if (isDataNotValid()) {
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, signInError = null) }
            val result = emailPwAuthRepository.signInWithEmailAndPassword(
                email = state.value.email,
                password = state.value.password
            )
            val signInResult = when (result) {
                is Resource.Error -> {
                    SignInResult(data = null, result.message)
                }

                is Resource.Success -> {
                    val currentUser = firebaseAuth.currentUser!!
                    val userData = userRepository.getUserFromFirestore(currentUser)
                    SignInResult(data = userData, null)
                }
            }
            onSignInResult(signInResult)
        }
    }

    private fun isValidPassword(password: String): Boolean {
        return password.trim().isNotEmpty()
    }

    private fun isValidEmail(username: String): Boolean {
        return username.trim().isNotEmpty()
    }

    private fun isDataNotValid(): Boolean {
        val isPasswordError = isValidPassword(state.value.password).not()
        val isEmailError = isValidEmail(state.value.email).not()
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isPasswordError = isPasswordError,
                    isEmailError = isEmailError
                )
            }
        }
        return isPasswordError || isEmailError
    }

    fun onSignInResult(result: SignInResult) {
        _state.update {
            it.copy(
                isSignInSuccessful = result.data != null,
                signInError = result.errorMessage,
                isLoading = false
            )
        }
    }

    fun resetState() = _state.update { SignInState() }

    suspend fun getSignedInUser(): UserData? {
        val currentUser = firebaseAuth.currentUser
        return userRepository.getUserFromFirestore(currentUser ?: return null)
    }

    fun signInByIntent(intent: Intent) = viewModelScope.launch {
        val signInResult = googleAuthRepository.signInWithGoogle(intent)
        onSignInResult(signInResult)
    }

    suspend fun googleSignIn(): IntentSender? {
        try {
            return googleAuthRepository.signInWithGoogle()
        } catch (ex: ApiException) {
            ex.printStackTrace()
            if (ex.statusCode == 8) {
                _state.update { it.copy(signInError = "No Connection!") }
            }
        }
        return null
    }


    suspend fun signOut() {
        if (firebaseAuth.currentUser?.isGoogleSignIn() ?: return) {
            googleAuthRepository.signOut()
        } else {
            emailPwAuthRepository.signOut()
        }
    }

    companion object {
        private const val TAG = "LoginViewModelTAG"
    }

}



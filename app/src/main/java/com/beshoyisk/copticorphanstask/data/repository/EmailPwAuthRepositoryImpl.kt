package com.beshoyisk.copticorphanstask.data.repository

import com.beshoyisk.copticorphanstask.domain.repository.EmailPwAuthRepository
import com.beshoyisk.copticorphanstask.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EmailPwAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : EmailPwAuthRepository {
    override suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String
    ): Resource<Boolean> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Resource.Success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "Failed to sign in!")

        }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Resource<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(true)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            e.printStackTrace()
            Resource.Error("User not exist")
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "Failed to sign in!")
        }
    }

    override fun signOut() = auth.signOut()


}
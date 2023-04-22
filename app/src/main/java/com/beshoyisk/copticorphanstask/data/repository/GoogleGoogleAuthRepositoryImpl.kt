package com.beshoyisk.copticorphanstask.data.repository

import android.content.Intent
import android.content.IntentSender
import com.beshoyisk.copticorphanstask.data.remote.auth.SignInResult
import com.beshoyisk.copticorphanstask.domain.model.toUserData
import com.beshoyisk.copticorphanstask.domain.repository.GoogleAuthRepository
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException
import javax.inject.Inject

class GoogleAuthRepositoryImpl @Inject constructor(
    private val signInRequest: BeginSignInRequest,
    private val oneTapClient: SignInClient,
    private val auth: FirebaseAuth
) : GoogleAuthRepository {
    override suspend fun signInWithGoogle(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(signInRequest).await()
        } catch (ex: ApiException) {
            throw ex
        } catch (ex: Exception) {
            ex.printStackTrace()
            if (ex is CancellationException) throw ex
            null
        }
        return result?.pendingIntent?.intentSender
    }

    override suspend fun signInWithGoogle(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleId = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleId, null)
        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user
            SignInResult(data = user?.toUserData())
        } catch (ex: Exception) {
            ex.printStackTrace()
            if (ex is CancellationException) throw ex
            SignInResult(
                data = null,
                errorMessage = ex.message
            )
        }
    }

    override suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        } catch (ex: Exception) {
            ex.printStackTrace()
            if (ex is CancellationException) throw ex
        }
    }

}
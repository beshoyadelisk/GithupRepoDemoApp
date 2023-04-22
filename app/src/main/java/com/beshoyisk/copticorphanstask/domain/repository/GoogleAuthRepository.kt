package com.beshoyisk.copticorphanstask.domain.repository

import android.content.Intent
import android.content.IntentSender
import com.beshoyisk.copticorphanstask.data.remote.auth.SignInResult
import com.beshoyisk.copticorphanstask.domain.model.UserData

interface GoogleAuthRepository {
    suspend fun signInWithGoogle(): IntentSender?
    suspend fun signInWithGoogle(intent: Intent): SignInResult
    suspend fun signOut()
}

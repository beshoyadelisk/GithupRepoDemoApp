package com.beshoyisk.copticorphanstask.domain.repository

import com.beshoyisk.copticorphanstask.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface EmailPwAuthRepository {
    suspend fun signUpWithEmailAndPassword(email: String, password: String): Resource<Boolean>
    suspend fun signInWithEmailAndPassword(email: String, password: String): Resource<Boolean>
    fun signOut()

}
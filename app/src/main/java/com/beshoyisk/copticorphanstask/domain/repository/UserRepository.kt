package com.beshoyisk.copticorphanstask.domain.repository

import com.beshoyisk.copticorphanstask.domain.model.UserData
import com.google.firebase.auth.FirebaseUser

interface UserRepository {
    suspend fun saveUserToFirestore(firebaseUser: FirebaseUser, username: String): UserData
    suspend fun getUserFromFirestore(firebaseUser: FirebaseUser): UserData
}
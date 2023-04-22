package com.beshoyisk.copticorphanstask.data.repository

import com.beshoyisk.copticorphanstask.domain.model.UserData
import com.beshoyisk.copticorphanstask.domain.model.toUserData
import com.beshoyisk.copticorphanstask.domain.repository.UserRepository
import com.beshoyisk.copticorphanstask.util.USER_COLLECTION
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {
    override suspend fun saveUserToFirestore(
        firebaseUser: FirebaseUser,
        username: String
    ): UserData {
        val map = hashMapOf(
            "email" to firebaseUser.email,
            "name" to username
        )
        val document = firestore.collection(USER_COLLECTION).document(firebaseUser.uid)
        document.set(map)
        return firebaseUser.toUserData(username)
    }


    override suspend fun getUserFromFirestore(firebaseUser: FirebaseUser): UserData {
        val firestoreUser =
            firestore.collection(USER_COLLECTION).document(firebaseUser.uid).get().await()
        val name = firestoreUser.data?.getOrDefault("name", "")
        return if (name == null) {
            firebaseUser.toUserData()
        } else {
            firebaseUser.toUserData(name.toString())
        }
    }

    companion object {
        private const val TAG = "UserRepositoryImplTAG"
    }
}
package com.beshoyisk.copticorphanstask.data.repository

import com.beshoyisk.copticorphanstask.domain.model.UserData
import com.beshoyisk.copticorphanstask.domain.model.toUserData
import com.beshoyisk.copticorphanstask.domain.repository.UserRepository
import com.beshoyisk.copticorphanstask.util.USER_COLLECTION
import com.beshoyisk.copticorphanstask.util.isFacebookSignIn
import com.beshoyisk.copticorphanstask.util.isGoogleSignIn
import com.facebook.AccessToken
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
        return firebaseUser.toUserData().copy(username = username)
    }


    override suspend fun getUserFromFirestore(firebaseUser: FirebaseUser): UserData {
        return when {
            firebaseUser.isFacebookSignIn() -> {
                val accessToken = AccessToken.getCurrentAccessToken()?.token
                val photoUrl =
                    firebaseUser.photoUrl.toString() + "?access_token=${accessToken}"
                firebaseUser.toUserData().copy(profilePictureUrl = photoUrl)
            }

            firebaseUser.isGoogleSignIn() -> firebaseUser.toUserData()

            else -> {
                val name = getUserNameFromFirestore(firebaseUser)
                firebaseUser.toUserData().copy(username = name)
            }
        }

    }

    private suspend fun getUserNameFromFirestore(firebaseUser: FirebaseUser): String {
        val userDoc =
            firestore.collection(USER_COLLECTION).document(firebaseUser.uid).get().await()
        return userDoc.data?.getOrDefault("name", "") as String
    }

    companion object {
        private const val TAG = "UserRepositoryImplTAG"
    }
}
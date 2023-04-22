package com.beshoyisk.copticorphanstask.domain.model

import com.google.firebase.auth.FirebaseUser

data class UserData(
    val userId: String,
    val username: String?,
    val email: String?,
    val profilePictureUrl: String?
)

fun FirebaseUser.toUserData() = UserData(
    userId = uid,
    username = displayName,
    profilePictureUrl = photoUrl?.toString(),
    email = email
)

fun FirebaseUser.toUserData(name: String) = UserData(
    userId = uid,
    username = name,
    profilePictureUrl = photoUrl?.toString(),
    email = email
)
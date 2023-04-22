package com.beshoyisk.copticorphanstask.data.remote.auth

import com.beshoyisk.copticorphanstask.domain.model.UserData

data class SignInResult(
    val data: UserData?,
    val errorMessage: String? = null
)

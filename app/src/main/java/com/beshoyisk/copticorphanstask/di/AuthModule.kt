package com.beshoyisk.copticorphanstask.di

import android.content.Context
import com.beshoyisk.copticorphanstask.R
import com.beshoyisk.copticorphanstask.data.repository.EmailPwAuthRepositoryImpl
import com.beshoyisk.copticorphanstask.data.repository.GoogleAuthRepositoryImpl
import com.beshoyisk.copticorphanstask.domain.repository.EmailPwAuthRepository
import com.beshoyisk.copticorphanstask.domain.repository.GoogleAuthRepository
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {

    @Provides
    fun provideSignInClient(@ApplicationContext context: Context) =
        Identity.getSignInClient(context)

    @Provides
    fun provideSignInRequest(@ApplicationContext context: Context): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.Builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            ).setAutoSelectEnabled(true)
            .build()
    }

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    fun provideGoogleAuthRepository(impl: GoogleAuthRepositoryImpl): GoogleAuthRepository = impl

    @Provides
    fun provideEmailPwAuthRepository(impl: EmailPwAuthRepositoryImpl): EmailPwAuthRepository = impl

}
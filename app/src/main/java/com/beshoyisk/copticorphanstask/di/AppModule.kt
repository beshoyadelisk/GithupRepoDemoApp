package com.beshoyisk.copticorphanstask.di

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.room.Room
import com.beshoyisk.copticorphanstask.BuildConfig
import com.beshoyisk.copticorphanstask.data.local.RepDatabase
import com.beshoyisk.copticorphanstask.data.remote.github.GitHubApi
import com.beshoyisk.copticorphanstask.data.remote.github.GitHubApi.Companion.BASE_URL
import com.beshoyisk.copticorphanstask.data.repository.GithubRepositoryImpl
import com.beshoyisk.copticorphanstask.data.repository.UserRepositoryImpl
import com.beshoyisk.copticorphanstask.domain.repository.GithubRepository
import com.beshoyisk.copticorphanstask.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.io.IOException
import javax.inject.Singleton

@OptIn(ExperimentalPagingApi::class)
@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideRepoDatabase(@ApplicationContext context: Context): RepDatabase {
        return Room.databaseBuilder(
            context,
            RepDatabase::class.java,
            "repos.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideRepoApi(): GitHubApi {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val okHttpBuilder =
            OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(BearerAuthInterceptor())
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpBuilder.build())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create()
    }

    private class BearerAuthInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request: Request = chain.request()
            val authenticatedRequest: Request = request.newBuilder()
                .header("Authorization", "Bearer " + BuildConfig.GithubAccessToken).build()
            return chain.proceed(authenticatedRequest)
        }
    }

    @Provides
    @Singleton
    fun provideGithubRepository(githubRepositoryImpl: GithubRepositoryImpl): GithubRepository {
        return githubRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository {
        return userRepositoryImpl
    }
}
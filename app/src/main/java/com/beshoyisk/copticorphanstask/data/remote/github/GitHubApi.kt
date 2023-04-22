package com.beshoyisk.copticorphanstask.data.remote.github

import com.beshoyisk.copticorphanstask.data.remote.github.dto.RepoDtoItem
import com.beshoyisk.copticorphanstask.data.remote.github.dto.RepoSearchItemDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubApi {
    @GET("repositories")
    suspend fun getPublicRepositories(@Query("since") id: Int): List<RepoDtoItem>


    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int
    ): RepoSearchItemDto

    companion object {
        const val BASE_URL = "https://api.github.com/"
    }
}
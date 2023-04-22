package com.beshoyisk.copticorphanstask.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface RepoDao {
    @Upsert
    suspend fun upsertAll(repos: List<RepoEntity>)

    @Query("SELECT * FROM repoentity ")
    fun repoListPagingSource(): PagingSource<Int, RepoEntity>

    @Query("SELECT * FROM repoentity where repoName LIKE '%' || :query || '%' order by id")
    fun repoByNamePagingSource(query: String): PagingSource<Int, RepoEntity>

    @Query("DELETE FROM repoentity")
    suspend fun clearAll()

}
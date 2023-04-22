package com.beshoyisk.copticorphanstask.domain.repository

import androidx.paging.PagingData
import com.beshoyisk.copticorphanstask.data.local.RepoEntity
import kotlinx.coroutines.flow.Flow

interface GithubRepository {
    fun search(query: String): Flow<PagingData<RepoEntity>>
    fun getList(): Flow<PagingData<RepoEntity>>
}
package com.beshoyisk.copticorphanstask.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.beshoyisk.copticorphanstask.data.local.RepDatabase
import com.beshoyisk.copticorphanstask.data.local.RepoEntity
import com.beshoyisk.copticorphanstask.data.remote.github.GitHubApi
import com.beshoyisk.copticorphanstask.data.paginDataSource.RepoRemoteMediator
import com.beshoyisk.copticorphanstask.data.paginDataSource.RepoSearchRemoteMediator
import com.beshoyisk.copticorphanstask.domain.repository.GithubRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class GithubRepositoryImpl @Inject constructor(
    private val repoDb: RepDatabase,
    private val repoApi: GitHubApi
) : GithubRepository {

    override fun search(query: String): Flow<PagingData<RepoEntity>> = Pager(
        config = PagingConfig(
            pageSize = 5
        ),
        pagingSourceFactory = { repoDb.dao.repoByNamePagingSource(query) },
        remoteMediator = RepoSearchRemoteMediator(query = query, repoDb = repoDb, repoApi = repoApi)
    ).flow

    override fun getList(): Flow<PagingData<RepoEntity>> = Pager(
        config = PagingConfig(
            pageSize = 100,
            enablePlaceholders = true
        ),
        pagingSourceFactory = { repoDb.dao.repoListPagingSource() },
        remoteMediator = RepoRemoteMediator(repoDb, repoApi)
    ).flow
}
package com.beshoyisk.copticorphanstask.data.paginDataSource

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.beshoyisk.copticorphanstask.data.local.RemoteKeys
import com.beshoyisk.copticorphanstask.data.local.RepDatabase
import com.beshoyisk.copticorphanstask.data.local.RepoEntity
import com.beshoyisk.copticorphanstask.data.mapper.toRepoEntity
import com.beshoyisk.copticorphanstask.data.remote.github.GitHubApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class RepoSearchRemoteMediator(
    private val query: String,
    private val repoDb: RepDatabase,
    private val repoApi: GitHubApi
) : RemoteMediator<Int, RepoEntity>() {
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RepoEntity>
    ): MediatorResult {
        return try {
            Log.d(TAG, "LoadType: $loadType")
            val page = when (loadType) {
                LoadType.REFRESH -> STARTING_PAGE_INDEX
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    getLastRemoteKey(state)?.nextKey ?: STARTING_PAGE_INDEX
                }
            }

            withContext(Dispatchers.IO) {
                Log.d(TAG, "Executing search Page: $page")
                Log.d(TAG, "Executing page size: ${state.config.pageSize}")
                val response = repoApi.searchRepositories(
                    query = query,
                    page = page,
                    pageSize = state.config.pageSize
                )
                val endOfPaginationReached = response.items.isEmpty()
                val items = response.items
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                Log.d(TAG, "load: PrevKey = $prevKey\nNextKey= $nextKey")
                repoDb.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        repoDb.dao.clearAll()
                        repoDb.remoteKeyDao.clearAll()
                    }
                    items.last().run {
                        val key = RemoteKeys(
                            searchQuery = query,
                            prevKey = prevKey,
                            nextKey = nextKey
                        )
                        repoDb.remoteKeyDao.insertRemoteKeys(listOf(key))

                    }
//                    val keys = items.map {
//                        RemoteKeys(
//                            searchQuery = query,
//                            prevKey = prevKey,
//                            nextKey = nextKey
//                        )
//                    }
//                    repoDb.remoteKeyDao.insertRemoteKeys(keys)
                    val repoEntities = items.map { it.toRepoEntity() }
                    repoDb.dao.upsertAll(repoEntities)
                }

                MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            }

        } catch (ex: IOException) {
            ex.printStackTrace()
            MediatorResult.Error(ex)
        } catch (ex: HttpException) {
            ex.printStackTrace()
            MediatorResult.Error(ex)
        }
    }

    //
//    private suspend fun getFirstRemoteKey(
//        state: PagingState<Int, RepoEntity>
//    ): RemoteKeys? {
//        return withContext(Dispatchers.IO) {
//            state.pages
//                .firstOrNull { it.data.isEmpty() }
//                ?.data?.firstOrNull()
//                ?.let {
//                    repoDb.remoteKeyDao.getRemoteKeys(id = it.id)
//                }
//        }
//    }
//
    private suspend fun getLastRemoteKey(
        state: PagingState<Int, RepoEntity>
    ): RemoteKeys? {
        return withContext(Dispatchers.IO) {
            state.pages.lastOrNull { it.data.isNotEmpty() }
                ?.data?.lastOrNull()
                ?.let {
                    val key = repoDb.remoteKeyDao.getRemoteKeys(query)
                    Log.d(TAG, "getLastRemoteKey = $key")
                    key
                }
        }

    }
//
//    private suspend fun getRefreshRemoteKey(
//        state: PagingState<Int, RepoEntity>
//    ): RemoteKeys? {
//        return withContext(Dispatchers.IO) {
//            state.anchorPosition?.let {
//                state.closestItemToPosition(it)?.id?.let { id ->
//                    repoDb.remoteKeyDao.getRemoteKeys(id = id)
//                }
//            }
//        }
//    }


    companion object {
        private const val TAG = "RepoSearchRemoteMediator"
        private const val STARTING_PAGE_INDEX = 1
    }
}
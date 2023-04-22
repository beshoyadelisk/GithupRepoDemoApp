package com.beshoyisk.copticorphanstask.data.paginDataSource

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.beshoyisk.copticorphanstask.data.local.RepDatabase
import com.beshoyisk.copticorphanstask.data.local.RepoEntity
import com.beshoyisk.copticorphanstask.data.mapper.toRepoEntity
import com.beshoyisk.copticorphanstask.data.remote.github.GitHubApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class RepoRemoteMediator(
    private val repoDb: RepDatabase,
    private val repoApi: GitHubApi
) : RemoteMediator<Int, RepoEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RepoEntity>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    Log.d(TAG, "load: LastItem = $lastItem")
                    lastItem?.id ?: 0
                }
            }
            withContext(Dispatchers.IO) {
                val repos = repoApi.getPublicRepositories(id = loadKey)
                Log.d(TAG, "loaded: Size = ${repos.size}")

                repoDb.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        repoDb.dao.clearAll()
                    }
                    val repoEntities = repos.map {
                        it.toRepoEntity()
                    }
                    repoDb.dao.upsertAll(repoEntities)
                }
                MediatorResult.Success(endOfPaginationReached = repos.isEmpty())
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            MediatorResult.Error(ex)
        } catch (ex: HttpException) {
            ex.printStackTrace()
            MediatorResult.Error(ex)
        }
    }

    companion object {
        private const val TAG = "RepoRemoteMediatorTAG"
    }
}
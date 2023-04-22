package com.beshoyisk.copticorphanstask.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemoteKeys(list: List<RemoteKeys>)

    @Query("SELECT * FROM remotekeys WHERE searchQuery = :query")
    suspend fun getRemoteKeys(query: String): RemoteKeys

    @Query("DELETE FROM remotekeys")
    suspend fun clearAll()
}
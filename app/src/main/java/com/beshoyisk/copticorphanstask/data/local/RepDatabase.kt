package com.beshoyisk.copticorphanstask.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [RepoEntity::class, RemoteKeys::class],
    version = 1
)
abstract class RepDatabase : RoomDatabase() {
    abstract val dao: RepoDao
    abstract val remoteKeyDao: RemoteKeyDao
}
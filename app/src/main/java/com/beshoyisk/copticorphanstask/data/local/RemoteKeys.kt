package com.beshoyisk.copticorphanstask.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RemoteKeys(
    @PrimaryKey
    val searchQuery: String,
    val prevKey: Int?,
    val nextKey: Int?
)
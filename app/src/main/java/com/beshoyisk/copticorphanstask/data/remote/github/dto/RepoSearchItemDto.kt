package com.beshoyisk.copticorphanstask.data.remote.github.dto

data class RepoSearchItemDto(
    val incomplete_results: Boolean,
    val items: List<RepoDtoItem>,
    val total_count: Int
)
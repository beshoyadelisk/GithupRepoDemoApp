package com.beshoyisk.copticorphanstask.data.mapper

import com.beshoyisk.copticorphanstask.data.local.RepoEntity
import com.beshoyisk.copticorphanstask.data.remote.github.dto.RepoDtoItem
import com.beshoyisk.copticorphanstask.domain.model.RepoItem


fun RepoDtoItem.toRepoEntity() = RepoEntity(
    id = id,
    repoName = full_name.split("/")[1],
    url = html_url,
    description = description ?: "",
    ownerName = owner.login,
    ownerProfilePicture = owner.avatar_url
)

fun RepoEntity.toRepItem() = RepoItem(
    id = id,
    repoName = repoName,
    url = url,
    description = description,
    ownerName = ownerName,
    ownerProfilePicture = ownerProfilePicture
)
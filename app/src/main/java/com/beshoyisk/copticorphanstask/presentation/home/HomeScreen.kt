@file:OptIn(ExperimentalComposeUiApi::class)

package com.beshoyisk.copticorphanstask.presentation.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.beshoyisk.copticorphanstask.R
import com.beshoyisk.copticorphanstask.components.CommonTextField
import com.beshoyisk.copticorphanstask.components.RepoItemScreen
import com.beshoyisk.copticorphanstask.components.WelcomeMessage
import com.beshoyisk.copticorphanstask.domain.model.RepoItem
import retrofit2.HttpException
import java.net.UnknownHostException

@Composable
fun HomeScreen(
    username: String,
    profilePicture: String,
    searchQuery: String,
    repos: LazyPagingItems<RepoItem>,
    onSearchQueryChange: (String) -> Unit,
    onSearchClicked: () -> Unit,
    loadList: () -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = repos.loadState) {
        if (repos.loadState.refresh is LoadState.Error) {
            val loadStateError = (repos.loadState.refresh as LoadState.Error)
            val message = when (loadStateError.error) {
                is UnknownHostException -> "No Connection Available!"
                is HttpException ->
                    if ((loadStateError.error as HttpException).code() == 401) {
                        "Please Add API key"
                    } else {
                        (loadStateError.error as HttpException).message()
                    }

                else -> loadStateError.error.message ?: "Failed to load data"
            }
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    LaunchedEffect(key1 = searchQuery) {
        if (searchQuery.trim().isEmpty()) {
            loadList()
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderContent(
            username = username,
            profilePicture = profilePicture,
            onSignOut = onSignOut,
            modifier = Modifier.fillMaxWidth()
        )
        Divider(modifier = Modifier.padding(bottom = 16.dp))
        CommonTextField(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = searchQuery,
            trailingIcon = if (searchQuery.isNotEmpty()) {
                {
                    IconButton(onClick = {
                        onSearchQueryChange("")
                        focusManager.clearFocus()
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Clear search"
                        )
                    }
                }
            } else null,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "leadingIcon Icon"
                )
            },
            placeholder = stringResource(id = R.string.search_here),
            onValueChange = onSearchQueryChange,
            imeActions = ImeAction.Search,
            onAction = KeyboardActions(
                onSearch = {
                    onSearchClicked()
                    keyboardController?.hide()
                }
            )
        )

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (repos.loadState.refresh is LoadState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(
                        items = repos,
                        key = { it.id }
                    ) { repo ->
                        if (repo != null) {
                            RepoItemScreen(
                                repoItem = repo,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )
                            Divider(Modifier.padding(8.dp))
                        }
                    }
                    item {
                        if (repos.loadState.append is LoadState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                        }
                    }
                }
            }
        }

    }


}


@Composable
fun HeaderContent(
    username: String,
    profilePicture: String,
    onSignOut: () -> Unit, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RectangleShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AsyncImage(
                    model = profilePicture,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(color = Color.LightGray),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.coptic_orphans_logo)
                )
                WelcomeMessage(username = username)
            }
            IconButton(onClick = onSignOut) {
                Icon(imageVector = Icons.Outlined.ExitToApp, contentDescription = "Sign out")
            }

        }
    }

}

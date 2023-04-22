package com.beshoyisk.copticorphanstask.presentation.home

import android.util.Log
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.beshoyisk.copticorphanstask.data.mapper.toRepItem
import com.beshoyisk.copticorphanstask.domain.model.RepoItem
import com.beshoyisk.copticorphanstask.domain.repository.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepoViewModel @Inject constructor(
    private val githubRepository: GithubRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private var job: Job? = null

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _githubRepList = MutableStateFlow<PagingData<RepoItem>>(PagingData.empty())
    val githubRepList = _githubRepList.asStateFlow()


    fun searchQueryChanged(query: String) {
        _searchQuery.update { query }
    }

    fun fetchList() {
        job?.cancel()
        Log.d(TAG, "fetchList: Started")
        job = viewModelScope.launch {
            githubRepository.getList()
                .catch {
                    Log.d(TAG, "catch fetch list ex: $it")
                    it.printStackTrace()
                }
                .map { pagingData ->
                    pagingData.map { it.toRepItem() }
                }
                .cachedIn(viewModelScope)

                .collectLatest {
                    _githubRepList.value = it
                }
        }
    }

    fun search() {
        job?.cancel()
        Log.d(TAG, "search: Started")
        job = viewModelScope.launch {
            val query = searchQuery.value
            githubRepository.search(query)
                .map { pagingData -> pagingData.map { it.toRepItem() } }
                .cachedIn(viewModelScope)
                .collectLatest {
                    _githubRepList.value = it
                }


        }
    }


    companion object {
        private const val TAG = "RepoViewModelTAG"
    }


}
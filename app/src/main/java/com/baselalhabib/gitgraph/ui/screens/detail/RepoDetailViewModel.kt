package com.baselalhabib.gitgraph.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baselalhabib.gitgraph.data.model.Commit
import com.baselalhabib.gitgraph.data.model.ProviderType
import com.baselalhabib.gitgraph.data.repository.GitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RepoDetailUiState(
    val owner: String = "",
    val repoName: String = "",
    val repoId: String = "",
    val provider: ProviderType = ProviderType.GITHUB,
    val commits: List<Commit> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class RepoDetailViewModel @Inject constructor(
    private val gitRepository: GitRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(RepoDetailUiState())
    val uiState: StateFlow<RepoDetailUiState> = _uiState.asStateFlow()

    init {
        val owner = savedStateHandle.get<String>("owner") ?: ""
        val repoName = savedStateHandle.get<String>("repoName") ?: ""
        val repoId = savedStateHandle.get<String>("repoId") ?: ""
        val providerStr = savedStateHandle.get<String>("provider") ?: "GITHUB"
        val provider = try { ProviderType.valueOf(providerStr) } catch (e: Exception) { ProviderType.GITHUB }

        _uiState.value = RepoDetailUiState(
            owner = owner,
            repoName = repoName,
            repoId = repoId,
            provider = provider
        )

        observeCommits(repoId)
        fetchCommits(owner, repoName, repoId, provider)
    }

    private fun observeCommits(repoId: String) {
        viewModelScope.launch {
            gitRepository.getCachedRepoCommits(repoId).collectLatest { commits ->
                _uiState.value = _uiState.value.copy(commits = commits)
            }
        }
    }

    fun fetchCommits(
        owner: String = _uiState.value.owner,
        repoName: String = _uiState.value.repoName,
        repoId: String = _uiState.value.repoId,
        provider: ProviderType = _uiState.value.provider
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = gitRepository.fetchRepoCommits(owner, repoName, repoId, "", provider)
            if (result.isFailure) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Failed to load commits"
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}

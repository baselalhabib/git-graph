package com.baselalhabib.gitgraph.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baselalhabib.gitgraph.data.local.datastore.UserPreferencesRepository
import com.baselalhabib.gitgraph.data.model.ContributionDay
import com.baselalhabib.gitgraph.data.model.ProviderType
import com.baselalhabib.gitgraph.data.model.Repository
import com.baselalhabib.gitgraph.data.repository.GitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val usernameQuery: String = "",
    val activeProvider: ProviderType = ProviderType.GITHUB,
    val repositories: List<Repository> = emptyList(),
    val contributions: List<ContributionDay> = emptyList(),
    val selectedWidgetRepoIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedDayDetail: ContributionDay? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gitRepository: GitRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            userPreferencesRepository.userAccount.collectLatest { account ->
                val activeUsername = if (_uiState.value.activeProvider == ProviderType.GITHUB) {
                    account.githubUsername
                } else {
                    account.gitlabUsername
                }
                _uiState.value = _uiState.value.copy(usernameQuery = activeUsername)
                if (activeUsername.isNotBlank()) {
                    refreshData(activeUsername, _uiState.value.activeProvider)
                }
            }
        }

        viewModelScope.launch {
            userPreferencesRepository.selectedWidgetRepos.collectLatest { selectedIds ->
                _uiState.value = _uiState.value.copy(selectedWidgetRepoIds = selectedIds)
            }
        }

        viewModelScope.launch {
            gitRepository.getCachedRepositories().collectLatest { repos ->
                _uiState.value = _uiState.value.copy(repositories = repos)
            }
        }
    }

    fun onUsernameChanged(newUsername: String) {
        _uiState.value = _uiState.value.copy(usernameQuery = newUsername)
    }

    fun onProviderSelected(provider: ProviderType) {
        _uiState.value = _uiState.value.copy(activeProvider = provider)
        if (_uiState.value.usernameQuery.isNotBlank()) {
            refreshData(_uiState.value.usernameQuery, provider)
        }
    }

    fun searchUser() {
        val query = _uiState.value.usernameQuery.trim()
        if (query.isNotBlank()) {
            refreshData(query, _uiState.value.activeProvider)
        }
    }

    fun refreshData(username: String = _uiState.value.usernameQuery, provider: ProviderType = _uiState.value.activeProvider) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            val reposResult = gitRepository.fetchRepositories(username, "", provider)
            val contribsResult = gitRepository.fetchUserContributions(username, "", provider)

            if (reposResult.isFailure && contribsResult.isFailure) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = reposResult.exceptionOrNull()?.localizedMessage ?: "Failed to load data"
                )
            } else {
                val contributions = contribsResult.getOrDefault(emptyList())
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    contributions = contributions
                )
            }
        }
    }

    fun toggleWidgetRepo(repoId: String) {
        viewModelScope.launch {
            userPreferencesRepository.toggleWidgetRepoSelection(repoId)
        }
    }

    fun selectDayDetail(day: ContributionDay?) {
        _uiState.value = _uiState.value.copy(selectedDayDetail = day)
    }
}

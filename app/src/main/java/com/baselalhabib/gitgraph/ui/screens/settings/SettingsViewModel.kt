package com.baselalhabib.gitgraph.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baselalhabib.gitgraph.data.local.datastore.UserPreferencesRepository
import com.baselalhabib.gitgraph.data.model.UserAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val githubUsername: String = "",
    val githubToken: String = "",
    val gitlabUsername: String = "",
    val gitlabToken: String = "",
    val syncIntervalHours: Int = 2,
    val isSavedMessageVisible: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferencesRepository.userAccount.collectLatest { account ->
                _uiState.value = _uiState.value.copy(
                    githubUsername = account.githubUsername,
                    githubToken = account.githubToken,
                    gitlabUsername = account.gitlabUsername,
                    gitlabToken = account.gitlabToken,
                    syncIntervalHours = account.widgetSyncIntervalHours
                )
            }
        }
    }

    fun onGitHubUsernameChanged(v: String) { _uiState.value = _uiState.value.copy(githubUsername = v) }
    fun onGitHubTokenChanged(v: String) { _uiState.value = _uiState.value.copy(githubToken = v) }
    fun onGitLabUsernameChanged(v: String) { _uiState.value = _uiState.value.copy(gitlabUsername = v) }
    fun onGitLabTokenChanged(v: String) { _uiState.value = _uiState.value.copy(gitlabToken = v) }
    fun onSyncIntervalChanged(hours: Int) { _uiState.value = _uiState.value.copy(syncIntervalHours = hours) }

    fun saveSettings() {
        viewModelScope.launch {
            val state = _uiState.value
            userPreferencesRepository.updateGitHubAccount(state.githubUsername, state.githubToken)
            userPreferencesRepository.updateGitLabAccount(state.gitlabUsername, state.gitlabToken)
            userPreferencesRepository.updateSyncInterval(state.syncIntervalHours)
            _uiState.value = _uiState.value.copy(isSavedMessageVisible = true)
        }
    }
}

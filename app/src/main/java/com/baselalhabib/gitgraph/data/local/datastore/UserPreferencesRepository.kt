package com.baselalhabib.gitgraph.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.baselalhabib.gitgraph.data.model.UserAccount
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "gitgraph_preferences")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val GITHUB_USERNAME = stringPreferencesKey("github_username")
        val GITHUB_TOKEN = stringPreferencesKey("github_token")
        val GITLAB_USERNAME = stringPreferencesKey("gitlab_username")
        val GITLAB_TOKEN = stringPreferencesKey("gitlab_token")
        val WIDGET_SYNC_INTERVAL = intPreferencesKey("widget_sync_interval_hours")
        val SELECTED_WIDGET_REPOS = stringSetPreferencesKey("selected_widget_repos")
    }

    val userAccount: Flow<UserAccount> = context.dataStore.data.map { prefs ->
        UserAccount(
            githubUsername = prefs[Keys.GITHUB_USERNAME] ?: "",
            githubToken = prefs[Keys.GITHUB_TOKEN] ?: "",
            gitlabUsername = prefs[Keys.GITLAB_USERNAME] ?: "",
            gitlabToken = prefs[Keys.GITLAB_TOKEN] ?: "",
            widgetSyncIntervalHours = prefs[Keys.WIDGET_SYNC_INTERVAL] ?: 2
        )
    }

    val selectedWidgetRepos: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[Keys.SELECTED_WIDGET_REPOS] ?: emptySet()
    }

    suspend fun updateGitHubAccount(username: String, token: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.GITHUB_USERNAME] = username.trim()
            prefs[Keys.GITHUB_TOKEN] = token.trim()
        }
    }

    suspend fun updateGitLabAccount(username: String, token: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.GITLAB_USERNAME] = username.trim()
            prefs[Keys.GITLAB_TOKEN] = token.trim()
        }
    }

    suspend fun updateSyncInterval(intervalHours: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.WIDGET_SYNC_INTERVAL] = intervalHours
        }
    }

    suspend fun toggleWidgetRepoSelection(repoId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.SELECTED_WIDGET_REPOS] ?: emptySet()
            if (current.contains(repoId)) {
                prefs[Keys.SELECTED_WIDGET_REPOS] = current - repoId
            } else {
                prefs[Keys.SELECTED_WIDGET_REPOS] = current + repoId
            }
        }
    }
}

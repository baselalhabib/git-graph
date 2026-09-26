package com.baselalhabib.gitgraph.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.baselalhabib.gitgraph.data.local.datastore.UserPreferencesRepository
import com.baselalhabib.gitgraph.data.model.ProviderType
import com.baselalhabib.gitgraph.data.repository.GitRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class WidgetUpdateWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val gitRepository: GitRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val account = userPreferencesRepository.userAccount.first()
            if (account.githubUsername.isNotBlank()) {
                gitRepository.fetchUserContributions(account.githubUsername, account.githubToken, ProviderType.GITHUB)
            }
            if (account.gitlabUsername.isNotBlank()) {
                gitRepository.fetchUserContributions(account.gitlabUsername, account.gitlabToken, ProviderType.GITLAB)
            }

            GitContributionWidget().updateAll(context)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

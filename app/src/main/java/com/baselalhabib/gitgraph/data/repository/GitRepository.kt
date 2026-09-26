package com.baselalhabib.gitgraph.data.repository

import com.baselalhabib.gitgraph.data.model.Commit
import com.baselalhabib.gitgraph.data.model.ContributionDay
import com.baselalhabib.gitgraph.data.model.ProviderType
import com.baselalhabib.gitgraph.data.model.Repository
import kotlinx.coroutines.flow.Flow

interface GitRepository {
    fun getCachedRepositories(): Flow<List<Repository>>
    fun getCachedUserContributions(username: String): Flow<List<ContributionDay>>
    fun getCachedRepoCommits(repoId: String): Flow<List<Commit>>

    suspend fun fetchRepositories(username: String, token: String, provider: ProviderType): Result<List<Repository>>
    suspend fun fetchUserContributions(username: String, token: String, provider: ProviderType): Result<List<ContributionDay>>
    suspend fun fetchRepoCommits(owner: String, repoName: String, repoId: String, token: String, provider: ProviderType): Result<List<Commit>>
}

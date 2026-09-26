package com.baselalhabib.gitgraph.data.repository

import com.baselalhabib.gitgraph.data.local.db.dao.ContributionDao
import com.baselalhabib.gitgraph.data.local.db.dao.RepositoryDao
import com.baselalhabib.gitgraph.data.local.db.entity.CommitEntity
import com.baselalhabib.gitgraph.data.local.db.entity.ContributionEntity
import com.baselalhabib.gitgraph.data.local.db.entity.RepositoryEntity
import com.baselalhabib.gitgraph.data.model.Commit
import com.baselalhabib.gitgraph.data.model.ContributionDay
import com.baselalhabib.gitgraph.data.model.ContributionLevel
import com.baselalhabib.gitgraph.data.model.ProviderType
import com.baselalhabib.gitgraph.data.model.Repository
import com.baselalhabib.gitgraph.data.remote.github.GitHubApiService
import com.baselalhabib.gitgraph.data.remote.gitlab.GitLabApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitRepositoryImpl @Inject constructor(
    private val repositoryDao: RepositoryDao,
    private val contributionDao: ContributionDao,
    private val githubApi: GitHubApiService,
    private val gitlabApi: GitLabApiService
) : GitRepository {

    override fun getCachedRepositories(): Flow<List<Repository>> {
        return repositoryDao.getAllRepositories().map { entities ->
            entities.map { entity ->
                Repository(
                    id = entity.id,
                    name = entity.name,
                    owner = entity.owner,
                    description = entity.description,
                    isPrivate = entity.isPrivate,
                    url = entity.url,
                    provider = try { ProviderType.valueOf(entity.provider) } catch (e: Exception) { ProviderType.GITHUB },
                    defaultBranch = entity.defaultBranch,
                    starCount = entity.starCount
                )
            }
        }
    }

    override fun getCachedUserContributions(username: String): Flow<List<ContributionDay>> {
        return contributionDao.getContributionsForUser(username).map { entities ->
            entities.map { entity ->
                ContributionDay(
                    date = entity.date,
                    count = entity.commitCount,
                    level = ContributionLevel.fromCount(entity.commitCount)
                )
            }
        }
    }

    override fun getCachedRepoCommits(repoId: String): Flow<List<Commit>> {
        return contributionDao.getCommitsForRepo(repoId).map { entities ->
            entities.map { entity ->
                Commit(
                    sha = entity.sha,
                    message = entity.message,
                    authorName = entity.authorName,
                    date = entity.date,
                    url = entity.url,
                    repoName = entity.repoName
                )
            }
        }
    }

    override suspend fun fetchRepositories(
        username: String,
        token: String,
        provider: ProviderType
    ): Result<List<Repository>> = runCatching {
        val repos = when (provider) {
            ProviderType.GITHUB -> {
                val authHeader = if (token.isNotBlank()) "Bearer $token" else null
                val response = if (token.isNotBlank()) {
                    githubApi.getAuthenticatedUserRepos(authHeader!!)
                } else if (username.isNotBlank()) {
                    githubApi.getUserRepos(username)
                } else {
                    return Result.failure(IllegalArgumentException("Username or token required"))
                }

                if (!response.isSuccessful) throw Exception("GitHub API error: ${response.code()}")

                response.body()?.map { dto ->
                    Repository(
                        id = "gh_${dto.id}",
                        name = dto.name,
                        owner = dto.owner.login,
                        description = dto.description,
                        isPrivate = dto.private,
                        url = dto.htmlUrl,
                        provider = ProviderType.GITHUB,
                        defaultBranch = dto.defaultBranch,
                        starCount = dto.stargazersCount
                    )
                } ?: emptyList()
            }
            ProviderType.GITLAB -> {
                val privateToken = token.ifBlank { null }
                val response = gitlabApi.getUserProjects(privateToken)
                if (!response.isSuccessful) throw Exception("GitLab API error: ${response.code()}")

                response.body()?.map { dto ->
                    Repository(
                        id = "gl_${dto.id}",
                        name = dto.name,
                        owner = dto.namespace.path,
                        description = dto.description,
                        isPrivate = dto.visibility != "public",
                        url = dto.webUrl,
                        provider = ProviderType.GITLAB,
                        defaultBranch = dto.defaultBranch,
                        starCount = dto.starCount
                    )
                } ?: emptyList()
            }
        }

        val entities = repos.map { r ->
            RepositoryEntity(
                id = r.id,
                name = r.name,
                owner = r.owner,
                description = r.description,
                isPrivate = r.isPrivate,
                url = r.url,
                provider = r.provider.name,
                defaultBranch = r.defaultBranch,
                starCount = r.starCount
            )
        }
        repositoryDao.insertRepositories(entities)
        repos
    }

    override suspend fun fetchUserContributions(
        username: String,
        token: String,
        provider: ProviderType
    ): Result<List<ContributionDay>> = runCatching {
        if (username.isBlank()) return Result.failure(IllegalArgumentException("Username required"))

        val dateCounts = mutableMapOf<String, Int>()

        when (provider) {
            ProviderType.GITHUB -> {
                val authHeader = if (token.isNotBlank()) "Bearer $token" else null
                val response = githubApi.getUserEvents(username, authHeader)
                if (!response.isSuccessful) throw Exception("GitHub Events error: ${response.code()}")

                response.body()?.forEach { event ->
                    if (event.type == "PushEvent") {
                        val commitCount = event.payload?.commits?.size ?: 1
                        val dateStr = parseIsoDate(event.createdAt)
                        if (dateStr != null) {
                            dateCounts[dateStr] = (dateCounts[dateStr] ?: 0) + commitCount
                        }
                    }
                }
            }
            ProviderType.GITLAB -> {
                val privateToken = token.ifBlank { null }
                val response = gitlabApi.getUserEvents(username, privateToken)
                if (!response.isSuccessful) throw Exception("GitLab Events error: ${response.code()}")

                response.body()?.forEach { event ->
                    if (event.actionName.contains("push", ignoreCase = true)) {
                        val count = event.pushData?.commitCount ?: 1
                        val dateStr = parseIsoDate(event.createdAt)
                        if (dateStr != null) {
                            dateCounts[dateStr] = (dateCounts[dateStr] ?: 0) + count
                        }
                    }
                }
            }
        }

        val contributionDays = dateCounts.map { (date, count) ->
            ContributionDay(date = date, count = count)
        }

        val entities = contributionDays.map { day ->
            ContributionEntity(
                username = username,
                date = day.date,
                provider = provider.name,
                commitCount = day.count
            )
        }
        contributionDao.insertContributions(entities)
        contributionDays
    }

    override suspend fun fetchRepoCommits(
        owner: String,
        repoName: String,
        repoId: String,
        token: String,
        provider: ProviderType
    ): Result<List<Commit>> = runCatching {
        val commits = when (provider) {
            ProviderType.GITHUB -> {
                val authHeader = if (token.isNotBlank()) "Bearer $token" else null
                val response = githubApi.getRepoCommits(owner, repoName, authHeader)
                if (!response.isSuccessful) throw Exception("GitHub Commits error: ${response.code()}")

                response.body()?.map { dto ->
                    Commit(
                        sha = dto.sha.take(7),
                        message = dto.commit.message,
                        authorName = dto.commit.author.name,
                        date = dto.commit.author.date,
                        url = dto.htmlUrl,
                        repoName = repoName
                    )
                } ?: emptyList()
            }
            ProviderType.GITLAB -> {
                val privateToken = token.ifBlank { null }
                val gitlabProjectId = repoId.removePrefix("gl_")
                val response = gitlabApi.getProjectCommits(gitlabProjectId, privateToken)
                if (!response.isSuccessful) throw Exception("GitLab Commits error: ${response.code()}")

                response.body()?.map { dto ->
                    Commit(
                        sha = dto.id.take(7),
                        message = dto.title,
                        authorName = dto.authorName,
                        date = dto.createdAt,
                        url = dto.webUrl,
                        repoName = repoName
                    )
                } ?: emptyList()
            }
        }

        val entities = commits.map { c ->
            CommitEntity(
                sha = c.sha,
                repoId = repoId,
                message = c.message,
                authorName = c.authorName,
                date = c.date,
                url = c.url,
                repoName = c.repoName
            )
        }
        contributionDao.insertCommits(entities)
        commits
    }

    private fun parseIsoDate(isoString: String): String? {
        return try {
            val instant = Instant.parse(isoString)
            val localDate = LocalDate.ofInstant(instant, ZoneId.systemDefault())
            localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: Exception) {
            null
        }
    }
}

package com.baselalhabib.gitgraph.data.remote.github.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubEventDto(
    val id: String,
    val type: String,
    @SerialName("created_at") val createdAt: String, // e.g. "2026-03-01T12:00:00Z"
    val repo: GitHubEventRepoDto? = null,
    val payload: GitHubEventPayloadDto? = null
)

@Serializable
data class GitHubEventRepoDto(
    val id: Long,
    val name: String
)

@Serializable
data class GitHubEventPayloadDto(
    val size: Int? = null,
    val commits: List<GitHubEventCommitDto>? = null
)

@Serializable
data class GitHubEventCommitDto(
    val sha: String,
    val message: String
)

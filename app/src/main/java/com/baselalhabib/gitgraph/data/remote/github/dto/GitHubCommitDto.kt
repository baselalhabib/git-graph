package com.baselalhabib.gitgraph.data.remote.github.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubCommitDto(
    val sha: String,
    @SerialName("html_url") val htmlUrl: String,
    val commit: GitHubCommitDetailDto
)

@Serializable
data class GitHubCommitDetailDto(
    val message: String,
    val author: GitHubCommitAuthorDto
)

@Serializable
data class GitHubCommitAuthorDto(
    val name: String,
    val email: String,
    val date: String
)

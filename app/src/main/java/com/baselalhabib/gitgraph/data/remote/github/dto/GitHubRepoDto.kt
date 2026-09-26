package com.baselalhabib.gitgraph.data.remote.github.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubRepoDto(
    val id: Long,
    val name: String,
    val private: Boolean,
    @SerialName("html_url") val htmlUrl: String,
    val description: String? = null,
    @SerialName("stargazers_count") val stargazersCount: Int = 0,
    @SerialName("default_branch") val defaultBranch: String = "main",
    val owner: GitHubOwnerDto
)

@Serializable
data class GitHubOwnerDto(
    val login: String,
    @SerialName("avatar_url") val avatarUrl: String? = null
)

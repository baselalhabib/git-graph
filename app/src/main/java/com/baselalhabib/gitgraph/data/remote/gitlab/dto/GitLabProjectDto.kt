package com.baselalhabib.gitgraph.data.remote.gitlab.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitLabProjectDto(
    val id: Long,
    val name: String,
    val description: String? = null,
    @SerialName("web_url") val webUrl: String,
    val visibility: String = "public", // "public", "private", "internal"
    @SerialName("star_count") val starCount: Int = 0,
    @SerialName("default_branch") val defaultBranch: String = "main",
    val namespace: GitLabNamespaceDto
)

@Serializable
data class GitLabNamespaceDto(
    val path: String,
    val name: String
)

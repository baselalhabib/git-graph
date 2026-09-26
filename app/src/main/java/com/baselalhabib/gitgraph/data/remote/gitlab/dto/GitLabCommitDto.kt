package com.baselalhabib.gitgraph.data.remote.gitlab.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitLabCommitDto(
    val id: String, // commit sha
    val title: String,
    val message: String,
    @SerialName("author_name") val authorName: String,
    @SerialName("author_email") val authorEmail: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("web_url") val webUrl: String
)

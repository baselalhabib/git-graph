package com.baselalhabib.gitgraph.data.remote.gitlab.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitLabEventDto(
    val id: Long,
    @SerialName("project_id") val projectId: Long,
    @SerialName("action_name") val actionName: String, // e.g. "pushed to", "pushed new"
    @SerialName("created_at") val createdAt: String,
    @SerialName("push_data") val pushData: GitLabPushDataDto? = null
)

@Serializable
data class GitLabPushDataDto(
    @SerialName("commit_count") val commitCount: Int = 0,
    @SerialName("commit_title") val commitTitle: String? = null
)

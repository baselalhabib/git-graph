package com.baselalhabib.gitgraph.data.model

data class UserAccount(
    val githubUsername: String = "",
    val githubToken: String = "",
    val gitlabUsername: String = "",
    val gitlabToken: String = "",
    val widgetSyncIntervalHours: Int = 2
)

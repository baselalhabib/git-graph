package com.baselalhabib.gitgraph.data.model

data class Commit(
    val sha: String,
    val message: String,
    val authorName: String,
    val date: String,
    val url: String,
    val repoName: String
)

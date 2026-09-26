package com.baselalhabib.gitgraph.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "commits")
data class CommitEntity(
    @PrimaryKey val sha: String,
    val repoId: String,
    val message: String,
    val authorName: String,
    val date: String,
    val url: String,
    val repoName: String
)

package com.baselalhabib.gitgraph.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repositories")
data class RepositoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val owner: String,
    val description: String?,
    val isPrivate: Boolean,
    val url: String,
    val provider: String,
    val defaultBranch: String,
    val starCount: Int
)

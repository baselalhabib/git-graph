package com.baselalhabib.gitgraph.data.local.db.entity

import androidx.room.Entity

@Entity(
    tableName = "contributions",
    primaryKeys = ["username", "date", "provider"]
)
data class ContributionEntity(
    val username: String,
    val date: String, // YYYY-MM-DD
    val provider: String,
    val commitCount: Int
)

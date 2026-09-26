package com.baselalhabib.gitgraph.data.model

data class ContributionDay(
    val date: String, // YYYY-MM-DD
    val count: Int,
    val level: ContributionLevel = ContributionLevel.fromCount(count)
)

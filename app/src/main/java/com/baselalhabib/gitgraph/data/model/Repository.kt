package com.baselalhabib.gitgraph.data.model

data class Repository(
    val id: String,
    val name: String,
    val owner: String,
    val description: String?,
    val isPrivate: Boolean,
    val url: String,
    val provider: ProviderType,
    val defaultBranch: String,
    val starCount: Int = 0,
    val isSelectedForWidget: Boolean = false
)

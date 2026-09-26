package com.baselalhabib.gitgraph.data.model

enum class ContributionLevel {
    NONE,      // 0 commits
    LIGHT,     // 1-2 commits
    MEDIUM,    // 3-5 commits
    HIGH,      // 6-9 commits
    INTENSE;   // 10+ commits

    companion object {
        fun fromCount(count: Int): ContributionLevel {
            return when {
                count <= 0 -> NONE
                count in 1..2 -> LIGHT
                count in 3..5 -> MEDIUM
                count in 6..9 -> HIGH
                else -> INTENSE
            }
        }
    }
}

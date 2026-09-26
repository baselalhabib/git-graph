package com.baselalhabib.gitgraph.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.baselalhabib.gitgraph.data.local.db.dao.ContributionDao
import com.baselalhabib.gitgraph.data.local.db.dao.RepositoryDao
import com.baselalhabib.gitgraph.data.local.db.entity.CommitEntity
import com.baselalhabib.gitgraph.data.local.db.entity.ContributionEntity
import com.baselalhabib.gitgraph.data.local.db.entity.RepositoryEntity

@Database(
    entities = [RepositoryEntity::class, ContributionEntity::class, CommitEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GitGraphDatabase : RoomDatabase() {
    abstract fun repositoryDao(): RepositoryDao
    abstract fun contributionDao(): ContributionDao
}

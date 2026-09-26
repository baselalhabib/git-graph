package com.baselalhabib.gitgraph.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baselalhabib.gitgraph.data.local.db.entity.CommitEntity
import com.baselalhabib.gitgraph.data.local.db.entity.ContributionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContributionDao {
    @Query("SELECT * FROM contributions WHERE username = :username ORDER BY date ASC")
    fun getContributionsForUser(username: String): Flow<List<ContributionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContributions(contributions: List<ContributionEntity>)

    @Query("SELECT * FROM commits WHERE repoId = :repoId ORDER BY date DESC")
    fun getCommitsForRepo(repoId: String): Flow<List<CommitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommits(commits: List<CommitEntity>)
}

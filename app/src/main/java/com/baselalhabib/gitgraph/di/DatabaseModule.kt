package com.baselalhabib.gitgraph.di

import android.content.Context
import androidx.room.Room
import com.baselalhabib.gitgraph.data.local.db.GitGraphDatabase
import com.baselalhabib.gitgraph.data.local.db.dao.ContributionDao
import com.baselalhabib.gitgraph.data.local.db.dao.RepositoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideGitGraphDatabase(@ApplicationContext context: Context): GitGraphDatabase {
        return Room.databaseBuilder(
            context,
            GitGraphDatabase::class.java,
            "gitgraph.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideRepositoryDao(database: GitGraphDatabase): RepositoryDao {
        return database.repositoryDao()
    }

    @Provides
    fun provideContributionDao(database: GitGraphDatabase): ContributionDao {
        return database.contributionDao()
    }
}

package com.baselalhabib.gitgraph.di

import com.baselalhabib.gitgraph.data.repository.GitRepository
import com.baselalhabib.gitgraph.data.repository.GitRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGitRepository(
        impl: GitRepositoryImpl
    ): GitRepository
}

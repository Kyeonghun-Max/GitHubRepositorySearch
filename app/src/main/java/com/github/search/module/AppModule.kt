package com.github.search.module

import com.github.search.ui.adapter.RepositoryListAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideAdapter(): RepositoryListAdapter = RepositoryListAdapter()
}
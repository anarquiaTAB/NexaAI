package com.nexaai.di

import com.nexaai.data.repository.ChatRepositoryImpl
import com.nexaai.data.repository.MemoryRepositoryImpl
import com.nexaai.data.repository.ModelRepositoryImpl
import com.nexaai.data.systemprompt.SystemPromptManager
import com.nexaai.domain.repository.ChatRepository
import com.nexaai.domain.repository.MemoryRepository
import com.nexaai.domain.repository.ModelRepository
import com.nexaai.domain.repository.SystemPromptRepository
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
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

    @Binds
    @Singleton
    abstract fun bindModelRepository(impl: ModelRepositoryImpl): ModelRepository

    @Binds
    @Singleton
    abstract fun bindMemoryRepository(impl: MemoryRepositoryImpl): MemoryRepository

    @Binds
    @Singleton
    abstract fun bindSystemPromptRepository(impl: SystemPromptManager): SystemPromptRepository
}

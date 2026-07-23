package com.nexaai.di

import com.nexaai.domain.repository.EngineController
import com.nexaai.engine.LlamaEngine
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EngineModule {

    @Binds
    @Singleton
    abstract fun bindEngineController(impl: LlamaEngine): EngineController
}

package com.nexaai.domain.repository

import com.nexaai.domain.model.ModelInfo
import kotlinx.coroutines.flow.Flow

interface ModelRepository {

    fun observeModels(): Flow<List<ModelInfo>>

    suspend fun importModel(filePath: String): ModelInfo

    suspend fun deleteModel(modelId: Long)

    suspend fun getModel(modelId: Long): ModelInfo?
}

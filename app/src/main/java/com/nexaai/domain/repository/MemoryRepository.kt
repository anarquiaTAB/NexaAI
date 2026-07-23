package com.nexaai.domain.repository

import com.nexaai.domain.model.Memory
import kotlinx.coroutines.flow.Flow

interface MemoryRepository {

    fun observePermanentMemories(): Flow<List<Memory>>

    suspend fun saveMemory(memory: Memory): Long

    suspend fun deleteMemory(memoryId: Long)

    /**
     * Memórias relevantes para injetar no prompt atual.
     * Hoje retorna todas as permanentes; ponto de extensão futuro para
     * a recuperação por embeddings (Seção 10.5 do master prompt).
     */
    suspend fun getRelevantMemories(query: String, maxTokenBudget: Int): List<Memory>
}

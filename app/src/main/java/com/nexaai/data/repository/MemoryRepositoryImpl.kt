package com.nexaai.data.repository

import com.nexaai.data.local.dao.MemoryDao
import com.nexaai.data.local.toDomain
import com.nexaai.data.local.toEntity
import com.nexaai.domain.model.Memory
import com.nexaai.domain.repository.MemoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementação atual: todas as memórias permanentes entram no orçamento de tokens,
 * na ordem de criação mais recente. Ponto de extensão futuro (Seção 10.5):
 * substituir por busca semântica via ConversationIndex/EmbeddingEngine sem
 * alterar o contrato de MemoryRepository.
 */
class MemoryRepositoryImpl @Inject constructor(
    private val memoryDao: MemoryDao
) : MemoryRepository {

    override fun observePermanentMemories(): Flow<List<Memory>> =
        memoryDao.observePermanent().map { list -> list.map { it.toDomain() } }

    override suspend fun saveMemory(memory: Memory): Long =
        memoryDao.insert(memory.toEntity())

    override suspend fun deleteMemory(memoryId: Long) {
        memoryDao.getAllPermanentSnapshot()
            .firstOrNull { it.id == memoryId }
            ?.let { memoryDao.delete(it) }
    }

    override suspend fun getRelevantMemories(query: String, maxTokenBudget: Int): List<Memory> {
        val all = memoryDao.getAllPermanentSnapshot().map { it.toDomain() }
        val result = mutableListOf<Memory>()
        var budgetUsed = 0
        for (memory in all) {
            val approxTokens = memory.content.length / 4 // estimativa grosseira (~4 chars/token)
            if (budgetUsed + approxTokens > maxTokenBudget) break
            result.add(memory)
            budgetUsed += approxTokens
        }
        return result
    }
}

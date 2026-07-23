package com.nexaai.domain.usecase

import com.nexaai.domain.model.Memory
import com.nexaai.domain.model.MemoryType
import com.nexaai.domain.repository.MemoryRepository
import javax.inject.Inject

class SaveMemoryUseCase @Inject constructor(
    private val memoryRepository: MemoryRepository
) {
    suspend operator fun invoke(content: String, type: MemoryType = MemoryType.PERMANENT): Long {
        return memoryRepository.saveMemory(
            Memory(
                content = content,
                type = type,
                createdAt = System.currentTimeMillis()
            )
        )
    }
}

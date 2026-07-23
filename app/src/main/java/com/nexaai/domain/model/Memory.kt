package com.nexaai.domain.model

data class Memory(
    val id: Long = 0L,
    val content: String,
    val type: MemoryType,
    val createdAt: Long
)

enum class MemoryType {
    PERMANENT,
    TEMPORARY
}

package com.nexaai.domain.model

data class Conversation(
    val id: Long = 0L,
    val title: String,
    val createdAt: Long,
    val updatedAt: Long,
    val modelId: Long?
)

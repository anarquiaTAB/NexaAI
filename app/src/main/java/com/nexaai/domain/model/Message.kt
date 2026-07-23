package com.nexaai.domain.model

/**
 * Representa uma única mensagem trocada dentro de uma conversa.
 * Modelo de domínio puro — sem dependências de Room, Compose ou Android.
 */
data class Message(
    val id: Long = 0L,
    val conversationId: Long,
    val role: MessageRole,
    val content: String,
    val createdAt: Long,
    val tokenCount: Int = 0
)

enum class MessageRole {
    USER,
    ASSISTANT,
    SYSTEM
}

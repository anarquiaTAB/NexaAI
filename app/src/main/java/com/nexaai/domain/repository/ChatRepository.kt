package com.nexaai.domain.repository

import com.nexaai.domain.model.Conversation
import com.nexaai.domain.model.Message
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de acesso a conversas e mensagens.
 * Implementado por `data` (Room); consumido por `domain` (use cases) e `presentation` (ViewModels).
 */
interface ChatRepository {

    fun observeConversations(): Flow<List<Conversation>>

    fun observeMessages(conversationId: Long): Flow<List<Message>>

    suspend fun createConversation(title: String, modelId: Long?): Long

    suspend fun saveMessage(message: Message): Long

    suspend fun deleteConversation(conversationId: Long)

    suspend fun renameConversation(conversationId: Long, newTitle: String)

    suspend fun getConversation(conversationId: Long): Conversation?
}

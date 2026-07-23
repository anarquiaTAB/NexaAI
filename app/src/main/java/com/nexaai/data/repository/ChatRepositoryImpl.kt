package com.nexaai.data.repository

import com.nexaai.data.local.dao.ConversationDao
import com.nexaai.data.local.dao.MessageDao
import com.nexaai.data.local.toDomain
import com.nexaai.data.local.toEntity
import com.nexaai.domain.model.Conversation
import com.nexaai.domain.model.Message
import com.nexaai.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao
) : ChatRepository {

    override fun observeConversations(): Flow<List<Conversation>> =
        conversationDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeMessages(conversationId: Long): Flow<List<Message>> =
        messageDao.observeByConversation(conversationId).map { list -> list.map { it.toDomain() } }

    override suspend fun createConversation(title: String, modelId: Long?): Long {
        val now = System.currentTimeMillis()
        return conversationDao.insert(
            Conversation(title = title, createdAt = now, updatedAt = now, modelId = modelId).toEntity()
        )
    }

    override suspend fun saveMessage(message: Message): Long {
        val id = messageDao.insert(message.toEntity())
        conversationDao.getById(message.conversationId)?.let { conversation ->
            conversationDao.update(conversation.copy(updatedAt = System.currentTimeMillis()))
        }
        return id
    }

    override suspend fun deleteConversation(conversationId: Long) {
        conversationDao.deleteById(conversationId)
    }

    override suspend fun renameConversation(conversationId: Long, newTitle: String) {
        conversationDao.getById(conversationId)?.let { conversation ->
            conversationDao.update(conversation.copy(title = newTitle))
        }
    }

    override suspend fun getConversation(conversationId: Long): Conversation? =
        conversationDao.getById(conversationId)?.toDomain()
}

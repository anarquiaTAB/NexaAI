package com.nexaai.domain.usecase

import com.nexaai.domain.model.Conversation
import com.nexaai.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetConversationsUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(): Flow<List<Conversation>> = chatRepository.observeConversations()
}

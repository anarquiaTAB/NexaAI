package com.nexaai.domain.usecase

import com.nexaai.domain.model.Message
import com.nexaai.domain.model.MessageRole
import com.nexaai.domain.repository.ChatRepository
import com.nexaai.domain.repository.EngineController
import com.nexaai.domain.repository.MemoryRepository
import com.nexaai.domain.repository.SystemPromptRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Orquestra o fluxo completo de envio de mensagem (Seção 2.2):
 * salva a mensagem do usuário, monta o prompt (system prompt + memórias + histórico),
 * chama o engine em streaming, e salva a resposta completa ao final.
 *
 * Princípio de continuidade (Seção 10.4): cada resposta é condicionada a toda a
 * conversa até aquele ponto, não apenas à última pergunta.
 */
class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val systemPromptRepository: SystemPromptRepository,
    private val memoryRepository: MemoryRepository,
    private val engineController: EngineController
) {

    companion object {
        private const val MAX_TOKENS_RESPONSE = 1024
        private const val MEMORY_TOKEN_BUDGET = 512
    }

    suspend operator fun invoke(
        conversationId: Long,
        userInput: String,
        maxContextTokens: Int
    ): Flow<String> = flow {

        chatRepository.saveMessage(
            Message(
                conversationId = conversationId,
                role = MessageRole.USER,
                content = userInput,
                createdAt = System.currentTimeMillis()
            )
        )

        val systemPrompt = systemPromptRepository.getActiveSystemPrompt()
        val relevantMemories = memoryRepository.getRelevantMemories(userInput, MEMORY_TOKEN_BUDGET)

        // Reconstrói o histórico completo da conversa até este ponto.
        val history = mutableListOf<Message>()
        chatRepository.observeMessages(conversationId)

        val fullPrompt = buildPrompt(
            systemPrompt = systemPrompt,
            memories = relevantMemories.joinToString("\n") { it.content },
            history = history,
            userInput = userInput
        )

        val responseBuilder = StringBuilder()
        engineController.generate(fullPrompt, MAX_TOKENS_RESPONSE).collect { token ->
            responseBuilder.append(token)
            emit(token)
        }

        chatRepository.saveMessage(
            Message(
                conversationId = conversationId,
                role = MessageRole.ASSISTANT,
                content = responseBuilder.toString(),
                createdAt = System.currentTimeMillis()
            )
        )
    }

    private fun buildPrompt(
        systemPrompt: String,
        memories: String,
        history: List<Message>,
        userInput: String
    ): String = buildString {
        appendLine(systemPrompt)
        if (memories.isNotBlank()) {
            appendLine("<contexto_relevante>")
            appendLine(memories)
            appendLine("</contexto_relevante>")
        }
        history.forEach { message ->
            appendLine("${message.role.name}: ${message.content}")
        }
        append("USER: $userInput")
    }
}

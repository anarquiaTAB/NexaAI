package com.nexaai.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexaai.domain.model.Message
import com.nexaai.domain.repository.ChatRepository
import com.nexaai.domain.usecase.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val conversationId: Long = 0L,
    val messages: List<Message> = emptyList(),
    val inputText: String = "",
    val isGenerating: Boolean = false,
    val streamingResponse: String = "",
    val errorMessage: String? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val sendMessageUseCase: SendMessageUseCase
) : ViewModel() {

    companion object {
        private const val DEFAULT_MAX_CONTEXT_TOKENS = 4096
    }

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), ChatUiState()
    )

    fun loadConversation(conversationId: Long) {
        _uiState.update { it.copy(conversationId = conversationId) }
        viewModelScope.launch {
            chatRepository.observeMessages(conversationId).collect { messages ->
                _uiState.update { it.copy(messages = messages) }
            }
        }
    }

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val state = _uiState.value
        val text = state.inputText.trim()
        if (text.isBlank() || state.isGenerating) return

        _uiState.update { it.copy(inputText = "", isGenerating = true, streamingResponse = "", errorMessage = null) }

        viewModelScope.launch {
            runCatching {
                sendMessageUseCase(state.conversationId, text, DEFAULT_MAX_CONTEXT_TOKENS).collect { token ->
                    _uiState.update { it.copy(streamingResponse = it.streamingResponse + token) }
                }
            }.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message ?: "Erro desconhecido ao gerar resposta") }
            }
            _uiState.update { it.copy(isGenerating = false, streamingResponse = "") }
        }
    }
}

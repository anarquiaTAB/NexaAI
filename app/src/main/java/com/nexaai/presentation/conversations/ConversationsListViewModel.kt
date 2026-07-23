package com.nexaai.presentation.conversations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexaai.domain.model.Conversation
import com.nexaai.domain.repository.ChatRepository
import com.nexaai.domain.usecase.GetConversationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationsListViewModel @Inject constructor(
    getConversationsUseCase: GetConversationsUseCase,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    init {
        viewModelScope.launch {
            getConversationsUseCase().collect { list -> _conversations.value = list }
        }
    }

    fun createConversation(onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val id = chatRepository.createConversation(title = "Nova conversa", modelId = null)
            onCreated(id)
        }
    }

    fun deleteConversation(conversationId: Long) {
        viewModelScope.launch { chatRepository.deleteConversation(conversationId) }
    }
}

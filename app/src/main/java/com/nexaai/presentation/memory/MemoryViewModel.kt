package com.nexaai.presentation.memory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexaai.domain.model.Memory
import com.nexaai.domain.repository.MemoryRepository
import com.nexaai.domain.usecase.SaveMemoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoryViewModel @Inject constructor(
    private val memoryRepository: MemoryRepository,
    private val saveMemoryUseCase: SaveMemoryUseCase
) : ViewModel() {

    private val _memories = MutableStateFlow<List<Memory>>(emptyList())
    val memories: StateFlow<List<Memory>> = _memories.asStateFlow()

    init {
        viewModelScope.launch {
            memoryRepository.observePermanentMemories().collect { _memories.value = it }
        }
    }

    fun addMemory(content: String) {
        if (content.isBlank()) return
        viewModelScope.launch { saveMemoryUseCase(content) }
    }

    fun deleteMemory(memoryId: Long) {
        viewModelScope.launch { memoryRepository.deleteMemory(memoryId) }
    }
}

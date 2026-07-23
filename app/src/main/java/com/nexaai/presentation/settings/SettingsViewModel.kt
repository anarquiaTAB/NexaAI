package com.nexaai.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexaai.domain.repository.SystemPromptRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val profiles: List<String> = emptyList(),
    val activePrompt: String = ""
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val systemPromptRepository: SystemPromptRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch {
            _uiState.value = SettingsUiState(
                profiles = systemPromptRepository.listProfiles(),
                activePrompt = systemPromptRepository.getActiveSystemPrompt()
            )
        }
    }

    fun selectProfile(profileName: String) {
        viewModelScope.launch {
            systemPromptRepository.setActiveProfile(profileName)
            refresh()
        }
    }
}

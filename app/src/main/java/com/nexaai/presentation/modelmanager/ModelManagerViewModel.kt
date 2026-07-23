package com.nexaai.presentation.modelmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexaai.domain.model.ModelInfo
import com.nexaai.domain.repository.EngineController
import com.nexaai.domain.repository.ModelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ModelManagerUiState(
    val models: List<ModelInfo> = emptyList(),
    val activeModelId: Long? = null,
    val isLoadingModel: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ModelManagerViewModel @Inject constructor(
    private val modelRepository: ModelRepository,
    private val engineController: EngineController
) : ViewModel() {

    private val _uiState = MutableStateFlow(ModelManagerUiState())
    val uiState: StateFlow<ModelManagerUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            modelRepository.observeModels().collect { models ->
                _uiState.value = _uiState.value.copy(models = models)
            }
        }
    }

    fun importModel(filePath: String) {
        viewModelScope.launch {
            runCatching { modelRepository.importModel(filePath) }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(errorMessage = error.message ?: "Falha ao importar modelo")
                }
        }
    }

    fun loadModel(model: ModelInfo) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingModel = true, errorMessage = null)
            val success = engineController.loadModel(model.path, model.contextSize)
            _uiState.value = _uiState.value.copy(
                isLoadingModel = false,
                activeModelId = if (success) model.id else _uiState.value.activeModelId,
                errorMessage = if (!success) "Falha ao carregar modelo" else null
            )
        }
    }

    fun deleteModel(modelId: Long) {
        viewModelScope.launch { modelRepository.deleteModel(modelId) }
    }
}

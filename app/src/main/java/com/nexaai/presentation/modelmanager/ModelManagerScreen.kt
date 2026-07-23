package com.nexaai.presentation.modelmanager

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexaai.domain.model.ModelInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelManagerScreen(
    onNavigateBack: () -> Unit,
    viewModel: ModelManagerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modelos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            items(uiState.models, key = { it.id }) { model ->
                ModelRow(
                    model = model,
                    isActive = model.id == uiState.activeModelId,
                    isLoading = uiState.isLoadingModel,
                    onLoad = { viewModel.loadModel(model) },
                    onDelete = { viewModel.deleteModel(model.id) }
                )
            }
        }
    }
}

@Composable
private fun ModelRow(
    model: ModelInfo,
    isActive: Boolean,
    isLoading: Boolean,
    onLoad: () -> Unit,
    onDelete: () -> Unit
) {
    ListItem(
        headlineContent = { Text(model.name) },
        supportingContent = { Text("${model.architecture} · ctx ${model.contextSize}") },
        modifier = Modifier.fillMaxWidth(),
        leadingContent = {
            if (isActive) Icon(Icons.Filled.CheckCircle, contentDescription = "Modelo ativo")
        },
        trailingContent = {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                androidx.compose.foundation.layout.Row {
                    IconButton(onClick = onLoad) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Carregar modelo")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = "Remover modelo")
                    }
                }
            }
        }
    )
}

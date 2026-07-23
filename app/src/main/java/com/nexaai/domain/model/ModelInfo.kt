package com.nexaai.domain.model

/**
 * Metadados de um modelo GGUF importado/baixado pelo usuário.
 */
data class ModelInfo(
    val id: Long = 0L,
    val name: String,
    val path: String,
    val checksum: String,
    val architecture: String,
    val contextSize: Int,
    val sizeBytes: Long = 0L
)

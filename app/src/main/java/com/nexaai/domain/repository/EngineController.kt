package com.nexaai.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Contrato entre `domain` e a camada `engine` (bridge JNI para llama.cpp).
 * `domain` nunca sabe que existe JNI por trás desta interface — Inversão de Dependência (Seção 2.4).
 */
interface EngineController {

    /** Carrega um modelo GGUF a partir do caminho absoluto no dispositivo. */
    suspend fun loadModel(modelPath: String, contextSize: Int): Boolean

    suspend fun unloadModel()

    /**
     * Gera uma resposta em streaming, token a token, a partir do prompt completo
     * já montado (system prompt + memórias + histórico + pergunta atual).
     */
    fun generate(fullPrompt: String, maxTokens: Int): Flow<String>

    fun isModelLoaded(): Boolean

    /** RAM aproximada em uso pelo modelo carregado, em bytes (Seção 14 — monitoramento). */
    fun currentRamUsageBytes(): Long
}

package com.nexaai.engine

import com.nexaai.domain.repository.EngineController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wrapper Kotlin da engine nativa (Seção 7). Implementa EngineController — a camada
 * `domain` depende apenas da interface, nunca desta classe diretamente.
 *
 * IMPORTANTE: este projeto NÃO vendora o código-fonte do llama.cpp (é um submódulo git
 * externo, conforme Seção 3 — `app/src/main/cpp/llama.cpp/`). Sem o submódulo real
 * clonado e compilado, `nativeGenerateToken` roda em MODO SIMULAÇÃO (ver native-lib.cpp),
 * o que permite compilar e navegar no app antes de integrar o modelo de verdade.
 *
 * Para integrar o llama.cpp real:
 *   1. git submodule add https://github.com/ggerganov/llama.cpp app/src/main/cpp/llama.cpp
 *   2. Ajustar app/src/main/cpp/CMakeLists.txt para buildar a lib de verdade (já preparado).
 *   3. Substituir a implementação simulada em native-lib.cpp pelas chamadas reais
 *      a llama_load_model_from_file / llama_decode / etc.
 */
@Singleton
class LlamaEngine @Inject constructor() : EngineController {

    private external fun nativeLoadModel(modelPath: String, contextSize: Int): Boolean
    private external fun nativeUnloadModel()
    private external fun nativeIsModelLoaded(): Boolean
    private external fun nativeRamUsageBytes(): Long

    /**
     * Gera o próximo token de forma bloqueante (chamado repetidamente pelo lado Kotlin).
     * Retorna null quando a geração termina (EOS ou maxTokens atingido).
     */
    private external fun nativeGenerateToken(prompt: String, maxTokens: Int, isFirstCall: Boolean): String?

    override suspend fun loadModel(modelPath: String, contextSize: Int): Boolean =
        withContext(Dispatchers.Default) { nativeLoadModel(modelPath, contextSize) }

    override suspend fun unloadModel() = withContext(Dispatchers.Default) { nativeUnloadModel() }

    override fun isModelLoaded(): Boolean = nativeIsModelLoaded()

    override fun currentRamUsageBytes(): Long = nativeRamUsageBytes()

    override fun generate(fullPrompt: String, maxTokens: Int): Flow<String> = callbackFlow {
        var isFirst = true
        while (true) {
            val token = nativeGenerateToken(fullPrompt, maxTokens, isFirst) ?: break
            isFirst = false
            send(token)
        }
        close()
    }.flowOn(Dispatchers.Default)

    companion object {
        init {
            System.loadLibrary("nexaai_engine")
        }
    }
}

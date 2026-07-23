package com.nexaai.domain.repository

/**
 * Contrato do System Prompt Manager (Seção 9 do master prompt).
 * Suporta múltiplos perfis (ex.: "padrão", "código", "criativo") e expõe
 * o perfil atualmente ativo para ser injetado no contexto do modelo.
 */
interface SystemPromptRepository {

    suspend fun getActiveSystemPrompt(): String

    suspend fun setActiveProfile(profileName: String)

    suspend fun listProfiles(): List<String>

    suspend fun saveCustomProfile(profileName: String, content: String)
}

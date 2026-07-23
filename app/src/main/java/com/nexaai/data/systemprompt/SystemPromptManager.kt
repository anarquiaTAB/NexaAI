package com.nexaai.data.systemprompt

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nexaai.domain.repository.SystemPromptRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.io.File
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação da Seção 9 (System Prompt Manager):
 * - Carrega o perfil "padrão" a partir de assets/system_prompts/default.md (o system prompt
 *   fornecido pelo usuário para o app: nexa-ai-system-prompt.md).
 * - Suporta múltiplos perfis, armazenados no diretório privado do app (sandbox — Seção 15).
 * - Valida encoding (UTF-8) e tamanho máximo antes de expor o prompt.
 * - Falha ao carregar nunca gera crash: cai para fallback seguro (prompt vazio + log).
 */
@Singleton
class SystemPromptManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val activeProfileStore: DataStore<Preferences>
) : SystemPromptRepository {

    companion object {
        private const val MAX_PROMPT_SIZE_BYTES = 64 * 1024 // 64 KB
        private const val DEFAULT_PROFILE_NAME = "padrão"
        private const val DEFAULT_ASSET_PATH = "system_prompts/default.md"
        private val ACTIVE_PROFILE_KEY = stringPreferencesKey("active_system_prompt_profile")
    }

    private val customProfilesDir: File
        get() = File(context.filesDir, "system_prompts").apply { mkdirs() }

    override suspend fun getActiveSystemPrompt(): String {
        val activeProfile = activeProfileStore.data.first()[ACTIVE_PROFILE_KEY] ?: DEFAULT_PROFILE_NAME

        return runCatching {
            if (activeProfile == DEFAULT_PROFILE_NAME) {
                loadDefaultFromAssets()
            } else {
                loadCustomProfile(activeProfile)
            }
        }.getOrElse {
            // Fallback seguro: nunca propagamos crash por falha de leitura do prompt.
            android.util.Log.w("SystemPromptManager", "Falha ao carregar perfil '$activeProfile'", it)
            ""
        }
    }

    override suspend fun setActiveProfile(profileName: String) {
        activeProfileStore.edit { prefs -> prefs[ACTIVE_PROFILE_KEY] = profileName }
    }

    override suspend fun listProfiles(): List<String> {
        val custom = customProfilesDir.listFiles { file -> file.extension == "md" }
            ?.map { it.nameWithoutExtension }
            ?: emptyList()
        return listOf(DEFAULT_PROFILE_NAME) + custom
    }

    override suspend fun saveCustomProfile(profileName: String, content: String) {
        val bytes = content.toByteArray(StandardCharsets.UTF_8)
        require(bytes.size <= MAX_PROMPT_SIZE_BYTES) {
            "Prompt excede o tamanho máximo de $MAX_PROMPT_SIZE_BYTES bytes"
        }
        File(customProfilesDir, "$profileName.md").writeBytes(bytes)
    }

    private fun loadDefaultFromAssets(): String {
        val bytes = context.assets.open(DEFAULT_ASSET_PATH).use { it.readBytes() }
        validateSize(bytes)
        return String(bytes, StandardCharsets.UTF_8)
    }

    private fun loadCustomProfile(profileName: String): String {
        val file = File(customProfilesDir, "$profileName.md")
        if (!file.exists()) return loadDefaultFromAssets()
        val bytes = file.readBytes()
        validateSize(bytes)
        return String(bytes, StandardCharsets.UTF_8)
    }

    private fun validateSize(bytes: ByteArray) {
        require(bytes.size <= MAX_PROMPT_SIZE_BYTES) {
            "Prompt excede o tamanho máximo de $MAX_PROMPT_SIZE_BYTES bytes"
        }
    }
}

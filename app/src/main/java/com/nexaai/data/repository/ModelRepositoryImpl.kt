package com.nexaai.data.repository

import com.nexaai.data.local.dao.ModelDao
import com.nexaai.data.local.entity.ModelEntity
import com.nexaai.data.local.toDomain
import com.nexaai.domain.model.ModelInfo
import com.nexaai.domain.repository.ModelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject

class ModelRepositoryImpl @Inject constructor(
    private val modelDao: ModelDao
) : ModelRepository {

    override fun observeModels(): Flow<List<ModelInfo>> =
        modelDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun importModel(filePath: String): ModelInfo {
        val file = File(filePath)
        require(file.exists()) { "Arquivo de modelo não encontrado: $filePath" }

        val checksum = sha256Of(file)
        val entity = ModelEntity(
            name = file.nameWithoutExtension,
            path = file.absolutePath,
            checksum = checksum,
            architecture = "gguf", // refinado na leitura real do header GGUF pela engine
            contextSize = 4096,
            sizeBytes = file.length()
        )
        val id = modelDao.insert(entity)
        return entity.copy(id = id).toDomain()
    }

    override suspend fun deleteModel(modelId: Long) {
        modelDao.getById(modelId)?.let { modelDao.delete(it) }
    }

    override suspend fun getModel(modelId: Long): ModelInfo? =
        modelDao.getById(modelId)?.toDomain()

    private fun sha256Of(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(8192)
            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}

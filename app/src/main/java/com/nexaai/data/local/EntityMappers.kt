package com.nexaai.data.local

import com.nexaai.data.local.entity.ConversationEntity
import com.nexaai.data.local.entity.MemoryEntity
import com.nexaai.data.local.entity.MessageEntity
import com.nexaai.data.local.entity.ModelEntity
import com.nexaai.domain.model.Conversation
import com.nexaai.domain.model.Memory
import com.nexaai.domain.model.MemoryType
import com.nexaai.domain.model.Message
import com.nexaai.domain.model.MessageRole
import com.nexaai.domain.model.ModelInfo

fun ConversationEntity.toDomain() = Conversation(
    id = id,
    title = title,
    createdAt = createdAt,
    updatedAt = updatedAt,
    modelId = modelId
)

fun Conversation.toEntity() = ConversationEntity(
    id = id,
    title = title,
    createdAt = createdAt,
    updatedAt = updatedAt,
    modelId = modelId
)

fun MessageEntity.toDomain() = Message(
    id = id,
    conversationId = conversationId,
    role = MessageRole.valueOf(role),
    content = content,
    createdAt = createdAt,
    tokenCount = tokens
)

fun Message.toEntity() = MessageEntity(
    id = id,
    conversationId = conversationId,
    role = role.name,
    content = content,
    createdAt = createdAt,
    tokens = tokenCount
)

fun ModelEntity.toDomain() = ModelInfo(
    id = id,
    name = name,
    path = path,
    checksum = checksum,
    architecture = architecture,
    contextSize = contextSize,
    sizeBytes = sizeBytes
)

fun ModelInfo.toEntity() = ModelEntity(
    id = id,
    name = name,
    path = path,
    checksum = checksum,
    architecture = architecture,
    contextSize = contextSize,
    sizeBytes = sizeBytes
)

fun MemoryEntity.toDomain() = Memory(
    id = id,
    content = content,
    type = MemoryType.valueOf(type),
    createdAt = createdAt
)

fun Memory.toEntity() = MemoryEntity(
    id = id,
    content = content,
    type = type.name,
    createdAt = createdAt
)

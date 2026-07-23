package com.nexaai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "models")
data class ModelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val path: String,
    val checksum: String,
    val architecture: String,
    val contextSize: Int,
    val sizeBytes: Long
)

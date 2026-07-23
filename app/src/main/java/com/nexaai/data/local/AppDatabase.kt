package com.nexaai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nexaai.data.local.dao.ConversationDao
import com.nexaai.data.local.dao.MemoryDao
import com.nexaai.data.local.dao.MessageDao
import com.nexaai.data.local.dao.ModelDao
import com.nexaai.data.local.entity.ConversationEntity
import com.nexaai.data.local.entity.MemoryEntity
import com.nexaai.data.local.entity.MessageEntity
import com.nexaai.data.local.entity.ModelEntity

@Database(
    entities = [
        ConversationEntity::class,
        MessageEntity::class,
        ModelEntity::class,
        MemoryEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun modelDao(): ModelDao
    abstract fun memoryDao(): MemoryDao

    companion object {
        const val DATABASE_NAME = "nexaai.db"

        // Seção 11.3: toda alteração futura de schema deve vir com uma Migration
        // explícita adicionada aqui. fallbackToDestructiveMigration() é proibido em produção.
        // Ex.: val MIGRATION_1_2 = object : Migration(1, 2) { override fun migrate(db: SupportSQLiteDatabase) { ... } }
    }
}

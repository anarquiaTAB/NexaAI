package com.nexaai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nexaai.data.local.entity.MemoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {

    @Query("SELECT * FROM memories WHERE type = 'PERMANENT' ORDER BY createdAt DESC")
    fun observePermanent(): Flow<List<MemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: MemoryEntity): Long

    @Delete
    suspend fun delete(entity: MemoryEntity)

    @Query("SELECT * FROM memories WHERE type = 'PERMANENT'")
    suspend fun getAllPermanentSnapshot(): List<MemoryEntity>
}

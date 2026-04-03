package com.example.organizadoracademico.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.organizadoracademico.data.local.entities.SyncQueueEntity

@Dao
interface SyncQueueDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SyncQueueEntity)

    @Query("SELECT * FROM sync_queue ORDER BY createdAt ASC LIMIT :limit")
    suspend fun getPending(limit: Int = 50): List<SyncQueueEntity>

    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM sync_queue WHERE entityType = :entityType AND entityLocalId = :entityLocalId")
    suspend fun deleteByEntity(entityType: String, entityLocalId: Int)

    @Query("UPDATE sync_queue SET retryCount = retryCount + 1 WHERE id = :id")
    suspend fun incrementRetry(id: Int)
}


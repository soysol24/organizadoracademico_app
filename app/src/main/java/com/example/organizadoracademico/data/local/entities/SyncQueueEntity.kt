package com.example.organizadoracademico.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val entityType: String,
    val action: String,
    val entityLocalId: Int,
    val payload: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val retryCount: Int = 0
)


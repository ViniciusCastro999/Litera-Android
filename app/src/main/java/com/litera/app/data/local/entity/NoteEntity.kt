package com.litera.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val volumeId: String,
    val text: String,
    val tags: String,
    val createdAt: Long
)

package com.litera.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shelf_books")
data class ShelfBookEntity(
    @PrimaryKey val volumeId: String,
    val title: String,
    val authorsLabel: String,
    val thumbnailUrl: String?,
    val isFavorite: Boolean,
    val isRead: Boolean,
    val currentPage: Int,
    val totalPages: Int,
    val addedAt: Long,
    val updatedAt: Long
)

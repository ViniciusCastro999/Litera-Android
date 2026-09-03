package com.litera.app.domain.model

/**
 * A book the signed-in user has added to their "Estante". Book metadata is
 * denormalized (copied) from [Book] at the moment it's added so the shelf
 * still renders nicely offline / without refetching from the API.
 */
data class ShelfBook(
    val volumeId: String,
    val title: String,
    val authorsLabel: String,
    val thumbnailUrl: String?,
    val isFavorite: Boolean,
    val isRead: Boolean,
    val currentPage: Int,
    val totalPages: Int,
    val addedAt: Long,
    val updatedAt: Long
) {
    val isCurrentlyReading: Boolean
        get() = !isRead && currentPage > 0

    val progress: Float
        get() = if (totalPages <= 0) 0f else (currentPage.toFloat() / totalPages).coerceIn(0f, 1f)
}

package com.litera.app.domain.repository

import com.litera.app.domain.model.Book
import com.litera.app.domain.model.ShelfBook
import kotlinx.coroutines.flow.Flow

interface ShelfRepository {
    fun observeShelf(): Flow<List<ShelfBook>>
    fun observeShelfItem(volumeId: String): Flow<ShelfBook?>

    suspend fun toggleFavorite(book: Book)
    suspend fun startReading(book: Book, totalPages: Int)
    suspend fun updateProgress(volumeId: String, currentPage: Int, totalPages: Int)
    suspend fun markAsRead(volumeId: String)
    suspend fun removeFromShelf(volumeId: String)
}

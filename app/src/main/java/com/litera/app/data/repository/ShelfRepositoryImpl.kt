package com.litera.app.data.repository

import com.litera.app.data.local.dao.ShelfDao
import com.litera.app.data.local.entity.ShelfBookEntity
import com.litera.app.domain.model.Book
import com.litera.app.domain.model.ShelfBook
import com.litera.app.domain.repository.ShelfRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ShelfRepositoryImpl @Inject constructor(
    private val dao: ShelfDao
) : ShelfRepository {

    override fun observeShelf(): Flow<List<ShelfBook>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeShelfItem(volumeId: String): Flow<ShelfBook?> =
        dao.observeById(volumeId).map { it?.toDomain() }

    override suspend fun toggleFavorite(book: Book) {
        val existing = dao.getById(book.volumeId)
        val now = System.currentTimeMillis()
        if (existing != null) {
            dao.upsert(existing.copy(isFavorite = !existing.isFavorite, updatedAt = now))
        } else {
            dao.upsert(book.toNewEntity(now).copy(isFavorite = true))
        }
    }

    override suspend fun startReading(book: Book, totalPages: Int) {
        val existing = dao.getById(book.volumeId)
        val now = System.currentTimeMillis()
        if (existing != null) {
            dao.upsert(
                existing.copy(
                    isRead = false,
                    currentPage = if (existing.currentPage <= 0) 1 else existing.currentPage,
                    totalPages = if (totalPages > 0) totalPages else existing.totalPages,
                    updatedAt = now
                )
            )
        } else {
            dao.upsert(book.toNewEntity(now).copy(currentPage = 1, totalPages = totalPages))
        }
    }

    override suspend fun updateProgress(volumeId: String, currentPage: Int, totalPages: Int) {
        val existing = dao.getById(volumeId) ?: return
        val now = System.currentTimeMillis()
        val isNowRead = totalPages > 0 && currentPage >= totalPages
        dao.upsert(
            existing.copy(
                currentPage = currentPage.coerceAtLeast(0),
                totalPages = totalPages.coerceAtLeast(existing.totalPages),
                isRead = existing.isRead || isNowRead,
                updatedAt = now
            )
        )
    }

    override suspend fun markAsRead(volumeId: String) {
        val existing = dao.getById(volumeId) ?: return
        dao.upsert(existing.copy(isRead = true, updatedAt = System.currentTimeMillis()))
    }

    override suspend fun removeFromShelf(volumeId: String) {
        dao.deleteById(volumeId)
    }
}

private fun ShelfBookEntity.toDomain() = ShelfBook(
    volumeId = volumeId,
    title = title,
    authorsLabel = authorsLabel,
    thumbnailUrl = thumbnailUrl,
    isFavorite = isFavorite,
    isRead = isRead,
    currentPage = currentPage,
    totalPages = totalPages,
    addedAt = addedAt,
    updatedAt = updatedAt
)

private fun Book.toNewEntity(now: Long) = ShelfBookEntity(
    volumeId = volumeId,
    title = title,
    authorsLabel = authorsLabel,
    thumbnailUrl = thumbnailUrl,
    isFavorite = false,
    isRead = false,
    currentPage = 0,
    totalPages = pageCount,
    addedAt = now,
    updatedAt = now
)

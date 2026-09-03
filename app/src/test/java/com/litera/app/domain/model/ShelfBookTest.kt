package com.litera.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShelfBookTest {

    @Test
    fun `isCurrentlyReading is true when not read and has progress`() {
        val book = shelfBook(isRead = false, currentPage = 10)

        assertTrue(book.isCurrentlyReading)
    }

    @Test
    fun `isCurrentlyReading is false when marked as read`() {
        val book = shelfBook(isRead = true, currentPage = 10)

        assertFalse(book.isCurrentlyReading)
    }

    @Test
    fun `isCurrentlyReading is false when no pages read yet`() {
        val book = shelfBook(isRead = false, currentPage = 0)

        assertFalse(book.isCurrentlyReading)
    }

    @Test
    fun `progress is 0 when totalPages is not positive`() {
        val book = shelfBook(currentPage = 5, totalPages = 0)

        assertEquals(0f, book.progress)
    }

    @Test
    fun `progress is currentPage over totalPages`() {
        val book = shelfBook(currentPage = 50, totalPages = 200)

        assertEquals(0.25f, book.progress)
    }

    @Test
    fun `progress is coerced to 1 when currentPage exceeds totalPages`() {
        val book = shelfBook(currentPage = 999, totalPages = 200)

        assertEquals(1f, book.progress)
    }

    private fun shelfBook(
        isRead: Boolean = false,
        currentPage: Int = 0,
        totalPages: Int = 100
    ) = ShelfBook(
        volumeId = "1",
        title = "Dom Casmurro",
        authorsLabel = "Machado de Assis",
        thumbnailUrl = null,
        isFavorite = false,
        isRead = isRead,
        currentPage = currentPage,
        totalPages = totalPages,
        addedAt = 0L,
        updatedAt = 0L
    )
}

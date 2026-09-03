package com.litera.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BookTest {

    @Test
    fun `authorsLabel joins multiple authors with comma`() {
        val book = book(authors = listOf("Machado de Assis", "Clarice Lispector"))

        assertEquals("Machado de Assis, Clarice Lispector", book.authorsLabel)
    }

    @Test
    fun `authorsLabel falls back to placeholder when there are no authors`() {
        val book = book(authors = emptyList())

        assertEquals("Autor desconhecido", book.authorsLabel)
    }

    @Test
    fun `primaryCategory returns the first category`() {
        val book = book(categories = listOf("Romance", "Clássicos"))

        assertEquals("Romance", book.primaryCategory)
    }

    @Test
    fun `primaryCategory is null when there are no categories`() {
        val book = book(categories = emptyList())

        assertNull(book.primaryCategory)
    }

    private fun book(
        authors: List<String> = emptyList(),
        categories: List<String> = emptyList()
    ) = Book(
        volumeId = "1",
        title = "Dom Casmurro",
        authors = authors,
        description = "",
        thumbnailUrl = null,
        categories = categories,
        pageCount = 200,
        averageRating = null,
        ratingsCount = 0,
        publishedDate = null,
        publisher = null,
        language = null,
        previewLink = null
    )
}

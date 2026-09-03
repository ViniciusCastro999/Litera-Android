package com.litera.app.domain.usecase

import com.litera.app.core.common.Resource
import com.litera.app.domain.model.Book
import com.litera.app.domain.repository.BookRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class BookUseCasesTest {

    private val repository: BookRepository = mockk()
    private lateinit var searchBooks: SearchBooksUseCase

    @Before
    fun setUp() {
        searchBooks = SearchBooksUseCase(repository)
    }

    @Test
    fun `search with blank query returns empty success without hitting repository`() = runTest {
        val result = searchBooks("   ")

        assertEquals(Resource.Success(emptyList<Book>()), result)
        coVerify(exactly = 0) { repository.searchBooks(any()) }
    }

    @Test
    fun `search trims query before delegating to repository`() = runTest {
        val books = listOf(sampleBook())
        coEvery { repository.searchBooks("dom casmurro") } returns Resource.Success(books)

        val result = searchBooks("  dom casmurro  ")

        assertEquals(Resource.Success(books), result)
        coVerify(exactly = 1) { repository.searchBooks("dom casmurro") }
    }

    @Test
    fun `search propagates repository errors`() = runTest {
        coEvery { repository.searchBooks("erro") } returns Resource.Error("Sem conexão com a internet.")

        val result = searchBooks("erro")

        assertEquals(Resource.Error("Sem conexão com a internet."), result)
    }

    private fun sampleBook() = Book(
        volumeId = "1",
        title = "Dom Casmurro",
        authors = listOf("Machado de Assis"),
        description = "",
        thumbnailUrl = null,
        categories = emptyList(),
        pageCount = 200,
        averageRating = null,
        ratingsCount = 0,
        publishedDate = null,
        publisher = null,
        language = null,
        previewLink = null
    )
}

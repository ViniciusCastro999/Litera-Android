package com.litera.app.data.remote

import com.litera.app.data.remote.dto.ImageLinksDto
import com.litera.app.data.remote.dto.VolumeDto
import com.litera.app.data.remote.dto.VolumeInfoDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BookMapperTest {

    @Test
    fun `maps a fully populated dto to the domain model`() {
        val dto = VolumeDto(
            id = "abc123",
            volumeInfo = VolumeInfoDto(
                title = "Dom Casmurro",
                authors = listOf("Machado de Assis"),
                publisher = "Editora X",
                publishedDate = "1899",
                description = "Um clássico.",
                pageCount = 256,
                categories = listOf("Romance"),
                averageRating = 4.5,
                ratingsCount = 120,
                imageLinks = ImageLinksDto(thumbnail = "http://books.google.com/cover.jpg"),
                language = "pt",
                previewLink = "http://books.google.com/preview"
            )
        )

        val book = dto.toDomain()

        assertEquals("abc123", book.volumeId)
        assertEquals("Dom Casmurro", book.title)
        assertEquals(listOf("Machado de Assis"), book.authors)
        assertEquals("Um clássico.", book.description)
        assertEquals("https://books.google.com/cover.jpg", book.thumbnailUrl)
        assertEquals(listOf("Romance"), book.categories)
        assertEquals(256, book.pageCount)
        assertEquals(4.5, book.averageRating)
        assertEquals(120, book.ratingsCount)
        assertEquals("1899", book.publishedDate)
    }

    @Test
    fun `fills sensible defaults when volumeInfo fields are missing`() {
        val dto = VolumeDto(id = "no-info", volumeInfo = null)

        val book = dto.toDomain()

        assertEquals("no-info", book.volumeId)
        assertEquals("Título desconhecido", book.title)
        assertEquals(emptyList<String>(), book.authors)
        assertEquals("Sinopse não disponível para este livro.", book.description)
        assertNull(book.thumbnailUrl)
        assertEquals(0, book.pageCount)
        assertEquals(0, book.ratingsCount)
    }

    @Test
    fun `falls back to smallThumbnail and upgrades it to https`() {
        val dto = VolumeDto(
            id = "1",
            volumeInfo = VolumeInfoDto(
                imageLinks = ImageLinksDto(smallThumbnail = "http://books.google.com/small.jpg")
            )
        )

        val book = dto.toDomain()

        assertEquals("https://books.google.com/small.jpg", book.thumbnailUrl)
    }
}

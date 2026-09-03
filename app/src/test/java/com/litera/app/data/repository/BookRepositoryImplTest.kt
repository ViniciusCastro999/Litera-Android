package com.litera.app.data.repository

import com.litera.app.core.common.Resource
import com.litera.app.data.remote.GoogleBooksApiService
import com.litera.app.data.remote.dto.VolumeDto
import com.litera.app.data.remote.dto.VolumeInfoDto
import com.litera.app.data.remote.dto.VolumesResponseDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class BookRepositoryImplTest {

    private val api: GoogleBooksApiService = mockk()
    private lateinit var repository: BookRepositoryImpl

    @Before
    fun setUp() {
        repository = BookRepositoryImpl(api, Dispatchers.Unconfined)
    }

    @Test
    fun `searchBooks maps a successful response into domain books`() = runTest {
        coEvery {
            api.searchVolumes(query = "dom casmurro", langRestrict = any(), country = any(), maxResults = any(), apiKey = any())
        } returns VolumesResponseDto(items = listOf(volume("1", "Dom Casmurro")))

        val result = repository.searchBooks("dom casmurro")

        assertTrue(result is Resource.Success)
        assertEquals(listOf("Dom Casmurro"), (result as Resource.Success).data.map { it.title })
    }

    @Test
    fun `searchBooks maps IOException to a connectivity error`() = runTest {
        coEvery {
            api.searchVolumes(query = any(), langRestrict = any(), country = any(), maxResults = any(), apiKey = any())
        } throws IOException("no network")

        val result = repository.searchBooks("qualquer coisa")

        assertTrue(result is Resource.Error)
        assertEquals("Sem conexão com a internet. Verifique sua rede e tente novamente.", (result as Resource.Error).message)
    }

    @Test
    fun `searchBooks maps HttpException to an error containing the status code`() = runTest {
        val httpException = HttpException(
            Response.error<VolumesResponseDto>(404, "not found".toResponseBody("text/plain".toMediaType()))
        )
        coEvery {
            api.searchVolumes(query = any(), langRestrict = any(), country = any(), maxResults = any(), apiKey = any())
        } throws httpException

        val result = repository.searchBooks("qualquer coisa")

        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).message.contains("404"))
    }

    @Test
    fun `searchBooks maps unexpected exceptions to a generic error`() = runTest {
        coEvery {
            api.searchVolumes(query = any(), langRestrict = any(), country = any(), maxResults = any(), apiKey = any())
        } throws IllegalStateException("boom")

        val result = repository.searchBooks("qualquer coisa")

        assertTrue(result is Resource.Error)
        assertEquals("boom", (result as Resource.Error).message)
    }

    @Test
    fun `getBooksByAuthor excludes the given volume id from the results`() = runTest {
        coEvery {
            api.searchVolumes(query = any(), langRestrict = any(), country = any(), maxResults = any(), apiKey = any())
        } returns VolumesResponseDto(items = listOf(volume("1", "Livro A"), volume("2", "Livro B")))

        val result = repository.getBooksByAuthor("Autor", excludeVolumeId = "1")

        assertTrue(result is Resource.Success)
        val titles = (result as Resource.Success).data.map { it.volumeId }
        assertEquals(listOf("2"), titles)
    }

    @Test
    fun `getRecommendedBooks falls back to default categories when none are preferred`() = runTest {
        val querySlot = slot<String>()
        coEvery {
            api.searchVolumes(query = capture(querySlot), langRestrict = any(), country = any(), maxResults = any(), apiKey = any())
        } returns VolumesResponseDto(items = emptyList())

        repository.getRecommendedBooks(preferredCategories = emptyList())

        assertTrue(querySlot.captured.contains("subject:"))
        coVerify(exactly = 1) { api.searchVolumes(query = any(), langRestrict = any(), country = any(), maxResults = any(), apiKey = any()) }
    }

    private fun volume(id: String, title: String) = VolumeDto(
        id = id,
        volumeInfo = VolumeInfoDto(title = title)
    )
}

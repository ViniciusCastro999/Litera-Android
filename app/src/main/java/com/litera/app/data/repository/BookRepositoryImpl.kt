package com.litera.app.data.repository

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.litera.app.BuildConfig
import com.litera.app.core.common.Constants
import com.litera.app.core.common.Resource
import com.litera.app.core.di.IoDispatcher
import com.litera.app.data.remote.GoogleBooksApiService
import com.litera.app.data.remote.toDomain
import com.litera.app.domain.model.Book
import com.litera.app.domain.repository.BookRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val api: GoogleBooksApiService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val crashlytics: FirebaseCrashlytics
) : BookRepository {

    // Google Books works without a key at a shared/lower quota; an empty
    // string is simply omitted by Retrofit's null-safe @Query handling below.
    private val apiKey: String? = BuildConfig.BOOKS_API_KEY.ifBlank { null }

    override suspend fun searchBooks(query: String): Resource<List<Book>> = safeCall {
        api.searchVolumes(
            // Plain "q=<text>" ranks poorly on the Google Books API,
            // surfacing tangential matches ahead of well-known titles.
            // intitle: matches how a book search box is actually used and
            // ranks title matches first; see also inauthor: below.
            query = "intitle:\"$query\"",
            langRestrict = Constants.BOOKS_LANGUAGE_RESTRICT,
            country = Constants.BOOKS_COUNTRY,
            maxResults = Constants.DEFAULT_PAGE_SIZE,
            apiKey = apiKey
        ).items.orEmpty().map { it.toDomain() }
    }

    override suspend fun getBooksByCategory(category: String, maxResults: Int): Resource<List<Book>> = safeCall {
        api.searchVolumes(
            query = "subject:\"$category\"",
            langRestrict = Constants.BOOKS_LANGUAGE_RESTRICT,
            country = Constants.BOOKS_COUNTRY,
            maxResults = maxResults,
            apiKey = apiKey
        ).items.orEmpty().map { it.toDomain() }
    }

    override suspend fun getRecommendedBooks(preferredCategories: List<String>): Resource<List<Book>> = safeCall {
        val categories = preferredCategories.ifEmpty { Constants.DEFAULT_CATEGORIES.take(3) }
        val query = categories.joinToString(separator = " OR ") { "subject:\"$it\"" }
        api.searchVolumes(
            query = query,
            langRestrict = Constants.BOOKS_LANGUAGE_RESTRICT,
            country = Constants.BOOKS_COUNTRY,
            maxResults = Constants.DEFAULT_PAGE_SIZE,
            apiKey = apiKey
        ).items.orEmpty().map { it.toDomain() }
    }

    override suspend fun getBookDetail(volumeId: String): Resource<Book> = safeCall {
        api.getVolume(volumeId = volumeId, country = Constants.BOOKS_COUNTRY, apiKey = apiKey).toDomain()
    }

    override suspend fun getBooksByAuthor(author: String, excludeVolumeId: String?): Resource<List<Book>> = safeCall {
        api.searchVolumes(
            query = "inauthor:\"$author\"",
            langRestrict = Constants.BOOKS_LANGUAGE_RESTRICT,
            country = Constants.BOOKS_COUNTRY,
            maxResults = Constants.DEFAULT_PAGE_SIZE,
            apiKey = apiKey
        ).items.orEmpty()
            .map { it.toDomain() }
            .filter { it.volumeId != excludeVolumeId }
    }

    private suspend fun <T> safeCall(block: suspend () -> T): Resource<T> = withContext(ioDispatcher) {
        try {
            Resource.Success(block())
        } catch (e: IOException) {
            Resource.Error("Sem conexão com a internet. Verifique sua rede e tente novamente.", e)
        } catch (e: retrofit2.HttpException) {
            Resource.Error("Não foi possível buscar os livros agora (erro ${e.code()}).", e)
        } catch (e: Exception) {
            // IOException/HttpException are expected day-to-day (offline, API
            // hiccups); anything else here is unexpected, so it's worth a
            // non-fatal report instead of silently showing a generic error.
            crashlytics.recordException(e)
            Resource.Error(e.message ?: "Ocorreu um erro inesperado.", e)
        }
    }
}

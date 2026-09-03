package com.litera.app.domain.usecase

import com.litera.app.core.common.Resource
import com.litera.app.domain.model.Book
import com.litera.app.domain.repository.BookRepository
import javax.inject.Inject

class SearchBooksUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(query: String): Resource<List<Book>> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return Resource.Success(emptyList())
        return repository.searchBooks(trimmed)
    }
}

class GetBooksByCategoryUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(category: String): Resource<List<Book>> =
        repository.getBooksByCategory(category)
}

class GetRecommendedBooksUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(preferredCategories: List<String>): Resource<List<Book>> =
        repository.getRecommendedBooks(preferredCategories)
}

class GetBookDetailUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(volumeId: String): Resource<Book> =
        repository.getBookDetail(volumeId)
}

class GetBooksByAuthorUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(author: String, excludeVolumeId: String? = null): Resource<List<Book>> =
        repository.getBooksByAuthor(author, excludeVolumeId)
}

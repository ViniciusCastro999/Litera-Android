package com.litera.app.domain.repository

import com.litera.app.core.common.Resource
import com.litera.app.domain.model.Book

interface BookRepository {
    suspend fun searchBooks(query: String): Resource<List<Book>>
    suspend fun getBooksByCategory(category: String, maxResults: Int = 20): Resource<List<Book>>
    suspend fun getRecommendedBooks(preferredCategories: List<String>): Resource<List<Book>>
    suspend fun getBookDetail(volumeId: String): Resource<Book>
    suspend fun getBooksByAuthor(author: String, excludeVolumeId: String? = null): Resource<List<Book>>
}

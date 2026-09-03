package com.litera.app.domain.model

/**
 * Domain representation of a book, sourced from the Google Books API and
 * mapped away from its DTO shape so the rest of the app never depends on
 * network types directly.
 */
data class Book(
    val volumeId: String,
    val title: String,
    val authors: List<String>,
    val description: String,
    val thumbnailUrl: String?,
    val categories: List<String>,
    val pageCount: Int,
    val averageRating: Double?,
    val ratingsCount: Int,
    val publishedDate: String?,
    val publisher: String?,
    val language: String?,
    val previewLink: String?
) {
    val authorsLabel: String
        get() = authors.joinToString(", ").ifBlank { "Autor desconhecido" }

    val primaryCategory: String?
        get() = categories.firstOrNull()
}

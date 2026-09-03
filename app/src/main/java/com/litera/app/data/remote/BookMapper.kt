package com.litera.app.data.remote

import com.litera.app.data.remote.dto.VolumeDto
import com.litera.app.domain.model.Book

fun VolumeDto.toDomain(): Book {
    val info = volumeInfo
    return Book(
        volumeId = id,
        title = info?.title ?: "Título desconhecido",
        authors = info?.authors ?: emptyList(),
        description = info?.description ?: "Sinopse não disponível para este livro.",
        thumbnailUrl = info?.imageLinks?.thumbnail?.toHttps() ?: info?.imageLinks?.smallThumbnail?.toHttps(),
        categories = info?.categories ?: emptyList(),
        pageCount = info?.pageCount ?: 0,
        averageRating = info?.averageRating,
        ratingsCount = info?.ratingsCount ?: 0,
        publishedDate = info?.publishedDate,
        publisher = info?.publisher,
        language = info?.language,
        previewLink = info?.previewLink
    )
}

// The API sometimes returns http:// image links, which are blocked by
// Android's default cleartext traffic policy on API 28+.
private fun String.toHttps(): String = replaceFirst("http://", "https://")

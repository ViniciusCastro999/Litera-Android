package com.litera.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class VolumesResponseDto(
    val totalItems: Int = 0,
    val items: List<VolumeDto>? = null
)

@Serializable
data class VolumeDto(
    val id: String,
    val volumeInfo: VolumeInfoDto? = null
)

@Serializable
data class VolumeInfoDto(
    val title: String? = null,
    val authors: List<String>? = null,
    val publisher: String? = null,
    val publishedDate: String? = null,
    val description: String? = null,
    val pageCount: Int? = null,
    val categories: List<String>? = null,
    val averageRating: Double? = null,
    val ratingsCount: Int? = null,
    val imageLinks: ImageLinksDto? = null,
    val language: String? = null,
    val previewLink: String? = null
)

@Serializable
data class ImageLinksDto(
    val smallThumbnail: String? = null,
    val thumbnail: String? = null
)

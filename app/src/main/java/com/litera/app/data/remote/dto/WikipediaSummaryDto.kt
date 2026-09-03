package com.litera.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class WikipediaSummaryDto(
    val title: String? = null,
    val extract: String? = null,
    val thumbnail: WikipediaThumbnailDto? = null
)

@Serializable
data class WikipediaThumbnailDto(
    val source: String? = null
)

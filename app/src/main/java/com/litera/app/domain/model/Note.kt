package com.litera.app.domain.model

data class Note(
    val id: Long,
    val volumeId: String,
    val text: String,
    val tags: List<String>,
    val createdAt: Long
)

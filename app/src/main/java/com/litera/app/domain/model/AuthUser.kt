package com.litera.app.domain.model

data class AuthUser(
    val uid: String,
    val email: String?,
    val displayName: String?
)

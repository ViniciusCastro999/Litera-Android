package com.litera.app.data.repository

import com.litera.app.core.common.Resource
import com.litera.app.data.remote.WikipediaApiService
import com.litera.app.domain.model.AuthorProfile
import com.litera.app.domain.repository.AuthorRepository
import javax.inject.Inject

class AuthorRepositoryImpl @Inject constructor(
    private val wikipediaApiService: WikipediaApiService
) : AuthorRepository {

    override suspend fun getAuthorProfile(name: String): Resource<AuthorProfile> {
        // Wikipedia lookups are best-effort: a miss (404, no network, an
        // ambiguous/foreign name) must never crash or block the author
        // screen — it just shows "biografia não disponível" instead.
        val summary = runCatching { wikipediaApiService.getSummary(name) }.getOrNull()
        return Resource.Success(
            AuthorProfile(
                name = name,
                bio = summary?.extract?.takeIf { it.isNotBlank() },
                photoUrl = summary?.thumbnail?.source
            )
        )
    }
}

package com.litera.app.domain.repository

import com.litera.app.core.common.Resource
import com.litera.app.domain.model.AuthorProfile

interface AuthorRepository {
    suspend fun getAuthorProfile(name: String): Resource<AuthorProfile>
}

package com.litera.app.domain.usecase

import com.litera.app.core.common.Resource
import com.litera.app.domain.model.AuthorProfile
import com.litera.app.domain.repository.AuthorRepository
import javax.inject.Inject

class GetAuthorProfileUseCase @Inject constructor(
    private val repository: AuthorRepository
) {
    suspend operator fun invoke(name: String): Resource<AuthorProfile> = repository.getAuthorProfile(name)
}

package com.litera.app.domain.usecase

import com.litera.app.domain.model.Club
import com.litera.app.domain.model.Comment
import com.litera.app.domain.model.Post
import com.litera.app.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePostsUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    operator fun invoke(): Flow<List<Post>> = repository.observePosts()
}

class ObservePostUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    operator fun invoke(postId: Long): Flow<Post?> = repository.observePost(postId)
}

class ObserveCommentsUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    operator fun invoke(postId: Long): Flow<List<Comment>> = repository.observeComments(postId)
}

class ObserveClubsUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    operator fun invoke(): Flow<List<Club>> = repository.observeClubs()
}

class ObserveClubUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    operator fun invoke(clubId: Long): Flow<Club?> = repository.observeClub(clubId)
}

class CreatePostUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    suspend operator fun invoke(authorName: String, authorPhotoUrl: String?, text: String, tags: List<String>) =
        repository.createPost(authorName, authorPhotoUrl, text, tags)
}

class ToggleLikeUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    suspend operator fun invoke(postId: Long) = repository.toggleLike(postId)
}

class AddCommentUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    suspend operator fun invoke(postId: Long, authorName: String, text: String) =
        repository.addComment(postId, authorName, text)
}

class SetClubMembershipUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    suspend operator fun invoke(clubId: Long, isMember: Boolean) = repository.setClubMembership(clubId, isMember)
}

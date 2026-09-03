package com.litera.app.data.repository

import com.litera.app.data.local.dao.ClubDao
import com.litera.app.data.local.dao.CommentDao
import com.litera.app.data.local.dao.PostDao
import com.litera.app.data.local.entity.ClubEntity
import com.litera.app.data.local.entity.CommentEntity
import com.litera.app.data.local.entity.PostEntity
import com.litera.app.domain.model.Club
import com.litera.app.domain.model.Comment
import com.litera.app.domain.model.Post
import com.litera.app.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG_DELIMITER = "||"

class CommunityRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val commentDao: CommentDao,
    private val clubDao: ClubDao
) : CommunityRepository {

    override fun observePosts(): Flow<List<Post>> =
        // Comment counts aren't joined into the feed query (kept simple);
        // the post detail screen shows the exact count via observePost.
        postDao.observeAll().map { posts -> posts.map { it.toDomain(commentCount = 0) } }

    override fun observePost(postId: Long): Flow<Post?> =
        postDao.observeById(postId).combine(commentDao.observeByPost(postId)) { post, comments ->
            post?.toDomain(commentCount = comments.size)
        }

    override fun observeComments(postId: Long): Flow<List<Comment>> =
        commentDao.observeByPost(postId).map { list -> list.map { it.toDomain() } }

    override fun observeClubs(): Flow<List<Club>> =
        clubDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeClub(clubId: Long): Flow<Club?> =
        clubDao.observeById(clubId).map { it?.toDomain() }

    override suspend fun createPost(authorName: String, authorPhotoUrl: String?, text: String, tags: List<String>) {
        postDao.insert(
            PostEntity(
                authorName = authorName,
                authorPhotoUrl = authorPhotoUrl,
                text = text,
                imageUrl = null,
                tags = tags.joinToString(TAG_DELIMITER),
                likeCount = 0,
                likedByMe = false,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun toggleLike(postId: Long) {
        val entity = postDao.getById(postId) ?: return
        postDao.update(
            entity.copy(
                likedByMe = !entity.likedByMe,
                likeCount = if (entity.likedByMe) entity.likeCount - 1 else entity.likeCount + 1
            )
        )
    }

    override suspend fun addComment(postId: Long, authorName: String, text: String) {
        commentDao.insert(CommentEntity(postId = postId, authorName = authorName, text = text, createdAt = System.currentTimeMillis()))
    }

    override suspend fun setClubMembership(clubId: Long, isMember: Boolean) {
        val entity = clubDao.getById(clubId) ?: return
        val delta = if (isMember) 1 else -1
        clubDao.update(entity.copy(isMember = isMember, memberCount = (entity.memberCount + delta).coerceAtLeast(0)))
    }
}

private fun PostEntity.toDomain(commentCount: Int) = Post(
    id = id,
    authorName = authorName,
    authorPhotoUrl = authorPhotoUrl,
    text = text,
    imageUrl = imageUrl,
    tags = tags.split(TAG_DELIMITER).filter { it.isNotBlank() },
    likeCount = likeCount,
    likedByMe = likedByMe,
    commentCount = commentCount,
    createdAt = createdAt
)

private fun CommentEntity.toDomain() = Comment(
    id = id,
    postId = postId,
    authorName = authorName,
    text = text,
    createdAt = createdAt
)

private fun ClubEntity.toDomain() = Club(
    id = id,
    handle = handle,
    avatarUrl = avatarUrl,
    memberCount = memberCount,
    currentBookTitle = currentBookTitle,
    currentBookThumbnailUrl = currentBookThumbnailUrl,
    description = description,
    isMember = isMember
)

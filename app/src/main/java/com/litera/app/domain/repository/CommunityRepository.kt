package com.litera.app.domain.repository

import com.litera.app.domain.model.Club
import com.litera.app.domain.model.Comment
import com.litera.app.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    fun observePosts(): Flow<List<Post>>
    fun observePost(postId: Long): Flow<Post?>
    fun observeComments(postId: Long): Flow<List<Comment>>
    fun observeClubs(): Flow<List<Club>>
    fun observeClub(clubId: Long): Flow<Club?>

    suspend fun createPost(authorName: String, authorPhotoUrl: String?, text: String, tags: List<String>)
    suspend fun toggleLike(postId: Long)
    suspend fun addComment(postId: Long, authorName: String, text: String)
    suspend fun setClubMembership(clubId: Long, isMember: Boolean)
}

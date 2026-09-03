package com.litera.app.domain.model

data class Post(
    val id: Long,
    val authorName: String,
    val authorPhotoUrl: String?,
    val text: String,
    val imageUrl: String?,
    val tags: List<String>,
    val likeCount: Int,
    val likedByMe: Boolean,
    val commentCount: Int,
    val createdAt: Long
)

data class Comment(
    val id: Long,
    val postId: Long,
    val authorName: String,
    val text: String,
    val createdAt: Long
)

data class Club(
    val id: Long,
    val handle: String,
    val avatarUrl: String?,
    val memberCount: Int,
    val currentBookTitle: String?,
    val currentBookThumbnailUrl: String?,
    val description: String,
    val isMember: Boolean
)

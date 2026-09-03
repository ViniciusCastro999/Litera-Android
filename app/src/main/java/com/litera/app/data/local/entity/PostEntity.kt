package com.litera.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val authorName: String,
    val authorPhotoUrl: String?,
    val text: String,
    val imageUrl: String?,
    val tags: String,
    val likeCount: Int,
    val likedByMe: Boolean,
    val createdAt: Long
)

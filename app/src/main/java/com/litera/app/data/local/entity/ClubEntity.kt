package com.litera.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clubs")
data class ClubEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val handle: String,
    val avatarUrl: String?,
    val memberCount: Int,
    val currentBookTitle: String?,
    val currentBookThumbnailUrl: String?,
    val description: String,
    val isMember: Boolean
)

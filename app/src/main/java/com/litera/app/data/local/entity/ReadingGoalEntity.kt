package com.litera.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reading_goals")
data class ReadingGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val label: String,
    val type: String,
    val targetValue: Int,
    val progressValue: Int,
    val createdAt: Long
)

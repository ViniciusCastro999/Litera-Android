package com.litera.app.domain.model

enum class ReadingGoalType {
    PAGES_PER_WEEK,
    BOOKS_PER_MONTH,
    NATIONAL_BOOKS
}

data class ReadingGoal(
    val id: Long,
    val label: String,
    val type: ReadingGoalType,
    val targetValue: Int,
    val progressValue: Int,
    val createdAt: Long
)

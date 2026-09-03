package com.litera.app.domain.model

data class FocusSession(
    val id: Long,
    val durationSeconds: Int,
    val completedAt: Long
)

data class FocusSettings(
    val durationOptionsSeconds: List<Int> = listOf(15 * 60, 30 * 60, 60 * 60),
    val selectedDurationSeconds: Int = 15 * 60,
    val notifyMorning: Boolean = false,
    val notifyAfternoon: Boolean = false,
    val notifyNight: Boolean = false
)

data class FocusStats(
    val totalFocusSeconds: Long,
    val sessionsCompleted: Int,
    val xp: Int
)

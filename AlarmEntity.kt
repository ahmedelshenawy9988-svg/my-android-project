package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val label: String = "المنبه",
    val isEnabled: Boolean = true,
    // Comma-separated list of Calendar day constants (e.g. "1,2,3,4,5,6,7" for Sun-Sat). Empty means one-time alarm.
    val daysOfWeek: String = "",
    val difficulty: String = "MEDIUM", // EASY, MEDIUM, HARD
    val questionsCount: Int = 5,
    val vibrate: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getFormattedTime(): String {
        val h = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
        val m = String.format("%02d", minute)
        val period = if (hour < 12) "ص" else "م"
        return "$h:$m $period"
    }

    fun getDaysList(): List<Int> {
        if (daysOfWeek.isBlank()) return emptyList()
        return daysOfWeek.split(",").mapNotNull { it.trim().toIntOrNull() }
    }

    fun isRepeating(): Boolean = daysOfWeek.isNotBlank()
}

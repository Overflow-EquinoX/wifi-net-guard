package com.gorinox.netguard.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_stats")
data class DailyStatsEntity(
    @PrimaryKey
    val date: String, // "YYYY-MM-DD" formatında günün tarihi
    val protectionDurationMinutes: Long = 0,
    val blockedAdsCount: Int = 0,
    val blockedTrackersCount: Int = 0,
    val threatsBlockedCount: Int = 0
)

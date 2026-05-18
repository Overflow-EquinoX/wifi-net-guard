package com.gorinox.netguard.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WiFiProfileDao {
    @Query("SELECT * FROM wifi_profiles")
    fun getAllProfilesFlow(): Flow<List<WiFiProfileEntity>>

    @Query("SELECT * FROM wifi_profiles")
    suspend fun getAllProfiles(): List<WiFiProfileEntity>

    @Query("SELECT * FROM wifi_profiles WHERE bssid = :bssid LIMIT 1")
    suspend fun getProfileByBssid(bssid: String): WiFiProfileEntity?

    @Query("SELECT * FROM wifi_profiles WHERE ssid = :ssid")
    suspend fun getProfilesBySsid(ssid: String): List<WiFiProfileEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: WiFiProfileEntity)

    @Update
    suspend fun updateProfile(profile: WiFiProfileEntity)

    @Delete
    suspend fun deleteProfile(profile: WiFiProfileEntity)
}

@Dao
interface ThreatLogDao {
    @Query("SELECT * FROM threat_logs ORDER BY timestamp DESC")
    fun getAllLogsFlow(): Flow<List<ThreatLogEntity>>

    @Query("SELECT * FROM threat_logs ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentLogs(limit: Int): List<ThreatLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ThreatLogEntity)

    @Query("UPDATE threat_logs SET resolved = 1 WHERE id = :id")
    suspend fun resolveThreat(id: Long)
}

@Dao
interface DailyStatsDao {
    @Query("SELECT * FROM daily_stats ORDER BY date DESC")
    fun getAllStatsFlow(): Flow<List<DailyStatsEntity>>

    @Query("SELECT * FROM daily_stats WHERE date = :date LIMIT 1")
    suspend fun getStatsForDate(date: String): DailyStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStats(stats: DailyStatsEntity)

    @Query("UPDATE daily_stats SET protectionDurationMinutes = protectionDurationMinutes + :minutes WHERE date = :date")
    suspend fun incrementDuration(date: String, minutes: Long)

    @Query("UPDATE daily_stats SET blockedAdsCount = blockedAdsCount + 1 WHERE date = :date")
    suspend fun incrementAds(date: String)

    @Query("UPDATE daily_stats SET blockedTrackersCount = blockedTrackersCount + 1 WHERE date = :date")
    suspend fun incrementTrackers(date: String)

    @Query("UPDATE daily_stats SET threatsBlockedCount = threatsBlockedCount + 1 WHERE date = :date")
    suspend fun incrementThreats(date: String)
}

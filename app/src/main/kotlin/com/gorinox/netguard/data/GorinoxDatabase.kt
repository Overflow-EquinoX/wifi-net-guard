package com.gorinox.netguard.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [WiFiProfileEntity::class, ThreatLogEntity::class, DailyStatsEntity::class, DomainFilterEntity::class],
    version = 2, // Version bumped due to new table
    exportSchema = false
)
abstract class GorinoxDatabase : RoomDatabase() {

    abstract fun wifiProfileDao(): WiFiProfileDao
    abstract fun threatLogDao(): ThreatLogDao
    abstract fun dailyStatsDao(): DailyStatsDao
    abstract fun domainFilterDao(): DomainFilterDao

    companion object {
        @Volatile
        private var INSTANCE: GorinoxDatabase? = null

        fun getDatabase(context: Context): GorinoxDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GorinoxDatabase::class.java,
                    "gorinox_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

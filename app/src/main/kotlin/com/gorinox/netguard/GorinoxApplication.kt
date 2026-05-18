package com.gorinox.netguard

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class GorinoxApplication : Application() {

    companion object {
        const val CHANNEL_SERVICE = "gorinox_service_channel"
        const val CHANNEL_ALERTS = "gorinox_alerts_channel"
        const val CHANNEL_SUMMARIES = "gorinox_summaries_channel"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java) ?: return

            // 1. Background Protection Channel (Silent/Low priority)
            val serviceChannel = NotificationChannel(
                CHANNEL_SERVICE,
                "Arka Plan Koruması",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Gorinox'un arka planda sessizce çalışmasını sağlar."
                setShowBadge(false)
            }

            // 2. High Priority Security Alerts (Shouts / Rings on Threat!)
            val alertChannel = NotificationChannel(
                CHANNEL_ALERTS,
                "Güvenlik Uyarıları",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Gerçek ve kritik tehditler algılandığında sizi uyarır."
                enableLights(true)
                enableVibration(true)
            }

            // 3. Daily Summary Channel (Default priority)
            val summaryChannel = NotificationChannel(
                CHANNEL_SUMMARIES,
                "Günlük Güvenlik Özetleri",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Günde bir kez gelen koruma özetlerini içerir."
            }

            notificationManager.createNotificationChannel(serviceChannel)
            notificationManager.createNotificationChannel(alertChannel)
            notificationManager.createNotificationChannel(summaryChannel)
        }
    }
}

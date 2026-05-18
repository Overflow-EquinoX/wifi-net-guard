package com.gorinox.netguard.security

import com.gorinox.netguard.data.WiFiProfileEntity
import com.gorinox.netguard.data.WiFiOwnerType

enum class NotificationDecision {
    NEVER,                  // Hiç bildirme, log bile alma
    SILENT_LOG,             // Logla ama bildirme (kullanıcı geçmişte görür)
    NOTIFY_SUMMARY,         // Günlük toplu özete ekle
    NOTIFY_IMMEDIATELY,     // Anında push bildirimi gönder
    CRITICAL_FULL_SCREEN    // Tam ekran kritik alarm göster
}

class NotificationDecisionEngine {

    /**
     * Context-aware decision engine that maps threat severity and WiFi owner types
     * to the quietest appropriate notification action.
     */
    fun shouldNotify(
        threatLevel: Int, // 1: Info, 2: Low, 3: High, 4: Critical
        wifiProfile: WiFiProfileEntity?,
        isWhitelistedDomain: Boolean
    ): NotificationDecision {
        
        // 1. Silent check: Whitelisted domains never cause an alarm
        if (isWhitelistedDomain) return NotificationDecision.NEVER
        if (threatLevel <= 0) return NotificationDecision.NEVER

        val ownerType = wifiProfile?.ownerType ?: WiFiOwnerType.UNKNOWN_PUBLIC

        // 2. Route based on context type
        return when (ownerType) {
            WiFiOwnerType.HOME -> {
                // Home networks: Extremely quiet. Only scream if threat is highly critical
                when (threatLevel) {
                    4 -> NotificationDecision.CRITICAL_FULL_SCREEN
                    3 -> NotificationDecision.NOTIFY_IMMEDIATELY
                    else -> NotificationDecision.SILENT_LOG
                }
            }
            
            WiFiOwnerType.WORK -> {
                // Work networks: Professional silence. Notify immediately only on level 3+
                when (threatLevel) {
                    4 -> NotificationDecision.CRITICAL_FULL_SCREEN
                    3 -> NotificationDecision.NOTIFY_IMMEDIATELY
                    else -> NotificationDecision.SILENT_LOG
                }
            }
            
            WiFiOwnerType.KNOWN_CAFE -> {
                // Known regular Cafes: Slightly higher awareness, alert on level 3+
                when (threatLevel) {
                    4 -> NotificationDecision.CRITICAL_FULL_SCREEN
                    3 -> NotificationDecision.NOTIFY_IMMEDIATELY
                    2 -> NotificationDecision.NOTIFY_SUMMARY
                    else -> NotificationDecision.SILENT_LOG
                }
            }
            
            WiFiOwnerType.UNKNOWN_PUBLIC -> {
                // New/Unknown Open Networks: High vigilance. Notify immediately on level 3+ and summarize level 2
                when (threatLevel) {
                    4 -> NotificationDecision.CRITICAL_FULL_SCREEN
                    3 -> NotificationDecision.NOTIFY_IMMEDIATELY
                    2 -> NotificationDecision.NOTIFY_SUMMARY
                    else -> NotificationDecision.SILENT_LOG
                }
            }
            
            WiFiOwnerType.SUSPICIOUS -> {
                // Network already flagged as risky: Alert on almost anything (level 2+)
                when (threatLevel) {
                    4 -> NotificationDecision.CRITICAL_FULL_SCREEN
                    3, 2 -> NotificationDecision.NOTIFY_IMMEDIATELY
                    else -> NotificationDecision.SILENT_LOG
                }
            }
        }
    }
}

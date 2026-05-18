package com.gorinox.netguard.security

import android.content.Context
import com.gorinox.netguard.data.GorinoxDatabase
import com.gorinox.netguard.data.WiFiProfileEntity
import com.gorinox.netguard.data.WiFiOwnerType

enum class SecurityAction {
    NONE,
    MONITOR,
    WARN_USER,
    DISCONNECT_AND_WARN
}

data class FakeWiFiResult(
    val isFake: Boolean,
    val confidence: Float, // 0.0 to 1.0
    val reason: String? = null,
    val action: SecurityAction = SecurityAction.NONE
)

class FakeWiFiDetector(
    private val context: Context,
    private val database: GorinoxDatabase
) {

    /**
     * Analyzes current WiFi connection parameters against stored baselines.
     * Evaluates security downgrade attacks, MAC address mismatches, and vendor differences.
     */
    suspend fun analyzeConnection(
        ssid: String,
        bssid: String,
        isSecured: Boolean
    ): FakeWiFiResult {
        val cleanSsid = ssid.replace("\"", "").trim()
        val cleanBssid = bssid.lowercase().trim()

        // 1. Fetch any stored profiles with the exact same SSID
        val knownProfiles = database.wifiProfileDao().getProfilesBySsid(cleanSsid)
        if (knownProfiles.isEmpty()) {
            // Never connected to this network name before, no threat can be calculated yet.
            return FakeWiFiResult(isFake = false, confidence = 0f, reason = "YENİ_AĞ")
        }

        // 2. Exact match check
        val exactProfile = knownProfiles.find { it.bssid == cleanBssid }
        if (exactProfile != null) {
            // Already connected to this exact physical router before
            if (exactProfile.isVerified && exactProfile.trustScore > 0.8f) {
                return FakeWiFiResult(isFake = false, confidence = 0f, reason = "GÜVENLİ_BİLİNEN_AĞ")
            }
            return FakeWiFiResult(isFake = false, confidence = 0.1f, reason = "BİLİNEN_FİZİKSEL_AĞ")
        }

        // 3. Potential Threat: Same name (SSID), but different hardware address (BSSID)!
        var suspicionScore = 0.3f // Base level for matching SSID on a different router
        val reasons = mutableListOf<String>()

        // Indicator 1: Subtly different spelling (Evil Twin typosquatting, e.g. "Starbucks_WiFi" vs "Starbucks  WiFi")
        // handled at network scanning or direct similarity analysis
        
        // Indicator 2: Encryption Downgrade Attack
        // If the real network was password protected, but this one is OPEN
        val wasSecuredBefore = knownProfiles.any { it.wasSecured }
        if (wasSecuredBefore && !isSecured) {
            suspicionScore += 0.4f
            reasons.add("ŞİFRESİZ_BAĞLANTI_SALDIRISI (Daha önce şifreliydi)")
        }

        // Indicator 3: Manufacturer (OUI) mismatch
        // If the authentic router was enterprise-grade (Cisco, Aruba), but this new BSSID is consumer (TP-Link)
        val firstKnownVendor = knownProfiles.firstOrNull()?.vendorName ?: MacVendorLookup.getVendor(knownProfiles.first().bssid)
        val currentVendor = MacVendorLookup.getVendor(cleanBssid)
        if (firstKnownVendor != "Bilinmeyen Üretici" && currentVendor != "Bilinmeyen Üretici") {
            if (isEnterpriseVendor(firstKnownVendor) && !isEnterpriseVendor(currentVendor)) {
                suspicionScore += 0.3f
                reasons.add("DONANIM_UYUMSUZLUĞU (Kurumsal router yerine ev router'ı: $currentVendor)")
            }
        }

        // Determine final decision based on suspicionScore
        return when {
            suspicionScore >= 0.7f -> FakeWiFiResult(
                isFake = true,
                confidence = suspicionScore.coerceIn(0f, 1f),
                reason = "EVIL_TWIN_SALDIRISI: " + reasons.joinToString(", "),
                action = SecurityAction.DISCONNECT_AND_WARN
            )
            suspicionScore >= 0.5f -> FakeWiFiResult(
                isFake = true,
                confidence = suspicionScore.coerceIn(0f, 1f),
                reason = "ŞÜPHELİ_AĞ_UĞRAĞI: " + reasons.joinToString(", "),
                action = SecurityAction.WARN_USER
            )
            else -> FakeWiFiResult(
                isFake = false,
                confidence = suspicionScore.coerceIn(0f, 1f),
                reason = "FARKLI_KONUM_VEYA_ŞUBE (Yeni fiziksel erişim noktası)",
                action = SecurityAction.MONITOR
            )
        }
    }

    private fun isEnterpriseVendor(vendor: String): Boolean {
        val lower = vendor.lowercase()
        return lower.contains("cisco") || lower.contains("aruba") || lower.contains("ruckus") || lower.contains("huawei")
    }
}

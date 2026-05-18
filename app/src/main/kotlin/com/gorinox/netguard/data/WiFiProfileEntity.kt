package com.gorinox.netguard.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class WiFiOwnerType {
    HOME,           // Ev WiFi'si
    WORK,           // İş yeri WiFi'si
    KNOWN_CAFE,     // Düzenli gidilen kafe
    KNOWN_PUBLIC,   // Bilinen ortak ağlar (hava limanı, otel vb)
    UNKNOWN_PUBLIC, // Yeni/Bilinmeyen ortak ağlar
    SUSPICIOUS      // Manuel veya otomatik şüpheli işaretlenmiş ağlar
}

@Entity(tableName = "wifi_profiles")
data class WiFiProfileEntity(
    @PrimaryKey
    val bssid: String,                 // MAC adresi (Eşsiz tanımlayıcı)
    val ssid: String,                  // WiFi adı
    val firstSeenAt: Long,             // İlk bağlantı zaman damgası
    val lastSeenAt: Long,              // Son bağlantı zaman damgası
    val totalSessions: Int = 1,        // Toplam bağlantı sayısı
    val totalTimeMinutes: Long = 0,    // Toplam geçirilen süre (dakika)
    
    // Davranış ve Karakter Fingerprint Verileri
    val avgDnsQueriesPerMin: Float = 0f,
    val avgUniqueIpsPerSession: Float = 0f,
    val avgPacketSize: Int = 0,
    val typicalDomainsJson: String = "[]", // En çok ziyaret edilen domainlerin JSON listesi
    
    // Güvenilirlik Dereceleri
    val trustScore: Float = 0.5f,      // 0.0 - 1.0 arası güven puanı
    val isVerified: Boolean = false,   // Kullanıcı doğrulamış mı?
    val ownerType: WiFiOwnerType = WiFiOwnerType.UNKNOWN_PUBLIC,
    
    // Geçmiş İnceleme Raporları
    val threatsDetected: Int = 0,
    val falseAlerts: Int = 0,
    val wasSecured: Boolean = true,    // WPA/WPA2/WPA3 vb. korumalı mı?
    val vendorName: String? = null,    // MAC adresi üreticisi (Cisco, TP-Link vb)
    val approximateLocation: String? = null
)

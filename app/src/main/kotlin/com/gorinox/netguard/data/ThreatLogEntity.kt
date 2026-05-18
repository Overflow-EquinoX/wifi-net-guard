package com.gorinox.netguard.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "threat_logs")
data class ThreatLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val bssid: String,
    val ssid: String,
    val threatType: String, // EVIL_TWIN, SSL_STRIP, DNS_HIJACK, PORT_SCAN, MITM
    val level: Int,         // 1: Bilgi, 2: Düşük, 3: Yüksek, 4: Kritik Tehlike
    val description: String,
    val resolved: Boolean = false
)

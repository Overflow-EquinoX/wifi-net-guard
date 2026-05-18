package com.gorinox.netguard.security

object MacVendorLookup {

    // Simple offline database of common Network Equipment Manufacturers by MAC OUI prefixes
    private val VENDOR_PREFIXES = mapOf(
        "00:00:0c" to "Cisco Systems",
        "00:03:0f" to "Cisco Systems",
        "00:1d:70" to "Cisco Systems",
        "00:24:c4" to "Cisco Systems",
        "00:0b:86" to "Aruba Networks",
        "00:1a:1e" to "Aruba Networks",
        "00:24:6c" to "Aruba Networks",
        "00:1b:2f" to "Ruckus Wireless",
        "00:22:7f" to "Ruckus Wireless",
        "00:18:82" to "Huawei Technologies",
        "00:e0:fc" to "Huawei Technologies",
        
        // Consumer Vendors
        "00:14:d1" to "Linksys",
        "00:18:f8" to "Linksys",
        "00:19:e0" to "TP-Link",
        "ec:08:6b" to "TP-Link",
        "50:c7:bf" to "TP-Link",
        "00:0f:b5" to "Netgear",
        "00:14:6c" to "Netgear",
        "00:18:4d" to "Netgear",
        "00:15:af" to "D-Link",
        "00:1b:11" to "D-Link",
        "00:1e:58" to "D-Link",
        "c8:3a:35" to "Tenda",
        "50:2b:73" to "Xiaomi",
        "04:cf:8c" to "Xiaomi"
    )

    fun getVendor(bssid: String): String {
        val cleanBssid = bssid.lowercase().replace("-", ":").trim()
        if (cleanBssid.length < 8) return "Bilinmeyen Üretici"
        
        val prefix = cleanBssid.substring(0, 8) // Get "aa:bb:cc" part
        return VENDOR_PREFIXES[prefix] ?: "Generic / Consumer Router"
    }
}

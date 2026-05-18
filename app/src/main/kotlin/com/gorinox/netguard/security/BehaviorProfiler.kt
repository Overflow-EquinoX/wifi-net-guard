package com.gorinox.netguard.security

enum class Severity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

data class AnomalyResult(
    val isAnomalous: Boolean,
    val threatType: String? = null,
    val severity: Severity = Severity.LOW,
    val description: String? = null
)

class BehaviorProfiler {

    /**
     * Analyzes session traffic characteristics.
     * Generates anomaly alerts in case of high DNS rates or HTTPS decryption failures.
     */
    fun checkAnomaly(
        currentDnsQueries: Int,
        httpsFailureCount: Int,
        totalHttps: Int
    ): AnomalyResult {
        val httpsSuccessCount = totalHttps - httpsFailureCount
        val failureRate = if (totalHttps > 0) httpsFailureCount.toFloat() / totalHttps else 0f

        return when {
            // High failure rates in HTTPS handshakes indicate potential SSL Decryption / SSL Strip proxy injection
            totalHttps >= 5 && failureRate > 0.4f -> {
                AnomalyResult(
                    isAnomalous = true,
                    threatType = "SSL_STRIP_OR_MITM",
                    severity = Severity.CRITICAL,
                    description = "HTTPS bağlantılarında yüksek hata oranı (%${(failureRate * 100).toInt()}). Ağda SSL Strip veya Ortadaki Adam (MITM) saldırısı olabilir."
                )
            }
            
            // Extreme spikes in DNS Queries within a short window suggest port scans, DDoS, or DNS tunnels
            currentDnsQueries > 150 -> {
                AnomalyResult(
                    isAnomalous = true,
                    threatType = "PORT_SCAN_OR_DDOS",
                    severity = Severity.HIGH,
                    description = "Kısa sürede aşırı yüksek DNS sorgusu ($currentDnsQueries adet/dk). Ağda tarama veya saldırı aktivitesi tespit edildi."
                )
            }
            
            else -> {
                AnomalyResult(
                    isAnomalous = false,
                    threatType = null,
                    severity = Severity.LOW,
                    description = "Normal trafik davranışı."
                )
            }
        }
    }
}

package com.gorinox.netguard.security

import android.net.LinkProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * Active probes to detect Man-in-the-Middle (MITM), DNS Hijacking, and Transparent Proxies.
 * These checks actively send small requests to verify network integrity upon connection.
 */
class ActiveThreatDetector {

    data class ActiveScanResult(
        val isThreatDetected: Boolean,
        val threatType: String? = null,
        val severity: Severity = Severity.LOW,
        val description: String? = null
    )

    /**
     * Runs all active network probes.
     */
    suspend fun performActiveScans(linkProperties: LinkProperties?): List<ActiveScanResult> {
        val results = mutableListOf<ActiveScanResult>()

        withContext(Dispatchers.IO) {
            // 1. Rogue DNS Server Check
            val dnsResult = checkRogueDnsServers(linkProperties)
            if (dnsResult != null) results.add(dnsResult)

            // 2. Transparent Proxy / Interception Check
            val proxyResult = checkTransparentProxy()
            if (proxyResult != null) results.add(proxyResult)

            // 3. DNS Hijacking Check
            val hijackingResult = checkDnsHijacking()
            if (hijackingResult != null) results.add(hijackingResult)
        }

        return results
    }

    /**
     * Checks if the DHCP provided DNS servers are suspicious.
     * (e.g., neither the gateway itself nor a well-known public DNS).
     */
    private fun checkRogueDnsServers(linkProperties: LinkProperties?): ActiveScanResult? {
        if (linkProperties == null) return null
        
        val dnsServers = linkProperties.dnsServers.map { it.hostAddress }
        if (dnsServers.isEmpty()) return null

        // Well-known safe public DNS servers
        val safeDns = listOf(
            "8.8.8.8", "8.8.4.4",       // Google
            "1.1.1.1", "1.0.0.1",       // Cloudflare
            "208.67.222.222", "208.67.220.220", // OpenDNS
            "9.9.9.9", "149.112.112.112" // Quad9
        )

        // Often, the DNS is just the local gateway (e.g., 192.168.1.1)
        val isLocalDns = dnsServers.any { it?.startsWith("192.168.") == true || it?.startsWith("10.") == true || it?.startsWith("172.") == true }
        val isSafePublicDns = dnsServers.any { safeDns.contains(it) }

        // If it's not local and not a known safe public DNS, it could be a rogue DNS server
        // This is a heuristic, so we flag it as LOW/MEDIUM severity to just monitor it.
        if (!isLocalDns && !isSafePublicDns) {
            return ActiveScanResult(
                isThreatDetected = true,
                threatType = "SUSPICIOUS_DNS",
                severity = Severity.MEDIUM,
                description = "Ağ, bilinmeyen veya şüpheli bir DNS sunucusu kullanıyor: ${dnsServers.firstOrNull()}. İnternet trafiğiniz izleniyor olabilir."
            )
        }
        return null
    }

    /**
     * Attempts to reach a known "204 No Content" generator.
     * If an attacker or Captive Portal intercepts HTTP traffic, they usually return a 200 OK with HTML.
     */
    private fun checkTransparentProxy(): ActiveScanResult? {
        return try {
            val url = URL("http://clients3.google.com/generate_204")
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 3000
            connection.readTimeout = 3000
            connection.instanceFollowRedirects = false // Don't follow redirects automatically

            val responseCode = connection.responseCode
            connection.disconnect()

            // 204 means no interception.
            // 200/302 means interception (Captive Portal or Transparent Proxy).
            if (responseCode != 204) {
                ActiveScanResult(
                    isThreatDetected = true,
                    threatType = "TRAFFIC_INTERCEPTION",
                    severity = Severity.HIGH,
                    description = "HTTP trafiğine müdahale ediliyor. Ağda bir kısıtlama (Captive Portal) veya şüpheli bir aracı (Proxy) bulunuyor."
                )
            } else {
                null
            }
        } catch (e: Exception) {
            // Network error, ignore
            null
        }
    }

    /**
     * Resolves highly secure domains to see if they point to local or private IPs.
     * Attackers use DNS spoofing to redirect users to fake login pages.
     */
    private fun checkDnsHijacking(): ActiveScanResult? {
        val testDomains = listOf("accounts.google.com", "apple.com", "microsoft.com")
        
        for (domain in testDomains) {
            try {
                val address = InetAddress.getByName(domain)
                val ip = address.hostAddress ?: continue
                
                // Real Google/Apple servers will never resolve to a private local IP.
                if (isPrivateIp(ip)) {
                    return ActiveScanResult(
                        isThreatDetected = true,
                        threatType = "DNS_HIJACKING",
                        severity = Severity.CRITICAL,
                        description = "DNS Sahtekarlığı (Spoofing) algılandı! '$domain' adresi sahte bir lokal IP'ye ($ip) yönlendiriliyor. Bilgileriniz çalınabilir!"
                    )
                }
            } catch (e: Exception) {
                // Resolution failed
            }
        }
        return null
    }

    private fun isPrivateIp(ip: String): Boolean {
        return ip.startsWith("192.168.") || 
               ip.startsWith("10.") || 
               ip.matches(Regex("^172\\.(1[6-9]|2[0-9]|3[0-1])\\..+")) ||
               ip == "127.0.0.1"
    }
}

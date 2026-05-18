package com.gorinox.netguard.security

object NormalTrafficFilter {

    // Whitelist domains for which we NEVER want to prompt or log deeply.
    private val ALWAYS_SILENT = setOf(
        // Google & Android core
        "google.com", "googleapis.com", "gstatic.com", "googleusercontent.com", 
        "youtube.com", "ytimg.com", "ggpht.com", "android.com",
        
        // Apple Services
        "apple.com", "icloud.com", "mzstatic.com",
        
        // Microsoft / Windows
        "microsoft.com", "live.com", "office.com", "windows.com", "bing.com",
        
        // Meta Services
        "facebook.com", "fbcdn.net", "instagram.com", "whatsapp.net", "whatsapp.com",
        
        // CDNs & Cloud Providers
        "cloudflare.com", "akamai.net", "cloudfront.net", "fastly.net", "jsdelivr.net",
        "amazonaws.com", "azure.com", "azureedge.net",
        
        // Turkish popular services & government
        "gov.tr", "edu.tr", "com.tr",
        "trendyol.com", "hepsiburada.com", "n11.com", "yemeksepeti.com", "getir.com",
        
        // Banks
        "akbank.com", "garanti.com.tr", "isbankasi.com.tr", "yapikredi.com.tr", 
        "ziraatbank.com.tr", "halkbank.com.tr", "vakifbank.com.tr",
        
        // Entertainment/Streaming
        "netflix.com", "spotify.com", "disneyplus.com"
    )

    /**
     * Checks if a domain/host is on the whitelist.
     * Handles wildcard subdomains (e.g., "sub.google.com" matches "google.com").
     */
    fun isWhitelisted(domain: String): Boolean {
        val cleanDomain = domain.lowercase().trim()
        
        // Direct match check
        if (ALWAYS_SILENT.contains(cleanDomain)) return true

        // Subdomain matching (e.g. mail.google.com should match google.com)
        return ALWAYS_SILENT.any { whitelisted ->
            cleanDomain.endsWith(".$whitelisted")
        }
    }
}

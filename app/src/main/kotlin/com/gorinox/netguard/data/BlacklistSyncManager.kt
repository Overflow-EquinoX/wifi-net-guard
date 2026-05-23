package com.gorinox.netguard.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class BlacklistSyncManager(private val database: GorinoxDatabase) {

    suspend fun syncBlacklist() {
        withContext(Dispatchers.IO) {
            try {
                Log.d("BlacklistSync", "Güncel kara liste indiriliyor...")
                
                // 1. URL'yi Firebase Remote Config'den al (Şifreli ve dışarıdan değiştirilebilir)
                val targetUrl = com.gorinox.netguard.utils.RemoteConfigManager.getBlacklistUrl()
                val rawData = URL(targetUrl).readText()
                
                // 2. Metni satır satır oku ve domainleri çıkar
                // StevenBlack formatı: "0.0.0.0 zararlisite.com"
                val domains = rawData.lines()
                    .asSequence()
                    .filter { it.startsWith("0.0.0.0") }
                    .map { it.removePrefix("0.0.0.0").trim() }
                    .filter { it.isNotEmpty() && it != "0.0.0.0" }
                    .map { 
                        DomainFilterEntity(
                            domain = it,
                            isWhitelist = false,
                            addedAt = System.currentTimeMillis()
                        )
                    }
                    .toList()

                Log.d("BlacklistSync", "${domains.size} adet tehdit bulundu. Veritabanı güncelleniyor...")

                // 3. Eski listeyi temizle ve yenisini yaz (Toplu işlem)
                database.domainFilterDao().clearAllFilters()
                
                // Chunk (parça parça) eklemek Room için daha sağlıklıdır
                domains.chunked(5000).forEach { chunk ->
                    database.domainFilterDao().insertFilters(chunk)
                }

                Log.d("BlacklistSync", "Kara liste başarıyla güncellendi!")

            } catch (e: Exception) {
                Log.e("BlacklistSync", "Kara liste güncellenirken hata oluştu: ${e.message}")
                // Hata olursa sistem eski veritabanındaki (dün indirilmiş) liste ile çalışmaya devam eder.
            }
        }
    }
}

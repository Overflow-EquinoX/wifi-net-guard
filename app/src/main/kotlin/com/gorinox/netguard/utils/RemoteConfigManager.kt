package com.gorinox.netguard.utils

import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.tasks.await

object RemoteConfigManager {
    private val remoteConfig = FirebaseRemoteConfig.getInstance()

    init {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            // Canlı ortamda bu süreyi 1 saat (3600 sn) yaparız.
            // Geliştirme aşamasında değişiklikleri anında görmek için 0 yapıyoruz.
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        
        // Varsayılan (Fallback) değerler
        // Uygulama internete veya Firebase'e bağlanamazsa bile çökmeyecek, bu test değerleriyle çalışacak.
        remoteConfig.setDefaultsAsync(mapOf(
            "blacklist_url" to "https://raw.githubusercontent.com/StevenBlack/hosts/master/alternates/fakenews/hosts",
            "ad_interstitial_id" to "ca-app-pub-3940256099942544/1033173712",
            "ad_rewarded_id" to "ca-app-pub-3940256099942544/5224354917"
        ))
    }

    suspend fun fetchAndActivate() {
        try {
            remoteConfig.fetchAndActivate().await()
            Log.d("RemoteConfig", "Güvenlik Duvarı ayarları Firebase'den başarıyla çekildi.")
        } catch (e: Exception) {
            Log.e("RemoteConfig", "Firebase'e bağlanılamadı, varsayılan (Lokal) değerler kullanılıyor.")
        }
    }

    fun getBlacklistUrl(): String = remoteConfig.getString("blacklist_url")
    fun getInterstitialAdId(): String = remoteConfig.getString("ad_interstitial_id")
    fun getRewardedAdId(): String = remoteConfig.getString("ad_rewarded_id")
}

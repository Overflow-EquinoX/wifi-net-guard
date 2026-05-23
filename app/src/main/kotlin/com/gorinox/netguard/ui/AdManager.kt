package com.gorinox.netguard.ui

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdManager(private val context: Context) {
    private var mInterstitialAd: InterstitialAd? = null
    private var mRewardedAd: RewardedAd? = null

    fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        val adUnitId = com.gorinox.netguard.utils.RemoteConfigManager.getInterstitialAdId()
        
        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e("AdManager", "Geçiş reklamı yüklenemedi: ${adError.message}")
                mInterstitialAd = null
            }
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d("AdManager", "Geçiş reklamı başarıyla yüklendi.")
                mInterstitialAd = interstitialAd
            }
        })
    }

    fun showInterstitialAd(activity: Activity, onAdDismissed: () -> Unit) {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    mInterstitialAd = null
                    loadInterstitialAd() // Bir sonrakini şimdiden arka planda yükle
                    onAdDismissed()
                }
                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    onAdDismissed()
                }
            }
            mInterstitialAd?.show(activity)
        } else {
            // Reklam yoksa kullanıcıyı bekletmeden işlemi yap
            onAdDismissed()
        }
    }

    fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()
        val adUnitId = com.gorinox.netguard.utils.RemoteConfigManager.getRewardedAdId()
        
        RewardedAd.load(context, adUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e("AdManager", "Ödüllü reklam yüklenemedi: ${adError.message}")
                mRewardedAd = null
            }
            override fun onAdLoaded(rewardedAd: RewardedAd) {
                Log.d("AdManager", "Ödüllü reklam başarıyla yüklendi.")
                mRewardedAd = rewardedAd
            }
        })
    }

    fun showRewardedAd(activity: Activity, onRewardEarned: () -> Unit, onAdNotReady: () -> Unit) {
        if (mRewardedAd != null) {
            mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    mRewardedAd = null
                    loadRewardedAd() // Bir sonrakini yükle
                }
            }
            mRewardedAd?.show(activity) { rewardItem ->
                // Kullanıcı reklamı başarıyla sonuna kadar izledi
                Log.d("AdManager", "Kullanıcı ödülü kazandı: ${rewardItem.amount} ${rewardItem.type}")
                onRewardEarned()
            }
        } else {
            // Reklam henüz yüklenmemişse veya internet yoksa
            Log.d("AdManager", "Ödüllü reklam henüz hazır değil.")
            onAdNotReady()
        }
    }
}

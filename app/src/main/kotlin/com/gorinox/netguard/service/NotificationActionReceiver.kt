package com.gorinox.netguard.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import com.gorinox.netguard.security.KillSwitchVpnService

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        when (action) {
            "ACTION_ENGAGE_KILL_SWITCH" -> {
                // Tier 2: Kullanıcı manuel "İnterneti Kes" dedi. VPN Kill-Switch'i başlat.
                val vpnIntent = Intent(context, KillSwitchVpnService::class.java)
                context.startService(vpnIntent)
                Toast.makeText(context, "Gorinox: İnternet trafiği boğuldu. Güvendesiniz.", Toast.LENGTH_LONG).show()
                
                // Ardından yine de ağı unutması için ayarlara yönlendir
                openWifiSettings(context)
            }
            "ACTION_OPEN_WIFI_SETTINGS" -> {
                // Tier 1: Zaten internet kilitli. Ağı fiziksel olarak unutması için ayarlara yönlendir.
                Toast.makeText(context, "Lütfen güvensiz WiFi ağına basılı tutarak 'Ağı Unut' seçeneğini seçin.", Toast.LENGTH_LONG).show()
                openWifiSettings(context)
            }
        }
    }

    private fun openWifiSettings(context: Context) {
        val wifiIntent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        context.startActivity(wifiIntent)
    }
}

package com.gorinox.netguard.security

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log

/**
 * A local VPN Service that acts as a "Traffic Blackhole" (Kill-Switch).
 * When active, it routes all device traffic to a dummy local interface, 
 * effectively preventing any data from leaving the device over the compromised WiFi.
 */
class KillSwitchVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        
        if (action == "STOP_KILL_SWITCH") {
            stopVpn()
            stopSelf()
            return START_NOT_STICKY
        }

        startVpn()
        return START_STICKY
    }

    private fun startVpn() {
        if (vpnInterface != null) return // Zaten çalışıyor

        try {
            val builder = Builder()
                .setSession("Gorinox Kill-Switch")
                .addAddress("10.0.0.2", 32)
                .addRoute("0.0.0.0", 0) // Tüm IPv4 trafiğini yakala
                .addRoute("::", 0)      // Tüm IPv6 trafiğini yakala
                .setBlocking(true)

            // MTU set to standard 1500
            builder.setMtu(1500)

            vpnInterface = builder.establish()
            Log.d("KillSwitch", "Kill-Switch VPN aktif. İnternet boğuldu.")
        } catch (e: Exception) {
            Log.e("KillSwitch", "VPN başlatılamadı: ${e.message}")
        }
    }

    private fun stopVpn() {
        try {
            vpnInterface?.close()
            vpnInterface = null
            Log.d("KillSwitch", "Kill-Switch VPN durduruldu. İnternet normale döndü.")
        } catch (e: Exception) {
            Log.e("KillSwitch", "VPN durdurulurken hata: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVpn()
    }
}

package com.gorinox.netguard.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.gorinox.netguard.GorinoxApplication
import com.gorinox.netguard.R
import com.gorinox.netguard.data.*
import com.gorinox.netguard.security.*
import com.gorinox.netguard.ui.MainActivity
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class WifiGuardService : Service() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Default + serviceJob)

    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var wifiManager: WifiManager
    private lateinit var database: GorinoxDatabase
    private lateinit var fakeWiFiDetector: FakeWiFiDetector
    private val behaviorProfiler = BehaviorProfiler()
    private val notificationDecisionEngine = NotificationDecisionEngine()
    private val activeThreatDetector = ActiveThreatDetector()

    private var currentSsid: String? = null
    private var currentBssid: String? = null

    // For mocking session packet rates (Layer 4 Simulation)
    private var dnsQueryCount = 0
    private var httpsFailureCount = 0
    private var totalHttpsCount = 0
    private var simulationJob: Job? = null

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            checkCurrentNetwork()
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            handleNetworkLost()
        }
    }

    override fun onCreate() {
        super.onCreate()
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        database = GorinoxDatabase.getDatabase(applicationContext)
        fakeWiFiDetector = FakeWiFiDetector(applicationContext, database)

        registerNetworkCallback()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1001, createServiceNotification())
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        unregisterNetworkCallback()
        serviceJob.cancel()
    }

    private fun registerNetworkCallback() {
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    private fun unregisterNetworkCallback() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            // Callback might not be registered
        }
    }

    /**
     * Checks the parameters of the active network connection.
     * Starts monitoring, triggers Layer 3 checks, and builds Layer 2 baseline models.
     */
    private fun checkCurrentNetwork() {
        serviceScope.launch {
            val wifiInfo: WifiInfo? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // On Q+, get wifi info via network capabilities/transport info or directly
                wifiManager.connectionInfo
            } else {
                wifiManager.connectionInfo
            }

            val bssid = wifiInfo?.bssid?.lowercase()
            val ssid = wifiInfo?.ssid?.replace("\"", "")

            if (bssid != null && bssid != "02:00:00:00:00:00" && !ssid.isNullOrEmpty() && ssid != "<unknown ssid>") {
                if (currentBssid != bssid) {
                    currentBssid = bssid
                    currentSsid = ssid

                    // Layer 2: Register WiFi connection in database (Baseline Learning)
                    val existingProfile = database.wifiProfileDao().getProfileByBssid(bssid)
                    val isSecured = wifiInfo.networkId != -1 // Basic heuristic check for secured WPA networks
                    
                    val now = System.currentTimeMillis()
                    val profile = if (existingProfile == null) {
                        WiFiProfileEntity(
                            bssid = bssid,
                            ssid = ssid,
                            firstSeenAt = now,
                            lastSeenAt = now,
                            wasSecured = isSecured,
                            vendorName = MacVendorLookup.getVendor(bssid)
                        )
                    } else {
                        existingProfile.copy(
                            lastSeenAt = now,
                            totalSessions = existingProfile.totalSessions + 1,
                            wasSecured = isSecured
                        )
                    }
                    database.wifiProfileDao().insertOrUpdateProfile(profile)

                    // Layer 3: Run Evil Twin Detection
                    val detectionResult = fakeWiFiDetector.analyzeConnection(ssid, bssid, isSecured)
                    if (detectionResult.isFake) {
                        handleThreatDetected(
                            threatType = "EVIL_TWIN",
                            level = 4,
                            description = detectionResult.reason ?: "Sahte WiFi / Evil Twin bağlantısı tespit edildi!",
                            profile = profile
                        )
                    } else {
                        // Run Active Network Probes (DNS Hijack, Transparent Proxy, Rogue DNS)
                        val activeNetwork = connectivityManager.activeNetwork
                        val linkProps = connectivityManager.getLinkProperties(activeNetwork)
                        val activeThreats = activeThreatDetector.performActiveScans(linkProps)
                        
                        var threatFound = false
                        for (threat in activeThreats) {
                            if (threat.isThreatDetected) {
                                threatFound = true
                                handleThreatDetected(
                                    threatType = threat.threatType ?: "NETWORK_INTERCEPTION",
                                    level = if (threat.severity == Severity.CRITICAL) 4 else if (threat.severity == Severity.HIGH) 3 else 2,
                                    description = threat.description ?: "Aktif ağ taraması sırasında şüpheli bir durum tespit edildi.",
                                    profile = profile
                                )
                            }
                        }

                        if (!threatFound) {
                            // Start normal behavior tracking simulation (Layer 4)
                            startBehaviorSimulation(profile)
                        }
                    }
                }
            }
        }
    }

    private fun handleNetworkLost() {
        currentBssid = null
        currentSsid = null
        stopBehaviorSimulation()
    }

    /**
     * Simulation of network packets to demonstrate Layer 4 & Layer 5 in action.
     * Generates a random DNS peak or SSL failure rate every 15 seconds to demonstrate safety vigilance.
     */
    private fun startBehaviorSimulation(profile: WiFiProfileEntity) {
        stopBehaviorSimulation()
        dnsQueryCount = 0
        httpsFailureCount = 0
        totalHttpsCount = 0

        simulationJob = serviceScope.launch {
            // Increment statistics duration every minute
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            while (isActive) {
                delay(15000) // Run simulation cycle every 15 seconds

                // 1. Simulate packet counter increments in Database Daily stats
                var stats = database.dailyStatsDao().getStatsForDate(today)
                if (stats == null) {
                    stats = DailyStatsEntity(date = today, protectionDurationMinutes = 1)
                } else {
                    stats = stats.copy(
                        protectionDurationMinutes = stats.protectionDurationMinutes + 1,
                        blockedAdsCount = stats.blockedAdsCount + (5..15).random(),
                        blockedTrackersCount = stats.blockedTrackersCount + (2..7).random()
                    )
                }
                database.dailyStatsDao().insertOrUpdateStats(stats)

                // 2. Randomly simulate normal or anomalous activity (10% chance of threat simulation)
                val chance = (1..100).random()
                if (chance > 92) {
                    // Simulate SSL Strip Threat
                    dnsQueryCount = (10..30).random()
                    totalHttpsCount = 20
                    httpsFailureCount = 12 // > 40% failure rate!
                } else {
                    // Normal behavior
                    dnsQueryCount = (10..40).random()
                    totalHttpsCount = 30
                    httpsFailureCount = 0
                }

                // Analyze Layer 4 Behavior Anomaly
                val anomalyResult = behaviorProfiler.checkAnomaly(dnsQueryCount, httpsFailureCount, totalHttpsCount)
                if (anomalyResult.isAnomalous) {
                    handleThreatDetected(
                        threatType = anomalyResult.threatType ?: "SSL_STRIP",
                        level = if (anomalyResult.severity == Severity.CRITICAL) 4 else 3,
                        description = anomalyResult.description ?: "Ağda anormal veri paketleri yakalandı.",
                        profile = profile
                    )
                }
            }
        }
    }

    private fun stopBehaviorSimulation() {
        simulationJob?.cancel()
        simulationJob = null
    }

    /**
     * Executes Layer 5 router and decides whether to notify, log silently, or terminate connection.
     */
    private fun handleThreatDetected(
        threatType: String,
        level: Int,
        description: String,
        profile: WiFiProfileEntity
    ) {
        serviceScope.launch {
            // Save threat to local database logs
            val log = ThreatLogEntity(
                timestamp = System.currentTimeMillis(),
                bssid = profile.bssid,
                ssid = profile.ssid,
                threatType = threatType,
                level = level,
                description = description
            )
            database.threatLogDao().insertLog(log)

            // Increment threat counter in daily statistics
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            var stats = database.dailyStatsDao().getStatsForDate(today)
            if (stats == null) {
                stats = DailyStatsEntity(date = today, threatsBlockedCount = 1)
            } else {
                stats = stats.copy(threatsBlockedCount = stats.threatsBlockedCount + 1)
            }
            database.dailyStatsDao().insertOrUpdateStats(stats)

            // Layer 5: Decide action
            val decision = notificationDecisionEngine.shouldNotify(level, profile, isWhitelistedDomain = false)
            
            if (decision == NotificationDecision.NOTIFY_IMMEDIATELY || decision == NotificationDecision.CRITICAL_FULL_SCREEN) {
                showSecurityAlertNotification(profile.ssid, description)
            }
            
            // Increment threat count in profile record
            database.wifiProfileDao().insertOrUpdateProfile(
                profile.copy(threatsDetected = profile.threatsDetected + 1, trustScore = (profile.trustScore - 0.2f).coerceAtLeast(0f))
            )
        }
    }

    private fun showSecurityAlertNotification(ssid: String, description: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, GorinoxApplication.CHANNEL_ALERTS)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle("🚨 TEHLİKE! Sahte Ağ veya Saldırı Algılandı")
            .setContentText("Ağ: $ssid. Detayları görmek ve önlem almak için dokunun.")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Bağlı olduğunuz '$ssid' ağında kritik güvenlik riski algılandı.\nDetay: $description"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(this).notify(2002, notification)
        } catch (e: SecurityPermissionException) {
            // Handle missing notification permission on Android 13+
        } catch (e: Exception) {
            // General safety catch
        }
    }

    private fun createServiceNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, GorinoxApplication.CHANNEL_SERVICE)
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setContentTitle("Wifi Net Guard Aktif Korumada")
            .setContentText("WiFi ağınız arka planda kesintisiz izleniyor.")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }
}

// Exception class to prevent lint errors during notifications
class SecurityPermissionException : Exception()

# 📱 WiFi Net Guard - Complete Development Specification
## Gemini Code Implementation Guide

**Publisher:** Gorinox  
**App Name:** WiFi Net Guard  
**Platform:** Android (Kotlin + Jetpack Compose)  
**Target:** Google Play Store  
**Version:** 1.0.0  
**Min SDK:** 26 (Android 8.0)  
**Target SDK:** 34 (Android 14)

---

## 🎯 PROJECT MISSION

### What Problem We Solve
Public WiFi networks (cafes, airports, hotels) are dangerous:
- Fake WiFi networks steal passwords
- Man-in-the-middle attacks intercept traffic
- DNS hijacking redirects to phishing sites
- SSL stripping removes encryption
- Malicious networks inject ads/malware

### Our Solution
**WiFi Net Guard** uses Android VPN Service API to analyze network traffic in real-time and detect 14 types of attacks - all **locally on device** without sending user data to external servers.

### Unique Value
"The mobile Wireshark that protects you from evil WiFi - without needing root or sending your data anywhere."

---

## 💰 BUSINESS MODEL

### Monetization Strategy
```
FREE TIER:
├── DNS manipulation detection
├── SSL strip detection  
├── Basic ad blocking
├── Community threat database
├── Local logs (7 days retention)
└── Daily summary notifications

PREMIUM TIER ($4.99/month or $39.99/year):
├── All 14 threat types detection
├── ML-based predictive defense
├── Root super-features (ARP monitoring)
├── Unlimited log retention
├── Cloud backup
├── Detailed PDF reports
├── Priority support
└── Ad-free experience

LIFETIME ($79.99 one-time):
└── All Premium features forever
```

### Revenue Split
- Google Play: 15% commission (subscriptions)
- Net to developer: 85%

### Target KPIs
- Month 1: 5,000 downloads
- Month 3: 50,000 downloads
- Premium conversion: 10-12%
- Month 3 revenue: $2,500-3,000/month

---

## 🏗️ TECHNICAL ARCHITECTURE

### Tech Stack

```
LANGUAGE & FRAMEWORK:
├── Kotlin 2.0+
├── Jetpack Compose (UI)
├── Coroutines + Flow (async)
├── Hilt (dependency injection)
└── Material Design 3

DATA & STORAGE:
├── Room (SQLite abstraction)
├── DataStore (preferences)
├── Proto DataStore (structured data)
└── Encrypted SharedPreferences (sensitive)

NETWORKING:
├── Retrofit2 (API client)
├── OkHttp3 (HTTP client)
├── Moshi (JSON parsing)
└── Coil (image loading)

VPN & PACKET ANALYSIS:
├── VpnService API (Android built-in)
├── Java NIO (packet I/O)
├── ByteBuffer (binary parsing)
└── Custom packet parser (no pcap4j - too heavy)

BACKGROUND WORK:
├── WorkManager (scheduled tasks)
├── ForegroundService (VPN)
└── AlarmManager (watchdog)

SECURITY:
├── Jetpack Security (encryption)
├── Certificate Transparency (CT logs)
├── SafetyNet Attestation
└── ProGuard/R8 (obfuscation)

ML (Phase 2):
├── TensorFlow Lite
└── ONNX Runtime

TESTING:
├── JUnit5 (unit tests)
├── Espresso (UI tests)
├── MockK (mocking)
├── Robolectric (Android testing)
└── Leak Canary (memory leaks)

BUILD & CI/CD:
├── Gradle 8.0+ (Kotlin DSL)
├── GitHub Actions (CI)
├── Firebase App Distribution (beta)
└── Play Console (production)
```

---

## 📂 PROJECT STRUCTURE

```
app/
├── src/
│   ├── main/
│   │   ├── kotlin/com/gorinox/wifinetguard/
│   │   │   ├── GorinoxApp.kt (Application class)
│   │   │   │
│   │   │   ├── ui/ (Jetpack Compose UI)
│   │   │   │   ├── theme/
│   │   │   │   │   ├── Color.kt
│   │   │   │   │   ├── Theme.kt
│   │   │   │   │   ├── Type.kt
│   │   │   │   │   └── Shape.kt
│   │   │   │   ├── screens/
│   │   │   │   │   ├── home/
│   │   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   │   ├── HomeViewModel.kt
│   │   │   │   │   │   └── HomeUiState.kt
│   │   │   │   │   ├── dashboard/
│   │   │   │   │   │   ├── DashboardScreen.kt
│   │   │   │   │   │   ├── DashboardViewModel.kt
│   │   │   │   │   │   └── components/
│   │   │   │   │   ├── threats/
│   │   │   │   │   │   ├── ThreatHistoryScreen.kt
│   │   │   │   │   │   ├── ThreatDetailScreen.kt
│   │   │   │   │   │   └── ThreatViewModel.kt
│   │   │   │   │   ├── settings/
│   │   │   │   │   │   ├── SettingsScreen.kt
│   │   │   │   │   │   └── SettingsViewModel.kt
│   │   │   │   │   ├── onboarding/
│   │   │   │   │   │   ├── OnboardingScreen.kt
│   │   │   │   │   │   └── OnboardingViewModel.kt
│   │   │   │   │   └── premium/
│   │   │   │   │       ├── PremiumScreen.kt
│   │   │   │   │       └── BillingManager.kt
│   │   │   │   ├── components/ (reusable UI)
│   │   │   │   │   ├── ThreatCard.kt
│   │   │   │   │   ├── SecurityStatusIndicator.kt
│   │   │   │   │   ├── NetworkCard.kt
│   │   │   │   │   └── StatCard.kt
│   │   │   │   └── navigation/
│   │   │   │       └── NavGraph.kt
│   │   │   │
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   │   ├── database/
│   │   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   │   ├── dao/
│   │   │   │   │   │   │   ├── ThreatLogDao.kt
│   │   │   │   │   │   │   ├── WiFiProfileDao.kt
│   │   │   │   │   │   │   └── CommunityThreatDao.kt
│   │   │   │   │   │   └── entities/
│   │   │   │   │   │       ├── ThreatLog.kt
│   │   │   │   │   │       ├── WiFiProfile.kt
│   │   │   │   │   │       └── CommunityThreat.kt
│   │   │   │   │   ├── preferences/
│   │   │   │   │   │   ├── UserPreferences.kt
│   │   │   │   │   │   └── PreferencesManager.kt
│   │   │   │   │   └── assets/
│   │   │   │   │       ├── oui_database.json (MAC vendor)
│   │   │   │   │       ├── hsts_preload.json
│   │   │   │   │       ├── known_certificates.json
│   │   │   │   │       └── cafe_profiles.json
│   │   │   │   ├── remote/
│   │   │   │   │   ├── api/
│   │   │   │   │   │   ├── ApiService.kt
│   │   │   │   │   │   └── ApiClient.kt
│   │   │   │   │   └── dto/ (data transfer objects)
│   │   │   │   └── repository/
│   │   │   │       ├── ThreatRepository.kt
│   │   │   │       ├── WiFiRepository.kt
│   │   │   │       └── CommunityRepository.kt
│   │   │   │
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   │   ├── Threat.kt
│   │   │   │   │   ├── WiFiNetwork.kt
│   │   │   │   │   ├── ThreatLevel.kt
│   │   │   │   │   └── NetworkPacket.kt
│   │   │   │   └── usecase/
│   │   │   │       ├── AnalyzeWiFiUseCase.kt
│   │   │   │       ├── DetectThreatsUseCase.kt
│   │   │   │       └── LogThreatUseCase.kt
│   │   │   │
│   │   │   ├── vpn/
│   │   │   │   ├── GorinoxVpnService.kt (main VPN service)
│   │   │   │   ├── PacketCaptureThread.kt
│   │   │   │   ├── PacketParser.kt
│   │   │   │   ├── PacketRouter.kt
│   │   │   │   └── VpnManager.kt
│   │   │   │
│   │   │   ├── detection/ (attack detection engines)
│   │   │   │   ├── MasterDetectionEngine.kt
│   │   │   │   ├── detectors/
│   │   │   │   │   ├── DnsManipulationDetector.kt
│   │   │   │   │   ├── SslStripDetector.kt
│   │   │   │   │   ├── ArpPoisoningDetector.kt
│   │   │   │   │   ├── AdInjectionDetector.kt
│   │   │   │   │   ├── CryptoMiningDetector.kt
│   │   │   │   │   ├── MitmDetector.kt
│   │   │   │   │   ├── PortScanDetector.kt
│   │   │   │   │   ├── DataExfiltrationDetector.kt
│   │   │   │   │   ├── PhishingDetector.kt
│   │   │   │   │   ├── FakeWiFiDetector.kt
│   │   │   │   │   ├── BeaconAnalyzer.kt
│   │   │   │   │   └── TrafficFingerprinter.kt
│   │   │   │   ├── analyzers/
│   │   │   │   │   ├── RouterVendorAnalyzer.kt
│   │   │   │   │   ├── SecurityAnalyzer.kt
│   │   │   │   │   ├── CaptivePortalDetector.kt
│   │   │   │   │   ├── DnsServerAnalyzer.kt
│   │   │   │   │   ├── SignalStrengthAnalyzer.kt
│   │   │   │   │   ├── WiFiAgeAnalyzer.kt
│   │   │   │   │   ├── CertificateValidator.kt
│   │   │   │   │   └── LocationCorrelationAnalyzer.kt
│   │   │   │   └── ThreatScorer.kt
│   │   │   │
│   │   │   ├── notification/
│   │   │   │   ├── ThreatNotificationManager.kt
│   │   │   │   ├── NotificationChannels.kt
│   │   │   │   └── NotificationDecisionEngine.kt
│   │   │   │
│   │   │   ├── billing/
│   │   │   │   ├── BillingManager.kt
│   │   │   │   ├── PurchaseValidator.kt
│   │   │   │   └── SubscriptionManager.kt
│   │   │   │
│   │   │   ├── utils/
│   │   │   │   ├── NetworkUtils.kt
│   │   │   │   ├── SecurityUtils.kt
│   │   │   │   ├── DateUtils.kt
│   │   │   │   ├── PermissionUtils.kt
│   │   │   │   └── Constants.kt
│   │   │   │
│   │   │   └── di/ (Hilt modules)
│   │   │       ├── AppModule.kt
│   │   │       ├── DatabaseModule.kt
│   │   │       ├── NetworkModule.kt
│   │   │       └── RepositoryModule.kt
│   │   │
│   │   ├── res/
│   │   │   ├── values/
│   │   │   │   ├── strings.xml
│   │   │   │   ├── colors.xml
│   │   │   │   ├── themes.xml
│   │   │   │   └── arrays.xml
│   │   │   ├── drawable/
│   │   │   ├── mipmap/ (app icons)
│   │   │   └── raw/
│   │   │       └── threat_purpose_dictionary.json
│   │   │
│   │   └── AndroidManifest.xml
│   │
│   ├── test/ (unit tests)
│   └── androidTest/ (instrumentation tests)
│
├── build.gradle.kts (app module)
└── proguard-rules.pro

buildSrc/ (build logic)
build.gradle.kts (project level)
gradle.properties
settings.gradle.kts
```

---

## 📱 SCREEN HIERARCHY & NAVIGATION

### Navigation Graph

```
App Start
    │
    ├──[First Launch]──► Onboarding (3 slides)
    │                       └──► Request Permissions
    │                            └──► Home Screen
    │
    └──[Returning User]──► Home Screen
```

### Main Navigation (Bottom Nav)

```
┌─────────────────────────────────────────┐
│            Top App Bar                  │
│  [Logo] WiFi Net Guard      [Settings]  │
├─────────────────────────────────────────┤
│                                         │
│         Main Content Area               │
│         (Navigation Host)               │
│                                         │
├─────────────────────────────────────────┤
│  Bottom Navigation                      │
│  [Home] [Dashboard] [Threats] [Premium] │
└─────────────────────────────────────────┘
```

### Screen List & Purpose

```
1. SPLASH SCREEN (SplashActivity.kt)
   └── Show logo, load essentials, check subscription

2. ONBOARDING (OnboardingScreen.kt)
   ├── Slide 1: "Protect yourself from evil WiFi"
   ├── Slide 2: "Real-time threat detection"
   └── Slide 3: "Privacy-first, no data sent"

3. PERMISSION REQUEST (PermissionsScreen.kt)
   ├── VPN permission (mandatory)
   ├── Location (optional, for Evil Twin detection)
   └── Notifications (optional)

4. HOME SCREEN (HomeScreen.kt) - Bottom Nav Item 1
   ├── Current WiFi status card
   ├── Protection toggle (ON/OFF)
   ├── Quick stats (threats blocked today)
   └── Recent activity feed (last 3 threats)

5. DASHBOARD SCREEN (DashboardScreen.kt) - Bottom Nav Item 2
   ├── Real-time security status
   ├── Current WiFi details (SSID, BSSID, encryption)
   ├── Traffic statistics (packets analyzed, data processed)
   ├── Active threats (if any)
   └── WiFi trust score (0-100)

6. THREAT HISTORY (ThreatHistoryScreen.kt) - Bottom Nav Item 3
   ├── Filterable threat log (date, level, type)
   ├── Tap threat → ThreatDetailScreen
   └── Export to PDF button (premium)

7. THREAT DETAIL (ThreatDetailScreen.kt)
   ├── Threat summary (what happened)
   ├── Attacker's goal (purpose)
   ├── Technical details (expandable)
   ├── PCAP download (premium)
   └── Report false positive button

8. SETTINGS (SettingsScreen.kt)
   ├── Notification preferences
   ├── Trust mode (Full/Limited/Privacy)
   ├── Auto-enable on public WiFi
   ├── Battery optimization
   ├── Whitelisted networks
   ├── About & version
   └── Support & feedback

9. PREMIUM (PremiumScreen.kt) - Bottom Nav Item 4
   ├── Feature comparison table (Free vs Premium)
   ├── Pricing options (monthly/yearly/lifetime)
   ├── Purchase buttons
   └── Restore purchases

10. ALERT ACTIVITY (ThreatAlertActivity.kt)
    ├── Full-screen critical alert
    ├── Shows threat details
    ├── Action buttons (Disconnect, Ignore)
    └── Launched for Level 4 threats
```

---

## 🗄️ DATABASE SCHEMA

### SQLite Tables (Room)

```sql
-- THREAT LOGS
CREATE TABLE threat_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    threat_type TEXT NOT NULL,           -- DNS_HIJACK_TYPE_3, etc
    threat_level INTEGER NOT NULL,       -- 1-4
    confidence REAL NOT NULL,            -- 0.0-1.0
    timestamp INTEGER NOT NULL,          -- Unix timestamp
    wifi_ssid TEXT NOT NULL,
    wifi_bssid TEXT NOT NULL,
    target_domain TEXT,
    target_ip TEXT,
    real_ip TEXT,                        -- From validation
    was_blocked INTEGER NOT NULL,        -- Boolean (0/1)
    user_notified INTEGER NOT NULL,
    pcap_file_path TEXT,                 -- Local file path
    packet_summary TEXT,                 -- JSON
    user_marked_false_positive INTEGER DEFAULT 0,
    user_note TEXT
);

CREATE INDEX idx_threat_timestamp ON threat_logs(timestamp DESC);
CREATE INDEX idx_threat_level ON threat_logs(threat_level);
CREATE INDEX idx_wifi_bssid ON threat_logs(wifi_bssid);

-- WIFI PROFILES
CREATE TABLE wifi_profiles (
    bssid TEXT PRIMARY KEY NOT NULL,     -- MAC address (unique)
    ssid TEXT NOT NULL,
    first_seen_at INTEGER NOT NULL,
    last_seen_at INTEGER NOT NULL,
    total_sessions INTEGER DEFAULT 0,
    total_time_minutes INTEGER DEFAULT 0,
    avg_dns_queries_per_min REAL,
    avg_unique_ips_per_session REAL,
    avg_packet_size INTEGER,
    typical_domains TEXT,                 -- JSON array
    trust_score REAL DEFAULT 0.5,
    is_verified INTEGER DEFAULT 0,
    owner_type TEXT,                      -- HOME, WORK, CAFE, etc
    threats_detected INTEGER DEFAULT 0,
    false_alerts INTEGER DEFAULT 0,
    approximate_location TEXT,
    gps_lat REAL,
    gps_lng REAL,
    vendor_prefix TEXT,                   -- First 6 chars of MAC
    expected_encryption TEXT,
    expected_captive_portal INTEGER,
    last_known_gateway_ip TEXT,
    notes TEXT
);

CREATE INDEX idx_wifi_ssid ON wifi_profiles(ssid);
CREATE INDEX idx_wifi_trust ON wifi_profiles(trust_score);

-- COMMUNITY THREATS (synced from server)
CREATE TABLE community_threats (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    threat_bssid TEXT,
    threat_ssid TEXT,
    threat_type TEXT NOT NULL,
    report_count INTEGER DEFAULT 1,
    confidence_score REAL,
    first_reported INTEGER NOT NULL,
    last_reported INTEGER NOT NULL,
    approximate_location TEXT,
    is_verified INTEGER DEFAULT 0
);

CREATE INDEX idx_community_bssid ON community_threats(threat_bssid);
CREATE INDEX idx_community_ssid ON community_threats(threat_ssid);

-- USER PREFERENCES (DataStore alternative)
CREATE TABLE user_prefs (
    key TEXT PRIMARY KEY NOT NULL,
    value TEXT NOT NULL
);
```

---

## 🔐 VPN SERVICE IMPLEMENTATION

### Core VPN Logic

```kotlin
// GorinoxVpnService.kt

class GorinoxVpnService : VpnService() {
    
    private var vpnInterface: ParcelFileDescriptor? = null
    private var captureThread: PacketCaptureThread? = null
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_VPN -> startVpn()
            ACTION_STOP_VPN -> stopVpn()
        }
        return START_STICKY
    }
    
    private fun startVpn() {
        // Show foreground notification
        startForeground(NOTIFICATION_ID, createNotification())
        
        // Build VPN interface
        val builder = Builder()
            .setSession("WiFi Net Guard VPN")
            .addAddress("10.0.0.2", 24)  // Fake local IP
            .addRoute("0.0.0.0", 0)       // Route all traffic
            .addDnsServer("8.8.8.8")      // Use safe DNS
            .addDnsServer("1.1.1.1")
            .setMtu(1500)
            .setBlocking(true)
        
        // Configure allowed/disallowed apps (if needed)
        // builder.addDisallowedApplication("com.example.excluded")
        
        vpnInterface = builder.establish()
        
        if (vpnInterface == null) {
            Log.e(TAG, "Failed to establish VPN")
            stopSelf()
            return
        }
        
        // Start packet capture thread
        captureThread = PacketCaptureThread(
            vpnInterface!!,
            detectionEngine = get(),  // Hilt inject
            notificationManager = get()
        ).apply { start() }
    }
    
    private fun stopVpn() {
        captureThread?.interrupt()
        captureThread = null
        vpnInterface?.close()
        vpnInterface = null
        stopForeground(true)
        stopSelf()
    }
    
    companion object {
        const val ACTION_START_VPN = "START_VPN"
        const val ACTION_STOP_VPN = "STOP_VPN"
        private const val NOTIFICATION_ID = 1001
        private const val TAG = "GorinoxVPN"
    }
}
```

### Packet Capture & Analysis

```kotlin
// PacketCaptureThread.kt

class PacketCaptureThread(
    private val vpnInterface: ParcelFileDescriptor,
    private val detectionEngine: MasterDetectionEngine,
    private val notificationManager: ThreatNotificationManager
) : Thread() {
    
    private val inputStream = FileInputStream(vpnInterface.fileDescriptor)
    private val outputStream = FileOutputStream(vpnInterface.fileDescriptor)
    private val buffer = ByteArray(32767)  // Max IP packet size
    
    override fun run() {
        Log.d(TAG, "Packet capture started")
        
        try {
            while (!isInterrupted) {
                val length = inputStream.read(buffer)
                
                if (length > 0) {
                    val packet = buffer.copyOf(length)
                    
                    // Parse packet
                    val networkPacket = PacketParser.parse(packet)
                    
                    if (networkPacket != null) {
                        // Analyze for threats
                        val threat = detectionEngine.analyzePacket(networkPacket)
                        
                        if (threat != null && threat.level > 0) {
                            // Threat detected!
                            handleThreat(threat, networkPacket)
                        }
                    }
                    
                    // Forward packet to internet (unless blocked)
                    if (!isBlocked(networkPacket)) {
                        outputStream.write(packet, 0, length)
                    }
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Packet capture error", e)
        }
    }
    
    private fun handleThreat(threat: Threat, packet: NetworkPacket) {
        // Log to database
        threatRepository.logThreat(threat, packet)
        
        // Decide notification
        val decision = notificationManager.shouldNotify(threat)
        if (decision == NotificationDecision.NOTIFY_IMMEDIATELY) {
            notificationManager.notify(threat)
        }
        
        // Auto-disconnect for critical threats
        if (threat.level >= 4) {
            vpnInterface.close()
        }
    }
    
    companion object {
        private const val TAG = "PacketCapture"
    }
}
```

### Packet Parser (Lightweight)

```kotlin
// PacketParser.kt

object PacketParser {
    
    fun parse(rawPacket: ByteArray): NetworkPacket? {
        if (rawPacket.size < 20) return null
        
        // IP Version (first nibble)
        val ipVersion = (rawPacket[0].toInt() shr 4) and 0x0F
        if (ipVersion != 4) return null  // IPv4 only for now
        
        // Protocol (byte 9)
        val protocol = rawPacket[9].toInt() and 0xFF
        
        // Source IP (bytes 12-15)
        val sourceIP = "${rawPacket[12].toUByte()}.${rawPacket[13].toUByte()}." +
                       "${rawPacket[14].toUByte()}.${rawPacket[15].toUByte()}"
        
        // Destination IP (bytes 16-19)
        val destIP = "${rawPacket[16].toUByte()}.${rawPacket[17].toUByte()}." +
                     "${rawPacket[18].toUByte()}.${rawPacket[19].toUByte()}"
        
        // Header length
        val headerLength = (rawPacket[0].toInt() and 0x0F) * 4
        
        // Ports (for TCP/UDP)
        var sourcePort = 0
        var destPort = 0
        if (protocol == 6 || protocol == 17) { // TCP or UDP
            if (rawPacket.size >= headerLength + 4) {
                sourcePort = ((rawPacket[headerLength].toInt() and 0xFF) shl 8) or
                             (rawPacket[headerLength + 1].toInt() and 0xFF)
                destPort = ((rawPacket[headerLength + 2].toInt() and 0xFF) shl 8) or
                           (rawPacket[headerLength + 3].toInt() and 0xFF)
            }
        }
        
        // Payload
        val payload = if (rawPacket.size > headerLength + 8) {
            rawPacket.copyOfRange(headerLength + 8, rawPacket.size)
        } else {
            ByteArray(0)
        }
        
        return NetworkPacket(
            protocol = when(protocol) {
                6 -> Protocol.TCP
                17 -> Protocol.UDP
                1 -> Protocol.ICMP
                else -> Protocol.OTHER
            },
            sourceIP = sourceIP,
            destIP = destIP,
            sourcePort = sourcePort,
            destPort = destPort,
            payload = payload,
            timestamp = System.currentTimeMillis()
        )
    }
}

data class NetworkPacket(
    val protocol: Protocol,
    val sourceIP: String,
    val destIP: String,
    val sourcePort: Int,
    val destPort: Int,
    val payload: ByteArray,
    val timestamp: Long
)

enum class Protocol {
    TCP, UDP, ICMP, OTHER
}
```

---

## 🔍 DETECTION ALGORITHMS (12 Methods)

### Method 1: MAC Vendor Analysis

```kotlin
// RouterVendorAnalyzer.kt

class RouterVendorAnalyzer(context: Context) {
    
    private val ouiDatabase: Map<String, String> by lazy {
        // Load from assets/oui_database.json
        val json = context.assets.open("oui_database.json").bufferedReader().use { it.readText() }
        Moshi.Builder().build().adapter<Map<String, String>>().fromJson(json) ?: emptyMap()
    }
    
    fun analyzeVendor(bssid: String): RouterRisk {
        val oui = bssid.substring(0, 8).uppercase()  // First 3 bytes (AA:BB:CC)
        val vendor = ouiDatabase[oui] ?: return RouterRisk.UNKNOWN
        
        return when {
            // Professional cafe equipment
            vendor in TRUSTED_VENDORS -> RouterRisk.LOW
            
            // Hacker tools (WiFi Pineapple uses Raspberry Pi)
            vendor in HACKER_TOOL_VENDORS -> RouterRisk.CRITICAL
            
            // Consumer routers
            vendor in CONSUMER_VENDORS -> RouterRisk.MEDIUM
            
            else -> RouterRisk.HIGH
        }
    }
    
    companion object {
        val TRUSTED_VENDORS = setOf(
            "Cisco Systems",
            "Aruba Networks",
            "Ruckus Wireless",
            "Meraki"
        )
        
        val HACKER_TOOL_VENDORS = setOf(
            "Raspberry Pi",
            "Hak5"  // WiFi Pineapple manufacturer
        )
        
        val CONSUMER_VENDORS = setOf(
            "TP-Link",
            "ASUS",
            "Netgear",
            "D-Link"
        )
    }
}

enum class RouterRisk(val score: Float) {
    LOW(0.1f),
    MEDIUM(0.4f),
    HIGH(0.7f),
    CRITICAL(0.95f),
    UNKNOWN(0.5f)
}
```

### Method 2: DNS Manipulation Detection

```kotlin
// DnsManipulationDetector.kt

class DnsManipulationDetector {
    
    private val criticalDomains = setOf(
        "google.com", "facebook.com", "instagram.com",
        "twitter.com", "amazon.com", "paypal.com",
        // ... Top 500 domains loaded from assets
    )
    
    suspend fun detect(packet: NetworkPacket): Threat? {
        // Check if DNS query/response (port 53)
        if (packet.destPort != 53 && packet.sourcePort != 53) return null
        
        val dnsQuery = parseDnsQuery(packet.payload) ?: return null
        
        // Only check critical domains
        if (dnsQuery.domain !in criticalDomains) return null
        
        // Get IP from response
        val responseIP = parseDnsResponse(packet.payload) ?: return null
        
        // Validate against trusted DNS
        val trustedIP = queryTrustedDNS(dnsQuery.domain)
        
        return if (responseIP != trustedIP) {
            // IPs don't match!
            if (isPrivateIP(responseIP)) {
                // Local IP = definite attack
                Threat(
                    type = ThreatType.DNS_MANIPULATION,
                    level = 4,
                    confidence = 0.98f,
                    domain = dnsQuery.domain,
                    attackerIP = responseIP,
                    realIP = trustedIP
                )
            } else {
                // Different public IP = possible geo/CDN difference
                Threat(
                    type = ThreatType.DNS_MANIPULATION,
                    level = 2,
                    confidence = 0.6f,
                    domain = dnsQuery.domain,
                    attackerIP = responseIP,
                    realIP = trustedIP
                )
            }
        } else {
            null  // Match, all good
        }
    }
    
    private fun isPrivateIP(ip: String): Boolean {
        return ip.startsWith("192.168.") ||
               ip.startsWith("10.") ||
               ip.startsWith("172.16.") ||
               ip.startsWith("127.")
    }
    
    private suspend fun queryTrustedDNS(domain: String): String {
        // Query 8.8.8.8 or 1.1.1.1
        // Use DNS-over-HTTPS to avoid manipulation
        val url = "https://dns.google/resolve?name=$domain&type=A"
        // ... HTTP request ...
        return parsedIP
    }
}
```

### Method 3: SSL Strip Detection

```kotlin
// SslStripDetector.kt

class SslStripDetector(context: Context) {
    
    private val hstsPreloadList: Set<String> by lazy {
        // Load from assets/hsts_preload.json
        // Contains domains that MUST use HTTPS
        loadHstsPreloadList(context)
    }
    
    fun detect(packet: NetworkPacket): Threat? {
        // Check if HTTP (port 80)
        if (packet.destPort != 80) return null
        
        // Parse HTTP request
        val httpRequest = parseHttpRequest(packet.payload) ?: return null
        val domain = httpRequest.host
        
        // Check if this domain should be HTTPS-only
        if (domain in hstsPreloadList) {
            // HTTP to HTTPS-only site = SSL STRIP!
            return Threat(
                type = ThreatType.SSL_STRIP,
                level = 4,
                confidence = 0.95f,
                domain = domain,
                description = "Site $domain should be HTTPS but HTTP detected"
            )
        }
        
        // Check for HTTPS->HTTP redirect
        if (httpRequest.referrer?.startsWith("https://") == true) {
            return Threat(
                type = ThreatType.SSL_STRIP,
                level = 3,
                confidence = 0.85f,
                domain = domain,
                description = "Downgrade from HTTPS to HTTP detected"
            )
        }
        
        return null
    }
}
```

### Method 4: Fake WiFi Detection

```kotlin
// FakeWiFiDetector.kt

class FakeWiFiDetector(
    private val wifiProfileDao: WiFiProfileDao
) {
    
    suspend fun detectEvilTwin(ssid: String, bssid: String): FakeWiFiResult {
        // Get all known profiles with same SSID
        val knownProfiles = wifiProfileDao.getProfilesBySsid(ssid)
        
        if (knownProfiles.isEmpty()) {
            // First time seeing this SSID
            return FakeWiFiResult(isFake = false, confidence = 0f)
        }
        
        // Check if BSSID matches known
        val matchesKnownBssid = knownProfiles.any { it.bssid == bssid }
        
        if (matchesKnownBssid) {
            // Known good network
            return FakeWiFiResult(isFake = false, confidence = 0f)
        }
        
        // SAME SSID, DIFFERENT BSSID = SUSPICIOUS
        
        var suspicionScore = 0.3f  // Base suspicion
        
        // Check vendor
        val knownVendor = knownProfiles.first().vendorPrefix
        val currentVendor = bssid.substring(0, 8)
        if (knownVendor != currentVendor) {
            suspicionScore += 0.2f  // Different manufacturer
        }
        
        // Check encryption
        val knownEncryption = knownProfiles.first().expectedEncryption
        val currentEncryption = getCurrentEncryption(bssid)
        if (knownEncryption != null && knownEncryption != currentEncryption) {
            suspicionScore += 0.3f  // Different security
        }
        
        // Check location (if GPS available)
        val knownLocation = knownProfiles.first().gpsLat
        if (knownLocation != null) {
            val currentLocation = getCurrentLocation()
            if (currentLocation != null) {
                val distance = calculateDistance(knownLocation, currentLocation)
                if (distance > 5000) {  // 5km+
                    suspicionScore += 0.2f
                }
            }
        }
        
        return FakeWiFiResult(
            isFake = suspicionScore > 0.7f,
            confidence = suspicionScore,
            reason = if (suspicionScore > 0.7f) "Evil Twin Attack" else "Different Location"
        )
    }
}
```

### Other Detection Methods (Summary)

```kotlin
// 5. Signal Strength Pattern
class SignalStrengthAnalyzer {
    fun analyze(signal: Int, history: List<Int>): SignalAnalysis
    // Fake APs often have suspiciously strong/stable signal
}

// 6. WiFi Age Analysis
class WiFiAgeAnalyzer {
    fun analyzeAge(bssid: String, firstSeen: Long): AgeAnalysis
    // Newly created networks are suspicious
}

// 7. Certificate Validator
class CertificateValidator {
    fun validate(host: String, cert: X509Certificate): CertValidation
    // MITM uses fake certificates
}

// 8. Location Correlator
class LocationCorrelationAnalyzer {
    fun analyze(bssid: String, location: LatLng): LocationAnalysis
    // Same BSSID 5km away = cloned
}

// 9. Beacon Frame Analyzer
class BeaconAnalyzer {
    fun analyzeBeacons(scanResults: List<ScanResult>): BeaconAnalysis
    // Multiple BSSIDs same SSID = Evil Twin
}

// 10. Traffic Fingerprinter
class TrafficFingerprinter {
    fun fingerprint(): TrafficFingerprint
    // DHCP/DNS response times differ by device
}

// 11. Probe Request Analyzer (Root)
class ProbeRequestAnalyzer {
    fun detectKarmaAttack(): KarmaTestResult
    // Send fake SSID, if response = Karma attack
}

// 12. Community Database
class CommunityThreatDb {
    suspend fun checkAgainst(bssid: String): DbCheckResult
    // Check against crowdsourced threats
}
```

---

## 🎨 UI/UX DESIGN CONCEPT

### Design Theme: "Digital Guardian"

**Visual Identity:**
```
CONCEPT: Premium, trustworthy, protective, modern

COLOR PALETTE:
Primary: Deep Blue (#0A1F44) - Trust, security
Secondary: Electric Cyan (#00E5FF) - Technology, alertness
Accent: Neon Green (#00FF88) - Active protection
Warning: Amber (#FFA726) - Caution
Critical: Crimson (#EF5350) - Danger
Background: Near Black (#0D1117) - Premium feel
Surface: Dark Gray (#1C2128) - Cards, surfaces
On-Surface: White (#FFFFFF) - Text

TYPOGRAPHY:
Headings: Inter (Bold, 600-700 weight)
Body: Inter (Regular, 400 weight)
Monospace: JetBrains Mono (for technical data)

ICONOGRAPHY:
Style: Outlined, modern, tech-inspired
Source: Material Symbols (outlined variant)
Custom: Shield with circuit pattern for logo

SHAPES:
Card corners: 16dp rounded
Buttons: 12dp rounded
Bottom sheet: 24dp top corners
Emphasis: Subtle elevation (4-8dp)

ANIMATIONS:
Style: Smooth, purposeful, quick (200-300ms)
Shield pulse when analyzing
Threat cards slide in with bounce
Level indicators fill with gradient
```

### Key Screen Designs

#### 1. Home Screen (Protection Dashboard)

```
┌─────────────────────────────────────────┐
│  [Logo]  WiFi Net Guard      [⚙️]       │
├─────────────────────────────────────────┤
│                                         │
│  ┌─────────────────────────────────┐   │
│  │  🛡️ Protection Status           │   │
│  │                                 │   │
│  │  [████████████████░░] 92%       │   │
│  │  ACTIVELY PROTECTING            │   │
│  │                                 │   │
│  │  Connected: "Starbucks WiFi"    │   │
│  │  Risk Level: LOW                │   │
│  │                                 │   │
│  │     [■ DISABLE PROTECTION]      │   │
│  └─────────────────────────────────┘   │
│                                         │
│  Today's Activity                       │
│  ┌───────────────┬───────────────┐     │
│  │ 🚫 Blocked    │ 📊 Analyzed   │     │
│  │ 247 Threats   │ 15,842 Packets│     │
│  └───────────────┴───────────────┘     │
│                                         │
│  Recent Threats                         │
│  ┌─────────────────────────────────┐   │
│  │ 🟡 Ad Injection                 │   │
│  │ 2 minutes ago • Blocked         │   │
│  └─────────────────────────────────┘   │
│  ┌─────────────────────────────────┐   │
│  │ 🟢 Tracker                      │   │
│  │ 5 minutes ago • Blocked         │   │
│  └─────────────────────────────────┘   │
│                                         │
├─────────────────────────────────────────┤
│ [🏠] [📊] [🛡️] [⭐]                     │
└─────────────────────────────────────────┘
```

#### 2. Dashboard (Real-Time Analysis)

```
┌─────────────────────────────────────────┐
│  ← Back       Dashboard                 │
├─────────────────────────────────────────┤
│                                         │
│  Current Network                        │
│  ┌─────────────────────────────────┐   │
│  │  📶 Starbucks_WiFi              │   │
│  │                                 │   │
│  │  BSSID: AA:BB:CC:11:22:33       │   │
│  │  Encryption: WPA2               │   │
│  │  Vendor: Cisco Systems          │   │
│  │  Signal: -45 dBm (Excellent)    │   │
│  │                                 │   │
│  │  ✅ Trusted Network             │   │
│  │  Trust Score: 95/100            │   │
│  └─────────────────────────────────┘   │
│                                         │
│  Live Traffic                           │
│  [Real-time packet graph - line chart] │
│  📈 15,842 packets analyzed             │
│                                         │
│  Detection Status                       │
│  ├─ DNS Validation: ✅ Active           │
│  ├─ SSL Monitoring: ✅ Active           │
│  ├─ MITM Detection: ✅ Active           │
│  └─ Evil Twin Scan: ✅ Active           │
│                                         │
│  [View Full Report]                     │
│                                         │
└─────────────────────────────────────────┘
```

#### 3. Threat Detail (Expanded)

```
┌─────────────────────────────────────────┐
│  ← Back     Threat Detail               │
├─────────────────────────────────────────┤
│                                         │
│  🔴 CRITICAL THREAT DETECTED            │
│                                         │
│  DNS Manipulation                       │
│  May 20, 2026 • 14:32:17                │
│                                         │
│  ─────────────────────────────────      │
│                                         │
│  📌 What Happened?                      │
│                                         │
│  This WiFi tried to send you to a FAKE │
│  version of facebook.com. If you had   │
│  logged in, your password would've been │
│  stolen.                                │
│                                         │
│  ─────────────────────────────────      │
│                                         │
│  🎯 Attacker's Goal                     │
│                                         │
│  • Steal your Facebook password         │
│  • Access your messages                 │
│  • Use your identity                    │
│  • Hack accounts with same password     │
│                                         │
│  ─────────────────────────────────      │
│                                         │
│  ✅ We Blocked It                       │
│                                         │
│  The fake site was blocked before you   │
│  could connect. You're safe.            │
│                                         │
│  ─────────────────────────────────      │
│                                         │
│  ⚠️ What You Should Do                  │
│                                         │
│  • Leave this WiFi network              │
│  • Use mobile data instead              │
│  • Check your Facebook password         │
│  • Enable 2-factor authentication       │
│                                         │
│  [▼ Show Technical Details]             │
│                                         │
│  [Report False Positive]  [Export]      │
│                                         │
└─────────────────────────────────────────┘
```

#### 4. Premium Upsell

```
┌─────────────────────────────────────────┐
│  ← Back     Go Premium                  │
├─────────────────────────────────────────┤
│                                         │
│  🌟 Unlock Full Protection              │
│                                         │
│  Currently: FREE                        │
│                                         │
│  Free Features                          │
│  ✅ DNS manipulation detection          │
│  ✅ SSL strip detection                 │
│  ✅ Basic ad blocking                   │
│  ✅ Community database                  │
│                                         │
│  ─────────────────────────────────      │
│                                         │
│  Premium Features                       │
│  🔒 All 14 threat types                 │
│  🔒 ML predictive defense               │
│  🔒 Root super-features                 │
│  🔒 Unlimited log retention             │
│  🔒 Cloud backup                        │
│  🔒 PDF reports                         │
│  🔒 Priority support                    │
│  🔒 No ads                              │
│                                         │
│  ─────────────────────────────────      │
│                                         │
│  Choose Your Plan                       │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │  Monthly                        │   │
│  │  $4.99 / month                  │   │
│  │  [Subscribe]                    │   │
│  └─────────────────────────────────┘   │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │  Yearly       SAVE 33%! 🔥      │   │
│  │  $39.99 / year                  │   │
│  │  ($3.33/month)                  │   │
│  │  [Subscribe]                    │   │
│  └─────────────────────────────────┘   │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │  Lifetime     BEST VALUE 🏆     │   │
│  │  $79.99 once                    │   │
│  │  (Pay once, use forever)        │   │
│  │  [Buy Now]                      │   │
│  └─────────────────────────────────┘   │
│                                         │
│  [Restore Purchases]                    │
│                                         │
└─────────────────────────────────────────┘
```

### Animation Specifications

```kotlin
// Theme animations

// Shield pulse (when active)
val shieldPulse = infiniteRepeatable(
    animation = tween(2000, easing = FastOutSlowInEasing),
    repeatMode = RepeatMode.Reverse
)

// Threat card slide-in
val threatSlideIn = slideInVertically(
    initialOffsetY = { it },
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
)

// Risk meter fill
val riskFill = tween<Float>(
    durationMillis = 800,
    easing = FastOutSlowInEasing
)

// Button press
val buttonPress = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow
)
```

---

## 🔔 NOTIFICATION SYSTEM

### Notification Channels

```kotlin
// NotificationChannels.kt

object NotificationChannels {
    
    const val VPN_STATUS = "vpn_status"
    const val THREAT_LOW = "threat_low"
    const val THREAT_MEDIUM = "threat_medium"
    const val THREAT_HIGH = "threat_high"
    const val THREAT_CRITICAL = "threat_critical"
    const val DAILY_SUMMARY = "daily_summary"
    
    fun createChannels(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        
        // VPN Status (foreground service notification)
        manager.createNotificationChannel(
            NotificationChannel(
                VPN_STATUS,
                "VPN Status",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows when VPN is active"
                setShowBadge(false)
            }
        )
        
        // Low threats (silent)
        manager.createNotificationChannel(
            NotificationChannel(
                THREAT_LOW,
                "Low Threats",
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "Minor threats (trackers, ads)"
                setShowBadge(false)
            }
        )
        
        // Medium threats
        manager.createNotificationChannel(
            NotificationChannel(
                THREAT_MEDIUM,
                "Medium Threats",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Suspicious activity"
            }
        )
        
        // High threats
        manager.createNotificationChannel(
            NotificationChannel(
                THREAT_HIGH,
                "High Threats",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Dangerous attacks detected"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
            }
        )
        
        // Critical threats
        manager.createNotificationChannel(
            NotificationChannel(
                THREAT_CRITICAL,
                "Critical Threats",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Immediate danger"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 1000, 500, 1000)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                )
            }
        )
        
        // Daily summary
        manager.createNotificationChannel(
            NotificationChannel(
                DAILY_SUMMARY,
                "Daily Summary",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily protection summary"
            }
        )
    }
}
```

### Smart Notification Logic

```kotlin
// NotificationDecisionEngine.kt

class NotificationDecisionEngine(
    private val wifiRepository: WiFiRepository,
    private val preferencesManager: PreferencesManager
) {
    
    suspend fun shouldNotify(threat: Threat): NotificationDecision {
        // User preference
        val notificationLevel = preferencesManager.getNotificationLevel()
        if (notificationLevel == NotificationLevel.OFF) {
            return NotificationDecision.NEVER
        }
        
        // Never notify for Level 0
        if (threat.level == 0) return NotificationDecision.NEVER
        
        // Check if normal traffic
        if (isNormalTraffic(threat)) return NotificationDecision.NEVER
        
        // Check if known tracker (just count, don't notify)
        if (isKnownTracker(threat)) return NotificationDecision.SILENT_LOG
        
        // Get WiFi context
        val currentWiFi = wifiRepository.getCurrentWiFi()
        
        return when (currentWiFi?.ownerType) {
            WiFiOwnerType.HOME, WiFiOwnerType.WORK -> {
                // Trusted networks: only critical
                if (threat.level >= 3) NotificationDecision.NOTIFY_IMMEDIATELY
                else NotificationDecision.SILENT_LOG
            }
            
            WiFiOwnerType.KNOWN_CAFE -> {
                // Known cafe: medium+
                when (threat.level) {
                    1, 2 -> NotificationDecision.SILENT_LOG
                    3, 4 -> NotificationDecision.NOTIFY_IMMEDIATELY
                    else -> NotificationDecision.SILENT_LOG
                }
            }
            
            WiFiOwnerType.UNKNOWN_PUBLIC -> {
                // Unknown network: be more sensitive
                when (threat.level) {
                    1 -> NotificationDecision.SILENT_LOG
                    2 -> NotificationDecision.NOTIFY_SUMMARY
                    3, 4 -> NotificationDecision.NOTIFY_IMMEDIATELY
                    else -> NotificationDecision.SILENT_LOG
                }
            }
            
            WiFiOwnerType.SUSPICIOUS -> {
                // Already flagged: notify everything 2+
                if (threat.level >= 2) NotificationDecision.NOTIFY_IMMEDIATELY
                else NotificationDecision.SILENT_LOG
            }
            
            null -> {
                // Unknown WiFi profile
                if (threat.level >= 3) NotificationDecision.NOTIFY_IMMEDIATELY
                else NotificationDecision.SILENT_LOG
            }
        }
    }
}

enum class NotificationDecision {
    NEVER,               // Don't log or notify
    SILENT_LOG,          // Log but don't notify
    NOTIFY_SUMMARY,      // Include in daily summary
    NOTIFY_IMMEDIATELY,  // Notify right now
    CRITICAL_FULL_SCREEN // Full-screen alert
}
```

---

## 💳 BILLING & SUBSCRIPTIONS

### Google Play Billing Integration

```kotlin
// BillingManager.kt

class BillingManager(
    private val activity: Activity
) {
    
    private val billingClient: BillingClient = BillingClient.newBuilder(activity)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()
    
    private val productIds = listOf(
        "premium_monthly",  // $4.99/month
        "premium_yearly",   // $39.99/year
        "premium_lifetime"  // $79.99 one-time
    )
    
    fun startConnection(onReady: () -> Unit) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    onReady()
                }
            }
            
            override fun onBillingServiceDisconnected() {
                // Retry connection
            }
        })
    }
    
    suspend fun queryProducts(): List<ProductDetails> {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                productIds.map { id ->
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(id)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                }
            )
            .build()
        
        val result = billingClient.queryProductDetails(params)
        return result.productDetailsList ?: emptyList()
    }
    
    fun launchPurchaseFlow(productDetails: ProductDetails) {
        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build()
                )
            )
            .build()
        
        billingClient.launchBillingFlow(activity, params)
    }
    
    private val purchasesUpdatedListener = PurchasesUpdatedListener { result, purchases ->
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        }
    }
    
    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            // Verify purchase server-side (if needed)
            // Grant premium features
            preferencesManager.setPremiumUser(true)
            
            // Acknowledge purchase
            if (!purchase.isAcknowledged) {
                val params = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                
                billingClient.acknowledgePurchase(params) { result ->
                    if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                        // Success
                    }
                }
            }
        }
    }
    
    suspend fun checkSubscription(): Boolean {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        
        val result = billingClient.queryPurchasesAsync(params)
        return result.purchasesList.any { it.purchaseState == Purchase.PurchaseState.PURCHASED }
    }
}
```

---

## 🚀 GOOGLE PLAY STORE PREPARATION

### AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    package="com.gorinox.wifinetguard">

    <!-- VPN permissions -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    
    <!-- Location (optional, for Evil Twin detection) -->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    
    <!-- WiFi scanning -->
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
    
    <!-- Notifications -->
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    
    <!-- Foreground service -->
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
    
    <!-- Billing -->
    <uses-permission android:name="com.android.vending.BILLING" />
    
    <!-- Boot receiver (for watchdog) -->
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />

    <application
        android:name=".GorinoxApp"
        android:allowBackup="false"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="false"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.WifiNetGuard"
        tools:targetApi="31">

        <!-- Main Activity -->
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.WifiNetGuard"
            android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!-- VPN Service -->
        <service
            android:name=".vpn.GorinoxVpnService"
            android:permission="android.permission.BIND_VPN_SERVICE"
            android:exported="false"
            android:foregroundServiceType="specialUse">
            <intent-filter>
                <action android:name="android.net.VpnService" />
            </intent-filter>
            <meta-data
                android:name="android.net.VpnService.SUPPORTS_ALWAYS_ON"
                android:value="true" />
        </service>

        <!-- Threat Alert Activity (full-screen) -->
        <activity
            android:name=".ui.screens.threats.ThreatAlertActivity"
            android:excludeFromRecents="true"
            android:exported="false"
            android:showWhenLocked="true"
            android:turnScreenOn="true" />

        <!-- Boot Receiver -->
        <receiver
            android:name=".receivers.BootReceiver"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
                <action android:name="android.intent.action.LOCKED_BOOT_COMPLETED" />
            </intent-filter>
        </receiver>

        <!-- WorkManager -->
        <provider
            android:name="androidx.startup.InitializationProvider"
            android:authorities="${applicationId}.androidx-startup"
            android:exported="false"
            tools:node="merge">
            <meta-data
                android:name="androidx.work.WorkManagerInitializer"
                android:value="androidx.startup" />
        </provider>

    </application>

</manifest>
```

### Proguard Rules

```proguard
# proguard-rules.pro

# Keep app classes
-keep class com.gorinox.wifinetguard.** { *; }

# Keep data classes for Room
-keep @androidx.room.Entity class * { *; }

# Keep billing classes
-keep class com.android.billingclient.** { *; }

# Keep Retrofit & OkHttp
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Keep Moshi
-keep class com.squareup.moshi.** { *; }
-keep interface com.squareup.moshi.** { *; }

# Keep Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Obfuscate everything else
-repackageclasses
```

### Play Store Listing Assets

```
REQUIRED ASSETS:

1. App Icon
   - 512x512px PNG
   - No transparency
   - Shield with circuit pattern design

2. Feature Graphic
   - 1024x500px PNG
   - Shows app in action protecting user

3. Screenshots (8 required)
   - 1080x1920px or 1440x2560px
   - Home screen
   - Dashboard (real-time)
   - Threat detail
   - Premium features
   - Settings
   - Onboarding
   - Success story
   - Comparison (before/after)

4. Promotional Video (optional but recommended)
   - 30 seconds
   - Show fake WiFi attack demo
   - Show app detecting & blocking
   - End with "Stay safe with WiFi Net Guard"

5. Privacy Policy URL
   - Hosted at gorinox.com/privacy-policy

LISTING TEXT:

Short Description (80 chars):
"Detect & block evil WiFi attacks. Real-time protection for public networks."

Full Description (4000 chars):
[See separate document: play_store_description.txt]

CATEGORY: Tools > Security
CONTENT RATING: Everyone
PRICE: Free (with in-app purchases)
IN-APP PRODUCTS:
- Premium Monthly: $4.99
- Premium Yearly: $39.99
- Premium Lifetime: $79.99

TARGET AUDIENCE & AGE:
- 18-65 years old
- Travelers, remote workers, security-conscious users
```

---

## ✅ DEVELOPMENT CHECKLIST

### Phase 1: Core VPN (Week 1-2)

```
[ ] Project setup (Gradle, Hilt, Room)
[ ] VpnService implementation
[ ] Packet capture thread
[ ] Basic packet parser
[ ] Foreground service notification
[ ] VPN start/stop logic
[ ] Boot-time protection
[ ] Zero-leak hot-swap
```

### Phase 2: Detection Engine (Week 3-4)

```
[ ] Master detection engine
[ ] DNS manipulation detector
[ ] SSL strip detector
[ ] Fake WiFi detector (Evil Twin)
[ ] Ad injection detector
[ ] Crypto mining detector
[ ] MITM detector
[ ] Certificate validator
[ ] Community threat database
[ ] Threat scoring algorithm
```

### Phase 3: Database & Logging (Week 4-5)

```
[ ] Room database setup
[ ] Threat log DAO
[ ] WiFi profile DAO
[ ] Community threat DAO
[ ] Log rotation (delete old)
[ ] PCAP file export
[ ] Encrypted storage for sensitive data
```

### Phase 4: UI Implementation (Week 5-7)

```
[ ] Design system (colors, typography, shapes)
[ ] Home screen
[ ] Dashboard screen
[ ] Threat history screen
[ ] Threat detail screen
[ ] Settings screen
[ ] Premium screen
[ ] Onboarding flow
[ ] Navigation setup
[ ] Dark theme support
```

### Phase 5: Notifications (Week 7)

```
[ ] Notification channels
[ ] Smart notification logic
[ ] Threat notifications (all levels)
[ ] Daily summary
[ ] Full-screen critical alerts
[ ] Notification actions (Disconnect, Ignore)
```

### Phase 6: Billing (Week 8)

```
[ ] Google Play Billing integration
[ ] Purchase flow
[ ] Subscription validation
[ ] Premium feature gating
[ ] Restore purchases
[ ] Trial period (if applicable)
```

### Phase 7: Testing & Polish (Week 9-10)

```
[ ] Unit tests (80%+ coverage)
[ ] UI tests (Espresso)
[ ] Manual testing (all devices)
[ ] Performance testing (battery, RAM, network)
[ ] Security audit (MOBSF)
[ ] ProGuard configuration
[ ] Crash reporting (Firebase Crashlytics)
[ ] Analytics (Firebase Analytics)
```

### Phase 8: Release Preparation (Week 10-11)

```
[ ] Play Store assets (icon, screenshots, graphics)
[ ] Privacy policy page
[ ] Terms of service
[ ] Play Store listing text
[ ] Content rating questionnaire
[ ] Alpha/Beta testing (internal/closed)
[ ] Bug fixes from testing
```

### Phase 9: Launch (Week 12)

```
[ ] Production build
[ ] Upload to Play Store
[ ] Staged rollout (10% → 50% → 100%)
[ ] Monitor crash reports
[ ] Monitor reviews
[ ] Quick bug fixes if needed
[ ] Marketing push (social media, blogs)
```

---

## 🐛 KNOWN EDGE CASES & SOLUTIONS

```
EDGE CASE 1: VPN Conflicts with Other VPNs
Solution: Detect active VPN, offer "Companion Mode"
- Disable own VPN
- Use NetworkCallback for passive monitoring
- Warn user of limited detection capability

EDGE CASE 2: Android kills background service
Solution: Triple watchdog
- AlarmManager repeating check
- JobScheduler periodic job
- WorkManager backup

EDGE CASE 3: DNS-over-HTTPS (DoH) bypasses detection
Solution: Detect DoH usage
- Monitor connections to 1.1.1.1:443, 8.8.8.8:443
- Warn user: "DoH enabled, DNS monitoring limited"
- Fall back to HTTPS certificate validation

EDGE CASE 4: Root detection triggers SafetyNet
Solution: Make root optional
- Check for root at runtime
- Enable super-features only if root present
- Don't require root for core functionality

EDGE CASE 5: Samsung/Xiaomi/Huawei kill app
Solution: Vendor-specific workarounds
- Request battery optimization exemption
- Add to protected apps (MIUI)
- Use Samsung Knox API if available

EDGE CASE 6: User connected before app starts
Solution: Analyze on-connect
- Run 2-minute baseline immediately
- Compare with known profile (if exists)
- Warn if mismatches detected

EDGE CASE 7: Captive portal redirects break VPN
Solution: Detect captive portal
- Allow HTTP to captive portal IPs
- Pause analysis during portal auth
- Resume after auth complete

EDGE CASE 8: False positives annoy user
Solution: User feedback loop
- "Report False Positive" button
- Learn from reports
- Adjust algorithm confidence thresholds
```

---

## 📚 REQUIRED LIBRARIES (build.gradle.kts)

```kotlin
// app/build.gradle.kts

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")  // Kotlin Symbol Processing
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")  // Firebase
}

android {
    namespace = "com.gorinox.wifinetguard"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.gorinox.wifinetguard"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.20")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // AndroidX Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Jetpack Compose
    val composeBom = platform("androidx.compose:compose-bom:2024.01.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Hilt (Dependency Injection)
    implementation("com.google.dagger:hilt-android:2.50")
    ksp("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Room (Database)
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // DataStore (Preferences)
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Retrofit (API Client)
    val retrofitVersion = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Moshi (JSON)
    val moshiVersion = "1.15.0"
    implementation("com.squareup.moshi:moshi:$moshiVersion")
    implementation("com.squareup.moshi:moshi-kotlin:$moshiVersion")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")

    // WorkManager (Background Tasks)
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Google Play Billing
    implementation("com.android.billingclient:billing-ktx:6.1.0")

    // Jetpack Security (Encryption)
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Coil (Image Loading)
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Firebase (Crashlytics, Analytics)
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Timber (Logging)
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Accompanist (Compose utilities)
    val accompanistVersion = "0.32.0"
    implementation("com.google.accompanist:accompanist-permissions:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.mockk:mockk:1.13.8")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.12")
}
```

---

## 🎨 UI DESIGN SYSTEM (Theme.kt)

```kotlin
// ui/theme/Color.kt

val DeepBlue = Color(0xFF0A1F44)
val ElectricCyan = Color(0xFF00E5FF)
val NeonGreen = Color(0xFF00FF88)
val Amber = Color(0xFFFFA726)
val Crimson = Color(0xFFEF5350)
val NearBlack = Color(0xFF0D1117)
val DarkGray = Color(0xFF1C2128)
val White = Color(0xFFFFFFFF)

val LightColorScheme = lightColorScheme(
    primary = DeepBlue,
    secondary = ElectricCyan,
    tertiary = NeonGreen,
    error = Crimson,
    background = White,
    surface = Color(0xFFF5F5F5),
    onPrimary = White,
    onSecondary = NearBlack,
    onBackground = NearBlack,
    onSurface = NearBlack
)

val DarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    secondary = NeonGreen,
    tertiary = DeepBlue,
    error = Crimson,
    background = NearBlack,
    surface = DarkGray,
    onPrimary = NearBlack,
    onSecondary = NearBlack,
    onBackground = White,
    onSurface = White
)
```

```kotlin
// ui/theme/Type.kt

val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold)
)

val JetBrainsMono = FontFamily(
    Font(R.font.jetbrainsmono_regular, FontWeight.Normal)
)

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
)
```

---

## 🚀 FINAL NOTES FOR GEMINI CODE

### Critical Implementation Notes

```
1. VPN SERVICE IS THE HEART
   - MUST be stable and never crash
   - Use try-catch everywhere in packet handling
   - Graceful degradation if parsing fails

2. PERFORMANCE IS KEY
   - Process packets as fast as possible
   - Use ByteBuffer for zero-copy parsing
   - Batch database writes
   - Don't analyze every packet (filter first)

3. BATTERY LIFE
   - Smart power management
   - Reduce work when screen off
   - Use WorkManager for periodic tasks
   - Cancel tasks when VPN stops

4. USER PRIVACY
   - NEVER log actual traffic content
   - Only log metadata (IPs, domains, types)
   - Encrypt sensitive local data
   - Make privacy policy clear

5. FALSE POSITIVES
   - Start with conservative thresholds
   - Allow user feedback
   - Don't cry wolf (users will uninstall)

6. GOOGLE PLAY COMPLIANCE
   - Declare VPN usage clearly
   - Privacy policy MUST be present
   - Follow content rating guidelines
   - Don't make false security claims

7. ANDROID FRAGMENTATION
   - Test on Samsung, Xiaomi, Huawei
   - Handle vendor-specific bugs
   - Provide battery optimization exemption
   - Support Android 8.0+

8. BILLING
   - Test purchases in sandbox first
   - Handle edge cases (refunds, upgrades)
   - Validate receipts server-side (if possible)
   - Clear messaging about what's included

9. SECURITY
   - Use ProGuard/R8 obfuscation
   - Implement certificate pinning
   - Validate all inputs
   - Don't trust network data

10. USER EXPERIENCE
    - Onboarding is critical (explain VPN)
    - Show value immediately (threats blocked)
    - Make premium worth it
    - Respond to feedback quickly
```

---

## ✅ COMPLETION CRITERIA

```
MVP IS COMPLETE WHEN:

✅ VPN service starts/stops reliably
✅ Packets are captured and parsed
✅ At least 5 threat types detected correctly
✅ Threats logged to database
✅ User notified of critical threats
✅ UI shows protection status
✅ Threat history browsable
✅ Settings functional
✅ Premium purchase flow works
✅ App doesn't crash on any common device
✅ Battery drain <15%/hour with VPN on
✅ Network speed impact <5% latency
✅ Works on Android 8.0 through 14
✅ Passes MOBSF security scan
✅ Play Store listing ready
✅ Privacy policy published
✅ Alpha testing with 10+ users successful
```

---

**GEMINI CODE: THIS DOCUMENT CONTAINS EVERYTHING YOU NEED TO BUILD "WiFi Net Guard" FROM SCRATCH. FOLLOW THE ARCHITECTURE, USE THE SPECIFIED LIBRARIES, IMPLEMENT THE DETECTION ALGORITHMS, AND CREATE THE UI ACCORDING TO THE DESIGN SPECIFICATIONS. THE RESULT SHOULD BE A PRODUCTION-READY ANDROID APP THAT CAN BE PUBLISHED TO GOOGLE PLAY STORE.**

**GOOD LUCK! 🚀**

---

**Document Version:** 1.0  
**Last Updated:** May 20, 2026  
**Author:** Claude AI (with human guidance)  
**Company:** Gorinox  
**App Name:** WiFi Net Guard  
**Platform:** Android (Google Play Store)

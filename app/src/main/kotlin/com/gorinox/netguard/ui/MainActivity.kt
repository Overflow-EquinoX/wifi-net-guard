package com.gorinox.netguard.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorinox.netguard.data.*
import com.gorinox.netguard.service.WifiGuardService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// --- CUSTOM HARMONIOUS COLOR PALETTE ---
val SpaceDark = Color(0xFF0F1016)
val CardBackground = Color(0x1F22304A)
val NeonGreen = Color(0xFF00FF9D)
val ThreatRed = Color(0xFFFF4D6D)
val GlassWhite = Color(0x1AFFFFFF)
val MutedText = Color(0xFF8E9BAE)

class MainActivityViewModel(private val database: GorinoxDatabase) : ViewModel() {
    
    // Live stream of threat logs
    val threatLogs: StateFlow<List<ThreatLogEntity>> = database.threatLogDao().getAllLogsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Stream of daily statistics
    val dailyStats: StateFlow<List<DailyStatsEntity>> = database.dailyStatsDao().getAllStatsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Stream of all WiFi Profiles
    val wifiProfiles: StateFlow<List<WiFiProfileEntity>> = database.wifiProfileDao().getAllProfilesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun clearAllData() {
        viewModelScope.launch {
            database.clearAllTables()
        }
    }
}

class MainActivity : AppCompatActivity() {

    private lateinit var database: GorinoxDatabase
    private lateinit var viewModel: MainActivityViewModel

    // Permission Launcher
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        
        if (fineLocationGranted || coarseLocationGranted) {
            startWifiGuardService()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        database = GorinoxDatabase.getDatabase(applicationContext)
        viewModel = MainActivityViewModel(database)

        checkPermissionsAndStartService()

        setContent {
            MaterialTheme {
                GorinoxAppScreen(viewModel, ::startWifiGuardService, ::stopWifiGuardService)
            }
        }
    }

    private fun checkPermissionsAndStartService() {
        val permissionsToRequest = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val allGranted = permissionsToRequest.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (allGranted) {
            startWifiGuardService()
        } else {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private fun startWifiGuardService() {
        val intent = Intent(this, WifiGuardService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }

    private fun stopWifiGuardService() {
        val intent = Intent(this, WifiGuardService::class.java)
        stopService(intent)
    }
}

@Composable
fun GorinoxAppScreen(
    viewModel: MainActivityViewModel,
    onStartService: () -> Unit,
    onStopService: () -> Unit
) {
    val context = LocalContext.current
    var isServiceRunning by remember { mutableStateOf(true) }
    
    val threatLogs by viewModel.threatLogs.collectAsState()
    val dailyStats by viewModel.dailyStats.collectAsState()
    val wifiProfiles by viewModel.wifiProfiles.collectAsState()

    // Determine current health based on active unresolved threats
    val hasActiveThreat = threatLogs.any { !it.resolved && it.level >= 3 }
    val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val todayStats = dailyStats.find { it.date == todayDate }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceDark)
    ) {
        // --- Sleek Gradient Background Glow ---
        val ambientColor = if (hasActiveThreat) ThreatRed else NeonGreen
        Box(
            modifier = Modifier
                .size(350.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-80).dp)
                .blur(90.dp)
                .background(Brush.radialGradient(listOf(ambientColor.copy(alpha = 0.2f), Color.Transparent)))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 40.dp)
        ) {
            // --- HEADER ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "WIFI NET GUARD",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "AKILLI GÜVENLİK DUVARI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedText,
                        letterSpacing = 1.sp
                    )
                }

                // Start/Stop Switch with Sleek Design
                Button(
                    onClick = {
                        if (isServiceRunning) {
                            onStopService()
                            isServiceRunning = false
                        } else {
                            onStartService()
                            isServiceRunning = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isServiceRunning) CardBackground else NeonGreen.copy(alpha = 0.8f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isServiceRunning) "Korumayı Durdur" else "Korumayı Başlat",
                        fontSize = 11.sp,
                        color = if (isServiceRunning) Color.White else SpaceDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // --- CENTRAL RADAR ORB (Premium Visual Wow Element) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background ripple pulse animation
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val scaleFactor by infiniteTransition.animateFloat(
                    initialValue = 0.7f,
                    targetValue = 1.3f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2200, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "scale"
                )
                val alphaFactor by infiniteTransition.animateFloat(
                    initialValue = 0.6f,
                    targetValue = 0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2200, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "alpha"
                )

                // Pulsing Ring
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .scale(scaleFactor)
                        .border(
                            width = 2.dp,
                            color = ambientColor.copy(alpha = alphaFactor),
                            shape = CircleShape
                        )
                )

                // Inner Main Shield Orb
                Box(
                    modifier = Modifier
                        .size(125.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(ambientColor.copy(alpha = 0.8f), ambientColor.copy(alpha = 0.2f))
                            )
                        )
                        .border(1.dp, ambientColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (hasActiveThreat) Icons.Default.Warning else Icons.Default.Shield,
                            contentDescription = "Security Status",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (hasActiveThreat) "RİSK VAR!" else "GÜVENDESİNİZ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- METRICS GRID (Glassmorphism design) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Engellenen Reklam",
                    value = "${todayStats?.blockedAdsCount ?: 0}",
                    icon = Icons.Default.Block,
                    accentColor = NeonGreen
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Tehdit Engellendi",
                    value = "${todayStats?.threatsBlockedCount ?: 0}",
                    icon = Icons.Default.Security,
                    accentColor = ThreatRed
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- BOTTOM DRAWER: LOGS OR NETWORK LISTS ---
            Text(
                text = "GÜVENLİK DUVARI ETKİNLİKLERİ",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MutedText,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (threatLogs.isEmpty()) {
                // Quiet Mode Placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardBackground)
                        .border(1.dp, GlassWhite, RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Safe Checkmark",
                            tint = NeonGreen,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ağ trafiği sakin ve güvende.",
                            fontSize = 13.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Wifi Net Guard sessizce arka planda gözlemliyor.",
                            fontSize = 11.sp,
                            color = MutedText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // List of Threat Logs
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(threatLogs) { log ->
                        ThreatLogItem(log)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MetricCard(
    modifier = ModifierModifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .border(1.dp, GlassWhite, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = MutedText,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun ThreatLogItem(log: ThreatLogEntity) {
    val accentColor = if (log.level >= 3) ThreatRed else Color(0xFFFFB703)
    val timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(log.timestamp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackground)
            .border(1.dp, GlassWhite, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Threat Indicator Dot
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(accentColor, CircleShape)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = log.threatType + " - " + log.ssid,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = log.description,
                fontSize = 11.sp,
                color = MutedText
            )
        }

        Text(
            text = timeString,
            fontSize = 11.sp,
            color = MutedText,
            fontWeight = FontWeight.Bold
        )
    }
}

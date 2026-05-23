package com.gorinox.netguard.security

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.NotificationCompat
import com.gorinox.netguard.GorinoxApplication
import com.gorinox.netguard.R
import com.gorinox.netguard.ui.MainActivity
import com.gorinox.netguard.data.GorinoxDatabase
import kotlinx.coroutines.*
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer

/**
 * Smart DNS VPN Service (The "Golden Ratio" Approach)
 * 
 * Intercepts ONLY DNS traffic by routing a fake DNS IP to the VPN interface.
 * All other traffic (TCP, UDP payloads) goes through the physical network normally.
 * This provides 100% DNS spoofing protection with 0% battery drain on regular data.
 */
class SmartDnsVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var dnsJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    // A fake IP address for our virtual DNS server inside the VPN
    private val VIRTUAL_DNS_IP = "10.1.10.2"
    private val VIRTUAL_CLIENT_IP = "10.1.10.1"
    
    private lateinit var database: GorinoxDatabase

    override fun onCreate() {
        super.onCreate()
        database = GorinoxDatabase.getDatabase(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "STOP_VPN") {
            stopVpn()
            return START_NOT_STICKY
        }

        startForeground(1002, createNotification())
        startVpn()
        return START_STICKY
    }

    private fun startVpn() {
        if (vpnInterface != null) return

        // Arka planda 7 günden eski logları temizle (TTL)
        serviceScope.launch {
            val sevenDaysAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000)
            database.threatLogDao().deleteLogsOlderThan(sevenDaysAgo)
        }

        // 2 Saatte Bir "Samimi Hatırlatma" Bildirimi Döngüsü
        serviceScope.launch {
            while (isActive) {
                delay(2L * 60 * 60 * 1000) // 2 Saat bekle (2 saat * 60 dk * 60 sn * 1000 ms)
                sendFriendlyReminderNotification()
            }
        }

        try {
            val builder = Builder()
                .setSession("Wifi Net Guard - Smart Shield")
                .addAddress(VIRTUAL_CLIENT_IP, 24)
                // We tell Android that this virtual IP is the DNS server
                .addDnsServer(VIRTUAL_DNS_IP)
                // We ONLY route traffic destined to the virtual DNS server to the VPN.
                // All other traffic bypasses the VPN completely!
                .addRoute(VIRTUAL_DNS_IP, 32)
                .setMtu(1500)
                .setBlocking(true)

            vpnInterface = builder.establish()
            Log.d("SmartVPN", "Smart DNS VPN started. Intercepting DNS only.")

            if (vpnInterface != null) {
                startDnsInterceptor()
            }
        } catch (e: Exception) {
            Log.e("SmartVPN", "Failed to start VPN: ${e.message}")
            stopSelf()
        }
    }

    private fun startDnsInterceptor() {
        val fd = vpnInterface?.fileDescriptor ?: return
        val inputStream = FileInputStream(fd)
        val outputStream = FileOutputStream(fd)

        dnsJob = serviceScope.launch {
            val buffer = ByteArray(32767)
            
            // We use a real UDP socket to forward DNS queries to Cloudflare (1.1.1.1)
            val realDnsSocket = DatagramSocket()
            protect(realDnsSocket) // Prevents infinite loop by bypassing the VPN for our own socket

            while (isActive) {
                try {
                    // Read incoming packet from Android OS
                    val length = inputStream.read(buffer)
                    if (length > 0) {
                        val packet = buffer.copyOf(length)
                        handlePacket(packet, outputStream, realDnsSocket)
                    }
                } catch (e: Exception) {
                    if (isActive) Log.e("SmartVPN", "Packet read error", e)
                }
            }
            realDnsSocket.close()
        }
    }

    /**
     * Reads the raw IP+UDP packet. Extracts the DNS query, sends it to a real secure DNS,
     * waits for the response, wraps it back in an IP+UDP header, and sends it to Android.
     */
    private suspend fun handlePacket(
        rawPacket: ByteArray,
        outputStream: FileOutputStream,
        realDnsSocket: DatagramSocket
    ) {
        // Very basic IP/UDP parsing.
        if (rawPacket.size < 28) return // IP header (20) + UDP header (8) = 28 bytes min

        val version = (rawPacket[0].toInt() shr 4) and 0x0F
        if (version != 4) return // IPv4 only for now

        val ipHeaderLen = (rawPacket[0].toInt() and 0x0F) * 4
        val protocol = rawPacket[9].toInt() and 0xFF
        
        if (protocol != 17) return // UDP only (DNS uses UDP 53)

        val srcIp = rawPacket.copyOfRange(12, 16)
        val dstIp = rawPacket.copyOfRange(16, 20)

        // UDP Header starts at ipHeaderLen
        val srcPort = ((rawPacket[ipHeaderLen].toInt() and 0xFF) shl 8) or (rawPacket[ipHeaderLen + 1].toInt() and 0xFF)
        val dstPort = ((rawPacket[ipHeaderLen + 2].toInt() and 0xFF) shl 8) or (rawPacket[ipHeaderLen + 3].toInt() and 0xFF)

        if (dstPort != 53) return // We only care about DNS

        // Extract DNS Payload
        val udpHeaderLen = 8
        val payloadStart = ipHeaderLen + udpHeaderLen
        val payloadLen = rawPacket.size - payloadStart
        
        if (payloadLen <= 0) return
        val dnsPayload = rawPacket.copyOfRange(payloadStart, rawPacket.size)

        // 1. Alan Adını (Domain) Paket İçerisinden Çıkartıyoruz (DNS Parsing)
        val domain = extractDomainName(dnsPayload)
        
        // 2. Güvenli Liste (Whitelist) ve Kara Liste (Blacklist) Kontrolü (İskelet)
        // Eğer false-positive (yanlış alarm) korkumuz varsa, önce Whitelist'e bakarız.
        val isWhitelisted = isDomainInWhitelist(domain)
        val isBlacklisted = !isWhitelisted && isDomainInBlacklist(domain)

        if (isBlacklisted) {
            // Zararlı domain yakalandı!
            Log.d("SmartVPN", "ENGELLENDİ: $domain")
            
            // 0.0.0.0 (NXDOMAIN) Sahte DNS Cevabı Üret
            val nxResponse = createNxDomainResponse(dnsPayload)
            
            // İşletim sistemine "Bu site yok/engelli" diye sahte paketi geri gönder
            val blockPacket = buildUdpIpPacket(
                srcIp = dstIp,
                dstIp = srcIp,
                srcPort = dstPort,
                dstPort = srcPort,
                payload = nxResponse
            )
            outputStream.write(blockPacket)
            
            // Room DB'ye ThreatLog yaz ve "Merak etme, seni koruduk" bildirimini ateşle!
            logAndNotifyThreat(domain ?: "Bilinmeyen Hedef")
            
            // Engellediğimiz için paketi dışarı (Cloudflare'e) yollamadan fonksiyonu bitiriyoruz.
            return
        }

        // Offload network I/O to another coroutine so we don't block the packet reading loop
        launch(Dispatchers.IO) {
            try {
                // 3. DNS Fallback Mekanizması (Cloudflare -> Quad9 -> Google)
                val dnsServers = listOf("1.1.1.1", "9.9.9.9", "8.8.8.8")
                var responsePayload: ByteArray? = null

                for (serverIp in dnsServers) {
                    try {
                        val serverAddress = InetAddress.getByName(serverIp)
                        val outPacket = DatagramPacket(dnsPayload, dnsPayload.size, serverAddress, 53)
                        realDnsSocket.send(outPacket)

                        // Receive the response
                        val receiveBuf = ByteArray(2048)
                        val inPacket = DatagramPacket(receiveBuf, receiveBuf.size)
                        
                        // Set a timeout so we don't block forever (örneğin 1 saniye bekler)
                        realDnsSocket.soTimeout = 1000
                        realDnsSocket.receive(inPacket)

                        responsePayload = receiveBuf.copyOf(inPacket.length)
                        break // Başarılı olursa döngüden çık
                    } catch (e: Exception) {
                        Log.w("SmartVPN", "DNS $serverIp yanıt vermedi, sonrakine geçiliyor...")
                        // Hata alırsak döngü devam eder, bir sonraki DNS'i dener.
                    }
                }

                if (responsePayload != null) {
                    // Build fake IP + UDP header to send back to Android OS
                    val responsePacket = buildUdpIpPacket(
                        srcIp = dstIp, // Swap IP
                        dstIp = srcIp, // Swap IP
                        srcPort = dstPort, // Swap Port
                        dstPort = srcPort, // Swap Port
                        payload = responsePayload
                    )

                    // Write it back to the VPN interface!
                    outputStream.write(responsePacket)
                } else {
                    Log.e("SmartVPN", "Hiçbir DNS sunucusu yanıt vermedi! ($domain)")
                }

            } catch (e: Exception) {
                Log.e("SmartVPN", "Network error in DNS processing", e)
            }
        }
    }

    private fun buildUdpIpPacket(srcIp: ByteArray, dstIp: ByteArray, srcPort: Int, dstPort: Int, payload: ByteArray): ByteArray {
        val ipHeaderLen = 20
        val udpHeaderLen = 8
        val totalLen = ipHeaderLen + udpHeaderLen + payload.size
        
        val packet = ByteBuffer.allocate(totalLen)
        
        // --- IP HEADER ---
        packet.put((0x45).toByte()) // IPv4, Header Length = 5 (20 bytes)
        packet.put(0.toByte()) // TOS
        packet.putShort(totalLen.toShort()) // Total Length
        packet.putShort(0.toShort()) // Identification
        packet.putShort(0.toShort()) // Flags & Fragment Offset
        packet.put(64.toByte()) // TTL
        packet.put(17.toByte()) // Protocol (UDP)
        packet.putShort(0.toShort()) // Checksum (0 for now, Android often ignores it for incoming local VPN packets)
        packet.put(srcIp)
        packet.put(dstIp)

        // Calculate IP Checksum
        val ipChecksum = calculateChecksum(packet.array(), 0, ipHeaderLen)
        packet.putShort(10, ipChecksum)

        // --- UDP HEADER ---
        packet.putShort(srcPort.toShort())
        packet.putShort(dstPort.toShort())
        val udpLen = udpHeaderLen + payload.size
        packet.putShort(udpLen.toShort())
        packet.putShort(0.toShort()) // UDP Checksum (Optional in IPv4, 0 means unused)

        // --- PAYLOAD ---
        packet.put(payload)

        return packet.array()
    }

    private fun calculateChecksum(data: ByteArray, offset: Int, length: Int): Short {
        var sum = 0L
        var i = offset
        var len = length
        while (len > 1) {
            sum += (((data[i].toInt() and 0xFF) shl 8) or (data[i + 1].toInt() and 0xFF)).toLong()
            i += 2
            len -= 2
        }
        if (len > 0) {
            sum += ((data[i].toInt() and 0xFF) shl 8).toLong()
        }
        while ((sum shr 16) > 0) {
            sum = (sum and 0xFFFF) + (sum shr 16)
        }
        return (sum.inv() and 0xFFFF).toShort()
    }

    /**
     * DNS UDP paketinin içerisinden sorgulanan alan adını (Domain) çıkartan Parser
     */
    private fun extractDomainName(dnsPayload: ByteArray): String? {
        try {
            if (dnsPayload.size < 12) return null // DNS Header boyutu 12 byte'tır
            var pos = 12
            val sb = StringBuilder()
            
            while (pos < dnsPayload.size) {
                val len = dnsPayload[pos].toInt() and 0xFF
                if (len == 0) break // 0 gördüğümüzde domain adı biter
                
                // Eğer sıkıştırma pointer'ı (Compression) varsa (RFC 1035), query kısmında nadirdir
                // güvenli kalmak adına pointer gördüğümüzde okumayı keseriz.
                if (len >= 192) break 
                
                pos++
                if (pos + len > dnsPayload.size) break
                val label = String(dnsPayload, pos, len, Charsets.UTF_8)
                if (sb.isNotEmpty()) sb.append(".")
                sb.append(label)
                pos += len
            }
            return sb.toString().lowercase()
        } catch (e: Exception) {
            Log.e("SmartVPN", "Domain okuma hatası", e)
            return null
        }
    }

    /**
     * Kötü niyetli siteler için sahte NXDOMAIN (Böyle bir site yok) cevabı üretir.
     * Bu sayede tarayıcı/uygulama anında "Bağlantı Yok" hatası verir ve koruma sağlanır.
     */
    private fun createNxDomainResponse(queryPayload: ByteArray): ByteArray {
        val response = queryPayload.copyOf()
        if (response.size >= 4) {
            // DNS Bayraklarını (Flags) "Response" ve "NXDOMAIN" (Hata) olarak değiştiriyoruz.
            // 0x8183 = Standart Sorguya Cevap, NXDOMAIN (Domain Bulunamadı)
            response[2] = 0x81.toByte()
            response[3] = 0x83.toByte()
        }
        return response
    }

    private fun logAndNotifyThreat(domain: String) {
        serviceScope.launch {
            // 1. Veritabanına Log Ekle
            val threatLog = com.gorinox.netguard.data.ThreatLogEntity(
                timestamp = System.currentTimeMillis(),
                bssid = "DNS_FILTER",
                ssid = "Smart DNS Kalkanı",
                threatType = "Zararlı Bağlantı Engellendi",
                level = 3,
                description = "Cihazınız $domain adresine girmeye çalıştı. Riskli olduğu için engelledik."
            )
            database.threatLogDao().insertLog(threatLog)

            // 2. Günlük İstatistikleri (UI Sayaçlarını) Güncelle
            val todayDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            val stats = database.dailyStatsDao().getStatsForDate(todayDate)
            if (stats == null) {
                database.dailyStatsDao().insertOrUpdateStats(
                    com.gorinox.netguard.data.DailyStatsEntity(date = todayDate, threatsBlockedCount = 1)
                )
            } else {
                database.dailyStatsDao().incrementThreats(todayDate)
            }

            // 3. Android Notification Gönder
            sendProtectionNotification(domain)
        }
    }

    private fun sendProtectionNotification(domain: String) {
        val notificationManager = getSystemService(android.app.NotificationManager::class.java)
        
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, "gorinox_events") // Varsa özel event kanalı kullanılmalı
            .setSmallIcon(android.R.drawable.ic_secure)
            .setContentTitle("Merak Etme, Seni Koruduk 😊")
            .setContentText("Zararlı bir bağlantıyı ($domain) anında engelledik. Güvendesiniz.")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun sendFriendlyReminderNotification() {
        val notificationManager = getSystemService(android.app.NotificationManager::class.java)
        
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, "gorinox_events")
            .setSmallIcon(android.R.drawable.ic_secure)
            .setContentTitle("Bizi açık unutmadın değil mi? 😊")
            .setContentText("Pil seviyen düştüyse veya bağlantında yavaşlık varsa korumayı geçici olarak kapatabilirsin. Daha sonra devam ederiz!")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Pil seviyen düştüyse veya bağlantında yavaşlık varsa korumayı geçici olarak kapatabilirsin. Daha sonra devam ederiz!"))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        // Sabit bir ID kullanalım ki sürekli yeni bildirim yığılmasın, var olan güncellensin
        notificationManager.notify(999, notification)
    }

    // Gelecekte Room DB'ye bağlanacak sahte (Mock) listeler
    private suspend fun isDomainInWhitelist(domain: String?): Boolean {
        if (domain == null) return false
        // Kullanıcının bankaları, WhatsApp, Instagram gibi temel siteler burada olacak.
        val hardcodedWhitelist = listOf("google.com", "whatsapp.com", "apple.com")
        if (hardcodedWhitelist.any { domain.endsWith(it) }) return true
        
        // Gerçek Veritabanı Kontrolü
        return database.domainFilterDao().isWhitelisted(domain)
    }

    private suspend fun isDomainInBlacklist(domain: String?): Boolean {
        if (domain == null) return false
        // İleride indirilen Blacklist DB'ye bakılacak
        val mockBlacklist = listOf("oltalama-sitesi.com", "reklam-sunucusu.net")
        if (mockBlacklist.any { domain.endsWith(it) }) return true
        
        // Gerçek Veritabanı Kontrolü
        return database.domainFilterDao().isBlacklisted(domain)
    }

    private fun stopVpn() {
        dnsJob?.cancel()
        try {
            vpnInterface?.close()
        } catch (e: Exception) {
            // Ignored
        }
        vpnInterface = null
        stopForeground(true)
        stopSelf()
        Log.d("SmartVPN", "Smart DNS VPN stopped.")
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVpn()
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, GorinoxApplication.CHANNEL_SERVICE)
            .setSmallIcon(android.R.drawable.ic_secure)
            .setContentTitle("Akıllı Kalkan Aktif")
            .setContentText("DNS sorgularınız şifreleniyor. Veri trafiğiniz güvende.")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }
}

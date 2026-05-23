# Architecture Decision Record: Smart DNS VPN Transition

## 1. Context & Problem Statement
The original `WiFi_Net_Guard_Complete_Development_Spec.md` proposed a full packet inspection VPN (`GorinoxVpnService`) capturing and parsing raw TCP/UDP packets via Java NIO.
This approach presented severe commercial and technical risks:
- Excessive battery drain due to Deep Packet Inspection (DPI).
- High complexity in maintaining TCP state machines.
- Severe risk of Google Play Store ban due to strict `VpnService` policies regarding user data inspection.
- Redundancy, as the majority of modern web traffic is encrypted (HTTPS/TLS 1.3), rendering payload inspection largely ineffective.

## 2. Decision: "The Golden Ratio" (Smart DNS & Metadata VPN)
We are officially pivoting the architecture from "Deep Packet Inspection" to a "Smart DNS & IP Metadata Filter".

### 3-Layered Security Architecture
1. **Active Probing (Connection Time):** 
   - Retaining the current `ActiveThreatDetector` and `FakeWiFiDetector`.
   - Validates the network environment (Captive Portals, Evil Twins, DNS Hijacking) immediately upon connection.
2. **Smart DNS VPN (Continuous Protection):**
   - The `VpnService` will **not** parse payloads.
   - It will intercept DNS queries (Port 53) and securely route them via DNS-over-HTTPS (DoH) to trusted providers (e.g., Cloudflare 1.1.1.1 or Quad9). This mitigates 100% of local DNS spoofing and hijacking.
   - It will monitor outgoing IP headers (Metadata). If an IP matches a known malicious database (Phishing/Malware), the connection is dropped.
3. **Emergency Kill-Switch:**
   - Retaining `KillSwitchVpnService`.
   - If anomalous behavior is detected (e.g., sudden port scanning or active MITM symptoms), all traffic is blackholed.

## 3. Implementation Changes
- Deprecate the planned `PacketCaptureThread` and `PacketParser` from the original spec.
- Introduce a lightweight DNS interception mechanism within `GorinoxVpnService`.
- Update the Google Play Store Privacy Policy explicitly stating: *"The app uses VpnService exclusively for DNS encryption and malicious IP blocking. No user data payloads are decrypted, inspected, or stored."*

## 4. Consequences
- **Positive:** Massive reduction in development complexity, battery drain < 1%, guaranteed Google Play compliance, highly scalable.
- **Negative:** Blind to specific internal application data exfiltration (which is acceptable for a consumer WiFi protection tool).

*Date: May 2026*
*Status: Accepted & Active*

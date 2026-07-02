# WebSocket (Gerçek Zamanlı Konum) Stratejisi

Bu doküman, `openapi.json` içerisindeki `/admin/locations` notuna dayanılarak, araçların anlık konumlarının harita üzerinde gösterilmesi (Tracking) için kullanılacak WebSocket entegrasyon planını tanımlar.

## 1. Genel Bakış

REST API tarafındaki `GET /admin/locations` endpoint'i sadece o anlık (snapshot) konumları test/eğitim amaçlı dönmektedir. Hareket halindeki araçların konumlarını canlı izlemek için WebSocket (WS) altyapısı kullanılacaktır. Backend'den gönderilen veri modeli REST'teki `VehiclePositionDto` ile birebir aynıdır.

## 2. API ve Veri Modeli

**Beklenen WebSocket JSON Yükü (Payload):**
```json
{
  "vehicleId": "clx0veh1234567890",
  "plate": "34 ABC 123",
  "status": "AVAILABLE",
  "latitude": 41.0151,
  "longitude": 28.9795,
  "updatedAt": "2026-06-30T12:00:01.000Z"
}
```
*(Not: Sunucu duruma göre bu veriyi tek bir JSON nesnesi veya liste `[]` olarak yayınlayabilir (push). Uygulamanın parse işlemi her ikisine de esnek olmalıdır.)*

## 3. İstemci (Android) Entegrasyonu

### 3.1. Kütüphane Seçimi
WebSocket bağlantısı yönetimi için uygulamanın ana network bağımlılığı olan **OkHttp WebSocket** (veya backend Socket.IO tabanlı ise ilgili Socket.IO istemcisi) kullanılacaktır.

### 3.2. Bağlantı Yaşam Döngüsü (Lifecycle)
1. **Bağlanma (Connect):** Kullanıcı "Harita/Konum Takibi" sayfasına veya aktif kiralama takip paneline girdiğinde (Composable `LaunchedEffect` tetiklendiğinde) WebSocket bağlantısı açılır.
2. **Sonlandırma (Disconnect):** Kullanıcı o sayfadan ayrıldığında (Composable `onDispose` anında), ağ ve pil tüketimini engellemek için soket derhal kapatılır.
3. **Reconnection (Yeniden Bağlanma):** İnternet anlık koptuğunda exponantial backoff (artan bekleme süresiyle tekrar bağlanma denemesi) stratejisi uygulanır.

### 3.3. Compose UI ve State (Durum) Yönetimi (Kotlin Flow)
Soketten gelen ham string mesajlar `kotlinx.serialization` (veya ilgili parser) kullanılarak `VehiclePositionDto` modeline dönüştürülür ve Data katmanından Domain Modeline (örn: `VehicleLocation`) aktarılır.

ViewModel içerisinde bu veri akışı bir `StateFlow`'da tutulur ve Harita UI'ı bu Flow'u dinleyerek pinleri (marker) günceller:

```kotlin
// Örnek Akış (ViewModel)
private val _vehicleLocations = MutableStateFlow<Map<String, VehicleLocation>>(emptyMap())
val vehicleLocations = _vehicleLocations.asStateFlow()

fun onLocationUpdateReceived(position: VehiclePositionDto) {
    _vehicleLocations.update { currentMap ->
        // ID'ye göre eski konumu ezer, yeni konumu yazar (Harita üzerindeki pini hareket ettirir)
        currentMap + (position.vehicleId to position.toDomain())
    }
}
```

### 3.4. Token (Güvenlik) Gönderimi
WebSocket bağlantısı için yetkilendirme gerekir. Bağlantı açılırken, korumalı erişim sağlamak adına `Authorization: Bearer <accessToken>` HTTP başlığına (veya sunucu yapısına göre query parametresine örn: `ws://domain/locations?token=...`) aktif `accessToken` dahil edilmelidir. Eğer bağlantı esnasında 401 alınırsa Token Refresh akışı tetiklenmelidir.

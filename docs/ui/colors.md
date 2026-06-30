# RenCar Android Renk Paleti (Colors) Kılavuzu

Bu doküman, RenCar Android mobil uygulamasında kullanılan renk sistemi kurallarını ve Jetpack Compose renk şemalarını tanımlar. Tasarım belgesinde (`rencar.pdf`) yer alan Açık (Light) ve Koyu (Dark) tema görsel standartları esas alınmıştır.

---

## 1. Temel Renk Rolleri ve HSL/Hex Değerleri

Uygulamanın ana marka kimliği, dinamik mobiliteyi ve güveni simgeleyen **Mavi (Turkcell Blue / RenCar Blue)** tonu etrafında şekillenir.

### 1.1. Marka ve Birincil Renkler (Primary & Accent)
- **Primary (RenCar Blue):** `#0066FF`
  - *Kullanım:* Ana butonlar ("Hemen Başla", "Kilidi Aç", "Rezervasyon Tamamla"), haritadaki aktif butonlar, seçili filtre sekmeleri ve onay simgeleri.
- **Primary Container:** `#E6F0FF` (Light) / `#002D80` (Dark)
  - *Kullanım:* Seçili filtre butonları arka planı, hafif vurgular.
- **Secondary (İkincil):** `#4B5563` (Neutral Gray)
  - *Kullanım:* Yardımcı butonlar ("Rezerve Et" çerçevesi), pasif sekmeler.

### 1.2. Durum ve Bildirim Renkleri (Semantic Colors)
- **Success (Yeşil):** `#10B981` (MÜSAİT, Yüklendi, Onaylı durumları)
- **Success Container:** `#D1FAE5` (Light) / `#064E3B` (Dark)
- **Warning (Turuncu/Sarı):** `#F59E0B` (Hasar fotoğrafları bilgilendirme kutusu)
- **Warning Container:** `#FEF3C7` (Light) / `#78350F` (Dark)
- **Error (Kırmızı):** `#EF4444` (Ehliyet reddi, "Kiralamayı Bitir" butonu, API hata diyalogları)
- **Error Container:** `#FEE2E2` (Light) / `#7F1D1D` (Dark)

### 1.3. Nötr Renkler (Neutral / Surface & Background)
- **Light Theme Background:** `#F8F9FA` (Uygulama arka planı, açık gri/beyaz)
- **Light Theme Surface:** `#FFFFFF` (Araç detay bottom sheet arka planı, araç kartları)
- **Dark Theme Background:** `#0F172A` (Koyu lacivert/siyah, Slate 900)
- **Dark Theme Surface:** `#1E293B` (Koyu kartlar, bottom sheet gövdesi, Slate 800)

---

## 2. Jetpack Compose Renk Şeması Yapısı

Aşağıdaki kod şeması, projede `RencarTheme` altında renklerin nasıl tanımlanacağını gösterir.

### 2.1. Light ve Dark Color Schemes

```kotlin
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Ham Renk Tanımlamaları
val RencarBlue = Color(0xFF0066FF)
val RencarBlueContainerLight = Color(0xFFE6F0FF)
val RencarBlueContainerDark = Color(0xFF002D80)

val SuccessGreen = Color(0xFF10B981)
val SuccessGreenContainerLight = Color(0xFFD1FAE5)
val SuccessGreenContainerDark = Color(0xFF064E3B)

val WarningOrange = Color(0xFFF59E0B)
val WarningOrangeContainerLight = Color(0xFFFEF3C7)

val ErrorRed = Color(0xFFEF4444)
val ErrorRedContainerLight = Color(0xFFFEE2E2)
val ErrorRedContainerDark = Color(0xFF7F1D1D)

val LightBackground = Color(0xFFF8F9FA)
val LightSurface = Color(0xFFFFFFFF)
val DarkBackground = Color(0xFF0F172A)
val DarkSurface = Color(0xFF1E293B)

// Jetpack Compose Renk Şemaları
val LightColorScheme = lightColorScheme(
    primary = RencarBlue,
    onPrimary = Color.White,
    primaryContainer = RencarBlueContainerLight,
    onPrimaryContainer = RencarBlue,
    secondary = Color(0xFF4B5563),
    onSecondary = Color.White,
    background = LightBackground,
    onBackground = Color(0xFF1E293B),
    surface = LightSurface,
    onSurface = Color(0xFF1E293B),
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorRedContainerLight,
    onErrorContainer = ErrorRed
)

val DarkColorScheme = darkColorScheme(
    primary = RencarBlue,
    onPrimary = Color.White,
    primaryContainer = RencarBlueContainerDark,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF9CA3AF),
    onSecondary = Color.Black,
    background = DarkBackground,
    onBackground = Color(0xFFF8FAFC),
    surface = DarkSurface,
    onSurface = Color(0xFFF8FAFC),
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorRedContainerDark,
    onErrorContainer = Color.White
)
```

---

## 3. Renk Kullanım Kuralları (Açık ve Koyu Tema Uyumu)

1. **Sert Beyazlardan Kaçınma:** Koyu modda (`Dark Mode`) arka plan rengi olarak saf siyah (`#000000`) yerine gözü yormayan Slate 900 (`#0F172A`) tonu kullanılacaktır.
2. **Durum Kartları Kontrastı:** "MÜSAİT" veya "Yüklendi" etiketleri, açık temada yeşil zemin üzerine yeşil metin (`#10B981` zemin `#064E3B` metin gibi) şeklinde olmalı, böylece okunabilirlik artırılmalıdır.
3. **Buton Pasiflik Durumları:** API doğrulamaları (Örn: Ehliyet fotoğraflarının eksik olması) sırasında pasif kalan butonlar için gri tonları tercih edilmeli ve bu tonların kontrastı TalkBack tarafından okunabilecek alt sınırda tutulmalıdır.
4. **Harita Tasarımı:** Google Maps / Mapbox entegrasyonunda harita stili, sistem temasına bağlı olarak dinamik olarak değiştirilmelidir. Koyu temaya geçildiğinde harita JSON stili otomatik olarak karanlık moda güncellenecektir.

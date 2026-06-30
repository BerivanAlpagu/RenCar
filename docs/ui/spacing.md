# RenCar Android Boşluk ve Düzen (Spacing & Layout) Kılavuzu

Bu doküman, RenCar Android uygulamasındaki boşluk (padding/margin), yerleşim düzeni (layout grid) ve köşe yuvarlatma (corner radius) standartlarını tanımlar. Tasarım belgesinde (`rencar.pdf`) yer alan görsel yerleşimler esas alınmıştır.

---

## 1. 8dp Grid Sistemi ve Spacing Tokens

RenCar Android uygulamasında tutarlı bir dikey ve yatay ritim sağlamak amacıyla **8dp tabanlı grid sistemi** kullanılacaktır. Tüm boşluklar 8'in katları (ve istisnai durumlarda 4'ün katları) şeklinde tanımlanır.

| Token Adı | Değer (dp) | Tipik Kullanım Alanları |
| :--- | :--- | :--- |
| `space_xxs` | 2 dp | İnce ayarlar, çizgi kalınlıkları |
| `space_xs` | 4 dp | OTP giriş kutuları arası boşluklar, ikon-metin yakınlığı |
| `space_s` | 8 dp | Küçük dikey boşluklar, alt başlık-gövde ilişkileri |
| `space_m` | 12 dp | Liste elemanlarının kendi içlerindeki yakınlıkları |
| `space_l` | 16 dp | Standart ekran kenar boşlukları (safe-area margin), kart içi padding |
| `space_xl` | 24 dp | Ekran başlığı ile içerik arası boşluklar, büyük butonların dikey mesafeleri |
| `space_xxl` | 32 dp | Splash ekranı logo yerleşimleri, ekran bölümleri arası geniş mesafeler |
| `space_xxxl` | 48 dp | Çok büyük dikey boşluklar, ekran sonu koruma alanları |

---

## 2. Jetpack Compose Spacing Tanımlaması

Boşlukların kod içinde sihirli sayılar (`hardcoded values`) olarak kullanılmasını önlemek için bir `Spacing` veri sınıfı (`CompositionLocal` ile beslenen) oluşturulacaktır.

```kotlin
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class RencarSpacing(
    val default: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 12.dp,
    val large: Dp = 16.dp,
    val extraLarge: Dp = 24.dp,
    val doubleExtraLarge: Dp = 32.dp,
    val tripleExtraLarge: Dp = 48.dp
)

val LocalRencarSpacing = staticCompositionLocalOf { RencarSpacing() }
```

Kullanım Örneği:
```kotlin
Modifier.padding(LocalRencarSpacing.current.large)
```

---

## 3. Köşe Yuvarlatma (Corner Radius / Shapes)

RenCar tasarımlarında modern ve yumuşak köşeli kart yapıları tercih edilmiştir. Bu yapılar Material 3 `Shapes` sistemiyle yönetilir.

| Şekil Türü | Yuvarlatma (Radius) | Örnek Bileşenler |
| :--- | :--- | :--- |
| **Small Shape** | 8 dp | OTP Giriş Kutuları, Küçük Durum Etiketleri (MÜSAİT) |
| **Medium Shape** | 12 dp | Giriş Butonları, Harita Filtre Butonları, TextField Alanları |
| **Large Shape** | 16 dp | Araç Detay Bottom Sheet, Haritadaki Araç Kartları, Profil Menüsü |
| **Extra Large** | 24 dp | Splash Ekranındaki Logo Vurgu Kutusu |
| **Pill (Full)** | 50 % / Max | Tamamen oval butonlar, profil fotoğrafı çerçevesi |

```kotlin
import androidx.compose.material3.Shapes
import androidx.compose.foundation.shape.RoundedCornerShape

val RencarShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)
```

---

## 4. Ekran Kenar Padding Kuralları (`rencar.pdf` Referanslı)

1. **Kenar Boşluğu (Margins):** Tüm ekranların sol ve sağ kenarlarında en az `space_l` (16dp) boşluk bırakılacaktır. TalkBack odak çerçevesinin kesilmemesi için bu alanlar güvenli bölge (safe zone) kabul edilir.
2. **Karton Tasarımları:** Harita üzerinde veya kiralama geçmişinde yer alan araç kartlarının kendi iç padding değerleri dikeyde `space_m` (12dp), yatayda `space_l` (16dp) olarak uygulanmalıdır.
3. **Dokunma Alanı Genişletmesi:** Boşluklar verilirken, tıklanabilir küçük ikonların (örn: `ArrowBack` geri butonu) dokunma hedefini daraltmamak için `padding` uygulaması tıklanabilir alanın dışına değil, içine yapılmalıdır (`Modifier.clickable` öncesi padding).

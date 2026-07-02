# RenCar Android Tipografi (Typography) Kılavuzu

Bu doküman, RenCar Android mobil uygulamasında kullanılan yazı tipi ailelerini, hiyerarşiyi, font boyutlarını ve Jetpack Compose `Typography` konfigürasyonunu tanımlar. Tasarım belgesindeki (`rencar.pdf`) yazı stili standartları esas alınmıştır.

---

## 1. Yazı Tipi Ailesi (Font Family)

Uygulamanın genelinde modern, temiz ve okunabilirliği yüksek olan **Outfit** yazı tipi ailesi birincil olarak kullanılacaktır. Alternatif olarak sistem varsayılanı (Sans-serif) yedeklenecektir.

```kotlin
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.turkcell.rencar.R

val OutfitFontFamily = FontFamily(
    Font(R.font.outfit_light, FontWeight.Light),
    Font(R.font.outfit_regular, FontWeight.Normal),
    Font(R.font.outfit_medium, FontWeight.Medium),
    Font(R.font.outfit_semibold, FontWeight.SemiBold),
    Font(R.font.outfit_bold, FontWeight.Bold)
)
```

---

## 2. Tipografi Ölçekleri (Type Scales)

Tasarım genelinde kullanılan metin hiyerarşisi aşağıdaki Material 3 tabanlı ölçeklerle yönetilecektir.

| Adlandırma | Boyut (SP) | Ağırlık (Weight) | Satır Yüksekliği (Line Height) | Kullanım Yeri |
| :--- | :--- | :--- | :--- | :--- |
| **HeadlineLarge** | 28 sp | Bold (700) | 36 sp | Karşılama, Büyük Ekran Başlıkları ("Tekrar hoş geldin") |
| **TitleLarge** | 22 sp | SemiBold (600) | 28 sp | Standart Ekran Başlıkları, Bottom Sheet Başlığı ("Renault Clio") |
| **TitleMedium** | 18 sp | Medium (500) | 24 sp | Bölüm Başlıkları, Önemli Kart Başlıkları ("Yakınında 12 araç") |
| **BodyLarge** | 16 sp | Normal (400) / Medium (500) | 22 sp | Form Giriş Alanları, Fiyat Gösterimleri, Buton Metinleri |
| **BodyMedium** | 14 sp | Normal (400) | 20 sp | Standart Bilgilendirme Metinleri, İkincil Etiketler |
| **LabelSmall** | 12 sp | Normal (400) / Medium (500) | 16 sp | Araç Plakaları, Durum Etiketleri, Küçük Açıklamalar |

---

## 3. Jetpack Compose Typography Tanımlaması

Aşağıdaki kod parçası, projedeki `Typography.kt` dosyasının temelini oluşturur.

```kotlin
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val RencarTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.5).sp
    ),
    titleLarge = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    labelSmall = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
```

---

## 4. Tipografi Kullanım ve Erişilebilirlik Kuralları

1. **SP Birim Zorunluluğu:** Metin boyutları tanımlanırken kesinlikle `dp` kullanılmamalı, sistem genelindeki yazı boyutu büyütme/küçültme ayarlarının düzgün çalışması için `sp` tercih edilmelidir.
2. **Dynamic Text Scaling (Dinamik Yazı Ölçekleme):** Kullanıcı sistem yazı boyutunu büyüttüğünde arayüzün kırılmaması için sabit yükseklik verilmiş (`height`) kutulardan kaçınılmalıdır. Bunun yerine minimum yükseklik (`defaultMinSize` veya `wrapContentHeight`) tercih edilmelidir.
3. **Satır Yüksekliği (Line Height):** Çok satırlı metinlerde (örn: "Kullanım şartları..." onay kutusu metni) satırların birbirine girmemesi için `lineHeight` parametresi belirtilen değerlerde sabit tutulmalıdır.
4. **Büyük/Küçük Harf Dönüşümleri:** Buton metinleri gibi tasarımlarda tüm harflerin büyük (`AllCaps`) yapılması okunabilirliği zorlaştırabilir. Butonlarda varsayılan olarak normal kelime yazım stili (Örn: "Hemen Başla") tercih edilmelidir.

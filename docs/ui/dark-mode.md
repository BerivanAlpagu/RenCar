# RenCar Android Karanlık Tema (Dark Mode) Kılavuzu

Bu doküman, RenCar Android mobil uygulamasında karanlık tema (Dark Mode) desteğinin nasıl uygulanacağını, tasarım sisteminde (`rencar.pdf`) yer alan koyu tonlu ekran kurallarını ve Jetpack Compose entegrasyonu detaylarını tanımlar.

Tasarım belgesinde (`rencar.pdf`) her açık renkli ekranın hemen ardından koyu renkli tasarımı (Bkz. Sayfa 2, 4, 6, 8 vb.) birebir tasarlanmıştır.

---

## 1. Karanlık Tema Renk Eşleşmesi (Theme Mapping)

Karanlık mod tasarımlarında saf siyah (`#000000`) yerine derin lacivert/koyu gri (`Slate 900`) ve yüzey elemanları için daha açık bir ton (`Slate 800`) kullanılarak derinlik hissi yaratılmıştır.

| Bileşen Türü | Açık Tema (Light Mode) | Koyu Tema (Dark Mode) |
| :--- | :--- | :--- |
| **Ana Arka Plan** | `#F8F9FA` (Açık Gri) | `#0F172A` (Slate 900) |
| **Yüzey / Kart / Sheet** | `#FFFFFF` (Saf Beyaz) | `#1E293B` (Slate 800) |
| **Birincil Yazı Rengi** | `#1E293B` (Slate 800) | `#F8FAFC` (Slate 50) |
| **İkincil Yazı Rengi** | `#64748B` (Slate 500) | `#94A3B8` (Slate 400) |
| **Pasif Buton / Çerçeve** | `#E2E8F0` (Slate 200) | `#334155` (Slate 700) |
| **Harita Stili** | Standart Açık Yol Haritası | Koyu/Gece Vektör Haritası |

---

## 2. Jetpack Compose Entegrasyonu

Karanlık mod geçişi, Android sistem tercihlerine bağlı olarak otomatik olarak çalışabileceği gibi kullanıcı tarafından uygulama ayarlarından da değiştirilebilir olmalıdır.

### 2.1. Dinamik Tema Yapısı

`RencarTheme` bileşeninde `isSystemInDarkTheme()` kontrolü veya kullanıcı tercihlerini tutan DataStore durumuna göre renk şeması dinamik olarak atanır.

```kotlin
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun RencarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme // colors.md dosyasında tanımlanan koyu renk şeması
    } else {
        LightColorScheme // colors.md dosyasında tanımlanan açık renk şeması
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = RencarTypography, // typography.md dosyasından
        shapes = RencarShapes,         // spacing.md dosyasından
        content = content
    )
}
```

---

## 3. Ekranlara Özel Karanlık Mod Detayları (`rencar.pdf` Referanslı)

### 3.1. Harita Ekranı Koyu Mod Entegrasyonu
- Harita kütüphanesi (Google Maps SDK), uygulamanın koyu temada olup olmadığını algılamalı ve dinamik JSON stil şablonu (`map_style_dark.json`) ile yüklenmelidir.
- Harita üzerindeki konum pinleri, açık modda koyu gölgelere sahipken, koyu modda pin çevrelerinde hafif bir mavi parlama (`glow effect`) kullanılarak belirgin hale getirilmelidir.

### 3.2. Form Giriş Elemanları ve Odak (Focus) Durumları
- Telefon numarası ve OTP kod kutularının (`rencar.pdf` Sayfa 4 ve 6) arka planı koyu modda `#1E293B` (Slate 800) olmalı, seçili (odaklanmış) kutunun kenar çizgisi ise uygulamanın birincil rengi olan `#0066FF` (RenCar Blue) ile parlamalıdır.

### 3.3. Araç Detay Bottom Sheet ve Kartlar (`rencar.pdf` Sayfa 12)
- Aşağıdan yukarı açılan bottom sheet, arka plandaki koyu haritadan net bir şekilde ayrılabilmesi için hafif bir sınır çizgisi (`border` veya `elevation`) içermelidir.
- Araç görselinin arkasında kalan gri gölgeler koyu modda devre dışı bırakılmalı, onun yerine aracın kendisi şeffaf PNG arka planı ile yüzeyin üzerinde durmalıdır.

---

## 4. Karanlık Mod Geliştirme Kuralları

1. **Sert Beyaz Yazılardan Kaçının:** Koyu modda çok parlak beyaz metinler kontrastı aşırı artırarak gözü yorabilir. Bu yüzden birincil metinlerde `#FFFFFF` yerine hafif kırık beyaz olan Slate 50 (`#F8FAFC`) veya Slate 100 (`#F1F5F9`) tercih edilmelidir.
2. **Resim ve İllüstrasyon Parlaklığı:** Uygulamadaki bazı görseller (örn: ehliyet yükleme şeması) koyu modda gözü alabilir. Gerekirse bu görsellerin üzerine koyu modda `%10` - `%20` arası koyuluk katmanı (`ColorFilter`) uygulanabilir.
3. **Önizleme (Preview) Desteği:** Yazılan her arayüz bileşeninin hem açık hem de koyu temadaki görünümü Compose Preview ile doğrulanmalıdır.
   ```kotlin
   @Preview(name = "Light Mode", showBackground = true)
   @Preview(name = "Dark Mode", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, showBackground = true)
   @Composable
   fun PreviewMyComponent() {
       RencarTheme {
           MyComponent()
       }
   }
   ```

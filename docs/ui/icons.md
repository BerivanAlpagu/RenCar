# RenCar Android Simge (Icons) Kılavuzu

Bu doküman, RenCar Android mobil uygulamasında kullanılan görsel simgelerin listesini, Material Icon kütüphanesindeki karşılıklarını ve bu simgelerin arayüzdeki erişilebilirlik kurallarını tanımlar. Tasarım belgesinde (`rencar.pdf`) yer alan tüm ekran tasarımları esas alınmıştır.

---

## 1. Alt Navigasyon (Bottom Navigation) Simgeleri

Ana harita ekranının en altında yer alan ve sekmeler arası geçişi sağlayan simge haritası aşağıdaki gibidir:

| Sekme Adı | Simge Tasarımı | Compose Material Icon Karşılığı |
| :--- | :--- | :--- |
| **Harita** | Harita İğnesi / Yol Haritası | `Icons.Rounded.Map` veya `Icons.Rounded.Explore` |
| **Geçmiş** | Saatli Dairesel Ok / Tarihçe | `Icons.Rounded.History` |
| **Cüzdan** | Kredi Kartı / Cüzdan | `Icons.Rounded.AccountBalanceWallet` |
| **Profil** | Kullanıcı Profili | `Icons.Rounded.Person` |

---

## 2. Özellik ve Durum (Status & Spec) Simgeleri

Araç detay bottom sheet ekranında (`rencar.pdf` Sayfa 11) araç özelliklerini göstermek için kullanılan ikonlar:

| Özellik Tipi | Simge Tipi | Compose Material Icon Karşılığı | `contentDescription` Türkçe Karşılığı |
| :--- | :--- | :--- | :--- |
| **Yakıt Durumu** | Benzin Pompası | `Icons.Rounded.LocalGasStation` | "Yakıt Seviyesi" |
| **Menzil** | Yol / Lokasyon | `Icons.Rounded.Directions` | "Kalan Menzil" |
| **Şanzıman/Vites**| Vites Kolu / Ayar | `Icons.Rounded.Build` / `Icons.Rounded.Settings` | "Vites Tipi" |
| **Kapasite** | Çoklu Kullanıcı | `Icons.Rounded.People` | "Koltuk Sayısı" |
| **Müsaittir** | Yeşil Onay İşareti | `Icons.Rounded.CheckCircle` | "Araç Kiralamaya Müsait" |

---

## 3. Profil ve Kontrol Ekranı Simgeleri

Profil sekmesinde (`rencar.pdf` Sayfa 25) yer alan liste ikonları ve aktif kiralama kontrolleri:

| Liste Elemanı / Buton | Simge Tipi | Compose Material Icon Karşılığı |
| :--- | :--- | :--- |
| **Ödeme Yöntemleri** | Kredi Kartı | `Icons.Rounded.Payment` |
| **Ayarlar** | Dişli / Çark | `Icons.Rounded.Settings` |
| **Yardım & Destek** | Soru İşareti Balonu | `Icons.Rounded.HelpOutline` |
| **Davet Et** | Hediye Paketi / Paylaş | `Icons.Rounded.CardGiftcard` veya `Icons.Rounded.Share` |
| **Çıkış Yap** | Çıkış Kapısı / Ok | `Icons.Rounded.ExitToApp` |
| **Kilitle / Aç** | Asma Kilit (Kapalı/Açık) | `Icons.Rounded.Lock` / `Icons.Rounded.LockOpen` |
| **Geri Butonu** | Sola Bakan Ok | `Icons.Rounded.ArrowBackIosNew` |
| **Arama Çubuğu** | Büyüteç | `Icons.Rounded.Search` |
| **Filtre Butonu** | Ayar Çubukları | `Icons.Rounded.Tune` |

---

## 4. Compose İçinde İkon Kullanım Kılavuzu

### 4.1. Dekoratif İkonlar (Accessibility Bypass)
Eğer simge sadece görsel bir süs ise ve yanındaki metin zaten o simgenin işlevini tamamen açıklıyorsa TalkBack'in bu simgeye odaklanıp gereksiz ses kalabalığı yapmasını önlemek için `contentDescription = null` atanmalıdır.

*Örnek (Profil listesi satırı):*
```kotlin
Row(modifier = Modifier.clickable { ... }) {
    Icon(
        imageVector = Icons.Rounded.Payment,
        contentDescription = null // Yanında "Ödeme yöntemleri" metni yazıyor
    )
    Text("Ödeme yöntemleri")
}
```

### 4.2. Tek Başına Duran İkonlar (Action Icons)
Eğer simge tek başına bir buton veya işlevsel bir öge ise (örn. Arama çubuğu yanındaki Filtre butonu veya Geri tuşu), mutlaka anlamlı bir `contentDescription` içermelidir.

*Örnek:*
```kotlin
IconButton(onClick = onFilterClick) {
    Icon(
        imageVector = Icons.Rounded.Tune,
        contentDescription = stringResource(R.string.accessibility_filter_button) // "Araçları filtrele"
    )
}
```

### 4.3. Renklendirme (Icon Tinting)
İkon renkleri hiçbir zaman sabit renk kodlarıyla verilmemelidir. Her ikon, uygulanan temaya (Açık/Koyu mod) uyum sağlayabilmesi için `LocalContentColor` veya temamızın `primary` / `onSurfaceVariant` renkleri ile boyanmalıdır (`tint` parametresi).
```kotlin
Icon(
    imageVector = Icons.Rounded.Map,
    contentDescription = null,
    tint = MaterialTheme.colorScheme.primary
)
```

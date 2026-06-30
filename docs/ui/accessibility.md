# RenCar Android Uygulaması Erişilebilirlik (Accessibility) Kılavuzu

Bu doküman, RenCar Android mobil uygulamasının tüm kullanıcılar için erişilebilir olmasını sağlamak amacıyla geliştirme ekibinin uyması gereken kuralları, tasarım sistemi standartlarını (`rencar.pdf`) ve API entegrasyonu (`openapi.json`) sırasındaki erişilebilirlik gereksinimlerini tanımlar.

Uygulamamız, Jetpack Compose mimarisiyle geliştirildiğinden, Android Erişilebilirlik Kılavuz İlkeleri (WCAG 2.1 AA uyumlu) Compose Semantics API'leri kullanılarak hayata geçirilecektir.

---

## 1. Genel Jetpack Compose Erişilebilirlik Standartları

Uygulama genelinde tüm ekranlarda uygulanacak temel kurallar aşağıda belirtilmiştir:

### 1.1. Dokunma Hedefi Boyutları (Touch Target Size)
- **Kural:** Tüm tıklanabilir, seçilebilir veya uzun basılabilir görsel ögelerin dokunma hedefi en az **48 x 48 dp** olmalıdır (Bkz: WCAG 2.1 - 2.5.5).
- **Compose Uygulaması:**
  ```kotlin
  Modifier.minimumInteractiveComponentSize() // ya da min boyutu 48.dp ayarlamak
  ```
- **Referans (`rencar.pdf`):**
  - Splash ekranındaki "Hemen Başla" ve "Giriş yap" butonları.
  - Harita ekranındaki filtreleme butonları ve profil menüsü ögeleri.
  - Araç detayındaki "Rezerve Et" ve "Kilidi Aç" butonları.

### 1.2. Renk Kontrastı (Color Contrast)
- **Kural:** Metinler ve arka planları arasında en az **4.5:1** kontrast oranı olmalıdır (Bkz: WCAG 1.4.3). Büyük metinler (18sp kalın veya 24sp normal üstü) için en az **3:1** oran sağlanmalıdır.
- **Tasarım Entegrasyonu (`rencar.pdf`):**
  - Açık Tema (Light Mode) ve Koyu Tema (Dark Mode) renk paletlerinde kontrast oranları test edilmeli, özellikle pasif butonlar ve ikincil metin tonlarında okunabilirlik korunmalıdır.
  - Örneğin, haritada gösterilen fiyat etiketleri (`#FFFFFF` üzerinde `#000000` veya mavi/turuncu kontrast oranları) bu kurallara uymalıdır.

### 1.3. Ekran Okuyucu (TalkBack) İçerik Açıklamaları (Content Descriptions)
- **Kural:** Dekore edici olmayan tüm görseller, ikonlar ve durum göstergeleri için anlamlı bir `contentDescription` sağlanmalıdır. Dekore edici (sadece süs amaçlı) görseller için `contentDescription = null` atanmalıdır.
- **Compose Uygulaması:**
  ```kotlin
  Icon(
      imageVector = Icons.Default.DirectionsCar,
      contentDescription = stringResource(R.string.desc_vehicle_icon)
  )
  ```

### 1.4. Semantik Gruplama (Semantic Grouping)
- **Kural:** Birbiriyle ilişkili olan ve ayrı ayrı odaklanıldığında anlamını yitiren bileşenler tek bir grup olarak birleştirilmelidir (Bkz: `mergeDescendants`).
- **Tasarım Entegrasyonu (`rencar.pdf`):**
  - **Araç Bilgisi Kartı:** "Renault Clio" başlığı, "MÜSAİT" durum etiketi ve plaka bilgisi tek bir odak çerçevesinde okunmalıdır.
  - **Özellik Grupları:** Yakıt (`%72`), Menzil (`~480 km`), Vites (`Manuel`) ve Koltuk (`5 kişi`) kartları kendi içlerinde gruplanarak tek seferde okunmalıdır.
  - **Compose Uygulaması:**
    ```kotlin
    Row(
        modifier = Modifier.semantics(mergeDescendants = true) { }
    ) {
        Text("Yakıt")
        Text("%72")
    }
    ```

---

## 2. Ekran Bazlı Erişilebilirlik Kılavuzu (`rencar.pdf` Referanslı)

### 2.1. Splash / Onboarding Ekranları
- **Logo ve Slogan:** RenCar logosu için "RenCar Uygulama Logosu" açıklaması eklenmeli. Altındaki "Yakındaki aracı bul, dakikalar içinde yola çık." sloganı ekran okuyucu tarafından doğal bir şekilde okunmalıdır.
- **Sayfa Göstergesi (Pager Indicator):** Onboarding aşamalarını gösteren noktalar TalkBack kullanıcısına kaçıncı sayfada olunduğunu bildirmelidir.
  - *Semantik Tanım:* `stateDescription = "Sayfa 1 / 3"` gibi dinamik güncellenen durumlar eklenmelidir.
- **"Giriş yap" ve "Hemen Başla" Butonları:** Butonların tıklanabilir olduğu net şekilde belirtilmelidir.

### 2.2. Giriş ve Kayıt (Auth & OTP Verification) Ekranları
- **Telefon Numarası Girişi:** Ülke kodu seçimi (`TR +90`) ve telefon numarası giriş alanı ilişkili olmalıdır. Text alanına TalkBack odaklandığında "Telefon numarası giriş alanı" olarak okunmalı, hata durumunda hata mesajı odak çerçevesi içine alınmalıdır.
- **OTP (Tek Kullanımlık Şifre) Giriş Kutuları:**
  - 6 haneli kod giriş kutuları ardışıl odak sırasına sahip olmalıdır (`FocusRequester`).
  - Kullanıcı bir rakam girdiğinde odak otomatik olarak bir sonraki kutuya geçmeli, ekran okuyucu bu geçişi "Metin kutusu 2" gibi net şekilde bildirmelidir.
  - SMS Gönderme süresi ("Kodu tekrar gönder - 0:42") TalkBack tarafından okunabilmeli ancak sürekli güncellenerek ekran okuyucunun odağını bölmemelidir (`LiveRegion` kullanımı sınırlı ve kontrollü olmalıdır).

### 2.3. Ehliyet Doğrulama Ekranı (License Upload)
- **Durum Göstergeleri (Steps):** "1. Ehliyet", "2. Selfie", "3. Onay" adımları TalkBack tarafından "Adım 1/3: Ehliyet" şeklinde okunmalıdır.
- **Ehliyet Ön ve Arka Yüz Fotoğraf Yükleme:**
  - Fotoğraf yükleme alanları buton rolü taşımalıdır.
  - Yüklenen ehliyet görseli önizlemesi için `contentDescription = "Yüklenen ehliyet ön yüz fotoğrafı"` denilmelidir.
  - Eğer ehliyet yüklenmediyse "Ehliyet ön yüzünü çekmek veya yüklemek için tıklayın" açıklaması sunulmalıdır.
- **Bilgi Notu:** "Bilgilerin güvenle saklanır..." metni ekran okuyucu tarafından okunabilir bir bilgi paneli (`semantics { heading() }` veya gruplanmış metin) olmalıdır.

### 2.4. Ana Harita ve Yakındaki Araçlar Ekranı
- **Harita Odaklaması ve Alternatif Liste Modu (Kritik):**
  - Harita üzerindeki araç pinleri görme engelli kullanıcılar için TalkBack ile seçilmesi oldukça zor olan dinamik bileşenlerdir.
  - **Erişilebilirlik Çözümü:** Ekranın uygun bir yerine TalkBack kullanıcıları için kolayca erişilebilir, haritadaki araçları liste halinde sunan gizli/erişilebilir bir "Araç Listesi Görünümü" butonu yerleştirilmelidir.
- **Harita Pinleri:** Haritada gösterilen araç fiyat pinleri (`₺28`, `₺32`, vb.) için TalkBack açıklaması şu şekilde olmalıdır: "Renault Clio, dakikası 28 Türk Lirası, kiralamak için çift dokunun".
- **Kategori Seçimi (Filters):** "Tümü", "Ekonomik", "Konfor", "SUV" butonlarında seçili olan kategori TalkBack tarafından "Seçili: Ekonomik, Filtre Butonu" şeklinde duyurulmalıdır (`Modifier.selectable`).

### 2.5. Araç Detay ve Rezervasyon Onay Ekranları
- **Durum Bilgisi:** "Renault Clio MÜSAİT" başlığında, "Müsaittir" ifadesi `stateDescription` veya `contentDescription` ile zenginleştirilmelidir.
- **Ücret Bilgileri:** `₺4,50 / dk` ifadesi TalkBack tarafından "Dakika başına 4 lira 50 kuruş" şeklinde okunmalıdır. "Saatlik ₺180" ise "Saatlik 180 Türk Lirası" olarak seslendirilmelidir.
- **Kullanım Şartları Onay Kutusu (Checkbox):** Checkbox ile yanındaki "Kullanım şartlarını..." metni semantik olarak birleştirilmelidir. TalkBack kullanıcısı checkbox'a odaklandığında hem metni duymalı hem de "Seçili değil, seçmek için çift dokunun" uyarısını almalıdır.

### 2.6. Hasar / Teslim Fotoğrafları (Araç Teslim Ekranı)
- **4 Yönlü Fotoğraf Yükleme:** "Ön", "Arka", "Sol", "Sağ" yönleri TalkBack tarafından sırayla ve net şekilde okunmalı, her bir adımın yüklendi/yüklenmedi durumu (Örn: "Ön yüz fotoğrafı yüklendi" veya "Sol yüz fotoğrafı eksik") kullanıcıya sesli bildirilmelidir.
- **"Kiralalamayı Başlat" Butonu:** Eksik fotoğraflar varken butonun pasif olduğu TalkBack'e "Kiralama başlatılamaz, eksik fotoğraflar var" açıklamasıyla aktarılmalıdır.

### 2.7. Aktif Kiralama Ekranı
- **Zamanlayıcı (Timer):** `00:24:18` (24 dakika 18 saniye) gibi sürekli değişen zamanlayıcılar TalkBack'i sürekli meşgul etmemelidir. Zamanlayıcı alanı seçildiğinde güncel süreyi okumalı, ancak arka planda her saniye sesli anons yapmamalıdır.
- **"Kilitle / Aç" ve "Kiralamayı Bitir" Butonları:** Yanlışlıkla basılmasını önlemek amacıyla "Kiralamayı Bitir" gibi kritik butonlar için TalkBack çift dokunma teyidi isteyecek şekilde tasarlanabilir veya butonun önemi ses tonuyla vurgulanabilir.

### 2.8. Ödeme ve Kiralama Özeti Ekranı
- **Fatura Detayları:** Süre, Mesafe, Kiralama Ücreti, Başlangıç Ücreti, Hizmet Bedeli, İndirim ve Toplam Tutar alanları sırayla, açıklama ve tutar ilişkisi kopmayacak şekilde (Tablo düzeni veya gruplanmış Row yapısında) okunmalıdır.
- **Kayıtlı Kartlar:** `•••• 4291` gibi maskelenmiş kart bilgileri için TalkBack "Son hanesi 4291 olan Visa kart" şeklinde seslendirme yapmalıdır.

---

## 3. API Hataları ve Dinamik Durum Yönetimi Erişilebilirliği (`openapi.json` Entegrasyonu)

API seviyesindeki durumlar ve hata kodları (`openapi.json` referanslı) kullanıcı arayüzünde erişilebilir şekilde yönetilmelidir:

### 3.1. Hata Mesajlarının Ekran Okuyucuya Duyurulması
API'den dönen tüm hata senaryoları (`400 Bad Request`, `401 Unauthorized`, `409 Conflict`, `413 Payload Too Large` vb.) için arayüzde gösterilen hata diyalogları veya toast mesajları TalkBack odağını hemen üzerine çekmelidir (`LiveRegionMode.Assertive` veya `AccessibilityLiveRegion`).
- **Örnek 413 (Dosya Çok Büyük):** Ehliyet yüklerken dosya 5MB'ı aşarsa dönen hata TalkBack tarafından doğrudan "Yükleme başarısız. Ehliyet fotoğraf boyutu maksimum 5 megabayt olmalıdır." şeklinde anons edilmelidir.
- **Örnek 409 (Plaka Çakışması):** "Bu plakaya sahip araç zaten mevcut" hatası form alanının hemen altında TalkBack tarafından okunmalıdır.

### 3.2. Rol ve Ehliyet Durum Değişikliklerinin Bildirilmesi
- **Rol Geçişleri (PENDING -> CUSTOMER):**
  - Ehliyet onaylandıktan sonra arka planda `/auth/refresh` atılarak rol güncellendiğinde, kullanıcıya bir pop-up veya bildirim aracılığıyla "Ehliyetiniz onaylandı! Artık araç kiralayabilirsiniz." sesli ve görsel mesajı sunulmalıdır.
- **Ehliyet Reddedilme Durumu (REJECTED):**
  - `/license/status` endpoint'inden `REJECTED` statusu ve `rejectionReason` (red gerekçesi) döndüğünde, profil ekranında bu gerekçe TalkBack tarafından öncelikli olarak okunmalıdır: "Ehliyetiniz reddedildi. Gerekçe: [Gerekçe metni]. Lütfen tekrar yükleyin."

---

## 4. Doğrulama ve Test Planı

Erişilebilirlik kriterlerinin karşılandığını doğrulamak için şu adımlar uygulanacaktır:

1. **Android Accessibility Scanner (Erişilebilirlik Tarayıcısı):** Uygulamanın tüm ekranları Google'ın resmi Accessibility Scanner aracı ile taranacak, kontrast ve dokunma hedefi hataları giderilecektir.
2. **TalkBack ile Manuel Test:** Uygulama happy-path senaryoları (Kayıt olma, ehliyet yükleme, araç seçme, rezervasyon ve kiralama bitirme) gözler kapalı şekilde sadece TalkBack açıkken baştan sona test edilecektir.
3. **Klavye Odak Testi:** Uygulamaya harici bir Bluetooth klavye bağlanarak tüm etkileşimli alanlar arasında `TAB` tuşuyla mantıklı bir sırada gezinilebildiği (Focus traversal) test edilecektir.

---

Bu kılavuz, [ADR Kararlarına](../decisions.md) uygun olarak geliştirilecek tüm ekranlar için bağlayıcıdır.

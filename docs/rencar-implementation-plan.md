# Rencar ekran uygulama plani

Bu dokuman, `rencar.pdf` icindeki 26 ekranin uygulama sirasini ve API bagimliliklarini tanimlar.
PDF yapisi 13 light + 13 dark ekran ciftinden olusur. Oncelik, once ortak veri modellerini ve tema altyapisini kurup sonra ekranlari akisa gore hayata gecirmektir.

## Kapsam

- 26 ekranin birebir uygulamasi
- Light ve dark tema parity
- API entegrasyonu
- Navigation, state management ve hata durumlari
- Bos durum, loading ve failure state'leri

## Ekran Envanteri

### 1. Splash / Onboarding

- Ekran 01: Splash / Onboarding
- Ekran 02: Splash / Onboarding dark
- Amac: ilk acilis, CTA yonlendirme, login/register gecisi

### 2. Giris / Kayit

- Ekran 03: Giris / Kayit
- Ekran 04: Giris / Kayit dark
- Amac: telefon numarasi ile SMS kodu isteme
- API: `POST /auth/login`, `POST /auth/register`

### 3. OTP Dogrulama

- Ekran 05: OTP Dogrulama
- Ekran 06: OTP Dogrulama dark
- Amac: gelen SMS kodunu girip akisi ilerletme
- Not: API contract'ta dogrudan OTP endpoint'i yok; mevcut auth akisi ile uygun backend davranisi netlestirilmeli

### 4. Ehliyet Dogrulama

- Ekran 07: Ehliyet dogrulama - ilk kayit
- Ekran 08: Ehliyet dogrulama - dark
- Amac: ilk kez kiralama oncesi ehliyet on/arka yukleme
- API: `POST /license/upload`, `GET /license/status`

### 5. Ana Harita

- Ekran 09: Ana Harita - yakindaki araclar
- Ekran 10: Ana Harita - dark
- Amac: harita uzerinde araclari, filtreleri ve alt navigation'i gostermek
- API: `GET /vehicles`

### 6. Arac Detay

- Ekran 11: Arac Detay (Bottom Sheet)
- Ekran 12: Arac Detay dark
- Amac: secili arac bilgisi, fiyat, ozellikler ve ana aksiyonlar
- API: `GET /vehicles/{id}`

### 7. Rezervasyon Onayi

- Ekran 13: Rezervasyon Onayi
- Ekran 14: Rezervasyon Onayi dark
- Amac: kiralama plani secimi, fiyat ozeti, sozlesme onayi
- API: `POST /rentals`

### 8. Arac Teslim Fotografi

- Ekran 15: Arac Teslim Fotografi - 4 yon
- Ekran 16: Arac Teslim Fotografi dark
- Amac: kiralama baslatmadan once arac durum fotografi toplama
- Not: backend'de bu adim icin ek medya upload destegi gerekebilir; mevcut contract'ta direkt endpoint yok

### 9. Aktif Kiralama

- Ekran 17: Aktif Kiralama
- Ekran 18: Aktif Kiralama dark
- Amac: anlik sure, mesafe, kilit ac/kapat ve kiralamayi bitir
- API: `POST /rentals/{id}/return`, `GET /rentals/{id}`

### 10. Odeme / Kiralama Ozeti

- Ekran 19: Odeme / Kiralama Ozeti
- Ekran 20: Odeme / Kiralama Ozeti dark
- Amac: yolculuk tamamlandiktan sonra toplam ucret ve odeme
- API: `GET /rentals/{id}`, `POST /rentals/{id}/return`

### 11. Cuzdan / Odeme Yontemleri

- Ekran 21: Cuzdan / Odeme Yontemleri
- Ekran 22: Cuzdan / Odeme Yontemleri dark
- Amac: bakiye, kartlar ve son islemler
- API: mevcut contract'ta wallet endpoint yok, UI verisi icin mock veya sonraki API extension gerekir

### 12. Kiralama Gecmisi

- Ekran 23: Kiralama Gecmisi
- Ekran 24: Kiralama Gecmisi dark
- Amac: onceki kiralamalarin listesi
- API: `GET /rentals`

### 13. Profil

- Ekran 25: Profil
- Ekran 26: Profil dark
- Amac: kullanici, ehliyet durumu, odeme, ayarlar ve cikis
- API: `GET /auth/me`, `POST /auth/logout`, `GET /license/status`

## Uygulama Sirasi

### Faz 1 - Temel altyapi

- Theme yapisi
- Light/dark renk tokenlari
- Typography ve spacing sabitleri
- Navigation iskeleti
- Genel component seti

### Faz 2 - Kimlik dogrulama

- Splash / onboarding
- Login / register
- OTP dogrulama
- Auth state kaliciligi

### Faz 3 - Uyum ve uygunluk

- Ehliyet yukleme
- Ehliyet durum ekrani
- Kiralama oncesi blokaj kurallari

### Faz 4 - Ana kiralama akisi

- Ana harita
- Arac detay
- Rezervasyon onayi
- Arac teslim fotografi
- Aktif kiralama

### Faz 5 - Sonradan goruntulenen ekranlar

- Odeme ozeti
- Cuzdan
- Kiralama gecmisi
- Profil

## Teknik Notlar

- `GET /vehicles` ve `GET /vehicles/{id}` icin list/detail cache kullanilmasi onerilir.
- `GET /auth/me` uygulama acilisinda session yenileme ile birlikte calismali.
- `POST /auth/refresh` token rotation icin merkezi bir auth interceptor gerektirir.
- `license`, `rental` ve `profile` ekranlari icin role bazli gorunum kurallari tanimlanmalidir.
- Dark mode sadece renk degistirmemeli; surface, elevation, shadow ve state renkleri de ayrica tasarlanmalidir.

## Acik Noktalar

- OTP dogrulama icin backend'de kod giris endpoint'i net degil.
- Cuzdan / odeme yontemleri icin mevcut API contract'ta endpoint yok.
- Arac teslim fotografi icin mevcut API contract'ta medya upload endpoint'i yok.

## Onay Kriteri

- Tum 26 ekranin isimleri ve sirasi net
- API bagimliliklari anlasilir
- Eksik backend alanlari isaretli
- Uygulamaya baslamadan once kapsam kilitlenmis

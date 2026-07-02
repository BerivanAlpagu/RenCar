# RenCar Android Uygulaması Implementasyon Planı

Bu doküman, RenCar Android projesi için uygulanacak kapsam, API sözleşmeleri, mimari kurallar, dosya dökümü ve batch planını tanımlar. 

## 1. Doğrulanan API Kaynağı
- **Kaynak:** `app/src/main/java/com/turkcell/rencar/docs/api/openapi.json` (Swagger)
- **Güvenlik (Security):** `access-token` header ile JWT Bearer token iletimi (Tüm korumalı endpointler için).

## 2. Doğrulanan Endpointler

### 2.1. Auth ve Token Akışı
- **`POST /auth/register`**: PENDING rolüyle yeni kullanıcı oluşturur. Telefon bilgisi zorunludur. Hemen JWT döner.
- **`POST /auth/login`**: Parolasız girişin ilk adımıdır, telefon numarası alır ve SMS kodu (simülasyon) gönderir.
- **`POST /auth/verify-otp`**: Gelen SMS kodunu doğrulayıp JWT (accessToken, refreshToken, user) döner.
- **`POST /auth/refresh`**: Token yenileme (Rotation). Eski refreshToken geçersiz olur. Reuse (tekrar kullanım) durumunda tüm oturumlar kapatılır. 
- **`POST /auth/logout`**: Kullanıcının tüm aktif refresh oturumlarını iptal eder.
- **`GET /auth/me`**: Token sahibinin profilini ve güncel rolünü (PENDING, CUSTOMER, ADMIN) döner.

### 2.2. Ehliyet Onay Akışı (License)
- **`POST /license/upload`**: PENDING kullanıcı ehliyet ön ve arka yüzünü yükler. Durum `UNDER_REVIEW` olur.
- **`GET /license/status`**: Ehliyet onay durumunu (NOT_SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED) döner.

### 2.3. Araçlar (Vehicles)
- **`GET /vehicles`**: Müsait (AVAILABLE) araçları listeler.
- **`GET /vehicles/{id}`**: Müsait araç detayını getirir.
- **`Admin Endpointleri`**: `/admin/vehicles` üzerinden CRUD işlemleri (Sadece ADMIN rolü erişebilir).

### 2.4. Kiralama (Rentals)
- **`POST /rentals`**: Anlık araç kiralar. Sadece CUSTOMER rolü işlem yapabilir. 
- **`GET /rentals`**: Mevcut kullanıcının kiralamalarını getirir.
- **`POST /rentals/{id}/return`**: Kiralamayı iade eder.

## 3. Tasarım Kapsamı
Gönderilen tasarım belgeleri (`rencar.pdf`) referans alınacaktır:
- **Auth Ekranları:** Login, Register
- **Onboarding & Ehliyet:** Ehliyet ön/arka yüz fotoğraf yükleme (PENDING -> CUSTOMER geçişi için bekleme ekranı).
- **Ana Ekran (Müsait Araçlar):** Listeleme ve filtreleme arayüzleri.
- **Araç Detay ve Kiralama:** Araç bilgileri, fiyat, kiralama başlatma butonu.
- **Profil ve Geçmiş:** Aktif kiralama durumu, geçmiş kiralamalar listesi, ayarlar.

## 4. Uygulama İçi Akış Kuralları

### 4.1. Rol ve Yetkilendirme (PENDING vs CUSTOMER)
- Yeni kayıt olan her kullanıcı varsayılan olarak `PENDING` rolündedir.
- PENDING kullanıcılar sisteme giriş yapabilir ancak araç kiralayamaz (Müşteri işlemleri 403 döner).
- Ehliyet onayından (APPROVED) sonra kullanıcının rolü backend tarafında `CUSTOMER` olarak güncellenir. Mobil uygulama, arka planda sessizce `/auth/refresh` atarak yeni `CUSTOMER` yetkili token'ı almalı ve UI'ı güncellemelidir.

### 4.2. Token Refresh (Rotation) Stratejisi
- Refresh token sadece tek kullanımlıktır.
- OkHttp `Authenticator` ile 401 Unauthorized yakalandığında refresh işlemi tetiklenir.
- Refresh işleminden sonra alınan yeni access ve refresh token'lar güvenli saklama alanına (EncryptedSharedPreferences/DataStore) yazılır.

### 4.3. Ehliyet Yükleme Kuralları
- Sadece `PENDING` kullanıcılar veya `REJECTED` (reddedilmiş) durumundakiler ehliyet yükleyebilir. `CUSTOMER` kullanıcılar yükleme yapamaz.
- Dosyalar `multipart/form-data` formatında (jpg/png, maks 5MB) gönderilir.

## 5. Bağımlılık Matrisi
Proje mimarisi şu bağımlılıklarla desteklenecektir:

| Kütüphane | Kullanım Alanı | Gerekçe |
| :--- | :--- | :--- |
| **Retrofit + OkHttp** | Network Katmanı | API istekleri, Token rotation ve Interceptor yönetimi. |
| **Kotlinx Serialization** | DTO Modelleme | JSON parse işlemleri ve type-safe navigation için. |
| **Hilt** | Dependency Injection | Repository, API, ViewModel injection. |
| **Jetpack Compose** | UI Katmanı | Tüm ekran tasarımları ve MVI state yönetimi. |
| **Room Database** | Caching (Offline) | Müsait araçlar ve geçmiş kiralamaların önbelleklenmesi. |

## 6. Dosya Dökümü ve Batch Planı
Proje Clean Architecture ve özellik (feature) bazlı paketleme (auth, vehicles, license vb.) yapısına göre inşa edilecektir. 

**Batch 1: Core ve Network Temelleri**
- `NetworkModule.kt`, `TokenInterceptor.kt`, `TokenAuthenticator.kt` (Refresh rotation akışı).
- Ortak Error handling sarmalayıcıları (Result, Error mapping).

**Batch 2: Auth Feature (Data & Domain)**
- `AuthApi.kt` (Login, VerifyOtp, Register, Refresh, Me)
- `AuthRepository.kt` ve `DefaultAuthRepository.kt`
- DTO -> Domain modelleri (`AuthResponseDto` vb.)

**Batch 3: Auth Feature (Presentation)**
- Login / Register ViewModelleri ve MVI sözleşmeleri (State, Intent, Effect).
- Compose Ekranları (`LoginScreen.kt`, `RegisterScreen.kt`).

**Batch 4: License ve Onboarding Akışı**
- `LicenseApi.kt` (upload, status).
- Ehliyet yükleme ekranı, durum bekleme UI'ı (`LicenseScreen.kt`).
- PENDING -> CUSTOMER token yenileme mantığı.

**Batch 5: Araçlar (Vehicles) Akışı**
- `VehicleApi.kt` (list, getOne).
- Araç listeleme ekranı (`VehiclesScreen.kt`), araç detayı.
- Room database entegrasyonu (Offline caching için DAO'lar).

**Batch 6: Kiralama (Rentals) Akışı**
- `RentalApi.kt` (rent, return, list).
- Kiralama işlemi, onay dialogları, iade ekranı.
- Profil içinde geçmiş kiralamalar sekmesi.

## 7. Happy-Path Test Planı
Implementasyon sonrası aşağıdaki testler çalıştırılacaktır:

1. **Kayıt ve Onay Akışı:** Register -> PENDING olarak gir -> Ehliyet Yükle (UNDER_REVIEW) -> Admin onaylar (APPROVED) -> Uygulama sessiz token refresh yapar -> Kullanıcı CUSTOMER olur.
2. **Kiralama Akışı:** CUSTOMER rolüyle araç listesi açılır -> Araç detaya girilir -> Kirala butonuna basılır -> Başarılı (`POST /rentals`) -> Kiralamalarım sekmesinde görülür -> İade et (`POST /rentals/{id}/return`) -> Başarılı.
3. **Session Yönetimi:** 401 alındığında Authenticator tetiklenir, `/auth/refresh` ile yeniler, kaldığı yerden isteği devam ettirir.

## 8. Sık Yapılan Hatalar ve Önlemler
- **Refresh Token Reuse:** Eski token gönderilirse session patlar. 
  - *Önlem:* Refresh token kullanıldıktan hemen sonra yerelde güncellenmelidir.
- **PENDING Kullanıcının Kiralamaya Çalışması:** API 403 döner. 
  - *Önlem:* UI, `/auth/me` rolüne veya `/license/status` verisine bakarak ehliyet onaysız kullanıcıya kiralama butonunu pasif/gizli tutmalıdır.
- **DTO'ları UI'da Kullanmak:**
  - *Önlem:* Domain katmanı oluşturulup DTO'lar repository'de Mapper aracılığıyla arındırılacaktır (Örn: `AuthResponseDto` -> `User`).
- **Offline Durumda Kiralama Yapmaya Çalışmak:**
  - *Önlem:* Room'dan veri okunsa dahi, kiralama/yükleme butonlarına basılırken Network kontrolü yapılmalıdır.


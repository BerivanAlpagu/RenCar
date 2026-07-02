# API Contract Plan (RenCar Android Uygulaması)

Bu doküman, `docs/decisions.md` dosyasındaki mimari kararlara ve `app/src/main/java/com/turkcell/rencar/docs/api/openapi.json` (Swagger) kaynağına dayanılarak hazırlanmıştır. Mobil uygulamanın backend ile iletişim kuracağı tüm güncel endpoint'ler, istek ve yanıt modelleri (DTO) aşağıda listelenmiştir. Uygulama geliştirilirken yalnızca buradaki şemalar referans alınmalıdır.

## 1. Auth ve Token Akışı (Auth)

Korumalı (Security gerektiren) tüm endpoint'ler için `Authorization: Bearer <accessToken>` header'ı kullanılmalıdır. 

### 1.1. Yeni Kullanıcı Kaydı
- **Endpoint:** `POST /auth/register`
- **Açıklama:** Kullanıcıyı varsayılan olarak `PENDING` rolüyle oluşturur ve hemen token döner.
- **Request Body (`RegisterDto`):**
  - `email` (string, email, zorunlu)
  - `password` (string, min 6, zorunlu)
  - `fullName` (string, zorunlu)
  - `phone` (string, opsiyonel)
- **Response (201 - `AuthResponseDto`):**
  - `accessToken` (string): Kısa ömürlü JWT.
  - `refreshToken` (string): Uzun ömürlü yenileme token'ı.
  - `user` (`UserResponseDto`)
- **Olası Hatalar:** `409 Conflict` (E-posta zaten kayıtlı)

### 1.2. Giriş Yap
- **Endpoint:** `POST /auth/login`
- **Açıklama:** E-posta ve şifre ile giriş yapıp token döner.
- **Request Body (`LoginDto`):**
  - `email` (string, email, zorunlu)
  - `password` (string, zorunlu)
- **Response (200 - `AuthResponseDto`):** `accessToken`, `refreshToken`, `user` (`UserResponseDto`)
- **Olası Hatalar:** `401 Unauthorized` (E-posta veya parola hatalı)

### 1.3. Token Yenileme (Rotation)
- **Endpoint:** `POST /auth/refresh`
- **Açıklama:** Geçerli bir refresh token ile **yeni** bir access + refresh çifti döner. Gönderilen eski refresh token anında geçersiz olur.
- **Request Body (`RefreshTokenDto`):**
  - `refreshToken` (string, zorunlu)
- **Response (200 - `AuthResponseDto`):** Yeni `accessToken`, `refreshToken`, `user`
- **Olası Hatalar:** `401 Unauthorized` (Token süresi dolmuş veya tekrar kullanılmış)

### 1.4. Çıkış Yap
- **Endpoint:** `POST /auth/logout`
- **Header:** `Authorization: Bearer <accessToken>`
- **Açıklama:** Kullanıcının aktif refresh oturumlarını iptal eder. 
- **Response (200 - `MessageResponseDto`):** `message` (string)
- **Olası Hatalar:** `401 Unauthorized`

### 1.5. Mevcut Kullanıcı Bilgisi
- **Endpoint:** `GET /auth/me`
- **Header:** `Authorization: Bearer <accessToken>`
- **Açıklama:** Token sahibinin profilini ve güncel rolünü (`PENDING`, `CUSTOMER`, `ADMIN`) döner. PENDING kullanıcı ehliyeti onaylandığında burada rolü CUSTOMER görünür.
- **Response (200 - `UserResponseDto`):**
  - `id` (string), `email` (string), `phone` (string, nullable), `fullName` (string)
  - `role` (enum: PENDING, CUSTOMER, ADMIN)
  - `createdAt`, `updatedAt` (date-time)
- **Olası Hatalar:** `401 Unauthorized`

---

## 2. Ehliyet Onay Akışı (License)

### 2.1. Ehliyet Yükle
- **Endpoint:** `POST /license/upload`
- **Header:** `Authorization: Bearer <accessToken>`
- **Content-Type:** `multipart/form-data`
- **Açıklama:** Sadece `PENDING` veya `REJECTED` kullanıcılar ehliyet yükleyebilir. İşlem sonrası durum `UNDER_REVIEW` olur.
- **Request Body (`UploadLicenseDto`):**
  - `front` (binary, file, zorunlu, maks 5MB, jpg/png)
  - `back` (binary, file, zorunlu, maks 5MB, jpg/png)
- **Response (201 - `LicenseResponseDto`):**
  - `id` (string)
  - `status` (enum: NOT_SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED)
  - `frontImageUrl` (string), `backImageUrl` (string)
  - `createdAt`, `updatedAt` (date-time)
- **Olası Hatalar:** `400 Bad Request`, `401 Unauthorized`, `409 Conflict` (Zaten CUSTOMER veya ehliyeti zaten incelemede), `413 Payload Too Large`

### 2.2. Ehliyet Durumu
- **Endpoint:** `GET /license/status`
- **Header:** `Authorization: Bearer <accessToken>`
- **Açıklama:** Mevcut kullanıcının ehliyet durumunu döner.
- **Response (200 - `LicenseStatusResponseDto`):**
  - `status` (enum: NOT_SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED)
  - `frontImageUrl` (string, nullable), `backImageUrl` (string, nullable)
  - `rejectReason` (string, nullable)
  - `reviewedAt` (date-time, nullable)
- **Olası Hatalar:** `401 Unauthorized`

---

## 3. Araçlar (Vehicles)

### 3.1. Müsait Araçları Listele
- **Endpoint:** `GET /vehicles`
- **Header:** `Authorization: Bearer <accessToken>`
- **Açıklama:** Yalnızca durumu `AVAILABLE` olan araçları döner (Müşteriye özel). `PENDING` kullanıcılar 403 alır.
- **Query Parametreleri:**
  - `type` (enum: SEDAN, SUV, HATCHBACK, STATION, MINIVAN) - Opsiyonel
  - `page` (number, min: 1) - Opsiyonel
  - `limit` (number, maks: 100) - Opsiyonel
- **Response (200 - List<`VehicleResponseDto`>):**
  - `id` (string), `plate` (string), `brand` (string), `model` (string)
  - `type` (enum), `pricePerDay` (number), `status` (enum: AVAILABLE)
  - `latitude`, `longitude` (number)
  - `createdAt`, `updatedAt` (date-time)
- **Olası Hatalar:** `401 Unauthorized`, `403 Forbidden` (CUSTOMER değilse)

### 3.2. Müsait Araç Detayı
- **Endpoint:** `GET /vehicles/{id}`
- **Header:** `Authorization: Bearer <accessToken>`
- **Path Parametreleri:** `id` (string, zorunlu)
- **Açıklama:** Tek bir `AVAILABLE` aracın detayını döner.
- **Response (200 - `VehicleResponseDto`):** Araç veri nesnesi.
- **Olası Hatalar:** `401 Unauthorized`, `403 Forbidden`, `404 Not Found` (Bulunamadı veya müsait değil)

---

## 4. Kiralama (Rentals)

### 4.1. Araç Kirala (Anlık)
- **Endpoint:** `POST /rentals`
- **Header:** `Authorization: Bearer <accessToken>`
- **Açıklama:** Sadece `CUSTOMER` rolü işlem yapabilir. `startDate` anlıktır (sunucu belirliyor).
- **Request Body (`CreateRentalDto`):**
  - `vehicleId` (string, zorunlu)
  - `endDate` (date-time, zorunlu, şimdiki zamandan ileride olmalı)
- **Response (201 - `RentalResponseDto`):**
  - `id` (string), `userId` (string), `vehicleId` (string)
  - `startDate`, `endDate` (date-time)
  - `totalPrice` (number)
  - `status` (enum: ACTIVE, COMPLETED, CANCELLED)
  - `createdAt` (date-time)
- **Olası Hatalar:** `400 Bad Request`, `401 Unauthorized`, `403 Forbidden` (CUSTOMER değilse), `404 Not Found`, `409 Conflict` (Araç AVAILABLE değil veya kullanıcının halihazırda aktif kiralaması var)

### 4.2. Kiralamalarım
- **Endpoint:** `GET /rentals`
- **Header:** `Authorization: Bearer <accessToken>`
- **Açıklama:** Kullanıcının geçmiş ve aktif tüm kiralamalarını (yeniden eskiye) listeler.
- **Response (200 - List<`RentalResponseDto`>):** Kiralamalar listesi.
- **Olası Hatalar:** `401 Unauthorized`, `403 Forbidden`

### 4.3. Kiralamayı İade Et
- **Endpoint:** `POST /rentals/{id}/return`
- **Header:** `Authorization: Bearer <accessToken>`
- **Path Parametreleri:** `id` (string, zorunlu)
- **Açıklama:** Kullanıcının aktif olan kiralama işlemini tamamlar (`COMPLETED`) ve aracı tekrar `AVAILABLE` yapar.
- **Response (200 - `RentalResponseDto`):** Güncel kiralama durumu.
- **Olası Hatalar:** `401 Unauthorized`, `403 Forbidden` (Başkasının kiralaması), `404 Not Found`, `409 Conflict` (Kiralama zaten iade edilmiş veya iptal edilmiş)

---

## 5. Uygulama İçi (UI) İş Mantığı Entegrasyonu
- **Token Handling:** API istekleri 401 döndürdüğünde, Interceptor/Authenticator arka planda `POST /auth/refresh` atmalı ve isteği yeni token ile yenilemelidir.
- **Rol Yetkisi:** Yeni kaydedilen bir `PENDING` kullanıcı "Kirala" sayfasına gitmek istediğinde 403 yememek için UI tarafında kiralama butonları gizli tutulmalı veya `PENDING -> Ehliyet Yükleme` ekranına yönlendirilmelidir.
- **UI State (Offline/Cache):** `GET /vehicles` vb. sorgular sonucu Room'a yazılmalı. Ancak kiralama (`POST /rentals`) gibi mutasyon işlemlerinde muhakkak network kontrolü şarttır.

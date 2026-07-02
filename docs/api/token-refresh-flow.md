# Token Refresh (Rotation) Akışı

Bu doküman, kullanıcı oturumlarının güvenli bir şekilde sürdürülebilmesi için uygulanacak `Token Rotation` (Token Döndürme) mekanizmasını tanımlar. Kurallar `openapi.json` ve `docs/decisions.md` kararlarına dayanmaktadır.

## 1. Temel Kurallar

1. **Kısa Ömürlü Access Token:** Korumalı endpoint'lere (örn. `/vehicles`, `/rentals`) erişim için kullanılır (`Authorization: Bearer <token>`).
2. **Tek Kullanımlık Refresh Token:** `POST /auth/refresh` endpoint'ine gönderilerek **yeni bir access + refresh token çifti** almak için kullanılır.
3. **Rotation (Döndürme):** Bir refresh token kullanıldığında anında geçersiz (iptal) olur ve yerine API'den dönen yepyeni refresh token kaydedilir.
4. **Reuse (Tekrar Kullanım) Koruması:** Eğer çalınmış veya daha önceden kullanılmış bir refresh token ile tekrar istek atılırsa, API `401 Unauthorized` döner ve o kullanıcıya ait **tüm aktif oturumları güvenlik gerekçesiyle iptal eder**.

## 2. İstemci (Android) Mimari Entegrasyonu

Android tarafında Retrofit ve OkHttp kullanılarak otomatik bir Refresh mekanizması kurulmalıdır:

### OkHttp Authenticator
Uygulama herhangi bir API isteğinden `401 Unauthorized` hatası aldığında, OkHttp'nin `Authenticator` arayüzü devreye girer:
1. `Authenticator` orijinal isteği duraklatır.
2. Senkron bir şekilde `POST /auth/refresh` çağrısı yapar (Request Body: `RefreshTokenDto`).
3. Dönen yeni `AuthResponseDto` içerisindeki `accessToken` ve `refreshToken` DataStore/EncryptedSharedPreferences'a kaydedilir.
4. Orijinal API isteği, yeni `accessToken` ile (Header güncellenerek) tekrar fırlatılır.
5. Eğer Refresh isteğinin kendisi de `401` dönerse, kullanıcının oturumu yerelde tamamen kapatılır (tokenlar silinir) ve **Login** ekranına yönlendirilir.

## 3. Rol Güncellemesi (Sessiz Refresh)
Kullanıcının ehliyeti onaylandığında rolü backend'de `PENDING` -> `CUSTOMER` olarak değişir. Ancak mobil cihazdaki mevcut access token hala `PENDING` rolünü içerir (Token payload'unda rol bulunur).
Bu durumu aşmak için:
- Onay bekleme ekranında (veya belirli aralıklarla) ehliyetin onaylandığı (`/license/status` -> `APPROVED`) tespit edildiğinde, mobil uygulama **arkaplanda manuel olarak** `POST /auth/refresh` çağrısı yapmalıdır.
- API'den dönecek yeni token'lar `CUSTOMER` rolünü barındıracağı için, kullanıcı şifre girmeye gerek kalmadan `CUSTOMER` yetkilerine (kiralama yapabilme) kavuşur ve UI bu yeni yetkiye göre (Örn: "Kirala" butonu aktifleşir) kendini günceller.

## 4. Eşzamanlı (Concurrency) İstekler Sorunu
Uygulama aynı anda 3 farklı API isteği atarsa ve üçü de `401` alırsa, hepsi aynı anda refresh atmaya çalışabilir. Bu durum "Refresh Token Reuse" kuralını tetikleyip oturumu çökertebilir.
- **Çözüm:** Authenticator içerisindeki refresh çağrısı `Mutex` veya `synchronized` bloğu ile korunmalı, sadece ilk gelen thread refresh atmalı, bekleyen diğer thread'ler ilk thread'in aldığı yeni token'ı kullanmalıdır.

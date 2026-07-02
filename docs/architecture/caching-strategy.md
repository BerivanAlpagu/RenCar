# Önbellekleme (Caching) Stratejisi

Bu doküman, uygulamanın ağ trafiğini azaltmak, performansını artırmak ve çevrimdışı (offline) kullanım deneyimini iyileştirmek için kullanılacak yerel önbellekleme mimarisini tanımlar.

## 1. Temel Yaklaşım: Single Source of Truth (SSOT)

Uygulama genelinde **Tek Gerçek Kaynak (Single Source of Truth)** prensibi benimsenecektir.
- UI (Jetpack Compose) katmanı veriyi doğrudan API'den (Network) **okumaz**.
- Ağ (Network) istekleri sadece yerel veritabanını (Room Database) veya önbelleği (Cache) günceller.
- UI, bir `Flow` (veya `PagingData`) aracılığıyla her zaman yerel veritabanındaki güncel durumu (State) dinler.

## 2. Kullanılacak Teknolojiler

| Veri Tipi | Kullanılan Araç | Gerekçe |
| :--- | :--- | :--- |
| **Büyük Listeler (Araçlar vb.)** | Room Database | Çevrimdışı destek, hızlı arama/filtreleme, Paging 3 entegrasyonu. |
| **Token & Hassas Veriler** | EncryptedSharedPreferences (veya Proto DataStore) | `accessToken` ve `refreshToken` gibi verilerin güvenli saklanması. |
| **Kullanıcı Tercihleri** | Preferences DataStore | Tema, dil vb. basit ayarların asenkron ve güvenli saklanması. |

## 3. Senaryo Bazlı Caching Kuralları

### 3.1. Araç (Vehicle) Listesi Caching
- Müşterilere açık araç listesi (`GET /vehicles`), uygulamanın ana akışı olduğu için **Room Database + RemoteMediator** ile önbelleklenecektir.
- **Akış:**
  1. UI araçları listelemek ister, Room DAO üzerinden `PagingSource` okunur.
  2. Cache boşsa, eskiyse veya sayfalama (pagination) sınırına gelinirse `RemoteMediator` API isteğini tetikler.
  3. API'den gelen araç listesi (`VehicleResponseDto` -> `VehicleEntity`) Room'a kaydedilir (insert/replace).
  4. UI, DAO'yu Flow ile dinlediği için otomatik olarak güncellenen Room verisini ekrana çizer.

### 3.2. Kullanıcı Oturumu (Auth) ve Token
- `AuthResponseDto` içerisinden çıkan `accessToken` ve `refreshToken` cihazın güvenli hafızasında şifreli (Encrypted) olarak tutulur.
- Profil bilgileri (`UserResponseDto`) Room içerisinde tek satırlık bir tablo (UserEntity) veya DataStore olarak önbelleklenir. Böylece uygulama yeniden başlatıldığında, `GET /auth/me` isteğinin tamamlanması beklenmeden kullanıcının ismi ve rolü (`PENDING`/`CUSTOMER`) UI'da hemen gösterilir.

### 3.3. Geçmiş Kiralamalar (Rentals)
- `GET /rentals` (Kiralamalarım) verisi Room veritabanına kopyalanır. Kullanıcı çevrimdışı iken de geçmiş kiralamalarını görebilir.
- Ancak yeni bir kiralama yaparken (`POST /rentals`) ağ bağlantısı **zorunludur**. Mutasyon işlemleri (yazma/kiralama) cache'e atılmaz, cihaz çevrimdışı ise UI tarafında Error Dialog (Network Error) gösterilir.

## 4. Cache Geçerliliği (Eviction / Invalidation)
- Uygulama arka plandan ön plana çıktığında (OnResume / Lifecycle) veya kullanıcı manuel olarak "Pull-to-Refresh" (Aşağı çekerek yenile) aksiyonu aldığında, API çağrısı zorlanarak Room veritabanındaki kayıtlar güncellenir.
- Kullanıcı çıkış yaptığında (`POST /auth/logout`), güvenliği sağlamak adına Room veritabanındaki ilgili tüm tablolar (`clearAllTables` veya User/Rental entity'lerinin silinmesi) ve Token'ları tutan DataStore içerikleri kalıcı olarak silinir (Wipe data).

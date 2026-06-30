# RenCar Android Terimler ve Sözlük (Glossary) Kılavuzu

Bu doküman, RenCar Android projesinde, veri modellerinde, backend API sözleşmelerinde (`openapi.json`) ve ekran tasarımlarında (`rencar.pdf`) kullanılan ortak teknik ve işlevsel terimleri tanımlar.

---

## 1. Kullanıcı Rolleri (User Roles)

Sistem yetkilendirmesi üç temel rol üzerine kuruludur. Rollerin değişimi API seviyesinde yönetilir:

- **PENDING (Beklemede/Onaysız):** Sisteme yeni kayıt olmuş (`POST /auth/register`) ancak ehliyet doğrulaması henüz tamamlanmamış kullanıcıdır. Araçları ve fiyatları listeleyebilir, profilini görebilir ancak kiralama yapamaz. Korumalı müşteri uç noktalarından `403 Forbidden` yanıtı alır.
- **CUSTOMER (Müşteri/Onaylı):** Ehliyet belgesi bir yönetici (ADMIN) tarafından onaylanmış ve araç kiralamaya yetkilendirilmiş kullanıcıdır.
- **ADMIN (Yönetici):** Ehliyet onay süreçlerini yöneten, sisteme yeni araç ekleyen (`POST /admin/vehicles`) ve mevcut araçların durumunu güncelleyen rol.

---

## 2. Ehliyet Doğrulama Durumları (License Statuses)

Kullanıcının ehliyet onay durumunu ve kiralama yetkisini belirten durum kodları (`GET /license/status` API yanıtı):

- **NOT_SUBMITTED (Yüklenmedi):** Kullanıcı ehliyet ön ve arka yüz fotoğraflarını henüz sisteme göndermemiştir.
- **UNDER_REVIEW (İncelemede):** Kullanıcı ehliyet görsellerini başarıyla sisteme yüklemiş (`POST /license/upload`) ve doğrulama sırasına girmiştir.
- **APPROVED (Onaylandı):** Ehliyet incelemesi başarılı olmuştur. Bu durumda arka planda kullanıcının rolü otomatik olarak `CUSTOMER` yapılır.
- **REJECTED (Reddedildi):** Yüklenen belgeler (örn: okunamayan fotoğraf, geçersiz ehliyet) yöneticiler tarafından reddedilmiştir. API'den `rejectionReason` (redgerekçesi) döner. Kullanıcı yeniden yükleme yapabilir.

---

## 3. API ve Güvenlik Terimleri

- **JWT (JSON Web Token):** Kullanıcının kimliğini doğrulamak için her ağ isteğinde `Authorization: Bearer <token>` başlığı altında iletilen kısa ömürlü erişim anahtarı.
- **Token Rotation (Tek Kullanımlık Refresh Token):** Güvenlik amacıyla, `access-token` süresi dolduğunda (`401 Unauthorized`), yerelde saklanan `refresh-token` kullanılarak yeni bir çift alınması süreci. Kullanılan eski refresh token anında iptal edilir.
- **Refresh Reuse (Tekrar Kullanım İhlali):** Eski/kullanılmış bir refresh token sisteme tekrar gönderilirse, API güvenlik ihlali algılar ve o kullanıcıya ait tüm aktif oturumları anında sonlandırır.
- **Multipart / Form-Data:** Ehliyet görsellerinin (ön ve arka yüz) ikili veri (`binary`) formatında yüklenmesini sağlayan HTTP istek tipi.

---

## 4. Kiralama ve Rezervasyon Terimleri

- **Active Rental (Aktif Kiralama):** Rezervasyon onaylandıktan ve hasar fotoğrafları yüklenip kiralama başlatıldıktan sonra başlayan süreç. Süre ve ücret yerelde ve backend'de dinamik olarak hesaplanır.
- **Return (İade):** Aracı teslim etme işlemi. Kullanıcı aracı teslim ettiğinde kiralama sonlandırılır (`POST /rentals/{id}/return`) ve kiralama özeti ekranı gösterilir.
- **Dakikalık / Saatlik / Günlük Planlar:** Kullanıcının kiralama onay ekranında seçebileceği, farklı baz ve dakika fiyatlandırma çarpanlarına sahip kiralama tarifeleri.
- **Hasar Tespiti (Damage Check):** Araç kiralanmadan önce kullanıcının aracın 4 farklı yönünden (Ön, Arka, Sol, Sağ) çekip yüklediği hasar kayıt fotoğrafları.

---

## 5. Uygulama İçi Mimari Terimleri

- **UDF (Unidirectional Data Flow):** Verinin tek bir yönde aktığı (State -> UI -> Intent -> ViewModel -> State) akış tasarımı.
- **Recomposition (Yeniden Çizim):** Jetpack Compose'un, değişen veri durumlarına göre arayüz bileşenlerini otomatik olarak yeniden çizmesi süreci.
- **Offline Caching:** İnternet bağlantısı olmasa dahi kullanıcının geçmiş kiralamalarını ve son yüklenen araç listesini görebilmesi için verilerin yerel Room veri tabanına kaydedilmesi işlemi.

# API Hata Yanıtları (Error Responses) Stratejisi

Bu doküman, `openapi.json` (Swagger) içerisindeki endpoint'lerden dönebilecek hata kodlarını ve Android uygulamasının (Retrofit / Interceptor katmanında) bu hataları nasıl yorumlayıp (handle edip) kullanıcıya veya iş mantığına yansıtacağını tanımlar.

## 1. Genel Hata Yönetimi
Network isteklerinden dönen hatalar `Result` (veya `ApiResult`) wrapper sınıflarına çevrilerek UI katmanına iletilir. API'den gelen spesifik hata mesajları UI'a yansıtılabilir veya yerelleştirilmiş (localized) statik metinler gösterilebilir.

---

## 2. HTTP Status Kodları ve Karşılıkları

### 400 Bad Request
- **Anlamı:** İstemci tarafında eksik/hatalı veri gönderimi (Validation).
- **Örnek Durumlar:**
  - Geçersiz DTO (örn. `endDate`'in geçmişte olması).
  - Ehliyet yüklerken dosya eksikliği veya geçersiz dosya tipi (sadece jpg/png kabul edilir).
- **Aksiyon:** Kullanıcıya "Girdiğiniz bilgileri kontrol ediniz" uyarısı veya spesifik validasyon hatası gösterilir.

### 401 Unauthorized
- **Anlamı:** Kimlik doğrulama hatası (Token eksik, geçersiz veya süresi dolmuş).
- **Örnek Durumlar:**
  - Hatalı giriş bilgileri (`/auth/login`).
  - Refresh token geçerliliğini yitirmiş veya tekrar (reuse) kullanılmış (`/auth/refresh`).
  - Access token'ın süresinin dolması.
- **Aksiyon:** 
  - Login ekranında ise: "E-posta veya parola hatalı".
  - Diğer ekranlarda ise: OkHttp `Authenticator` otomatik olarak `/auth/refresh` tetikler. Refresh işlemi de 401 dönerse oturum tamamen kapatılır, token'lar temizlenir ve kullanıcı Login ekranına yönlendirilir.

### 403 Forbidden
- **Anlamı:** Yetki yetersizliği (Rol tabanlı kısıtlamalar).
- **Örnek Durumlar:**
  - `PENDING` kullanıcının kiralama (`/rentals`) işlemi yapmaya çalışması.
  - `CUSTOMER` kullanıcının `/admin` endpoint'lerine erişmek istemesi.
  - Bir kullanıcının başkasına ait kiralamayı iade (`/rentals/{id}/return`) etmeye çalışması.
- **Aksiyon:** Kullanıcıya "Bu işlemi yapmak için yetkiniz bulunmuyor" uyarısı gösterilir. (`PENDING` ise kiralama butonları disable bırakılır veya ehliyet onayı ekranına yönlendirilebilir).

### 404 Not Found
- **Anlamı:** Talep edilen kaynak bulunamadı.
- **Örnek Durumlar:**
  - Belirtilen ID'ye sahip araç veya kiralama kaydının olmaması.
  - Araç `AVAILABLE` durumunda değilse `/vehicles/{id}` endpoint'inden döner.
- **Aksiyon:** "Aradığınız kayıt bulunamadı" veya "Araç şu anda müsait değil" uyarısı ile liste sayfasına/önceki ekrana dönülür.

### 409 Conflict
- **Anlamı:** İş kuralı (Business logic) çakışması.
- **Örnek Durumlar:**
  - Kayıt esnasında e-postanın zaten var olması (`/auth/register`).
  - Zaten CUSTOMER olan veya incelemede ehliyeti olan birinin tekrar ehliyet yüklemeye çalışması (`/license/upload`).
  - Kiralanmak istenen aracın müsait olmaması VEYA kullanıcının halihazırda aktif bir kiralamasının bulunması (`/rentals`).
  - İptal edilmiş/İade edilmiş kiralamanın tekrar iade edilmeye çalışılması.
- **Aksiyon:** Kullanıcıya çakışma nedenini belirten spesifik uyarı gösterilir (Örn: "Zaten aktif bir kiralamanız bulunmaktadır").

### 413 Payload Too Large
- **Anlamı:** Yüklenen dosya boyutunun üst sınırı aşması.
- **Örnek Durumlar:** 
  - `/license/upload` işleminde 5MB sınırının aşılması.
- **Aksiyon:** Kullanıcıya "Yüklediğiniz fotoğraf 5MB'dan küçük olmalıdır" uyarısı gösterilir.

---

## 3. Mimari Entegrasyon (OkHttp & Retrofit)
Hata yönetimi uygulama genelinde merkezi bir mekanizma veya `suspend` fonksiyonlarını sarmalayan bir yapı (örneğin `safeApiCall`) üzerinden gerçekleştirilecektir.

```kotlin
sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val code: Int, val message: String?) : ApiResult<Nothing>()
    object NetworkError : ApiResult<Nothing>() // İnternet yok vs.
}
```
UI katmanı sadece bu `ApiResult` durumlarını dinleyerek hataları (Snackbar, Dialog vb.) ile kullanıcıya yansıtır. Doğrudan Exception yakalama işlemleri Data katmanında (Repository) sonlanmalıdır.

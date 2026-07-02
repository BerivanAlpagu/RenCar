# Hata Yönetimi (Error Handling) Mimarisi

Bu doküman, uygulamanın farklı katmanlarında (Data, Domain, Presentation) meydana gelebilecek hataların (Network, Database, Business Logic vb.) nasıl yakalanıp yönetileceğini ve son kullanıcıya nasıl sunulacağını tanımlar.

## 1. Temel Prensip

- **Exception Fırlatmak Yok:** Katmanlar arası iletişimde (özellikle Data -> Domain -> UI arasında) iş mantığı akışını bozacak `Exception` (throw) fırlatılmasından kaçınılır.
- **Sealed Class ile Sarmalama:** Tüm hatalar (öngörülebilen veya öngörülemeyen), kontrollü bir şekilde bir Wrapper (Sarmalayıcı) sınıf içerisinde döndürülür.

## 2. Ortak Hata Modeli (Result / Resource)

Uygulama genelinde Data ve Domain katmanı arasında aşağıdaki (veya benzeri) bir yapı kullanılacaktır:

```kotlin
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(
        val code: Int? = null,
        val message: String,
        val type: ErrorType = ErrorType.UNKNOWN
    ) : Result<Nothing>()
}

enum class ErrorType {
    NETWORK, HTTP, VALIDATION, UNAUTHORIZED, CONFLICT, UNKNOWN
}
```

## 3. Katman Bazlı Hata Yönetimi

### 3.1. Data Katmanı (API & Repository)
API istekleri atılırken `try-catch` blokları doğrudan Repository katmanında veya yardımcı bir `safeApiCall` extension fonksiyonunda değerlendirilmelidir. 

- **HTTP Hataları (4xx, 5xx):** Retrofit'in `HttpException` fırlatması durumunda, hata içeriği parse edilerek (Bkz: `docs/api/error-responses.md`) spesifik mesaj çıkarılır ve `Result.Error` olarak dönülür.
- **Network Hataları (IOException vb.):** İnternet kesintisi veya Timeout gibi durumlar yakalanıp `Result.Error(type = ErrorType.NETWORK)` olarak sarmalanır.

### 3.2. Domain Katmanı (Use Cases)
Use Case'ler Data katmanından gelen `Result` objesini alıp gerekirse iş kurallarını uygular. Örneğin bir hata alındığında lokal veritabanından yedek veri dönme kararı (Fallback) Domain katmanında verilebilir.

### 3.3. Presentation Katmanı (ViewModel & UI)
ViewModel, dönen `Result.Error` tipine göre UI'ı yönlendirir.
- **State Güncellemesi:** Tam ekran hata durumları (Örn: Sayfa yüklenemedi, Empty State) için UI State `isError = true` ve `errorMessage = "..."` şeklinde güncellenir.
- **One-time Event (Side Effect):** Butona tıklandıktan sonra dönen spesifik bir hata (Örn: Hatalı şifre, veya Zaten Aktif Kiralama Var - 409) Toast/Snackbar/Dialog olarak gösterilmek isteniyorsa, MVI mimarisindeki `Effect` (Side Effect / Channel) kanalı üzerinden UI'a sadece tek seferlik iletilir.

```kotlin
// ViewModel Örneği
when (val result = useCase()) {
    is Result.Success -> {
        _state.update { it.copy(isLoading = false, data = result.data) }
    }
    is Result.Error -> {
        _state.update { it.copy(isLoading = false) }
        _effect.send(UiEffect.ShowError(result.message))
    }
}
```

## 4. Kullanıcı Deneyimi (UX) Kuralları
- Teknik sistem hataları (Örn: "java.net.UnknownHostException", "SocketTimeoutException") **asla** son kullanıcıya gösterilmez. Bunun yerine, "İnternet bağlantınızı kontrol ediniz" gibi kullanıcı dostu ve anlaşılır string metinleri (yerelleştirilmiş - strings.xml) kullanılır.
- Uygulamanın aniden çökmesine (Crash) neden olabilecek durumlar Repository/UseCase sınırlarında yakalanmalı ve UI sadece durumu bilmelidir.

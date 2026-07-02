# Durum (State) Yönetimi Stratejisi

Bu doküman, uygulamanın anlık verilerini (yükleniyor, hata, başarı durumları vb.) UI ile ViewModel arasında tutarlı bir şekilde nasıl paylaşacağını tanımlar. (Bu doküman [mvi-overview.md](mvi-overview.md) ile birlikte okunmalıdır).

## 1. Temel Araç: StateFlow
- Eskiden kullanılan `LiveData` veya `RxJava` yerine, tüm proje genelinde **Kotlin Coroutines `StateFlow`** yapısı kullanılacaktır.
- `StateFlow` her zaman bir başlangıç (initial) değerine sahip olmalı ve içinde her zaman tek bir güncel durumu barındırmalıdır. (Tıpkı Compose'un beklentisi gibi).

```kotlin
// Örnek ViewModel Tanımı
private val _state = MutableStateFlow(LoginState())
val state = _state.asStateFlow() // UI'a sadece okunabilir halini (Read-only) açarız.
```

## 2. Compose'da State'i Dinlemek (collectAsStateWithLifecycle)
Jetpack Compose ekranlarında StateFlow'u dinlerken sadece `collectAsState()` kullanmak, uygulama arka plana (Background) alındığında bile veri akışını sürdürmesine neden olur. Bu pil tüketimi ve gereksiz kaynak kullanımı yaratır.
- **Kural:** Her zaman `collectAsStateWithLifecycle()` kullanılmalıdır. Bu sayede ekran (Activity/Fragment) "Lifecycle.State.STARTED" durumundan düştüğünde akış otomatik duraklatılır, ekrana dönüldüğünde tekrar başlar.

## 3. UI State Modellemesi (Sealed Class vs Data Class)

State yönetimi yapılırken ekranın karmaşıklığına göre iki farklı yaklaşım kullanılır:

### Yöntem A: Data Class (Önerilen)
Aynı anda hem veriyi gösterip hem de küçük bir "loading" döndürmek istiyorsak (Örn: Veri varken pull-to-refresh yapılması durumu), tüm durumlar tek bir obje içinde birleşir.
```kotlin
data class VehicleListState(
    val isLoading: Boolean = false,
    val vehicles: List<Vehicle> = emptyList(),
    val errorMessage: String? = null
)
```

### Yöntem B: Sealed Interface (Sıkı Ekranlar)
Bir ekranın durumları birbirini tamamen eziyorsa (Örn: Ya Empty Screen, Ya Loading Screen, Ya da Veri Ekranı varsa), Sealed yapısı kullanılarak hata yapma payı (Örn: Hem isLoading=true hem hata dolu olması) engellenir.
```kotlin
sealed interface VehicleListState {
    object Loading : VehicleListState
    data class Success(val vehicles: List<Vehicle>) : VehicleListState
    data class Error(val message: String) : VehicleListState
}
```

## 4. Derived (Türetilmiş) Durumlar
UI içerisinde, asıl State'ten türetilebilen veriler için ViewModel'de ayrı değişkenler tutulmaz. Compose tarafında hesaplanır.
- Örneğin; `List<Vehicle>` state'te mevcuttur. Ekrandaki "Toplam X araç bulundu" yazısı ayrı bir state değildir, Compose içerisinde doğrudan `state.vehicles.size` ile türetilir (Veya performans için `derivedStateOf` kullanılır). Bu yaklaşım State'in şişmesini engeller.

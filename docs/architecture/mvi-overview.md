# MVI (Model-View-Intent) Mimarisi

RenCar Android projesi, Jetpack Compose'un bildirimsel (declarative) yapısıyla en uyumlu çalışan **MVI (Model-View-Intent)** tasarım desenini (pattern) temel alır.

## 1. MVI Nedir?

MVI, Tek Yönlü Veri Akışını (UDF - Unidirectional Data Flow) en katı şekilde uygulayan UI desenidir.
- **Model (State):** O anki ekranın tam bir anlık görüntüsüdür (Snapshot). Ekranda tam olarak ne görünmesi gerektiğini belirler.
- **View (Compose):** Modeli (State) alıp doğrudan ekrana çizen, hiçbir iş mantığı (business logic) barındırmayan saf arayüz (UI) katmanıdır.
- **Intent (Event):** Kullanıcının (veya sistemin) View üzerinde yaptığı aksiyonların (Örn: Tıklama, Kaydırma, Sayfa Yükleme) ViewModel'e iletilme niyetidir.

## 2. MVI Sözleşmesi (Contract) Bileşenleri

Her feature/ekranın (Örn: `LoginScreen`) bir Sözleşmesi (Contract) olmalıdır. Bu contract 3 temel bileşen içerir:

### 2.1. State (Durum)
Ekrandaki tüm verileri (loading durumu, text inputlar, gelen veriler vb.) tutan `data class` yapısıdır.
```kotlin
data class LoginState(
    val isLoading: Boolean = false,
    val email: String = "",
    val error: String? = null
)
```
- ViewModel içerisinde `MutableStateFlow` olarak tutulur.
- Compose içerisinde `collectAsStateWithLifecycle()` ile güvenli bir şekilde dinlenir.

### 2.2. Intent / Event (Kullanıcı Aksiyonları)
Kullanıcının ekranda tetikleyebileceği her aksiyon bir `sealed interface` (veya `sealed class`) içinde toplanır.
```kotlin
sealed interface LoginEvent {
    data class EmailChanged(val value: String) : LoginEvent
    object LoginButtonClicked : LoginEvent
}
```
- ViewModel, bu eventleri tek bir public fonksiyon (örn: `onEvent(event: LoginEvent)`) aracılığıyla alır. Bu fonksiyon içinde `when` bloğu ile gerekli UseCase'leri tetikler.

### 2.3. Effect / Side Effect (Yan Etkiler)
State'in aksine, ekranda sadece **tek bir kez** gerçekleşmesi gereken (Örn: Toast göstermek, başka sayfaya yönlenmek (Navigate)) tek atımlık olaylar için kullanılır.
```kotlin
sealed interface LoginEffect {
    object NavigateToHome : LoginEffect
    data class ShowSnackbar(val message: String) : LoginEffect
}
```
- ViewModel'de event kaybını önlemek için genellikle bir `Channel` kullanılır ve `receiveAsFlow()` ile dışarı açılır.
- Compose içerisinde `LaunchedEffect` bloğu altında collect edilir.

## 3. Compose ile MVI Entegrasyon Akışı

1. Compose ekranı yüklenir, `viewModel.state.collectAsStateWithLifecycle()` ile State'i dinlemeye başlar.
2. Kullanıcı "Giriş" butonuna tıklar: `viewModel.onEvent(LoginEvent.LoginButtonClicked)`.
3. ViewModel `isLoading = true` yapar, StateFlow güncellenir, Compose UI (View) kendisini tekrar çizer (Recomposition) ve butonu yükleniyor moduna alır.
4. ViewModel UseCase üzerinden API'ye gider.
5. API başarılı döner, ViewModel `LoginEffect.NavigateToHome` Effect'ini fırlatır.
6. Compose tarafındaki `LaunchedEffect` bu Effect'i yakalar ve Ana Ekrana geçişi (Navigation) gerçekleştirir.

## 4. Neden Compose ile Birlikte MVI?
- **Tek Gerçek Kaynak (SSOT):** Ekranın durumu parçalı State'ler (farklı LiveData'lar) halinde değil, tek bir Obje (State) içinde tutulduğu için tutarsızlık (Örn: Hata varken yükleniyor ikonunun da görünmesi) ihtimali ortadan kalkar.
- **Test Edilebilirlik:** ViewModel testleri çok kolaydır. "Eğer X eventi yollanırsa, State objesinin içindeki Y değişkeni güncelleniyor mu?" şeklinde %100 kapsama (coverage) ulaşılabilir.
- **Debug Kolaylığı:** Herhangi bir hatada, sadece o anki State objesinin log'una bakarak ekranın neden o şekilde çizildiği anında anlaşılabilir.

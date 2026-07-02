# RenCar Android Bileşen Kuralları (Component Rules)

Bu doküman, RenCar Android mobil uygulamasında Jetpack Compose ile yazılacak olan özel bileşenlerin (`Custom Composables`) mimari kurallarını, girdi/çıktı davranışlarını ve tasarım belgesi (`rencar.pdf`) doğrultusundaki standartlarını tanımlar.

---

## 1. Temel Bileşen Standartları ve Kuralları

Tüm arayüz bileşenleri oluşturulurken şu 4 kurala kesinlikle uyulmalıdır:

1. **Modifier Parametresi Alma:** Her Composable fonksiyon, hiyerarşideki en dışta yer alan layout'a uygulanmak üzere bir `modifier: Modifier = Modifier` parametresi almalıdır. Bu, bileşenin çağrıldığı üst katmanda boyutlandırma ve boşluklandırma işlemlerinin esnekçe yapılmasını sağlar.
2. **Stateless (Durumsuz) Tasarım:** Bileşenler kendi içlerinde durum (`State`) barındırmamalıdır (State Hoisting kuralı). Veriler yukarıdan parametre olarak geçilmeli (`Data Down`), tıklamalar ve etkileşimler ise event olarak yukarıya fırlatılmalıdır (`Events Up`).
3. **Önizleme Desteği:** Her bileşen için en az bir adet `@Preview` fonksiyonu yazılmalıdır (Açık ve Koyu tema uyumlu).
4. **Erişilebilirlik Uyumu:** Bileşenlerin TalkBack odak çerçeveleri ve içerik açıklamaları bileşen içerisinde varsayılan olarak doğru şekilde set edilmelidir (Bkz. [Erişilebilirlik Kılavuzu](accessibility.md)).

---

## 2. Özel Ortak Bileşenler (Core Components)

### 2.1. Butonlar (Buttons)
Tasarımda iki ana buton tipi mevcuttur: `PrimaryButton` ve `SecondaryButton` (`rencar.pdf` Sayfa 11 "Rezerve Et" ve "Kilidi Aç" butonları).

```kotlin
@Composable
fun RencarPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .pressClickEffect(onClick), // animations.md mikro-etkileşim
        enabled = enabled && !isLoading,
        shape = MaterialTheme.shapes.medium, // 12.dp corner radius
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}
```

### 2.2. Giriş ve OTP Alanları (Text Fields)
Telefon numarası girişi ve 6 haneli OTP kutuları (`rencar.pdf` Sayfa 5):

- **OTP Giriş Kutusu:** 6 adet tek karakterlik kutudan oluşan bu bileşen, girilen karakterleri liste olarak almalı ve odak değişimini otomatik yönetmelidir.

```kotlin
@Composable
fun OtpCell(
    value: String,
    onValueChange: (String) -> Unit,
    isFocused: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.length <= 1) onValueChange(it)
        },
        modifier = modifier
            .width(48.dp)
            .height(56.dp),
        textStyle = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlignment.Center),
        singleLine = true,
        shape = MaterialTheme.shapes.small, // 8.dp
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.secondary
        )
    )
}
```

### 2.3. Araç Detay ve Özet Kartları (Cards)
- **Araç Kartı:** Müsait araçları listeleyen ve harita üzerinde veya bottom sheet'te gösterilen kartlar.
- **Kural:** Araç kartında mutlaka görsel yükleme durumu (loading / error placeholder) ele alınmalı, resim yüklenene kadar `shimmerBrush` animasyonu gösterilmelidir.

---

## 3. Ekran Seviyesi Bileşenleri (Screen Composables)

MVI (Model-View-Intent) mimarisinde ekranlar `Screen` ekine sahip olur (Örn: `LoginScreen.kt`, `VehiclesScreen.kt`). 
- Ekran seviyesindeki Composable'lar, `ViewModel` referansını alan ve State'i toplayan (`collectAsStateWithLifecycle`) ana sarmalayıcı olmalıdır.
- ViewModel'den arındırılmış saf UI bileşeni ise `Content` ekiyle (Örn: `LoginContent`) ayrılarak kolayca test edilebilir ve önizlenebilir hale getirilmelidir.

```kotlin
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoginContent(
        uiState = uiState,
        onIntent = viewModel::handleIntent
    )
}
```

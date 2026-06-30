# RenCar Android Jetpack Compose Geliştirme Kılavuzu (Compose Guidelines)

Bu doküman, RenCar Android mobil uygulamasında Jetpack Compose ile arayüz geliştirirken uyulması gereken yazılım standartlarını, performans optimizasyonlarını ve kodlama pratiklerini tanımlar.

---

## 1. Modifiers (Düzenleyiciler) ve Sıralama Kuralları

Compose mimarisinde `Modifier` zincirinin yazım sırası, bileşenin çizimini ve tıklama alanlarını doğrudan etkiler. RenCar projesinde modifier zinciri şu sıraya göre oluşturulmalıdır:

1. **Boyutlandırma (Sizing & Layout Constraints):** `fillMaxWidth()`, `size()`, `height()`, `width()`.
2. **Boşluklar ve Dış Kenar (Padding):** `padding()`.
3. **Erişilebilirlik (Semantics):** `semantics()`, `contentDescription()`.
4. **Arka Plan ve Şekil (Background & Clip):** `background()`, `clip()`.
5. **Etkileşim (Gestures / Clickable):** `clickable()`, `selectable()`.

*Örnek:*
```kotlin
Row(
    modifier = Modifier
        .fillMaxWidth()
        .padding(RencarTheme.spacing.medium) // 1. Padding
        .clip(MaterialTheme.shapes.large)   // 2. Kırpma (Köşe yuvarlama)
        .background(MaterialTheme.colorScheme.surface) // 3. Renk
        .clickable { onClick() }             // 4. Etkileşim
) {
    // İçerik
}
```

---

## 2. Recomposition (Yeniden Çizim) Optimizasyonları

Gereksiz arayüz güncellemelerini önlemek ve 60 FPS / 120 FPS akıcılığı korumak için aşağıdaki kurallar uygulanacaktır:

### 2.1. Kararlı Sınıflar (Stability Rules)
Compose derleyicisinin (compiler) listeleri kararsız (`unstable`) kabul etmesini önlemek amacıyla, listeler doğrudan veri sınıflarında geçilmemelidir.
- Kotlin standart `List` yapısı yerine kotlinx collections veya `@Immutable` sarmalayıcılar kullanılmalıdır.

```kotlin
import androidx.compose.runtime.Immutable

@Immutable
data class VehicleListState(
    val vehicles: List<Vehicle> = emptyList() // Listeyi sarmalayan kararlı sınıf
)
```

### 2.2. Derived State Kullanımı
Başka bir state'in değişimine bağlı olarak hesaplanan durumlarda, recomposition sıklığını düşürmek için `derivedStateOf` kullanılmalıdır. (Örn: Liste kaydırma durumları).

```kotlin
val isScrollToTopButtonVisible by remember {
    derivedStateOf { listState.firstVisibleItemIndex > 0 }
}
```

### 2.3. Lambda Modifier'lar ile Çizim Aşaması Atlama
Sık güncellenen animasyon durumlarında (örn. timer süresi veya kaydırma mesafesine göre değişen boyutlar), recomposition tetiklememek için lambda tabanlı modifier'lar (`graphicsLayer`) kullanılmalıdır.
```kotlin
// YANLIŞ: Recomposition tetikler
Modifier.alpha(animatedAlpha)

// DOĞRU: Sadece çizim (Draw) aşamasını günceller, Recomposition atlanır
Modifier.graphicsLayer { alpha = animatedAlpha }
```

---

## 3. Önizleme (Compose Previews) Standartları

Yazılan tüm UI kodlarının tasarım bütünlüğünü anında görebilmek amacıyla çoklu önizleme annotasyonları tanımlanacaktır.

```kotlin
import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Light Mode - Phone",
    showBackground = true,
    device = "spec:width=411dp,height=891dp"
)
@Preview(
    name = "Dark Mode - Phone",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=411dp,height=891dp"
)
annotation class RencarDevicePreviews
```

Bileşenlerde kullanımı:
```kotlin
@RencarDevicePreviews
@Composable
fun VehicleCardPreview() {
    RencarTheme {
        VehicleSummaryCard(
            vehicleName = "Renault Clio",
            pricePerMin = "₺4,50"
        )
    }
}
```

---

## 4. Hilt ve Navigasyon Entegrasyonu

1. **ViewModels Entegrasyonu:** Composable fonksiyonlara ViewModel nesnesi parametre olarak geçilirken `hiltViewModel()` kullanılmalı ve bu sadece en dıştaki `Screen` seviyesindeki Composable içinde yapılmalıdır.
2. **Type-Safe Navigation:** Compose Navigation 2.8+ ile birlikte gelen Kotlin Serialization tabanlı type-safe rotalar kullanılacaktır. Rotalar ve argümanlar veri sınıfları (`@Serializable`) olarak tanımlanmalıdır.

```kotlin
import kotlinx.serialization.Serializable

@Serializable
sealed interface ScreenRoute {
    @Serializable
    data object Login : ScreenRoute
    
    @Serializable
    data class VehicleDetail(val id: String) : ScreenRoute
}
```

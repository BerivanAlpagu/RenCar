# RenCar Android Animasyon ve Hareket (Animations & Motion) Kılavuzu

Bu doküman, RenCar Android mobil uygulamasında kullanılacak hareket tasarım dili, mikro etkileşimler, ekran geçişleri ve Jetpack Compose animasyon standartlarını tanımlar. Tasarım belgesinde (`rencar.pdf`) yer alan dinamik arayüz deneyimini desteklemek amacıyla oluşturulmuştur.

---

## 1. Hareket Tasarım İlkeleri

RenCar uygulamasındaki tüm animasyonlar üç temel kurala dayanır:
1. **Hızlı ve Akıcı (Fast & Fluid):** Animasyonlar kullanıcının işlemini geciktirmemeli, ortalama **200ms - 300ms** aralığında tamamlanmalıdır.
2. **Fiziksel Doğallık (Natural Physics):** Doğal bir ivmelenme hissi için doğrusal (`linear`) geçişler yerine **Spring (Yay)** ve **Bezier (EaseInOut/Out)** eğrileri kullanılmalıdır.
3. **Anlamlı Geribildirim (Feedback):** Kullanıcı bir butona bastığında veya durum değiştiğinde (örn: ehliyet yükleme tamamlandığında), animasyon durumun değiştiğini doğrulamalıdır.

---

## 2. Yaygın Geçişler ve Zamanlamalar

| Geçiş Türü | Süre (Duration) | Easing / Curve | Compose Karşılığı |
| :--- | :--- | :--- | :--- |
| **Ekran Giriş/Çıkış** | 300 ms | EaseOut | `slideInHorizontally` + `fadeIn` |
| **Bottom Sheet Açılma** | 250 ms | FastOutSlowIn | `ModalBottomSheet` varsayılan animasyonu |
| **Buton Basılma (Scale)**| 100 ms | Spring (Low stiffness) | `animateFloatAsState` |
| **Durum Değişimi (Renk)**| 200 ms | LinearOutSlowIn | `animateColorAsState` |
| **Shimmer (Yükleniyor)** | 1200 ms (Döngü)| Linear | `rememberInfiniteTransition` |

---

## 3. Jetpack Compose Animasyon Örnekleri

### 3.1. Araç Listesi Yükleniyor Göstergesi (Shimmer Effect)
Araç listesi veritabanından veya API'den (`GET /vehicles`) çekilirken boş kartların üzerine uygulanacak parlama (shimmer) animasyonu:

```kotlin
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun shimmerBrush(showShimmer: Boolean = true, targetValue: Float = 1000f): Brush {
    return if (showShimmer) {
        val shimmerColors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f),
        )

        val transition = rememberInfiniteTransition(label = "Shimmer")
        val translateAnimation by transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "ShimmerTranslate"
        )

        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation, y = translateAnimation)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}
```

### 3.2. Buton Tıklama Mikro Etkileşimi (Press Scale)
Butonlara basıldığında hafifçe küçülüp bırakıldığında eski boyutuna dönmesini sağlayan yay temelli ölçeklendirme efekti:

```kotlin
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun Modifier.pressClickEffect(onClick: () -> Unit): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "PressScale"
    )

    return this
        .graphicsLayer(scaleX = scale, scaleY = scale)
        .clickable(
            interactionSource = interactionSource,
            indication = null, // Custom ripple kontrolü için
            onClick = onClick
        )
}
```

### 3.3. Harita / Detay Bottom Sheet Geçişi
Harita üzerinde bir araca tıklanıldığında araç detaylarının (`rencar.pdf` Sayfa 11) aşağıdan yukarıya yumuşak bir şekilde gelmesi için standard `ModalBottomSheet` bileşeninin sunduğu animasyonlar kullanılacak veya özel kart geçişi için `AnimatedVisibility` tercih edilecektir:

```kotlin
AnimatedVisibility(
    visible = isVehicleSelected,
    enter = slideInVertically(
        initialHeight = { it },
        animationSpec = tween(durationMillis = 250, easing = EaseOutQuad)
    ) + fadeIn(),
    exit = slideOutVertically(
        targetHeight = { it },
        animationSpec = tween(durationMillis = 200, easing = EaseInQuad)
    ) + fadeOut()
) {
    VehicleDetailCard(...)
}
```

---

## 4. Performans ve Erişilebilirlik Kısıtlamaları

1. **Gereksiz Yeniden Çizimlerin (Recomposition) Önlenmesi:** Animasyonlu durumlar (`State`) Modifier'lar içinde doğrudan okunmak yerine, lambda tabanlı Modifier (`graphicsLayer { scaleX = scale }` gibi) ile çağrılmalıdır. Bu sayede recomposition aşaması atlanarak doğrudan çizim (`Draw`) aşamasında güncelleme yapılır.
2. **Animasyonları Kapatma Desteği:** Sistem ayarlarında "Animasyonları kaldır/azalt" seçeneği aktif olan kullanıcılar için animasyon geçişleri otomatik olarak devre dışı kalmalı (`fadeIn/fadeOut` veya anlık geçişe indirgenmeli) ya da süreleri sıfırlanmalıdır.
3. **Zamanlayıcı Güncelleme Sıklığı:** Kiralama süresi gibi dinamik saat sayaçları animasyonlu geçişlerle değil, her saniyede bir durağan şekilde güncellenerek ekran okuyucunun (TalkBack) performans kaybı yaşamasını önlemelidir.

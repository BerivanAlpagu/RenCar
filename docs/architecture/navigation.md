# Navigasyon (Navigation) Stratejisi

Bu doküman, Android uygulamasında ekranlar arası geçişlerin (Routing) ve veri taşıma (Argument passing) işlemlerinin nasıl yönetileceğini tanımlar.

## 1. Temel Teknoloji: Jetpack Navigation Compose
Uygulamada XML bazlı Fragments ve NavGraph yerine tamamen Jetpack Navigation Compose kullanılacaktır. `NavHost`, `composable` ve `navigation` bileşenleri ile uygulamanın ekran akışları (NavGraph) Kotlin koduyla deklaratif (bildirimsel) olarak oluşturulur.

## 2. Rotalar (Routes) ve Tip Güvenliği (Type Safety)
Rotalar için basit string'ler (`"home_screen"`) yerine, uygulamanın tip güvenliğini (Type Safety) sağlamak adına `sealed class` veya `sealed interface` yapısı kullanılacaktır.

```kotlin
sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object Home : Screen("home_screen")
    
    // Argüman alan ekranlar
    data class VehicleDetail(val vehicleId: String) {
        val route = "vehicle_detail_screen/$vehicleId"
        companion object {
            const val ROUTE_TEMPLATE = "vehicle_detail_screen/{vehicleId}"
        }
    }
}
```

## 3. Modüler Navigasyon Yönetimi
Feature-based (özellik bazlı) modüler yapı nedeniyle, her modülün/paketin kendi NavGraph eklentisini yazması beklenir.
Ana `AppNavHost`, alt graph'ları çağıran bir kabuk görevi görür:

```kotlin
NavHost(navController = navController, startDestination = Screen.Login.route) {
    authNavGraph(navController)
    homeNavGraph(navController)
    rentalsNavGraph(navController)
}
```

Her bir `NavGraph` fonksiyonu (Örn: `fun NavGraphBuilder.authNavGraph(...)`), kendi içindeki ekran geçişlerinden (Login -> Register) sorumludur. 

## 4. Argüman Taşıma (Argument Passing)
Büyük veri nesneleri (Örn: `Vehicle` objesinin tamamı) navigasyon argümanı olarak **kesinlikle** taşınmamalıdır (App Crash / TransactionTooLargeException riski).
- Sadece benzersiz tanımlayıcı (Örn: `vehicleId` - String/Int) taşınmalıdır.
- Hedef ekran, bu `vehicleId` bilgisini `SavedStateHandle` üzerinden (ViewModel'de) alıp Local DB'den veya API'den asıl objeyi çekmelidir.

## 5. Side Effect (Navigasyon Tetikleme)
Navigasyon işlemleri Jetpack Compose UI tarafında (ViewModel'den gelen Effect/Event'lere tepki olarak) yapılmalıdır. ViewModel'in içerisine `NavController` referansı inject edilmesi yasaktır. ViewModel sadece "NavigateToHome" gibi bir etki (Effect) fırlatır, arayüz (UI) bunu yakalayıp `navController.navigate(...)` işlemini yürütür.

# Modül Sınırları (Module Boundaries) Stratejisi

Bu doküman, uygulamanın kod tabanında (codebase) modüllerin (veya paketlerin) birbirlerinden nasıl izole edileceğini ve "High Cohesion, Low Coupling" (Yüksek Uyum, Düşük Bağımlılık) kuralının nasıl uygulanacağını tanımlar.

## 1. Paketleme / Modülerizasyon Yaklaşımı

Proje, geleneksel "Layer-based" (Katman bazlı: tüm data bir yerde, tüm ui bir yerde) yapıdan ziyade, ölçeklenebilirlik için **"Feature-based" (Özellik Bazlı)** olarak gruplandırılmalıdır. Uygulama ister tek modül (App) içinde paketlere ayrılsın, ister çok modüllü (Multi-module) yapıya geçsin, her özelliğin kendi sınırları (Data, Domain, Presentation) olmalıdır.

Örnek Feature (Özellik) Sınırları:
- `feature_auth` (Giriş, Kayıt, Şifre, Ehliyet onay adımları)
- `feature_vehicles` (Araç listesi, harita ve araç detayı)
- `feature_rentals` (Kiralama geçmişi ve yeni kiralama işlemi)

## 2. Sınırlar Arası Bağımlılık Kuralları

- **Yatay Bağımlılık Yasaktır:** `feature_auth` alanı, asla doğrudan `feature_vehicles` alanının sınıflarını (Örn: VehicleRepository) çağırmamalıdır. İki özellik arası iletişim gerekiyorsa ortak (core) bir yapı veya Navigation (App modülü üzerinden Route argümanları) üzerinden haberleşilmelidir.
- **Core (Çekirdek) Modüller:** Tüm feature modülleri, ortak kodları (kod tekrarını önlemek için) barındıran `core` alanlarına bağımlı olabilir.
  - `core_network` (Retrofit, Hata Yönetimi, Result Wrappers)
  - `core_ui` / `core_designsystem` (Ortak Compose bileşenleri, Tema, Typografi)
  - `core_domain` (Ortak arayüzler ve modeller)

## 3. Görünürlük (Visibility) ve Kapsülleme

Kotlin'in görünürlük belirteçleri (Visibility Modifiers) mimariyi korumak için sıkı bir şekilde kullanılmalıdır:

- **`internal`:** Bir feature veya katman içindeki sınıfların (Örn: `RetrofitVehicleApi`, `DefaultVehicleRepository`, `AuthViewModel`) dışarıdan veya diğer modüllerden/özelliklerden erişilmesine gerek yoktur. Bu sınıflar sadece bulundukları klasör/modül içerisinde geçerli olmalı ve `internal` kelimesiyle işaretlenmelidir.
- **`public`:** Sadece diğer modüllerin/paketlerin bilmesi **zorunlu** olan Domain Modelleri (`Vehicle`), DTO'lar, UseCase'ler ve Repository Arayüzleri (Interfaces) `public` bırakılır.
- Hilt (Dependency Injection) modülleri aracılığıyla `internal` (gizli) sınıflar, `public` (açık) arayüzlerin arkasına gizlenerek (encapsulation) sisteme inject edilir.

## 4. Ana Modülün (App Module) Görevi

Ana dizin olan `app` modülü/paketi, kendi içinde hiçbir iş mantığı (business logic) barındırmamalıdır. Görevi;
- `@HiltAndroidApp` sınıfını barındırmak,
- Navigation Graph (Ekranlar arası yönlendirme kökleri) kurgusunu oluşturmak,
- Farklı özellik (feature) paketlerini birbirine bağlayan (wiring) bir yapıştırıcı (glue) olmak olmalıdır.

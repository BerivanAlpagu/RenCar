# Mimari Genel Bakış (Architecture Overview)

RenCar Android Uygulaması, modern Android geliştirme standartlarına uygun, ölçeklenebilir, test edilebilir ve bakımı kolay bir altyapı üzerine inşa edilmiştir.

Bu doküman, projede kullanılan temel tasarım desenlerinin (Design Patterns) ve kütüphanelerin kuşbakışı bir özetini sunar. Detaylar ilgili markdown dosyalarında mevcuttur.

## 1. Mimari Temelleri

- **[Clean Architecture](clean-architecture.md):** Uygulama *Data (Veri)*, *Domain (Çekirdek İş Mantığı)* ve *Presentation (Sunum/UI)* olmak üzere 3 temel katmandan oluşur. Bağımlılık (Dependency) her zaman en dıştan en içe (Domain'e) doğru akar.
- **[Modüler Yapı](module-boundaries.md):** Proje, "Özellik Bazlı" (Feature-Based) paketlenir. Her özellik (`auth`, `vehicles`, `rentals`) kendi içinde kapalı (High Cohesion) bir kutu gibi çalışır.
- **[MVI (Model-View-Intent)](mvi-overview.md):** Sunum katmanında (Presentation) Tek Yönlü Veri Akışı (UDF) kullanılır. UI sadece State'i okur, ViewModel'e Event (Intent) gönderir.
- **[Tek Gerçek Kaynak (SSOT)](caching-strategy.md):** UI veriyi hiçbir zaman direkt API'den okumaz. API veriyi yerel veritabanına (Room) yazar, UI yerel veritabanındaki değişimi anlık olarak dinler (Flow).

## 2. Kullanılan Temel Teknolojiler (Tech Stack)

| Kategori | Teknoloji / Kütüphane | Kullanım Amacı |
| :--- | :--- | :--- |
| **Kullanıcı Arayüzü (UI)** | **Jetpack Compose** | Deklaratif (bildirimsel) ve modern UI bileşenleri oluşturma. |
| **Navigasyon** | **Jetpack Navigation Compose** | Ekranlar arası geçişler (Type-safe argümanlarla). |
| **Bağımlılık Enjeksiyonu** | **Dagger Hilt** | Sınıflar arası bağımlılık yönetimi (DI) ve test edilebilirlik. |
| **Asenkron İşlemler** | **Kotlin Coroutines & Flow** | Ağ istekleri, veritabanı okumaları ve UI State akış yönetimi. |
| **Ağ (Network)** | **Retrofit & OkHttp** | REST API haberleşmesi, Token rotasyonu, Logging interceptor'lar. |
| **Veritabanı (Local)** | **Room Database** | Offline destek, önbellekleme (Caching), RemoteMediator entegrasyonu. |
| **Resim Yükleme** | **Coil** | Compose ile tam uyumlu, hafıza dostu resim gösterme. |
| **Sayfalama (Pagination)**| **Jetpack Paging 3** | Büyük listelerin (Araçlar vb.) sonsuz kaydırma ile performanslı gösterimi. |

## 3. Kod Kalitesi ve Güvenlik
- Projede herhangi bir `Exception` doğrudan UI'a yansıtılmaz. Hatalar **ApiResult / Result Wrapper** ile sarmalanarak (`docs/architecture/error-handling.md`) kullanıcı dostu mesajlara dönüştürülür.
- Güvenlik kritik veriler (Token'lar), cihazın güvenli hafızasında (EncryptedSharedPreferences / Security Crypto) tutulur.

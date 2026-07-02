# Clean Architecture Stratejisi

RenCar Android projesi, S.O.L.I.D. prensiplerine ve Separation of Concerns (Sorumlulukların Ayrılığı) kuralına dayanarak **Clean Architecture** (Temiz Mimari) yaklaşımını benimser.

Uygulama, bağımlılık yönünün (Dependency Rule) her zaman dışarıdan içeriye (Data/Presentation -> Domain) doğru aktığı üç ana katmandan (Layer) oluşur.

## 1. Mimari Katmanlar (Layers)

### 1.1. Domain Layer (Çekirdek Katman)
Uygulamanın kalbidir. Hiçbir Android framework bağımlılığı (Context, ViewModel, Retrofit, Room vb.) içermez. Sadece saf Kotlin ile yazılır.
- **Modeller:** İş kurallarını yansıtan saf veri yapıları (örn: `User`, `Vehicle`, `Rental`). (UI ve API'den yalıtılmıştır).
- **Repository Arayüzleri (Interfaces):** Verinin nereden geldiğini umursamadan, Data katmanının uygulaması (implement etmesi) gereken sözleşmeleri tanımlar (örn: `AuthRepository`).
- **Use Cases (Interactors):** Spesifik ve tek bir iş kuralını yerine getiren sınıflardır (örn: `RentVehicleUseCase`, `GetAvailableVehiclesUseCase`).

### 1.2. Data Layer (Veri Katmanı)
Verinin nereden (Network veya Local DB) ve nasıl alınacağından sorumludur. Domain katmanındaki Repository arayüzlerini uygular (implement eder).
- **DTO'lar & Entities:** API'den dönen DTO'lar (`VehicleResponseDto`) ve veritabanı tabloları (`VehicleEntity`).
- **Data Sources:** Uzak sunucu işlemleri (Retrofit API arabirimleri) ve Yerel veritabanı işlemleri (Room DAO'ları).
- **Mappers:** DTO ve Entity'leri, güvenli bir şekilde Domain modellerine çeviren extension fonksiyonlardır (`dto.toDomain()`).
- **Repository Implementasyonları:** Caching stratejisini (Single Source of Truth) yürüterek uzak ve yerel veri kaynaklarını koordine eden sınıflardır (örn: `DefaultVehicleRepository`).

### 1.3. Presentation Layer (Sunum Katmanı)
Kullanıcı arayüzünü (UI) barındıran ve kullanıcı etkileşimlerini (events) yöneten en dış katmandır.
- **Jetpack Compose:** Declarative (Bildirimsel) UI bileşenleri ve ekranlar.
- **ViewModels:** MVI (Model-View-Intent) veya MVVM desenini kullanarak UI State'ini tutan ve Domain katmanındaki UseCase'leri tetikleyen sınıflardır.
- UI, Data katmanını (Retrofit, Room) kesinlikle bilmez. Sadece ViewModel üzerinden Domain ile (UseCase / Modeller) iletişim kurar.

## 2. Bağımlılık Akışı (Dependency Rule)

Kural: **Hiçbir iç katman, dış katman hakkında bir şey bilemez.** Bağımlılık her zaman **Domain katmanına** doğru olmalıdır:
- `Presentation` -> bilir -> `Domain` (ViewModel, UseCase'i inject edip çağırır)
- `Data` -> bilir -> `Domain` (Data katmanı, Domain'deki model ve arayüzleri bilir)
- `Domain` -> bilir -> **HİÇBİR ŞEYİ** (Domain katmanı ne Retrofit'i ne Compose'u ne de Data katmanını tanır).

*(Not: Proje yapısı çok modüllü (multi-module) veya tek modülde klasör bazlı (feature-based) ayrılmış olabilir. Ancak her yapıda bu 3 katmanlı izolasyon hiyerarşisinin korunması zorunludur.)*

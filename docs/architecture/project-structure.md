# Proje Klasör Yapısı (Project Structure) Stratejisi

Bu doküman, "Feature-Based (Özellik Bazlı) Clean Architecture" stratejisinin Android (Kotlin) projesindeki somut klasör/paket hiyerarşisini tanımlar.

## 1. Ana Hiyerarşi Özeti

Kod tabanı, karmaşıklığı izole edebilmek için temel olarak `app`, `core` ve `feature` dizinlerinden (veya multi-module yapısındaki modüllerden) oluşacaktır.

```text
com.turkcell.rencar
├── app/                  # Uygulama ayağa kalkış noktası (DI, Navigation)
├── core/                 # Özelliklerden bağımsız ortak (shared) bileşenler
│   ├── network/          # Retrofit, Interceptors, ApiResult wrapper'ları
│   ├── ui/               # Ortak Compose bileşenleri (LoadingView, ErrorDialog vb.)
│   ├── designsystem/     # Renkler (Colors), Tema (Theme), Tipografi, İkonlar
│   └── domain/           # Proje genelinde kullanılan ortak Exception ve Base Modeller
└── feature/              # İşlev bazlı ayrılmış ana özellikler
    ├── auth/             # Giriş, Kayıt, Şifre yenileme, Ehliyet onay süreci
    ├── vehicles/         # Ana sayfa (Araç listeleme), Harita (Socket takibi), Detaylar
    └── rentals/          # Kiralama yapma, Aktif kiralama durumu ve Geçmiş liste
```

## 2. Bir Feature (Özellik) Paketinin İçi

Her bir `feature` paketi kendi içinde **Clean Architecture** kurallarını (Data, Domain, Presentation) birebir uygular. Örneğin `feature/auth` klasörünün iç yapısı şu şekilde olacaktır:

```text
feature/auth/
├── data/
│   ├── remote/           # AuthApiService (Retrofit arayüzü), Request/Response DTO'lar
│   ├── local/            # Token DataStore, Room DAO'ları (eğer varsa)
│   ├── mapper/           # DtoToDomain mapper extension'ları
│   └── repository/       # DefaultAuthRepository (Data'yı Domain'e bağlayan somut sınıf)
├── domain/
│   ├── model/            # User, TokenInfo (UI ve API'den bağımsız saf Kotlin Modelleri)
│   ├── repository/       # AuthRepository (Interface - Veri kaynağını soyutlar)
│   └── usecase/          # LoginUseCase, ValidateLicenseUseCase
└── presentation/
    ├── login/            # LoginScreen (Compose), LoginViewModel, LoginContract (State/Event)
    ├── register/         # RegisterScreen, RegisterViewModel vb.
    └── license_upload/   # Ehliyet yükleme UI bileşenleri
```

## 3. Katman İletişim Kuralları (Kısa Hatırlatma)
1. **Presentation** klasörü altındaki sınıflar (ViewModel, Screen) sadece **Domain** klasörüne (UseCase ve Modellere) erişebilir. `Data` klasörünü göremez.
2. **Data** klasörü altındaki Mapper ve Repository'ler, **Domain** klasöründeki arayüzleri uygular (`implements`).
3. **Domain** klasörü tamamen bağımsızdır; ne Retrofit (Data) ne de Compose (Presentation) kütüphanelerini bilemez.
4. Hilt Modülleri (`@Module`) genellikle Data katmanı içinde veya uygulamanın en dış kabuğu olan `app/di` dizininde tanımlanır.

## 4. Kaynak Dosyaları (Resources - res/)
Proje tek modül altında paketlere ayrılmışsa (Single Module with feature packages), tüm kaynaklar `app/src/main/res/` altında toplanır:
- `/values/strings.xml`: Hata mesajları, Buton yazıları (Hardcoded string kullanımı kesinlikle yasaktır).
- `/drawable`: Sadece Compose'un varsayılan `Icons` nesnesinde bulunmayan özel vektör çizimler.
- `/font`: Müşteri veya tasarıma (Design System) özel indirilen tipografi dosyaları.

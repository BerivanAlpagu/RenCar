# Dependency Injection (Bağımlılık Enjeksiyonu) Stratejisi

Bu doküman, RenCar projesinde nesnelerin (Object) yaşam döngülerinin yönetilmesi ve mimari katmanlar (Layers) arası gevşek bağlılığın (Loose Coupling) sağlanması için kullanılacak Dependency Injection mimarisini tanımlar. Projede **Dagger Hilt** kullanılacaktır.

## 1. Neden Hilt?
- Jetpack (özellikle ViewModel, WorkManager ve Compose) ile resmi/doğal entegrasyona sahiptir.
- Dagger'ın sunduğu compile-time (derleme zamanı) güvenlik garantisini korurken boilerplate (tekrar eden) kod miktarını ciddi şekilde azaltır.
- Test edilebilirlik (Mock nesnelerin kolayca enjekte edilmesi) açısından Android'in endüstri standart kütüphanesidir.

## 2. Hilt Bileşenleri ve Kapsamlar (Scopes & Components)

Proje genelinde aşağıdaki standart Hilt bileşenleri kullanılacaktır:

| Kapsam (Scope) | Hilt Bileşeni (Component) | Kullanım Alanı (Örnek) |
| :--- | :--- | :--- |
| `@Singleton` | `SingletonComponent` | Uygulama (Application) ayakta olduğu sürece yaşayacak tekil nesneler. (Örn: `Retrofit`, `RoomDatabase`, `OkHttpClient`) |
| `@ViewModelScoped` | `ViewModelComponent` | Yalnızca ViewModel'in yaşam döngüsüne bağlı olan bağımlılıklar. |
| `@ActivityRetainedScoped`| `ActivityRetainedComponent` | Ekran döndürmelerinde (Configuration changes) hayatta kalması gereken spesifik managers/controllers. |

## 3. Modül (Module) Yapılandırması

Bağımlılıkların DI grafiğine tanıtılması için `@Module` sınıfları oluşturulur. Temel modüller şunlardır:

### 3.1. NetworkModule
- Retrofit instance'ları, OkHttpClient, Token Interceptor, Authenticator ve API servis arayüzlerinin oluşturulduğu yerdir.
- Üçüncü parti kütüphaneler olduğu için `@Provides` notasyonu kullanılır.
- Component: `@InstallIn(SingletonComponent::class)`

### 3.2. DatabaseModule
- Room veritabanı kurulumu (`Room.databaseBuilder`) ve `DAO` sınıflarının (interface) enjekte edilebilir hale getirildiği yerdir.
- Component: `@InstallIn(SingletonComponent::class)`

### 3.3. RepositoryModule
- **En Önemli Modül:** Domain katmanındaki Repository Interface'lerinin (Örn: `VehicleRepository`), Data katmanındaki somut karşılıkları (Örn: `DefaultVehicleRepository`) ile eşleştirildiği (Bind) yerdir.
- Interface -> Implementation eşleşmesi olduğu için `@Binds` notasyonu ile abstract fonksiyonlar kullanılır.
- Component: `@InstallIn(SingletonComponent::class)`

## 4. Uygulama Kuralları ve Standartlar

1. **`@Inject constructor` Önceliği:** Kod mülkiyeti (sahipliği) bizde olan sınıflarda (örn. Custom Repository impl, UseCases) Hilt Modülü içine `@Provides` yazmak **yerine**, doğrudan sınıfın constructor'ına `@Inject constructor` notasyonu eklenmelidir.
2. **ViewModel Entegrasyonu:** Jetpack Compose ekranlarında ViewModel'ler manuel instanciate edilmez, doğrudan `hiltViewModel()` fonksiyonu kullanılarak ağaçtan (graph) çekilir.
3. **Application Sınıfı:** Projenin başlangıç (Application) sınıfı mutlaka `@HiltAndroidApp` notasyonu ile işaretlenmelidir.
4. **Activity/Fragment:** Eğer eski usül View katmanı kullanılacaksa (veya ana Single Activity'de) mutlaka `@AndroidEntryPoint` eklenmelidir.
5. **Bağımsızlık:** Sınıflar, bağımlılıklarını constructor üzerinden almalı (Constructor Injection), dışarıdan bir DI konteynerini (`Hilt` dahil) direkt içlerinde aramamalıdırlar. Böylece test ortamında sahte (fake/mock) nesneler kolayca parametre olarak geçilebilir.

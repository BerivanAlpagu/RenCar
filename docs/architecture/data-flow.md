# Veri Akışı (Data Flow) Stratejisi

Bu doküman, RenCar Android uygulamasında kullanıcı etkileşimlerinden başlayıp, sunucudan dönen verinin tekrar ekrana çizilmesine kadar geçen sürecin (Data Flow) nasıl işlediğini tanımlar. Projede **Tek Yönlü Veri Akışı (Unidirectional Data Flow - UDF)** benimsenmiştir.

## 1. Tek Yönlü Veri Akışı (UDF) Temel Prensibi

Veri ve durum (State) her zaman yukarıdan aşağıya (ViewModel -> Compose), kullanıcı aksiyonları (Event/Intent) ise her zaman aşağıdan yukarıya (Compose -> ViewModel) akar.

### Akış Diyagramı (Adım Adım)

1. **Kullanıcı Etkileşimi (UI Event):** Kullanıcı bir butona tıklar (Örn: "Kirala" butonu).
2. **ViewModel (Intent):** Jetpack Compose, bu eylemi ViewModel'e bir `Intent` (MVI) veya fonksiyon çağrısı olarak iletir.
3. **Domain (UseCase):** ViewModel, iş kuralını barındıran ilgili UseCase'i tetikler (Örn: `RentVehicleUseCase`).
4. **Data (Repository):** UseCase, veriye ulaşmak veya mutasyon yapmak için ilgili Repository interface'ini (Örn: `VehicleRepository`) çağırır.
5. **Data Source (API / DB):** Repository, kararına göre (SSOT - Single Source of Truth) veriyi ağdan (Retrofit) çeker veya yerel veritabanından (Room) okur. 
6. **Mapping (DTO -> Domain):** Dönen veri (Örn: `RentalResponseDto`) Mapper aracılığıyla Domain Modeline (`Rental`) dönüştürülür.
7. **Sonuç (Result/Flow):** Repository sonucu UseCase'e, UseCase de ViewModel'e (`ApiResult.Success` veya `Flow`) iletir.
8. **UI Güncellemesi (State):** ViewModel, eline ulaşan bu domain verisini (StateFlow) günceller. StateFlow'u dinleyen Compose UI otomatik olarak yeniden çizilir (Recomposition).

---

## 2. Senaryo Örnekleri

### Senaryo A: Araç Listesini Görüntüleme (Listeleme Akışı)
- **Kullanıcı** Ana Ekrana girer.
- **ViewModel**, `GetAvailableVehiclesUseCase`'i çağırır.
- **Repository**, veriyi `Room Database` üzerinden bir `PagingSource` veya `Flow` olarak döner (Cache stratejisi gereği).
- Eğer yerel veri boşsa veya sayfa sonuna gelinirse `RemoteMediator` tetiklenir, API'den yeni araçları çeker (`GET /vehicles`), Room DB'ye yazar.
- Room DB güncellendiği an `Flow` tetiklenir ve UI kendiliğinden güncel araç listesini gösterir.

### Senaryo B: Araç Kiralama İşlemi (Mutasyon Akışı)
- **Kullanıcı** araç detay ekranında "Hemen Kirala" der.
- **ViewModel** -> `RentVehicleUseCase` tetiklenir.
- **Repository** -> API'ye `POST /rentals` isteği atar (Ağ zorunludur, cache kullanılmaz).
- Eğer hata alınırsa (Örn: `409 Conflict`), API katmanındaki Interceptor bunu yakalar, Error Response dökümanına göre `ApiResult.Error(409)` döner.
- **ViewModel** bu hatayı alır ve UI State'i `Error` moduna çeker.
- **UI** kullanıcıya "Zaten aktif bir kiralamanız var" Dialog'unu gösterir.

---

## 3. Asenkron İşlemler (Coroutines & Flows)

- Tüm veri çekme işlemleri `suspend` fonksiyonlar kullanılarak veya `Flow` döndürülerek asenkron yönetilecektir.
- Network ve Database işlemleri (Data Layer) her zaman `Dispatchers.IO` üzerinde yapılmalıdır.
- ViewModel'deki `StateFlow` güncellemeleri otomatik olarak `Main` thread üzerinde yakalanır, UI'ı bloklamaz.

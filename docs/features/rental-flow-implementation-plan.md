# Kiralama Akışı — Implementation Plan (Rezervasyon → Kilit Açma → Sürüş → Teslim → Ödeme)

## Bağlam (Context)

Backend (`docs/api/openapi.json`) güncellendi ve artık şu iş kuralını dayatıyor: **kiralama yalnızca önceden rezervasyon yapılmış bir araçta açılabilir** (`POST /rentals` → 409 eğer aktif rezervasyon yoksa), yolculuk başlamadan önce 4 yönlü araç fotoğrafı zorunlu (`PREPARING` durumu), ve ücret yolculuk **bittikten sonra** kalem kalem hesaplanıp kilitleniyor (`POST /rentals/{id}/finish`).

Android tarafında bu akışın ekran iskeleti zaten var (`ReservationConfirmationScreen`, `HandoverPhotoScreen`, `ActiveRentalScreen`, `PaymentSummaryScreen`), fakat **hiçbiri gerçek API'ye bağlı değil** — plan seçimi, fiyatlar, fotoğraflar ve süre/mesafe tamamen sabit/mock veri veya client-side simülasyon (`ValueAnimator`). Bu plan, mevcut mimariyi (Hilt + feature-based MVI + Retrofit/kotlinx.serialization + Compose Navigation) bozmadan bu ekranları gerçek uçlara bağlamayı ve eksik reservation/photo/finish/pay API katmanını kurmayı hedefler.

Proje `docs/architecture/mvi-overview.md`'de dokümante edilen **MVI (Model-View-Intent)** desenini kullanıyor: her ekran için `XxxContract.kt` (State/Event/Effect) + `XxxViewModel.kt` (`onEvent` dispatcher). Bu plandaki tüm yeni/yeniden yazılan ViewModel'ler bu kalıba uyacak; kanonik referans örnekler `feature/wallet/presentation/wallet/WalletContract.kt`+`WalletViewModel.kt` ve `feature/rentals/presentation/history/RentalHistoryContract.kt`+`RentalHistoryViewModel.kt`.

**Bilinen kapsam sınırı (kullanıcıyla netleştirildi):** `POST /rentals/{id}/photos` yalnızca `PREPARING` aşamasında çalışır (409 döner ACTIVE/COMPLETED'ta) — yani **teslim (dönüş) fotoğrafları için backend'de bir upload endpoint'i yok**. Karara göre: "Bitir" adımında kullanıcıdan yine 4 foto çekmesi istenir (UX tutarlılığı için) ama bu fotoğraflar **yüklenmez**, sadece yerelde tutulur; hemen ardından `POST /rentals/{id}/finish` çağrılır. İleride backend'e uç eklenirse `ReturnPhotoViewModel` içindeki tek bir yer değiştirilerek bağlanabilir.

**Kapsam dışı:** İyzico WebView/3DS/Checkout-Form akışı bu planda yok — `PaymentSummaryScreen` yalnızca `WALLET` ve `CARD` (simüle) yöntemlerini destekleyecek şekilde planlanıyor. Lisans (ehliyet) yükleme ekranının kendisi (selfie eksikliği dahil) bilinçli olarak **dokunulmuyor** — yalnızca giriş sonrası yönlendirme mantığı düzeltiliyor.

---

## Bölüm 1 — Giriş sonrası yönlendirme düzeltmesi (onay varsa direkt anasayfa)

**Sorun:** `feature/auth/presentation/AuthViewModel.kt:117-132` (`verifyOtp`) token kaydettikten sonra **koşulsuz** `AuthEvent.NavigateToLicense` yayınlıyor — ehliyet zaten onaylı olsa bile kullanıcıyı tekrar yükleme ekranına gönderiyor. Soğuk başlangıçta (`SplashViewModel.kt:38-69`) bu mantık zaten doğru yapılmış: `licenseApi.getStatus()` çağrılıp `APPROVED → Home`, `UNDER_REVIEW → LicenseApproval`, diğerleri → `License`. Eksik olan tek şey, aynı kontrolün **giriş (login/OTP) yolunda** da yapılması.

**Değişiklikler:**
- `feature/auth/domain/` (şu an boş, `.gitkeep`) içine `ResolvePostAuthDestinationUseCase.kt` eklenir: `LicenseApi`'yi enjekte edip `SplashViewModel`'deki when-bloğuyla birebir aynı kararı üretir (`AuthDestination` sealed sonucu: `Home`, `LicenseApproval`, `License`). Bu, mantığı iki yerde (Splash + Auth) tekrar yazmak yerine tek yerde tutar.
- `SplashViewModel` bu use case'i kullanacak şekilde küçük bir refactor alır (mevcut davranışı değiştirmez, sadece kodu taşır).
- `AuthViewModel`'e `LicenseApi` (veya doğrudan use case) enjekte edilir; `AuthEvent`'e eksik olan `NavigateToLicenseApproval` case'i eklenir (zaten `NavigateToHome` tanımlı ama hiç kullanılmıyordu).
- `verifyOtp` (satır 117-132), token kaydından sonra use case'i çağırıp sonuca göre `NavigateToHome` / `NavigateToLicenseApproval` / `NavigateToLicense` yayınlar.
- `MainActivity.kt:90-114` içindeki `authViewModel.events` `LaunchedEffect`'ine yeni `NavigateToLicenseApproval` case'i eklenir (`navController.navigate(Screen.LicenseApproval) { popUpTo(0){inclusive=true} }`).

Bu, kullanıcının "mevcut akışı bozma" isteğiyle uyumlu: `register`/`login` akışı, OTP ekranı, lisans yükleme ekranının kendisi hiç değişmiyor — sadece OTP doğrulandıktan sonraki yönlendirme kararı düzeltiliyor.

---

## Bölüm 2 — Yeni `feature/reservations` paketi

Backend'de `Reservations` ayrı bir tag/kaynak (`POST /reservations`, `GET /reservations/active`, `DELETE /reservations/{id}`) ve Android'de hiç karşılığı yok. Mevcut `feature/vehicles`, `feature/rentals` klasör düzenini birebir taklit ederek yeni bir feature paketi açılır:

```
feature/reservations/
  data/remote/ReservationApi.kt          (POST reservations, GET reservations/active, DELETE reservations/{id})
  data/remote/dto/ReservationDtos.kt      (CreateReservationDto, ReservationResponseDto, ReservationVehicleSummaryDto)
  data/repository/DefaultReservationRepository.kt
  data/di/ReservationDataModule.kt        (@Binds, RentalDataModule.kt'nin birebir kopyası deseni)
  domain/model/Reservation.kt
  domain/repository/ReservationRepository.kt
  domain/usecase/CreateReservationUseCase.kt / GetActiveReservationUseCase.kt / CancelReservationUseCase.kt
```

`NetworkModule.kt`'ye `provideReservationApi` eklenir (mevcut `provideRentalApi` deseniyle aynı).

`Reservation` domain modeli: `id, vehicleId, vehicle(plate,brand,model,type,lat,lng,pricePerMinute), status(ACTIVE/CONVERTED/CANCELLED/EXPIRED), expiresAt, remainingSeconds`.

---

## Bölüm 3 — `feature/vehicles` genişletmesi

Mevcut `Vehicle` domain modeli (`feature/vehicles/domain/model/Vehicle.kt`) çok eksik: `status`, `pricePerMinute`, `pricePerHour`, `fuelPercent`, `rangeKm`, `transmission`, `seats`, `segment` alanları yok — bu yüzden bottom sheet'te (`MapScreen.kt:218-327`) "%72", "Otomatik", "5 kişi", "₺4,50/dk" gibi değerler sabit yazılmış. Backend `VehicleResponseDto` bunların hepsini zaten veriyor.

**Değişiklikler:**
- `Vehicle` domain modeline eksik alanlar eklenir; `VehicleResponseDto` (data DTO) ve `DefaultVehicleRepository.getAvailableVehicles` mapping'i buna göre güncellenir.
- `VehicleApi.getVehicles`'a `includeBusy: Boolean?` query param eklenir — `MapViewModel.fetchVehicles` artık `includeBusy=true` ile çağırır ki RESERVED/RENTED araçlar da (gri marker için) haritada görünsün (API bunu zaten bu amaçla tasarlamış).
- `VehicleApi`'ye iki yeni uç: `GET /vehicles/{id}` (getOne) ve `GET /vehicles/{id}/quote?plan&minutes` (`QuoteResponseDto`: usageFee/startFee/serviceFee/estimatedTotal) eklenir — plan seçim ekranındaki "Tahmini ücret" satırı için.
- `MarkerUtil.kt`'de araç durumuna göre (AVAILABLE = mavi, RESERVED/RENTED = gri, kullanıcının kendi rezervasyonu/kiralaması = vurgulu) marker rengi ayrımı eklenir — şu an tüm marker'lar aynı görünüyor.

---

## Bölüm 4 — `feature/rentals` genişletmesi (DTO/API/Repository)

Mevcut `RentalApi.kt` sadece `POST rentals` içeriyor; `CreateRentalDto`/`RentalResponseDto` de backend'in şu anki şemasıyla uyumsuz (eski `endDate` zorunlu / `totalPrice: Double` varsayımı — artık `plan` zorunlu, `totalPrice` nullable, `vehicle` iç içe obje).

**DTO'lar yeniden yazılır / eklenir** (`data/remote/dto/`):
- `CreateRentalDto(vehicleId, plan, endDate: String? = null)`
- `RentalResponseDto` — backend şemasıyla birebir: `id,userId,vehicleId,vehicle(RentalVehicleSummaryDto),plan,startedAt,endedAt,endDate,totalPrice?,startFee,serviceFee?,distanceKm,durationMinutes,status,paymentStatus,paymentMethod?,discountAmount,createdAt` (nullable alanlar için `kotlinx.serialization.json.JsonElement?` yerine gerçek tipli nullable alanlar kullanılır, `AuthApi.kt`'deki `UserResponseDto.phone: JsonElement?` deseni yerine daha spesifik tipleme tercih edilir çünkü bu alanlar sayısal/string, obje değil)
- `ActiveRentalResponseDto` (RentalResponseDto + `elapsedSeconds`, `currentCost`)
- `RentalPhotoDto`, `RentalPhotosStateDto` (`uploadedCount`, `remainingSides`, `photosComplete`)
- `FinishRentalResponseDto` (RentalResponseDto + `usageFee`, `elapsedSeconds`)
- `PayRentalDto(method, cardId?, discountCode?, iyzicoPaymentId?)`, `PayRentalResponseDto`

**`RentalApi` genişler:**
```kotlin
@POST("rentals") suspend fun createRental(@Body body: CreateRentalDto): RentalResponseDto
@GET("rentals") suspend fun listMine(): List<RentalResponseDto>
@GET("rentals/active") suspend fun getActive(): Response<ActiveRentalResponseDto>   // 404 -> boş
@GET("rentals/{id}") suspend fun getOne(@Path("id") id: String): RentalResponseDto
@Multipart @POST("rentals/{id}/photos")
  suspend fun uploadPhoto(@Path("id") id: String, @Part("side") side: RequestBody, @Part file: MultipartBody.Part): RentalPhotosStateDto
@GET("rentals/{id}/photos") suspend fun getPhotos(@Path("id") id: String): RentalPhotosStateDto
@POST("rentals/{id}/start") suspend fun start(@Path("id") id: String): RentalResponseDto
@POST("rentals/{id}/finish") suspend fun finish(@Path("id") id: String): FinishRentalResponseDto
@POST("rentals/{id}/pay") suspend fun pay(@Path("id") id: String, @Body body: PayRentalDto): PayRentalResponseDto
@DELETE("rentals/{id}") suspend fun cancel(@Path("id") id: String): Response<Unit>   // yalnız PREPARING
```
Multipart deseni `LicenseApi.upload` / `LicenseViewModel.upload` (`feature/auth/presentation/license/LicenseViewModel.kt:36-64`) ile birebir aynı: `File.asRequestBody("image/jpeg".toMediaType())`.

**`RentalRepository` arayüzü ve `DefaultRentalRepository`** bu uçların hepsini sarmalayacak şekilde genişletilir; `getRentalHistory()` artık **gerçek** `GET /rentals` çağırır (şu an `data/repository/DefaultRentalRepository.kt:15-66`'da 4 satırlık hardcoded mock liste dönüyor — bu silinir).

**Domain modelleri:**
- `RentalStatus` enumuna `PREPARING` eklenir (`PREPARING, ACTIVE, COMPLETED, CANCELLED`).
- `Rental` domain modeli gerçek alanları taşıyacak şekilde genişletilir (plan, totalPrice: Double?, startFee, serviceFee: Double?, paymentStatus, vehicle özeti).

---

## Bölüm 5 — Ekran ekran akış

### 5.1 Harita / Bottom Sheet (`MapScreen.kt`, `MapContract.kt`, `MapViewModel.kt`)

- `MapState`'e `activeReservation: Reservation?` ve `activeRental: Rental?` eklenir. `MapViewModel` ekran her göründüğünde (`onEvent(OnScreenResumed)` gibi yeni bir event veya mevcut `OnLocationPermissionGranted` sonrasına eklenerek) `GET /reservations/active` ve `GET /rentals/active` çağırır — bu, uygulama kapanıp açıldığında yarım kalan rezervasyon/kiralamayı geri kazandırır (backend zaten bu amaçla var).
- `VehicleDetailBottomSheet` (`MapScreen.kt:180-367`) iki moda ayrılır:
  - Araç `AVAILABLE` ve kullanıcının başka aktif rezervasyonu yoksa → sadece **"Rezerve Et"** görünür. Tıklanınca `MapViewModel` doğrudan `ReservationRepository.create(vehicleId)` çağırır (yeni ekrana gitmeden) → başarılı olursa state güncellenir, sheet "Rezerve edildi · 14:59 kaldı" rozetiyle güncellenir.
  - Araç kullanıcının **kendi aktif rezervasyonu** ise (bottom sheet tekrar açıldığında, `activeReservation.vehicleId == vehicle.id`) → **"Kilidi Aç"** aktif olur (satır 354'teki no-op `onClick = { /* Unlock */ }` gerçek callback'e bağlanır) ve `Screen.ReservationConfirmation(vehicleId)`'e navigate eder.
  - Sabit "MÜSAİT" rozeti (satır 218) ve "%72", "Otomatik", "5 kişi", "₺.../dk" gibi sabit değerler (satır 246-327) artık gerçek `vehicle.status/fuelPercent/transmission/seats/pricePerMinute` alanlarından okunur (Bölüm 3).

### 5.2 Plan seçimi + kilit açma (`ReservationConfirmationScreen.kt`)

Şu an tamamen stateless/sabit veri (Renault Clio, ₺4,50/dk sabit metin, `onConfirmClick` sadece navigate ediyor). Bu ekran **gerçek plan seçim + unlock onay ekranı** olur, MVI Contract kalıbıyla:
- `ReservationConfirmationContract.kt`: `ReservationConfirmationState(isLoading, vehicle: Vehicle?, selectedPlan: RentalPlan, quote: QuoteResponseDto?, isQuoteLoading, error)`; `ReservationConfirmationEvent` (`LoadVehicle`, `PlanSelected(plan)`, `ConfirmClicked`); `ReservationConfirmationEffect` (`NavigateToHandover(rentalId)`, `ShowError(message)`, `NavigateBack`).
- `ReservationConfirmationViewModel.kt`: `onEvent` dispatcher. `LoadVehicle` → `VehicleApi.getOne(vehicleId)` çeker (init'te tetiklenir). `PlanSelected` hem `selectedPlan` state'ini günceller hem `VehicleApi.getQuote(vehicleId, plan, minutes)` tetikler (satır 233-244'teki sabit "15 dk / ₺15,00 / ~₺135" satırları buradan beslenir). `ConfirmClicked` → `RentalRepository.createRental(vehicleId, plan)` çağırır (satır 103-116'daki "Rezervasyonu Tamamla" butonu artık **rezervasyon değil, kilit açma** onayıdır); başarılı olursa dönen `rental.id` ile `Effect.NavigateToHandover(rentalId)` gönderir. 409 (aktif rezervasyon yok) durumunda `Effect.ShowError` + `NavigateBack`.

### 5.3 Başlangıç fotoğrafları (`HandoverPhotoScreen.kt`)

Şu an hiç ViewModel'i yok; kamera `TakePicturePreview()` (yalnız bellekte `Bitmap`, dosya yok) kullanıyor ve galeri seçimi sahte 1x1 bitmap üretiyor (satır 89-104) — hiçbiri yüklenmiyor. Değişiklikler:
- `Screen.HandoverPhoto(vehicleId)` → `Screen.HandoverPhoto(rentalId: String)` olarak değişir (fotoğraf yükleme uçları `rentalId` ister).
- Kamera launcher `TakePicturePreview()`'dan `TakePicture()` (FileProvider `Uri`'sine yazan, gerçek dosya üreten) moduna geçirilir — `LicenseUploadScreen.kt:72-90` ve oradaki `Bitmap.toLocalFile` / `Uri.toLocalFile` yardımcıları (satır 443-465, şu an dosyaya özel `private`) örnek alınır.
- Bu iki yardımcı fonksiyon, hem lisans hem handover hem de teslim ekranında tekrar kullanılacağı için `core/ui/` (şu an sadece `.gitkeep` olan boş paket — tam bunun için ayrılmış) altına `ImageFileUtil.kt` olarak taşınıp public yapılır; `LicenseUploadScreen.kt` de bu paylaşılan util'i kullanacak şekilde güncellenir (kod tekrarı kaldırılır).
- `HandoverPhotoContract.kt`: `HandoverPhotoState(rentalId, photos: Map<Side, PhotoUiState>, uploadedCount, photosComplete, isStarting, error)`; `HandoverPhotoEvent` (`LoadExistingPhotos`, `PhotoCaptured(side, file)`, `StartRentalClicked`, `CancelClicked`); `HandoverPhotoEffect` (`NavigateToActiveRental(rentalId)`, `NavigateBackToMap`, `ShowError(message)`).
- `HandoverPhotoViewModel.kt`: kamera/galeri launcher'ları Compose tarafında kalır (bunlar UI-seviyeli sistem çağrılarıdır), ama sonuç dosyası `onEvent(PhotoCaptured(side, file))` ile iletilir; ViewModel bunun üzerine `RentalApi.uploadPhoto(rentalId, side, file)` çağırır, dönen `RentalPhotosStateDto` ile `uploadedCount`/`photosComplete` state'ini günceller ("2/4 çekildi" göstergesi — satır 218 — artık sunucu cevabından beslenir, yerel sayaçtan değil). `LoadExistingPhotos` (init'te tetiklenir) `GET rentals/{id}/photos` ile ekran yeniden açıldığında kaldığı yerden devam ettirir.
- "Kiralamayı Başlat" butonu (satır 185-202) → `onEvent(StartRentalClicked)`; `photosComplete == true` olduğunda `RentalApi.start(rentalId)` çağırır → başarıyla `Effect.NavigateToActiveRental(rentalId)` gönderir.
- Geri tuşu → `onEvent(CancelClicked)`: PREPARING aşamasındaki kiralamayı `DELETE /rentals/{id}` ile iptal eder ki araç tekrar `AVAILABLE` olsun (şu an hiçbir iptal çağrısı yok, kullanıcı geri çıkarsa araç sunucu tarafında kilitli kalır), sonra `Effect.NavigateBackToMap` gönderir.

### 5.4 Aktif sürüş (`ActiveRentalScreen.kt`, `ActiveRentalViewModel.kt`)

Şu an `ActiveRentalViewModel.startRental` (satır 24-38) **ikinci bir** `createRental` çağrısı yapıyor (endDate = now+1h hardcoded) — bu artık yanlış çünkü kiralama zaten Bölüm 5.2'de oluşturuldu ve 5.3'te başlatıldı. Süre/mesafe/ücret gösterimi de tamamen sahte (`ValueAnimator` ile 10 saniyede Taksim→Galata simülasyonu, `ActiveRentalScreen.kt` üstündeki MapLibre polyline animasyonu). Ayrıca bu ViewModel şu an MVI Contract kalıbına uymuyor (Event/Effect yok, doğrudan `startRental(vehicleId)` fonksiyon çağrısı) — bu bölümde baştan yazılırken doğru kalıba geçirilir.

- `Screen.ActiveRental(vehicleId)` → `Screen.ActiveRental(rentalId: String)`.
- Yeni `ActiveRentalContract.kt` eklenir (şu an `ActiveRentalState` `ActiveRentalViewModel.kt` içine gömülü, ayrı dosyaya çıkarılır): `ActiveRentalState(rental, elapsedSeconds, currentCost, distanceKm, isLoading, error)`; `ActiveRentalEvent` (`ScreenOpened(rentalId)`, `PollTick`, `FinishClicked`); `ActiveRentalEffect` (`NavigateToReturnPhoto(rentalId)`, `ShowError(message)`).
- `ActiveRentalViewModel.startRental` ve endDate hardcoded'lı `createRental` çağrısı tamamen kaldırılır. `onEvent(ScreenOpened(rentalId))` hem `RentalApi.getOne(rentalId)` ile araç/plan bilgisini alır hem `viewModelScope` içinde 3-5 saniyede bir kendine `onEvent(PollTick)` gönderen bir döngü başlatır; `PollTick` handler'ı `RentalApi.getActive()` çağırıp `elapsedSeconds`/`currentCost`/`distanceKm`'i günceller. Kiralama zaten bitmişse (404) veya başka bir aktif kiralama gelirse `Effect.ShowError` gönderilir.
- "Kilitle/Aç" no-op butonu (yolculuk sırasında kilit açma/kapama backend'de yok) — bu buton kaldırılır veya sadece bilgilendirici hale getirilir (kapsam dışı, backend karşılığı yok).
- "Kiralamayı Bitir" → `onEvent(FinishClicked)` → `Effect.NavigateToReturnPhoto(rentalId)` gönderir (artık doğrudan ödeme ekranına gitmiyor; `finish` API çağrısı `ReturnPhotoScreen`'de, foto adımından sonra yapılıyor — bkz. 5.5). Client-side `ValueAnimator` simülasyonu ve sahte polyline kaldırılır; MapLibre görünümü artık `getActive()`'ten gelen gerçek konum/mesafe ile (varsa WS/GPS entegrasyonu ayrı bir iş — bu plan kapsamında en azından sayısal değerler gerçek).

### 5.5 Teslim fotoğrafları (YENİ: `ReturnPhotoScreen.kt`)

Bölüm başındaki karara göre: `HandoverPhotoScreen`'in UI/kamera bileşenleri (4'lü `PhotoCard` grid'i, kamera/galeri seçim dialog'u) **aynen** yeniden kullanılır (ortak `PhotoCaptureGrid` composable'ına çıkarılır, iki ekran da bunu kullanır) ama bu sefer:
- `ReturnPhotoContract.kt` + `ReturnPhotoViewModel.kt` eklenir — `HandoverPhotoContract`'a çok benzer state/event şekli (`ReturnPhotoState(photos, allCaptured, isFinishing, error)`; `ReturnPhotoEvent(PhotoCaptured, ConfirmClicked)`; `ReturnPhotoEffect(NavigateToPayment(rentalId), ShowError)`), fakat davranış farklı: fotoğraflar hiçbir uca **yüklenmez** (backend desteklemiyor) — sadece yerel `Bitmap`/`Uri` state'inde tutulur (UX/damage-claim kaydı amaçlı; ileride backend ucu eklenirse `onEvent(ConfirmClicked)` içindeki tek bir yer değiştirilerek upload'a çevrilebilir).
- `onEvent(ConfirmClicked)`, 4 foto tamamlandıktan sonra `RentalApi.finish(rentalId)` çağırır → `FinishRentalResponseDto` alınır → `Effect.NavigateToPayment(rentalId)` gönderilir.
- `Screen` sealed interface'ine `data class ReturnPhoto(val rentalId: String) : Screen` eklenir; `MainActivity.kt`'de `ActiveRentalScreen`'in "Bitir" callback'i buraya, buranın "Teslimi Onayla" callback'i de `PaymentSummary`'ye yönlendirilir.

### 5.6 Ödeme özeti (`PaymentSummaryScreen.kt`)

Şu an tamamen sabit (`₺110,50`, `24 dk`, `VISA •••• 4291` — hiçbir state/ViewModel yok, `onPayClick` direkt Home'a gidiyor).

- `Screen.PaymentSummary(vehicleId)` → `Screen.PaymentSummary(rentalId: String)`.
- `PaymentSummaryContract.kt`: `PaymentSummaryState(rental, wallet, cards, selectedMethod, isPaying, receipt: PayRentalResponseDto?, error)`; `PaymentSummaryEvent` (`LoadSummary`, `MethodSelected(method)`, `CardSelected(cardId)`, `PayClicked`); `PaymentSummaryEffect` (`NavigateHome`, `ShowError(message)`).
- `PaymentSummaryViewModel.kt`: `LoadSummary` (init'te tetiklenir) `RentalApi.getOne(rentalId)` ile kilitlenmiş `totalPrice/startFee/serviceFee/distanceKm/durationMinutes` değerlerini, `WalletApi.getWallet()` ile bakiyeyi, `CardsApi.list()` ile kayıtlı kartları çeker (Wallet/Cards için mevcut `feature/wallet` API'leri — `Cards` için henüz Retrofit arayüzü yoksa `feature/wallet` içine `CardsApi` eklenir, `WalletApi`'nin yanına).
- Kullanıcı `onEvent(MethodSelected(WALLET/CARD))` ve gerekirse `CardSelected(cardId)` ile seçim yapar; "Öde" butonu → `onEvent(PayClicked)` → `RentalApi.pay(rentalId, PayRentalDto(method, cardId?))` çağırır. Başarılı olursa `receipt` state'e yazılıp kısaca gösterilir, sonra `Effect.NavigateHome` gönderilir. 409 (yetersiz bakiye vb.) durumunda `Effect.ShowError` gönderilir, kullanıcı ekranda kalır.

---

## Bölüm 6 — Navigasyon (`Screen.kt`, `MainActivity.kt`)

`Screen.kt` değişiklikleri:
```kotlin
data class HandoverPhoto(val rentalId: String) : Screen        // vehicleId yerine rentalId
data class ActiveRental(val rentalId: String) : Screen         // vehicleId yerine rentalId
data class ReturnPhoto(val rentalId: String) : Screen          // YENİ
data class PaymentSummary(val rentalId: String) : Screen       // vehicleId yerine rentalId
// ReservationConfirmation(vehicleId) DEĞİŞMEZ — rental henüz yok, plan seçimi burada oluşturuluyor
```
`MainActivity.kt:236-287` içindeki ilgili `composable<Screen.X>` blokları yeni parametreye ve yeni callback zincirine göre güncellenir (`ReservationConfirmation → HandoverPhoto(rentalId) → ActiveRental(rentalId) → ReturnPhoto(rentalId) → PaymentSummary(rentalId) → Home`).

**Not — MVI tutarlılığı:** Bu planda tarif edilen tüm yeni sunum katmanı bileşenleri (`ReservationConfirmation`, `HandoverPhoto`, `ActiveRental`, `ReturnPhoto`, `PaymentSummary`) `docs/architecture/mvi-overview.md`'deki Contract (State/Event/Effect) + `onEvent` deseni izler; `WalletContract.kt`/`WalletViewModel.kt` ve `RentalHistoryContract.kt`/`RentalHistoryViewModel.kt` referans alınacak kanonik örneklerdir (`_state: MutableStateFlow`, `_effect: Channel<...>` + `receiveAsFlow()`). Bölüm 5.1'de genişletilen `MapContract`/`MapViewModel` ise mevcut haliyle effect için `Channel` yerine `MutableSharedFlow` kullandığından, o dosyaya eklenecek yeni event/effect'ler tutarlılık için mevcut `MutableSharedFlow` stiline uyar (yeni bir stil karışıklığı yaratmamak için).

---

## Doğrulama Planı

Bu değişiklikler gerçek backend'e (`https://rencarv2.halitkalayci.com/`) bağlı olduğundan, uçtan uca test için:
1. `./gradlew assembleDebug` ile derleme hatasız geçmeli (yeni DTO/API/DI modülleri).
2. Gerçek cihaz/emülatörde: telefonla giriş → OTP (123456) → ehliyet onaylıysa **direkt anasayfa** (yeni test senaryosu: onaylı bir test kullanıcısıyla giriş yapıp License ekranına hiç uğramadığını doğrula).
3. Haritadan bir araca dokun → "Rezerve Et" → 15 dk sayaç görünmeli → aynı araca tekrar dokun → "Kilidi Aç" artık aktif → plan seç → onay → 4 foto çek/yükle (sayaç sunucudan gelmeli) → "Kiralamayı Başlat" → aktif sürüş ekranında süre/ücret birkaç saniyede bir artmalı (gerçek `GET /rentals/active` polling).
4. "Bitir" → 4 foto (yüklenmeyecek, sadece yerel) → "Teslimi Onayla" → ödeme özetinde gerçek `finish` tutarları görünmeli → WALLET ile öde (yetersiz bakiye ve yeterli bakiye senaryoları) → Home'a dön, cüzdan bakiyesi düşmüş olmalı.
5. Ara senaryolar: PREPARING aşamasında geri çıkıp kiralamayı iptal etme (araç tekrar AVAILABLE olmalı), uygulamayı kapatıp açma (harita ekranı `GET /reservations/active` / `GET /rentals/active` ile kaldığı yeri geri kazanmalı).

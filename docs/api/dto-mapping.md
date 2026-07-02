# DTO (Data Transfer Object) Mapping Stratejisi

Bu doküman, `docs/decisions.md` dosyasındaki "DTO'ları UI'da Kullanmak" kararına uygun olarak, `openapi.json` (Swagger) içerisindeki DTO'ların uygulama içindeki **Domain Model**'lere (UI ve İş mantığında kullanılacak modellere) nasıl dönüştürüleceğini tanımlar.

## 1. Temel Kural
API'den gelen hiçbir DTO doğrudan UI (Jetpack Compose) katmanına aktarılmayacaktır. Tüm DTO'lar, Repository veya UseCase katmanında "Mapper" (dönüştürücü) fonksiyonlar aracılığıyla arındırılarak Domain Modellerine çevrilecektir.

---

## 2. DTO -> Domain Model Eşleşmeleri

### 2.1. Auth ve Kullanıcı (User)
**API DTO'ları:** `UserResponseDto`, `AuthResponseDto`

**Domain Model: `User`**

| API DTO Alanı (`UserResponseDto`) | Domain Model Alanı (`User`) | Tip | Açıklama |
| :--- | :--- | :--- | :--- |
| `id` | `id` | String | Kullanıcının benzersiz ID'si |
| `email` | `email` | String | E-posta adresi |
| `phone` | `phone` | String? | Nullable telefon numarası |
| `fullName` | `fullName` | String | Ad ve soyad |
| `role` | `role` | UserRole (Enum) | PENDING, CUSTOMER, ADMIN |

*Not: `AuthResponseDto` içerisindeki `accessToken` ve `refreshToken` API modülü/güvenli alana kaydedilir, DTO'nun içindeki `user` objesi yukarıdaki `User` modeline dönüştürülüp UI'a aktarılır.*

### 2.2. Ehliyet (License)
**API DTO'ları:** `LicenseResponseDto`, `LicenseStatusResponseDto`

**Domain Model: `LicenseStatus`**

| API DTO Alanı | Domain Model Alanı | Tip | Açıklama |
| :--- | :--- | :--- | :--- |
| `status` | `status` | LicenseState (Enum) | NOT_SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED |
| `frontImageUrl` | `frontImageUrl` | String? | Opsiyonel resim URL'i |
| `backImageUrl` | `backImageUrl` | String? | Opsiyonel resim URL'i |
| `rejectReason` | `rejectReason` | String? | Yalnızca REJECTED durumunda dolu gelir |
| `reviewedAt` | `reviewedAt` | LocalDateTime? | Kotlin DateTime tipine (veya Date) çevrilir |

### 2.3. Araç (Vehicle)
**API DTO'su:** `VehicleResponseDto`

**Domain Model: `Vehicle`**

| API DTO Alanı (`VehicleResponseDto`) | Domain Model Alanı (`Vehicle`) | Tip | Açıklama |
| :--- | :--- | :--- | :--- |
| `id` | `id` | String | Aracın ID'si |
| `plate` | `plate` | String | Plaka bilgisi |
| `brand`, `model` | `brand`, `model` | String | Marka ve model |
| `type` | `type` | VehicleType (Enum) | SEDAN, SUV, HATCHBACK, STATION, MINIVAN |
| `pricePerDay` | `pricePerDay` | Double | Günlük kiralama ücreti |
| `status` | `status` | VehicleStatus (Enum) | AVAILABLE, RENTED, MAINTENANCE |
| `latitude`, `longitude` | `location` | Location (Nesne) | Enlem ve boylam harita modülü için `Location` nesnesine gruplanır |
| `createdAt`, `updatedAt` | - | - | Genellikle UI'da gösterilmez, arındırılır. |

### 2.4. Kiralama (Rental)
**API DTO'su:** `RentalResponseDto`

**Domain Model: `Rental`**

| API DTO Alanı (`RentalResponseDto`) | Domain Model Alanı (`Rental`) | Tip | Açıklama |
| :--- | :--- | :--- | :--- |
| `id` | `id` | String | Kiralama işlem ID'si |
| `vehicleId` | `vehicleId` | String | Kiralanan araç ID'si (Detay çekmek için) |
| `startDate` | `startDate` | LocalDateTime | Başlangıç tarihi (String'den Parse edilir) |
| `endDate` | `endDate` | LocalDateTime | Bitiş tarihi |
| `totalPrice` | `totalPrice` | Double | Toplam fiyat |
| `status` | `status` | RentalStatus (Enum) | ACTIVE, COMPLETED, CANCELLED |

---

## 3. Mapper Örneği (Kotlin)
Repository katmanında DTO'yu Domain'e çevirmek için kullanılacak örnek `extension` fonksiyon mimarisi:

```kotlin
fun UserResponseDto.toDomain(): User {
    return User(
        id = this.id,
        email = this.email,
        phone = this.phone,
        fullName = this.fullName,
        role = UserRole.valueOf(this.role) // PENDING, CUSTOMER, ADMIN
    )
}
```
Bu yaklaşım, Backend API'sinde oluşabilecek DTO değişikliklerinin (`openapi.json` güncellemeleri) sadece Data katmanındaki (Mapper) fonksiyonunu etkilemesini sağlar, UI katmanı tamamen yalıtılır.

# Mock Data (Sahte Veri) Stratejisi

Bu doküman, henüz backend API tam olarak hazır olmadığında veya lokal geliştirme (UI ön izlemeleri - Previews) sırasında kullanılacak sahte (mock) verilerin standartlarını tanımlar. Buradaki veriler `openapi.json` içerisindeki `example` alanları temel alınarak oluşturulmuştur.

## 1. Kullanıcı (User & Auth) Mock Verileri

Jetpack Compose Preview'larında veya Offline testlerde kullanılabilecek örnek kullanıcı modelleri:

### PENDING (Ehliyetsiz) Kullanıcı
```json
{
  "id": "clx0a1b2c3d4e5f6g7h8i9j0k",
  "email": "ahmet.yilmaz@example.com",
  "phone": "+905551112233",
  "fullName": "Ahmet Yılmaz",
  "role": "PENDING",
  "createdAt": "2026-06-26T10:00:00.000Z",
  "updatedAt": "2026-06-26T10:00:00.000Z"
}
```

### CUSTOMER (Onaylı) Kullanıcı
```json
{
  "id": "clx0a1b2c3d4e5f6g7h8i9j0k",
  "email": "ahmet.yilmaz@example.com",
  "phone": "+905551112233",
  "fullName": "Ahmet Yılmaz",
  "role": "CUSTOMER",
  "createdAt": "2026-06-26T10:00:00.000Z",
  "updatedAt": "2026-06-28T14:00:00.000Z"
}
```

## 2. Araç (Vehicle) Mock Verileri

Araç listesi (Ana Ekran) önizlemesi için kullanılabilecek örnek liste elemanı:

```json
{
  "id": "clx0veh1234567890",
  "plate": "34 ABC 123",
  "brand": "Volkswagen",
  "model": "Passat",
  "type": "SEDAN",
  "pricePerDay": 1500.0,
  "status": "AVAILABLE",
  "latitude": 41.0151,
  "longitude": 28.9795,
  "createdAt": "2026-06-30T10:00:00.000Z",
  "updatedAt": "2026-06-30T10:00:00.000Z"
}
```
*(Birden fazla araç listesi için ID'ler ve plaka/marka bilgileri değiştirilerek sahte listeler türetilebilir)*

## 3. Ehliyet Durumu (License Status) Mock Verileri

Ehliyet onay bekleme ekranının tasarımı için kullanılacak `LicenseStatus` varyasyonları:

### İncelemede (Under Review)
```json
{
  "status": "UNDER_REVIEW",
  "frontImageUrl": "http://localhost:3000/uploads/licenses/front-123.jpg",
  "backImageUrl": "http://localhost:3000/uploads/licenses/back-123.jpg",
  "rejectReason": null,
  "reviewedAt": null
}
```

### Reddedilmiş (Rejected)
```json
{
  "status": "REJECTED",
  "frontImageUrl": "http://localhost:3000/uploads/licenses/front-123.jpg",
  "backImageUrl": "http://localhost:3000/uploads/licenses/back-123.jpg",
  "rejectReason": "Fotoğraf bulanık, ehliyet numarası okunmuyor.",
  "reviewedAt": "2026-06-27T11:00:00.000Z"
}
```

## 4. Kiralama (Rental) Mock Verileri

Aktif kiralama ekranı veya geçmiş kiralamalar listesi için örnek veri:

```json
{
  "id": "clx0rent1234567890",
  "userId": "clx0usr1234567890",
  "vehicleId": "clx0veh1234567890",
  "startDate": "2026-06-30T12:00:00.000Z",
  "endDate": "2026-07-05T10:00:00.000Z",
  "totalPrice": 7500.0,
  "status": "ACTIVE",
  "createdAt": "2026-06-30T12:00:00.000Z"
}
```

## 5. Uygulama İçi (Mock Interceptor / Preview) Kullanımı

Eğer Backend henüz tamamlanmamışsa, OkHttp'ye bir `MockInterceptor` eklenerek yukarıdaki JSON string'ler direkt olarak HTTP 200/201 dönüşü şeklinde sarmalanabilir. UI (Compose Preview) tarafında ise, DTO JSON'larına ihtiyaç duyulmadan doğrudan Domain Model sınıfları ile hardcoded veriler (örn. `val mockVehicle = Vehicle(id = "1", ...)`) kullanılmalıdır.

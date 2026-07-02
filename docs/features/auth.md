# auth

Bu dokuman, RenCar uygulamasinda giris, kayit ve oturum yonetimi icin uyulacak kurallari tanimlar.

## Kapsam

- Telefon tabanli ilk bilgi toplama akisi ile backend'in destekledigi giris/kayit modeli arasindaki uyum
- Login ve register ekranlarinin ayri olmasi
- OTP adimi varsa UI seviyesinde tutulmasi, backend endpoint net degilse akisin stub olarak ayrilmasi
- Token alma, saklama ve yenileme kurallari

## API ve Veri Kurallari

- `POST /auth/register` yeni kullanici kaydi icin kullanilir.
- `POST /auth/login` mevcut kullaniciyi oturum acmak icin telefon numarasi ile SMS kodu talep eder.
- `POST /auth/verify-otp` SMS kodunu dogrulayip tokenlari doner.
- `POST /auth/refresh` access token yenilemek icin kullanilir.
- `POST /auth/logout` tum aktif refresh oturumlarini iptal eder.
- `GET /auth/me` aktif kullanicinin profilini ve rolunu getirir.
- Kayit formunda `fullName`, `email`, `phone`, `password` alanlari bulunur (`phone` artik zorunludur).
- Login formunda `phone` alani bulunur, ardindan gelen adimda ise `code` girilir.

## Uygulama Kurallari

- Auth akisi MVI ile yonetilir.
- Form alanlari ekran state'inde tutulur, network call repository/use-case katmanina gider.
- Basarili login veya register sonrasi tokenlar guvenli saklamaya yazilir.
- `PENDING` rolundeki kullanicilar uygulamaya giris yapabilir ancak kiralama gibi korumali islem yapamaz.
- `401` alindiginda otomatik refresh tetiklenir.
- Refresh basarisiz olursa kullanici login ekranina dusurulur.

## UI Kurallari

- Login ve register ayri ekran olarak tasarlanir.
- Ekranlar arasinda toggle veya gecis linki bulunur.
- Hatali alanlar anlik validation ile gostirilir.
- Loading aninda butonlar pasif hale getirilir.

## Durumlar

- Baslangic
- Form degisimi
- Validation hatasi
- Request loading
- Basarili oturum
- Hata durumu

## Notlar

- Mevcut backend contract'ta OTP verify endpoint'i yoksa OTP ekranı sadece tasarim/akış adimi olarak tutulur.
- Auth feature diger tum feature'larin giris noktasi oldugu icin burada yapilan degisiklikler navigation ve token interceptor tarafini etkiler.


# rentals

Bu dokuman, kiralama ile ilgili tum adimlari tanimlar.

## Kapsam

- Rezervasyon onayi
- Arac teslim fotografi
- Aktif kiralama
- Kiralamayi bitirme
- Odeme ozeti

## API ve Veri Kurallari

- `POST /rentals` kiralama baslatir.
- `GET /rentals` kullanicinin kiralama gecmisini getirir.
- `GET /rentals/{id}` tek kiralama detayini getirir.
- `POST /rentals/{id}/return` kiralamayi sonlandirir.

## Uygulama Kurallari

- Kiralama akisi `CUSTOMER` rolune sahip kullanicilar icin aktif olur.
- `PENDING` kullanici kiralama baslatamaz.
- Rezervasyon ekraninda plan secimi, fiyat ozeti ve onay checkbox'i bulunur.
- Arac teslim fotografi adimi kiralama baslamadan once zorunlu kabul edilir.
- Aktif kiralama ekraninda sure, mesafe ve anlik ucret gosterilir.
- Odeme ozeti kiralama bittikten sonra gelir.

## UI Kurallari

- Kiralama onayi, teslim fotografi ve aktif kiralama ayri sayfalardir.
- Bir adim tamamlanmadan sonraki adim aktif edilmez.
- Iptal ve geri donus butonlari her kritik ekranda bulunur.
- Hata durumunda kullanici kiralama akisinda kalir, veri kaybi olmaz.

## Durumlar

- Plan secildi / secilmedi
- Fotograf eksik / tam
- Kiralama basladi
- Kiralama devam ediyor
- Kiralama bitti
- Odeme basarili / basarisiz

## Notlar

- Teslim fotografi backend tarafinda medya upload gerektiriyorsa bu alan ayrica stub ile soyutlanmalidir.
- Kiralama verileri liste ve detay ekranlarinda ayni domain modeli uzerinden map edilmelidir.


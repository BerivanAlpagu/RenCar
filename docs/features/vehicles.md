# vehicles

Bu dokuman, harita uzerinden arac gosterimi ve arac detay akisini tanimlar.

## Kapsam

- Yakindaki uygun araclarin listelenmesi
- Harita uzerinde arac konumlari
- Arac detay bottom sheet
- Arac secimi ve rezervasyon akisi

## API ve Veri Kurallari

- `GET /vehicles` uygun araclari getirir.
- `GET /vehicles/{id}` secilen aracin detayini getirir.
- Araç verisi fiyat, konum, durum ve ozellikleri icermelidir.
- Listeleme ve detay verisi birbirini tutmalidir.

## UI Kurallari

- Harita ekranı ana akistir.
- Arac detaylari bottom sheet ile acilir.
- Secili arac state olarak tutulur.
- Harita ekraninda filtreler ve yakindaki arac bilgisi gosterilir.

## MVI Kurallari

- Harita, secili arac ve filtre state'i presentation katmaninda tutulur.
- Secim degisince detay ekranina gecis yapilir.
- Loading durumunda iskelet ekran veya placeholder kullanilir.
- Bos liste durumunda kullaniciya baska bolge secmesi onerilir.

## Notlar

- Konum verisi backend'den geliyor gibi ele alinmali.
- Harita implementasyonu sonradan native map SDK ile degistirilebilir ama feature sozlesmesi bozulmamalidir.


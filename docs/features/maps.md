# maps

Bu dokuman, harita tabanli ana ekran kurallarini tanimlar.

## Kapsam

- Kullanici konumuna yakin araclari gosterme
- Arac etiketleri ve fiyatlari
- Arama ve filtreleme
- Bottom navigation ile birlikte calisma

## Kurallar

- Harita ekranı uygulamanin ana giris noktalarindan biridir.
- Kullanici konumu yetkisi reddedilirse yedek bolge bazli gosterim yapilir.
- Arac tiklaninca detay bottom sheet acar.
- Filtreler state olarak tutulur.

## API

- `GET /vehicles`
- `GET /vehicles/{id}`

## Notlar

- Harita verisi ile liste verisi ayni source'dan beslenmelidir.
- Offline durumda son bilinen arac listesi okunabilir ama kiralama gibi islemler network ister.


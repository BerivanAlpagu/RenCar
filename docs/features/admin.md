# admin

Bu dokuman, admin rollerine ozel alanlar icin genel kurallari tanimlar.

## Kapsam

- Arac yonetimi
- Ehliyet inceleme
- Kiralama yonetimi
- Gerekirse operasyonel kontrol ekranlari

## API ve Veri Kurallari

- Admin endpointlerine sadece `ADMIN` rolune sahip kullanicilar erisebilir.
- `/admin/vehicles` CRUD islemleri icin kullanilir.
- `/admin/licenses` ehliyet inceleme icin kullanilir.
- `/admin/rentals` kiralamalari izlemek icin kullanilir.
- `/admin/locations` anlik arac konum snapshot'lari icin kullanilir.

## Uygulama Kurallari

- Admin alanlari normal customer akisindan tamamen ayrilir.
- Yetki kontrolu hem backend hem UI tarafinda yapilir.
- Listeleme ekranlarinda filtreleme ve detay goruntuleme bulunur.
- Onay/red gibi kritik islemler feedback ile desteklenir.

## Notlar

- Admin feature su an son kullanici akisinda gorunmeyebilir ama domain sozlesmesi korunmali.
- Bu alanlar ileride ayri navigation veya ayri module olarak parcali hale getirilebilir.


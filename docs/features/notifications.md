# notifications

Bu dokuman, bildirim davranislarini ve uygulama ici durum iletilerini tanimlar.

## Kapsam

- Push veya local bildirimler
- Kiralama durumu degisiklikleri
- Ehliyet inceleme sonucu
- Odeme/rezervasyon sonuc bildirimleri

## Kurallar

- Kritik olaylar bildirim olarak kullaniciya iletilir.
- Bildirimlerin okunmus/bekleyen durumu ayrilabilir.
- Bildirimler ana akisi bloklamaz.

## Onerilen Olaylar

- Kayit basarili
- Ehliyet inceleme basladi
- Ehliyet onaylandi / reddedildi
- Kiralama basladi
- Kiralama bitirildi
- Odeme basarili

## Notlar

- Bildirim feature'i backend socket veya push altyapisina baglanabilir.
- Su an icin local toast/snackbar seviyesinde stub kullanilabilir.


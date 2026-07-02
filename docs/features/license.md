# license

Bu dokuman, ehliyet dogrulama ve onay akisini tanimlar.

## Kapsam

- Ehliyet on yuz yukleme
- Ehliyet arka yuz yukleme
- Selfie veya ek dogrulama adimi varsa onu ayri state olarak tutma
- Onay bekleme ve reddedilme durumlari

## API ve Veri Kurallari

- `POST /license/upload` ehliyet belgelerini yukler.
- `GET /license/status` mevcut dogrulama durumunu getirir.
- Durumlar en az su sekilde ele alinmalidir:
  - `NOT_SUBMITTED`
  - `UNDER_REVIEW`
  - `APPROVED`
  - `REJECTED`

## Uygulama Kurallari

- Sadece yetkili rol ve durumdaki kullanicilar bu adima girebilir.
- Yukleme tamamlanmadan onay adimi aktif olmaz.
- Reddedilen kullanici tekrar yukleme yapabilir.
- Approval durumunda kullanici bir sonraki ana akisa yonlendirilir.

## UI Kurallari

- On yuz, arka yuz ve gerekiyorsa selfie alanlari ayri kartlar halinde gosterilir.
- Yukleme durumlari badge ile belirtilir.
- Bekleme durumunda kullaniciya acik ve kisa bilgi verilir.
- Onayli durumda aksiyon butonu ilerleme yonunde olur.

## Notlar

- Ekran MVI state ile yonetilir.
- Dosya secme, kamera acma ve upload sonuc state'leri birbirinden ayrilmali.


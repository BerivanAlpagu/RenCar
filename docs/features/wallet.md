# wallet

Bu dokuman, Cuzdan ekranini ve bakiye / kart yonetimini tanimlar.

## Kapsam

- Rencar bakiyesi
- Kayıtlı kartlar
- Son islemler
- Bakiye yukleme aksiyonu

## Durum

- PDF'de wallet ayri bottom-nav sekmesi ve ayri ekran olarak yer alir.
- Bu alan kiralama gecmisine sikistirilmaz.
- Mevcut backend contract'ta wallet endpoint'i yoksa ekran mock veri ile calisir.

## Uygulama Kurallari

- Bakiye karti ustte yer alir.
- Kayıtlı kartlar ayri listede gosterilir.
- Son islemler pozitif/negatif renklerle ayrilir.
- Varsayilan kart etiketi gosterilebilir.

## Notlar

- Gercek backend baglantisi geldiginde kart ve transaction verisi repository katmanina tasinir.
- Bakiye yukleme islemi su an UI stub olarak ele alinabilir.


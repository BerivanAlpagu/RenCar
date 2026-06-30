# profile

Bu dokuman, kullanici profil sayfasi ve hesap ayarlari kurallarini tanimlar.

## Kapsam

- Kullanici bilgileri
- Ehliyet durumu ozet karti
- Odeme yontemlerine yonlendirme
- Ayarlar
- Davet ve cikis aksiyonlari

## API ve Veri Kurallari

- `GET /auth/me` kullanici adini, emailini ve rolunu getirir.
- `GET /license/status` profil icinde ehliyet durumunu gostermek icin kullanilir.
- `POST /auth/logout` cikis icin kullanilir.

## Uygulama Kurallari

- Profil sayfasi bottom-nav uzerinden erisilebilir.
- Kullanici bilgileri degistirilebilir alanlar ile salt okunur alanlar ayri ele alinmalidir.
- Cikis yap islemi local session temizligi ile birlikte calismalidir.

## UI Kurallari

- Ustte avatar, isim ve telefon bilgisi bulunur.
- Altinda ehliyet durumu karti yer alir.
- Menu aksiyonlari listelenir.
- Kritik aksiyonlar ayrilmis ve net gorunur olmalidir.


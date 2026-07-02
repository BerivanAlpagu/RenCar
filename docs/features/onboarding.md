# onboarding

Bu dokuman, uygulamanin ilk acilis ve kullanici yonlendirme adimlarini tanimlar.

## Kapsam

- Splash ekranı
- Onboarding benzeri ilk karşılama ekranı
- Giris ekranina yonlendirme

## Kurallar

- Ilk acilista kullanici direkt ana ekrana alinmaz.
- Oturum varsa `GET /auth/me` ile durum kontrol edilir.
- Oturum yoksa login ekranina gecilir.
- Splash ekraninda sadece marka, kisa mesaj ve basla aksiyonu bulunur.
- Onboarding birden fazla sayfaya ayrilacaksa state gecisleri lokal tutulur.

## Akis

- Uygulama acilir.
- Mevcut session kontrol edilir.
- Session varsa akisa devam edilir.
- Session yoksa giris/kayit ekranina gidilir.

## Notlar

- Onboarding metinleri kisa, tek amacli ve eylem odakli olmalidir.
- Bu alan feature'larin geri kalanindan bagimsiz olmalidir.


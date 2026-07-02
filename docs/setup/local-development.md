# local-development

Bu dokuman, projeyi yerelde nasil calistiracagini tanimlar.

## Adimlar

1. Repo'yu ac.
2. Android Studio'da Gradle sync yap.
3. `app` modulu icin uygun emulator sec.
4. Gerekirse emulator'a `Wipe Data` veya `Cold Boot` uygula.
5. `assembleDebug` ile derlemeyi kontrol et.
6. Uygulamayi calistir.

## Kurallar

- Cihazi secmeden run denemesi yapma.
- Run configuration olarak `app` modulu secili olmali.
- Birden fazla proje aciksa dogru root klasorundesin diye emin ol.
- Debug build alinirken terminaldeki hata mesajini once oku.

## Pratik Notlar

- Emulator baska uygulamada acilsa sorun degil; app yeniden baslatilabilir.
- Run tusu pasifse once sync, sonra device, sonra run config kontrol edilir.
- Build basarili ama run olmuyorsa genelde sorun IDE ayarindadir.


# environment-setup

Bu dokuman, gelistirme ortaminin nasil kurulacagini tanimlar.

## Gerekli Bilesenler

- Android Studio guncel stabil surum
- JDK 21
- Android SDK ve emulator image
- Git
- Gradle Wrapper

## Kurulum Sirasi

1. Repoyu local'e cek.
2. Android Studio ile proje klasorunu ac.
3. Gradle sync calistir.
4. Gerekli SDK bileşenlerini indir.
5. Bir emulator veya fiziksel cihaz tanimla.
6. `assembleDebug` ile proje derlemesini dogrula.

## Kurallar

- `local.properties` ve gizli dosyalar repo'ya eklenmez.
- IDE JDK'si ile Gradle JDK'si uyumlu olmali.
- Emulator Android API seviyesi proje hedefi ile uyumlu olmali.
- Ilk acilista sync hata verirse once Gradle tarafini duzelt.

## Notlar

- Bu proje Compose tabanli oldugu icin UI preview ve hot reload benzeri akislardan faydalanilabilir.
- Ortam degisiklikleri sonrasinda yeniden sync almak iyi pratiktir.


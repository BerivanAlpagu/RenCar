# build-variants

Bu dokuman, derleme varyantlari ve ortam ayarlarini tanimlar.

## Mevcut Yapı

- Debug
- Release

## Kurallar

- Debug varyanti gelistirme ve test icin kullanilir.
- Release varyanti minify/obfuscation ve imza ayarlari icin ayridir.
- Uretim sirrari debug kaynaklarinda tutulmaz.

## Onerilen Ortam Ayarlari

- Debug: local/mock base URL
- Release: production base URL
- Gerekirse staging varyanti ileride eklenebilir

## Notlar

- Varyant degisince API base url, log seviyesi ve crash reporting ayri davranabilir.
- Yeni flavor eklenirse docs ve CI scriptleri birlikte guncellenmelidir.


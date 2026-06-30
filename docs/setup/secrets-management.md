# secrets-management

Bu dokuman, gizli bilgi ve anahtarlarin nasil yonetilecegini tanimlar.

## Kurallar

- API anahtarlari repoya yazilmaz.
- `local.properties` icine hassas bilgi konmaz.
- `.env` veya benzeri yerel dosyalar gerekiyorsa gitignore ile korunur.
- JWT, refresh token ve session bilgileri guvenli saklamaya yazilir.

## Saklama Stratejisi

- Uygulama ici tokenlar icin EncryptedSharedPreferences veya guncel guvenli DataStore yaklasimi kullanilir.
- Build-time gizli bilgiler CI environment variables ile gelir.
- Loglara token, sifre veya gizli header yazilmaz.

## Notlar

- Debug loglarinda da sirri maskelemek gerekir.
- Admin veya backend anahtarlari mobile uygulamaya gomulmez.


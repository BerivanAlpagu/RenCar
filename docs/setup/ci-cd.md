# ci-cd

Bu dokuman, entegrasyon ve dagitim akisini tanimlar.

## Hedef

- Her degisiklikte derleme kontrolu
- Testlerin otomatik calismasi
- Release artifact olusturma

## Onerilen Akis

1. Kod degisikligi push edilir.
2. CI pipeline `assembleDebug` veya ilgili testleri calistirir.
3. Basariliysa merge onayi verilir.
4. Release icin ayrica imzali derleme alinir.

## Kurallar

- CI sirrari repository icinde tutulmaz.
- Build fail olursa once compile, sonra test, sonra packaging kontrol edilir.
- Lint ve unit test eklenirse pipeline'a dahil edilmelidir.

## Notlar

- Mevcut proje icin ilk asama sadece debug build dogrulamasi olabilir.
- Sonraki asamada release signing ve artifact upload eklenebilir.


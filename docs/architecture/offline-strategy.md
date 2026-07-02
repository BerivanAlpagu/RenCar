# Çevrimdışı (Offline) Kullanım Stratejisi

Bu doküman, uygulamanın internet bağlantısı olmadığında veya ağ bağlantısının zayıf olduğu durumlarda nasıl davranacağını (Offline-first yaklaşımı) tanımlar.

## 1. Temel Yaklaşım: Offline-First

RenCar uygulaması mümkün olduğunca "Offline-first" (Önce Çevrimdışı) mantığıyla çalışacaktır. Bu, kullanıcının interneti olmasa bile daha önceden yüklediği verileri görebilmesi anlamına gelir. Bu strateji [caching-strategy.md](caching-strategy.md) dokümanında belirtilen "Tek Gerçek Kaynak" (Room DB) prensibi ile doğrudan bağlantılıdır.

## 2. Ağ Durumu Takibi (Network Monitoring)

Uygulama, cihazın anlık internet durumunu dinlemelidir (Örn: `ConnectivityManager.NetworkCallback` kullanılarak).
- İnternet bağlantısı koptuğunda, UI tarafında kullanıcıyı rahatsız etmeyen küçük bir uyarı (Örn: Ekranın üstünde "İnternet bağlantısı yok, çevrimdışı moddasınız" şeklinde bir Banner) gösterilmelidir.
- Bağlantı geri geldiğinde bu banner kaybolmalı ve Data katmanı bekleyen okuma (fetch) işlemlerini otomatik olarak tekrar denemelidir.

## 3. Okuma (Read) İşlemlerinde Offline Senaryosu
- **Araç Listesi & Geçmiş Kiralamalar:** İnternet yoksa, Room Database'de bulunan en son kayıtlı veriler kullanıcıya gösterilmeye devam eder. Yükleme (Loading) ikonu gösterilmez, veri "eski" bile olsa listelenir.
- **Detay Sayfaları:** Daha önce açılmamış bir araç detayına tıklanırsa ve Local DB'de bu aracın detayı tam yoksa, o an için "Lütfen internete bağlanın" şeklinde bir hata ekranı (Empty/Error State) gösterilir.

## 4. Yazma (Write/Mutasyon) İşlemlerinde Offline Senaryosu
Araç kiralama, profil güncelleme veya ehliyet yükleme gibi işlemlerde (POST/PUT/PATCH):
- **Anlık Doğrulama:** İnternet yoksa işlem kesinlikle yerelde (Local DB) "bekliyor" (Pending) statüsünde tutulmaz. (Örn: Başkası aynı aracı kiralayabilir).
- **Hata Gösterimi:** Kullanıcı "Kirala" butonuna bastığında `NoConnectivityException` fırlatılır ve UI'da "Bu işlem için internet bağlantısı gereklidir" uyarısı (Dialog veya Snackbar) anında gösterilir.
- **İstisna (Arka Plan İşlemleri):** Eğer çok büyük dosyalar (Ehliyet fotoğrafları) yükleniyorsa, **WorkManager** kullanılarak işlem arka plana alınabilir ve "İnternet geldiğinde yüklemeye devam et" stratejisi uygulanabilir. Ancak mevcut MVI ve UseCase yapısında, finansal kritiklik taşıyan kiralama işlemlerinde anlık red (Fail Fast) yaklaşımı esastır.

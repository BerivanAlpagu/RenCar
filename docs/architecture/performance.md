# Performans Stratejisi

Bu doküman, Android uygulamasının akıcı, hafıza dostu (Memory-efficient) ve düşük pil tüketimiyle çalışmasını sağlamak için uyulması gereken performans kurallarını tanımlar.

## 1. Jetpack Compose Performans Kuralları (Recomposition)

Compose UI'da en büyük performans düşüşü gereksiz "Recomposition" (Yeniden Çizim) işlemlerinden kaynaklanır.

- **Stabilite (Stability) Kavramı:** State objeleri ve Listeler gibi veri yapıları olabildiğince `Immutable` (değişmez) olmalıdır. Veri sınıflarında (data class) `var` yerine daima `val` kullanılmalıdır. Gerekirse veri sınıfına `@Immutable` veya `@Stable` notasyonu eklenerek Compose'un bu objenin değişmediğinden emin olması sağlanır.
- **Listeler ve Key Kullanımı:** `LazyColumn` veya `LazyRow` kullanılırken, her `item` için kesinlikle eşsiz (unique) bir `key` parametresi (Örn: `vehicleId`) verilmelidir. Aksi halde listedeki bir eleman değiştiğinde tüm liste baştan çizilir.
- **Derived State:** Sürekli güncellenen değerler (Örn: Scroll pozisyonu) yüzünden ekranın tamamının çizilmesini önlemek için `derivedStateOf {}` bloğu kullanılmalıdır. Böylece sadece şart sağlandığında UI uyarılır.

## 2. Ağ (Network) ve Veri Performansı

- **Sayfalama (Pagination):** Büyük veriler (Örn: 1000 tane araç kaydı) API'den tek seferde çekilmez. `Paging 3` kullanılarak 20'şerlik (Limit) paketler halinde, kullanıcı aşağı kaydırdıkça indirilir. (Bkz: [pagination.md](../api/pagination.md))
- **Gereksiz İsteklerin Engellenmesi:** Token refresh (`/auth/refresh`) işlemlerinde çoklu istek fırlamasını engellemek için Authenticator içinde `Mutex` (Kilitleme) kullanılacaktır.
- **WebSocket Optimizasyonu:** Araç konumlarını anlık çeken WebSocket bağlantısı (`/admin/locations`), sadece kullanıcı Harita ekranında aktif olarak beklerken açılmalı, ekran arka plana (onPause) geçtiğinde pil tüketimini durdurmak için derhal kapatılmalıdır.

## 3. Resim Yükleme (Image Loading)

- Projede görsel yönetimi için **Coil** kullanılacaktır.
- Resimler indirilirken cihazın RAM'ini şişirmemek adına; resimler liste boyutuna göre otomatik olarak küçültülecek (downsampling) ve Coil'in disk/memory önbellekleme (caching) özelliklerinden faydalanılacaktır.

## 4. Bellek Sızıntısı (Memory Leak) Önlemleri

- ViewModel'ler içerisine kesinlikle `Context`, `Activity` veya View referansları (Örn: Compose bileşenleri) aktarılmamalıdır (Inject edilmemelidir). Aksi takdirde ekran döndürüldüğünde Activity silinemez ve Memory Leak oluşur. Gerekirse sadece `ApplicationContext` enjekte edilmelidir.
- Uygulama geliştirme ve test sürecinde sızıntıları otomatik tespit etmek için (Sadece Debug sürümünde) **LeakCanary** kütüphanesi aktif edilmelidir.

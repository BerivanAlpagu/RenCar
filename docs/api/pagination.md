# Pagination (Sayfalama) Stratejisi

Bu doküman, `openapi.json` içerisindeki listeleme (GET) endpoint'lerinde kullanılan sayfalama mantığını ve Android uygulamasında bu yapının **Jetpack Paging 3** kütüphanesi ile nasıl entegre edileceğini tanımlar.

## 1. API Sayfalama Parametreleri

Sayfalamayı destekleyen uç noktalar (örn: `GET /vehicles`, `GET /admin/vehicles`, `GET /admin/rentals`), sorgu (query) parametreleri aracılığıyla yönetilir.

- **`page`**: Yüklenmek istenen sayfa numarasıdır. (Minimum: 1, İlk sayfa: 1)
- **`limit`**: Bir sayfada dönecek maksimum kayıt sayısıdır. (1 ile 100 arası değer alır, Örn: 20)

**Örnek İstek URL'i:**
`GET /vehicles?type=SUV&page=1&limit=20`

## 2. API Yanıt (Response) Yapısı

Mevcut `openapi.json` tasarımına göre, sayfalama içeren endpoint'ler veriyi bir üst Wrapper (örn. `totalCount`, `nextPage` içeren bir JSON nesnesi) olmadan, **doğrudan Liste (Array)** olarak dönmektedir:

```json
[
  { "id": "clx0veh123...", "plate": "34 ABC 123", "status": "AVAILABLE" },
  { "id": "clx0veh456...", "plate": "34 DEF 456", "status": "AVAILABLE" }
]
```

## 3. İstemci (Android) Tarafı Yönetimi

Android uygulamasında performanslı bir sonsuz kaydırma (infinite scrolling) yeteneği sağlamak adına **Jetpack Paging 3** kütüphanesi kullanılacaktır. Yanıtta sayfalama metadatası olmadığı için aşağıdaki strateji izlenecektir:

### 3.1. PagingSource Mantığı
1. **Başlangıç:** İlk yüklemede `page = 1` olarak istek atılır.
2. **Limit Kontrolü:** API isteğinden dönen veri listesi eleman sayısı (`response.size`) eğer istenen `limit` değerine (örn: 20) eşitse, bir sonraki sayfa mevcuttur `nextKey = page + 1` set edilir.
3. **Sayfanın Sonu (End of Pagination):** Dönen veri setinin boyutu, `limit`'ten daha küçükse (veya liste tamamen boş dönerse), tüm verilerin bittiği anlaşılır ve `nextKey = null` yapılarak Paging işlemi durdurulur.

### 3.2. Offline/Önbellek Desteği (RemoteMediator)
Özellikle Ana Sayfadaki Araç Listesi (`GET /vehicles`) için **Room Database + RemoteMediator** entegrasyonu kurulmalıdır:
- İnternet varken veriler API'den çekilir, yerel veritabanına (Room) yazılır ve UI, akışı doğrudan veritabanından (`PagingSource<Int, Vehicle>`) okur.
- Böylece kullanıcı uygulamayı çevrimdışı başlattığında son kopyalanan araç listesini sorunsuzca gezebilir. Kiralama butonuna tıklandığında network durumu kontrolü (Error Handling) devreye girer.

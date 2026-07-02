# Test Stratejisi (Testing Strategy)

Bu doküman, uygulamanın kod kalitesini, kararlılığını (stability) ve sürdürülebilirliğini güvence altına almak için uygulanacak test hiyerarşisini ve kullanılacak kütüphaneleri tanımlar.

## 1. Test Piramidi

Uygulamada endüstri standardı olan Test Piramidi yaklaşımı benimsenmiştir:
1. **Unit Tests (Birim Testleri - %70-80):** İş mantığının (Domain) ve ViewModellerin hızlı, izole JVM testleri.
2. **Integration Tests (Entegrasyon Testleri - %10-15):** Veritabanı (Room DAO'ları) ve Repository etkileşim testleri.
3. **UI / E2E Tests (Arayüz Testleri - %5-10):** Kritik kullanıcı senaryolarının Jetpack Compose üzerinden uçtan uca testi.

## 2. Birim Testleri (Unit Testing)

En yüksek test kapsamına (coverage) sahip olması gereken katmanlardır. Android framework'ü gerektirmez, doğrudan JVM üzerinde (`src/test` dizininde) milisaniyeler içinde koşar.

### 2.1. Domain Katmanı (Use Cases)
- Hiçbir Android bağımlılığı olmadığı için test edilmesi en kolay kısımdır.
- **Araçlar:** JUnit 4/5, MockK (Repository interface'lerini mock'lamak için), Truth veya Kotest Assertions.
- **Kural:** Her UseCase'in tüm olası senaryoları (Başarılı dönüş, API hatası, Validation Exception vb.) teker teker test edilmelidir.

### 2.2. Presentation Katmanı (ViewModels)
- MVI mimarisi (UDF) sayesinde ViewModel testleri son derece öngörülebilirdir.
- **Test Akışı:** Event yollanır -> UseCase'in mock dönüşü sağlanır -> `StateFlow` un doğru güncellenip güncellenmediği kontrol edilir.
- **Araçlar:** Coroutine akışlarını test etmek için `Turbine` kütüphanesi çok etkilidir. `StateFlow` ve `Channel`'ların yaydığı değerler sırayla `awaitItem()` ile doğrulanır.
- Tüm asenkron işlemler için test boyunca `StandardTestDispatcher` veya `UnconfinedTestDispatcher` kullanılarak coroutine'lerin anında yürütülmesi sağlanır.

## 3. UI ve Entegrasyon Testleri (Instrumented Testing)

Gerçek veya sanal bir Android cihazında çalışan, daha yavaş ama gerçeğe en yakın testlerdir (`src/androidTest` dizini).

### 3.1. Compose UI Testleri
- Arayüz testleri Espresso yerine Compose'un kendi aracı olan `createComposeRule` / `createAndroidComposeRule` ile yazılır.
- **Kural:** UI elemanlarına erişirken ekrandaki metinler (Text) yerine `testTag`'ler üzerinden (`onNodeWithTag("login_button")`) ilerlenmelidir. Bu, dil değişikliklerinde testin kırılmasını önler.
- Hilt Entegrasyonu: Gerçek API çağrıları yapmak yerine, `@TestInstallIn` notasyonu ile sahte (Fake) repository modülleri inject edilerek UI testlerinin deterministic (öngörülebilir) olması sağlanır.

### 3.2. Data Katmanı (Room Database)
- SQL sorgularının ve DAO işlemlerinin doğruluğunu kanıtlamak için Room veritabanı In-Memory (Hafızada: `Room.inMemoryDatabaseBuilder`) olarak oluşturulur. Disk kullanılmadığı için test bittiğinde veri tamamen uçar ve testler arası kalıntı (flakiness) oluşmaz.

## 4. Sürekli Entegrasyon (CI Pipeline)
Tüm birim (Unit) testleri, repoya açılan her Pull Request (PR) anında GitHub Actions (veya GitLab CI) üzerinde otomatik olarak çalıştırılacaktır. Testleri geçemeyen kodlar `main` veya `develop` branch'ine birleştirilemez.

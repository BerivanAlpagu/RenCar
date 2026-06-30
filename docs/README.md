# RenCar Proje Dokümantasyonu (Documentation Index)

Bu dizin, RenCar Android mobil uygulamasının mimari kararlarını, tasarım standartlarını, API entegrasyon kurallarını ve geliştirme süreçlerini barındıran merkezi dokümantasyon merkezidir.

Proje, [ADR Kararlarında](decisions.md) (Architectural Decision Records) tanımlanan kurallara ve kısıtlamalara sıkı sıkıya bağlı olarak geliştirilmektedir.

---

## 1. Dokümantasyon Haritası (Navigation Map)

Aşağıdaki bağlantıları kullanarak ilgili geliştirme ve mimari kılavuzlara hızlıca erişebilirsiniz:

### 1.1. Kurulum ve Başlangıç (Setup)
- [Yerel Geliştirme Ortamı (Local Development)](setup/local-development.md): SDK ayarları ve emülatör konfigürasyonları.
- [Ortam Kurulumu (Environment Setup)](setup/environment-setup.md): Gerekli araçlar ve Java/Kotlin sürümleri.
- [Gizli Değişkenler Yönetimi (Secrets Management)](setup/secrets-management.md): API anahtarları ve keystore şifrelerinin güvenli saklanması.
- [Build Varyantları (Build Variants)](setup/build-variants.md): Development, Staging ve Production derleme kuralları.
- [CI/CD Süreçleri](setup/ci-cd.md): Otomatik test ve dağıtım boru hatları.

### 1.2. Mimari ve Veri Akışı (Architecture & Data Flow)
- [Proje Genel Bakış (Overview)](architecture/overview.md): Clean Architecture katmanları ve modül tasarımı.
- [MVI Mimarisi (MVI Overview)](architecture/mvi-overview.md): Jetpack Compose ile unidirectional data flow (UDF) ve state yönetimi.
- [Bağımlılık Enjeksiyonu (DI - Dependency Injection)](architecture/dependency-injection.md): Dagger Hilt modülleri ve kapsamları.
- [Hata Yönetimi (Error Handling)](architecture/error-handling.md): API hataları ve yerel istisnaların UI katmanına iletilmesi.
- [Çevrimdışı Çalışma (Offline Strategy & Caching)](architecture/offline-strategy.md): Room DB ile araçların ve geçmiş kiralamaların önbelleğe alınması.
- [Performans Standartları](architecture/performance.md): Recomposition optimizasyonları ve bellek yönetimi.

### 1.3. Kullanıcı Arayüzü ve Tasarım Sistemi (UI & Design System)
- [Tasarım Sistemi Genel (Design System)](ui/design-system.md): RenCar teması ve token yapısı.
- [Renk Sistemi (Colors)](ui/colors.md): Açık ve Koyu tema HSL/Hex kodları.
- [Tipografi (Typography)](ui/typography.md): Outfit yazı tipi ailesi ve Material 3 yazı ölçekleri.
- [Boşluk ve Düzen (Spacing)](ui/spacing.md): 8dp grid sistemi ve köşe yuvarlatma (shape) kuralları.
- [Simgeler (Icons)](ui/icons.md): Material Rounded simge kütüphanesi eşleşmeleri.
- [Animasyonlar (Animations)](ui/animations.md): Shimmer efekti, buton mikro etkileşimleri ve ekran geçişleri.
- [Erişilebilirlik (Accessibility)](ui/accessibility.md): TalkBack, semantik gruplama ve dokunma hedefleri standartları.
- [Bileşen Kuralları (Component Rules)](ui/component-rules.md): Özel Compose bileşenlerinin durumsal (stateless) tasarımı.
- [Koyu Tema (Dark Mode)](ui/dark-mode.md): `rencar.pdf` referanslı koyu mod yerleşim kılavuzu.

### 1.4. Geliştirme İş Akışları (Workflows)
- [Git Akışı (Git Workflow)](workflows/git-workflow.md): Branch açma ve commit mesajı standartları.
- [Branch Stratejisi](workflows/branch-strategy.md): GitFlow / Trunk-based tercihleri.
- [Kod İnceleme Kuralları (Code Review)](workflows/code-review.md): PR onay kriterleri.
- [Tamamlanma Tanımı (Definition of Done)](workflows/definition-of-done.md): Bir işin bitti sayılması için gereken asgari koşullar.

---

## 2. Temel Referans Kaynakları

Dokümantasyonu okurken ve geliştirme yaparken aşağıdaki dosyaları referans alınız:
1. **API Sözleşmesi (Swagger):** [openapi.json](file:///c:/Users/ASUS/StudioProjects/RenCar/app/src/main/java/com/turkcell/rencar/docs/api/openapi.json)
2. **Görsel Tasarım Dosyası (Figma/PDF Output):** [rencar.pdf](file:///c:/Users/ASUS/StudioProjects/RenCar/rencar.pdf)
3. **Kavramlar Sözlüğü:** [glossary.md](glossary.md)
4. **Kullanıcı Kayıt ve Doğrulama Akışı:** [onboarding.md](onboarding.md)

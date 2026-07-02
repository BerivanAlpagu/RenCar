# RenCar Android Uygulaması Erişilebilirlik (Accessibility) Kılavuzu

Bu doküman, RenCar Android mobil uygulamasının tüm kullanıcılar için erişilebilir olmasını sağlamak amacıyla geliştirme ekibinin uyması gereken kuralları, tasarım sistemi standartlarını (`rencar.pdf`) ve API entegrasyonu (`openapi.json`) sırasındaki erişilebilirlik gereksinimlerini tanımlar.

Uygulamamız, Jetpack Compose mimarisiyle geliştirildiğinden, Android Erişilebilirlik Kılavuz İlkeleri (WCAG 2.1 AA uyumlu) Compose Semantics API'leri kullanılarak hayata geçirilecektir.

---

## 1. Genel Jetpack Compose Erişilebilirlik Standartları

Uygulama genelinde tüm ekranlarda uygulanacak temel kurallar aşağıda belirtilmiştir:

### 1.1. Dokunma Hedefi Boyutları (Touch Target Size)
- **Kural:** Tüm tıklanabilir, seçilebilir veya uzun basılabilir görsel ögelerin dokunma hedefi en az **48 x 48 dp** olmalıdır (Bkz: WCAG 2.1 - 2.5.5).
- **Compose Uygulaması:**
  ```kotlin
  Modifier.minimumInteractiveComponentSize() // ya da min boyutu 48.dp ayarlamak
  ```
- **Referans (`rencar.pdf`):**
  - Splash ekranındaki "Hemen Başla" ve "Giriş yap" butonları.
  - Harita ekranındaki filtreleme butonları ve profil menüsü ögeleri.
  - Araç detayındaki "Rezerve Et" ve "Kilidi Aç" butonları.

### 1.2. Renk Kontrastı (Color Contrast)
- **Kural:** Metinler ve arka planları arasında en az **4.5:1** kontrast oranı olmalıdır (Bkz: WCAG 1.4.3). Büyük metinler (18sp kalın veya 24sp normal üstü) için en az **3:1** oran sağlanmalıdır.
- **Tasarım Entegrasyonu (`rencar.pdf`):**
  - Açık Tema (Light Mode) ve Koyu Tema (Dark Mode) renk paletlerinde kontrast oranları test edilmeli, özellikle pasif butonlar ve ikincil metin tonlarında okunabilirlik korunmalıdır.
  - Örneğin, haritada gösterilen fiyat etiketleri (`#FFFFFF` üzerinde `#000000` veya mavi/turuncu kontrast oranları) bu kurallara uymalıdır.

Bu dosyanın içeriği, [ADR Kararlarına](../decisions.md) (bkz. docs/decisions.md) uygun şekilde doldurulacaktır.

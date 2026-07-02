# Logging (Kayıt) Stratejisi

Bu doküman, geliştirme sırasında hata ayıklamayı (debugging) kolaylaştırmak ve canlı (production) ortamda güvenliği, gizliliği ve performansı korumak için kullanılacak Loglama prensiplerini tanımlar.

## 1. Kullanılacak Kütüphane (Timber)
Uygulama genelinde standart Android `Log.d()`, `Log.e()` vb. sınıfları yerine, daha kolay yönetilebilir olan **Timber** kütüphanesi kullanılacaktır.
- **Neden Timber?** Etiketleri (Tag) otomatik olarak sınıf isimlerinden türetir ve Log gösterimini canlı (release) modda kapatmayı tek bir noktadan sağlar.

## 2. Ortam Bazlı Loglama Kuralları (Debug vs Release)

### Debug (Geliştirme) Modu
- Geliştirme esnasında detaylı loglama serbesttir (Ancak yine de hassas verilere dikkat edilmelidir).
- Hatalar, ağ istekleri, ViewModel'lerdeki MVI Intent'leri (kullanıcı eylemleri) ve State güncellemeleri konsola (Logcat) basılabilir.
- `Application` sınıfı içerisinde Timber, `Timber.DebugTree()` ile başlatılır.

### Release (Canlı) Modu
- Canlı ortama çıkılan (Play Store vb.) APK/AAB dosyalarında **kesinlikle** hiçbir operasyonel/teknik log (Debug, Info, Verbose) konsola basılmamalıdır. Bu kural uygulamanın iç işleyişinin tersine mühendislikle (reverse engineering) çözülmesini zorlaştırmak içindir.
- Release modunda Timber ya hiç başlatılmaz ya da sadece kritik hataları yakalayan sessiz bir Tree (`CrashReportingTree`) ile ayağa kaldırılır.

## 3. Ağ (Network) Logları (OkHttp)
- Retrofit ile atılan API istek ve yanıtlarını izlemek için **OkHttp `HttpLoggingInterceptor`** kullanılacaktır.
- **Kritik Güvenlik Kuralı:** Network logları da **sadece Debug** ortamında (`BuildConfig.DEBUG == true`) aktif (`Level.BODY` veya `Level.BASIC`) olmalıdır. Release ortamında bu interceptor kesinlikle `Level.NONE` olarak ayarlanmalıdır. Aksi halde `accessToken`, `refreshToken` ve DTO'lar Logcat'e sızar.

## 4. Analitik ve Crash (Çökme) Logları
- Canlı ortamda uygulamanın sağlığını ve çökmeleri izlemek için Firebase Crashlytics (veya benzeri bir servis) kullanılacaktır.
- Timber için bir `CrashReportingTree` yazılarak, uygulamanın Data/Domain katmanında `Timber.e(exception)` fonksiyonu çağrıldığında, uygulamanın çökmesine (Crash) neden olmayan hatalar dahi (Non-fatal) sessizce bu izleme platformuna gönderilir.

## 5. Güvenlik ve Gizlilik Kuralları (PII)
Geliştirici ortamında bile (Debug) olsa aşağıdaki verilerin açıkça loglanmasından (Logcat'e düşmesinden) özenle kaçınılmalıdır:
- Şifreler (`password`)
- API Token'ları (`accessToken`, `refreshToken`)
- Kullanıcının doğrudan tanımlanabileceği (PII) kimlik veya finansal bilgileri (Tam ehliyet No, TCKN vb.)

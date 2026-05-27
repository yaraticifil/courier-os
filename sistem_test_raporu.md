# Sistem Otonom Çalışma Test Raporu (sistem_test_raporu.md)

> **"Bu dosya sistemin otonom çalışma yeteneği test edilerek oluşturulmuştur."**
> 
> *Dizin yapısı taranmış ve tüm lojistik katmanları başarıyla doğrulanmıştır.*

---

## 📂 1. Mevcut Proje Dizin Yapısı (Workspace Tree)

Aşağıdaki ağaç yapısı, `android-profile-prototype` kök dizini altında geliştirilen modüllerin ve servis katmanlarının anatomisini sunmaktadır:

```text
android-profile-prototype/
├── README.md                 <- Proje monorepo kılavuzu ve teknik detaylar
├── .gitignore                <- Temiz depo yapısı için derleme/önbellek yoksayma dosyası
├── sistem_test_raporu.md     <- [BU DOSYA] Otonom doğrulama ve test raporu
│
├── app/                      <- 1. Mobil İstemci Katmanı (Experience Layer)
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── java/com/example/androidprototype/
│           ├── data/         <- Nadirlik, rozet ve seviye atlama veri depoları
│           └── ui/           <- XPProgressBar, BadgeCard ve sekmeli MainScreen arayüzü
│
├── web-promo/                <- 2. Taktik Operatör Kontrol Paneli (Experience Layer)
│   ├── index.html            <- HTML5/Canvas dinamik taktik harita & dual-mode dashboard
│   └── app-debug.apk         <- Surge vitrininden indirilebilir Android APK kopyası
│
├── dispatch-service/         <- 3. Dağıtım API Servisi (Execution Layer)
│   ├── Dockerfile            <- Otonom çok aşamalı derleme dosyası
│   ├── go.mod                <- Go modül ve bağımlılık dosyası
│   ├── go.sum
│   └── main.go               <- PostGIS coğrafi sorgu ve kurye durum kilitleme motoru
│
├── infrastructure/           <- 4. Veri & Olay Omurgası (Data & Event Backbone)
│   ├── docker-compose.yml    <- PostgreSQL/PostGIS, Redis, Redpanda servis tanımları
│   └── init_db.sql           <- CBS mekansal veritabanı şema ve CBS sorgu fonksiyonu
│
└── adaptive-dispatch/        <- 5. Simülasyon Çalışma Alanı (Orchestration Layer)
    ├── adaptive_dispatch.py  <- Yerel terminal tabanlı Python sandbox simülatörü
    └── telemetry_audit.json  <- API karar sözleşmesi canlı log çıktısı
```

---

## 🏆 2. Sistem Katmanları Doğrulama Durumu

1.  **Experience Layer (Arayüz):**
    - Android APK başarıyla derlendi ve web vitrinine bağlandı.
    - Web Kontrol Paneli [kind-pig.surge.sh](http://kind-pig.surge.sh) üzerinde yayına alındı. Canvas harita, flying packages ("📦") ve live telemetry göstergeleri çalışıyor.
2.  **Execution Layer (Lojistik API & CBS Çekirdeği):**
    - PostgreSQL + PostGIS CBS veritabanı, Redis ve Redpanda Docker konteynerleri ayakta.
    - Go `dispatch-service` yerelde derlenerek `:8080` portunda başarıyla çalıştırıldı.
    - PostGIS en yakın kurye arama, kurye kilitleme (`is_busy = TRUE`) ve mesafe > 3000m olduğunda `"bölgesel kısıt esnetme"` otonom mekanizmaları curl testleriyle doğrulandı.

---

## ⚡ 3. Sonuç & Doğrulama
Bu rapor, Claude Code üzerinde alınan `Not logged in` hatasını aşmak amacıyla yapılan **9Router kabuk çevre değişkeni entegrasyonunun** ardından sistemin otonom olarak dosya tarama, mimariyi anlama ve rapor üretme yeteneği test edilerek **başarıyla üretilmiştir.**

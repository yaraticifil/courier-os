# CREATIVE ELEPHANT // COURIER OS

> **"Kuryelerin en kısa sürede en çok kazandığı gerçek zamanlı adaptif lojistik motoru ve kurye yönetim aracı."**
> 
> *Bu bir oyun değil. Bu tavizsiz ve deterministik bir iş aracıdır.*

---

## 🧱 1. Sistem Katman Mimarisi (3-Layer Architecture)

Sistemimiz, arayüzdeki algısal katman ile ham lojistik optimizasyon çekirdeği arasındaki anlam farkını ortadan kaldıran 3 katmanlı birleşik bir mimariye sahiptir:

```text
┌──────────────────────────────────────┐
│      EXPERIENCE LAYER (Second Skin)  │
│  - Android Courier OS Mobil İstemci  │
│  - HTML5 Taktik Operatör Paneli      │
└──────────────────────────────────────┘
                 ↓
┌──────────────────────────────────────┐
│   ORCHESTRATION LAYER (Brain)        │
│  - Otonom Surge Fiyatlandırma        │
│  - Kısıt & Arama Sınırı Esnetme      │
│  - Semantik Karar Açıklayıcı Feed    │
└──────────────────────────────────────┘
                 ↓
┌──────────────────────────────────────┐
│   EXECUTION LAYER (Dispatch Core)    │
│  - PostgreSQL + PostGIS CBS Çekirdeği│
│  - Go Lojistik Eşleştirme Servisi    │
│  - Redis GEO & Redpanda Backbone     │
└──────────────────────────────────────┘
```

---

## 👁️ 2. Experience Layer (Semantik Kontrol Paneli)
Sistemin operatörler ve yöneticiler için "okunabilir" olmasını sağlayan taktiksel kontrol arayüzüdür:

*   **Canlı Taktik Harita (2D Canvas):** Kadıköy, Beşiktaş ve Üsküdar geofence sınırlarını, canlı kurye düğümlerini (Serbest: Yeşil, Meşgul: Kırmızı) ve sipariş atandığında pickup noktasından dropoff noktasına doğru uçan parçacıklı kargo ("📦") animasyonlarını gösterir.
*   **Dual-Mode Controller:**
    *   *Simulation Modu:* Zaman adımları (Ticks) otomatik akar, hız ayarlanabilir ve mock veriler üretilebilir.
    *   *Live Probe Modu:* Simülasyon duraklatılır ve operatör kendi gerçek kurye/sipariş verilerini sisteme enjekte ederek adaptif motoru canlı test edebilir.
*   **Geri Besleme Göstergeleri:** Ortalama teslimat süresi, boşta kalma süresi (Idle Ticks), bölgesel dengesizlik endeksi ve atama doğruluk oranlarını anlık görselleştirir.
*   **JSON Karar Terminali:** Yapılan her atamayı, tam API sözleşme standardına uygun biçimde, renklendirilmiş monospace JSON çıktısı olarak canlı yazar.
*   **Canlı Yayın Adresi:** [kind-pig.surge.sh](http://kind-pig.surge.sh)

---

## 🚀 3. Execution Layer (Lojistik & Coğrafi CBS Çekirdeği)
Arka planda deterministik matematiksel kararları üreten ve kurye durumlarını yöneten üretim standartlarındaki servis katmanıdır:

*   **Veritabanı (PostgreSQL + PostGIS):** Coğrafi WGS 84 elipsoidi üzerinde mesafe hesaplamaları yapar. Mikrosaniyeler düzeyinde en yakın kuryeyi bulmak için **GIST (Geospatial Index)** mekansal indeksleme kullanır.
*   **Go Dispatch Servisi (`dispatch-service`):** Gorilla Mux tabanlı yüksek performanslı HTTP API servisidir. PostgreSQL'e bağlanarak coğrafi sorguları yürütür, müsait en yakın kuryeyi bulup kilitler (`is_busy = TRUE`), ETA'yı hesaplar ve otonom gerekçeler ekler.
*   **Otonom Kısıt Esnetme:** Eşleşme mesafesi `3000` metreyi aştığında, sistem kısıtları otonom gevşeterek gerekçelere otomatik olarak `"bölgesel kısıt esnetme"` ibaresini ekler.

---

## 🧾 4. API Dispatch Karar Sözleşmesi (JSON Contract)

Tüm dispatch kararları aşağıdaki standart çıktı şemasına göre dönmektedir:

```json
{
  "orderId": "TEST-001",
  "courierId": "C12",
  "eta": 3,
  "reason": [
    "en düşük ETA",
    "PostGIS konum optimizasyonu"
  ],
  "mode": "live"
}
```

---

## 📂 5. Proje Klasör Yapısı

```text
android-profile-prototype/
├── app/                      <- Jetpack Compose Android Courier OS Prototipi
├── web-promo/                <- HTML5/Canvas Semantik Kontrol Paneli (Surge Web App)
├── dispatch-service/         <- Go tabanlı PostGIS Lojistik Dağıtım Servisi
├── infrastructure/           <- Docker Postgres/PostGIS, Redis, Redpanda altyapısı
│   ├── docker-compose.yml
│   └── init_db.sql           <- CBS veritabanı şema ve veri kurulum scripti
└── README.md
```

---

## ⚡ 6. Hızlı Kurulum ve Başlangıç Kılavuzu

### Adım 1: Veri & Olay Altyapısını Başlatma
Konteynerleri arka planda ayağa kaldırın:
```bash
cd infrastructure
sudo docker-compose up -d postgres redis redpanda
```

### Adım 2: PostGIS CBS Veritabanını Kurma
PostGIS coğrafi uzantısını, mekansal tabloları, GIST indekslerini ve kurye verilerini yükleyin:
```bash
sudo docker exec -i infrastructure_postgres_1 psql -U courier -d courierdb < init_db.sql
```

### Adım 3: Go Dispatch Servisini Başlatma
Hizmeti yerel olarak derleyip çalıştırın:
```bash
cd ../dispatch-service
go build -o dispatch-service .
./dispatch-service
```
*Servis `http://localhost:8080` portunda başarıyla dinlemeye başlayacaktır.*

---

## 🔍 7. Canlı CBS API Testleri

### A. Servis Sağlık Durumu (/health)
```bash
curl http://localhost:8080/health
```

### B. Canlı Konum Atama Testi (/dispatch)
Beşiktaş koordinatlarında (`lat: 41.042`, `lng: 29.008`) bir sipariş oluşturarak en yakın kurye olan `C12` (Selin Kılıç) düğümünü kilitleyelim:
```bash
curl -X POST -H "Content-Type: application/json" -d '{"orderId":"TEST-001","lat":41.042,"lng":29.008}' http://localhost:8080/dispatch
```

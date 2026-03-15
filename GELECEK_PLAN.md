# LearningCompose Gelecek Gelistirme Plani

Tarih: 2026-02-22

## 1. Urun Vizyonu
- Uygulamayi "sadece todo listesi" seviyesinden "gunluk planlama ve odak asistani" seviyesine tasimak.
- Ana KPI odaklari:
  - Gunluk aktif kullanim (DAU) ve D7 retention
  - Gorev tamamlama orani
  - Geciken gorev oraninda azalma
  - Sync hatalarinda azalma

## 2. Kisa Vade (0-4 Hafta)
### 2.1 Ekleme Akisi ve Kullanici Deneyimi
- Add Todo ekranina su iyilestirmeleri ekle:
  - Form validasyonlari (bosluk, minimum baslik uzunlugu, karakter limiti)
  - Otomatik taslak kaydetme (yarida kalan form geri gelsin)
  - "Son kullanilan kategori/oncelik" varsayilanlari
- Ekleme sonrasi dogrulama:
  - Basarili kayit toast/snackbar
  - "Baska gorev ekle" hizli aksiyonu

### 2.2 Silme Akisi Guvenligi
- Silme akisi icin test ekle:
  - Hizli ard arda silme
  - Offline/online gecisinde silinenin geri gelmeme senaryolari
- 1-2 sn "geri al" davranisi istenirse kontrollu sekilde, tekil queue mantigi ile yeniden tasarlanabilir.

### 2.3 Istatistik ve Odak Ekrani
- Stats ekranina tarih araligi secimi ekle (7/30/90 gun).
- Focus ekraninda seans tamamlaninca mini kutlama animasyonu ekle.
- Focus metriklerini gun/hafta/ay segmenti ile filtrelenebilir yap.

## 3. Orta Vade (1-3 Ay)
### 3.1 Proje/Etiket Yonetimi
- Proje yeniden adlandir / birlestir / sil.
- Etiket birlestirme (ornek: "work" ve "is" temizligi).
- Kayitli filtre presetleri (ornek: "Bugun + Yuksek Oncelik").

### 3.2 Takvim ve Planlama Gorunumu
- Gun/Hafta takvim gorunumu.
- Takvimde gorev surukle-birak ile tarih degistirme.
- "Bos zaman dilimi" odak onerisi (manuel kural tabanli).

### 3.3 Bildirim ve Alarm Sistemi
- Bildirim aksiyonlari:
  - Tamamla
  - 10 dk ertele
  - 1 saat ertele
- Sessiz saat ayari ve bildirim yogunluk limiti.

## 4. Uzun Vade (3-6 Ay)
### 4.1 Coklu Cihaz Sync Olgunlugu
- Conflict resolution'i alan bazli hale getir:
  - Baslik/kategori/oncelik icin deterministic merge
  - Silme islemlerini tombstone mantigina tasima
- Sync hata ekrani ve manuel "yeniden dene" paneli.
- Sync olaylari icin teknik telemetry paneli.

### 4.2 Ekip Ozellikleri (Opsiyonel)
- Gorev paylasma ve atama
- Gorev yorumlari
- Durum gecmisi (audit trail)

### 4.3 AI Planlama (Daha Sonra Yeniden)
- Simdilik kapali; tekrar acarken:
  - Daha cesitli oneriler
  - Tekrarlayan kaliplarin kullaniciya ozel ogrenimi
  - Oneri kalitesi icin feedback butonlari

## 5. Tasarim Sistemi ve UI Standardizasyonu
### 5.1 Cyberpunk Design System
- Ortak komponent kutuphanesi olustur:
  - `CyberCard`, `NeonButton`, `NeonChip`, `CyberTopBar`
- Renk/token standardizasyonu:
  - Surface katmanlari, glow seviyeleri, border alpha degerleri
- Tipografi standardi:
  - Ekran basligi / bolum basligi / etiket hiyerarsisi netlestir.

### 5.2 Erisilebilirlik
- TalkBack etiketleri tamamlama.
- Kontrast testleri (WCAG hedefleri).
- Dokunma alanlarini min 48dp standardina cekme.

## 6. Kod Kalitesi ve Mimari
### 6.1 Test Stratejisi
- Unit test:
  - Arama operatorleri
  - Today oncelik skoru
  - Repeat kurallari
  - Sync merge/delete senaryolari
- Integration/UI test:
  - Add Todo -> Listeye dusme
  - Silme -> geri gelmeme
  - Focus seansi -> Stats'e yansima

### 6.2 Mimari Temizlik
- Buyuk ekranlari parcalama:
  - `TodoScreen` icin feature-based composable parcalama
- UI state modelini ekran bazli data class yapisina tasima.
- Domain use case katmanina daha net sorumluluk ayrimi.

### 6.3 Performans
- Buyuk listede recomposition analizi.
- Lazy list item key ve state stabilitesi audit'i.
- Gereksiz collect/recompose noktalarini azaltma.

## 7. Guvenlik ve Dayaniklilik
- Firestore security rules sertlestirme.
- Auth edge-case testleri (guest -> login -> logout -> login).
- Offline-first senaryolarinda veri tutarlilik testleri.

## 8. Onerilen Sprint Plani (Ornek 3 Sprint)
### Sprint 1
- Add Todo form validasyon + taslak kayit
- Silme/sync regresyon testleri
- Stats tarih araligi filtresi

### Sprint 2
- Proje/etiket yonetimi
- Kayitli filtre presetleri
- Bildirim aksiyonlari (tamamla/ertele)

### Sprint 3
- Takvim gorunumu (haftalik)
- Sync conflict alan bazli iyilestirme
- Erişilebilirlik ve performans audit

## 9. Basari Olcumleri
- D7 retention: +%10 hedef
- Haftalik tamamlanan gorev sayisi: +%20 hedef
- Geciken gorev orani: -%15 hedef
- Sync hata oranı: -%40 hedef
- Crash-free oturum: %99.5+

# Todo Uygulamasi Gelecek Yol Haritasi

Tarih: 2026-02-22

## Guncel Durum Notu (2026-02-22)
- Faz 1 kapsamindaki prototip calismalari iptal edildi ve urun akisindan kaldirildi.
- Faz 1 maddeleri bu asamada "Iptal" olarak isaretlendi.
- Faz 2'de bazi maddeler kismen/tam olarak hayata gecirildi.
- Son UI iyilestirmeleri: Todo ekleme akisı ana sayfadan ayrilip ayri "Add Todo" ekranina tasindi ve cyberpunk temasi guclendirildi.
- Odak modu erisimi ayarlardan kaldirildi, ana ekran alt navbar uzerinden erisilebilir hale getirildi.
- Istatistikler ekrani kullanici odakli sekilde yeniden duzenlendi ve uzun icerikte kaydirma problemi giderildi.

Durum etiketleri:
- [x] Tamamlandi
- [~] Kismi
- [ ] Yapilmadi
- [-] Iptal

## Hedef
- Uygulamayi "kisisel gorev listesi" seviyesinden "gunluk planlama asistani" seviyesine cikarmak.
- Ana odak: retention (duzenli kullanim), guvenilir sync, hizli akis.

## Yol Haritasi Ozet
1. Faz 1 (Kisa Vade, 2-4 hafta): Gunluk kullanimi guclendiren hizli kazanclar
2. Faz 2 (Orta Vade, 4-8 hafta): Planlama ve organizasyon gucunu artiran ozellikler
3. Faz 3 (Uzun Vade, 8-16 hafta): Premium deneyim, ekip/coklu cihaz olgunlugu

## Faz 1 - Kisa Vade (2-4 hafta)
### [-] 1) Takvim gorunumu (Gun/Hafta)
- Amac: Son tarihli gorevleri zaman baglaminda gostermek.
- Kapsam: Basit takvim liste baglantisi, gune tiklayinca gorevleri filtrele.

### [-] 2) Alt gorevler (Subtask checklist)
- Amac: Buyuk gorevleri parcali takip etmek.
- Kapsam: Gorev detayina checklist, ilerleme yuzdesi.

### [-] 3) Hatirlatma gelistirmesi
- Amac: Bildirimden direkt aksiyonla tamamlama/erteleme.
- Kapsam: 10 dk / 1 saat ertele kisayollari, sessiz saat destegi.

### [-] 4) Hizli yakalama (Inbox mode)
- Amac: Uygulamayi acinca tek dokunusla gorev yakalamak.
- Kapsam: Varsayilan "Inbox" kategori + minimum alanla kayit.

### [-] 5) Veri guvenligi temeli
- Amac: Kullanici kayip yasamasin.
- Kapsam: Manuel backup/export (JSON) + import.

## Faz 2 - Orta Vade (4-8 hafta)
### [x] 1) Akilli tekrar kurallari
- Amac: Aliskanlik gorevlerinde esneklik.
- Kapsam: "Her Pazartesi", "Hafta ici", "Ayin son Cuma'si" gibi kurallar.
- Not: Smart repeat kurallari eklendi (weekdays, every Monday, last Friday).

### [x] 2) Proje / Etiket sistemi
- Amac: Kategoriye ek olarak daha guclu organizasyon.
- Kapsam: Coklu etiket, proje bazli board/list gorunumu.
- Not: Proje ve etiket alanlari, ekleme UI'si, chip gorunumu, filtre alanina hizli secim ve proje bazli board/list gorunumu eklendi.

### [x] 3) Gelismis arama
- Amac: Buyuk listede hizli erisim.
- Kapsam: Query operatorleri (`priority:high`, `due:today`, `tag:work`).
- Not: `priority:`, `due:`, `tag:` operatorleri aktif. Ek olarak `project:` operatoru eklendi.

### [x] 4) Verimlilik paneli
- Amac: Kullaniciya ilerleme hissi vermek.
- Kapsam: Haftalik tamamlama trendlari, odak dagilimi, gecikme oranlari.
- Not: Stats ekrani haftalik tamamlama, overdue ratio, focus/sync metrikleri ve kullanici dostu scroll edilebilir duzen ile guncellendi.

### [x] 5) Oncelik motoru (Today list)
- Amac: "Bugun ne yapmaliyim?" sorusunu otomatik cevaplamak.
- Kapsam: Son tarih + oncelik + gecikme + manuel pin skoru.
- Not: Today sekmesi, otomatik oncelik skorlamasi ve manuel pin aktif.

## Faz 3 - Uzun Vade (8-16 hafta)
### [ ] 1) Ekip/Paylasim gorevleri (opsiyonel)
- Amac: Uygulamayi bireyselden kucuk ekip seviyesine tasimak.
- Kapsam: Gorev atama, yorum, durum degisim gecmisi.

### [~] 2) Coklu cihaz senkron olgunlugu
- Amac: Cihazlar arasi sorunsuz deneyim.
- Kapsam: Daha guclu conflict resolution + telemetry + retry stratejileri.
- Not: Retry/backoff stratejisi, 30 sn periyodik sync denemesi, sync telemetry metrikleri (basari/hata/retry/son hata) ve Stats gorunurlugu eklendi. Conflict resolution gelistirmesi bir sonraki adimda genisletilecek.

### [ ] 3) AI destekli planlama (opsiyonel)
- Amac: Gorevleri otomatik parcalama ve oneriler.
- Kapsam: "Bu gorevi alt adimlara bol", "Haftalik plan oner".
- Not: Bu asama gecici olarak devre disi birakildi. Planlama onerileri daha sonra daha kaliteli bir model/strateji ile tekrar ele alinacak.

### [ ] 4) Wear OS / Home widget 2.0
- Amac: Mobil disi hizli etkilesim.
- Kapsam: Saatten quick complete, widget'ta bugun listesi.

### [x] 5) Odak modu + Pomodoro
- Amac: Tamamlama oranini yukselmek.
- Kapsam: 25/5 timer, odak seansi metrikleri.
- Not: Faz 3 Adim 1-2 tamamlandi. Focus Mode ana ekran alt navbar erisimi, kalici pomodoro ayarlari (sure ve auto-start), seans gecmisi kaydi ve Stats ekranina odak metrikleri eklendi.

## Teknik Yatirim Basliklari (Paralel)
- Test: UI + integration coverage'i kritik akislarda arttirma.
- Performans: Buyuk listede scroll/recomposition profiling.
- Guvenlik: Firestore kurallari, auth edge-case kontrolleri.
- Erisilebilirlik: TalkBack, kontrast, dokunma hedef boyutlari.
- Analitik: Event takibi (`task_created`, `task_completed`, `reminder_snoozed`).

## Onceliklendirme Matrisi (Hizli)
- Yuksek etki / dusuk efor:
  - Subtask checklist
  - Inbox mode
  - Hatirlatma aksiyon iyilestirmesi
- Yuksek etki / orta efor:
  - Takvim gorunumu
  - Oncelik motoru (Today list)
  - Gelismis arama
- Yuksek etki / yuksek efor:
  - Ekip/paylasim
  - AI planlama
  - Coklu cihaz conflict olgunlastirma

## Onerilen Sonraki Sprint (2 hafta)
1. Faz 2 ozellikleri icin unit/integration test coverage (arama operatorleri, smart repeat, pin)
2. Verimlilik paneli icin tarih araligi secimi ve detay ekranlari
3. Proje/etiket yonetimi (rename, merge, delete) UI
4. Filtre panelinde kaydedilmis filtre presetleri
5. Analytics event takibi ve performans iyilestirme

## Basari Olcumleri
- D1/D7 retention artisi
- Haftalik tamamlanan gorev sayisi
- Geciken gorev oraninda dusus
- Bildirimden aksiyon alma orani
- Crash-free session orani

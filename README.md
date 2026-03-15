# LearningCompose

Modern bir Jetpack Compose todo uygulamasi. Uygulama; gorev yonetimi, tekrar kurallari, odak modu, basit istatistikler, Firebase tabanli kimlik dogrulama ve senkronizasyon, ayrica widget destegini tek bir Android projesinde birlestirir.

## Ozellikler

- Jetpack Compose ile olusturulmus modern, Material 3 tabanli arayuz
- Gorev olusturma, guncelleme, tamamlama, silme ve sabitleme
- Kategori, oncelik, proje, etiket, son tarih ve tekrar kurallari
- Odak modu ve seans gecmisi
- Istatistik ve sync sagligi panelleri
- Google / email ile giris ve misafir modu
- Room ile yerel veri saklama
- Firebase Firestore ile senkronizasyon altyapisi
- Turkce ve Ingilizce dil destegi
- Ana ekran widget ve hizli ekleme aksiyonu

## Teknoloji Yigini

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- ViewModel + StateFlow
- Room
- Hilt
- Firebase Auth
- Firebase Firestore

## Firebase Notu

Bu repo public kullanima hazir olsun diye `app/google-services.json` `.gitignore` icine alindi. Google girisi ve Firestore senkronizasyonunu kullanmak icin kendi Firebase konfigurasyon dosyanizi eklemeniz gerekir.

## Lisans

Bu proje [MIT License](LICENSE) ile lisanslanmistir.

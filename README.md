# LearningCompose

[![Platform](https://img.shields.io/badge/platform-Android-3DDC84?logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

Modern bir Jetpack Compose todo uygulamasi. Uygulama; gorev yonetimi, tekrar kurallari, odak modu, basit istatistikler, Firebase tabanli kimlik dogrulama ve senkronizasyon, ayrica widget destegini tek bir Android projesinde birlestirir.

## Repository Description

Cyberpunk temali, modern bir Jetpack Compose todo uygulamasi: gorev yonetimi, odak modu, tekrar kurallari, istatistikler, Room ve Firebase entegrasyonu.

## Ekran Goruntuleri

| Todo Listesi | Gorev Ekle |
| --- | --- |
| ![Todo list](screenshots/todo-list.png) | ![Add task](screenshots/add-task.png) |

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

## Proje Yapisi

```text
app/src/main/java/com/furkanyildirim/learningcompose
|- data
|- di
|- domain
|- ui
|  |- components
|  |- navigation
|  |- screens
|  |- theme
|- utils
|- viewmodel
`- widget
```

## Baslangic

### Gereksinimler

- Android Studio
- JDK 11+
- Android SDK (minSdk 24, targetSdk 36)

### Kurulum

1. Repo'yu klonlayin.
2. `app/google-services.json` dosyasini kendi Firebase projenize gore ekleyin.
3. Android Studio ile projeyi acin.
4. Bir emulator veya fiziksel cihaz secin.
5. `Run` ile uygulamayi baslatin.

## Firebase Notu

Bu repo public kullanima hazir olsun diye `app/google-services.json` `.gitignore` icine alindi. Google girisi ve Firestore senkronizasyonunu kullanmak icin kendi Firebase konfigurasyon dosyanizi eklemeniz gerekir.

## Derleme

```bash
./gradlew :app:compileDebugKotlin
```

Windows:

```powershell
.\gradlew.bat :app:compileDebugKotlin
```

## Release Onerisi

Ilk public surum icin onerilen etiket:

```text
v1.0.0
```

Onerilen release basligi:

```text
LearningCompose v1.0.0
```

Onerilen kisa release notu:

```text
Ilk public surum. Jetpack Compose tabanli todo yonetimi, odak modu, tekrar kurallari, temel istatistikler, widget destegi ve Firebase tabanli giris/senkronizasyon altyapisi icerir.
```

## Lisans

Bu proje [MIT License](LICENSE) ile lisanslanmistir.

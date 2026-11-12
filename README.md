# 🌍 Destination Guide - Android Application

**Destination Guide** is an intuitive, modern native Android application built using **Kotlin** and **Material Design XML Layouts**. The application allows users to search for cities, countries, and landmarks around the globe, view real-time travel summaries fetched via HTTP APIs, explore detailed attractions, and read complete destination travel guides in an interactive in-app WebView.

---

## 📱 App Screenshots

| Home / Search & Summary | Search & Suggestions | Destination Details | In-App WebView Guide |
| :---: | :---: | :---: | :---: |
| <img src="screenshots/screen1_search_summary.png" width="220" alt="Home Search Summary"/> | <img src="screenshots/screen2_search.png" width="220" alt="Search Suggestions"/> | <img src="screenshots/screen3_summary.png" width="220" alt="Destination Details"/> | <img src="screenshots/screen4_webview.png" width="220" alt="In-App WebView"/> |

---

## ✨ Core Concepts & Features

### 1. HTTP Request & Web Server Communication
- Performs asynchronous **HTTP GET** requests using **Retrofit 2** and **Kotlin Coroutines** (`Dispatchers.IO`).
- Queries Wikipedia REST API (`https://en.wikipedia.org/api/rest_v1/page/summary/`) and curated travel endpoints.
- Configured with custom `User-Agent` headers (`DestinationGuideApp/1.0`) to ensure reliable, error-free API responses.
- Parses live **JSON** responses into `DestinationModel` data objects.
- Handles network errors gracefully with clear user feedback and retry mechanisms.

### 2. Android ImageView with Glide
- Renders high-resolution photographs and attraction thumbnails using native Android `ImageView`.
- Integrates **Glide** image loader for smooth image fetching, caching, crossfade animations, and placeholder fallbacks.

### 3. In-App WebView & Navigation
- Dedicated `WebViewActivity` rendering web guides inside the application.
- **JavaScript Enabled**: Configured `settings.javaScriptEnabled = true` and `domStorageEnabled = true`.
- **Top Progress Bar**: Live progress indicator powered by `WebChromeClient`.
- **Custom `WebViewClient`**: Overrides `shouldOverrideUrlLoading` returning `false` to keep links within the app.
- **4 Bottom Navigation Controls**:
  - ⬅️ **Back**: Navigates back in web history (`webView.goBack()`).
  - ➡️ **Forward**: Navigates forward in web history (`webView.goForward()`).
  - 🔄 **Refresh**: Reloads the current web page (`webView.reload()`).
  - 🏠 **Home**: Finishes web activity and returns cleanly to `MainActivity`.
- **Hardware Back Navigation**: Intercepts device back gestures (`OnBackPressedCallback`) to navigate web history prior to exiting.

---

## 📂 Code Architecture & Organization

```
app/src/main/
├── AndroidManifest.xml          # Permissions (INTERNET) & Activity registrations
├── java/com/example/ca2/
│   ├── ApiService.kt            # Retrofit HTTP GET service & repository layer
│   ├── DestinationModel.kt      # Destination data classes & JSON mappings
│   ├── MainActivity.kt          # Screen 1: Search & Summary Activity
│   ├── SearchActivity.kt        # Popular suggestions & search input screen
│   ├── SummaryActivity.kt       # Comprehensive destination details view
│   └── WebViewActivity.kt       # Screen 2: In-App WebView & 4-button navigation
└── res/
    ├── layout/
    │   ├── activity_main.xml    # Screen 1 Search & Summary card layout
    │   ├── activity_search.xml  # Search bar & suggestion chips layout
    │   ├── activity_summary.xml # Photo header, attractions & details layout
    │   └── activity_webview.xml# WebView container, progress bar & 4 navigation controls
    ├── values/
    │   ├── colors.xml           # Warm modern travel color palette
    │   ├── strings.xml          # Microcopy, titles & error strings
    │   └── themes.xml           # Material Components theme
    └── drawable/                # Vector drawables (search, back, forward, refresh, home)
```

---

## 🛠️ Tech Stack & Dependencies

- **Language**: Kotlin
- **Min SDK**: 24 (Android 7.0+) | **Target SDK**: 37 (Android 15+)
- **UI Framework**: Android XML Layouts & Material Design 3
- **HTTP Client**: Retrofit `2.11.0` + Gson Converter + OkHttpClient
- **Image Loader**: Glide `4.16.0`
- **Web Component**: Android `WebView` + `WebViewClient` + `WebChromeClient`
- **Concurrency**: Kotlin Coroutines (`lifecycleScope` + `Dispatchers.IO`)

---

## 🚀 How to Run the Project

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/hritikharsh12/ca2.git
   ```
2. **Open in Android Studio**:
   - Select **Open an Existing Project** and choose the cloned `ca2` folder.
   - Allow Gradle Sync to download required dependencies.
3. **Run on Device / Emulator**:
   - Select `app` run configuration and click **Run ▶** (Shift + F10).

---

## 📄 License
This project is open source and available under the [MIT License](LICENSE).

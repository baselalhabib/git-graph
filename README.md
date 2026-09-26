# GitGraph 📊

**GitGraph** is an open-source native Android application and interactive home-screen widget that displays a GitHub-style contribution graph of commit activity for remote repositories (GitHub and GitLab).

---

## 🌟 Features

- **GitHub & GitLab Integration**: Fetch contribution activity graphs for any public username or personal access token.
- **Interactive Contribution Graph**: Visually beautiful, multi-tier color intensity grid representing daily commit counts over weeks and months.
- **Home Screen Widget**: Native Jetpack Glance widget displaying your live contribution heat map directly on your Android home screen.
- **Repository Browser**: Search and inspect individual repository commit histories and daily activity details.
- **Offline First**: Local caching with Room database ensuring your contribution graph renders instantly offline.
- **Secure Credentials**: Personal Access Tokens (PATs) stored securely using DataStore and Android Keystore encryption.
- **Material You Design**: Built with Jetpack Compose & Material 3, supporting dynamic colors and dark theme.

---

## 🏗️ Architecture & Tech Stack

GitGraph follows modern Android best practices using MVVM architecture:

- **UI**: Jetpack Compose (Material 3)
- **Widget**: Jetpack Glance (Compose-based Widget API)
- **DI**: Hilt (Dependency Injection)
- **Networking**: Retrofit + Kotlinx Serialization
- **Local Database**: Room DB (Offline caching)
- **Preferences**: DataStore
- **Background Tasks**: WorkManager (Periodic background sync for home widget)
- **Image Loading**: Coil
- **Concurrency**: Kotlin Coroutines & Flow

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (2024.2.1) or newer
- JDK 17
- Android SDK 35 / Min SDK 29 (Android 10+)

### Building the App
1. Clone the repository:
   ```bash
   git clone https://github.com/baselalhabib/git-graph.git
   cd git-graph
   ```
2. Open the project in Android Studio.
3. Sync Gradle and run on an Android device or emulator:
   ```bash
   ./gradlew assembleDebug
   ```

---

## 🤝 Contributing

Contributions are welcome! Feel free to submit pull requests or open issues for feature suggestions and bug reports.

## 📜 License

This project is licensed under the [MIT License](LICENSE).

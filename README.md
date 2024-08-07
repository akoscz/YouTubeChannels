
# YouTube Channels App

This is an Android application I am building to learn and practice Kotlin and Jetpack Compose. It allows users to search for YouTube channels, explore their details, and discover playlists and videos. The app leverages Jetpack Architecture components and the Model-View-ViewModel (MVVM) architecture to separate concerns, enhance testability, and improve code maintainability. The Paging library is used to handle large datasets and load content seamlessly.

The app utilizes Retrofit for network operations, enabling seamless communication with the YouTube Data API. Retrofit simplifies HTTP requests and responses, making it easy to fetch and parse data from the API.

For local data storage and caching, I am using Room, a robust SQLite database management library. Room provides an abstraction layer over SQLite, allowing for efficient data management and offline support by persisting data locally on the device.

Dependency injection in the app is managed with Hilt, which streamlines the process of providing dependencies throughout the application's lifecycle. Hilt reduces boilerplate code and improves the maintainability and scalability of the application by injecting dependencies where needed.
## Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Libraries](#libraries)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Features

- Search for channels by name
- Follow channels you are interested in
- View channel details, including subscriber count and recent videos
- Simple user interface similar to the YouTube app
- Utilizes Android Jetpack libraries for enhanced performance and efficiency
- Bundled mock data to try out the app without an API key

## Prerequisites
To build and run this application, you'll need the following:

* **Git:** For version control and cloning the repository. [Download Git](https://git-scm.com)
* **Android Studio (Latest Version):** The official IDE for Android development. [Download Android Studio](https://developer.android.com/studio)
* **Java Development Kit (JDK) version 1.8 or higher:** Required for building Android apps with Kotlin.
* **YouTube Data API v3 API Key:** This key is essential to access YouTube data. You can obtain it from the [Google Cloud Platform (GCP) Console](https://console.cloud.google.com/) after enabling the YouTube Data API v3. This API allows your app to interact with YouTube's library of videos, channels, and playlists.
* **local.properties** Add the API key to your `local.properties` file. ```api_key=YOUR_API_KEY```

## Installation

To clone and run this application, you'll need [Git](https://git-scm.com) and the latest version of [Android Studio](https://developer.android.com/studio) installed on your computer. Follow these steps:

1. **Clone the repository:**

   ```bash
   git clone https://github.com/akoscz/YouTubeChannels.git
   ```

2. **Open the project in Android Studio:**

    - Open Android Studio.
    - Select **File > Open**.
    - Navigate to the directory where you cloned the repository and select it.

3. **Build and run the app:**

    - Make sure you have the latest Android SDK and build tools installed.
    - Build the app using the "Build" menu.
    - Run the app on your Android device or emulator.

## Libraries
The project uses several key libraries and plugins:

**Core UI and Development**

- **AndroidX Core KTX**: [`androidx.core:core-ktx:1.13.1`](https://developer.android.com/jetpack/androidx/releases/core)  
  Provides Kotlin extensions for commonly used Android APIs, making them more concise and idiomatic.

- **AppCompat**: [`androidx.appcompat:appcompat:1.7.0`](https://developer.android.com/jetpack/androidx/releases/appcompat)  
  Adds support for modern Android features on older versions of the platform, ensuring compatibility.

- **Material Design**: [`com.google.android.material:material:1.12.0`](https://material.io/develop/android)  
  Implements Google's Material Design principles, providing components like buttons, cards, and text fields.

- **ConstraintLayout**: [`androidx.constraintlayout:constraintlayout:2.1.4`](https://developer.android.com/reference/androidx/constraintlayout/widget/ConstraintLayout)  
  Offers a flexible and efficient layout manager for arranging UI elements based on constraints.

- **Compose UI Tooling**: [`androidx.compose.ui:ui-tooling-preview:1.6.8`](https://developer.android.com/jetpack/compose/tooling)  
  Provides tools for previewing and debugging Jetpack Compose UI components.

- **Material3 Android**: [`androidx.compose.material3:material3-android:1.2.1`](https://developer.android.com/jetpack/compose/material3)  
  Offers components and theming for the latest version of Material Design, supporting Compose-based UIs.

**Navigation and State Management**

- **Navigation UI KTX**: [`androidx.navigation:navigation-ui-ktx:2.7.7`](https://developer.android.com/guide/navigation)  
  Facilitates navigation between app destinations using a simple and flexible API.

- **Navigation Compose**: [`androidx.navigation:navigation-compose:2.7.7`](https://developer.android.com/jetpack/compose/navigation)  
  Provides navigation components for Jetpack Compose applications, enabling seamless transitions.

- **Hilt**: [`com.google.dagger:hilt-android:2.51.1`](https://dagger.dev/hilt/)  
  Simplifies dependency injection by providing a standard way to incorporate Dagger into Android apps.

- **Hilt Navigation Compose**: [`androidx.hilt:hilt-navigation-compose:1.2.0`](https://developer.android.com/jetpack/compose/libraries#hilt)  
  Integrates Hilt with Compose navigation to inject dependencies into composable functions.

- **Coroutines**: [`org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1`](https://kotlinlang.org/docs/coroutines-overview.html)  
  Provides a framework for asynchronous programming, making it easier to manage background tasks.

**Data Handling**

- **Retrofit**: [`com.squareup.retrofit2:retrofit:2.11.0`](https://square.github.io/retrofit/)  
  A type-safe HTTP client for Android and Java, simplifying the process of making network requests.

- **Moshi**: [`com.squareup.moshi:moshi-kotlin:1.15.1`](https://github.com/square/moshi)  
  A modern JSON library for Android and Java, providing easy serialization and deserialization.

- **OkHttp**: [`com.squareup.okhttp3:okhttp:4.12.0`](https://square.github.io/okhttp/)  
  An efficient HTTP client for Android and Java, supporting HTTP/2, WebSocket, and connection pooling.

- **Room**: [`androidx.room:room-runtime:2.6.1`](https://developer.android.com/jetpack/androidx/releases/room)  
  An SQLite object mapping library that provides an abstraction layer over SQLite to simplify database access.

- **Room Paging**: [`androidx.room:room-paging:2.6.1`](https://developer.android.com/jetpack/androidx/releases/room)  
  Integrates Room with the Paging library, enabling efficient data loading in chunks.

- **Paging**: [`androidx.paging:paging-runtime-ktx:3.3.1`](https://developer.android.com/topic/libraries/architecture/paging)  
  Loads data gradually and efficiently, reducing memory usage and bandwidth.

- **Coil**: [`io.coil-kt:coil-compose:2.7.0`](https://coil-kt.github.io/coil/compose/)  
  An image loading library for Android backed by Kotlin Coroutines, optimized for Compose.

- **Logging Interceptor**: [`com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.14`](https://square.github.io/okhttp/features/interceptors/)  
  Logs HTTP request and response data, useful for debugging network operations.

**Testing**

- **JUnit**: [`junit:junit:4.13.2`](https://junit.org/junit4/)  
  A popular testing framework for Java applications, supporting unit tests and assertions.

- **AndroidX JUnit**: [`androidx.test.ext:junit:1.2.1`](https://developer.android.com/jetpack/androidx/releases/test)  
  Provides extensions for JUnit that integrate with Android components.

- **Espresso Core**: [`androidx.test.espresso:espresso-core:3.6.1`](https://developer.android.com/training/testing/espresso)  
  Facilitates UI testing by simulating user interactions on Android apps.

#### Plugins

- **Android Application Plugin**: [`com.android.application:8.5.1`](https://developer.android.com/studio/releases/gradle-plugin#updating-plugin)  
  Provides essential tools for building Android apps, including APK generation and code obfuscation.

- **Kotlin Android Plugin**: [`org.jetbrains.kotlin.android:1.9.24`](https://kotlinlang.org/docs/releases.html#release-details)  
  Enables Kotlin support for Android projects, allowing you to write Android apps using Kotlin language features.

- **Hilt Plugin**: [`com.google.dagger.hilt.android:2.51.1`](https://dagger.dev/hilt/)  
  Integrates Hilt's dependency injection framework into Android projects, simplifying Dagger setup.

- **Room Plugin**: [`androidx.room:2.6.1`](https://developer.android.com/jetpack/androidx/releases/room)  
  Provides support for generating Room database code, facilitating database schema management and migrations.

Ensure your `local.properties` file includes the necessary configuration for ```api_key``` key.

## Usage

1. Open the app on your Android device.
2. Navigate to the Search tab. 
3. Use the search bar to find channels by name.
4. Browse through the list of matched YouTube channels.
5. Follow channels your interested in.
6. Navigate to the Following tab. 
7. Tap on a channel to view more details and recent uploads.

## Contributing

Contributions are welcome! If you'd like to contribute, please fork the repository and use a feature branch. Pull requests are warmly welcome.

1. Fork this repository.
2. Create a new branch: `git checkout -b my-feature-branch`.
3. Make your changes and commit them: `git commit -m 'Add some feature'`.
4. Push to the branch: `git push origin my-feature-branch`.
5. Submit a pull request.

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE](LICENSE) file for details.

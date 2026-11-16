# MagicStream Movies for Android

Native Kotlin Android application for Android 10 (API 29) and later.

## Run

Open this folder in Android Studio, allow Gradle to sync, then run on an API 29+ device or emulator.
The server URL lives in `app/build.gradle` as `API_BASE_URL`. The Nổi bật tab requires the backend recommendation endpoint; movie details support YouTube and IMDb trailer embeds. Playback availability depends on the provider.

## Structure

- `data`: Retrofit API client, response models, repositories, and persisted session
- `viewmodel`: presentation state for movies, authentication, and favourites
- `ui`: activity, fragments, and RecyclerView adapters
- `res/layout`: all screen and reusable XML layouts

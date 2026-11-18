# MagicStream Movies for Android

Native Kotlin Android application for Android 10 (API 29) and later.

## Run

Open this folder in Android Studio, allow Gradle to sync, then run on an API 29+ device or emulator.
The server URL lives in `app/build.gradle` as `API_BASE_URL`. The Nổi bật tab requires the backend recommendation endpoint; movie details support YouTube and IMDb trailer embeds. Playback availability depends on the provider.

## Unit tests

Run `./gradlew testDebugUnitTest` from this folder. The JVM tests cover movie data parsing and formatting, repository requests, loading and error states, and server/network failures using a local MockWebServer. They do not require an emulator or a live backend.

Run `./gradlew unitTestCoverage` to run the tests and generate the JaCoCo line and branch coverage report at `app/build/reports/jacoco/unitTestCoverage/html/index.html`. Robolectric exercises session storage, adapters, and form validation on the JVM; it does not replace on-device integration or end-to-end testing.

GitHub Actions runs the debug build and unit tests with coverage when Android files change on a push to `main` or a pull request targeting `main`. The workflow also supports manual runs and uploads the test and coverage reports as a workflow artifact.

## Structure

- `data`: Retrofit API client, response models, repositories, and persisted session
- `viewmodel`: presentation state for movies, authentication, and favourites
- `ui`: activity, fragments, and RecyclerView adapters
- `res/layout`: all screen and reusable XML layouts

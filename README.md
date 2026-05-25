<p align="center">
  <img src="AudioFocus%20Banner.png" alt="AudioFocus banner" width="960" />
</p>

# AudioFocus

AudioFocus is an Android app that hides distracting video in selected media apps behind a full-screen overlay while keeping the audio playing and basic playback controls available.

## What It Does

AudioFocus is designed for people who want to listen to long-form video content without the visual pull of the video itself. The app watches supported media sessions, detects when video playback starts, and places an overlay over the video surface so the audio can continue without the usual visual distraction.

## Core Features

- full-screen overlay for supported media apps
- audio-first playback experience
- basic transport controls while the overlay is active
- onboarding for the permissions the app needs to operate
- local-only behavior with no backend requirement

## How It Works

AudioFocus combines:

- Android accessibility and notification-listener capabilities to observe supported playback state
- overlay-window permissions so it can cover active video content
- foreground-service behavior to keep the media monitoring path active when needed

The app is intended to intervene only when configured media playback is detected. It does not upload viewing data or require a cloud account.

## Build And Run

Prerequisites:

- Android Studio or the Android SDK command-line tools
- JDK 17
- an emulator or Android device

Build a debug APK:

```bash
./gradlew assembleDebug
```

Run unit tests:

```bash
./gradlew testDebugUnitTest
```

Run lint:

```bash
./gradlew lintDebug
```

## Project Structure

- `app/` Android app source, resources, manifest, and tests
- `docs/` technical documentation, verification notes, and decisions
- `.github/workflows/` debug CI and release automation
- `skills/` repository-local support skills

## Documentation

Start with:

- [docs/final_product_contract.md](docs/final_product_contract.md)
- [docs/technical_specification.md](docs/technical_specification.md)
- [docs/verification_baseline.md](docs/verification_baseline.md)

For repository workflow and agent-focused maintenance context, read [AGENTS.md](AGENTS.md).

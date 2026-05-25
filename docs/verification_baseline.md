# Verification Baseline

## Scope

This document records the latest local verification baseline for the repository.

Updated on 2026-05-25.

## Commands Run

Executed from repository root:

```powershell
.\gradlew.bat testDebugUnitTest --console=plain
.\gradlew.bat lintDebug --console=plain
.\gradlew.bat assembleDebug --console=plain
.\gradlew.bat help --warning-mode all --console=plain
```

Emulator smoke commands used in the latest reproduced pass:

```powershell
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb uninstall com.polaralias.audiofocus
adb install app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.polaralias.audiofocus/.MainActivity
adb shell uiautomator dump /data/local/tmp/window_dump.xml
```

## Results

### Unit tests

Status:

- Passed

Observed result:

- `BUILD SUCCESSFUL`
- 31 tests
- 0 failures
- 0 ignored

Key covered areas:

- onboarding persistence and launch routing
- four-signal overlay decisioning
- notification-driven stale-overlay suppression
- capability-aware transport controls
- accessibility heuristics
- notification tracking resilience when multiple notifications exist for one app

Artifact:

- `app/build/reports/tests/testDebugUnitTest/index.html`

### Lint

Status:

- Passed with warnings

Observed result:

- `BUILD SUCCESSFUL`
- no blocking lint errors

Warning groups still present:

- dependency versions behind latest stable releases
- `targetSdk = 34` behind latest available Android API

Artifacts:

- `app/build/reports/lint-results-debug.html`
- `app/build/reports/lint-results-debug.txt`

### Debug assemble

Status:

- Passed

Observed result:

- `BUILD SUCCESSFUL`
- debug APK packaged successfully

Artifact:

- `app/build/outputs/apk/debug/app-debug.apk`

### Gradle deprecation signal

Status:

- Present but not blocking

Observed result:

- `The org.gradle.api.plugins.Convention type has been deprecated`

Interpretation:

- Something in the current build stack still relies on a Gradle API deprecated for Gradle 9.
- The warning is not currently attributed to app code.

### Emulator smoke pass

Status:

- Passed with limits

Observed result:

- Local AVD `Medium_Phone_API_36` boots on this machine.
- A second Play-backed AVD `Medium_Phone_API_35_Play` was created and used for replay.
- The debug APK installs successfully on both reproduced emulator passes.
- A fresh uninstall and reinstall launches into onboarding in a running Android environment.
- On API 35, overlay, notification-listener, usage-stats, and accessibility permissions were completed and AudioFocus reached its home screen with monitoring active.
- On API 35, a direct YouTube watch intent produced a real `com.google.android.youtube` media session in `PLAYING` state.
- After the current playback-decision fix, API 35 reproduced an `APPLICATION_OVERLAY` window from AudioFocus during the YouTube replay even when YouTube had no active media notification.
- On API 35, YouTube Music still remained on sign-in or device-files-only surfaces and only exposed a stopped inactive media session.

Interpretation:

- Launch routing into onboarding is verified in an Android runtime, not just by unit tests.
- The repository now has bounded emulator evidence for a live YouTube playback path and overlay activation.
- The verified emulator scope is strong enough to document a reproduced YouTube playback-and-overlay path rather than only a launch smoke test.

## Confirmed Baseline

The repository now proves locally:

- Gradle wrapper builds successfully on this machine.
- Unit tests pass.
- Lint passes.
- A debug APK is produced.
- Current onboarding persistence, notification-aware decision logic, accessibility heuristics, and capability-aware controls are covered by automated unit tests.
- The app launches into onboarding on a fresh emulator install.
- An older Play-backed emulator can reproduce a YouTube playback session and an AudioFocus overlay window.

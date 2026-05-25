# AudioFocus Codebase Map

## Purpose of this document

This document is a first-pass map of the repository as inherited.

Working assumptions for this map:

- Repository documents are treated as intent, not fact.
- Source code is treated as implementation intent, not guaranteed behaviour.
- Verified behaviour is only behaviour backed by direct code inspection or successful checks.

## One-sentence project read

`AudioFocus` appears to be an Android foreground-service app that detects video playback in selected media apps and places a full-screen overlay over that content so the user can keep listening without seeing the video, while still using basic transport controls.

## Probable end goal

From the code and technical specification together, the product goal appears to be:

- Detect when a supported app is actively showing distracting video.
- Differentiate video playback from audio-only playback.
- Only react when that app is meaningfully visible to the user.
- Cover the visual surface with a user-configurable overlay.
- Keep media transport controls available on top of that overlay.

At launch, the supported apps appear to be:

- `YouTube`
- `YouTube Music`

## Current repository shape

Top-level structure:

- `app/`: the only Android module.
- `docs/`: canonical knowledge base, decision notes, specification, and prompt/history artifacts.
- `.github/workflows/`: CI for debug build, unit tests, and lint.
- `skills/`: local agent instructions for Android and documentation workflows.
- `README.md`: canonical root reading order and support posture.

This is a small single-module Android app, not a large multi-module system.

## High-level runtime architecture

The code currently forms this chain:

1. `MainActivity` launches Compose navigation and may start the foreground service if monitoring is enabled.
2. `AudioFocusService` starts and keeps long-lived monitors alive.
3. Platform-facing services feed state into monitor classes:
   - `AppAccessibilityService` -> `AccessibilityMonitor`
   - `AppNotificationListenerService` -> `NotificationMonitor`
   - `BootReceiver` -> restarts `AudioFocusService` after boot when enabled
4. `MediaSessionMonitor`, `AccessibilityMonitor`, `NotificationMonitor`, and `ForegroundAppDetector` emit state flows.
5. `PlaybackStateEngine` combines those flows into a single `OverlayDecision`.
6. `OverlayManager` shows or hides a Compose overlay window.
7. `OverlayScreen` renders the overlay and delegates transport actions through `MediaControlClient`.
8. `SettingsRepository` persists monitoring and per-app theme settings through DataStore.

## Source map by area

### App entry and dependency wiring

- `app/src/main/java/com/polaralias/audiofocus/AudioFocusApplication.kt`
  - Hilt application entry point.
- `app/src/main/java/com/polaralias/audiofocus/MainActivity.kt`
  - Starts the service when monitoring is enabled.
  - Hosts the Compose app UI.
- `app/src/main/java/com/polaralias/audiofocus/di/SettingsModule.kt`
  - Binds `SettingsRepositoryImpl` to `SettingsRepository`.

Assessment:

- Dependency injection is minimal and focused.
- The app is structurally simple enough to understand without deep framework indirection.

### Core model and decision logic

- `app/src/main/java/com/polaralias/audiofocus/core/model/CoreModels.kt`
  - Central enums and data classes:
    - `TargetApp`
    - `WindowState`
    - `PlaybackType`
    - `PlaybackStateSimplified`
    - `OverlayDecision`
    - theme/settings models
- `app/src/main/java/com/polaralias/audiofocus/core/logic/PlaybackStateEngine.kt`
  - Main decision engine for whether overlay should appear.
- `app/src/main/java/com/polaralias/audiofocus/core/logic/MediaControlClient.kt`
  - Sends media actions to the active `MediaController`.

Assessment:

- This is the real product core.
- The decision engine is the best place to start any future verification effort.
- The data model and monitor heuristics now align more closely, but their real-device reliability still needs validation.

### Platform monitoring layer

- `service/monitor/AccessibilityMonitor.kt`
  - Consumes accessibility events.
  - Walks the current view tree looking for `SurfaceView` or `TextureView`.
  - Infers window size heuristically from bounds.
- `service/monitor/MediaSessionMonitor.kt`
  - Tracks active media sessions via `MediaSessionManager`.
  - Maps platform playback state into simplified playback state.
- `service/monitor/NotificationMonitor.kt`
  - Tracks media-style notifications for supported packages.
- `service/monitor/ForegroundAppDetector.kt`
  - Tracks foreground package using accessibility events and a usage-stats bootstrap.

Assessment:

- This is the highest-risk layer because it depends on Android platform behaviour, OEM differences, accessibility tree shape, and permission state.
- The code suggests the app is event-driven rather than polling-heavy, which matches the intended product shape.
- The monitors are small enough to audit fully, but they need real-device validation more than they need immediate refactoring.

### Long-running service and overlay window

- `service/AudioFocusService.kt`
  - Foreground service.
  - Starts monitor collection and reacts to settings changes.
  - Applies `PlaybackStateEngine` decisions to `OverlayManager`.
- `service/overlay/OverlayManager.kt`
  - Creates a full-screen `TYPE_APPLICATION_OVERLAY` Compose view.
  - Manages synthetic lifecycle ownership for that view.
- `ui/overlay/OverlayScreen.kt`
  - Renders the actual full-screen overlay.
  - Uses either:
    - configured solid color
    - configured image
    - media album art fallback
  - Shows transport controls and progress.

Assessment:

- This is the user-visible feature surface.
- Overlay lifecycle handling is more manual than a normal in-app Compose screen, which makes it a natural future verification hotspot.

### User-facing app UI

- `ui/navigation/AppNavigation.kt`
  - Two routes: onboarding and home.
- `ui/onboarding/OnboardingScreen.kt`
  - Sequential permission flow:
    - overlay
    - accessibility
    - notification listener
    - usage stats
- `ui/home/HomeScreen.kt`
  - Master enable/disable switch for monitoring.
  - Per-app tab selection.
  - Theme type selection:
    - solid color
    - image
  - Blur slider.
  - Visual preview card.

Assessment:

- The app UI is primarily a configuration shell around the service feature.
- There is no evidence of a deeper settings or content model beyond service enablement and theme customization.

### Persistence and permissions

- `domain/PermissionManager.kt`
  - Checks and opens settings screens for required permissions.
- `domain/settings/SettingsRepository.kt`
  - Settings abstraction.
- `data/settings/SettingsRepositoryImpl.kt`
  - DataStore-backed implementation.
  - Persists:
    - monitoring enabled
    - onboarding completion
    - per-app theme type
    - color
    - image URI
    - blur level

Assessment:

- Persistence scope is narrow and understandable.
- Theme configuration is clearly per supported app.
- Some legacy compatibility paths still exist, which suggests the settings model evolved in place.

## Inferred behaviour by supported app

### YouTube

The code appears to intend:

- No overlay when paused or stopped.
- No overlay for audio-only or no-video states.
- Overlay when playback is active and accessibility detects visible video and the app is:
  - fullscreen
  - minimised in-app
  - picture-in-picture

Actual caution:

- The current monitor logic does not clearly produce all of those states distinctly.

### YouTube Music

The code appears to intend:

- Overlay only when playback is active.
- Overlay only when accessibility detects visible video.
- No overlay when backgrounded or not visible.

Actual caution:

- The codebase suggests YouTube Music is treated as a possible video surface, not purely an audio app.

## Important mismatches and uncertainties

These are the main places where the repository intent and the actual code shape diverge.

### 1. Accessibility heuristics now emit the richer state model, but still need device proof

The enums advertise:

- `AUDIO_ONLY`
- `FOREGROUND_MINIMISED`
- `BACKGROUND`

Current code inspection suggests:

- `AccessibilityMonitor` now emits `VISIBLE_VIDEO`, `AUDIO_ONLY`, and `NONE`.
- `AccessibilityMonitor` now infers `FOREGROUND_FULLSCREEN`, `FOREGROUND_MINIMISED`, `PICTURE_IN_PICTURE`, and `NOT_VISIBLE` from local heuristics.
- `BACKGROUND` is still inferred later by `PlaybackStateEngine` when the foreground package does not match.

Implication:

- The product language is more precise than the current implementation evidence.

### 2. Notification monitoring is now part of overlay decisions, but runtime reliability is still unverified

`NotificationMonitor` is now consumed by `PlaybackStateEngine` and is used to suppress stale overlays when the supported app no longer has an active media notification.

Implication:

- The repository now has a real four-signal decision engine in code.
- The remaining uncertainty is runtime reliability across Android versions and OEM behaviour, not a dormant code path.

### 3. System-UI protection is specified but not clearly enforced

The technical spec says overlays must never obstruct system UI.

The implementation currently creates a full-screen application overlay window and does not show obvious code that explicitly suppresses itself around:

- notification shade
- settings
- lock screen
- other system surfaces

Implication:

- This may work acceptably in practice, or may not. The code alone does not prove compliance with the documented requirement.

### 4. Onboarding completion is now persisted and participates in launch routing

Navigation start is now chosen from persisted onboarding completion plus current permission truth.

Implication:

- Returning users with all required permissions can land directly in the home experience.
- Permission regressions fall back to onboarding rather than assuming prior completion is still valid.

### 5. Media control capability checks are implemented locally, but not yet device-verified

The overlay UI now disables seek, play or pause, and skip controls when the active session does not advertise the corresponding capability, and `MediaControlClient` suppresses unsupported commands.

Implication:

- The repository now matches the intended contract locally for capability-aware controls.
- Real-session behaviour on device is still a validation task.

### 6. Notification-derived media relevance is now tracked more defensibly

`NotificationMonitor` now tracks active media notifications per notification key rather than only by app name.

Implication:

- Removing one media notification no longer clears another still-active notification from the same supported app.
- Notification-derived stale-overlay suppression is better aligned with real notification lifecycles.

## Verification state as of this review

### Verified by direct inspection

- Single Android app module exists and is internally coherent.
- Manifest declares the expected high-privilege services and permissions.
- Foreground service, accessibility service, and notification listener are wired together.
- Settings persistence exists and is used by both app UI and overlay UI.
- A unit test file exists for `PlaybackStateEngine`.
- CI workflow exists for debug build, unit tests, and lint.

### Unverified because documents are not trusted

- Product claims in `docs/technical_specification.md`
- Completion status claims in `AGENTS.md`
- Historical prompt artifacts under `docs/archive/prompts/`

### Superseded by later verification evidence

The first mapping pass was produced before local Gradle verification was repaired.

Current truth:

- Local automated verification now has a separate evidence record in `docs/verification_baseline.md`.
- That document supersedes the earlier “could not complete local automation” state from the first pass.

## Initial quality read

This repository does not look like a random experiment. It looks like a small, focused Android app with a clear idea, a plausible architecture, and incomplete hardening.

The current state appears to be:

- strong enough to understand
- strong enough to document
- strong enough to publish honestly as a repository
- not strong enough yet to present as fully device-verified product behaviour

The main problem is not “there is no architecture”.
The main problem is “the architecture is only partially proven”.

## Priority areas for follow-up investigation

If this repository is being turned into a public portfolio project, the next research passes should focus on these containers.

### A. Runtime truth table verification

Validate on device:

- which supported app states actually trigger overlay
- whether audio-only playback is reliably ignored
- whether PiP, minimised, and fullscreen states are distinguishable in practice
- what happens during app switching and paused playback

### B. Permission and lifecycle truth

Validate:

- first-run onboarding behaviour
- app relaunch after permissions already granted
- boot restart behaviour
- service stop/start behaviour from the home toggle
- process death recovery

### C. Overlay safety and UX

Validate:

- whether overlay wrongly covers system UI
- touchability and media control responsiveness
- album art and custom image rendering behaviour
- behaviour on API levels below blur support

### D. Dependency and dead-code audit

Confirm:

- which dependencies are actually needed
- whether Room/Media3/serialization are unused
- whether legacy settings compatibility paths are still required

### E. Test surface expansion

Current test footprint is now meaningfully broader than the original inherited baseline.

Likely future test targets:

- `PlaybackStateEngine` truth-table coverage
- settings persistence mapping
- service enable/disable state transitions
- real-device overlay truth table validation support

## Practical summary

If you need a concise mental model of the repo today:

- The app tries to identify supported video playback.
- It reduces several Android signals into one overlay decision.
- It renders a configurable full-screen blocking overlay with transport controls.
- Most of the complexity lives in platform detection rather than business logic.
- The repository already has enough structure to become a strong public project, but it needs verification, documentation, and evidence before it should be treated as production-grade.

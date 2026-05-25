# Decision 002: Current Runtime Contract Boundaries

## Status

Accepted on 2026-05-23.

## Context

The data model and desired-state specification describe a richer product than the currently evidenced implementation.

Future agents need a compact statement of what the runtime contract is today so they do not over-claim support or accidentally treat dormant paths as active behaviour.

## Decision

The current runtime contract should be described as follows:

- Supported target apps are only `YouTube` and `YouTube Music`.
- The active overlay-decision inputs are:
  - accessibility state
  - media session state
  - notification-derived media relevance
  - foreground package
- `NotificationMonitor` is an active overlay-decision input and is used to suppress stale overlay decisions when the supported app no longer has an active media notification.
- Current runtime logic allows a fallback overlay decision when a supported app is `PLAYING`, exposes `VISIBLE_VIDEO`, and is confirmed foreground even if a media notification is absent.
- `AccessibilityMonitor` currently emits:
  - `PlaybackType.VISIBLE_VIDEO`
  - `PlaybackType.AUDIO_ONLY`
  - `PlaybackType.NONE`
  - `WindowState.FOREGROUND_FULLSCREEN`
  - `WindowState.FOREGROUND_MINIMISED`
  - `WindowState.PICTURE_IN_PICTURE`
  - `WindowState.NOT_VISIBLE`
- `WindowState.BACKGROUND` is derived later by `PlaybackStateEngine` when foreground-package evidence contradicts target-app visibility.
- The app persists onboarding completion separately from monitoring-enabled state and uses that plus current permission truth to choose launch routing.
- Overlay controls are action-capability-gated in the current UI and `MediaControlClient` avoids sending unsupported commands.
- System-UI safety while overlays are visible remains a desired-state requirement pending device verification.

## Consequences

- Agents should document the decision engine as a four-signal system.
- Agents should describe `AUDIO_ONLY` and `FOREGROUND_MINIMISED` as active runtime outputs, while still describing their reliability as `verified limited` until device validation happens.
- Remaining product gaps should focus on runtime safety and device proof, not on dormant onboarding, notification, or control-capability paths.

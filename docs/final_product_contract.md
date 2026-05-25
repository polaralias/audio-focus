# Final Product Contract

## Purpose

This document defines the intended final product state for AudioFocus.

Use it when planning, implementing, or validating work toward the finished product.
Do not treat it as proof that the current repository already satisfies this contract.

For current repository truth, read:

1. `README.md`
2. `docs/agent_harness.md`
3. `docs/verification_baseline.md`
4. `docs/codebase_map.md`
5. `docs/decisions/002-current-runtime-contract.md`

## Contract status

- This is the canonical desired end state for the product.
- It is more specific than `docs/technical_specification.md` for agent implementation work.
- It should be used as the target contract when current implementation and final intent differ.

## Final product outcomes

The finished product should:

- detect distracting video playback in supported apps with enough precision to avoid false overlays during audio-only use
- use four decision signals, not three
- distinguish audio-only playback from visible video
- distinguish fullscreen, foreground-minimised, PiP, background, and not-visible states with product-meaningful accuracy
- use notification evidence as an active decision input rather than a passive log stream
- keep overlays off system UI and non-target surfaces
- persist onboarding state so returning users do not restart onboarding once the required permissions are already established
- gate overlay controls by advertised transport capability
- preserve the current app-specific theme customization model

## Supported apps

At final product state, supported apps remain:

- `YouTube`
- `YouTube Music`

Expansion beyond those apps should be treated as a separate product decision.

## Final decision engine contract

The final overlay decision engine must combine four live signals:

1. accessibility state
2. media session state
3. notification-derived playback relevance
4. foreground-app visibility

The engine should not rely on any single signal being perfect.
The intended role of each signal is:

- accessibility:
  - detect visible video surfaces
  - infer whether the visible target window is fullscreen, minimised in-app, PiP, background, or not visible
  - distinguish `AUDIO_ONLY` from `VISIBLE_VIDEO` when the app remains active without a visible video surface
- media session:
  - provide the authoritative simplified playback state
  - expose metadata and transport capability for overlay controls
- notification:
  - confirm that the target app still presents an active media-style session
  - help suppress stale overlay decisions when session state lingers after the app is no longer foreground-relevant
- foreground-app detection:
  - identify whether the supported app is actually the top user-facing app
  - demote candidate overlays when another app is foregrounded

## Final state model contract

The final product should treat the following model states as active runtime states, not just declarations:

### Playback type

- `NONE`
- `AUDIO_ONLY`
- `VISIBLE_VIDEO`

### Window state

- `NOT_VISIBLE`
- `FOREGROUND_FULLSCREEN`
- `FOREGROUND_MINIMISED`
- `PICTURE_IN_PICTURE`
- `BACKGROUND`

Interpretation requirements:

- `AUDIO_ONLY` means the supported app has active playback but the current surface should not trigger a blocking overlay.
- `FOREGROUND_MINIMISED` means the supported app is still foreground-relevant but the video surface is not occupying the full user-visible experience.
- `BACKGROUND` means the app may still have an active session, but it should not trigger overlay because it is not the active visible target.

## Final overlay behaviour contract

### YouTube

- show overlay for `VISIBLE_VIDEO` when window state is:
  - `FOREGROUND_FULLSCREEN`
  - `FOREGROUND_MINIMISED`
  - `PICTURE_IN_PICTURE`
- do not show overlay when playback is `PAUSED` or `STOPPED`
- do not show overlay for `AUDIO_ONLY`
- do not show overlay for `BACKGROUND` or `NOT_VISIBLE`

### YouTube Music

- show overlay for `VISIBLE_VIDEO` in any visible user-facing state:
  - `FOREGROUND_FULLSCREEN`
  - `FOREGROUND_MINIMISED`
  - `PICTURE_IN_PICTURE`
- do not show overlay for `AUDIO_ONLY`
- do not show overlay for `BACKGROUND` or `NOT_VISIBLE`
- do not allow a stale media session alone to keep the overlay alive

## Final onboarding and lifecycle contract

The final product should:

- route first launch through onboarding
- persist onboarding completion separately from monitoring enabled state
- allow returning users with all required permissions to land directly in the home experience
- recover monitoring state after reboot when monitoring was enabled
- restore overlay behaviour safely after service restart or process death

## Final control contract

The final overlay controls should:

- show play or pause according to current playback state
- enable seek only when the active session supports seeking
- enable skip forward and skip backward only when the active session supports those actions
- avoid sending unsupported commands optimistically

## Final safety contract

The final product must not obstruct:

- notification shade
- Android settings screens
- lock screen affordances
- emergency or system-level callouts
- non-target apps

## Gap policy

When current implementation differs from this final contract:

- document the current truth in current-state docs
- document the target truth here
- put durable interpretation or sequencing decisions in `docs/decisions/`
- do not silently collapse the distinction

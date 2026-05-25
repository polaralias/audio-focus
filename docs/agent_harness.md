# Agent Harness

## Purpose

This document is the operating contract for future agent work in this repository.

Use it to answer:

1. Which docs are authoritative?
2. What does the repository actually prove today?
3. Which gaps are current-state gaps versus final-product intent?
4. Which docs must move when code or evidence changes?

## Trust Hierarchy

Interpret repository truth in this order:

1. Code plus successful verification artifacts
2. `docs/verification_baseline.md`
3. `docs/release_readiness.md`
4. `docs/codebase_map.md`
5. `docs/decisions/`
6. `README.md`
7. `GLOSSARY.md`
8. `docs/final_product_contract.md`
9. `docs/technical_specification.md`
10. `AGENTS.md`
11. `docs/archive/`

Rules:

- Items 1-7 are current-state surfaces.
- `docs/final_product_contract.md` is the canonical target contract for the finished product.
- `docs/technical_specification.md` is desired-state architecture intent, not current proof.
- `docs/archive/` is retained context only.

## Current Runtime Contract

Assume the following unless new evidence supersedes it:

- Supported apps are only `YouTube` and `YouTube Music`.
- Overlay decisions use four live signals:
  - accessibility state
  - media session state
  - notification-derived media relevance
  - foreground package
- `AccessibilityMonitor` currently emits:
  - `PlaybackType.VISIBLE_VIDEO`
  - `PlaybackType.AUDIO_ONLY`
  - `PlaybackType.NONE`
  - `WindowState.FOREGROUND_FULLSCREEN`
  - `WindowState.FOREGROUND_MINIMISED`
  - `WindowState.PICTURE_IN_PICTURE`
  - `WindowState.NOT_VISIBLE`
- `WindowState.BACKGROUND` is currently an engine-level demotion when foreground package evidence contradicts target visibility.
- App launch uses persisted onboarding completion plus current permission truth to choose between onboarding and home.
- Overlay transport controls are gated by advertised controller capabilities.
- Notification activity is tracked per notification key so one removal does not clear another active notification from the same app.
- System-UI safety while overlays are visible is still a verification gap, not a proven behaviour claim.

## Release Posture

The repository can currently be described as:

- `verified working` for local build, unit tests, lint, and debug assembly
- `verified limited` for a reproduced emulator smoke pass
- `untested` for the physical-device behaviour matrix

Use `docs/release_readiness.md` for the publication boundary and `docs/verification_baseline.md` for dated evidence.

## Change Protocol

When changing implementation:

1. Update the implementation.
2. Run the narrowest meaningful verification.
3. Update the canonical docs in the same slice.
4. Widen support claims only if evidence exists.

When changing repository understanding without code changes:

1. Prefer `GLOSSARY.md` for terms and evidence language.
2. Prefer `docs/decisions/` for durable interpretation changes.
3. Prefer `docs/codebase_map.md` for implementation-shape facts.
4. Prefer `docs/release_readiness.md` and `docs/verification_baseline.md` for support posture and evidence.

## Open Verification Work

The remaining important unknowns are runtime-validation questions:

- exact overlay truth table on physical devices
- YouTube and YouTube Music accessibility-tree reliability across app versions
- permission onboarding behaviour across OEM settings variants
- interaction with notification shade, settings, lock screen, and other system surfaces
- boot recovery, service restart, and process-death recovery
- real media transport behaviour against active sessions

Describe these as `untested` or `verified limited` until new evidence lands.

# TDD Slice 001: Persisted Onboarding Completion

## Status

Completed on 2026-05-23.

## Purpose

This document is the current TDD start surface for the next development tranche.

Use it to begin red-green-refactor work without rediscovering the repository contract.

## Why this slice goes first

This slice is the best first TDD candidate because it is:

- explicitly required by the final product contract
- clearly absent from the current runtime contract
- locally testable without depending on physical-device-only verification
- narrow enough for tracer-bullet development

Reference gap:

- current truth: app launch always starts at onboarding and onboarding completion is not persisted
- target truth: returning users with required permissions should land directly in home

Canonical references:

1. `docs/agent_harness.md`
2. `docs/decisions/002-current-runtime-contract.md`
3. `docs/decisions/003-final-product-contract.md`
4. `docs/final_product_contract.md`
5. `app/src/main/java/com/polaralias/audiofocus/ui/navigation/AppNavigation.kt`
6. `app/src/main/java/com/polaralias/audiofocus/ui/onboarding/OnboardingScreen.kt`
7. `app/src/main/java/com/polaralias/audiofocus/domain/settings/SettingsRepository.kt`
8. `app/src/main/java/com/polaralias/audiofocus/data/settings/SettingsRepositoryImpl.kt`
9. `app/src/main/java/com/polaralias/audiofocus/MainActivity.kt`

## Current repository truth

The repository currently proves:

- navigation defaults to `Routes.ONBOARDING`
- onboarding completion is transient callback state only
- `AppSettings` persists monitoring and theme configuration, not onboarding completion

The repository does not yet prove:

- a persisted onboarding-complete flag
- launch-time route selection based on persisted onboarding state plus current permission state

## TDD scope

This slice should only establish:

- persisted onboarding completion separate from monitoring enabled state
- launch-time route selection between onboarding and home
- safe fallback to onboarding when required permissions are missing

This slice should not attempt to solve:

- OEM-specific permission-screen navigation differences
- physical-device validation of every onboarding step
- service lifecycle or boot recovery changes unrelated to route selection

## Proposed public interface

Drive the slice through small public interfaces rather than UI internals.

Recommended interface changes:

1. Extend `AppSettings` with `hasCompletedOnboarding: Boolean`.
2. Extend `SettingsRepository` with a write path for onboarding completion.
3. Introduce a small route-selection interface that answers whether app launch should begin at onboarding or home.

Recommended launch contract:

- If `hasCompletedOnboarding` is `false`, start at onboarding.
- If `hasCompletedOnboarding` is `true` and required permissions are still granted, start at home.
- If `hasCompletedOnboarding` is `true` but required permissions are now missing, start at onboarding.
- Completing onboarding should persist the flag before navigating home.

## Behaviour priorities

These are the first behaviours to test, in order:

1. Fresh install starts at onboarding.
2. Completed onboarding with all required permissions starts at home.
3. Completed onboarding with a later permission regression falls back to onboarding.
4. Completing onboarding persists independently from monitoring enabled state.

## Tracer-bullet recommendation

Start with one test for:

- completed onboarding plus all permissions granted returns `Routes.HOME`

Why this first:

- it forces creation of the launch-routing interface
- it proves the new persisted setting participates in app startup
- it avoids dragging Compose navigation into the first RED cycle

## Test levels

Preferred automated coverage for this slice:

- unit tests for route-selection logic
- unit tests for settings mapping if `AppSettings` grows a new persisted field

Possible follow-up coverage after the logic is green:

- narrow Compose or ViewModel test for onboarding completion callback persistence

Avoid for the first pass:

- brittle UI tests tied to navigation internals
- device-only tests as the primary proof for the persisted-flag logic

## Out of scope signals

Do not expand this tranche into:

- notification-derived overlay decisioning
- capability-aware media transport controls
- accessibility-state enrichment for `AUDIO_ONLY` or `FOREGROUND_MINIMISED`
- overlay safety against system UI

Those are valid later slices, but they should not be mixed into the first TDD loop.

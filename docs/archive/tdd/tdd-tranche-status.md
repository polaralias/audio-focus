# TDD Tranche Status

## Status

Active on 2026-05-23.

## Completed local TDD slices

The following slices are now implemented and locally verified:

1. Slice 001: persisted onboarding completion
2. Slice 002: capability-aware overlay controls
3. Slice 003: notification-derived overlay decision input
4. Slice 004: accessibility heuristics for `AUDIO_ONLY` and `FOREGROUND_MINIMISED`

## What the repository now proves locally

- onboarding completion is persisted separately from monitoring enabled state
- launch routing chooses onboarding or home from persisted onboarding truth plus current permission truth
- overlay controls are disabled when the active session does not advertise the corresponding capability
- `MediaControlClient` suppresses unsupported commands
- `PlaybackStateEngine` now combines accessibility, media session, notification, and foreground-app signals
- accessibility heuristics emit `AUDIO_ONLY`, `VISIBLE_VIDEO`, `FOREGROUND_FULLSCREEN`, `FOREGROUND_MINIMISED`, `PICTURE_IN_PICTURE`, and `NOT_VISIBLE`

## Remaining gaps to the final product contract

The main remaining gaps are no longer repository-structure or dormant-code gaps. They are runtime-validation and platform-behaviour gaps:

- system-UI-safe overlay behaviour
- exact overlay truth table on physical devices
- OEM-specific permission and settings flows
- notification, accessibility, and media-session reliability across real app versions and Android variants
- reboot, process-death, and lifecycle recovery proof on device

## Next recommended tranche

The next tranche should be a verification-led tranche, not another broad local-TDD tranche.

Primary documents for that work:

1. `docs/verification_baseline.md`
2. `docs/agent_harness.md`
3. `docs/final_product_contract.md`

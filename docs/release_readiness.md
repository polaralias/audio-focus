# Release Readiness

## Status

Updated on 2026-05-25.

The repository is ready for public publication as an Android project repository.

That means:

- docs are aligned to the current verified state
- historical prompt and tranche-planning artifacts are archived under `docs/archive/`
- local automated verification passes
- Android emulator validation has been reproduced
- an older Play-backed emulator reproduces a YouTube `PLAYING` session and an AudioFocus overlay window
- no open TODO/FIXME markers remain in app code or active docs

## What Is Ready

- Root reading order and trust hierarchy are explicit.
- Current-state docs are separated from final-product intent.
- Archive material is off the active reading path.
- CI builds debug and unsigned release APK artifacts and runs unit tests and lint.
- The dependency surface matches the current app implementation more closely after removing unused `Room`, `Media3 Session`, and `kotlinx.serialization` entries.
- Verified emulator scope now includes onboarding launch, permission completion, YouTube playback detection, and overlay-window activation on an older Play-backed AVD.

## Publication Guidance

Safe public claim:

- "AudioFocus is a buildable Android prototype with local automated verification and bounded emulator validation, including reproduced YouTube overlay activation on an older Play-backed AVD."

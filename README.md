# AudioFocus

AudioFocus is an Android app that hides distracting video in selected media apps behind a full-screen overlay while preserving audio playback and basic transport controls.

## Status

Current repository truth:

- Local automated verification passes on this machine.
- Android emulator validation has been reproduced locally.
- An older Play-backed emulator reproduces a YouTube `PLAYING` session and an AudioFocus overlay window.

Publication posture:

- The repository is ready to publish as a documented, buildable project.
- The current public claim is bounded to local automated verification and emulator-verified runtime behaviour.

## Reading Order

Read these files in order:

1. [`README.md`](README.md)
2. [`docs/agent_harness.md`](docs/agent_harness.md)
3. [`docs/release_readiness.md`](docs/release_readiness.md)
4. [`docs/verification_baseline.md`](docs/verification_baseline.md)
5. [`docs/codebase_map.md`](docs/codebase_map.md)
6. [`docs/final_product_contract.md`](docs/final_product_contract.md)
7. [`docs/technical_specification.md`](docs/technical_specification.md)
8. [`docs/decisions/001-repository-truth-precedence.md`](docs/decisions/001-repository-truth-precedence.md)
9. [`docs/decisions/002-current-runtime-contract.md`](docs/decisions/002-current-runtime-contract.md)
10. [`docs/decisions/003-final-product-contract.md`](docs/decisions/003-final-product-contract.md)
11. [`GLOSSARY.md`](GLOSSARY.md)
12. [`AGENTS.md`](AGENTS.md)

## Canonical Docs

These are the current source-of-truth surfaces:

- Repository operating contract: [`docs/agent_harness.md`](docs/agent_harness.md)
- Release posture: [`docs/release_readiness.md`](docs/release_readiness.md)
- Verification evidence: [`docs/verification_baseline.md`](docs/verification_baseline.md)
- Implementation map: [`docs/codebase_map.md`](docs/codebase_map.md)
- Final product target: [`docs/final_product_contract.md`](docs/final_product_contract.md)
- Repository language: [`GLOSSARY.md`](GLOSSARY.md)
- Durable interpretation decisions: `docs/decisions/`

Historical material lives under `docs/archive/` and should not be treated as the active contract.

## Verified Baseline

The latest local verification pass confirms:

- `.\gradlew.bat testDebugUnitTest`
- `.\gradlew.bat lintDebug`
- `.\gradlew.bat assembleDebug`
- `.\gradlew.bat help --warning-mode all`

The latest emulator validation confirms:

- fresh-install launch into onboarding
- permission completion through to the active home screen on a Play-backed AVD
- reproduced YouTube playback detection
- reproduced AudioFocus overlay-window activation during the YouTube path

See [`docs/verification_baseline.md`](docs/verification_baseline.md) for the dated evidence and current limits.

## Repository Layout

- `app/` Android app code, resources, manifest, and tests
- `docs/` current docs, decisions, verification evidence, and archive
- `.github/workflows/` CI for debug build, unit tests, and lint
- `skills/` repository-local agent skills

## Contribution Rule

When behaviour or support claims change, update the canonical docs in the same slice and keep current-state evidence separate from desired-state specification.

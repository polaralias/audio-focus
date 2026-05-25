# Glossary

This file defines the repository language future contributors and agents should use when describing AudioFocus.

## Evidence language

### verified working

Behaviour or tooling that has direct confirming evidence, such as a successful build, passing test, successful manual reproduction, or dated validation note.

### verified limited

Partially confirmed behaviour where some evidence exists, but the full support claim is not yet proven across the intended scenarios or platforms.

### current observed state

What the repository code, build outputs, or direct inspection currently show, without claiming that the behaviour is fully correct or complete.

### desired state

What the project intends to support or achieve, even if the repository does not yet prove it.

### final product contract

The canonical documented description of the intended finished product state that implementation should target, separate from current verified behaviour.

### untested

A flow, platform condition, or support claim that has not yet been validated with sufficient evidence.

### known broken

A behaviour that has evidence of failure or drift from expected behaviour.

### dormant

A code path, dependency, or signal that exists in the repository but is not currently active in the effective runtime behaviour being documented.

## Product terms

### overlay

The full-screen blocking UI shown on top of a supported app to hide distracting video while preserving audio and transport controls.

### overlay decision

The result emitted by the playback decision logic that determines whether the overlay should be shown, and for which supported app.

### supported app

An app explicitly modeled in `TargetApp`. In the current codebase this means YouTube and YouTube Music only.

### playback type

The repository model for the kind of media surface currently inferred:

- `VISIBLE_VIDEO`
- `AUDIO_ONLY`
- `NONE`

Important:

- The model is richer than the currently verified implementation.

### window state

The repository model for how the target app is currently presented to the user:

- `NOT_VISIBLE`
- `FOREGROUND_FULLSCREEN`
- `FOREGROUND_MINIMISED`
- `PICTURE_IN_PICTURE`
- `BACKGROUND`

Important:

- Not every declared window state is currently proven to be emitted distinctly by the implementation.

### derived state

A state or conclusion produced later by logic combining signals, rather than emitted directly by the originating monitor.

## Knowledge-base terms

### canonical doc

A tracked document future contributors should trust first for current repository truth.

### evidence doc

A document that records what has been validated, how it was validated, and what remains unverified.

### aspirational doc

A document that describes desired behaviour or design intent rather than current proven behaviour.

### derived artifact

A generated or historical artifact that is useful for context but should not be treated as the current source of truth.

### trust hierarchy

The documented precedence order future agents should use when different repository artifacts appear to disagree.

### agent harness

The repository-level operating contract for agent-led development, covering reading order, documentation precedence, evidence thresholds, and capture rules.

### current runtime contract

The documented boundary of what the repository currently implements or proves.

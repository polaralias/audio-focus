# Decision 003: Final Product Contract Layer

## Status

Accepted on 2026-05-23.

## Context

The repository now needs to support two different but legitimate reading modes:

- current repository truth for safe continuation work
- final intended product state for agent-led implementation planning

Without a separate target-contract layer, agents either:

- over-claim that the current code already satisfies the intended design, or
- under-specify future implementation because the current runtime limits dominate the docs

## Decision

The repository will maintain two explicit contract layers:

1. current runtime contract
2. final product contract

They serve different purposes:

- current runtime contract:
  - describes what the repository currently implements or proves
  - anchors verification claims and safe support language
- final product contract:
  - describes the intended finished behaviour for implementation and validation planning
  - may be more ambitious than the current code, but must be explicit and concrete

The final product contract for AudioFocus includes:

- a four-signal decision engine
- active use of notification-derived evidence
- runtime emission and use of `AUDIO_ONLY`
- runtime emission and use of `FOREGROUND_MINIMISED`
- capability-aware overlay controls
- persisted onboarding completion
- system-UI-safe overlay behaviour

`docs/final_product_contract.md` is the canonical target-contract document for this layer.

## Consequences

- Agents can implement toward the intended product without erasing the current-truth boundary.
- Verification docs remain honest about what is proven today.
- Future code changes can be tracked as movement from current contract toward final contract.

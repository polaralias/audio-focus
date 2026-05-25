# Decision 001: Repository Truth Precedence

## Status

Accepted on 2026-05-23.

## Context

The repository now has several documentation surfaces with different intent:

- current-truth documentation
- final-product target-contract documentation
- desired-state specification
- inherited agent notes
- historical prompt artifacts

Without a documented precedence rule, future agents can over-trust aspirational or inherited text and make unsupported claims.

## Decision

Documentation should be interpreted with this precedence:

1. code plus successful verification artifacts
2. `docs/verification_baseline.md`
3. `docs/codebase_map.md`
4. other decision notes in `docs/decisions/`
5. `README.md`
6. `GLOSSARY.md`
7. `docs/final_product_contract.md`
8. `docs/technical_specification.md`
9. `AGENTS.md`
10. `docs/archive/`

Additional rules:

- `docs/final_product_contract.md` is the canonical target contract for the intended finished product state.
- `docs/technical_specification.md` is a desired-state contract unless specific behaviour is separately verified.
- `AGENTS.md` is authoritative for local workflow guidance, but not for current behaviour or completion claims unless supported by canonical evidence docs.
- `docs/archive/` contains historical artifacts and must not be treated as the active product contract.

## Consequences

- Agents can work repository-first without re-litigating which documents to trust.
- Agents can safely distinguish current repository truth from implementation targets for future TDD work.
- Behaviour claims must be grounded in code and evidence before they are promoted into canonical docs.
- Future documentation work should update canonical surfaces in the same slice as implementation or verification changes.

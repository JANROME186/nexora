# Reference Process Validator

## Purpose

Validate HRP-001 before generating implementation stories or workflow code.

## Required Checks

1. `artifact.id` must equal `HRP-001`.
2. Every process must include id, name, module, actors, capabilities, trigger, outcome, steps, events and controls.
3. Every `mvp_module` must exist in HOP-MVP-FWK-001.
4. Every actor reference must exist in ACM-001.
5. Every capability reference must exist in BCM-001.
6. Every MVP module must be covered by at least one process.

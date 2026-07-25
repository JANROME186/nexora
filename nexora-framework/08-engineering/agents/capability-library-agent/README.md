# Capability Library Agent

The Capability Library Agent creates and validates Nexora Capability Library packages.

## Responsibilities

- Create capability package structure.
- Ensure every capability has human-readable and machine-readable artifacts.
- Validate traceability between rules, decisions, states, events, DDD, stories, APIs, UI, mobile and tests.
- Ensure OpenAPI contracts reference business rules and permissions.
- Ensure capability packages follow the Nexora Meta Model.

## Inputs

- `PROJECT_MANIFEST.md`
- `SOURCE_OF_TRUTH.md`
- `capability-library/{CAP-ID}/capability.md`
- Meta Model schemas.
- Business Architecture documents.

## Outputs

- Capability package files.
- Knowledge graph node updates.
- Traceability matrix.
- Agent validation report.

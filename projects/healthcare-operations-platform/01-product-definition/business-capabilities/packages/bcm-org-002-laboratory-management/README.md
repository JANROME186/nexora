# Laboratory Management Capability Package (`BCM-ORG-002`)

## Overview

The **Laboratory Management** capability package governs laboratory organization identity, multi-branch laboratory registration, sanitary license tracking, clinical director assignment, and operational status management under tenant administration for Healthcare Operations Platform.

## Package Models

- `capability-package.md`: Package identity, scope, dependencies, and surface definitions.
- `business-model.md`: Aggregate definitions for `Laboratory` (AGG-002), `LaboratorySanitaryLicense`, and `ClinicalDirectorAssignment`.
- `business-rules.md`: Enforcement rules for code uniqueness, sanitary license validity, and director verification.
- `processes.md`: Business processes for laboratory registration, director assignment, and status transition.
- `events.md`: Domain events emitted on laboratory registration, license updates, and status changes.
- `openapi-source.md`: Source OpenAPI contract definitions for `/api/platform/laboratories`.
- `permissions.md`: IAM permission definitions (`laboratory:create`, `laboratory:read`, `laboratory:manage_license`, etc.).
- `ui-model.md`: Web UI screens for laboratory administration in employee portal.
- `mobile-model.md`: Mobile view models and read-only laboratory cards.
- `test-model.md`: Acceptance, contract, and unit test specifications.
- `observability-model.md`: Operational metrics and structured logging guidelines.
- `generation-plan.md`: Generator targets vs. custom business logic boundaries.
- `traceability.md`: Mapping matrix connecting BCM-ORG-002 to requirements, rules, endpoints, and QA evidence.

# Laboratory Management Capability Package (`BCM-ORG-002`)

## Overview

The **Laboratory Management** capability package governs laboratory organization identity, multi-branch laboratory registration, sanitary license tracking, clinical director assignment, and operational status management under tenant administration for Healthcare Operations Platform.

## Package Models

- `capability-package.yaml`: Package identity, scope, dependencies, and surface definitions.
- `business-model.yaml`: Aggregate definitions for `Laboratory` (AGG-002), `LaboratorySanitaryLicense`, and `ClinicalDirectorAssignment`.
- `business-rules.yaml`: Enforcement rules for code uniqueness, sanitary license validity, and director verification.
- `processes.yaml`: Business processes for laboratory registration, director assignment, and status transition.
- `events.yaml`: Domain events emitted on laboratory registration, license updates, and status changes.
- `openapi-source.yaml`: Source OpenAPI contract definitions for `/api/platform/laboratories`.
- `permissions.yaml`: IAM permission definitions (`laboratory:create`, `laboratory:read`, `laboratory:manage_license`, etc.).
- `ui-model.yaml`: Web UI screens for laboratory administration in employee portal.
- `mobile-model.yaml`: Mobile view models and read-only laboratory cards.
- `test-model.yaml`: Acceptance, contract, and unit test specifications.
- `observability-model.yaml`: Operational metrics and structured logging guidelines.
- `generation-plan.yaml`: Generator targets vs. custom business logic boundaries.
- `traceability.yaml`: Mapping matrix connecting BCM-ORG-002 to requirements, rules, endpoints, and QA evidence.

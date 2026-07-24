# Branch Management Capability Package (`BCM-ORG-003`)

## Overview

The **Branch Management** capability package governs physical branch location setup, operating schedules, reception and sample collection readiness, contact details, and operational status management under laboratory administration for Healthcare Operations Platform.

## Package Models

- `capability-package.yaml`: Package identity, scope, dependencies, and surface definitions.
- `business-model.yaml`: Aggregate definitions for `Branch` (AGG-003), `BranchAddress`, `BranchCapacityConfig`, and `BranchSchedule`.
- `business-rules.yaml`: Enforcement rules for branch code uniqueness, address completeness, parent laboratory status constraints, and branch quotas.
- `processes.yaml`: Business processes for physical branch creation, operating parameter configuration, and operational status management.
- `events.yaml`: Domain events emitted on branch creation, configuration updates, and status changes.
- `openapi-source.yaml`: Source OpenAPI contract definitions for `/api/platform/branches`.
- `permissions.yaml`: IAM permission definitions (`branch:create`, `branch:read`, `branch:manage_schedule`, etc.).
- `ui-model.yaml`: Web UI screens for branch management in employee portal.
- `mobile-model.yaml`: Mobile view models and branch discovery screens.
- `test-model.yaml`: Acceptance, contract, and unit test specifications.
- `observability-model.yaml`: Operational metrics and structured logging guidelines.
- `generation-plan.yaml`: Generator targets vs. custom business logic boundaries.
- `traceability.yaml`: Mapping matrix connecting BCM-ORG-003 to requirements, rules, endpoints, and QA evidence.

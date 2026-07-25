# Branch Management Capability Package (`BCM-ORG-003`)

## Overview

The **Branch Management** capability package governs physical branch location setup, operating schedules, reception and sample collection readiness, contact details, and operational status management under laboratory administration for Healthcare Operations Platform.

## Package Models

- `capability-package.md`: Package identity, scope, dependencies, and surface definitions.
- `business-model.md`: Aggregate definitions for `Branch` (AGG-003), `BranchAddress`, `BranchCapacityConfig`, and `BranchSchedule`.
- `business-rules.md`: Enforcement rules for branch code uniqueness, address completeness, parent laboratory status constraints, and branch quotas.
- `processes.md`: Business processes for physical branch creation, operating parameter configuration, and operational status management.
- `events.md`: Domain events emitted on branch creation, configuration updates, and status changes.
- `openapi-source.md`: Source OpenAPI contract definitions for `/api/platform/branches`.
- `permissions.md`: IAM permission definitions (`branch:create`, `branch:read`, `branch:manage_schedule`, etc.).
- `ui-model.md`: Web UI screens for branch management in employee portal.
- `mobile-model.md`: Mobile view models and branch discovery screens.
- `test-model.md`: Acceptance, contract, and unit test specifications.
- `observability-model.md`: Operational metrics and structured logging guidelines.
- `generation-plan.md`: Generator targets vs. custom business logic boundaries.
- `traceability.md`: Mapping matrix connecting BCM-ORG-003 to requirements, rules, endpoints, and QA evidence.

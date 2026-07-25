# NFR-CATALOG-001 — Non-Functional Requirements

Total NFRs: **60**

## Security

- **NFR-001** — OAuth2/OIDC authentication must be supported.
- **NFR-002** — All protected operations must enforce RBAC/ABAC permissions.
- **NFR-003** — Sensitive data must be encrypted in transit and at rest.
- **NFR-004** — Secrets must never be committed to the repository.
- **NFR-005** — Audit logs must capture security-relevant events.

## Privacy

- **NFR-006** — The platform must enforce least-privilege access to patient data.
- **NFR-007** — AI context packages must minimize sensitive data exposure.
- **NFR-008** — Consent and privacy-related actions must be auditable.
- **NFR-009** — Country-specific privacy rules must be supported through country packs.
- **NFR-010** — Data export must respect authorization and retention policies.

## Performance

- **NFR-011** — Employee portal common actions should respond within 2 seconds under normal load.
- **NFR-012** — Search endpoints must support pagination and filtering.
- **NFR-013** — Long-running jobs must be asynchronous.
- **NFR-014** — Large exports/imports must not block interactive workflows.
- **NFR-015** — Mobile interactions must be optimized for low-resource devices.

## Availability

- **NFR-016** — Core MVP1 workflows must tolerate transient infrastructure failures where possible.
- **NFR-017** — Background jobs must be retryable and idempotent.
- **NFR-018** — Local and on-prem deployments must be supported.
- **NFR-019** — Backups and restore procedures must be documented.
- **NFR-020** — Critical operations must have observable failure states.

## Accessibility

- **NFR-021** — Web interfaces must follow WCAG-aligned accessibility practices.
- **NFR-022** — Forms must support keyboard navigation.
- **NFR-023** — Validation errors must be clear and localized.
- **NFR-024** — Low-vision-friendly contrast must be supported.
- **NFR-025** — Mobile UI must support common device accessibility features.

## Internationalization

- **NFR-026** — All user-facing labels must be externalized.
- **NFR-027** — Dates, currencies, addresses and phone numbers must be locale-aware.
- **NFR-028** — Country packs must configure fiscal, regulatory and identification differences.
- **NFR-029** — Spanish and English must be supported from the start.
- **NFR-030** — The model must not hardcode country-specific assumptions.

## Portability

- **NFR-031** — Docker Compose must support local development.
- **NFR-032** — Docker Swarm and Kubernetes deployment paths must remain possible.
- **NFR-033** — The product must avoid direct dependency on one cloud provider.
- **NFR-034** — Infrastructure adapters must abstract object storage, messaging and identity providers.
- **NFR-035** — On-premise operation must remain supported.

## Observability

- **NFR-036** — OpenTelemetry must be used for traces and metrics.
- **NFR-037** — Structured logs must be generated for backend services.
- **NFR-038** — Audit logs must be separate from operational logs.
- **NFR-039** — Health checks must exist for deployable units.
- **NFR-040** — Business KPIs must be observable where defined.

## Interoperability

- **NFR-041** — OpenAPI must define APIs before implementation.
- **NFR-042** — ASTM, HL7, FHIR and DICOM must be handled through adapters.
- **NFR-043** — External integrations must not bypass domain validation.
- **NFR-044** — Webhooks must be signed and auditable when used.
- **NFR-045** — Integration failures must be traceable and retryable.

## AI Governance

- **NFR-046** — AI outputs must be treated as recommendations unless explicitly approved by governance.
- **NFR-047** — Clinical AI functions require human review where patient impact exists.
- **NFR-048** — AI providers must be abstracted.
- **NFR-049** — AI usage, prompt context and decisions must be logged.
- **NFR-050** — Fallback behavior must exist when AI is unavailable.

## Data Migration

- **NFR-051** — CSV, XLSX, JSON, XML and database export formats should be supported through the migration framework.
- **NFR-052** — Imports must be validated before execution.
- **NFR-053** — Migration jobs must be idempotent and auditable.
- **NFR-054** — Reconciliation reports must compare source and destination counts.
- **NFR-055** — Legacy data must enter through the Universal Import Model.

## Compliance

- **NFR-056** — Traceability must exist for clinical and administrative actions.
- **NFR-057** — Retention policies must be configurable.
- **NFR-058** — Quality and audit evidence must be preserved.
- **NFR-059** — Regulatory variation must be modeled through country packs.
- **NFR-060** — Compliance-related changes require review.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: NFR-CATALOG-001
  type: non-functional-requirements-catalog
  version: 1.0.0
  status: approved
summary:
  count: 60
  categories:
  - Security
  - Privacy
  - Performance
  - Availability
  - Accessibility
  - Internationalization
  - Portability
  - Observability
  - Interoperability
  - AI Governance
  - Data Migration
  - Compliance
requirements:
- id: NFR-001
  category: Security
  statement: OAuth2/OIDC authentication must be supported.
  status: approved
- id: NFR-002
  category: Security
  statement: All protected operations must enforce RBAC/ABAC permissions.
  status: approved
- id: NFR-003
  category: Security
  statement: Sensitive data must be encrypted in transit and at rest.
  status: approved
- id: NFR-004
  category: Security
  statement: Secrets must never be committed to the repository.
  status: approved
- id: NFR-005
  category: Security
  statement: Audit logs must capture security-relevant events.
  status: approved
- id: NFR-006
  category: Privacy
  statement: The platform must enforce least-privilege access to patient data.
  status: approved
- id: NFR-007
  category: Privacy
  statement: AI context packages must minimize sensitive data exposure.
  status: approved
- id: NFR-008
  category: Privacy
  statement: Consent and privacy-related actions must be auditable.
  status: approved
- id: NFR-009
  category: Privacy
  statement: Country-specific privacy rules must be supported through country packs.
  status: approved
- id: NFR-010
  category: Privacy
  statement: Data export must respect authorization and retention policies.
  status: approved
- id: NFR-011
  category: Performance
  statement: Employee portal common actions should respond within 2 seconds under
    normal load.
  status: approved
- id: NFR-012
  category: Performance
  statement: Search endpoints must support pagination and filtering.
  status: approved
- id: NFR-013
  category: Performance
  statement: Long-running jobs must be asynchronous.
  status: approved
- id: NFR-014
  category: Performance
  statement: Large exports/imports must not block interactive workflows.
  status: approved
- id: NFR-015
  category: Performance
  statement: Mobile interactions must be optimized for low-resource devices.
  status: approved
- id: NFR-016
  category: Availability
  statement: Core MVP1 workflows must tolerate transient infrastructure failures where
    possible.
  status: approved
- id: NFR-017
  category: Availability
  statement: Background jobs must be retryable and idempotent.
  status: approved
- id: NFR-018
  category: Availability
  statement: Local and on-prem deployments must be supported.
  status: approved
- id: NFR-019
  category: Availability
  statement: Backups and restore procedures must be documented.
  status: approved
- id: NFR-020
  category: Availability
  statement: Critical operations must have observable failure states.
  status: approved
- id: NFR-021
  category: Accessibility
  statement: Web interfaces must follow WCAG-aligned accessibility practices.
  status: approved
- id: NFR-022
  category: Accessibility
  statement: Forms must support keyboard navigation.
  status: approved
- id: NFR-023
  category: Accessibility
  statement: Validation errors must be clear and localized.
  status: approved
- id: NFR-024
  category: Accessibility
  statement: Low-vision-friendly contrast must be supported.
  status: approved
- id: NFR-025
  category: Accessibility
  statement: Mobile UI must support common device accessibility features.
  status: approved
- id: NFR-026
  category: Internationalization
  statement: All user-facing labels must be externalized.
  status: approved
- id: NFR-027
  category: Internationalization
  statement: Dates, currencies, addresses and phone numbers must be locale-aware.
  status: approved
- id: NFR-028
  category: Internationalization
  statement: Country packs must configure fiscal, regulatory and identification differences.
  status: approved
- id: NFR-029
  category: Internationalization
  statement: Spanish and English must be supported from the start.
  status: approved
- id: NFR-030
  category: Internationalization
  statement: The model must not hardcode country-specific assumptions.
  status: approved
- id: NFR-031
  category: Portability
  statement: Docker Compose must support local development.
  status: approved
- id: NFR-032
  category: Portability
  statement: Docker Swarm and Kubernetes deployment paths must remain possible.
  status: approved
- id: NFR-033
  category: Portability
  statement: The product must avoid direct dependency on one cloud provider.
  status: approved
- id: NFR-034
  category: Portability
  statement: Infrastructure adapters must abstract object storage, messaging and identity
    providers.
  status: approved
- id: NFR-035
  category: Portability
  statement: On-premise operation must remain supported.
  status: approved
- id: NFR-036
  category: Observability
  statement: OpenTelemetry must be used for traces and metrics.
  status: approved
- id: NFR-037
  category: Observability
  statement: Structured logs must be generated for backend services.
  status: approved
- id: NFR-038
  category: Observability
  statement: Audit logs must be separate from operational logs.
  status: approved
- id: NFR-039
  category: Observability
  statement: Health checks must exist for deployable units.
  status: approved
- id: NFR-040
  category: Observability
  statement: Business KPIs must be observable where defined.
  status: approved
- id: NFR-041
  category: Interoperability
  statement: OpenAPI must define APIs before implementation.
  status: approved
- id: NFR-042
  category: Interoperability
  statement: ASTM, HL7, FHIR and DICOM must be handled through adapters.
  status: approved
- id: NFR-043
  category: Interoperability
  statement: External integrations must not bypass domain validation.
  status: approved
- id: NFR-044
  category: Interoperability
  statement: Webhooks must be signed and auditable when used.
  status: approved
- id: NFR-045
  category: Interoperability
  statement: Integration failures must be traceable and retryable.
  status: approved
- id: NFR-046
  category: AI Governance
  statement: AI outputs must be treated as recommendations unless explicitly approved
    by governance.
  status: approved
- id: NFR-047
  category: AI Governance
  statement: Clinical AI functions require human review where patient impact exists.
  status: approved
- id: NFR-048
  category: AI Governance
  statement: AI providers must be abstracted.
  status: approved
- id: NFR-049
  category: AI Governance
  statement: AI usage, prompt context and decisions must be logged.
  status: approved
- id: NFR-050
  category: AI Governance
  statement: Fallback behavior must exist when AI is unavailable.
  status: approved
- id: NFR-051
  category: Data Migration
  statement: CSV, XLSX, JSON, XML and database export formats should be supported
    through the migration framework.
  status: approved
- id: NFR-052
  category: Data Migration
  statement: Imports must be validated before execution.
  status: approved
- id: NFR-053
  category: Data Migration
  statement: Migration jobs must be idempotent and auditable.
  status: approved
- id: NFR-054
  category: Data Migration
  statement: Reconciliation reports must compare source and destination counts.
  status: approved
- id: NFR-055
  category: Data Migration
  statement: Legacy data must enter through the Universal Import Model.
  status: approved
- id: NFR-056
  category: Compliance
  statement: Traceability must exist for clinical and administrative actions.
  status: approved
- id: NFR-057
  category: Compliance
  statement: Retention policies must be configurable.
  status: approved
- id: NFR-058
  category: Compliance
  statement: Quality and audit evidence must be preserved.
  status: approved
- id: NFR-059
  category: Compliance
  statement: Regulatory variation must be modeled through country packs.
  status: approved
- id: NFR-060
  category: Compliance
  statement: Compliance-related changes require review.
  status: approved
```

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

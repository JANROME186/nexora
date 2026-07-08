# Validators

Validators ensure that machine-readable artifacts comply with the Nexora Meta Model.

Initial validation rules:

1. Every artifact must have `id`, `type`, `name`, `status`, `version`, and `owner`.
2. Every production-ready artifact must have at least one relation to its upstream source of truth.
3. API artifacts must reference OpenAPI contracts.
4. UI and Mobile artifacts must reference stories and design system components.
5. QA artifacts must reference rules, stories or API contracts.
6. Breaking changes require RFC and ADR references.

Future tooling may implement these validations using JSON Schema, Spectral, custom scripts or CI/CD jobs.

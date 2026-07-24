---
task_id: COM-MOD-017-BE-001
status: closed
next_step: COM-MOD-017-BE-002
---

## Status
Closed.

## Cambios Clave
Compiled BCM-PLT-011 Product Marketplace and Entitlements backend outputs: new `marketplaceentitlements`
Spring Modulith module (packagecatalog, commercialoffers, tenantentitlements, packageinstallation,
compatibilityevaluation, billingadapter capabilities), all 21 openapi-source.yaml operations functional
with no endpoint responding unimplemented. New schema `db/product-marketplace-and-entitlements/schema.sql`
(6 tables), 4 new IAM `PermissionCode` screens plus `MARKETPLACE_OPERATOR`/`TENANT_ADMIN` roles, 16
`marketplace.error.*` i18n keys (default/es-MX/en-US). 60 new tests (per-capability unit tests, a
full-lifecycle API test, a real-Postgres local-database test); backend coverage raised from the 84.25%
floor to a reproducible 84.53% (442 tests, 0 failures/errors/skipped, Docker Compose PostgreSQL 16 up).
Found and fixed 2 real SpotBugs `IMPROPER_UNICODE` findings introduced by this item's own new code.
Mandatory gates run and clean: Maven verify, checkstyle, PMD, SpotBugs, OWASP Dependency-Check (72 deps/
0 vulns), Trivy fs (vuln/secret/misconfig, all severities, 0 findings), YAML parse (1,369 files/0 errors),
git diff --check.

## Deuda Técnica Creada
TD-BE-018 (open, non-blocking): the five `custom_implementation_points` named by
`generation-plan.yaml` (central entitlement policy evaluator, compatibility evaluation strategy,
billing provider adapter boundary, installation rollback orchestration, runtime feature-availability
integration with IAM/menu generation) were compiled at a basic, correct level sufficient for every
generated endpoint to function, not their full modeled sophistication. Targeted at COM-MOD-017-BE-002,
matching the BE-001/BE-002 maturation pattern already used for MVP-MOD-005's fiscal adapter and
MVP-MOD-008's integration adapter.

## Siguiente Paso
Run `COM-MOD-017-BE-002` to implement the BCM-PLT-011 custom rules named by TD-BE-018: the full
entitlement-policy.yaml evaluation_order, the remaining compatibility.yaml dimensions, a more mature
billing adapter boundary (retry/idempotency without becoming a domain source of truth), a real
multi-step installation rollback audit trail, and a first runtime feature-availability check wired
into IAM permission evaluation or employee-portal menu generation.

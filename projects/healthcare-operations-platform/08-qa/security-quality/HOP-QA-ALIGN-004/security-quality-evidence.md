# HOP-QA-ALIGN-004 Security Quality Evidence

Human-readable companion for `security-quality-evidence.yaml`.

All-severity dependency, filesystem, secret, misconfiguration and DAST scans passed. **Status:
passed.**

## Open source first

Two new dependencies were introduced, both to make DAST possible and both scoped to a patched,
minimal footprint:

- `springdoc-openapi-starter-webmvc-api` 3.0.3 (Apache-2.0) — exposes `GET /v3/api-docs`; the
  `-api` artifact was chosen specifically because it does not bundle swagger-ui/DOMPurify.
- Classic Jackson 2.x (`jackson-databind`/`jackson-core` 2.22.1, `jackson-annotations` 2.22,
  Apache-2.0) pinned via `dependencyManagement` to patch a CVE in the version swagger-core pulls
  in independently of Spring Boot 4's Jackson 3.x management.

## Vulnerabilities found and fixed

| CVE(s) | Component | Fix |
| --- | --- | --- |
| CVE-2026-54515 (CVSS 5.3) | jackson-databind 2.21.4 | Pinned to 2.22.1 |
| CVE-2026-41238/41239/41240/49458/49459/49978 (up to CVSS 6.9) | DOMPurify 3.3.2 (bundled in swagger-ui) | Switched to the `-api` springdoc artifact, which never bundles swagger-ui |

## Application defects found and fixed

| Defect | Endpoint(s) | Fix |
| --- | --- | --- |
| Unvalidated tenantId reaching a length-constrained audit column → unhandled 500 | `POST /api/people/persons/index/rebuild` | Validate tenant exists first → clean 404 |
| Unvalidated name reaching a length-constrained column → unhandled 500 | `POST /api/platform/tenants` and laboratory/branch creation | Validate name length (≤180 chars) → clean 400 |
| Missing security response headers | All backend and employee-portal routes | New `SecurityHeadersFilter` (backend); new Vite dev-server middleware (employee portal) |

## Residual findings — accepted risk

| Finding | Risk | Debt | Owner | Expiration |
| --- | --- | --- | --- | --- |
| CSP / Cross-Origin-Embedder-Policy not set (employee portal dev server) | Medium / Low | TD-FE-005 | frontend platform team | Before employee portal's first production deployment |
| Malformed empty-key query parameter → 500 on `POST /api/platform/tenants` | Low | TD-QA-004 | backend platform team | Next backend web-infrastructure touch |

## Technical debt

Closed: TD-QA-001 (DAST automation), TD-QA-002 (Trivy currency).

Newly registered: TD-FE-005, TD-QA-004.

Unchanged, out of this item's scope: TD-APP-002 (mobile coverage baseline).

## Readiness decision

Security quality status: **passed**. Ready for HOP-QA-ALIGN-005.

# HOP-QA-ALIGN-004 Validation

Human-readable companion for `HOP-QA-ALIGN-004-validation.yaml`.

All-severity dependency, filesystem, secret, misconfiguration and DAST checks were executed
against the fully running local stack and passed. **HOP-QA-ALIGN-004 is closed.**

## Result summary

| Gate | Result | Notes |
| --- | --- | --- |
| Backend dependency scan (OWASP Dependency-Check, all severities) | passed | 0 vulnerabilities, after fixing 2 CVEs (see below) |
| Frontend dependency audit (`npm audit --audit-level=low`) | passed | 0 vulnerabilities, 603 dependencies |
| Trivy filesystem/secret/misconfig scan | passed | 0 findings; Trivy upgraded 0.69.2 → 0.72.0 (closes TD-QA-002) |
| OWASP ZAP baseline scan (employee portal) | passed | 0 FAIL, 4 WARN (2 accepted-risk debt, 2 informational) |
| OWASP ZAP API scan (backend) | passed | 0 FAIL, 1 WARN (accepted-risk debt) |
| Backend tests | passed | 77 tests, 0 failures |
| Frontend tests | passed | 18 tests, 0 failures |
| Mobile tests | passed | 8 tests, 0 failures |
| Backend line coverage | passed | 66.43% (baseline 65.82%, no regression) |
| Frontend line coverage | passed | 72.89% (baseline 72.89%, no regression) |

## What DAST actually required fixing

Getting the OWASP ZAP scans running at all, and then acting on what they found, was most of this
backlog item's work:

1. **The backend had no OpenAPI document to scan.** Added `springdoc-openapi-starter-webmvc-api`
   (not the `-ui` variant, to avoid bundling swagger-ui's vulnerable DOMPurify) so
   `GET /v3/api-docs` exists.
2. **That addition introduced 2 real CVEs**, caught by the very next dependency-check run:
   `jackson-databind` (pulled by swagger-core independently of Spring Boot 4's Jackson 3.x
   management) and DOMPurify (would have shipped with `-ui`). Fixed by choosing the `-api`
   artifact and pinning the classic Jackson 2.x line via `dependencyManagement`.
3. **Docker Desktop's `--network host` does not reach the Windows host.** Switched to
   `--add-host=host.docker.internal:host-gateway` and targeted
   `http://host.docker.internal:<port>`.
4. **Vite's dev server rejected that Host header (403).** Added `allowedHosts` to
   `vite.config.ts`.
5. **ZAP's baseline scan then found 5 real missing security headers** across both surfaces
   (`X-Frame-Options`, `X-Content-Type-Options`, `Referrer-Policy`, `Permissions-Policy`,
   `Cross-Origin-Opener-Policy`, `Cross-Origin-Resource-Policy`). All fixed: a Vite dev-server
   middleware for the employee portal, a new `SecurityHeadersFilter` for the backend.
6. **ZAP's API scan found two genuine unhandled-500 defects**, both fixed:
   - `POST /api/people/persons/index/rebuild` never checked that `tenantId` was a real tenant
     before writing it into `audit_events.tenant_id` (`varchar(36)`); a long fuzzed value caused
     an unhandled `DataIntegrityViolationException`. Now validates tenant existence first and
     returns a clean 404.
   - `POST /api/platform/tenants` (and laboratories/branches, which share the same code path) had
     no max-length check on `name` against the `varchar(180)` column. Now validates length and
     returns a clean 400.

## Residual findings, formally dispositioned

- **CSP and Cross-Origin-Embedder-Policy** on the employee portal (Medium and Low risk): a
  production-strength policy would break Vite's eval-based HMR; a permissive dev-only policy would
  just fake the scanner. Deferred to the production hosting layer, which doesn't exist yet.
  Tracked as **TD-FE-005**, owner frontend/platform team, must close before first production
  deployment.
- **One malformed empty-key query parameter causing a 500** on `POST /api/platform/tenants` (Low
  risk, high confidence, no information disclosure): a Tomcat parameter-parsing edge case distinct
  from the two defects above, which are fully fixed. Tracked as **TD-QA-004**, owner backend
  platform team, next backend web-infrastructure touch.
- Two employee-portal findings (`Modern Web Application`, `Storable but Non-Cacheable Content`)
  are informational only — no risk, no action required.
- Mobile coverage baseline establishment remains blocked by how `mobile-app` reuses
  `employee-portal`'s installed toolchain (sibling `node_modules` isn't part of Node's module
  resolution chain for the coverage provider). This is pre-existing **TD-APP-002**, not part of
  this backlog item's required scope.

## Technical debt disposition

- **Closed:** TD-QA-001 (DAST now runs), TD-QA-002 (Trivy upgraded).
- **New:** TD-FE-005 (CSP/COEP), TD-QA-004 (malformed-parameter 500).
- **Unchanged:** TD-APP-002 (mobile coverage baseline, out of this item's scope).

## Readiness decision

HOP-QA-ALIGN-004 is **closed**. The next backlog item is HOP-QA-ALIGN-005 (message
externalization and magic-string remediation baseline). MVP-MOD-004-FE-001 remains paused until
HOP-QA-ALIGN-CLOSEOUT.

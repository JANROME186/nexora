# Security Quality Evidence — COM-MOD-009-PORTAL-002 Doctor Portal Commercial Workflow Compilation

**Status:** passed
**Backlog item:** COM-MOD-009-PORTAL-002
**Module:** COM-MOD-009 Patient and Doctor Portals
**Standard:** Open Source First Security Quality Standard

## Summary

This backlog item rebuilt the doctor portal commercial workflow: login flow, permission-filtered
dynamic navigation, localized screens (Patients/Results/Orders/Notifications), and new backend
least-privilege enforcement (doctorId-filtered orders, a referring-doctor authorization port for
results history, three new interceptor self-access blocks).

## Verification Checklist

| Security Check | Status | Details |
| --- | --- | --- |
| Tests Execution | **passed** | 30 doctor-portal Vitest tests and 280 backend Maven tests, 0 failures/errors/skipped. |
| Message Externalization | **passed** | Doctor-domain es-MX/en-US catalogs replace a stale employee-portal-domain catalog; no hardcoded visible strings remain. |
| Permission / Dynamic Menu | **passed** | Navigation tabs derived at render time from `permissionsForRoles`; backend `RolePermissionCatalog` grants match the frontend model 1:1. |
| Secrets Scan | **passed** | Checked code and configuration for plaintext credentials; 0 findings. |
| Quality Tools (npm) | **passed** | Clean typecheck, lint (0 errors), build, jscpd duplication, format, license checks. |
| Quality Tools (Maven) | **passed** | `mvn -Pquality "-Dhop.local-db-tests=true" clean verify` passed; Spring Modulith boundary re-verified. |
| Coverage baseline (doctor-portal) | **passed** | Line coverage rose from 40.62% to **89.86%**, exceeding the 80% target floor (TD-FE-009 closed). |
| Coverage baseline (backend) | **passed** | Line coverage rose from 80.49% to **80.60%**, no regression. |
| Agent-Agnostic Scan | **passed** | Checked for vendor-specific agent/runtime dependencies; 0 findings. |
| Stale Pointers Sweep | **passed** | Active backlog trackers now point to `COM-MOD-009-QA-001`. |
| Vulnerability Scan (npm audit) | **passed** | 0 vulnerabilities in doctor-portal dependencies. |
| Vulnerability Scan (OWASP Dependency-Check) | **passed** | 0 vulnerabilities across 65 backend dependencies. |
| Vulnerability Scan (Trivy) | **passed** | 0 vulnerabilities/secrets/misconfigurations, backend and doctor-portal. |

## Technical Debt Remediation

- **TD-FE-009 (Doctor Portal Coverage Baseline):** Closed. Doctor portal coverage raised from
  40.62% to 89.86%, exceeding the final closure target.
- **TD-IAM-002 (Permission Granularity Gap):** Materially reduced further with 2 new granular
  permission codes and real per-request ownership enforcement.
- **TD-I18N-002 (Full Localization Adoption):** Materially reduced further by replacing the
  doctor-portal's wrong-domain locale catalog with a complete, correct one.
- **TD-FE-011 (new):** Registered — patient-portal's `npm run lint` currently fails with 2
  pre-existing `sonarjs/no-hardcoded-passwords` errors, unrelated to this backlog item and out
  of scope on a closed deliverable. Not blocking; not hidden.

## Commercial Readiness Disclosure

- HOP is not commercially complete or GA-ready.
- Next backlog focus: `COM-MOD-009-QA-001` (Channel access and privacy evidence).

# COM-MOD-009-DEF Validation — Patient and Doctor Portals Capability Package Models

**Status:** passed
**Backlog item:** COM-MOD-009-DEF
**Module:** COM-MOD-009 Patient and Doctor Portals
**Code implemented:** No — this is a definition-only backlog item.

## Scope

Modeled the new Business Capability Package for `BCM-PLT-001 Identity and Access Management` (IAM) and updated 8 existing capability packages to fully support Patient and Doctor Portals scope (authentication, dynamic menus, session context, audits, support impersonation, localization preferred locales, and privacy controls).

| Capability | Bounded context | Primary aggregate |
| --- | --- | --- |
| BCM-PLT-001 Identity and Access Management | `identity-access` | `UserAccount` (AGG-004) |

Each of the affected packages has been transitioned in terms of roadmap context, surface requirements (`patient_portal`, `doctor_portal`, `mobile_app` set to `required`), and UI/mobile models.

## Key modeling decisions

- **BCM-PLT-001** governs authorization and authentication for portals. It maps new roles (`PATIENT`, `REFERRING_DOCTOR`) and defines a deny-by-default behavior.
- **Support Impersonation Panel** (SCR-IAM-001-03) defines a secure assistance framework: it requires active consent/ticket registration, audits the action, and dynamically restricts permissions to exclude high-risk clinical mutations (e.g. results signing, prescription release, or payment processing).
- **Localization preferred locales** (es-MX, en-US) are tied directly to the session/login context and dynamic menu filtering.

## Validations

All definition validation checks were successfully executed:
- **Required-artifact completeness:** All 14 files for the new `bcm-plt-001` package exist and conform to standards.
- **YAML parse check:** No syntax errors across all touched and created YAML files.
- **Agent-agnostic check:** Verified zero references to LLM assistants or vendor-specific platforms.
- **Secrets scan:** Checked and confirmed zero plaintext passwords or secrets in the models.
- **Stale pointers sweep:** Advanced active backlog trackers to `COM-MOD-009-BE-001`.

## Debt-first review

- **TD-FE-008 & TD-FE-009** (Patient & Doctor portal test coverage floors): The upcoming implementation backlog items will target the 80% coverage goal. These models serve as the technical basis for generating the required TypeScript components and API wrappers.
- **TD-IAM-002** (IAM permission granularity): Screen permissions (`PORTAL_PATIENT_ACCESS`, `PORTAL_DOCTOR_ACCESS`) are explicitly introduced to restrict access to portal screens.

## Readiness

- COM-MOD-009-DEF: **closed**
- Next backlog item: **COM-MOD-009-BE-001** (Backend compilation of portal access structures)
- HOP commercially complete / GA-ready: **No**
- Coverage baselines: backend 78.51%, employee portal 85.50%, mobile 98.87%, patient portal 41.93%, doctor portal 40.62%.

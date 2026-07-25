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

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: COM-MOD-009-DEF-VALIDATION
  type: qa-validation-evidence
  backlog_item: COM-MOD-009-DEF
  status: passed
  created_date: 2026-07-19
  standard: ../../../../nexora-framework/02-standards/standards/capability-package-standard.md
scope_note: 'COM-MOD-009-DEF is a definition-only backlog item: it creates capability
  package models under 01-product-definition/business-capabilities/packages/ for BCM-PLT-001,
  updates 8 affected capability packages, updates capability-package-index.md, and
  updates project/backlog registries. No backend, frontend or mobile code was created
  or modified. Backend/frontend/mobile build, test, coverage, SAST, dependency vulnerability
  and DAST gates therefore do not apply to this backlog item; they are unchanged from
  their last measured values (backend 78.51%, employee portal 85.50%, mobile 98.87%,
  patient portal 41.93%, doctor portal 40.62%) and are not regressed.

  '
validations:
- id: VAL-001
  name: Required-artifact completeness
  description: Verification that all 14 required files are present under BCM-PLT-001.
  status: passed
- id: VAL-002
  name: YAML syntax
  description: All YAML models parse without syntax errors.
  status: passed
- id: VAL-003
  name: Traceability
  description: BCM-PLT-001 traces cleanly to domain foundation UserAccount aggregate.
  status: passed
- id: VAL-004
  name: Least-privilege access rules
  description: Roles and scopes (PATIENT, REFERRING_DOCTOR) are modeled with deny-by-default
    behavior.
  status: passed
- id: VAL-005
  name: Support impersonation audit
  description: Impersonation flows enforce active consent, restrict clinical/financial
    signatures, and log to audit trails.
  status: passed
- id: VAL-006
  name: Localization
  description: Mexican es-MX and US en-US preferred locales are mapped for dynamic
    dynamic menus and error translations.
  status: passed
results:
  yaml_parse:
    files_checked: 24
    detail: All capability packagestouched parse without syntax errors.
  agent_agnostic_scan:
    pattern: vendor-specific agent/runtime references
    matches_found: 0
    detail: No named-agent or vendor-runtime dependency found in any new or touched
      artifact.
  secrets_scan:
    pattern: password|secret|api[_-]?key|private[_-]?key|token\s*[:=]
    matches_found: 0
    detail: No credential-shaped literal found.
readiness:
  status: passed
  ready_for_next_backlog_item: COM-MOD-009-BE-001
```

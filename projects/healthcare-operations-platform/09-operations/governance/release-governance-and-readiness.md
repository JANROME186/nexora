# HOP Release Governance and Release Readiness Checklist

## Overview

This specification defines the release taxonomy, governance gates, decision framework, and **Release Readiness Checklist** for promoting software artifacts to production environments in the **Healthcare Operations Platform (HOP)**.

It ensures that no release reaches production without meeting strict quality, security, operational, and customer enablement criteria.

---

## Release Taxonomy (Semantic Versioning)

HOP releases follow Semantic Versioning (`MAJOR.MINOR.PATCH`):
- **MAJOR (`X.0.0`)**: Major platform architecture changes, core domain model updates, or breaking API contract changes. Requires full CAB + Executive signoff.
- **MINOR (`1.X.0`)**: Addition of new Business Capability Packages (e.g., COM-MOD modules), new portal surfaces, or non-breaking API enhancements. Requires full CAB signoff.
- **PATCH (`1.0.X`)**: Bug fixes, security vulnerability patches, performance optimizations, or documentation updates. Requires Standard CAB / L2 Lead signoff.

---

## Release Readiness Gate Matrix

```
┌─────────────────────────┐      ┌───────────────────────────┐
│ 1. Quality & Test Gate  │ ───► │ 2. Security & Compliance │
└─────────────────────────┘      └───────────────────────────┘
             │                                 │
             ▼                                 ▼
┌─────────────────────────┐      ┌───────────────────────────┐
│ 3. Operations & Infra   │ ───► │ 4. Support & Enablement   │
└─────────────────────────┘      └───────────────────────────┘
                                               │
                                               ▼
                                   [ Go / No-Go Decision ]
```

### Gate 1: Quality & Automated Testing Baseline
- [ ] **Backend Maven Quality Profile**: `mvn -Pquality verify` passes with **0 test failures** and **0 skipped tests**.
- [ ] **Frontend Web Quality Suites**: `npm run quality` passes clean for Employee Portal, Patient Portal, Doctor Portal, and Public Website.
- [ ] **Mobile Quality Suite**: Vitest suite passes clean.
- [ ] **Coverage Floor Enforcement**:
  - Backend Java/Maven: $\ge 84.25\%$
  - Employee Portal Web: $\ge 89.75\%$
  - Mobile App Foundation: $\ge 99.21\%$
  - Patient Portal Web: $\ge 94.11\%$
  - Doctor Portal Web: $\ge 96.28\%$
  - Public Website: $\ge 98.61\%$

### Gate 2: Security & Supply Chain Baseline
- [ ] **Vulnerability Scans**: OWASP Dependency-Check and `npm audit` report zero unhandled High or Critical vulnerabilities.
- [ ] **Container & Filesystem Scans**: Trivy fs scan (`vuln,secret,misconfig`) runs clean across all severities.
- [ ] **DAST Validation**: OWASP ZAP baseline and API scans pass with **0 FAIL-NEW** findings on active REST surfaces.
- [ ] **Secrets & Agent-Agnostic Sweep**: No hardcoded API keys, passwords, or agent-proprietary locks.

### Gate 3: Operations & Infrastructure Baseline
- [ ] **Environment Strategy Verification**: Check `09-operations/deployment/environment-matrix.md`.
- [ ] **Reversible Schema Migrations**: Database DDL scripts validated for backward compatibility and `down` migration steps.
- [ ] **Backup & Restore Readiness**: Recent restore rehearsal verified (`09-operations/runbooks/restore-runbook.md`).
- [ ] **Health Probe Check**: `GET /actuator/health/readiness` returns `UP (200 OK)` under simulated local stack start.

### Gate 4: Support & Customer Enablement Baseline
- [ ] **Customer Guides**: Onboarding documentation (`09-operations/onboarding/`) up-to-date.
- [ ] **Bilingual Release Notes**: Release notes authored and verified in both `es-MX` and `en-US`.
- [ ] **Support Training**: L1/L2 support staff briefed on new capabilities and operational workarounds.

---

## Go / No-Go Decision Framework

1. **Participants**: Product Owner, Architecture Lead, QA/Security Lead, Operations Lead.
2. **Voting Policy**:
   - **Major / Minor Release**: **Unanimous (4 of 4)** consensus required.
   - **Patch Release**: **3 of 4** consensus required.
3. **Veto Conditions**: Any open P1 security vulnerability, failing automated test, or unhandled coverage regression automatically triggers a **NO-GO** decision.

---

## Traceability & Standards

- **Capabilities**: [BCM-ORG-001](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-org-001-tenant-management/capability-package.md), [BCM-PLT-002](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-002-platform-configuration/capability-package.md), [BCM-PLT-006](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-006-observability/capability-package.md), [BCM-PLT-007](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/capability-package.md), [BCM-PLT-008](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/capability-package.md)
- **Deployment Strategy**: Integrated with [production-deployment-strategy.md](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/09-operations/deployment/production-deployment-strategy.md).
- **Agent-Agnostic**: Yes
- **Open-Source-First**: Yes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GOV-REL-001
  type: operational-governance-specification
  name: HOP Release Governance & Release Readiness Checklist Specification
  version: 1.0.0
  status: approved
  human_readable: release-governance-and-readiness.md
  machine_readable: release-governance-and-readiness.md
  backlog_item: COM-MOD-016-OPS-001
  created_date: 2026-07-24
  owner: HOP Product Architecture & Release Governance Team
project:
  name: Healthcare Operations Platform
  slug: healthcare-operations-platform
  module: COM-MOD-016
  release: REL-003
release_taxonomy:
  semantic_versioning: MAJOR.MINOR.PATCH
  release_types:
  - type: Major
    description: Significant architectural milestone, breaking API changes, or core
      framework migration.
    frequency: Bi-annually or annual
    cab_level: Executive + CAB
  - type: Minor
    description: New business capability packages, feature modules, or portal workflow
      additions.
    frequency: Monthly
    cab_level: Full CAB
  - type: Patch
    description: Bug fixes, performance optimizations, security dependency updates,
      or documentation patches.
    frequency: Bi-weekly or on-demand
    cab_level: Standard CAB / L2 Lead
release_readiness_checklist:
  quality_and_testing_gate:
  - criterion: All backend unit, integration, and local database tests pass with zero
      failures or skips.
    evidence_required: Maven verify log output with clean 0 failures.
  - criterion: All web employee portal, public website, patient portal, and doctor
      portal tests pass.
    evidence_required: npm run quality / vitest execution reports.
  - criterion: Mobile application foundation quality suite passes.
    evidence_required: Vitest coverage report >= 99.21%.
  - criterion: Stack coverage floors preserved across all 6 surfaces.
    evidence_required: backend >= 84.25%, employee_portal >= 89.75%, mobile >= 99.21%,
      patient_portal >= 94.11%, doctor_portal >= 96.28%, public_website >= 98.61%.
  security_and_compliance_gate:
  - criterion: Dependency vulnerability scan reports zero unhandled High/Critical
      findings.
    evidence_required: OWASP Dependency-Check & npm audit reports.
  - criterion: Container and filesystem security scan clean across all severities.
    evidence_required: Trivy fs scan report (vuln, secret, misconfig).
  - criterion: OWASP ZAP DAST scan executed against runnable API and web surfaces
      with 0 FAIL-NEW findings.
    evidence_required: OWASP ZAP baseline and API scan report.
  - criterion: Hardcoded secrets scan executed clean across repository.
    evidence_required: Git secrets sweep output.
  operations_and_infrastructure_gate:
  - criterion: Deployment strategy updated and environment matrix verified.
    evidence_required: 09-operations/deployment/production-deployment-strategy.md
  - criterion: Database migrations validated for backward compatibility and rollback
      execution.
    evidence_required: Liquibase / Flyway DDL verification log.
  - criterion: Backup and restore rehearsal executed within last 30 days.
    evidence_required: 09-operations/runbooks/backup-runbook.md & restore-runbook.md
      evidence.
  - criterion: Actuator health and Prometheus metrics endpoints active.
    evidence_required: GET /actuator/health/readiness returning UP.
  support_and_enablement_gate:
  - criterion: Customer onboarding guides and configuration references updated.
    evidence_required: 09-operations/onboarding/ guides verified.
  - criterion: Release notes published in es-MX and en-US locales.
    evidence_required: Release notes artifact under documentation directory.
  - criterion: L1/L2 support teams trained on new capability features and operational
      workarounds.
    evidence_required: Support signoff in handoff checklist.
go_no_go_decision_framework:
  decision_roles:
  - Product Owner
  - Architecture Lead
  - Quality Assurance Lead
  - Operations Lead
  voting_rule: Unanimous consensus required for Major/Minor release promotion; 3 of
    4 for Patch release.
traceability:
  capabilities:
  - BCM-ORG-001
  - BCM-PLT-002
  - BCM-PLT-006
  - BCM-PLT-007
  - BCM-PLT-008
  standards_compliance:
    agent_agnostic: true
    open_source_first: true
    no_proprietary_agent_dependencies: true
```

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
- [ ] **Environment Strategy Verification**: Check `09-operations/deployment/environment-matrix.yaml`.
- [ ] **Reversible Schema Migrations**: Database DDL scripts validated for backward compatibility and `down` migration steps.
- [ ] **Backup & Restore Readiness**: Recent restore rehearsal verified (`09-operations/runbooks/restore-runbook.yaml`).
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

- **Capabilities**: [BCM-ORG-001](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-org-001-tenant-management/capability-package.yaml), [BCM-PLT-002](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-002-platform-configuration/capability-package.yaml), [BCM-PLT-006](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-006-observability/capability-package.yaml), [BCM-PLT-007](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/capability-package.yaml), [BCM-PLT-008](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/capability-package.yaml)
- **Deployment Strategy**: Integrated with [production-deployment-strategy.yaml](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/09-operations/deployment/production-deployment-strategy.yaml).
- **Agent-Agnostic**: Yes
- **Open-Source-First**: Yes

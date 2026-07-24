# HOP Operational Acceptance Criteria (OAC) Specification

## Purpose

The **Operational Acceptance Criteria (OAC)** define the mandatory non-functional and operational quality gates that every module, feature, or service must pass before promotion to production in the **Healthcare Operations Platform (HOP)**.

Passing OAC guarantees that new software is deployable, monitorable, supportable, secure, resilient, and backed by complete documentation.

---

## OAC Category Matrix

```
┌───────────────────────────────────┐      ┌───────────────────────────────────┐
│ 1. Observability Instrumentation │ ───► │ 2. Runbook & Documentation       │
└───────────────────────────────────┘      └───────────────────────────────────┘
                  │                                          │
                  ▼                                          ▼
┌───────────────────────────────────┐      ┌───────────────────────────────────┐
│ 3. Data Protection & Rehearsal    │ ───► │ 4. Security & Audit Compliance    │
└───────────────────────────────────┘      └───────────────────────────────────┘
                  │                                          │
                  ▼                                          ▼
┌───────────────────────────────────┐      ┌───────────────────────────────────┐
│ 5. Performance & Resilience       │ ───► │ 6. Support & Enablement Signoff  │
└───────────────────────────────────┘      └───────────────────────────────────┘
```

### 1. Observability & Telemetry Instrumentation
- [ ] **MDC Trace Context**: Every log line populates `traceId`, `tenantId`, and `userId` context.
- [ ] **Prometheus Metrics**: HTTP request counters, latency histograms, JVM memory, and DB connection pool metrics exported at `/actuator/prometheus`.
- [ ] **Health Probes**: Liveness (`/actuator/health/liveness`) and Readiness (`/actuator/health/readiness`) health groups configured and functional.

### 2. Runbook & Operational Documentation
- [ ] **Executable Runbooks**: Machine-readable `.yaml` and companion `.md` runbooks exist under `09-operations/runbooks/`.
- [ ] **Command Alignment**: Executable commands verified against `compose.local.yml`, environment configuration files, and `local-toolchain-inventory.yaml`.
- [ ] **Clear Failure Recovery**: Prerequisites, inputs, required IAM roles, and step-by-step failure recovery procedures documented.

### 3. Data Protection & Recovery Rehearsal
- [ ] **Backup Verification**: Automated PostgreSQL backup script verified with SHA-256 integrity checksums.
- [ ] **Restore Rehearsal**: Successful restore rehearsal into an isolated database instance within the last 30 days (`restore-runbook.yaml`).
- [ ] **Privacy & Masking**: Sensitive patient profiles and document identification fields masked according to tenant privacy policy.

### 4. Security & Audit Compliance
- [ ] **Permission Mapping**: Every REST endpoint mapped to an explicit `PermissionCode` in `EndpointPermissionRegistry`.
- [ ] **Audit Trail Recording**: Mutating business actions emit an append-only `AuditEvent` record (`BCM-PLT-007`).
- [ ] **Security Scans Passed**: Trivy filesystem scan, OWASP Dependency-Check, and OWASP ZAP DAST run clean without unhandled High/Critical vulnerabilities.

### 5. Performance & System Resilience
- [ ] **Latency Compliance**: API p95 latency $\le 200\text{ ms}$ under standard operational load.
- [ ] **Database Outage Resilience**: Simulated database disconnect correctly transitions readiness probe to `DOWN (503)`, and service recovers automatically when connectivity is restored.
- [ ] **Error Handling**: Graceful error envelopes (`code` + `messageKey`) returned for non-500 client errors.

### 6. Support Enablement & Handoff Signoff
- [ ] **Support Briefing**: L1 Helpdesk and L2 Support personnel trained on new capability features and operational workarounds.
- [ ] **Bilingual Documentation**: User guides and release notes published in both `es-MX` and `en-US` locales.
- [ ] **Formal Signoff**: Handoff checklist signed off by Delivery Lead and Operations Manager.

---

## Traceability & Standards

- **Capabilities**: [BCM-ORG-001](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-org-001-tenant-management/capability-package.yaml), [BCM-ORG-002](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-org-002-laboratory-management/capability-package.yaml), [BCM-ORG-003](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-org-003-branch-management/capability-package.yaml), [BCM-PLT-002](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-002-platform-configuration/capability-package.yaml), [BCM-PLT-006](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-006-observability/capability-package.yaml), [BCM-PLT-007](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/capability-package.yaml), [BCM-PLT-008](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/capability-package.yaml)
- **Agent-Agnostic**: Yes
- **Open-Source-First**: Yes

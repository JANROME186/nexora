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
- [ ] **Command Alignment**: Executable commands verified against `compose.local.json`, environment configuration files, and `local-toolchain-inventory.md`.
- [ ] **Clear Failure Recovery**: Prerequisites, inputs, required IAM roles, and step-by-step failure recovery procedures documented.

### 3. Data Protection & Recovery Rehearsal
- [ ] **Backup Verification**: Automated PostgreSQL backup script verified with SHA-256 integrity checksums.
- [ ] **Restore Rehearsal**: Successful restore rehearsal into an isolated database instance within the last 30 days (`restore-runbook.md`).
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

- **Capabilities**: [BCM-ORG-001](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-org-001-tenant-management/capability-package.md), [BCM-ORG-002](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-org-002-laboratory-management/capability-package.md), [BCM-ORG-003](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-org-003-branch-management/capability-package.md), [BCM-PLT-002](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-002-platform-configuration/capability-package.md), [BCM-PLT-006](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-006-observability/capability-package.md), [BCM-PLT-007](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/capability-package.md), [BCM-PLT-008](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/capability-package.md)
- **Agent-Agnostic**: Yes
- **Open-Source-First**: Yes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GOV-OAC-001
  type: operational-governance-specification
  name: HOP Operational Acceptance Criteria (OAC) Specification
  version: 1.0.0
  status: approved
  human_readable: operational-acceptance-criteria.md
  machine_readable: operational-acceptance-criteria.md
  backlog_item: COM-MOD-016-OPS-001
  created_date: 2026-07-24
  owner: HOP Platform Engineering & Operations Assurance Team
project:
  name: Healthcare Operations Platform
  slug: healthcare-operations-platform
  module: COM-MOD-016
  release: REL-003
operational_acceptance_categories:
- category: 1_Observability_Instrumentation
  description: Verification that all new or modified REST endpoints, background jobs,
    and domain events emit trace logs, metrics, and health status indicators.
  acceptance_criteria:
  - Spring MDC populate traceId, tenantId, and userId on every request log.
  - Micrometer metrics emitted under /actuator/prometheus for HTTP requests, JVM health,
    and database connection pools.
  - Actuator liveness and readiness health groups reflect component status.
  evidence_required: Prometheus metric query logs and log snippet showing MDC fields.
- category: 2_Runbook_and_Operational_Documentation
  description: Verification that executable operational runbooks exist for all administrative,
    backup, restore, triage, and maintenance procedures.
  acceptance_criteria:
  - Executable runbook pair (.yaml and .md) exists under 09-operations/runbooks/.
  - Runbook commands cross-checked against compose files and application configuration.
  - Expected inputs, prerequisites, target roles, and failure remediation paths documented.
  evidence_required: Runbook directory index and execution command validation.
- category: 3_Data_Protection_and_Rehearsal
  description: Verification that backup, restore, data retention, and privacy controls
    operate safely.
  acceptance_criteria:
  - PostgreSQL database backup script verified (pg_dump with SHA-256 checksum).
  - Database restore rehearsal executed into an isolated test instance with matching
    row counts.
  - Sensitive patient profile and financial fields masked or restricted per RBAC permissions.
  evidence_required: Backup and restore rehearsal evidence log under 08-qa.
- category: 4_Security_and_Audit_Compliance
  description: Verification of authentication, request-time authorization, audit event
    logging, and vulnerability gates.
  acceptance_criteria:
  - Every endpoint mapped to explicit permission in EndpointPermissionRegistry.
  - Every mutating business action records an append-only audit event (AGG-018 AuditEvent).
  - Vulnerability scans (Trivy, OWASP Dependency-Check, OWASP ZAP DAST) pass without
    unhandled High/Critical findings.
  evidence_required: Security quality evidence log under 08-qa/security-quality/.
- category: 5_Performance_and_Resilience
  description: Verification that system meets SLO response times and handles failure
    modes gracefully.
  acceptance_criteria:
  - API p95 latency <= 200ms under standard operational load.
  - Readiness probe correctly reflects database disconnects (readiness transitions
    to DOWN 503).
  - Application recovers automatically when database service is restored.
  evidence_required: Resilience check log (e.g., COM-MOD-012-QA-001 evidence).
- category: 6_Support_and_Enablement_Signoff
  description: Verification that support personnel have been trained and customer
    guides are available.
  acceptance_criteria:
  - L1/L2 support staff briefed on new feature workflows and known error workarounds.
  - Onboarding and configuration guides updated under 09-operations/onboarding/.
  - Release notes published in es-MX and en-US locales.
  evidence_required: Support Handoff Checklist signoff.
traceability:
  capabilities:
  - BCM-ORG-001
  - BCM-ORG-002
  - BCM-ORG-003
  - BCM-PLT-002
  - BCM-PLT-006
  - BCM-PLT-007
  - BCM-PLT-008
  standards_compliance:
    agent_agnostic: true
    open_source_first: true
    no_proprietary_agent_dependencies: true
```

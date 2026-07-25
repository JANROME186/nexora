# HOP Sales Demo Script

## Overview

- **Duration:** 45 minutes
- **Audience:** Laboratory directors, operations managers, quality managers, and IT decision-makers
- **Environment:** HOP local stack (Docker Compose) with pre-seeded demo data

## Demo Flow

### 1. Opening (5 min)

- Introduce Nexora and the Healthcare Operations Platform vision
- Explain the multi-tenant SaaS model with self-hostable, open-source-first architecture
- Position HOP against legacy laboratory information systems

> **Key Message:** HOP is a modern, cloud-ready platform that replaces fragmented legacy systems with a unified healthcare operations platform built on open standards.

### 2. Tenant and Organization Setup (3 min)

- Show tenant provisioning via the employee portal
- Create a laboratory with branches
- Demonstrate multi-branch structure

### 3. Diagnostic Catalog Configuration (5 min)

- Navigate the diagnostic service catalog
- Show test creation with analytes and reference ranges
- Demonstrate panel composition from existing tests
- Show price list configuration and publication

### 4. Patient and Doctor Registration (3 min)

- Register a patient with duplicate detection
- Register a referring doctor with credential management
- Show person search and merge capabilities

### 5. Front Desk Workflow (5 min)

- Create an appointment
- Walk through reception and admission
- Create a diagnostic order with pricing
- Show quotation generation

### 6. Cashier and Payment (3 min)

- Open a cash session
- Register payment for the diagnostic order
- Generate a billing request
- Close the cash session with variance report

### 7. Laboratory Workflow (5 min)

- Demonstrate sample collection and labeling
- Show sample reception at the laboratory
- Process samples and capture results
- Perform technical validation followed by medical validation
- Release the result

### 8. Result Delivery (3 min)

- Generate a PDF report from the released result
- Show the result in the patient portal
- Show the result in the doctor portal
- Demonstrate critical result notification

### 9. Inventory and Quality (3 min)

- Show reagent inventory and stock movements
- Demonstrate internal quality control recording
- Show equipment and calibration management

### 10. Public Website (2 min)

- Show the public-facing service catalog
- Demonstrate appointment request from the public website
- Show request routing to the employee portal

### 11. Platform Operations (3 min)

- Show tenant management and feature flags
- Demonstrate observability (Prometheus metrics, health checks)
- Show the audit trail

### 12. Closing and Commercial (5 min)

- Summarize the complete operating cycle demonstrated
- Present the three commercial packages (Starter, Professional, Enterprise)
- Discuss the pilot program and onboarding process
- Address questions and next steps

## Common Objections and Responses

| Objection | Response |
|-----------|----------|
| How does HOP compare to established LIS vendors? | Modern open-source architecture, multi-tenant SaaS, digital channels, marketplace, no vendor lock-in |
| Can HOP handle regulatory requirements? | Enterprise tier includes external QC, CAPA, audit management; full traceability and clinical validation separation |
| What about data migration? | Dedicated BCM-PLT-010 capability with CSV/JSON/XLSX/ZIP support, dry-run validation, reconciliation reports |
| Is HOP cloud-agnostic? | Yes; Docker + PostgreSQL, no proprietary cloud dependencies |

## Related Documents

- [Demo Data Checklist](demo-data-checklist.md)
- [Sales Enablement One-Pager](sales-enablement-one-pager.md)
- [Buyer Personas](buyer-personas-and-use-cases.md)

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-SALES-DEMO-001
  type: sales-demo-script
  name: HOP Sales Demo Script
  version: 1.0.0
  status: approved
  human_readable: sales-demo-script.md
  machine_readable: sales-demo-script.md
  backlog_item: COM-MOD-016-COM-001
  created_date: 2026-07-24
  owner: Nexora Commercial Team
project:
  name: Healthcare Operations Platform
  slug: healthcare-operations-platform
  module: COM-MOD-016
  release: REL-003
demo_overview:
  duration_minutes: 45
  target_audience: Laboratory directors, operations managers, quality managers, and
    IT decision-makers.
  demo_environment: HOP local stack (Docker Compose) with pre-seeded demo data.
  objective: Demonstrate how HOP digitizes the complete diagnostic laboratory operating
    cycle from tenant setup through result delivery, with multi-channel access, inventory
    management, quality controls, and operational governance.
demo_flow:
- section: 1_Opening
  duration_minutes: 5
  talking_points:
  - Introduce Nexora and the Healthcare Operations Platform vision.
  - Explain the multi-tenant SaaS model with self-hostable, open-source-first architecture.
  - Position HOP against legacy laboratory information systems.
  key_message: HOP is a modern, cloud-ready platform that replaces fragmented legacy
    systems with a unified healthcare operations platform built on open standards.
- section: 2_Tenant_and_Organization_Setup
  duration_minutes: 3
  demo_steps:
  - Show tenant provisioning via the employee portal.
  - Create a laboratory with branches.
  - Demonstrate multi-branch structure.
  capabilities_shown:
  - BCM-ORG-001
  - BCM-ORG-002
  - BCM-ORG-003
  screen: Employee Portal - Administration
- section: 3_Diagnostic_Catalog_Configuration
  duration_minutes: 5
  demo_steps:
  - Navigate the diagnostic service catalog.
  - Show test creation with analytes and reference ranges.
  - Demonstrate panel composition from existing tests.
  - Show price list configuration and publication.
  capabilities_shown:
  - BCM-SVC-001
  - BCM-SVC-002
  - BCM-SVC-003
  - BCM-SVC-004
  - BCM-SVC-006
  - BCM-SVC-009
  screen: Employee Portal - Diagnostic Catalog
- section: 4_Patient_and_Doctor_Registration
  duration_minutes: 3
  demo_steps:
  - Register a patient with duplicate detection.
  - Register a referring doctor with credential management.
  - Show person search and merge capabilities.
  capabilities_shown:
  - BCM-PER-001
  - BCM-PER-002
  - BCM-PER-003
  screen: Employee Portal - People Management
- section: 5_Front_Desk_Workflow
  duration_minutes: 5
  demo_steps:
  - Create an appointment.
  - Walk through reception and admission.
  - Create a diagnostic order with pricing.
  - Show quotation generation.
  capabilities_shown:
  - BCM-ATT-001
  - BCM-ATT-003
  - BCM-ATT-004
  - BCM-LAB-001
  - BCM-ATT-006
  screen: Employee Portal - Front Desk
- section: 6_Cashier_and_Payment
  duration_minutes: 3
  demo_steps:
  - Open a cash session.
  - Register payment for the diagnostic order.
  - Generate a billing request.
  - Close the cash session with variance report.
  capabilities_shown:
  - BCM-ATT-005
  - BCM-ATT-008
  screen: Employee Portal - Cashier
- section: 7_Laboratory_Workflow
  duration_minutes: 5
  demo_steps:
  - Demonstrate sample collection and labeling.
  - Show sample reception at the laboratory.
  - Process samples and capture results.
  - Perform technical validation followed by medical validation.
  - Release the result.
  capabilities_shown:
  - BCM-LAB-002
  - BCM-LAB-003
  - BCM-LAB-005
  - BCM-LAB-006
  - BCM-LAB-008
  - BCM-LAB-009
  - BCM-LAB-010
  screen: Employee Portal - Laboratory
- section: 8_Result_Delivery
  duration_minutes: 3
  demo_steps:
  - Generate a PDF report from the released result.
  - Show the result in the patient portal.
  - Show the result in the doctor portal.
  - Demonstrate critical result notification.
  capabilities_shown:
  - BCM-RES-001
  - BCM-RES-002
  - BCM-RES-004
  - BCM-RES-006
  - BCM-RES-007
  screens:
  - Employee Portal - Results
  - Patient Portal
  - Doctor Portal
- section: 9_Inventory_and_Quality
  duration_minutes: 3
  demo_steps:
  - Show reagent inventory and stock movements.
  - Demonstrate internal quality control recording.
  - Show equipment and calibration management.
  capabilities_shown:
  - BCM-INV-001
  - BCM-INV-002
  - BCM-QLT-001
  - BCM-QLT-003
  - BCM-QLT-004
  screen: Employee Portal - Inventory and Quality
- section: 10_Public_Website
  duration_minutes: 2
  demo_steps:
  - Show the public-facing service catalog.
  - Demonstrate appointment request from the public website.
  - Show request routing to the employee portal.
  capabilities_shown:
  - BCM-SVC-001
  - BCM-ATT-001
  - BCM-PLT-005
  screen: Public Website
- section: 11_Platform_Operations
  duration_minutes: 3
  demo_steps:
  - Show tenant management and feature flags.
  - Demonstrate observability (Prometheus metrics, health checks).
  - Show the audit trail.
  capabilities_shown:
  - BCM-PLT-002
  - BCM-PLT-006
  - BCM-PLT-007
  screen: Employee Portal - Platform Administration
- section: 12_Closing_and_Commercial
  duration_minutes: 5
  talking_points:
  - Summarize the complete operating cycle demonstrated.
  - Present the three commercial packages (Starter, Professional, Enterprise).
  - Discuss the pilot program and onboarding process.
  - Address questions and next steps.
  key_message: HOP covers the full diagnostic laboratory lifecycle in a single, integrated
    platform with transparent pricing and a structured onboarding path.
objection_handling:
- objection: How does HOP compare to established LIS vendors?
  response: HOP is built on modern, open-source-first architecture. Unlike legacy
    LIS systems, HOP offers multi-tenant SaaS, digital channels for patients and doctors,
    built-in quality management, and a marketplace for extensions. The open architecture
    means no vendor lock-in for integrations or data migration.
- objection: Can HOP handle our regulatory requirements?
  response: HOP Enterprise includes advanced quality and compliance capabilities (external
    QC, CAPA, audit management) specifically designed for regulated laboratory environments.
    Every clinical action is traceable and auditable, and the platform enforces separation
    between technical validation, medical validation, and result release.
- objection: What about data migration from our current system?
  response: HOP includes a dedicated data migration capability (BCM-PLT-010) that
    supports CSV, JSON, NDJSON, XLSX, and ZIP formats. The migration process includes
    dry-run validation, reconciliation reports, and checkpointed resume, so you can
    validate your data before committing. This is included in the Professional and
    Enterprise packages.
- objection: Is HOP truly cloud-agnostic?
  response: Yes. HOP is built with open-source, self-hostable technologies. It runs
    on any infrastructure that supports Docker containers and PostgreSQL. There are
    no proprietary cloud provider dependencies.
open_source_first: true
agent_agnostic: true
no_proprietary_agent_dependencies: true
```

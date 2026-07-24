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

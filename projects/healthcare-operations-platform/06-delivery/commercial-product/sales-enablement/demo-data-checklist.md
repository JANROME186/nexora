# HOP Demo Data Checklist

## Overview

This checklist defines the data required to run a complete HOP sales demo using the local Docker Compose stack.

## Pre-Seeded Data (Available from Schema)

| Category | Item | Source |
|----------|------|--------|
| Tenant | Demo tenant with legal name, tax ID, professional tier | `db/platform-foundation/schema.sql` |
| Laboratory | Primary laboratory linked to demo tenant | `db/platform-foundation/schema.sql` |
| Branches | 2 branches (Central Lab, Branch Norte) | `db/platform-foundation/schema.sql` |
| Users | Admin, Front Desk, Lab Technician, Medical Validator, Cashier, Quality Manager | `db/platform-foundation/schema.sql` |
| Diagnostic Services | 3 published services (Hematology, Chemistry, Immunology) | `db/catalog-test-configuration/schema.sql` |
| Tests | 10+ published tests with analytes and reference ranges | `db/catalog-test-configuration/schema.sql` |
| Panels | 2 published panels | `db/catalog-test-configuration/schema.sql` |
| Price Lists | 1 active price list | `db/catalog-test-configuration/schema.sql` |
| Preparation Instructions | 2 linked to relevant tests | `db/catalog-test-configuration/schema.sql` |
| Localization | es-MX and en-US active | Built-in |

## Manual Setup Required

| Category | Item | Notes |
|----------|------|-------|
| Patients | 3 patient records with varied profiles | Create via API or employee portal |
| Doctors | 2 referring doctor records with credentials | Create via API or employee portal |
| Diagnostic Orders | Orders in various lifecycle states | Create during demo or warm-up |
| Inventory | 3 reagent products with lots and stock entries | Create via employee portal |
| Quality Control | 1 internal QC run with results | Create via employee portal |
| Feature Flags | 2 feature flags for toggle demo | Create via employee portal admin |

## Demo Startup

```bash
# Start the local stack
docker compose -f compose.local.json up -d

# Verify backend health
curl http://localhost:8080/actuator/health

# Access points
# Employee Portal: http://localhost:5173
# Patient Portal:  http://localhost:5174
# Public Website:  http://localhost:5176
```

## Related Documents

- [Sales Demo Script](sales-demo-script.md)
- [Local Solution Runbook](../../../09-operations/runbooks/local-solution-runbook.md)

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-DEMO-DATA-001
  type: demo-data-checklist
  name: HOP Demo Data Checklist
  version: 1.0.0
  status: approved
  human_readable: demo-data-checklist.md
  machine_readable: demo-data-checklist.md
  backlog_item: COM-MOD-016-COM-001
  created_date: 2026-07-24
  owner: Nexora Commercial Team
project:
  name: Healthcare Operations Platform
  slug: healthcare-operations-platform
  module: COM-MOD-016
  release: REL-003
demo_environment:
  runtime: Docker Compose local stack
  database: PostgreSQL 16
  runbook_reference: 09-operations/runbooks/local-solution-runbook.md
  seed_data_reference: 07-implementation/backend/src/main/resources/db/
checklist:
- category: Tenant and Organization
  items:
  - name: Demo tenant
    description: A pre-configured tenant with legal name, tax ID, and professional
      tier.
    seed_source: db/platform-foundation/schema.sql
    status: seeded_by_schema
  - name: Demo laboratory
    description: Primary laboratory record linked to the demo tenant.
    seed_source: db/platform-foundation/schema.sql
    status: seeded_by_schema
  - name: Demo branches
    description: At least 2 branches (e.g., Central Lab, Branch Norte) for multi-branch
      demo.
    seed_source: db/platform-foundation/schema.sql
    status: seeded_by_schema
- category: Users and Roles
  items:
  - name: Admin user
    description: ADMINISTRATOR role with full permissions for platform administration
      demo.
    seed_source: db/platform-foundation/schema.sql
    status: seeded_by_schema
  - name: Front desk user
    description: FRONT_DESK role for reception and order creation demo.
    seed_source: db/platform-foundation/schema.sql
    status: seeded_by_schema
  - name: Lab technician user
    description: LAB_TECHNICIAN role for sample processing and technical validation
      demo.
    seed_source: db/platform-foundation/schema.sql
    status: seeded_by_schema
  - name: Medical validator user
    description: MEDICAL_VALIDATOR role for medical validation and result release
      demo.
    seed_source: db/platform-foundation/schema.sql
    status: seeded_by_schema
  - name: Cashier user
    description: CASHIER role for payment and billing demo.
    seed_source: db/platform-foundation/schema.sql
    status: seeded_by_schema
  - name: Quality manager user
    description: QUALITY_MANAGER role for quality and compliance demo.
    seed_source: db/platform-foundation/schema.sql
    status: seeded_by_schema
- category: Diagnostic Catalog
  items:
  - name: Diagnostic services
    description: At least 3 published diagnostic services (e.g., Hematology, Chemistry,
      Immunology).
    seed_source: db/catalog-test-configuration/schema.sql
    status: seeded_by_schema
  - name: Tests
    description: At least 10 published tests with analytes and reference ranges.
    seed_source: db/catalog-test-configuration/schema.sql
    status: seeded_by_schema
  - name: Panels
    description: At least 2 published panels composed from seeded tests.
    seed_source: db/catalog-test-configuration/schema.sql
    status: seeded_by_schema
  - name: Price lists
    description: At least 1 active price list with prices for all seeded tests and
      panels.
    seed_source: db/catalog-test-configuration/schema.sql
    status: seeded_by_schema
  - name: Preparation instructions
    description: At least 2 preparation instructions linked to relevant tests.
    seed_source: db/catalog-test-configuration/schema.sql
    status: seeded_by_schema
- category: People
  items:
  - name: Demo patients
    description: At least 3 patient records with varied profiles for duplicate detection
      demo.
    seed_source: manual_via_api_or_ui
    status: manual_setup_required
  - name: Demo doctors
    description: At least 2 referring doctor records with credentials.
    seed_source: manual_via_api_or_ui
    status: manual_setup_required
- category: Operational Data
  items:
  - name: Sample diagnostic orders
    description: Pre-created orders in various lifecycle states (requested, accepted,
      in_progress, completed).
    seed_source: manual_via_api_or_ui
    status: manual_setup_required
    note: Create during demo or as pre-demo warm-up.
  - name: Inventory items
    description: At least 3 reagent products with lot numbers and stock entries.
    seed_source: manual_via_api_or_ui
    status: manual_setup_required
  - name: Quality control data
    description: At least 1 internal QC run with results for quality demo section.
    seed_source: manual_via_api_or_ui
    status: manual_setup_required
- category: Platform Configuration
  items:
  - name: Feature flags
    description: At least 2 feature flags configured for feature toggle demo.
    seed_source: manual_via_api_or_ui
    status: manual_setup_required
  - name: Localization
    description: Both es-MX and en-US locales active for language switching demo.
    seed_source: built_in
    status: available_by_default
demo_startup_commands:
- step: Start the local stack
  command: docker compose -f compose.local.json up -d
  working_directory: 07-implementation/backend/
- step: Verify backend health
  command: curl http://localhost:8080/actuator/health
- step: Verify employee portal
  command: Open http://localhost:5173 in browser
- step: Verify patient portal
  command: Open http://localhost:5174 in browser
- step: Verify public website
  command: Open http://localhost:5176 in browser
open_source_first: true
agent_agnostic: true
no_proprietary_agent_dependencies: true
```

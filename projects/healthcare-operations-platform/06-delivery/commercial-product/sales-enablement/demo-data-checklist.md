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
docker compose -f compose.local.yml up -d

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

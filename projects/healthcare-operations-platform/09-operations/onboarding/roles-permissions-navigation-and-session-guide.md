# Roles, Permissions, Dynamic Navigation & Session Guide

## Overview

This guide documents the Role-Based Access Control (RBAC) architecture, 27 granular permission codes, dynamic menu filtering, and session context policies in **Healthcare Operations Platform (HOP)**.

## Granular Permission Catalog (27 Permissions)

| Permission Code | Category | Description | Granted Standard Roles |
|---|---|---|---|
| `PERM_TENANT_READ` | Platform | Read tenant details | `ADMIN` |
| `PERM_TENANT_WRITE` | Platform | Provision and update tenant details | `ADMIN` |
| `PERM_ORG_MANAGE` | Organization | Manage organization, lab and branch profiles | `ADMIN` |
| `PERM_USER_MANAGE` | IAM | Create and manage staff accounts and roles | `ADMIN` |
| `PERM_CATALOG_READ` | Catalog | Read published diagnostic test catalog | `ADMIN`, `FRONT_DESK`, `LAB_TECH`, `DOCTOR` |
| `PERM_CATALOG_WRITE` | Catalog | Create, update and publish test catalog | `ADMIN` |
| `PERM_PATIENT_READ` | Master Data | View patient profiles and search history | `ADMIN`, `FRONT_DESK`, `LAB_TECH`, `MEDICAL_VALIDATOR` |
| `PERM_PATIENT_WRITE` | Master Data | Register and edit patient demographics | `ADMIN`, `FRONT_DESK` |
| `PERM_DOCTOR_READ` | Master Data | View referring doctor directory | `ADMIN`, `FRONT_DESK`, `LAB_TECH` |
| `PERM_DOCTOR_WRITE` | Master Data | Register and manage doctors | `ADMIN` |
| `PERM_ORDER_READ` | Care Delivery | View diagnostic orders and status | `ADMIN`, `FRONT_DESK`, `LAB_TECH`, `MEDICAL_VALIDATOR`, `DOCTOR` |
| `PERM_ORDER_CREATE` | Care Delivery | Register walk-in or scheduled orders | `ADMIN`, `FRONT_DESK` |
| `PERM_ORDER_CANCEL` | Care Delivery | Cancel diagnostic orders with policy check | `ADMIN`, `FRONT_DESK` |
| `PERM_CASH_MANAGE` | Billing | Open/close cashier sessions and record payments | `ADMIN`, `FRONT_DESK` |
| `PERM_BILLING_REQUEST` | Billing | Generate fiscal billing requests | `ADMIN`, `FRONT_DESK` |
| `PERM_SAMPLE_COLLECT` | Lab Workflow | Record sample collection and label generation | `ADMIN`, `FRONT_DESK`, `LAB_TECH` |
| `PERM_SAMPLE_RECEIVE` | Lab Workflow | Receive and inspect lab samples | `ADMIN`, `LAB_TECH` |
| `PERM_RESULT_ENTER` | Lab Workflow | Enter test results and analytical values | `ADMIN`, `LAB_TECH` |
| `PERM_RESULT_VAL_TECH` | Lab Workflow | Technical validation of analytical results | `ADMIN`, `LAB_TECH` |
| `PERM_RESULT_VAL_MED` | Lab Workflow | Medical validation and sign-off | `ADMIN`, `MEDICAL_VALIDATOR` |
| `PERM_RESULT_RELEASE` | Lab Workflow | Release final diagnostic report to channels | `ADMIN`, `MEDICAL_VALIDATOR` |
| `PERM_INVENTORY_READ` | Inventory | View stock levels, reagents, lots | `ADMIN`, `LAB_TECH`, `QUALITY_MANAGER` |
| `PERM_INVENTORY_WRITE` | Inventory | Adjust stock, record exits, waste, purchase | `ADMIN`, `LAB_TECH` |
| `PERM_QUALITY_READ` | Quality | View EQC, CAPA, QC runs, calibrations | `ADMIN`, `QUALITY_MANAGER` |
| `PERM_QUALITY_WRITE` | Quality | Record QC runs, maintenance, CAPA events | `ADMIN`, `QUALITY_MANAGER` |
| `PERM_INTEGRATION_MANAGE`| Interop | Manage API keys, webhooks, rate limits | `ADMIN` |
| `PERM_MIGRATION_EXECUTE` | Interop | Upload and commit open data ingestion jobs | `ADMIN` |

## Standard Role Mapping

1. **`ADMIN`**: Full platform and operational access across all permissions.
2. **`FRONT_DESK`**: Reception, patient registration, order intake, cashier sessions, sample collection.
3. **`LAB_TECH`**: Sample reception, result entry, technical validation, inventory tracking.
4. **`MEDICAL_VALIDATOR`**: Medical review, diagnostic interpretation, report sign-off and release.
5. **`QUALITY_MANAGER`**: Equipment calibration, internal QC, CAPA management, quality events.
6. **`PATIENT`**: Patient portal self-service (own orders, own released results, profile).
7. **`DOCTOR`**: Doctor portal access (referred patients, orders, released diagnostic reports).

## Dynamic Menu & Navigation Filtering

The Employee Portal (`07-implementation/employee-portal/`) enforces permission-filtered dynamic navigation:
- Navigation items evaluate `SCREEN_TO_PERMISSION` mappings against the active session user's granted permissions.
- Hidden items are omitted completely from DOM rendering (not just disabled), preventing unauthorized DOM discovery.

## Session Context & Security Headers

All requests from web/mobile channels must supply HTTP session context headers:
- `X-Tenant-ID`: Identifies target tenant context.
- `Authorization`: Bearer JWT token with user identity and scopes.
- `X-Branch-ID`: Identifies active operational branch context.
- `X-Trace-ID`: Distributed correlation trace identifier.

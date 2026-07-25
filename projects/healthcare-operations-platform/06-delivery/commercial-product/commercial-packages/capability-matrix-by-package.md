# HOP Capability Matrix by Package

## Overview

This matrix maps every HOP business capability to its availability across the three commercial product packages. Capabilities marked as "expansion" require the Enterprise tier plus the named expansion package.

## Legend

| Status | Meaning |
|--------|---------|
| Included | Capability is included in this package tier |
| Not Available | Capability is not available at this tier |
| Expansion | Requires Enterprise tier plus the named expansion package |

## Core Operations

| Capability | Name | Starter | Professional | Enterprise |
|-----------|------|---------|-------------|-----------|
| BCM-ORG-001 | Tenant Management | Included | Included | Included |
| BCM-ORG-002 | Laboratory Management | Included | Included | Included |
| BCM-ORG-003 | Branch Management | Included | Included | Included |

## Diagnostic Catalog

| Capability | Name | Starter | Professional | Enterprise |
|-----------|------|---------|-------------|-----------|
| BCM-SVC-001 | Diagnostic Service Catalog | Included | Included | Included |
| BCM-SVC-002 | Test Catalog | Included | Included | Included |
| BCM-SVC-003 | Panel Catalog | Included | Included | Included |
| BCM-SVC-004 | Analyte Catalog | Included | Included | Included |
| BCM-SVC-005 | Patient Preparation Management | Included | Included | Included |
| BCM-SVC-006 | Reference Range Management | Included | Included | Included |
| BCM-SVC-007 | Sample Catalog | Included | Included | Included |
| BCM-SVC-009 | Price List Management | Included | Included | Included |

## People and Clinical Master Data

| Capability | Name | Starter | Professional | Enterprise |
|-----------|------|---------|-------------|-----------|
| BCM-PER-001 | Person Management | Included | Included | Included |
| BCM-PER-002 | Patient Management | Included | Included | Included |
| BCM-PER-003 | Doctor Management | Included | Included | Included |

## Front Desk and Care Delivery

| Capability | Name | Starter | Professional | Enterprise |
|-----------|------|---------|-------------|-----------|
| BCM-ATT-001 | Appointment Scheduling | Included | Included | Included |
| BCM-ATT-002 | Patient Registration | Included | Included | Included |
| BCM-ATT-003 | Reception Management | Included | Included | Included |
| BCM-ATT-004 | Admission Management | Included | Included | Included |
| BCM-ATT-005 | Cashier Operations | Included | Included | Included |
| BCM-ATT-006 | Quotation Management | Included | Included | Included |
| BCM-ATT-008 | Billing Request Management | Included | Included | Included |

## Laboratory Workflow

| Capability | Name | Starter | Professional | Enterprise |
|-----------|------|---------|-------------|-----------|
| BCM-LAB-001 | Diagnostic Order Management | Included | Included | Included |
| BCM-LAB-002 | Sample Collection | Included | Included | Included |
| BCM-LAB-003 | Sample Labeling | Included | Included | Included |
| BCM-LAB-005 | Sample Reception | Included | Included | Included |
| BCM-LAB-006 | Laboratory Processing | Included | Included | Included |
| BCM-LAB-008 | Technical Validation | Included | Included | Included |
| BCM-LAB-009 | Medical Validation | Included | Included | Included |
| BCM-LAB-010 | Result Release | Included | Included | Included |

## Results and Delivery

| Capability | Name | Starter | Professional | Enterprise |
|-----------|------|---------|-------------|-----------|
| BCM-RES-001 | Result Management | Included | Included | Included |
| BCM-RES-002 | PDF Report Generation | Included | Included | Included |
| BCM-RES-004 | Digital Delivery | Not Available | Included | Included |
| BCM-RES-005 | Result History | Included | Included | Included |
| BCM-RES-006 | Critical Results | Not Available | Included | Included |
| BCM-RES-007 | Result Notifications | Not Available | Included | Included |

## Platform Services

| Capability | Name | Starter | Professional | Enterprise |
|-----------|------|---------|-------------|-----------|
| BCM-PLT-001 | Identity and Access Management | Included | Included | Included |
| BCM-PLT-002 | Platform Configuration and Feature Flags | Not Available | Included | Included |
| BCM-PLT-003 | Notification Management | Included | Included | Included |
| BCM-PLT-004 | Integration Management | Not Available | Included | Included |
| BCM-PLT-005 | API Management | Not Available | Included | Included |
| BCM-PLT-006 | Observability | Not Available | Included | Included |
| BCM-PLT-007 | Audit Trail | Included | Included | Included |
| BCM-PLT-008 | Document Management | Not Available | Included | Included |
| BCM-PLT-009 | Workflow Engine | Not Available | Included | Included |
| BCM-PLT-010 | Open Data Ingestion and Migration | Not Available | Included | Included |
| BCM-PLT-011 | Product Marketplace and Entitlements | Not Available | Not Available | Included |

## Inventory and Quality

| Capability | Name | Starter | Professional | Enterprise |
|-----------|------|---------|-------------|-----------|
| BCM-INV-001..009 | Inventory Management (9 capabilities) | Not Available | Included | Included |
| BCM-QLT-001 | Internal Quality Controls | Not Available | Included | Included |
| BCM-QLT-002 | External Quality Controls | Not Available | Not Available | Included |
| BCM-QLT-003 | Calibration Management | Not Available | Included | Included |
| BCM-QLT-004 | Equipment Management | Not Available | Included | Included |
| BCM-QLT-005 | Maintenance Management | Not Available | Included | Included |
| BCM-QLT-006 | CAPA Management | Not Available | Not Available | Included |
| BCM-QLT-007 | Audit Management | Not Available | Not Available | Included |

## Expansion Packages

| Capability | Name | Starter | Professional | Enterprise |
|-----------|------|---------|-------------|-----------|
| BCM-IMG-001..008 | Imaging Operations (8 capabilities) | Not Available | Not Available | Expansion |
| BCM-AI-001..008 | AI Overlay (8 capabilities) | Not Available | Not Available | Expansion |

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-COM-CAP-MATRIX-001
  type: capability-matrix
  name: HOP Capability Matrix by Package
  version: 1.0.0
  status: approved
  human_readable: capability-matrix-by-package.md
  machine_readable: capability-matrix-by-package.md
  backlog_item: COM-MOD-016-COM-001
  created_date: 2026-07-24
  owner: Nexora Commercial Product Team
project:
  name: Healthcare Operations Platform
  slug: healthcare-operations-platform
  module: COM-MOD-016
  release: REL-003
legend:
  included: Capability is included in this package tier.
  add_on: Capability can be purchased as an add-on for this tier.
  not_available: Capability is not available at this tier.
  expansion: Capability requires the Enterprise tier plus the named expansion package.
capability_matrix:
- capability: BCM-ORG-001
  name: Tenant Management
  domain: Organization
  starter: included
  professional: included
  enterprise: included
- capability: BCM-ORG-002
  name: Laboratory Management
  domain: Organization
  starter: included
  professional: included
  enterprise: included
- capability: BCM-ORG-003
  name: Branch Management
  domain: Organization
  starter: included
  professional: included
  enterprise: included
- capability: BCM-SVC-001
  name: Diagnostic Service Catalog
  domain: Catalog
  starter: included
  professional: included
  enterprise: included
- capability: BCM-SVC-002
  name: Test Catalog
  domain: Catalog
  starter: included
  professional: included
  enterprise: included
- capability: BCM-SVC-003
  name: Panel Catalog
  domain: Catalog
  starter: included
  professional: included
  enterprise: included
- capability: BCM-SVC-004
  name: Analyte Catalog
  domain: Catalog
  starter: included
  professional: included
  enterprise: included
- capability: BCM-SVC-005
  name: Patient Preparation Management
  domain: Catalog
  starter: included
  professional: included
  enterprise: included
- capability: BCM-SVC-006
  name: Reference Range Management
  domain: Catalog
  starter: included
  professional: included
  enterprise: included
- capability: BCM-SVC-007
  name: Sample Catalog
  domain: Catalog
  starter: included
  professional: included
  enterprise: included
- capability: BCM-SVC-009
  name: Price List Management
  domain: Catalog
  starter: included
  professional: included
  enterprise: included
- capability: BCM-PER-001
  name: Person Management
  domain: People
  starter: included
  professional: included
  enterprise: included
- capability: BCM-PER-002
  name: Patient Management
  domain: People
  starter: included
  professional: included
  enterprise: included
- capability: BCM-PER-003
  name: Doctor Management
  domain: People
  starter: included
  professional: included
  enterprise: included
- capability: BCM-ATT-001
  name: Appointment Scheduling
  domain: Front Desk
  starter: included
  professional: included
  enterprise: included
- capability: BCM-ATT-002
  name: Patient Registration
  domain: Front Desk
  starter: included
  professional: included
  enterprise: included
- capability: BCM-ATT-003
  name: Reception Management
  domain: Front Desk
  starter: included
  professional: included
  enterprise: included
- capability: BCM-ATT-004
  name: Admission Management
  domain: Front Desk
  starter: included
  professional: included
  enterprise: included
- capability: BCM-ATT-005
  name: Cashier Operations
  domain: Front Desk
  starter: included
  professional: included
  enterprise: included
- capability: BCM-ATT-006
  name: Quotation Management
  domain: Front Desk
  starter: included
  professional: included
  enterprise: included
- capability: BCM-ATT-008
  name: Billing Request Management
  domain: Front Desk
  starter: included
  professional: included
  enterprise: included
- capability: BCM-LAB-001
  name: Diagnostic Order Management
  domain: Laboratory
  starter: included
  professional: included
  enterprise: included
- capability: BCM-LAB-002
  name: Sample Collection
  domain: Laboratory
  starter: included
  professional: included
  enterprise: included
- capability: BCM-LAB-003
  name: Sample Labeling
  domain: Laboratory
  starter: included
  professional: included
  enterprise: included
- capability: BCM-LAB-005
  name: Sample Reception
  domain: Laboratory
  starter: included
  professional: included
  enterprise: included
- capability: BCM-LAB-006
  name: Laboratory Processing
  domain: Laboratory
  starter: included
  professional: included
  enterprise: included
- capability: BCM-LAB-008
  name: Technical Validation
  domain: Laboratory
  starter: included
  professional: included
  enterprise: included
- capability: BCM-LAB-009
  name: Medical Validation
  domain: Laboratory
  starter: included
  professional: included
  enterprise: included
- capability: BCM-LAB-010
  name: Result Release
  domain: Laboratory
  starter: included
  professional: included
  enterprise: included
- capability: BCM-RES-001
  name: Result Management
  domain: Results
  starter: included
  professional: included
  enterprise: included
- capability: BCM-RES-002
  name: PDF Report Generation
  domain: Results
  starter: included
  professional: included
  enterprise: included
- capability: BCM-RES-004
  name: Digital Delivery
  domain: Results
  starter: not_available
  professional: included
  enterprise: included
- capability: BCM-RES-005
  name: Result History
  domain: Results
  starter: included
  professional: included
  enterprise: included
- capability: BCM-RES-006
  name: Critical Results
  domain: Results
  starter: not_available
  professional: included
  enterprise: included
- capability: BCM-RES-007
  name: Result Notifications
  domain: Results
  starter: not_available
  professional: included
  enterprise: included
- capability: BCM-PLT-001
  name: Identity and Access Management
  domain: Platform
  starter: included
  professional: included
  enterprise: included
- capability: BCM-PLT-002
  name: Platform Configuration and Feature Flags
  domain: Platform
  starter: not_available
  professional: included
  enterprise: included
- capability: BCM-PLT-003
  name: Notification Management
  domain: Platform
  starter: included
  professional: included
  enterprise: included
- capability: BCM-PLT-004
  name: Integration Management
  domain: Platform
  starter: not_available
  professional: included
  enterprise: included
- capability: BCM-PLT-005
  name: API Management
  domain: Platform
  starter: not_available
  professional: included
  enterprise: included
- capability: BCM-PLT-006
  name: Observability
  domain: Platform
  starter: not_available
  professional: included
  enterprise: included
- capability: BCM-PLT-007
  name: Audit Trail
  domain: Platform
  starter: included
  professional: included
  enterprise: included
- capability: BCM-PLT-008
  name: Document Management
  domain: Platform
  starter: not_available
  professional: included
  enterprise: included
- capability: BCM-PLT-009
  name: Workflow Engine
  domain: Platform
  starter: not_available
  professional: included
  enterprise: included
- capability: BCM-PLT-010
  name: Open Data Ingestion and Migration
  domain: Platform
  starter: not_available
  professional: included
  enterprise: included
- capability: BCM-PLT-011
  name: Product Marketplace and Entitlements
  domain: Platform
  starter: not_available
  professional: not_available
  enterprise: included
- capability: BCM-INV-001
  name: Product Catalog
  domain: Inventory
  starter: not_available
  professional: included
  enterprise: included
- capability: BCM-INV-002
  name: Reagent Management
  domain: Inventory
  starter: not_available
  professional: included
  enterprise: included
- capability: BCM-INV-003
  name: Lot Management
  domain: Inventory
  starter: not_available
  professional: included
  enterprise: included
- capability: BCM-INV-004
  name: Procurement Management
  domain: Inventory
  starter: not_available
  professional: included
  enterprise: included
- capability: BCM-INV-005
  name: Stock Entries
  domain: Inventory
  starter: not_available
  professional: included
  enterprise: included
- capability: BCM-INV-006
  name: Stock Exits
  domain: Inventory
  starter: not_available
  professional: included
  enterprise: included
- capability: BCM-INV-007
  name: Consumption Tracking
  domain: Inventory
  starter: not_available
  professional: included
  enterprise: included
- capability: BCM-INV-008
  name: Inventory Adjustments
  domain: Inventory
  starter: not_available
  professional: included
  enterprise: included
- capability: BCM-INV-009
  name: Waste Management
  domain: Inventory
  starter: not_available
  professional: included
  enterprise: included
- capability: BCM-QLT-001
  name: Internal Quality Controls
  domain: Quality
  starter: not_available
  professional: included
  enterprise: included
- capability: BCM-QLT-002
  name: External Quality Controls
  domain: Quality
  starter: not_available
  professional: not_available
  enterprise: included
- capability: BCM-QLT-003
  name: Calibration Management
  domain: Quality
  starter: not_available
  professional: included
  enterprise: included
- capability: BCM-QLT-004
  name: Equipment Management
  domain: Quality
  starter: not_available
  professional: included
  enterprise: included
- capability: BCM-QLT-005
  name: Maintenance Management
  domain: Quality
  starter: not_available
  professional: included
  enterprise: included
- capability: BCM-QLT-006
  name: CAPA Management
  domain: Quality
  starter: not_available
  professional: not_available
  enterprise: included
- capability: BCM-QLT-007
  name: Audit Management
  domain: Quality
  starter: not_available
  professional: not_available
  enterprise: included
- capability: BCM-IMG-001
  name: Imaging Appointment
  domain: Imaging
  starter: not_available
  professional: not_available
  enterprise: expansion (HOP-EXP-IMAGING)
- capability: BCM-IMG-002
  name: Imaging Reception
  domain: Imaging
  starter: not_available
  professional: not_available
  enterprise: expansion (HOP-EXP-IMAGING)
- capability: BCM-IMG-003
  name: Study Management
  domain: Imaging
  starter: not_available
  professional: not_available
  enterprise: expansion (HOP-EXP-IMAGING)
- capability: BCM-IMG-004
  name: DICOM/PACS Integration
  domain: Imaging
  starter: not_available
  professional: not_available
  enterprise: expansion (HOP-EXP-IMAGING)
- capability: BCM-IMG-005
  name: Dictation
  domain: Imaging
  starter: not_available
  professional: not_available
  enterprise: expansion (HOP-EXP-IMAGING)
- capability: BCM-IMG-006
  name: Signature
  domain: Imaging
  starter: not_available
  professional: not_available
  enterprise: expansion (HOP-EXP-IMAGING)
- capability: BCM-IMG-007
  name: Imaging Delivery
  domain: Imaging
  starter: not_available
  professional: not_available
  enterprise: expansion (HOP-EXP-IMAGING)
- capability: BCM-IMG-008
  name: Imaging Reports
  domain: Imaging
  starter: not_available
  professional: not_available
  enterprise: expansion (HOP-EXP-IMAGING)
- capability: BCM-AI-001
  name: AI Assistant
  domain: AI
  starter: not_available
  professional: not_available
  enterprise: expansion (HOP-EXP-AI)
- capability: BCM-AI-002
  name: OCR
  domain: AI
  starter: not_available
  professional: not_available
  enterprise: expansion (HOP-EXP-AI)
- capability: BCM-AI-003
  name: Summary
  domain: AI
  starter: not_available
  professional: not_available
  enterprise: expansion (HOP-EXP-AI)
- capability: BCM-AI-004
  name: Semantic Search
  domain: AI
  starter: not_available
  professional: not_available
  enterprise: expansion (HOP-EXP-AI)
- capability: BCM-AI-005
  name: Retrieval
  domain: AI
  starter: not_available
  professional: not_available
  enterprise: expansion (HOP-EXP-AI)
- capability: BCM-AI-006
  name: AI Audit
  domain: AI
  starter: not_available
  professional: not_available
  enterprise: expansion (HOP-EXP-AI)
- capability: BCM-AI-007
  name: AI Policy
  domain: AI
  starter: not_available
  professional: not_available
  enterprise: expansion (HOP-EXP-AI)
- capability: BCM-AI-008
  name: AI Explainability
  domain: AI
  starter: not_available
  professional: not_available
  enterprise: expansion (HOP-EXP-AI)
open_source_first: true
agent_agnostic: true
no_proprietary_agent_dependencies: true
```

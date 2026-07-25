# HOP Commercial Product Packages

## Overview

The Healthcare Operations Platform (HOP) is offered in three commercial product packages, each building on the previous tier, plus optional expansion packages for specialized capabilities.

## Package Tiers

### HOP Starter

**Target:** Single-location clinical laboratories with up to 3 branches and basic diagnostic workflows.

**Includes:**
- Tenant, laboratory, and branch management
- Full diagnostic catalog (services, tests, panels, analytes, preparations, reference ranges, samples, price lists)
- Patient, doctor, and person management
- Appointment scheduling, reception, admission, and quotation management
- Diagnostic order management and cashier/billing request operations
- Complete laboratory workflow (sample collection through result release)
- Result management and PDF report generation
- Employee portal for operational staff
- Identity and access management, audit trail, and notification management

**Limits:** Up to 3 branches, 25 concurrent users, 5,000 monthly orders.

### HOP Professional

**Target:** Multi-branch diagnostic laboratories with digital patient and doctor channels, inventory management, and quality controls.

**Includes everything in Starter, plus:**
- Patient portal, doctor portal, and mobile app
- Public website and digital growth channels
- Inventory management (products, reagents, lots, stock movements, procurement, adjustments, waste)
- Internal quality controls, calibration, equipment, and maintenance management
- Integration and migration readiness (adapter contracts, API governance, data migration)
- Platform hardening and SaaS operations (observability, feature flags, platform configuration, workflow engine)
- Digital delivery, critical results, and result notifications

**Limits:** Up to 15 branches, 100 concurrent users, 25,000 monthly orders.

### HOP Enterprise

**Target:** Large diagnostic networks, hospital laboratory groups, and regulated organizations.

**Includes everything in Professional, plus:**
- Advanced quality and compliance (external QC, CAPA, audit management)
- Product marketplace and extension packaging
- Eligibility for expansion packages (Imaging, AI)
- Dedicated support with custom SLAs
- Unlimited branches, users, and orders

## Expansion Packages

| Package | Module | Requires | Status |
|---------|--------|----------|--------|
| Imaging Operations | COM-MOD-014 | Enterprise | Planned |
| AI Overlay | COM-MOD-015 | Enterprise | Planned |

## Related Documents

- [Capability Matrix by Package](capability-matrix-by-package.md)
- [Pricing Model](pricing-model.md)
- [Upgrade/Downgrade Criteria](upgrade-downgrade-criteria.md)

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-COM-PKG-001
  type: commercial-product-packages
  name: HOP Commercial Product Packages
  version: 1.0.0
  status: approved
  human_readable: hop-commercial-packages.md
  machine_readable: hop-commercial-packages.md
  backlog_item: COM-MOD-016-COM-001
  created_date: 2026-07-24
  owner: Nexora Commercial Product Team
project:
  name: Healthcare Operations Platform
  slug: healthcare-operations-platform
  module: COM-MOD-016
  release: REL-003
packages:
- id: HOP-PKG-STARTER
  name: HOP Starter
  commercial_name: Nexora HOP Starter
  tier: starter
  target_customer: Single-location clinical laboratories with up to 3 branches and
    basic diagnostic workflows.
  description: Core operational workflows for tenant setup, diagnostic catalog, patient
    and doctor management, front desk, sample collection through result delivery,
    cashier and billing request management. Includes employee portal access for operational
    staff.
  included_modules:
  - MVP-MOD-001
  - MVP-MOD-002
  - MVP-MOD-003
  - MVP-MOD-004
  - MVP-MOD-005
  - MVP-MOD-006
  - MVP-MOD-007
  included_capabilities:
  - BCM-ORG-001
  - BCM-ORG-002
  - BCM-ORG-003
  - BCM-SVC-001
  - BCM-SVC-002
  - BCM-SVC-003
  - BCM-SVC-004
  - BCM-SVC-005
  - BCM-SVC-006
  - BCM-SVC-007
  - BCM-SVC-009
  - BCM-PER-001
  - BCM-PER-002
  - BCM-PER-003
  - BCM-ATT-001
  - BCM-ATT-002
  - BCM-ATT-003
  - BCM-ATT-004
  - BCM-ATT-005
  - BCM-ATT-006
  - BCM-ATT-008
  - BCM-LAB-001
  - BCM-LAB-002
  - BCM-LAB-003
  - BCM-LAB-005
  - BCM-LAB-006
  - BCM-LAB-008
  - BCM-LAB-009
  - BCM-LAB-010
  - BCM-RES-001
  - BCM-RES-002
  - BCM-RES-005
  - BCM-PLT-001
  - BCM-PLT-003
  - BCM-PLT-007
  product_surfaces:
    backend: included
    employee_portal: included
    patient_portal: not_included
    doctor_portal: not_included
    mobile_app: not_included
    public_website: not_included
  tenant_limits:
    max_branches: 3
    max_concurrent_users: 25
    max_monthly_orders: 5000
    data_retention_months: 24
  support_tier: L1 email support with standard SLA
- id: HOP-PKG-PROFESSIONAL
  name: HOP Professional
  commercial_name: Nexora HOP Professional
  tier: professional
  target_customer: Multi-branch diagnostic laboratories with digital patient and doctor
    channels, inventory management and quality controls.
  description: Everything in Starter plus patient and doctor portals, mobile app foundation,
    public website and digital growth, inventory and internal quality management,
    integration and migration readiness, and platform hardening for SaaS operations.
  includes_package: HOP-PKG-STARTER
  additional_modules:
  - MVP-MOD-008
  - COM-MOD-009
  - COM-MOD-010
  - COM-MOD-011
  - COM-MOD-012
  additional_capabilities:
  - BCM-RES-004
  - BCM-RES-006
  - BCM-RES-007
  - BCM-PLT-002
  - BCM-PLT-004
  - BCM-PLT-005
  - BCM-PLT-006
  - BCM-PLT-008
  - BCM-PLT-009
  - BCM-PLT-010
  - BCM-INV-001
  - BCM-INV-002
  - BCM-INV-003
  - BCM-INV-004
  - BCM-INV-005
  - BCM-INV-006
  - BCM-INV-007
  - BCM-INV-008
  - BCM-INV-009
  - BCM-QLT-001
  - BCM-QLT-003
  - BCM-QLT-004
  - BCM-QLT-005
  product_surfaces:
    backend: included
    employee_portal: included
    patient_portal: included
    doctor_portal: included
    mobile_app: included
    public_website: included
  tenant_limits:
    max_branches: 15
    max_concurrent_users: 100
    max_monthly_orders: 25000
    data_retention_months: 60
  support_tier: L1 and L2 support with priority SLA
- id: HOP-PKG-ENTERPRISE
  name: HOP Enterprise
  commercial_name: Nexora HOP Enterprise
  tier: enterprise
  target_customer: Large diagnostic networks, hospital laboratory groups, and regulated
    organizations requiring advanced compliance, marketplace and expansion capabilities.
  description: Everything in Professional plus advanced quality and compliance (external
    QC, CAPA, audit management), product marketplace and extension packaging, and
    eligibility for expansion packages (imaging, AI overlay). Includes dedicated support
    and custom SLAs.
  includes_package: HOP-PKG-PROFESSIONAL
  additional_modules:
  - COM-MOD-013
  - COM-MOD-016
  - COM-MOD-017
  additional_capabilities:
  - BCM-QLT-002
  - BCM-QLT-006
  - BCM-QLT-007
  - BCM-PLT-011
  product_surfaces:
    backend: included
    employee_portal: included
    patient_portal: included
    doctor_portal: included
    mobile_app: included
    public_website: included
    marketplace: included
  tenant_limits:
    max_branches: unlimited
    max_concurrent_users: unlimited
    max_monthly_orders: unlimited
    data_retention_months: 84
  support_tier: L1, L2 and L3 support with custom SLA and dedicated account manager
expansion_packages:
- id: HOP-EXP-IMAGING
  name: Imaging Operations
  requires_package: HOP-PKG-ENTERPRISE
  module: COM-MOD-014
  status: planned
  capabilities:
  - BCM-IMG-001
  - BCM-IMG-002
  - BCM-IMG-003
  - BCM-IMG-004
  - BCM-IMG-005
  - BCM-IMG-006
  - BCM-IMG-007
  - BCM-IMG-008
- id: HOP-EXP-AI
  name: AI Overlay
  requires_package: HOP-PKG-ENTERPRISE
  module: COM-MOD-015
  status: planned
  capabilities:
  - BCM-AI-001
  - BCM-AI-002
  - BCM-AI-003
  - BCM-AI-004
  - BCM-AI-005
  - BCM-AI-006
  - BCM-AI-007
  - BCM-AI-008
open_source_first: true
agent_agnostic: true
no_proprietary_agent_dependencies: true
```

# HOP Tenant Upgrade and Downgrade Criteria

## Overview

This document defines the criteria, migration actions, and data impact for tenant package tier changes in the Healthcare Operations Platform.

## Upgrade Paths

### Starter to Professional

**Trigger Criteria:**
- Tenant exceeds 3 branches
- Tenant requires patient/doctor portals or mobile app
- Tenant needs inventory, quality, or integration capabilities
- Tenant exceeds 25 concurrent users or 5,000 monthly orders

**Migration Actions:**
1. Enable Professional-tier feature flags (BCM-PLT-002)
2. Provision patient portal, doctor portal, and mobile app access
3. Enable public website deployment
4. Activate inventory and internal quality modules
5. Activate integration and migration capabilities
6. Update tenant tier in BCM-ORG-001
7. Adjust billing and SLA

**Data Impact:** No data loss. All existing data preserved. New capabilities available immediately.

### Professional to Enterprise

**Trigger Criteria:**
- Tenant requires advanced compliance (external QC, CAPA, audit management)
- Tenant requires product marketplace
- Tenant exceeds Professional-tier limits
- Regulatory requirements mandate advanced audit capabilities

**Migration Actions:**
1. Enable Enterprise-tier feature flags (BCM-PLT-002)
2. Activate advanced quality and compliance modules
3. Provision marketplace access
4. Assign dedicated account manager
5. Configure custom SLA terms
6. Update tenant tier in BCM-ORG-001

**Data Impact:** No data loss. All existing data preserved. Advanced capabilities available immediately.

## Downgrade Paths

### Enterprise to Professional

**Preconditions:**
- No active marketplace packages requiring Enterprise tier
- No open CAPA, external QC, or audit management records
- Customer acknowledges loss of Enterprise-only capabilities

**Data Impact:** Enterprise-only data becomes read-only and inaccessible through the UI. Data can be exported before downgrade. No automatic deletion.

### Professional to Starter

**Preconditions:**
- 3 or fewer active branches
- 25 or fewer concurrent users
- No active integration endpoints or migration jobs
- Customer acknowledges loss of Professional-only capabilities

**Data Impact:** Professional-only data becomes read-only and inaccessible through the UI. Patient, doctor, and mobile portal users lose access immediately.

## Feature Gate Mechanism

Package tier enforcement uses BCM-PLT-002 (Platform Configuration and Feature Flags). Each capability has a feature flag scoped to the tenant tier. The backend enforces feature gates at the API level; UI surfaces hide inaccessible screens via permission-filtered navigation (BCM-PLT-001). Every tier change is recorded as an append-only AuditEvent (AGG-018).

## Rollback Policy

All tier changes have a **30-day rollback window** during which the tenant can revert to the previous tier.

## Related Documents

- [Commercial Packages](hop-commercial-packages.md)
- [Pricing Model](pricing-model.md)
- [Capability Matrix](capability-matrix-by-package.md)

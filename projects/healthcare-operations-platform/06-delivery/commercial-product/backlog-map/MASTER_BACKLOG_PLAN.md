---
artifact:
  id: HOP-COM-BACKLOG-MASTER
  type: atomic-backlog-master-plan
  status: active
  optimization: atomic_context
---

# HOP Master Backlog Plan

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-COM-BACKLOG-MASTER
  type: atomic-backlog-master-plan
  status: active
current_baseline:
  completed_module: COM-MOD-017 (COM-MOD-017-CLOSEOUT closed; REL-003 Commercial General Availability fully complete)
  completed_status: closed
  active_module: COM-MOD-015
  active_backlog_item: COM-MOD-015-BE-002
  active_module_progress: COM-MOD-015-BE-001 closed. Compiled AI Overlay backend assistant orchestration, human-review policy,
    audit outputs and local PostgreSQL persistence. Active backlog item advanced to COM-MOD-015-BE-002.
  paused_backlog_item: null
  pause_reason: null
release_plan:
- id: REL-000
  name: Foundation Completed
  status: completed
  modules:
  - MVP-MOD-001
  outcome: Platform foundation implemented and ready for functional validation.
- id: REL-001
  name: Operational Core
  status: planned
  target_readiness: internal_alpha
  modules:
  - MVP-MOD-002
  - MVP-MOD-003
  - MVP-MOD-004
  - MVP-MOD-005
  - MVP-MOD-006
  - MVP-MOD-007
  - MVP-MOD-008
  outcome: A diagnostic laboratory can configure catalog, register people, create orders, collect samples, validate results
    and deliver reports.
- id: REL-002
  name: Commercial Beta
  status: planned
  target_readiness: customer_beta
  modules:
  - COM-MOD-009
  - COM-MOD-010
  - COM-MOD-011
  - COM-MOD-012
  outcome: HOP can run a controlled paid pilot with customer-facing channels, inventory/quality baseline and SaaS operations.
- id: REL-003
  name: Commercial General Availability
  status: planned
  target_readiness: commercial_ga
  modules:
  - COM-MOD-013
  - COM-MOD-016
  - COM-MOD-017
  outcome: HOP is ready to be sold, onboarded, supported and governed as a commercial product.
- id: REL-004
  name: Expansion Packages
  status: planned
  target_readiness: commercial_expansion
  modules:
  - COM-MOD-014
  - COM-MOD-015
  outcome: Imaging operations and AI-assisted overlays can be commercialized as optional product packages.
module_index:
- id: HOP-QUALITY-ALIGNMENT
  name: Enterprise Quality Alignment
  release: REL-001
  status: closed
  path: 06-delivery/commercial-product/backlog-map/modules/HOP-QUALITY-ALIGNMENT.md
  items: 7
- id: HOP-ENTERPRISE-FOUNDATION-ALIGNMENT
  name: Enterprise Product Foundation Alignment
  release: REL-001
  status: closed
  path: 06-delivery/commercial-product/backlog-map/modules/HOP-ENTERPRISE-FOUNDATION-ALIGNMENT.md
  items: 1
- id: MVP-MOD-002
  name: Diagnostic Catalog
  release: REL-001
  status: next
  path: 06-delivery/commercial-product/backlog-map/modules/MVP-MOD-002.md
  items: 6
- id: MVP-MOD-003
  name: People and Clinical Master Data
  release: REL-001
  status: planned
  path: 06-delivery/commercial-product/backlog-map/modules/MVP-MOD-003.md
  items: 6
- id: MVP-MOD-004
  name: Front Desk and Care Delivery
  release: REL-001
  status: in_progress
  path: 06-delivery/commercial-product/backlog-map/modules/MVP-MOD-004.md
  items: 6
- id: MVP-MOD-005
  name: Cashier and Billing Request
  release: REL-001
  status: in_progress
  path: 06-delivery/commercial-product/backlog-map/modules/MVP-MOD-005.md
  items: 6
- id: MVP-MOD-006
  name: Laboratory Workflow
  release: REL-001
  status: closed
  path: 06-delivery/commercial-product/backlog-map/modules/MVP-MOD-006.md
  items: 6
- id: MVP-MOD-007
  name: Results and Digital Delivery
  release: REL-001
  status: closed
  path: 06-delivery/commercial-product/backlog-map/modules/MVP-MOD-007.md
  items: 8
- id: MVP-MOD-008
  name: Integration and Migration Readiness
  release: REL-001
  status: in_progress
  path: 06-delivery/commercial-product/backlog-map/modules/MVP-MOD-008.md
  items: 6
- id: COM-MOD-009
  name: Patient and Doctor Portals
  release: REL-002
  status: module_closed
  path: 06-delivery/commercial-product/backlog-map/modules/COM-MOD-009.md
  items: 7
- id: COM-MOD-010
  name: Inventory and Internal Quality
  release: REL-002
  status: module_closed
  path: 06-delivery/commercial-product/backlog-map/modules/COM-MOD-010.md
  items: 6
- id: COM-MOD-011
  name: Public Website and Digital Growth
  release: REL-002
  status: closed
  path: 06-delivery/commercial-product/backlog-map/modules/COM-MOD-011.md
  items: 6
- id: COM-MOD-012
  name: Platform Hardening and SaaS Operations
  release: REL-002
  status: module_closed
  path: 06-delivery/commercial-product/backlog-map/modules/COM-MOD-012.md
  items: 6
- id: COM-MOD-013
  name: Advanced Quality and Compliance
  release: REL-003
  status: module_closed
  path: 06-delivery/commercial-product/backlog-map/modules/COM-MOD-013.md
  items: 5
- id: COM-MOD-014
  name: Imaging Operations
  release: REL-004
  status: active
  path: 06-delivery/commercial-product/backlog-map/modules/COM-MOD-014.md
  items: 7
- id: COM-MOD-015
  name: AI Overlay
  release: REL-004
  status: planned
  path: 06-delivery/commercial-product/backlog-map/modules/COM-MOD-015.md
  items: 6
- id: COM-MOD-016
  name: Commercial Launch and Customer Enablement
  release: REL-003
  status: module_closed
  path: 06-delivery/commercial-product/backlog-map/modules/COM-MOD-016.md
  items: 6
- id: COM-MOD-017
  name: Product Marketplace and Extension Packaging
  release: REL-003
  status: module_closed
  path: 06-delivery/commercial-product/backlog-map/modules/COM-MOD-017.md
  items: 8
item_count: 109
item_index_path: 06-delivery/commercial-product/backlog-map/BACKLOG_ITEM_INDEX.md
load_policy:
  load_master_first: true
  load_module_record_when_selecting_dependency: true
  load_item_record_only_for_active_or_impacted_item: true
```

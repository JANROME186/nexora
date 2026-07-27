---
artifact:
  id: HOP-COM-BACKLOG-001
  type: commercial-product-backlog-index
  status: active
  optimization: atomic_context
---

# HOP Commercial Product Backlog

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-COM-BACKLOG-001
  type: commercial-product-backlog
  name: Healthcare Operations Platform Commercial Product Backlog
  version: 1.0.0
  status: approved
  human_readable: HOP_COMMERCIAL_PRODUCT_BACKLOG.md
  machine_readable: HOP_COMMERCIAL_PRODUCT_BACKLOG.md
  created_date: 2026-07-08
  owner: Nexora Product Architecture Team
  scope: Full product delivery after MVP-MOD-001 completion
product:
  name: Healthcare Operations Platform
  short_name: HOP
  commercial_name: Nexora Healthcare Operations Platform
  company: Nexora
  current_baseline:
    completed_module: COM-MOD-015 (COM-MOD-015-CLOSEOUT closed; AI Overlay module fully closed)
    completed_status: closed
    active_module: COM-MOD-015
    active_backlog_item: null
    active_module_progress: COM-MOD-015-CLOSEOUT closed. Formally closed AI Overlay (BCM-AI-001 through BCM-AI-008 marked module_closed). AI Overlay module fully closed.
    paused_backlog_item: null
    pause_reason: null
mdpe_policy:
  architecture: Model Driven Product Engineering
  primary_development_unit: Business Capability Package
  roadmap_grouping_unit: module
  source_of_truth_rule: Models are the only durable editable source of truth.
  execution_flow:
  - model
  - compile
  - implement_rules
  - validate
  - release
  do_not_write_manually:
  - CRUD scaffolding
  - DTOs
  - Controllers
  - Repositories
  - Swagger documentation
  - SDKs
  - Repetitive documentation
  - Duplicate models
  - Repetitive test cases
  write_manually:
  - Business model
  - Business rules
  - Business processes
  - Domain decisions
  - OpenAPI source contracts
  - Non-generatable custom rule implementation
  - Compiler templates and generators
  - Architecture decisions
backlog_master_plan: 06-delivery/commercial-product/backlog-map/MASTER_BACKLOG_PLAN.md
backlog_item_index: 06-delivery/commercial-product/backlog-map/BACKLOG_ITEM_INDEX.md
definition_of_ready_done: 06-delivery/commercial-product/backlog-map/DEFINITION_OF_READY_DONE.md
active_item:
  id: COM-MOD-015-CLOSEOUT
  name: Module closeout and registry update
  status: next
  module_id: COM-MOD-015
  module_name: AI Overlay
  release: REL-004
modules:
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
  status: module_closed
  path: 06-delivery/commercial-product/backlog-map/modules/COM-MOD-014.md
  items: 7
- id: COM-MOD-015
  name: AI Overlay
  release: REL-004
  status: module_closed
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
context_policy:
  do_not_load_module_records_by_default: true
  load_active_item_record_only: true
  validator_loads_item_index_for_historical_status: true
```

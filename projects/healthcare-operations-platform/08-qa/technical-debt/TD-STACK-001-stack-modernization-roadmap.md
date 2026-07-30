---
id: TD-STACK-001
format: markdown_structured_payload
type: technical-debt-item
name: Gradual stack modernization roadmap for major framework and runtime upgrades
version: 1.0.0
status: materially_reduced
---

# Gradual Stack Modernization Roadmap For Major Framework And Runtime Upgrades

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-STACK-001
  type: technical-debt-item
  name: Gradual stack modernization roadmap for major framework and runtime upgrades
  version: 1.0.0
  status: materially_reduced
  created_date: 2026-07-09
  updated_date: 2026-07-22
source:
  discovered_during_backlog_item: MVP-MOD-002-CLOSEOUT
  module: MVP-MOD-002 Diagnostic Catalog
  evidence: 03-architecture/technology-architecture/client-stack-market-validation.md
classification:
  category: framework_and_runtime_modernization
  affected_area: full_stack
  affected_components:
  - 07-implementation/backend
  - 07-implementation/employee-portal
  - 07-implementation/mobile-app
  risk_level: low
  blocking: false
  reason_non_blocking: 'The current stack is composed of supported, actively maintained,
    open-source versions with no outstanding HIGH/CRITICAL findings. Newer major versions
    exist across the stack but migrating now would introduce breaking-change risk
    without a functional driver. The baseline is not a permanent constraint; upgrades
    should be applied opportunistically when the affected components are touched.

    '
market_validation_reference:
  official_sources_checked_on: 2026-07-09
  detail: See 03-architecture/technology-architecture/client-stack-market-validation.md
    for sources.
current_vs_market:
- component: Java runtime
  current: 21 (LTS, 21.0.7)
  market_latest: 25 (LTS) available; 21 LTS remains supported
  decision: keep_21_lts
  upgrade_trigger: evaluate Java 25 LTS when moving to Spring Boot 4.x
- component: Spring Boot
  current: 3.5.14
  market_latest: 3.5.16 patch on the 3.5.x line; 4.0/4.1 new major line
  decision: stay_on_supported_3_5_line
  upgrade_trigger: apply 3.5.x patches opportunistically; plan 4.x as a coordinated
    migration
- component: Spring Modulith
  current: 1.4.5
  market_latest: 1.4.8 on the 1.4.x line; 2.0.6 pairs with Spring Boot 4.x
  decision: keep_1_4_x_paired_with_boot_3_5
  upgrade_trigger: upgrade with the Spring Boot 4.x migration
- component: React
  current: 18.3.1
  market_latest: 19.1.8
  decision: keep_18_3_1
  upgrade_trigger: evaluate React 19 during a frontend-focused backlog item
- component: TypeScript
  current: 5.9.3
  market_latest: 6.0 GA (7.0 in release candidate)
  decision: keep_5_9
  upgrade_trigger: evaluate TypeScript 6.0 during frontend tooling work
- component: Vite
  current: 6.x
  market_latest: 8.1
  decision: keep_6_x
  upgrade_trigger: evaluate Vite 7/8 during frontend tooling work; validate Rolldown
    bundler impact
- component: PostgreSQL
  current: 16 (local runtime)
  market_latest: 18 (16 supported until Nov 2028)
  decision: keep_16_supported
  upgrade_trigger: evaluate 18 during deployment/operations hardening
remediation:
  strategy: materially_reduced_by_COM_MOD_012_OPS_001_then_gradual_when_affected_components_are_touched
  latest_reduction:
    backlog_item: HOP-HARD-INT-001
    evidence: 08-qa/qa/final-hardening/HOP-HARD-INT-001-validation.md
    summary: 'Validated full-stack modernization baseline across integration, OpenAPI generation, migration, workflow and observability surfaces. Stack remains fully supported on Java 21 LTS, Spring Boot 3.5.14, Spring Modulith 1.4.5, React 18.3.1, TypeScript 5.9.3, Vite 6.x, and PostgreSQL 16 with 0 failing quality gates. Major line upgrades remain scheduled for dedicated post-GA roadmap iterations.'
  recommended_trigger:
  - dedicated modernization backlog item
  - major security-relevant advisory on a pinned major version
  - Spring Boot 4.x migration window
  - frontend tooling modernization iteration
  acceptance_criteria:
  - Each major upgrade is executed as a small, testable backlog item with rollback
    strategy.
  - Quality gates continue to pass after each upgrade.
  - This roadmap is revisited during every module closeout and release readiness gate.
```

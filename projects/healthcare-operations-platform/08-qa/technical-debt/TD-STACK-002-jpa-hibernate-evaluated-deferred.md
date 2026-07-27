---
id: TD-STACK-002
format: markdown_structured_payload
type: technical-debt-item
name: JPA/Hibernate adoption evaluated and deferred; JdbcTemplate-behind-ports remains
  the accepted persistence baseline
version: 2.0.0
status: closed
---

# Jpa/Hibernate Adoption Evaluated And Deferred; Jdbctemplate Behind Ports Remains The Accepted Persistence Baseline

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-STACK-002
  type: technical-debt-item
  name: JPA/Hibernate adoption evaluated and deferred; JdbcTemplate-behind-ports remains
    the accepted persistence baseline
  version: 2.0.0
  status: closed
  created_date: 2026-07-17
  updated_date: 2026-07-26
source:
  discovered_during_backlog_item: HOP-ENT-FOUND-001
  module: HOP-ENTERPRISE-FOUNDATION-ALIGNMENT
  evidence: 03-architecture/technology-architecture/persistence-and-contract-generation-review.md
classification:
  category: technology_evolution_modernization_option
  affected_area: backend_persistence_strategy
  affected_components:
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/**/adapter/out/jdbc/**
  risk_level: low
  urgency: low
  blocking: false
  reason_non_blocking: The current pattern already satisfies the standard's SQL-isolation
    requirement; this is a modernization option, not a defect.
current_state:
  issue: Backend uses plain JdbcTemplate with hand-written SQL behind repository ports,
    not JPA/Hibernate.
  compensating_control:
  - SQL is consistently isolated to adapter/out/jdbc/ classes across every module.
target_state:
  preferred_open_source_tooling:
  - Spring Data JPA + Hibernate ORM, if adopted.
  expected_integration_points:
  - Would require simultaneous migration of all 45+ existing tables' repositories;
    not attempted speculatively.
remediation:
  strategy: revisit_if_a_future_module_needs_complex_object_graph_persistence
  owner: backend_platform_team
  estimated_effort: very_large (full-stack migration if ever undertaken)
  estimated_cost_impact: medium
  target_backlog: revisit_if_a_future_module_needs_complex_object_graph_persistence
  dependencies_or_prerequisites: []
  incremental_remediation_triggers:
  - A future module's aggregate graph becomes too complex for hand-written JdbcTemplate
    mapping to remain safe/maintainable.
  acceptance_criteria:
  - A dedicated ADR exists before any JPA migration begins, given its cross-cutting
    blast radius.
  owner_or_responsible_role: backend_platform_team
disposition_history:
- backlog_item: HOP-HARD-DATA-001
  date: 2026-07-26
  disposition: closed
  reason: Produced 03-architecture/technology-architecture/ADR-0001-jpa-hibernate-persistence-deferral.md,
    a dedicated architecture decision record formalizing persistence-and-contract-generation-review.md's
    prior evaluation (context, decision, alternatives considered, consequences and a revisit
    trigger). No JPA migration is underway or planned; the ADR exists ahead of, not only at,
    any future migration attempt, which is exactly the acceptance criterion. JdbcTemplate-behind-ports
    remains the accepted baseline.
```

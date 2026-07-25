---
id: TD-BE-020
format: markdown_structured_payload
type: technical-debt-item
name: local profile silently had no real datasource because DataSourceAutoConfiguration
  stayed globally excluded after the YAML-to-properties migration
version: 1.0.0
status: closed
---

# Local Profile Silently Had No Real Datasource Because DataSourceAutoConfiguration Stayed Globally Excluded After The YAML-To-Properties Migration

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-BE-020
  type: technical-debt-item
  name: local profile silently had no real datasource because DataSourceAutoConfiguration
    stayed globally excluded after the YAML-to-properties migration
  version: 1.0.0
  status: closed
  created_date: 2026-07-24
source:
  discovered_during_backlog_item: COM-MOD-017-BE-002
  module: cross-cutting (every backend module with a local-database integration test)
  evidence: 08-qa/qa/product-marketplace-and-extension-packaging/COM-MOD-017-BE-002-validation.md
classification:
  category: infrastructure_regression
  affected_area: local_profile_spring_boot_datasource_autoconfiguration
  affected_components:
  - application.properties
  - application-local.properties
  risk_level: high
  urgency: none_already_fixed
  blocking: false
  reason_non_blocking: Fixed within the same session it was discovered, before any
    coverage/evidence figures were taken from the affected state.
current_state:
  issue: 'application.properties (the always-active default profile file) sets
    spring.autoconfigure.exclude[0]=...DataSourceAutoConfiguration for every profile,
    including local. This is correct for every profile except local (the only profile
    with a real Postgres datasource; every other profile uses in-memory
    @Profile("!local") adapters and genuinely has no datasource). application-local.properties
    never re-enabled it. Root-caused to NXF-FMT-002''s zero_yaml_migrator.py conversion
    (commit a446ef4, chore(framework): migrate repository off yaml artifacts):
    the original YAML config almost certainly scoped this exclusion to the non-local
    profile only (a multi-document YAML pattern with spring.config.activate.on-profile),
    and the flat-properties-file migration lost that conditionality. Effect: every
    @ActiveProfiles("local")/@EnabledIfSystemProperty(hop.local-db-tests=true)
    LocalDatabaseTest across the entire backend (not just marketplaceentitlements)
    failed ApplicationContext startup with NoSuchBeanDefinitionException for
    JdbcTemplate, and every @Profile("local") JDBC repository adapter in every
    module silently never executed, making backend coverage figures measured
    without -Dhop.local-db-tests=true meaningless (a clean run measured 70.27%,
    a ~14-point drop from the accepted 84.53% floor) and coverage figures measured
    even with -Dhop.local-db-tests=true wrong until this was fixed (the flag alone
    is not sufficient once DataSourceAutoConfiguration is excluded).'
target_state:
  preferred_remediation: Re-enable DataSourceAutoConfiguration under the local profile.
  quality_goal: A fresh Docker Postgres volume plus -Dhop.local-db-tests=true must
    reproduce the documented coverage floor with 0 LocalDatabaseTest failures across
    every module, not just marketplaceentitlements.
remediation:
  strategy: closed_by_COM_MOD_017_BE_002_adding_spring.autoconfigure.exclude=_override_to_application-local.properties
  owner: backend_team
  estimated_effort: trivial
  estimated_cost_impact: none
  target_backlog: COM-MOD-017-BE-002
  dependencies_or_prerequisites: []
  acceptance_criteria:
  - application-local.properties re-enables DataSourceAutoConfiguration for the local
    profile only; every other profile's exclusion is unchanged.
  - A clean rebuild with a fresh Docker Postgres volume and -Dhop.local-db-tests=true
    passes every LocalDatabaseTest across the whole backend, not just marketplaceentitlements.
  fix_verified: 'mvn -Pquality -Dhop.local-db-tests=true clean verify: 484 tests,
    0 failures/errors/skipped (up from 27 skipped/1 failing before the fix), backend
    line coverage 84.65% (clean-rebuild, real Postgres, matching the accepted floor
    methodology).'
```

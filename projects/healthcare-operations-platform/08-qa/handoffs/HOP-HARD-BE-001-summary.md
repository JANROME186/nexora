---
id: HOP-HARD-BE-001-summary
type: backlog-handoff-summary
status: closed
backlog_item: HOP-HARD-BE-001
---

# HOP-HARD-BE-001 Summary

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-HARD-BE-001-summary
  type: backlog-handoff-summary
  status: closed
  backlog_item: HOP-HARD-BE-001
summary:
  closed_items:
  - TD-BE-006
  - TD-BE-007
  - TD-BE-008
  materially_reduced_or_revalidated:
  - TD-BE-002
  - TD-BE-003
  - TD-BE-004
  - TD-BE-005
  - TD-BE-021
  - TD-BE-022
  implementation:
  - PatientRegistrationService.commit now has a transaction boundary.
  - Non-local profiles now have a fallback transaction manager while local JDBC keeps the real DataSource transaction manager.
  - CredentialExpirationWatcher proactively expires verified credentials and writes audit evidence.
  - Patient and doctor snapshots consume tenant-configurable document masking policy.
validation:
  maven_quality_verify: passed
  tests_run: 528
  line_coverage_percent: 84.62
  trivy_backend_source_scan: passed
  static_analysis: executed_with_residual_debt
next_backlog_item: HOP-HARD-IAM-001
```

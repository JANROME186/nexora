---
artifact:
  id: HOP-BACKLOG-ITEM-HOP-HARD-APP-001
  type: backlog-item-record
  status: active
  optimization: atomic_context
---

# HOP-HARD-APP-001 Backlog Item

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: HOP-HARD-APP-001
name: Mobile, patient portal, doctor portal and imaging delivery hardening
status: planned
module_id: HOP-FINAL-HARDENING
module_name: Final Commercial Hardening and Technical Debt Burn-down
release: REL-GA
workstream: mobile
objective: Complete mobile and portal delivery gaps, including imaging study delivery views and mobile layout baseline.
mapped_items:
- TD-APP-001
- TD-UX-003
related_backlog_items:
- COM-MOD-014-PORTAL-001
evidence:
  qa: 08-qa/qa/final-hardening/HOP-HARD-APP-001-validation.md
  security_quality: 08-qa/security-quality/HOP-HARD-APP-001/security-quality-evidence.md
  handoff: 08-qa/handoffs/HOP-HARD-APP-001-summary.md
closure:
  next_backlog_item: HOP-HARD-WEB-001
```

---
artifact:
  id: HOP-BACKLOG-ITEM-HOP-HARD-BE-001
  type: backlog-item-record
  status: active
  optimization: atomic_context
---

# HOP-HARD-BE-001 Backlog Item

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: HOP-HARD-BE-001
name: Backend quality, persistence and coverage debt burn-down
status: active
module_id: HOP-FINAL-HARDENING
module_name: Final Commercial Hardening and Technical Debt Burn-down
release: REL-GA
workstream: backend
objective: Reduce backend commercial-readiness risk before any new feature work by closing or materially reducing the highest-impact backend quality and persistence debt.
mapped_items:
- TD-BE-002
- TD-BE-003
- TD-BE-004
- TD-BE-005
- TD-BE-006
- TD-BE-007
- TD-BE-008
- TD-BE-021
- TD-BE-022
required_actions:
- Review each mapped debt item file and update it only with evidence-backed status changes.
- Execute or complete Java/Maven quality gates for static analysis, secure code, dependency vulnerability analysis across all severities, SBOM, license/build rules, duplicate code, complexity and architecture checks.
- Improve backend test coverage materially toward the 80 percent target without dropping below the current documented 70.16 percent floor.
- Prioritize transactional safety, scheduler-backed state transitions, tenant-configurable masking and modeled AI overlay endpoint gaps.
- Keep persistence behind ports/adapters; do not introduce direct SQL coupling outside accepted repository/adapters.
evidence:
  qa: 08-qa/qa/final-hardening/HOP-HARD-BE-001-validation.md
  security_quality: 08-qa/security-quality/HOP-HARD-BE-001/security-quality-evidence.md
  handoff: 08-qa/handoffs/HOP-HARD-BE-001-summary.md
closure:
  next_backlog_item: HOP-HARD-IAM-001
  commit_suggestion: "fix(hop): burn down backend hardening debt"
```

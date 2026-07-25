---
id: TD-I18N-001
format: markdown_structured_payload
type: technical-debt-item
name: Establish message externalization and magic-string remediation baseline
version: 2.0.0
status: closed
---

# Establish Message Externalization And Magic String Remediation Baseline

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-I18N-001
  type: technical-debt-item
  name: Establish message externalization and magic-string remediation baseline
  version: 2.0.0
  status: closed
  created_date: 2026-07-15
  updated_date: 2026-07-16
source:
  discovered_during_backlog_item: HOP-QUALITY-ALIGNMENT-GAP-ANALYSIS
  module: HOP-QUALITY-ALIGNMENT
  evidence: 08-qa/qa/quality-alignment/HOP-QUALITY-ALIGNMENT-GAP-ANALYSIS.md
classification:
  category: i18n_and_code_decoupling
  affected_area: backend_frontend_mobile_messages_and_magic_values
  affected_components:
  - 07-implementation/backend
  - 07-implementation/employee-portal
  - 07-implementation/mobile-app
  risk_level: medium
  blocking: false
current_state:
  issue: 'Resolved by HOP-QA-ALIGN-005. HOP now has a project-wide inventory of user-visible
    messages, validation prose, error descriptions, status labels, repeated magic
    strings and configurable values across backend, frontend and mobile, with a P0/P1/P2
    classification, a documented externalization strategy per stack, and the safe/small
    findings remediated directly.

    '
target_state:
  preferred_open_source_tooling:
  - backend message catalog or resource bundle strategy
  - stable API/domain error codes
  - frontend localization dictionaries or message catalog
  - mobile localization resources
  - literal-string and magic-value scan evidence
remediation:
  strategy: immediate_quality_alignment_before_mvp_mod_004_fe_001
  recommended_trigger:
  - HOP-QA-ALIGN-005
  acceptance_criteria:
  - Message externalization inventory exists in YAML and Markdown.
  - Backend, frontend and mobile strategies are documented.
  - Findings are remediated or assigned immediate technical-debt targets.
closure:
  closed_by_backlog_item: HOP-QA-ALIGN-005
  closure_note: 'All three acceptance criteria met. Inventory: 08-qa/qa/quality-alignment/
    HOP-QA-ALIGN-005-message-externalization-inventory.md (+ .md), covering ~130
    user-visible strings, 34 validation/error messages, 5 status-label representations,
    11 API route prefixes, 1 permission/scope union, 7 query-key literals, 8 repeated
    magic-string/number clusters and 7 configurable-business-value clusters in employee-portal;
    a parallel, smaller inventory for mobile-app; and all 33 backend domain error
    codes modeled in the five bcm-lab-001/bcm-att-001/ bcm-att-003/bcm-att-004/bcm-att-006
    openapi-source.md error_model.domain_errors blocks (30 with a runtime throw
    site, 3 authorization-scope codes not yet enforced by a throw site). Strategies
    documented per stack in the inventory Markdown (backend: stable-code-prefixed
    exceptions today, resource-bundle-backed message catalog as the next step; frontend:
    shared message/constant modules today, react-i18next or format.js as the next
    step; mobile: shared message module today, full localization resources once a
    renderer exists). Remediated directly and safely: all 30 backend runtime throw
    sites in frontdeskcaredelivery now reference a new FrontDeskErrorCodes constants
    class instead of inline string literals (behavior-preserving, verified by the
    unchanged 77-test backend suite); employee-portal''s 10 repeated "Select a <entity>
    first." messages, the "Unexpected error. Please try again." fallback (2 occurrences)
    and the duplicated 0.85/0.5 confidence-threshold function now live in src/i18n/messages.ts
    and src/i18n/matching.ts; the three independently-declared idle/loading/success/error
    unions converged on the single exported AsyncStatus type; mobile-app''s 6 validation
    messages now live in src/i18n/messages.ts. Remaining, larger-scope work (backend
    code field in API error responses, full frontend i18n library adoption for the
    ~130 single-occurrence UI strings, full mobile localization once a renderer exists)
    is reasonable to defer past this baseline and is tracked by the new TD-I18N-002.'
new_debt_registered:
- TD-I18N-002
```

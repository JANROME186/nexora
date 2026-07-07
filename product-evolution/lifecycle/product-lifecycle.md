# Product Lifecycle Model

**Artifact ID:** PLC-001  
**Status:** Draft  
**Version:** 0.22.0

## Lifecycle States

Every product capability, API, module, connector, country pack, healthcare pack, UI feature, AI capability and integration must have a lifecycle state.

| State | Meaning |
|---|---|
| Idea | Concept exists but is not approved. |
| Discovery | Business value and feasibility are being explored. |
| Analysis | Requirements, rules and impact are being documented. |
| Architecture | Architecture, data, security and integration models are being defined. |
| Design | UX, API, domain and data designs are being prepared. |
| Build | Implementation is in progress. |
| Alpha | Internal validation only. |
| Beta | Controlled customer validation. |
| GA | Generally available. |
| Maintenance | Stable, supported and maintained. |
| Deprecated | Still supported but planned for removal or replacement. |
| Removed | No longer available. |

## Governance Rules

1. No artifact may move to Build without an approved business capability, domain impact and API/data impact assessment.
2. No public API may move to GA without a versioning and deprecation policy.
3. No feature may move to GA without feature flag and rollback strategy.
4. No AI feature may move to Beta without clinical guardrails and privacy review.
5. No country pack may move to GA without regulatory owner approval.

## Lifecycle Metadata

Each machine-readable artifact should include:

```yaml
lifecycle:
  state: analysis
  introducedIn: 0.22.0
  deprecatedIn: null
  removedIn: null
  replacement: null
  supportPolicy: active
```

---
id: TD-BE-018
format: markdown_structured_payload
type: technical-debt-item
name: Marketplace entitlement policy, compatibility strategy, billing adapter and
  installation rollback orchestration are basic implementations only
version: 3.0.0
status: closed
---

# Marketplace Entitlement Policy, Compatibility Strategy, Billing Adapter And Installation Rollback Orchestration Are Basic Implementations Only

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-BE-018
  type: technical-debt-item
  name: Marketplace entitlement policy, compatibility strategy, billing adapter and
    installation rollback orchestration are basic implementations only
  version: 3.0.0
  status: closed
  created_date: 2026-07-24
  updated_date: 2026-07-25
source:
  discovered_during_backlog_item: COM-MOD-017-BE-001
  module: COM-MOD-017 Product Marketplace and Extension Packaging
  evidence: 08-qa/qa/product-marketplace-and-extension-packaging/COM-MOD-017-BE-001-validation.md
classification:
  category: backend_custom_rule_deferred
  affected_area: marketplace_entitlements_custom_implementation_points
  affected_components:
  - marketplaceentitlements.tenantentitlements.application.EntitlementPolicyEvaluator
  - marketplaceentitlements.compatibilityevaluation.application.CompatibilityEvaluator
  - marketplaceentitlements.billingadapter.adapter.out.adapter.LocalDeterministicBillingAdapter
  - marketplaceentitlements.packageinstallation.application.PackageInstallationService
  risk_level: low
  urgency: low
  blocking: false
  reason_non_blocking: Every BCM-PLT-011 openapi-source.md operation is functional
    end to end with no endpoint responding unimplemented; all five named
    custom_implementation_points are now closed (see remediation.acceptance_criteria
    below) -- the fifth was closed for real by COM-MOD-017-FE-001 via its follow-up
    item TD-BE-019, which COM-MOD-017-QA-001 confirmed closed on re-inspection.
current_state:
  issue: 'COM-MOD-017-BE-002 closed four of the five gaps this item named. EntitlementPolicyEvaluator
    now implements the full entitlement-policy.md evaluation_order (tenant_status,
    package_status, license_status, compatibility_status, iam_permission, feature_flag,
    clinical_safety_control, usage_limit); CompatibilityEvaluator evaluates all 9
    compatibility.md dimensions via a new declared-metadata parser; the billing adapter
    boundary gained retry/idempotency keyed on providerReference, mirroring FiscalAdapterPort''s
    submit/retry pattern; and packageinstallation gained a persisted multi-step InstallationStep
    audit trail that rollback now derives its target version from. The fifth point
    -- runtime feature-availability integration with IAM permission evaluation and
    employee-portal menu generation -- remains open: no employee-portal marketplace
    screens exist yet (App.tsx''s SCREEN_COMPONENTS map requires one React component
    per ScreenKey, so the 4 marketplace screens cannot be wired into permissions.ts/AppShell.tsx
    without also building those screens, which is a COM-MOD-017-FE-001-scale deliverable
    per generation-plan.md''s own employee_portal output list, not a BE-002 custom_implementation_point).
    A design constraint was also confirmed during BE-002: marketplaceentitlements cannot
    depend on both identityaccess and platformconfiguration while either of them also
    depends back on marketplaceentitlements, or Spring Modulith''s cycle detection
    (PlatformFoundationModulithTest) fails the build; the iam_permission/feature_flag
    evaluation_order steps were therefore implemented as a policy-decision-point taking
    pre-resolved facts (EntitlementEvaluationRequest.permissionGranted/featureFlagEnabled)
    rather than pulling from identityaccess/platformconfiguration via a port, keeping
    the module boundary acyclic.'
target_state:
  preferred_remediation: See TD-BE-019 for the remaining runtime-feature-availability/IAM/menu
    scope, targeted at COM-MOD-017-FE-001 once marketplace employee-portal screens
    exist.
  quality_goal: Match the precedent set by MVP-MOD-005 (fiscal adapter), MVP-MOD-008
    (integration adapter) and COM-MOD-011 (rate-limit policy), where BE-001 compiled
    a basic generated boundary and BE-002 delivered the mature custom-rule implementation.
    Achieved for 4 of 5 named points.
remediation:
  strategy: closed_by_COM_MOD_017_BE_002_plus_COM_MOD_017_FE_001_TD_BE_019_closure_confirmed_by_COM_MOD_017_QA_001
  owner: backend_team
  estimated_effort: none_remaining
  estimated_cost_impact: low
  target_backlog: COM-MOD-017-BE-002
  dependencies_or_prerequisites:
  - COM-MOD-017-BE-001 generated backend outputs (closed).
  acceptance_criteria:
  - id: EntitlementPolicyEvaluator implements the full entitlement-policy.md evaluation_order.
    status: closed
  - id: CompatibilityEvaluator evaluates all 9 compatibility.md dimensions.
    status: closed
  - id: Billing adapter boundary supports retry/idempotency without becoming a domain
      source of truth (INV-MKT-003 preserved).
    status: closed
  - id: Installation rollback preserves a real multi-step audit trail, not a single
      checkpoint field.
    status: closed
  - id: A runtime feature-availability check gates at least one IAM permission or
      employee-portal menu decision.
    status: closed
    repointed_to: TD-BE-019
    closure_note: TD-BE-019 closed by COM-MOD-017-FE-001 (marketplace ScreenKey/permission
      navigation wiring plus the entitlement-gated install control in MarketplaceInstallationsScreen);
      COM-MOD-017-QA-001 re-verified TD-BE-019's evidence and confirmed both of its
      acceptance criteria hold.
closure:
  status: closed
  closed_by_backlog_item: COM-MOD-017-QA-001
  closed_date: 2026-07-25
  mechanism: All 5 of the 5 originally named custom_implementation_points are closed.
    The first 4 were closed directly by COM-MOD-017-BE-002. The 5th was repointed
    to TD-BE-019 rather than forced, and TD-BE-019 was subsequently closed for real
    by COM-MOD-017-FE-001. This item's own status had not yet been synced to reflect
    that chained closure; COM-MOD-017-QA-001 corrected it as its debt-first action.
```

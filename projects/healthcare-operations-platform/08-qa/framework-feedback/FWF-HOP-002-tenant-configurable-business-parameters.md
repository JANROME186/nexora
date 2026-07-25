---
id: FWF-HOP-002
format: markdown_structured_payload
type: project-framework-feedback-item
name: Capability packages lack a modeled surface for tenant-configurable business
  parameters
version: 1.0.0
status: proposed
---

# Capability Packages Lack A Modeled Surface For Tenant Configurable Business Parameters

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: FWF-HOP-002
  type: project-framework-feedback-item
  name: Capability packages lack a modeled surface for tenant-configurable business
    parameters
  version: 1.0.0
  status: proposed
  created_date: 2026-07-09
source:
  source_project: healthcare-operations-platform
  source_backlog_item_or_phase: MVP-MOD-003-BE-002
  related_framework_backlog_item: null
classification:
  feedback_type: modeling_gap
  affected_framework_area: capability_package_standard
  priority_recommendation: P2_medium
  urgency: next_planning_cycle
observed_friction_or_gap: 'BCM-PER-001''s business-rules.md declares several rules
  whose exact thresholds are explicitly "tenant-configurable" in prose (duplicate-detection
  scoring weights and high-confidence threshold, age-of-majority years for representative-registration
  default, mandatory consent types), but none of the capability-package-standard''s
  model files (business-rules.md, permissions.md, generation-plan.md) provide
  a declarative way to express "this numeric/set parameter is tenant-overridable,
  here is its default and its owning capability." There is also no modeled REST configuration
  surface (no operation in any of the four packages'' openapi-source.md exposes
  reading/writing these parameters per tenant).

  '
evidence:
- bcm-per-001-person-management/business-rules.md RN-003 (duplicate scoring), RN-006
  (merge policy) and bcm-att-002-patient-registration/business-rules.md RN-008 (age-of-majority
  default) all use the word "tenant-configurable" or equivalent without a modeled
  parameter schema.
- Implementation had to invent an unmodeled in-memory policy-store component (TenantPeoplePolicyStore,
  PersonDuplicateScoringPolicy) with hardcoded safe defaults and no REST surface,
  because no capability package declared where such configuration should live or how
  it should be exposed/generated.
proposed_improvement: 'Extend the capability-package-standard with an optional "tenant_configurable_parameters"
  model file (or a section of business-rules.md) that lets a package declare named
  parameters (type, default, owning rule id, validation range) and optionally reference
  a generatable config-read/config-write operation pair in openapi-source.md, so
  tenant-tunable business thresholds become first-class generatable/traceable model
  elements instead of ad hoc implementation-time decisions.

  '
expected_benefit:
- Removes a recurring implementation-time judgment call (where do tenant-tunable thresholds
  live) that otherwise differs project to project and agent to agent.
- Makes tenant-configurable business parameters traceable in traceability.md like
  any other modeled element.
- Enables generation-plan.md to optionally generate the REST config surface instead
  of leaving it fully custom.
impact_assessment:
  impacted_project_types:
  - all_nexora_projects_with_multi_tenant_configurable_business_rules
  impact_on_project_delivery: medium
  recurrence_probability: likely_in_other_modules_with_multi_tenant_scoring_or_thresholds
  framework_reusability: all_projects
  estimated_effort: medium
  risk_if_not_addressed: 'Each project keeps re-deciding, ad hoc, how tenant-configurable
    business parameters are stored and exposed, leading to inconsistent patterns across
    capability packages and projects.

    '
decision:
  central_backlog_item: null
  status: proposed
```

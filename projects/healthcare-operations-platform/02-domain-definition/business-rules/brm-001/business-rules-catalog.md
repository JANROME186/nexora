# BRM-001 — Business Rules Catalog

## Purpose

BRM-001 defines the minimum cross-module rules required to start MVP development without leaving security, clinical validation, audit or integration behavior ambiguous.

## Rule Categories

- Security
- Authorization
- Domain ownership
- Catalog
- Revenue
- Clinical traceability
- Clinical validation
- Clinical safety
- Privacy
- Integration
- AI governance
- Audit

## Development Start Rule

Implementation may start when the rules marked in `minimum_rules_for_development_start` are loaded into the module backlog and translated into tests or acceptance criteria.

## Non-Goals

BRM-001 does not define every laboratory-specific clinical rule. Detailed analyte rules, reference ranges and abnormality thresholds belong to catalog configuration.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: BRM-001
  type: business-rules-catalog
  name: Healthcare Operations Platform MVP Business Rules Catalog
  version: 1.0.0
  status: draft
  owner: Product Architecture Team
  source_of_truth: 02-domain-definition/business-rules/brm-001/business-rules-catalog.md
  depends_on:
  - ACM-001
  - HRP-001
  - BCM-001
  - BCM-002
rules:
- id: BRM-001-R001
  name: Protected actions require authenticated actor
  category: security
  applies_to:
  - all_mvp_modules
  statement: Every protected operation must execute under an authenticated human or
    service actor.
  enforcement: application_service
  audit_required: true
- id: BRM-001-R002
  name: Role assignment must be scoped
  category: authorization
  applies_to:
  - MVP-MOD-001
  statement: Role assignments must include tenant, laboratory, branch or explicitly
    platform scope.
  enforcement: domain_rule
  audit_required: true
- id: BRM-001-R003
  name: Patient master data ownership
  category: domain_ownership
  applies_to:
  - MVP-MOD-003
  - MVP-MOD-004
  - MVP-MOD-007
  statement: Only patient-management may mutate Patient aggregate state.
  enforcement: bounded_context_boundary
  audit_required: true
- id: BRM-001-R004
  name: Orders use patient snapshots
  category: clinical_operations
  applies_to:
  - MVP-MOD-004
  statement: Diagnostic orders must reference patient snapshots and patient ids, not
    direct patient aggregate mutation.
  enforcement: domain_rule
  audit_required: true
- id: BRM-001-R005
  name: Only published catalog items can be ordered
  category: catalog
  applies_to:
  - MVP-MOD-002
  - MVP-MOD-004
  statement: Draft, deprecated or unpublished tests and panels cannot be added to
    new diagnostic orders.
  enforcement: domain_rule
  audit_required: true
- id: BRM-001-R006
  name: Order pricing requires snapshot
  category: revenue
  applies_to:
  - MVP-MOD-004
  - MVP-MOD-005
  statement: Accepted orders must preserve a price snapshot used by cash and billing
    workflows.
  enforcement: domain_rule
  audit_required: true
- id: BRM-001-R007
  name: Payment requires active cash session
  category: revenue
  applies_to:
  - MVP-MOD-005
  statement: Cashier payment registration requires an active cash session for the
    actor and branch.
  enforcement: application_service
  audit_required: true
- id: BRM-001-R008
  name: Billing uses fiscal adapter boundary
  category: integration
  applies_to:
  - MVP-MOD-005
  - MVP-MOD-008
  statement: Fiscal invoice requests and cancellations must pass through country-pack
    adapter interfaces.
  enforcement: adapter_boundary
  audit_required: true
- id: BRM-001-R009
  name: Sample must trace to order and collector
  category: clinical_traceability
  applies_to:
  - MVP-MOD-006
  statement: Every collected sample must reference order, patient snapshot, branch,
    collector and collection time.
  enforcement: domain_rule
  audit_required: true
- id: BRM-001-R010
  name: Rejected samples block dependent result release
  category: clinical_operations
  applies_to:
  - MVP-MOD-006
  statement: Results dependent on rejected samples cannot be released unless a replacement
    or override process is completed.
  enforcement: domain_rule
  audit_required: true
- id: BRM-001-R011
  name: Technical validation precedes medical validation
  category: clinical_validation
  applies_to:
  - MVP-MOD-006
  statement: Medical validation cannot be completed until technical validation is
    complete or explicitly waived by policy.
  enforcement: workflow_rule
  audit_required: true
- id: BRM-001-R012
  name: Medical validation required before release
  category: clinical_validation
  applies_to:
  - MVP-MOD-006
  - MVP-MOD-007
  statement: Laboratory results cannot be released to external actors before medical
    validation.
  enforcement: domain_rule
  audit_required: true
- id: BRM-001-R013
  name: Critical results require notification trace
  category: clinical_safety
  applies_to:
  - MVP-MOD-007
  statement: Critical results must create a traceable notification or escalation record.
  enforcement: workflow_rule
  audit_required: true
- id: BRM-001-R014
  name: External portals show released results only
  category: privacy
  applies_to:
  - MVP-MOD-007
  statement: Patient and doctor portals may display only released results authorized
    for the requesting actor.
  enforcement: authorization_policy
  audit_required: true
- id: BRM-001-R015
  name: Patient representative access requires authorization
  category: privacy
  applies_to:
  - MVP-MOD-003
  - MVP-MOD-007
  statement: Representatives may access patient information only when an active representative
    relationship exists.
  enforcement: authorization_policy
  audit_required: true
- id: BRM-001-R016
  name: Integration adapters cannot bypass validation
  category: integration
  applies_to:
  - MVP-MOD-008
  statement: External messages and migration records must be normalized and validated
    before reaching domain commands.
  enforcement: anti_corruption_layer
  audit_required: true
- id: BRM-001-R017
  name: AI cannot make clinical decisions
  category: ai_governance
  applies_to:
  - all_mvp_modules
  statement: AI may assist or summarize, but cannot validate, release, amend or diagnose
    clinical results.
  enforcement: governance_rule
  audit_required: true
- id: BRM-001-R018
  name: Audit records are append-only
  category: audit
  applies_to:
  - all_mvp_modules
  statement: Audit events must be immutable and corrections must be represented as
    additional events.
  enforcement: infrastructure_policy
  audit_required: true
coverage:
  minimum_rules_for_development_start:
  - BRM-001-R001
  - BRM-001-R002
  - BRM-001-R003
  - BRM-001-R005
  - BRM-001-R007
  - BRM-001-R009
  - BRM-001-R012
  - BRM-001-R014
  - BRM-001-R016
  - BRM-001-R018
  open_items:
  - Country-specific fiscal rules must be added through country packs.
  - Detailed analyte-specific clinical rules belong to catalog configuration.
```

# Nexora Context Map

## Purpose

The Context Map defines the formal relationships between bounded contexts in Nexora.

Its purpose is to avoid duplicated ownership, hidden dependencies, unclear integrations and direct access to foreign domain models.

## Mandatory Rules

1. A bounded context owns its aggregates.
2. Other contexts may reference external aggregates only through stable identifiers or snapshots.
3. Cross-context behavior must use APIs, events, commands, published language or anti-corruption layers.
4. External protocols must be translated before entering domain logic.
5. AI, migration and integration contexts are never allowed to bypass validation or authorization.

## Strategic Relationships

### Identity ↔ Organization

Relationship: Shared Kernel.

Shared concepts:
- TenantId
- LaboratoryId
- BranchId
- UserId
- PermissionCode

### Orders ↔ Patient

Relationship: Customer/Supplier.

Orders consumes:
- PatientId
- PatientSnapshot
- PatientEligibilityStatus

Orders cannot mutate patient master data.

### Orders ↔ Catalog/Test Configuration

Relationship: Customer/Supplier.

Orders consumes:
- TestDefinition
- PanelDefinition
- SampleRequirement
- PreparationInstruction

### Results ↔ Orders/Samples

Relationship: Conformist with Published Language.

Results follows upstream clinical lifecycle events such as:
- OrderAccepted
- SampleCollected
- SampleRejected
- OrderCancelled

### Billing ↔ Cash

Relationship: Customer/Supplier.

Billing consumes fiscal-eligible sale and payment events.

### Migration ↔ Core Contexts

Relationship: Anti-Corruption Layer.

Migration imports must pass through:
- Universal Import Model
- Canonical Data Model
- Domain validation
- Reconciliation

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: CTX-MAP-001
  type: context-map
  name: Nexora Context Map
  version: 1.0.0
  status: approved
  owner: Architecture Domain Team
  source_of_truth: 02-domain-definition/domain-foundation/context-map/context-map.md
  generated_artifacts:
  - 04-generated/diagrams/domain/context-map.mmd
  - 04-generated/markdown/domain/context-map.md
  supersedes: []
  dependencies:
  - BC-CATALOG-001
principles:
- No bounded context may directly modify another context aggregate.
- Cross-context communication must use published language, domain events, APIs, or
  anti-corruption layers.
- Shared kernel must remain small, stable and explicitly versioned.
- Data duplication is allowed only as read model projection, never as independent
  source of truth.
- Migration and interoperability adapters must never bypass domain validation.
relationships:
- id: REL-CTX-001
  source: identity-access
  target: organization-management
  relationship: shared-kernel
  shared_concepts:
  - TenantId
  - LaboratoryId
  - BranchId
  - UserId
  - PermissionCode
  notes: Identity and organization contexts share stable identifiers and authorization
    concepts.
- id: REL-CTX-002
  source: orders-samples
  target: patient-management
  relationship: customer-supplier
  supplier: patient-management
  published_language:
  - PatientSnapshot
  - PatientId
  - PatientEligibilityStatus
  notes: Orders consumes patient identity and eligibility but cannot mutate patient
    master data.
- id: REL-CTX-003
  source: orders-samples
  target: catalog-test-configuration
  relationship: customer-supplier
  supplier: catalog-test-configuration
  published_language:
  - TestDefinition
  - PanelDefinition
  - SampleRequirement
  - PreparationInstruction
  notes: Orders depends on configured tests, sample requirements and patient preparation
    instructions.
- id: REL-CTX-004
  source: laboratory-results
  target: orders-samples
  relationship: conformist-with-published-language
  published_language:
  - OrderAccepted
  - SampleCollected
  - SampleRejected
  - OrderCancelled
  notes: Results follows the order/sample lifecycle as its upstream process.
- id: REL-CTX-005
  source: billing-tax
  target: cash-sales
  relationship: customer-supplier
  supplier: cash-sales
  published_language:
  - SaleCompleted
  - PaymentRegistered
  - RefundApproved
  - CashCancellationApproved
  notes: Billing is triggered by fiscal-eligible sales and payments.
- id: REL-CTX-006
  source: cash-sales
  target: orders-samples
  relationship: customer-supplier
  supplier: orders-samples
  published_language:
  - DiagnosticOrderCreated
  - OrderPriceCalculated
  - OrderCancelled
  notes: Cash depends on order pricing but cannot change clinical order state except
    through approved commands.
- id: REL-CTX-007
  source: inventory-procurement
  target: catalog-test-configuration
  relationship: customer-supplier
  supplier: catalog-test-configuration
  published_language:
  - ConsumableRequirement
  - ReagentRequirement
  - TestConsumptionProfile
  notes: Inventory uses consumption profiles to estimate and register reagent usage.
- id: REL-CTX-008
  source: imaging-operations
  target: orders-samples
  relationship: conformist-with-published-language
  published_language:
  - DiagnosticOrderCreated
  - ImagingStudyRequested
  - OrderCancelled
  notes: Imaging consumes imaging study orders and maintains its own DICOM/PACS lifecycle.
- id: REL-CTX-009
  source: ai-platform
  target: clinical-contexts
  relationship: anti-corruption-layer
  published_language:
  - DeidentifiedClinicalContext
  - AIRecommendation
  - AIConfidenceScore
  notes: AI must operate through privacy-safe abstractions and cannot directly modify
    clinical source data.
- id: REL-CTX-010
  source: data-migration-portability
  target: all-core-contexts
  relationship: anti-corruption-layer
  published_language:
  - UniversalImportRecord
  - CanonicalDataRecord
  - MigrationValidationReport
  notes: Migration transforms external formats into canonical records before domain
    import.
- id: REL-CTX-011
  source: integration-interoperability
  target: all-core-contexts
  relationship: anti-corruption-layer
  published_language:
  - ExternalMessageEnvelope
  - NormalizedClinicalMessage
  - IntegrationAcknowledgement
  notes: External protocols such as HL7, ASTM, FHIR and DICOM are normalized before
    reaching domains.
- id: REL-CTX-012
  source: notifications
  target: all-event-producing-contexts
  relationship: published-language-subscriber
  published_language:
  - NotificationRequested
  - NotificationTemplate
  - RecipientPreference
  notes: Notifications subscribes to events but does not own business decisions.
- id: REL-CTX-013
  source: audit-compliance
  target: all-contexts
  relationship: published-language-subscriber
  published_language:
  - AuditEvent
  - ComplianceTrace
  - AccessLog
  notes: Audit receives immutable traces from all contexts.
forbidden_relationships:
- id: FORBID-CTX-001
  description: Billing must not directly update Patient master data.
- id: FORBID-CTX-002
  description: AI must not directly release, validate or amend clinical results.
- id: FORBID-CTX-003
  description: Migration adapters must not write directly to persistence tables.
- id: FORBID-CTX-004
  description: External integration protocols must not bypass authorization and validation.
- id: FORBID-CTX-005
  description: Read models must not become independent sources of truth.
```

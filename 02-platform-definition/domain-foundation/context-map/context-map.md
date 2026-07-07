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

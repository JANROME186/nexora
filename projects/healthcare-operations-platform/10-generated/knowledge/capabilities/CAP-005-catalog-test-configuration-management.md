---
id: CAP-005
format: markdown_structured_payload
type: business_capability
name: Catalog & Test Configuration Management
version: 0.27.0
status: draft
---

# Catalog & Test Configuration Management

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: CAP-005
name: Catalog & Test Configuration Management
type: business_capability
status: draft
version: 0.27.0
owner:
- Product
- Clinical Operations
- Laboratory Operations
- Compliance
- Data Governance
principles:
- Configuration over Customization
- API Contract First
- Specification Driven Development
- Multi-Tenant
- Localization First
- Clinical Traceability
- Auditability
- Progressive Experience
relationships:
  depends_on:
  - CAP-002
  - CAP-003
  - CAP-004
  enables:
  - CAP-006 Order Management
  - CAP-007 Sample Collection
  - CAP-008 Results & Reporting
  - CAP-009 Billing & Cashier
  - CAP-010 Inventory Management
  - CAP-011 Patient Portal
  - CAP-012 Doctor Portal
  contracts:
  - API-CAT-001
  - API-TCFG-001
  entities:
  - ENT-CAT-001 CatalogDefinition
  - ENT-CAT-002 CatalogValue
  - ENT-CAT-003 StudyConfiguration
  - ENT-CAT-004 StudyVersion
  - ENT-CAT-005 StudyComponent
  - ENT-CAT-006 AnalyteDefinition
  - ENT-CAT-007 SampleRequirement
  - ENT-CAT-008 PreparationInstruction
  - ENT-CAT-009 ReferenceRangeSet
  - ENT-CAT-010 ReferenceRange
  - ENT-CAT-011 FormulaDefinition
  - ENT-CAT-012 ResultFieldDefinition
  - ENT-CAT-013 ReportTemplateDefinition
  - ENT-CAT-014 TemplateSection
  - ENT-CAT-015 LocalizationEntry
  - ENT-CAT-016 BranchOverride
  - ENT-CAT-017 PriceListReference
  - ENT-CAT-018 CatalogImportBatch
  events:
  - EVT-CAT-001 CatalogCreated
  - EVT-CAT-002 CatalogValueAdded
  - EVT-CAT-003 StudyConfigurationCreated
  - EVT-CAT-004 StudyConfigurationSubmittedForReview
  - EVT-CAT-005 StudyConfigurationApproved
  - EVT-CAT-006 StudyConfigurationPublished
  - EVT-CAT-007 StudyConfigurationDeprecated
  - EVT-CAT-008 ReferenceRangeConfigured
  - EVT-CAT-009 FormulaValidated
  - EVT-CAT-010 ReportTemplateLinked
  - EVT-CAT-011 PriceReferenceLinked
  - EVT-CAT-012 CatalogImportValidated
  - EVT-CAT-013 CatalogImportRejected
source_of_truth:
  capability: business/capabilities/catalog-test-configuration-management/README.md
  machine_model: business/capabilities/catalog-test-configuration-management/capability.md
  catalog_contract: 05-contracts/contracts/openapi/catalogs/catalogs.openapi.md
  test_configuration_contract: 05-contracts/contracts/openapi/test-configuration/test-configuration.openapi.md
  knowledge_node: knowledge/capabilities/CAP-005-catalog-test-configuration-management.md
  adr: adr/ADR-016-catalog-test-configuration-as-configuration-engine.md
quality_gates:
- published_versions_are_immutable
- no_overlapping_reference_ranges
- formulas_reference_published_analytes
- openapi_contract_exists
- audit_events_defined
- permissions_defined
- localization_supported
```

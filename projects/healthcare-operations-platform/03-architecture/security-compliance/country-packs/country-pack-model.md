---
id: CP-ARCH-001
name: Country Pack Model
version: 0.19.0
status: Draft
owner: Product Architecture
artifact_type: country_pack_architecture
---

# Country Pack Model

## Objective

Allow Nexora to adapt to local country requirements without changing the core product.

## Country Pack examples

- `country-mx`: Mexico.
- `country-co`: Colombia.
- `country-pe`: Peru.
- `country-cl`: Chile.
- `country-eu`: European Union-oriented profile.

## Pack contents

A Country Pack may include:

- Translations.
- Fiscal rules.
- Invoice integrations.
- Consent templates.
- Privacy workflows.
- Required catalogs.
- Address formats.
- Identity document types.
- Report legal footer templates.
- Retention rules.
- Regulatory labels.

## Rule

The core domain must depend on compliance capabilities, not on country-specific implementations.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: CP-ARCH-001
name: Country Pack Model
version: 0.19.0
status: Draft
examples:
- country-mx
- country-co
- country-pe
- country-cl
- country-eu
contents:
- translations
- fiscal_rules
- invoice_integrations
- consent_templates
- privacy_workflows
- required_catalogs
- address_formats
- identity_document_types
- report_legal_footers
- retention_rules
- regulatory_labels
core_dependency_rule: core_depends_on_capabilities_not_country_implementations
```

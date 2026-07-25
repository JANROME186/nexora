# Country Pack Architecture

**Artifact ID:** CPK-001
**Status:** Draft
**Version:** 0.22.0

## Purpose

Country Packs allow Nexora to adapt to local regulatory, fiscal, language, formatting and catalog requirements without changing the core product.

## Country Pack Responsibilities

- Fiscal rules.
- Invoice provider integration.
- Tax identifiers.
- Address formats.
- Legal consent templates.
- Data retention rules.
- Language variants.
- Regulatory reports.
- Local catalog mappings.
- Payment method rules.

## Examples

### Mexico Pack

- CFDI integration.
- RFC validation.
- SAT catalogs.
- Mexican address structure.
- Spanish Mexico localization.

### Colombia Pack

- DIAN integration.
- NIT validation.
- Colombian geographic catalogs.

### Peru Pack

- SUNAT integration.
- RUC validation.

## Design Rule

Country-specific behavior must be provided by country packs, not hard-coded inside the core domain.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: CPK-001
name: Country Pack Architecture
type: country-pack-architecture
version: 0.22.0
status: draft
responsibilities:
- fiscalRules
- invoiceProviderIntegration
- taxIdentifiers
- addressFormats
- legalConsentTemplates
- dataRetentionRules
- languageVariants
- regulatoryReports
- localCatalogMappings
- paymentMethodRules
examples:
  mexico:
  - cfdiIntegration
  - rfcValidation
  - satCatalogs
  - mexicanAddressStructure
  - es-MXLocalization
  colombia:
  - dianIntegration
  - nitValidation
  - geographicCatalogs
  peru:
  - sunatIntegration
  - rucValidation
design_rule: Country-specific behavior must be provided by country packs, not hard-coded
  inside the core domain.
```

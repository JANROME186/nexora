---
id: CAP-009
format: markdown_structured_payload
name: Billing, Invoicing & Tax Compliance
version: 0.31.0
status: Draft
---

# Billing, Invoicing & Tax Compliance

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: CAP-009
name: Billing, Invoicing & Tax Compliance
version: 0.31.0
status: Draft
owner: Product / Finance / Compliance
classification: core-business-capability
stage: MVP-1-plus
dependsOn:
- CAP-002
- CAP-003
- CAP-006
- CAP-008
supports:
- country-packs
- licensing-engine
- marketplace-tax-connectors
sourceOfTruth:
  businessRules: capability-library/CAP-009-billing-invoicing-tax-compliance/02-business-rules.md
  openapi: contracts/billing/openapi.md
  dataModel: capability-library/CAP-009-billing-invoicing-tax-compliance/08-entities.md
  stateMachines: capability-library/CAP-009-billing-invoicing-tax-compliance/04-state-machines.md
entities:
- Invoice
- InvoiceLine
- TaxLine
- FiscalProfile
- FiscalAddress
- FiscalDocument
- FiscalProviderSubmission
- FolioSequence
- TaxConfiguration
- CreditNote
- InvoiceDelivery
- CancellationRequest
events:
- InvoiceIssueRequested
- InvoiceDraftCreated
- TaxCalculated
- FolioReserved
- InvoiceSubmittedToProvider
- InvoiceIssued
- InvoiceIssueFailed
- InvoiceDelivered
- InvoiceCancellationRequested
- InvoiceCancelled
- InvoiceCancellationRejected
apis:
- Billing API
qualityGates:
- openapi-contract-valid
- fiscal-state-machine-valid
- tenant-isolation-tested
- idempotency-tested
- audit-trail-tested
```

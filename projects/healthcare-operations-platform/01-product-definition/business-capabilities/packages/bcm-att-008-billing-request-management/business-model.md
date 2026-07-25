---
id: HOP-BM-BCM-ATT-008
format: markdown_structured_payload
type: business-model
name: Billing Request Management Business Model
version: 0.1.0
status: modeled
---

# Billing Request Management Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-ATT-008
  type: business-model
  name: Billing Request Management Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-008
  bounded_context: billing-tax
  primary_aggregate: AGG-012 Invoice
entities:
- id: ENT-BILL-001
  name: InvoiceRequest
  is_aggregate_root: true
  aggregate_ref: AGG-012
  description: Provider-agnostic fiscal billing request linked to a Sale and patient
    fiscal snapshot.
  fields:
  - name: invoiceRequestId
    type: InvoiceRequestId
    required: true
    identifier: true
  - name: tenantId
    type: TenantId
    required: true
  - name: laboratoryId
    type: LaboratoryId
    required: true
  - name: branchId
    type: BranchId
    required: true
  - name: saleId
    type: SaleId
    required: true
  - name: patientId
    type: PatientId
    required: true
  - name: fiscalProfileSnapshot
    type: FiscalProfileSnapshot
    required: true
  - name: taxLines
    type: list[TaxLine]
    required: true
  - name: status
    type: enum
    values: requested, submitted, issued, failed, cancelled
    required: true
  - name: adapterCorrelationId
    type: string
    required: false
  - name: adapterResponseSnapshot
    type: AdapterResponseSnapshot
    required: false
  - name: audit
    type: AuditMetadata
    required: true
value_objects:
- id: VO-BILL-001
  name: FiscalProfileSnapshot
  fields:
  - name: legalName
    type: string
    required: true
  - name: taxIdentifier
    type: string
    required: true
  - name: fiscalAddress
    type: string
    required: true
  - name: fiscalRegime
    type: string
    required: false
  - name: capturedAt
    type: datetime
    required: true
- id: VO-BILL-002
  name: TaxLine
  fields:
  - name: baseAmount
    type: Money
    required: true
  - name: taxCode
    type: string
    required: true
  - name: taxRate
    type: decimal
    required: true
  - name: taxAmount
    type: Money
    required: true
- id: VO-BILL-003
  name: AdapterResponseSnapshot
  fields:
  - name: providerCode
    type: string
    required: true
  - name: externalInvoiceId
    type: string
    required: false
  - name: statusCode
    type: string
    required: true
  - name: receivedAt
    type: datetime
    required: true
invariants:
- id: INV-BILL-001
  statement: An invoice request must reference an existing Sale from BCM-ATT-005.
- id: INV-BILL-002
  statement: Fiscal profile data is captured as an immutable snapshot at request time.
- id: INV-BILL-003
  statement: Country-specific fiscal connector state must remain behind the billing
    adapter boundary.
- id: INV-BILL-004
  statement: Issued or cancelled invoice requests are terminal and cannot be resubmitted.
external_references:
- shared_kernel:
  - TenantId
  - LaboratoryId
  - BranchId
  - PatientId
  - SaleId
  - Money
  - AuditMetadata
- capabilities:
  - BCM-ATT-005
  - BCM-PER-002
  - BCM-PLT-004
  - BCM-PLT-007
```

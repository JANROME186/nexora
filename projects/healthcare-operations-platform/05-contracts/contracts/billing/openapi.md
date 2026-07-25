---
id: openapi
format: markdown_structured_payload
---

# Openapi

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
openapi: 3.0.3
info:
  title: Nexora Billing API
  version: 0.31.0
  description: API contract for billing, invoicing and tax compliance.
servers:
- url: https://api.nexora.local/v1
paths:
  /billing/fiscal-profiles:
    post:
      summary: Create fiscal profile
      operationId: createFiscalProfile
      tags:
      - Billing
      responses:
        '201':
          description: Fiscal profile created
    get:
      summary: Search fiscal profiles
      operationId: searchFiscalProfiles
      tags:
      - Billing
      responses:
        '200':
          description: Fiscal profiles found
  /billing/fiscal-profiles/{id}:
    get:
      summary: Get fiscal profile
      operationId: getFiscalProfile
      tags:
      - Billing
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: string
      responses:
        '200':
          description: Fiscal profile
    put:
      summary: Update fiscal profile
      operationId: updateFiscalProfile
      tags:
      - Billing
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: string
      responses:
        '200':
          description: Fiscal profile updated
  /billing/invoices:
    post:
      summary: Create invoice draft
      operationId: createInvoiceDraft
      tags:
      - Billing
      responses:
        '201':
          description: Invoice draft created
    get:
      summary: Search invoices
      operationId: searchInvoices
      tags:
      - Billing
      responses:
        '200':
          description: Invoices found
  /billing/invoices/{id}:
    get:
      summary: Get invoice detail
      operationId: getInvoice
      tags:
      - Billing
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: string
      responses:
        '200':
          description: Invoice detail
  /billing/invoices/{id}/issue:
    post:
      summary: Issue invoice
      operationId: issueInvoice
      tags:
      - Billing
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: string
      - name: Idempotency-Key
        in: header
        required: true
        schema:
          type: string
      responses:
        '202':
          description: Invoice issue requested
  /billing/invoices/{id}/retry:
    post:
      summary: Retry invoice issue
      operationId: retryInvoiceIssue
      tags:
      - Billing
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: string
      responses:
        '202':
          description: Retry requested
  /billing/invoices/{id}/cancel:
    post:
      summary: Cancel invoice
      operationId: cancelInvoice
      tags:
      - Billing
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: string
      responses:
        '202':
          description: Cancellation requested
  /billing/invoices/{id}/documents:
    get:
      summary: Get invoice documents
      operationId: getInvoiceDocuments
      tags:
      - Billing
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: string
      responses:
        '200':
          description: Fiscal documents
  /billing/invoices/{id}/deliver:
    post:
      summary: Deliver invoice documents
      operationId: deliverInvoice
      tags:
      - Billing
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: string
      responses:
        '202':
          description: Delivery requested
  /billing/folio-sequences:
    get:
      summary: List folio sequences
      operationId: listFolioSequences
      tags:
      - Billing
      responses:
        '200':
          description: Folio sequences
    post:
      summary: Create folio sequence
      operationId: createFolioSequence
      tags:
      - Billing
      responses:
        '201':
          description: Folio sequence created
components:
  schemas:
    InvoiceStatus:
      type: string
      enum:
      - Draft
      - PendingIssue
      - Issued
      - Failed
      - CancellationRequested
      - Cancelled
      - CancellationRejected
      - Replaced
    Invoice:
      type: object
      properties:
        id:
          type: string
        tenantId:
          type: string
        branchId:
          type: string
        status:
          $ref: '#/components/schemas/InvoiceStatus'
        total:
          type: number
          format: decimal
        currency:
          type: string
        createdAt:
          type: string
          format: date-time
    FiscalProfile:
      type: object
      properties:
        id:
          type: string
        ownerType:
          type: string
          enum:
          - Patient
          - Company
          - Payer
        ownerId:
          type: string
        countryCode:
          type: string
        taxIdentifier:
          type: string
        fiscalName:
          type: string
```

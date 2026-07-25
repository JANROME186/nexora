---
id: HOP-PERM-BCM-ATT-006
format: markdown_structured_payload
type: permissions
name: Quotation Management Permissions
version: 0.2.0
status: modeled
---

# Quotation Management Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-ATT-006
  type: permissions
  name: Quotation Management Permissions
  version: 0.2.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-006
  depends_on_capability: BCM-PLT-001
scopes:
- code: quotation.manage
  description: Draft, issue, accept, convert, cancel or expire a quotation.
- code: quotation.discount.override
  description: Apply a discount beyond the standard receptionist policy limit.
- code: quotation.read
  description: Read quotation history for audit and review.
- code: quotation.request.public
  description: Anonymous, rate-limited creation of a draft-state QuotationRequest
    from a ProspectiveContact via the COM-MOD-011 public website. Cannot issue, accept,
    convert or read other quotations.
roles:
- role: receptionist
  grants:
  - quotation.manage
  - quotation.read
- role: branch-administrator
  grants:
  - quotation.manage
  - quotation.discount.override
  - quotation.read
- role: tenant-administrator
  grants:
  - quotation.read
- role: public-website-visitor
  grants:
  - quotation.request.public
  authentication: anonymous
  governed_by: BCM-PLT-005 RateLimitPolicy (classification=public)
access_policies:
- id: POL-QUO-006-01
  statement: Quotation commands are scoped to the actor's tenant, laboratory and branch.
  enforcement: row_level_tenant_laboratory_branch_filter
- id: POL-QUO-006-02
  statement: A discount beyond the standard policy limit requires quotation.discount.override.
  enforcement: scope_escalation_policy
- id: POL-QUO-006-03
  statement: Converting a quotation must delegate order mutation to BCM-LAB-001 aggregate
    commands.
  enforcement: cross_capability_delegation_policy
- id: POL-QUO-006-04
  statement: Public, anonymous quotation requests (RN-009) can only create a draft-state
    quotation for the website's own tenant/branch context; they can never issue, accept,
    convert or read quotations belonging to others.
  enforcement: status_and_scope_restricted_public_write
audit_obligations:
  audit_sink: BCM-PLT-007
  events:
  - event: QuotationDrafted
    fields:
    - quotationId
    - actorId
    - branchId
  - event: QuotationIssued
    fields:
    - quotationId
    - actorId
    - totalAmount
    - validUntil
  - event: QuotationAccepted
    fields:
    - quotationId
    - actorId
    - totalAmount
  - event: QuotationConverted
    fields:
    - quotationId
    - convertedOrderId
    - actorId
  - event: QuotationClosed
    fields:
    - quotationId
    - actorId
    - reasonCode
```

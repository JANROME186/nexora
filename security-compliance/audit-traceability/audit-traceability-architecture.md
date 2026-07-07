---
id: AUD-ARCH-001
name: Audit and Traceability Architecture
version: 0.19.0
status: Draft
owner: Security Architecture
artifact_type: audit_architecture
---

# Audit and Traceability Architecture

## Objective

Ensure every clinically, financially, administratively, or security-relevant action can be traced.

## Audit event categories

- Identity and session events.
- User and permission changes.
- Patient record changes.
- Consent changes.
- Order lifecycle events.
- Sample lifecycle events.
- Result creation, modification, validation and release.
- Payment, cancellation and cash cut events.
- Invoice events.
- Inventory movements.
- AI usage events.
- Data export events.
- Administrative configuration changes.

## Audit event minimum fields

- auditEventId.
- timestamp.
- tenantId.
- branchId when applicable.
- actorType.
- actorId.
- action.
- resourceType.
- resourceId.
- beforeHash or beforeSnapshot when required.
- afterHash or afterSnapshot when required.
- reason when required.
- correlationId.
- requestId.
- sourceIp or device fingerprint when applicable.

## Clinical traceability

Result modification, validation, release, correction, and cancellation must always be traceable and should never overwrite historical evidence without an audit record.

## AI traceability

When AI assists in a clinical or operational process, the audit trail must record:

- AI capability used.
- Provider or model family when allowed.
- Input classification.
- Output classification.
- Human reviewer.
- Final decision.

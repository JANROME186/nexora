# External Result Ingestion Flow

**Artifact ID:** IIA-FLOW-001
**Version:** 0.20.0

## Flow

```text
Device / External LIS
  ↓
Protocol Adapter
  ↓
Payload Validation
  ↓
Canonical Message Transformation
  ↓
Order/Sample Matching
  ↓
Result Draft Creation
  ↓
Clinical Validation Queue
  ↓
Result Approved
  ↓
Patient/Doctor Delivery
```

## Events

- `ExternalResultMessageReceived`
- `ExternalResultMessageValidated`
- `ExternalResultMatchedToSample`
- `ExternalResultQuarantined`
- `ResultDraftCreated`
- `ResultValidationRequested`

## Failure Scenarios

- Invalid payload.
- Unknown device.
- Unmatched order.
- Unmatched sample.
- Duplicate result.
- Unsupported analyte code.
- Unit conversion required.

---
id: HOP-EVT-BCM-PER-001
format: markdown_structured_payload
type: events
name: Person Management Events
version: 0.1.0
status: modeled
---

# Person Management Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-PER-001
  type: events
  name: Person Management Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PER-001
domain_events:
- name: PersonDuplicateDetectionRequested
  description: A duplicate detection query was executed and returned candidate matches.
  payload:
  - detectionId
  - tenantId
  - actorId
  - candidateCount
  - topConfidenceScore
  audit: true
- name: PersonSearchIndexRebuilt
  description: The person search index was rebuilt from source aggregates.
  payload:
  - tenantId
  - actorId
  - fromEventOffset
  - toEventOffset
  - completedAt
  audit: true
- name: PersonMergeCoordinationCompleted
  description: A cross-context merge coordination completed for a person.
  payload:
  - coordinationId
  - tenantId
  - sourceAggregateType
  - sourceRecordId
  - targetRecordId
  - actorId
  audit: true
integration_events:
  published:
  - name: PersonReadModelInvalidated
    description: Signals downstream projections to refresh person search caches.
    consumers:
    - patient-management
    - medical-staff
    - orders-samples
  consumed:
  - name: PatientRegistered
    source: BCM-PER-002
  - name: PatientUpdated
    source: BCM-PER-002
  - name: PatientMerged
    source: BCM-PER-002
  - name: DoctorRegistered
    source: BCM-PER-003
  - name: DoctorCredentialVerified
    source: BCM-PER-003
  - name: DoctorSuspended
    source: BCM-PER-003
published_language:
- PersonNaturalKey
- PersonSearchIndex
- PersonDuplicateDetectionResult
```

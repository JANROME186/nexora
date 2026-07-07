# CAP-001 Patient Management - Business Rules

## Patient identity

| Rule ID | Rule | Priority |
|---|---|---|
| CAP-001-BR-001 | Every patient must belong to one laboratory tenant. | Critical |
| CAP-001-BR-002 | A patient may be visible in multiple branches only when tenant policy allows it. | High |
| CAP-001-BR-003 | A patient must have at least one primary identifier configured by the country pack or laboratory policy. | Critical |
| CAP-001-BR-004 | A patient record must not be physically deleted when clinical, billing or audit history exists. | Critical |
| CAP-001-BR-005 | Possible duplicate patients must be flagged before creating a new record when matching thresholds are reached. | High |

## Required data

| Rule ID | Rule | Priority |
|---|---|---|
| CAP-001-BR-010 | Full name is mandatory for administrative registration. | Critical |
| CAP-001-BR-011 | Date of birth is mandatory when age-dependent reference values or minor checks are required. | Critical |
| CAP-001-BR-012 | Sex at birth must be captured when clinical reference values depend on it. | High |
| CAP-001-BR-013 | Contact information is required when digital result delivery is enabled. | High |
| CAP-001-BR-014 | Communication consent is required before sending results or promotions through digital channels. | Critical |

## Minors and guardianship

| Rule ID | Rule | Priority |
|---|---|---|
| CAP-001-BR-020 | A minor patient must have at least one guardian or responsible party. | Critical |
| CAP-001-BR-021 | Guardian relationship type must be selected from an approved catalog. | High |
| CAP-001-BR-022 | A guardian may authorize consent only when the country pack and laboratory policy allow that role. | Critical |
| CAP-001-BR-023 | Patient portal access for minors must follow country and laboratory privacy rules. | Critical |

## Privacy and consent

| Rule ID | Rule | Priority |
|---|---|---|
| CAP-001-BR-030 | Patient consent records must be auditable and versioned. | Critical |
| CAP-001-BR-031 | Sensitive patient data must not be exposed to users without explicit permission. | Critical |
| CAP-001-BR-032 | Patient data exports must be logged with actor, purpose, timestamp and scope. | Critical |
| CAP-001-BR-033 | Result delivery channels must respect patient communication preferences. | High |

## Lifecycle

| Rule ID | Rule | Priority |
|---|---|---|
| CAP-001-BR-040 | An inactive patient cannot be used to create new orders unless reactivated. | High |
| CAP-001-BR-041 | A blocked patient requires supervisor override to create new orders. | Critical |
| CAP-001-BR-042 | Patient merge is not part of MVP 1 but all duplicates must be recorded for future reconciliation. | Medium |

# Business Rules

| ID | Rule | Severity |
|---|---|---|
| BR-RES-001 | A result cannot be published if the order is cancelled. | Critical |
| BR-RES-002 | A result cannot be delivered to a patient until it is clinically validated when the test requires validation. | Critical |
| BR-RES-003 | Critical values must generate an alert and require acknowledgment. | Critical |
| BR-RES-004 | Any amendment to a validated result must create a new result version and preserve the previous version. | Critical |
| BR-RES-005 | Only users with validation permission may clinically validate a result. | Critical |
| BR-RES-006 | Reference ranges must be evaluated using configured sex, age, units and test method when available. | High |
| BR-RES-007 | Calculated analytes must be recalculated when a dependent analyte changes. | High |
| BR-RES-008 | Reports must show laboratory identity, branch, patient, order, sample, test, result, reference ranges and responsible professional. | High |
| BR-RES-009 | Result delivery must be audited by channel, recipient, timestamp and actor. | High |
| BR-RES-010 | Patient portal access must not expose internal validation notes. | High |
| BR-RES-011 | Result status transitions must follow the approved state machine. | High |
| BR-RES-012 | Rejected samples must block final result generation unless an authorized override exists. | Critical |

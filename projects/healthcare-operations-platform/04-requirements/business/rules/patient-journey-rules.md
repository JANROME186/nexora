---
id: BRG-001
name: Patient Journey Business Rules
version: 0.1.0
status: Draft
owner: Business Architecture
---

# Patient Journey Business Rules

| ID | Rule | Priority | Related Artifacts |
|---|---|---|---|
| BR-001 | A patient must belong to a laboratory tenant before any order is created. | Must | CAP-001, CAP-007, ENT-001 |
| BR-002 | Patient identity must be confirmed before diagnostic order creation. | Must | BPMN-001, EVT-002 |
| BR-003 | A minor or legally dependent patient must have a guardian or responsible person linked. | Must | JRN-001, EVT-003 |
| BR-004 | Consent must be captured when required by study type, branch policy or country pack. | Must | EVT-004, CAP-011 |
| BR-005 | Preparation instructions must be shown before payment and before sample collection. | Should | CAP-006, CAP-008 |
| BR-006 | Orders must preserve status history and audit metadata. | Must | CAP-007, CAP-015 |
| BR-007 | A cancelled order must require cancellation reason and authorized user. | Must | CAP-007, CAP-010 |
| BR-008 | Critical result rules must trigger alerts without requiring manual refresh. | Must | CAP-009, CAP-014 |
| BR-009 | Patient portal access must require secure authentication and tenant isolation. | Must | CAP-012, CAP-015 |
| BR-010 | AI explanations for results must be presented as educational support and must not replace medical interpretation. | Must | CAP-022, Security, Compliance |

## Rule Governance

- Rules are versioned artifacts.
- A rule change requires impact analysis.
- A rule can be country-specific, laboratory-specific or global.
- Deprecated rules must remain traceable to historical orders and results.

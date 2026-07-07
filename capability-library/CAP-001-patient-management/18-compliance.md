# CAP-001 Patient Management - Compliance

## Compliance concerns

- Personal data protection.
- Sensitive clinical data protection.
- Consent traceability.
- Auditability of changes.
- Data minimization.
- Retention policies.
- Country-specific identity and consent requirements.

## Required controls

| Control ID | Control |
|---|---|
| CTRL-PAT-001 | Patient access must be permission controlled. |
| CTRL-PAT-002 | Sensitive fields must support masking. |
| CTRL-PAT-003 | All patient mutations must be audited. |
| CTRL-PAT-004 | Consent must include version, scope, actor and timestamp. |
| CTRL-PAT-005 | Data exports must be logged. |
| CTRL-PAT-006 | Patient deletion must be logical unless retention/legal policy permits physical deletion. |

## Country pack extension points

- Legal age threshold.
- Required patient identifiers.
- Required consent types.
- Data retention period.
- Electronic signature requirements.
- Patient data access/export rules.

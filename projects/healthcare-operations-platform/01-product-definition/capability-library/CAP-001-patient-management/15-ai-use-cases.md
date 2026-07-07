# CAP-001 Patient Management - AI Use Cases

## AI principles

- AI is an assistant, not the source of truth.
- AI must not make autonomous clinical/legal decisions.
- AI must respect privacy and tenant isolation.
- AI features must have fallback workflows.

## MVP AI candidates

| Use case ID | Use case | Risk | MVP status |
|---|---|---|---|
| AI-PAT-001 | Intake form completion assistance | Medium | Candidate |
| AI-PAT-002 | Duplicate explanation summary | Medium | Candidate |
| AI-PAT-003 | Patient profile summarization for authorized staff | Medium | Candidate |
| AI-PAT-004 | Consent explanation in simple language | Low | Candidate |
| AI-PAT-005 | Patient chatbot for profile update guidance | Medium | Later |

## Guardrails

- AI cannot create or modify patient records without user confirmation.
- AI cannot expose sensitive data to unauthorized users.
- AI outputs must be logged when used in operational decisions.
- AI must cite the source fields used for profile summaries when possible.

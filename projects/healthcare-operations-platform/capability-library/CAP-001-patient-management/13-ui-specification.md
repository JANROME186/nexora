# CAP-001 Patient Management - UI Specification

## Web principles

- Simple, functional and compatible with commercial browsers.
- Progressive enhancement without blocking core functionality.
- Low-resource rendering for basic patient workflows.
- Accessible forms, clear validation and keyboard navigation.
- Reusable components from Nexora Design System.

## MVP screens

| Screen ID | Screen | Channel |
|---|---|---|
| WEB-PAT-001 | Patient Search | Admin portal |
| WEB-PAT-002 | Patient Registration Form | Admin portal |
| WEB-PAT-003 | Patient Summary | Admin portal |
| WEB-PAT-004 | Patient Edit Profile | Admin portal |
| WEB-PAT-005 | Patient Consent Capture | Admin portal |
| WEB-PAT-006 | Guardian Capture | Admin portal |
| WEB-PAT-007 | Duplicate Patient Review | Admin portal |
| WEB-PAT-008 | Patient Portal Profile | Patient portal |

## UX requirements

- Forms must autosave draft only when supported by environment and policy.
- Validation messages must be field-level and human-readable.
- Critical actions must require confirmation.
- Sensitive fields must support masking.
- UI must not expose restricted fields even if API returns them accidentally.

## Progressive levels

| Level | Features |
|---|---|
| Core | Search, create, update, consent, guardian. |
| Enhanced | Duplicate review, timeline, masked sensitive data. |
| Intelligent | AI-assisted intake, suggestions, duplicate explanations. |

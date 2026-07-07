# CAP-001 Patient Management - Mobile Specification

## Mobile principles

- Support low, mid and high-range Android/iOS devices without sacrificing core usability.
- Keep core patient workflows lightweight.
- AI and advanced features must be progressive and optional.
- Offline-friendly patterns may be used for draft capture where safe.

## MVP mobile surfaces

| Screen ID | Screen | App |
|---|---|---|
| MOB-PAT-001 | Patient Profile | Patient App |
| MOB-PAT-002 | Edit Contact Info | Patient App |
| MOB-PAT-003 | Consent Preferences | Patient App |
| MOB-PAT-004 | Patient Search | Staff App |
| MOB-PAT-005 | Quick Patient Registration | Staff App |

## Low-resource requirements

- Avoid heavy animations in clinical/admin flows.
- Use paginated lists.
- Compress and resize uploaded documents/images.
- Use network retry and clear offline/error states.
- Do not require latest OS-only capabilities for core flows.

## Anti-obsolescence rule

Compatibility must not force Nexora to avoid modern security, IA, accessibility or usability patterns. Advanced capabilities may degrade gracefully, but core workflows must remain safe and usable.

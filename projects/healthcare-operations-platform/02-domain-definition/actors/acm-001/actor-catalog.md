# ACM-001 — Actor Catalog

## Purpose

ACM-001 defines the human, system and external actors required to implement the Healthcare Operations Platform MVP.

Actors are defined independently from any identity provider or application framework. Implementation teams may map them to roles, groups, claims or policies, but the source of truth is this catalog.

## Actor Groups

| Group | Description |
| --- | --- |
| internal_staff | Employees and operational users inside the diagnostic organization. |
| clinical_external | Referring physicians and external clinical collaborators. |
| patient_external | Patients and authorized representatives. |
| system_external | External systems, devices, fiscal services and integration partners. |
| platform_system | Internal platform services using scoped service identities. |

## Minimum MVP Roles

The MVP must support these roles before implementation can start safely:

- Platform Super Administrator
- Tenant Administrator
- Branch Administrator
- Receptionist
- Cashier
- Sample Collector
- Laboratory Technician
- Technical Validator
- Medical Validator
- Catalog Manager
- Referring Doctor
- Patient
- Patient Representative
- Integration Partner System
- Service Identity

## Access Scopes

Access must be scoped by context:

- `platform`
- `tenant`
- `laboratory`
- `branch`
- `assigned_patients`
- `represented_patients`
- `self`
- `contract_scoped`
- `device_scoped`
- `fiscal_document_scoped`
- `notification_scoped`
- `audit_scoped`

## Implementation Rule

No MVP endpoint should be generated without mapping it to at least one actor, one permission and one audit expectation.

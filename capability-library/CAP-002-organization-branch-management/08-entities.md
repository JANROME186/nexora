# 08 Entities

| ID | Entity | Type | Description |
|---|---|---|---|
| ENT-ORG-001 | Tenant | Aggregate Root | SaaS tenant representing an isolated laboratory customer. |
| ENT-ORG-002 | Organization | Aggregate Root | Legal/commercial organization under a tenant. |
| ENT-ORG-003 | Branch | Aggregate Root | Physical or operational diagnostic center. |
| ENT-ORG-004 | BranchAddress | Entity | Address assigned to a branch. |
| ENT-ORG-005 | BranchSchedule | Entity | Operating schedule for a branch. |
| ENT-ORG-006 | BranchService | Entity | Service enabled at branch level. |
| ENT-ORG-007 | OrganizationalUnit | Aggregate Root | Department or functional unit. |
| ENT-ORG-008 | Position | Entity | Role/position in the organization hierarchy. |
| ENT-ORG-009 | BranchContact | Entity | Contact information for a branch. |
| ENT-ORG-010 | BranchConfiguration | Entity | Operational configuration per branch. |
| ENT-ORG-011 | BranchHoliday | Entity | Branch-specific non-operating day. |
| ENT-ORG-012 | ServiceAvailability | Entity | Derived availability for service scheduling. |
| ENT-ORG-013 | TenantLocaleConfiguration | Entity | Locale, language, timezone and currency defaults. |
| ENT-ORG-014 | LegalTaxProfile | Entity | Country-specific fiscal identity. |
| ENT-ORG-015 | OrganizationAuditRecord | Entity | Organization change audit trail. |

## Entity Design Notes

- Tenant isolation is mandatory for every organization entity.
- Branch deletion must be logical when historical transactions exist.
- Address and tax data should support country pack extensions.
- Branch services are configuration data, not hardcoded capabilities.

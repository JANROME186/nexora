# 09 Domain Events

| ID | Event | Producer | Consumers |
|---|---|---|---|
| EVT-ORG-001 | TenantCreated | Organization Context | IAM, Billing, Licensing |
| EVT-ORG-002 | TenantActivated | Organization Context | IAM, Notification, Analytics |
| EVT-ORG-003 | TenantSuspended | Organization Context | IAM, Order, Billing |
| EVT-ORG-004 | BranchCreated | Organization Context | IAM, Inventory, Caja |
| EVT-ORG-005 | BranchActivated | Organization Context | Orders, Scheduling, Portal |
| EVT-ORG-006 | BranchDeactivated | Organization Context | Orders, Scheduling, Portal |
| EVT-ORG-007 | BranchAddressAdded | Organization Context | Scheduling, Billing, Compliance |
| EVT-ORG-008 | BranchScheduleDefined | Organization Context | Scheduling, Orders, Portal |
| EVT-ORG-009 | BranchServiceEnabled | Organization Context | Scheduling, Orders, Catalogs |
| EVT-ORG-010 | BranchServiceDisabled | Organization Context | Scheduling, Orders |
| EVT-ORG-011 | PrimaryBranchChanged | Organization Context | Reporting, Billing |
| EVT-ORG-012 | OrganizationalUnitCreated | Organization Context | IAM, HR-lite |
| EVT-ORG-013 | PositionDefined | Organization Context | IAM |
| EVT-ORG-014 | OrganizationConfigurationChanged | Organization Context | Audit, Analytics, Feature Flags |

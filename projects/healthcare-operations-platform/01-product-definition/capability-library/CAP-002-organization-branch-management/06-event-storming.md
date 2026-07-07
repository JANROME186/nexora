# 06 Event Storming

## Commands

- CreateTenant
- ActivateTenant
- CreateBranch
- UpdateBranchProfile
- AddBranchAddress
- DefineBranchSchedule
- EnableBranchService
- DisableBranchService
- ActivateBranch
- DeactivateBranch
- AssignPrimaryBranch
- CreateOrganizationalUnit
- DefinePosition

## Domain Events

- TenantCreated
- TenantActivated
- TenantSuspended
- BranchCreated
- BranchAddressAdded
- BranchScheduleDefined
- BranchServiceEnabled
- BranchServiceDisabled
- BranchActivated
- BranchDeactivated
- PrimaryBranchChanged
- OrganizationalUnitCreated
- PositionDefined
- OrganizationConfigurationChanged

## External Events Consumed

- LicensePlanChanged
- CountryPackActivated
- UserCreated
- PendingOrdersCleared

## Policies

- When BranchActivated, branch becomes selectable for order creation.
- When BranchDeactivated, branch must be hidden from new order creation.
- When LicensePlanChanged, branch services must be revalidated.
- When CountryPackActivated, tax and address validation rules must be updated.

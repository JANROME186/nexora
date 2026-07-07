# 07 Domain Model

## Bounded Context

**Organization Context**

Responsible for tenant, legal organization, branch, location, operational hierarchy and service availability.

## Aggregates

### Tenant Aggregate

Root: `Tenant`

Owns:

- Tenant configuration.
- Default country, timezone, currency and locale.
- License references.
- Primary organization reference.

### Organization Aggregate

Root: `Organization`

Owns:

- Legal profile.
- Tax profile.
- Brand profile.
- Organization status.

### Branch Aggregate

Root: `Branch`

Owns:

- Address collection.
- Contact data.
- Branch schedule.
- Branch services.
- Operational configuration.
- Branch status.

### Organizational Structure Aggregate

Root: `OrganizationalUnit`

Owns:

- Positions.
- Reporting hierarchy.
- Branch assignments.

## Value Objects

- TenantId
- OrganizationId
- BranchId
- BranchCode
- LegalName
- TradeName
- TaxIdentifier
- Address
- GeoLocation
- Timezone
- Locale
- CurrencyCode
- Schedule
- ServiceCode
- ContactInfo

## Domain Services

- BranchActivationPolicy
- BranchServiceAvailabilityPolicy
- TenantIsolationPolicy
- OrganizationConfigurationPolicy
- CountryPackValidationService

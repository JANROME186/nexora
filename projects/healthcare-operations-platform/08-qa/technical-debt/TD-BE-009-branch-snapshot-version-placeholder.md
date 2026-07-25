---
id: TD-BE-009
format: markdown_structured_payload
type: technical-debt-item
name: Branch snapshot version is a fixed placeholder, not a real optimistic-concurrency
  counter
version: 2.0.0
status: closed
---

# Branch Snapshot Version Is A Fixed Placeholder, Not A Real Optimistic Concurrency Counter

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-BE-009
  type: technical-debt-item
  name: Branch snapshot version is a fixed placeholder, not a real optimistic-concurrency
    counter
  version: 2.0.0
  status: closed
  created_date: 2026-07-15
  closed_date: 2026-07-17
  closed_by_backlog_item: HOP-ENT-FOUND-001
closure_evidence: 'organizationmanagement.domain.Branch gained a real `int version`
  field (set to 1 on creation by OrganizationManagementService, since Branch still
  has no update/mutation command). BranchSnapshot.from(Branch) now reads branch.version()
  directly; the hardcoded UNVERSIONED=1 constant was removed. JdbcOrganizationRepository
  persists/reads the new column, and db/platform-foundation/schema.sql (plus its previously-drifted
  Docker-init duplicate, resynced during this same backlog item) declares `version
  integer NOT NULL DEFAULT 1` on organization.branches. Verified by BranchSnapshotTest
  and the full local-database-backed backend test suite (182/182 passing, 0 failures/errors).
  Because Branch still has no update command, the "incremented on every mutation"
  half of the original acceptance criteria cannot yet be exercised end to end; this
  is closed as a source-of-truth fix (version is now a real aggregate field, not a
  placeholder constant) rather than as a fully demonstrated increment path.

  '
source:
  discovered_during_backlog_item: MVP-MOD-004-BE-001
  module: MVP-MOD-004 Front Desk and Care Delivery
  evidence: 08-qa/qa/front-desk-care-delivery/MVP-MOD-004-BE-001-validation.md
classification:
  category: business_rule_modeling_gap
  affected_area: organization_management_branch_versioning
  affected_components:
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/organizationmanagement/domain/Branch.java
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/organizationmanagement/domain/BranchSnapshot.java
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/frontdeskcaredelivery/diagnosticordermanagement/domain/BranchSnapshot.java
  risk_level: low
  blocking: false
  reason_non_blocking: 'BCM-LAB-001''s business-model.md (VO-ORD-003) requires a
    BranchSnapshot.sourceVersion field to be present on every DiagnosticOrder, but
    does not require it to be a meaningfully incrementing counter. Patient and Doctor
    snapshots already carry a real version (their aggregates track `version` since
    MVP-MOD-003), so this gap is isolated to Branch. Order correctness is unaffected
    because branch identity/name is still captured correctly; only the version number
    itself is a constant placeholder.

    '
current_state:
  issue: 'organizationmanagement.domain.Branch has no `version` field (unlike Patient,
    Doctor, TestDefinition, PanelDefinition and PriceList, which all track version).
    BranchDirectory''s BranchSnapshot.from(Branch) therefore hard-codes version=1
    for every branch, and DiagnosticOrder.branchSnapshot().sourceVersion() is always
    1 regardless of how many times the branch has actually been updated.

    '
  compensating_control:
  - BranchSnapshot.java documents the placeholder explicitly in its Javadoc and points
    to this technical debt item.
  - Branch identity fields (branchId, name, status) are captured correctly at order
    time; only the version counter is not meaningful yet.
target_state:
  preferred_open_source_tooling: []
  expected_integration_points:
  - organizationmanagement/domain/Branch.java (add a version int field)
  - organizationmanagement/application/OrganizationManagementService.java (increment
    version on every branch mutation, matching the Patient/Doctor/TestDefinition convention)
  - organizationmanagement/adapter/out/jdbc/JdbcOrganizationRepository.java (persist
    the new column)
  - db/platform-foundation/schema.sql (add a version column to organization.branches)
remediation:
  strategy: gradual_when_organization_management_branch_lifecycle_is_next_touched
  recommended_trigger:
  - A future backlog item that adds branch update/deactivate commands (Branch currently
    only supports create) is the natural point to add version tracking consistently.
  acceptance_criteria:
  - Branch gains a version field incremented on every mutation, following the existing
    Patient/Doctor/TestDefinition pattern.
  - BranchSnapshot.from(Branch) in both organizationmanagement and frontdeskcaredelivery
    captures the real version instead of the fixed placeholder.
  - No regression in existing Branch creation tests.
```

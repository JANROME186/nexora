---
id: HOP-TEST-BCM-PLT-010
format: markdown_structured_payload
type: test-model
name: Open Data Ingestion and Migration Test Model
version: 0.1.0
status: modeled
---

# Open Data Ingestion And Migration Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-PLT-010
  type: test-model
  name: Open Data Ingestion and Migration Test Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PLT-010
test_cases:
- id: TST-MIG-010-01
  type: acceptance
  validates_rule: RN-001
  statement: A bundle missing a valid manifest or declaring an unsupported format
    is rejected before any file is parsed.
  generatable: false
- id: TST-MIG-010-02
  type: acceptance
  validates_rule: RN-002
  statement: Attempting to execute an import whose dry-run validation has not passed
    is blocked.
  generatable: false
- id: TST-MIG-010-03
  type: architecture
  validates_rule: RN-003
  statement: No import code path writes directly to a domain aggregate's storage;
    every committed record is attributable to an existing domain command.
  generatable: false
- id: TST-MIG-010-04
  type: acceptance
  validates_rule: RN-004
  statement: Retrying a failed migration job resumes from its last checkpoint and
    does not re-invoke already-committed domain commands.
  generatable: false
- id: TST-MIG-010-05
  type: acceptance
  validates_rule: RN-005
  statement: Every migration job step produces or updates a ReconciliationReport with
    imported, rejected, skipped and warning counts.
  generatable: false
- id: TST-MIG-010-06
  type: contract
  validates_rule: RN-006
  statement: Unauthorized or out-of-scope callers cannot create, approve or review
    a migration job outside their tenant/laboratory.
  generatable: true
test_layers:
- contract_tests
- unit_tests
- acceptance_tests
- architecture_tests
generation_policy:
  repetitive_tests: generated
  custom_rule_tests:
  - TST-MIG-010-01
  - TST-MIG-010-02
  - TST-MIG-010-03
  - TST-MIG-010-04
  - TST-MIG-010-05
```

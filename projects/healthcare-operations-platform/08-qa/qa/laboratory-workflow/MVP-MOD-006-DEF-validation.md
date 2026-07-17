# MVP-MOD-006-DEF Laboratory Workflow Capability Package Validation

Artifact ID: `HOP-QA-MVP-MOD-006-DEF-001`
Status: `passed`
Machine-readable source: `MVP-MOD-006-DEF-validation.yaml`

## Scope

- Backlog item: `MVP-MOD-006-DEF`
- Module: `MVP-MOD-006 Laboratory Workflow`
- Release: `REL-001`
- Execution flow stage: `model`
- Code implemented: **no** — this is a definition-only backlog item.

## Capabilities modeled

| Capability | Package folder | Bounded context | Aggregate role |
| --- | --- | --- | --- |
| BCM-LAB-002 Sample Collection | `bcm-lab-002-sample-collection/` | orders-samples | Owns AGG-008 Sample |
| BCM-LAB-003 Sample Labeling | `bcm-lab-003-sample-labeling/` | orders-samples | Delegated: `labelInfo` |
| BCM-LAB-005 Sample Reception | `bcm-lab-005-sample-reception/` | orders-samples | Delegated: `receptionRecord`, rejection-at-reception, disposal |
| BCM-LAB-006 Laboratory Processing | `bcm-lab-006-laboratory-processing/` | laboratory-results | Owns AGG-009 LaboratoryResult |
| BCM-LAB-008 Technical Validation | `bcm-lab-008-technical-validation/` | laboratory-results | Delegated: `technicalValidation`, `criticalFlag` |
| BCM-LAB-009 Medical Validation | `bcm-lab-009-medical-validation/` | laboratory-results | Delegated: `medicalValidation` |
| BCM-LAB-010 Result Release | `bcm-lab-010-result-release/` | laboratory-results | Delegated: `releaseRecord`, `amendments` |

Each package has the full 14-artifact set (13 YAML models + `README.md`), for 98 files total.

## Aggregate ownership design

Two aggregates are shared across sibling capabilities within the same bounded context, mirroring the
`DiagnosticOrder` / `BCM-LAB-001` ownership pattern already used in MVP-MOD-004:

- **Sample (AGG-008)**, bounded context `orders-samples`: owned by BCM-LAB-002, which models the full
  aggregate and creates it via `CollectSample`. BCM-LAB-003 and BCM-LAB-005 hold delegated authority
  over one named field each (`labelInfo`; `receptionRecord`/rejection-at-reception/disposal), each with
  an `architecture_boundary` rule restricting it to that field set.
- **LaboratoryResult (AGG-009)**, bounded context `laboratory-results`: owned by BCM-LAB-006, which
  models the full aggregate and creates it via `CaptureResultValue`. BCM-LAB-008, BCM-LAB-009 and
  BCM-LAB-010 hold delegated authority over their own named fields (`technicalValidation`/
  `criticalFlag`; `medicalValidation`; `releaseRecord`/`amendments`).

No capability outside these seven, and no other bounded context, may mutate either aggregate — this
matches `aggregate-catalog.yaml`'s `forbidden_mutators` declarations for AGG-008 and AGG-009.

## Clinical rules modeled (backlog minimum set)

| Requirement | Where modeled |
| --- | --- |
| Sample must reference a valid order | BCM-LAB-002 RN-001 |
| Traceable identification before reception/processing | BCM-LAB-002 RN-003, BCM-LAB-005 RN-001 |
| Audit on every transition | BCM-LAB-002 RN-004/RN-009, BCM-LAB-006 RN-007/RN-008 |
| Rejected sample must not be processed | BCM-LAB-002 RN-005, BCM-LAB-005 RN-002/RN-003 |
| Structured hemolysis/incidence reason | BCM-LAB-002 `SampleRejectionReason`, BCM-LAB-005 `ReceptionConditionCheck` |
| Result respects test/unit/range/method | BCM-LAB-006 RN-002/RN-003 |
| Validation by authorized role | BCM-LAB-008 RN-002, BCM-LAB-009 RN-002 |
| Critical result triggers notification hook | BCM-LAB-008 RN-003/RN-004 |
| Chain of custody preserved | BCM-LAB-002 `ChainOfCustodyEvent`, INV-COL-002/005 |
| No deletion of clinical evidence | BCM-LAB-002 RN-009, BCM-LAB-005 RN-004, BCM-LAB-010 RN-003 (append-only amendment) |

## Validations executed

19 validation checks (VAL-001 through VAL-019) covering artifact completeness, YAML syntax, BCM-001/
BCM-002/domain-foundation traceability, aggregate-ownership boundary compliance, business-rule format,
generation-plan separation, MDPE manual-authoring compliance, API/permission/UI/mobile surface
classification, agent-agnostic scanning, the clinical rule minimum set, HRP/BRM alignment, no
unauthorized aggregate duplication against MVP-MOD-002/003/004/005, and the AI-governance exclusion
for medical validation and release. All passed; zero blocking gaps.

## Technical debt

This backlog item made no code changes, so no code-changing debt item could be remediated directly.
The debt-first review requirement was still honored: `TD-BE-010` (order cancellation's downstream
sample-state check, explicitly deferred pending MVP-MOD-006 modeling) was identified as the one open
item this backlog's modeling work unblocks, and both `TD-BE-010-order-cancellation-sample-state-check-deferred.yaml`
and `technical-debt-index.yaml` were updated to record that its modeling precondition is now satisfied
— the code-level fix remains open, targeted at `MVP-MOD-006-BE-002`.

## Non-blocking observations

- Sample Transport (BCM-LAB-004) and Quality Control (BCM-LAB-007) remain MVP2-roadmap capabilities,
  explicitly out of scope.
- Mobile sample collection is modeled at intent level only (per the module's `sample_collection_later`
  declaration); no mobile screens are compiled by this or the next backend backlog item.

## Readiness

- `MVP-MOD-006-DEF` status: **closed**.
- HOP commercially complete: **no**. HOP GA-ready: **no**.
- Next backlog item: `MVP-MOD-006-BE-001` — Compile sample lifecycle backend outputs.
- Backend coverage 67.47%, frontend coverage 80.66% — both unchanged since no code was implemented in
  this backlog item.

# HOP Commercial Backlog Execution Prompts

Artifact ID: `HOP-COM-PROMPTS-001`
Version: `1.0.0`
Status: `approved`

The machine-readable source is `HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.yaml`.

## Purpose

Use this playbook after `MVP-MOD-001 Platform Foundation` to continue HOP toward a commercial product. It tells an agent how to select the next backlog item, generate module definitions, validate them, implement one item at a time and close each module.

## Required Rule

If `BUSINESS_REQUIREMENT.md` is missing, stop. That file is requester-supplied source material and is the raw input for all product analysis.

## Required Context

Load these first:

- `../../../../AGENT_BOOTSTRAP.md`
- `../../../../PROJECT_STATE.yaml`
- `../../../../SOURCE_OF_TRUTH.yaml`
- `../../../../nexora-framework/00-start-here/FRAMEWORK_EXECUTION_SEQUENCE.yaml`
- `../../../../nexora-framework/02-standards/standards/agent-agnostic-standard.yaml`
- `../../BUSINESS_REQUIREMENT.md`
- `../../PROJECT_BRIEF.yaml`
- `../../SOURCE_OF_TRUTH.yaml`
- `../../PROJECT_STATE.yaml`
- `../../01-product-definition/business-capabilities/bcm-001/business-capability-map.yaml`
- `../../01-product-definition/business-capabilities/bcm-002/capability-dependency-map.yaml`
- `../mvp/healthcare-operations-platform-mvp-framework.yaml`
- `HOP_COMMERCIAL_PRODUCT_BACKLOG.yaml`

## Prompt 1: Select Next Executable Backlog Item

Instruction:

```text
Load the HOP project state and HOP commercial product backlog.
Select the next executable backlog item by dependency order.
If any dependency is incomplete, stop and report the exact blocker.
If the selected item is a definition item, prepare the module definition package plan.
If the selected item is an implementation item, verify that the definition package exists first.
```

Expected result:

- Selected module.
- Selected backlog item.
- Dependency status.
- Blocking gaps, if any.
- Execution plan.

## Prompt 2: Generate Module Definition Package

Instruction:

```text
Generate the complete definition package for the selected HOP module.
Use the commercial product backlog as the module source, BCM-001 for capabilities, BCM-002 for dependency profiles, and the domain foundation for bounded contexts and aggregate ownership.
Place the package under the module folder declared by the backlog or under 06-delivery/mvp/modules for MVP modules and 06-delivery/commercial-product/modules for commercial modules.
Do not implement code during this step.
Every machine-executable artifact must be YAML when applicable, with Markdown as the human companion.
```

Expected result:

- `module-definition.yaml`
- `module-definition.md`
- `domain-model.yaml`
- `domain-model.md`
- `api-contract.openapi.yaml`
- `database-migration-plan.yaml`
- `database-migration-plan.md`
- `ui-screen-map.yaml`
- `ui-screen-map.md`
- `security-and-audit-rules.yaml`
- `security-and-audit-rules.md`
- `test-plan.yaml`
- `test-plan.md`
- `traceability.yaml`

## Prompt 3: Validate Module Definition Package

Instruction:

```text
Validate the selected module definition package.
Confirm that all required artifacts exist, YAML is parseable, Markdown companion files exist where required, capabilities trace to BCM-001, dependencies trace to BCM-002, API surfaces are classified, and security/audit/test expectations are present.
Scan for named-agent dependencies or vendor-specific execution requirements.
If any blocking gap exists, update the package before allowing implementation.
```

Expected result:

- Validation report.
- Missing artifacts.
- Traceability gaps.
- Agent-agnostic findings.
- Ready-for-implementation decision.

## Prompt 4: Implement Selected Backlog Item

Instruction:

```text
Implement only the selected backlog item.
Load the selected module definition package first and follow existing project implementation patterns.
Keep changes scoped to the selected backlog item.
Add or update tests appropriate to the backend, web, mobile, portal, integration or operations scope.
Write QA evidence under 08-qa and update PROJECT_STATE.yaml and SOURCE_OF_TRUTH.yaml.
Stop before starting the next backlog item.
```

Expected result:

- Code changes.
- Tests.
- QA evidence in YAML and Markdown.
- Registry updates.

## Prompt 5: Close Module

Instruction:

```text
Close the selected HOP module.
Validate all backlog items for the module, confirm all required tests and QA evidence are present, confirm traceability is complete, and update project state and source registries.
Recommend the next module only after closeout evidence is written.
```

Expected result:

- Closeout evidence in YAML and Markdown.
- Updated `PROJECT_STATE.yaml`.
- Updated `SOURCE_OF_TRUTH.yaml`.
- Next module recommendation.

## Next Backlog Item

Start with:

- Module: `MVP-MOD-002 Diagnostic Catalog`
- Backlog item: `MVP-MOD-002-DEF`
- Folder: `06-delivery/mvp/modules/MVP-MOD-002-diagnostic-catalog/`

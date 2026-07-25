# Nexora Project Folder Standard

## Purpose

Every Nexora solution must be self-contained under `projects/<project-slug>/`.

This lets an AI agent load the project folder, understand the user need, apply the Nexora recipe and prepare the MVP without depending on prior chat context.

## Required Files

Each project must include:

- `README.md`
- `BUSINESS_REQUIREMENT.md`
- `PROJECT_BRIEF.md`
- `PROJECT_STATE.md`
- `SOURCE_OF_TRUTH.md`

## Required Folders

| Folder | Purpose |
| --- | --- |
| `00-intake` | User needs, assumptions, constraints and source material. |
| `01-product-definition` | Vision, personas, scope, capabilities, roadmap and MVP. |
| `02-domain-definition` | Bounded contexts, aggregates, vocabulary, processes and business rules. |
| `03-architecture` | Application, technology, data, security, integration and AI architecture. |
| `04-requirements` | User stories, workflows, acceptance criteria and traceability. |
| `05-contracts` | OpenAPI, events, async contracts, imports, exports and adapters. |
| `06-delivery` | MVP modules, release packages, implementation backlogs and delivery plans. |
| `07-implementation` | Code or generated implementation assets when colocated. |
| `08-qa` | Test plans, fixtures, contract tests, quality evidence and security quality gate evidence. |
| `09-operations` | Deployment, local runtime, observability and runbooks. |
| `10-generated` | Generated diagrams, indexes and agent context packs. |
| `99-legacy` | Imported previous-structure assets kept for continuity. |

## Business Requirement Rule

Every project must start with:

`BUSINESS_REQUIREMENT.md`

This file captures the high-level business requirement supplied by the requester. Agents must not generate it. `PROJECT_BRIEF.md` structures and refines it into the formal product and MVP context.

## Readiness Rule

Development may start only when the project `PROJECT_STATE.md` declares:

```yaml
development_readiness:
  status: ready
  blocking_definition_gaps: []
```

## Security Quality Evidence Rule

Code-changing backlog items must write security quality evidence under:

`08-qa/security-quality/<backlog-item-id>/`

The evidence records applicable open source checks for tests, SAST/static analysis, dependency vulnerabilities, secrets, coverage and DAST when a runnable surface exists.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: NXF-STD-001
  type: project-folder-standard
  name: Nexora Project Folder Standard
  version: 1.0.0
  status: approved
  owner: Nexora Engineering
required_project_root_files:
- README.md
- BUSINESS_REQUIREMENT.md
- PROJECT_BRIEF.md
- PROJECT_STATE.md
- SOURCE_OF_TRUTH.md
required_project_folders:
- 00-intake
- 01-product-definition
- 02-domain-definition
- 03-architecture
- 04-requirements
- 05-contracts
- 06-delivery
- 07-implementation
- 08-qa
- 09-operations
- 10-generated
- 99-legacy
folder_purposes:
  00-intake: User needs, assumptions, constraints, business requirement versions,
    change impact assessments and source material.
  01-product-definition: Product vision, personas, scope, capabilities, roadmap and
    MVP definition.
  02-domain-definition: Domain model, bounded contexts, aggregates, vocabulary and
    business rules.
  03-architecture: Application, technology, data, security, integration and AI architecture.
  04-requirements: User stories, acceptance criteria, workflows and traceability.
  05-contracts: OpenAPI, events, async contracts, import/export contracts and adapters.
  06-delivery: MVP modules, release packages, implementation backlogs and delivery
    plans.
  07-implementation: Code or code-generation outputs when implementation is colocated.
  08-qa: Test plans, contract tests, fixtures, quality evidence and security quality
    gate evidence.
  09-operations: DevOps, deployment profiles, observability and runbooks.
  10-generated: Generated diagrams, markdown, indexes and agent context packs.
  99-legacy: Imported or previous-structure assets kept for continuity.
rules:
- Project-specific files stay inside the project folder.
- Root-level files describe Nexora and repository orchestration only.
- Every project must start from requester-supplied BUSINESS_REQUIREMENT.md.
- Agents must resolve the latest BUSINESS_REQUIREMENT version before project analysis,
  validation, planning or development.
- Projects may version business requirements under 00-intake/business-requirements/.
- Business requirement changes require impact assessment before modifying derived
  artifacts or implementation.
- Code-changing backlog items must place security quality evidence under 08-qa/security-quality/<backlog-item-id>/.
- Agents must not generate BUSINESS_REQUIREMENT.md.
- PROJECT_BRIEF.md must structure and refine BUSINESS_REQUIREMENT.md.
- Every project must declare its own SOURCE_OF_TRUTH.md.
- Every project must declare readiness in PROJECT_STATE.md.
- Generated artifacts must not be edited manually.
- A project reaches implementation readiness only when blocking_definition_gaps is
  empty.
```

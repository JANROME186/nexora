# Nexora Project Folder Standard

## Purpose

Every Nexora solution must be self-contained under `projects/<project-slug>/`.

This lets an AI agent load the project folder, understand the user need, apply the Nexora recipe and prepare the MVP without depending on prior chat context.

## Required Files

Each project must include:

- `README.md`
- `BUSINESS_REQUIREMENT.md`
- `PROJECT_BRIEF.md`
- `PROJECT_STATE.yaml`
- `SOURCE_OF_TRUTH.yaml`

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

Development may start only when the project `PROJECT_STATE.yaml` declares:

```yaml
development_readiness:
  status: ready
  blocking_definition_gaps: []
```

## Security Quality Evidence Rule

Code-changing backlog items must write security quality evidence under:

`08-qa/security-quality/<backlog-item-id>/`

The evidence records applicable open source checks for tests, SAST/static analysis, dependency vulnerabilities, secrets, coverage and DAST when a runnable surface exists.

# NEXORA — Strategic and Technical Handoff

**Document ID:** VISION-002
**Version:** 1.0.0
**Status:** Approved
**Owner:** Nexora Founders

## Purpose

This document captures the strategic continuity context for Nexora without depending on a specific agent, coding assistant, platform, provider or previous conversation.

It exists so any capable agent or human engineering team can understand:

- Nexora is the company.
- Healthcare Operations Platform is the first product.
- Nexora AI Engineering Framework is the reusable method for building products.
- Healthcare Operations Platform validates the framework, but it is not the final strategic asset.
- Development must start only from repository source artifacts.

## Strategic Identity

Nexora is a software development and Artificial Intelligence company.

Healthcare Operations Platform is Nexora's first product.

Nexora AI Engineering Framework is the reusable methodology for creating products through structured knowledge, traceability, validation and AI-assisted engineering.

The long-term asset is not only the first product. The long-term asset is the repeatable ability to build enterprise-grade products.

## Approved Principles

- Business First.
- Knowledge First.
- Domain Driven Design.
- API First.
- OpenAPI First.
- Contract First.
- AI Native.
- Agent Agnostic.
- Provider Agnostic.
- Cloud Agnostic.
- Platform Agnostic.
- Security by Design.
- Privacy by Design.
- Accessibility First.
- Documentation as Code.
- Knowledge as Code.
- Infrastructure as Code.
- Traceability by Design.
- Continuous Evolution.

## Architecture Rule

Healthcare Operations Platform has an Architecture Freeze v1.0.

Agents must not redesign the architecture during implementation.

Any architecture change requires:

- ADR.
- Justification.
- Impact analysis.
- Updated `PROJECT_STATE.md`.
- Updated `SOURCE_OF_TRUTH.md` when source artifacts change.

## Product Scope

Healthcare Operations Platform is a SaaS and deployable enterprise platform for diagnostic healthcare operations.

The long-term product should support:

- Clinical laboratories.
- Imaging centers.
- Multi-branch diagnostic organizations.
- Patients.
- Doctors.
- Employees.
- Cashier and billing.
- Inventory.
- Quality.
- Logistics.
- Home collection.
- Digital reports.
- Patient, doctor and employee portals.
- Mobile applications.
- AI capabilities.
- Legacy data migration.
- Public APIs.
- ASTM, HL7, FHIR and DICOM integration boundaries.

## Current Repository Model

The active repository model is:

```text
nexora/
  nexora-framework/
  projects/
    healthcare-operations-platform/
```

The framework is reusable.

Each product or client solution lives under `projects/<project-slug>/`.

Project-specific artifacts must not be placed at repository root.

## Current Development Position

The first implementation target is:

```text
projects/healthcare-operations-platform/
```

The first module is:

```text
MVP-MOD-001 Platform Foundation
```

The module package is:

```text
projects/healthcare-operations-platform/06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/
```

The development prompt hierarchy is:

1. `nexora-framework/05-prompts/prompts/generic-project-lifecycle-prompts.md`
2. `nexora-framework/05-prompts/prompts/auxiliary-development-prompts.md`
3. `projects/healthcare-operations-platform/06-delivery/mvp/MVP_BACKLOG_EXECUTION_PROMPTS.md`
4. Target module package files.

## Strategic Gaps vs MVP Blockers

Some enterprise-wide artifacts are still expected to mature over time:

- Full enterprise requirements depth.
- Full operating model.
- Expanded canonical vocabulary.
- Expanded canonical data model.
- Complete API surface for every future module.
- Full migration, imaging, quality, inventory and AI operating detail.
- Extended governance and validation automation.

These are strategic roadmap items.

They must not block the first MVP-MOD-001 implementation when the selected module package is complete and project readiness is approved.

## Rule for Starting Development

Development may start only when:

- `BUSINESS_REQUIREMENT.md` exists and remains requester-supplied source material.
- `PROJECT_STATE.md` shows `development_readiness.status: ready`.
- `blocking_definition_gaps` is empty.
- The selected module package contains its required YAML, Markdown, OpenAPI and traceability files.
- The selected module has a clear ordered backlog.
- Agent-agnostic validation passes.

## Immediate Development Instruction

Use the generic MVP development prompt:

```text
Develop the MVP for projects/healthcare-operations-platform/ using its PROJECT_STATE.md, SOURCE_OF_TRUTH.md and ordered module package.
```

The first backlog item is:

```text
PF-BE-001 - Create backend project skeleton
```

## Success Criteria for the Next Phase

The next phase succeeds when MVP-MOD-001 has:

- Backend foundation.
- Local runtime.
- Tenant, laboratory and branch commands.
- Identity and role assignment baseline.
- Append-only audit.
- Employee administration web shell.
- Mobile app foundation.
- Smoke and contract tests.
- Updated traceability, QA evidence and project state.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: VISION-002
  type: strategic-technical-handoff
  name: Nexora Strategic and Technical Handoff
  version: 1.0.0
  status: approved
  owner: Nexora Founders
  human_readable: NEXORA_STRATEGIC_HANDOFF.md
  machine_readable: NEXORA_STRATEGIC_HANDOFF.md
purpose: Provide agent-agnostic continuity context for Nexora, Healthcare Operations
  Platform and the Nexora AI Engineering Framework.
identity:
  company: Nexora
  first_product: Healthcare Operations Platform
  reusable_methodology: Nexora AI Engineering Framework
architecture_rule:
  status: Architecture Freeze v1.0
  redesign_allowed_without_adr: false
  change_requirements:
  - ADR
  - Justification
  - Impact analysis
  - Updated PROJECT_STATE.md
  - Updated SOURCE_OF_TRUTH.md when source artifacts change
active_repository_model:
  framework_path: nexora-framework/
  projects_path: projects/
  first_project_path: projects/healthcare-operations-platform/
  rule: Project-specific artifacts remain inside their project folder.
development_position:
  target_project: projects/healthcare-operations-platform/
  target_module: MVP-MOD-001
  target_module_name: Platform Foundation
  module_package: projects/healthcare-operations-platform/06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/
  first_backlog_item: PF-BE-001
prompt_hierarchy:
- nexora-framework/05-prompts/prompts/generic-project-lifecycle-prompts.md
- nexora-framework/05-prompts/prompts/auxiliary-development-prompts.md
- projects/healthcare-operations-platform/06-delivery/mvp/MVP_BACKLOG_EXECUTION_PROMPTS.md
- target module package files
strategic_roadmap_items_not_blocking_mvp_mod_001:
- Full enterprise requirements depth.
- Full operating model.
- Expanded canonical vocabulary.
- Expanded canonical data model.
- Complete API surface for every future module.
- Full migration, imaging, quality, inventory and AI operating detail.
- Extended governance and validation automation.
development_start_gate:
  required:
  - BUSINESS_REQUIREMENT.md exists and remains requester supplied.
  - PROJECT_STATE.md shows development_readiness.status ready.
  - blocking_definition_gaps is empty.
  - Selected module package contains required YAML, Markdown, OpenAPI and traceability
    files.
  - Selected module has a clear ordered backlog.
  - Agent-agnostic validation passes.
immediate_development_instruction: Develop the MVP for projects/healthcare-operations-platform/
  using its PROJECT_STATE.md, SOURCE_OF_TRUTH.md and ordered module package.
next_phase_success_criteria:
- Backend foundation.
- Local runtime.
- Tenant, laboratory and branch commands.
- Identity and role assignment baseline.
- Append-only audit.
- Employee administration web shell.
- Mobile app foundation.
- Smoke and contract tests.
- Updated traceability, QA evidence and project state.
```

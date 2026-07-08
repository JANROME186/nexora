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
- Updated `PROJECT_STATE.yaml`.
- Updated `SOURCE_OF_TRUTH.yaml` when source artifacts change.

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

1. `nexora-framework/05-prompts/prompts/generic-project-lifecycle-prompts.yaml`
2. `nexora-framework/05-prompts/prompts/auxiliary-development-prompts.yaml`
3. `projects/healthcare-operations-platform/06-delivery/mvp/MVP_BACKLOG_EXECUTION_PROMPTS.yaml`
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
- `PROJECT_STATE.yaml` shows `development_readiness.status: ready`.
- `blocking_definition_gaps` is empty.
- The selected module package contains its required YAML, Markdown, OpenAPI and traceability files.
- The selected module has a clear ordered backlog.
- Agent-agnostic validation passes.

## Immediate Development Instruction

Use the generic MVP development prompt:

```text
Develop the MVP for projects/healthcare-operations-platform/ using its PROJECT_STATE.yaml, SOURCE_OF_TRUTH.yaml and ordered module package.
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

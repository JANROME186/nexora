# Model Driven Product Engineering Standard

Artifact ID: `NXF-MDPE-STD-001`
Version: `1.0.0`
Status: `approved`

## Purpose

Nexora is a Model Driven Product Engineering framework. The editable source of truth is the model. Platform artifacts are generated, compiled or derived from those models.

This is not low-code and not a simple code generator. It is an engineering system where business knowledge is modeled first and software is an output.

## Architecture

Nexora has two strategic layers:

```text
NEXORA
  |
  +-- NEXORA DEFINITION
  |     Editable product knowledge and models
  |
  +-- NEXORA PLATFORM
        Generated, compiled, implemented or runtime artifacts
```

## Nexora Definition

Only durable editable artifacts live here:

- Business Model
- Healthcare Operating Model
- Business Capability Map
- Capability Packages
- Canonical Business Vocabulary
- Business Rules
- Canonical Data Model
- DDD Model
- OpenAPI Sources
- UI Model
- Mobile Model
- AI Model
- Deployment Model
- Observability Model
- Test Model

## Nexora Platform

The platform contains generated, compiled or implementation-derived outputs:

- Backend
- Frontend
- Flutter mobile
- Docker
- Terraform
- Tests
- CI/CD
- Observability assets
- SDKs
- Swagger documentation
- Repetitive documentation

## What Must Not Be Written Manually

Do not manually write repetitive platform artifacts when they can be generated from models:

- CRUD scaffolding
- DTOs
- Controllers
- Repositories
- Swagger documentation
- SDKs
- Repetitive documentation
- Duplicate models
- Repetitive test cases

## What Must Be Written Manually

Nexora authors a small amount of high-value knowledge:

- Business models
- Business rules
- Business processes
- Domain decisions
- OpenAPI source contracts
- Non-generatable custom rule implementation
- Compiler templates and generators
- Architecture decisions

## Execution Flow

The official flow is:

```text
Model -> Compile -> Implement Rules -> Validate -> Release
```

Backlogs must not start with manual CRUD, DTO, controller or repository tasks. They must start by completing the capability model and then generating or deriving platform outputs.

## Capability Rule

Nexora develops Business Capabilities, not standalone modules.

Modules may still exist as roadmap groupings, but each capability is the primary product unit. Every capability must become a versionable Capability Package with models, rules, processes, events, contracts, UI/mobile definitions, tests, observability and implementation context.

# NEXORA — CODEX BOOTSTRAP

## 1. Purpose

This file is the starting context for Codex or any AI engineering agent working on the Nexora repository.

The agent must continue the project from the repository, not from previous chat context.

---

## 2. Company Definition

**Nexora** is a technology company focused on:

- Software development.
- Artificial Intelligence.
- AI-assisted engineering.
- Business automation.
- SaaS platforms.
- Data-driven products.
- Cloud/on-premise deployable enterprise software.

Nexora is not only a healthcare software product.

Nexora is the company and engineering organization.

---

## 3. First Product

The first product developed by Nexora is:

# Healthcare Operations Platform

A SaaS and deployable platform for diagnostic healthcare organizations, initially focused on:

- Clinical laboratories.
- Imaging centers.
- Multi-branch diagnostic companies.
- Patient portals.
- Doctor portals.
- Employee portals.
- Mobile apps.
- AI-assisted operations.
- Interoperability with ASTM, HL7, FHIR and DICOM.
- Data migration from legacy systems.

Commercial product name may be:

# Nexora Healthcare Operations Platform

Internal short name:

# HOP

---

## 4. Repository Role

This repository is currently the definition and engineering knowledge base for the first Nexora product.

It contains:

- Company foundation.
- Product definition.
- Architecture baseline.
- Domain foundation.
- Business capability map.
- Engineering rules.
- Source-of-truth registries.
- AI agent instructions.

This repository should evolve into `nexora-definition`.

---

## 5. Architecture Status

**Architecture Freeze v1.0**

Do not redesign the architecture.

Any architectural change requires an ADR.

The approved architecture must be treated as stable.

---

## 6. Approved Principles

- Business First
- Domain Driven Design
- OpenAPI First
- Contract First
- API First
- AI Native
- AI Agent Agnostic
- Cloud Agnostic
- Compute Agnostic
- Platform Agnostic
- Docker First
- Kubernetes Ready
- On-Premise Supported
- Serverless Preferred, not mandatory
- Mobile First
- Accessibility First
- Internationalization Ready
- Security by Design
- Privacy by Design
- Observability by Design
- Documentation as Code
- Knowledge as Code
- Infrastructure as Code

---

## 7. Official Technology Direction

Backend:
- Java 21
- Spring Boot 3.x
- Spring Modulith initially
- Hexagonal Architecture
- DDD
- CQRS where useful
- Event Driven where useful
- PostgreSQL
- Redis
- OpenAPI 3.1

Frontend:
- React
- TypeScript
- Responsive UI
- Accessibility-focused

Mobile:
- Flutter
- Android and iOS
- Support for low, mid and high-end devices
- Progressive capabilities
- Offline where operationally necessary

Infrastructure:
- Docker
- Docker Compose for local development
- Docker Swarm as simple team deployment option
- Kubernetes for enterprise deployments
- Helm
- Terraform
- Cloud agnostic
- On-premise compatible

Observability:
- OpenTelemetry
- Structured logs
- Metrics
- Traces
- Audit logs

---

## 8. Product Scope

Healthcare Operations Platform must support four main access areas:

1. Public website.
2. Employee portal.
3. Doctor portal.
4. Patient portal.

It must also support mobile apps where appropriate.

---

## 9. Current Repository State

Completed baseline artifacts include:

- Bounded Context Catalog.
- Context Map.
- Shared Kernel.
- Aggregate Catalog.
- BCM-001 Business Capability Map.
- Business Capability Agent.
- Validators.
- ADRs.
- Source of Truth registry.
- Project State registry.
- BCM-002 Capability Dependency Map.
- HOP MVP Agent-Agnostic Implementation Framework.

Current phase:

# Product Definition

Next expected deliverables:

1. ACM-001 Actor Catalog.
2. HRP-001 Healthcare Reference Processes.
3. BRM-001 Business Rules Catalog.
4. CBV-001 Canonical Business Vocabulary expansion.
5. MVP-MOD-001 Platform Foundation Definition Package.

---

## 10. Source of Truth Rules

Editable source artifacts:
- Business capability definitions.
- Domain definitions.
- Business rules.
- Process models.
- Canonical vocabulary.
- OpenAPI source contracts.
- Architecture decisions.
- Project state and source-of-truth registries.

Generated artifacts:
- Generated markdown.
- Generated diagrams.
- Generated OpenAPI indexes.
- Generated agent context packages.
- Generated backlog views.

Never manually edit generated artifacts unless explicitly converting them into source artifacts through an ADR.

---

## 11. Working Rules for Codex

Codex must:

1. Read this file first.
2. Inspect the repository structure.
3. Preserve Architecture Freeze v1.0.
4. Work through small commits or clearly separated file groups.
5. Update `PROJECT_STATE.yaml` for every meaningful iteration.
6. Update `SOURCE_OF_TRUTH.yaml` when new source artifacts are introduced.
7. Add or update ADRs for architectural decisions.
8. Avoid duplicated concepts.
9. Preserve traceability.
10. Prefer YAML source artifacts plus human-readable Markdown.
11. Avoid provider-specific lock-in.
12. Avoid agent-specific instructions except through adapters.
13. Never continue architecture discussion unless the user explicitly asks for architecture change.

---

## 12. Immediate Task for Codex

Start with:

# ACM-001 — Actor Catalog

Goal:

Define the human, system and external actors that participate in the Healthcare Operations Platform MVP.

The actor catalog must align with BCM-001, BCM-002 and the HOP MVP Framework.

Expected files:

- `02-platform-definition/actors/acm-001/actor-catalog.yaml`
- `02-platform-definition/actors/acm-001/actor-catalog.md`
- `04-generated/diagrams/product/actor-context-map.mmd`
- `03-engineering/validators/actor-catalog-validator.md`
- ADR if actor taxonomy introduces a governance decision.
- Update `PROJECT_STATE.yaml`
- Update `SOURCE_OF_TRUTH.yaml`

---

## 13. Commit Message Suggestion

Use:

```bash
feat(product): add ACM-001 actor catalog
```

---

## 14. Important Clarification

Nexora is the company.

Healthcare Operations Platform is the first product.

Do not model Nexora as only a healthcare product.

Do not rename the whole company to the healthcare platform.

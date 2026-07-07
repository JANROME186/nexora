# CAP-001 Patient Management

**Capability ID:** CAP-001  
**Name:** Patient Management  
**Status:** Draft  
**Specification Version:** 0.23.0  
**Owner:** Product + Clinical Operations + Data Stewardship

Patient Management is the foundational capability that governs the full lifecycle of patient identity, demographic information, clinical profile, consent, documents, guardianship, communication preferences, portal access and historical relationship with Nexora diagnostic services.

This capability is intentionally designed as a reusable business capability consumed by:

- Public web appointment flows.
- Administrative web portal.
- Patient portal.
- Doctor portal.
- Mobile applications.
- Public APIs.
- AI assistants.
- Billing, orders, results, notifications and compliance services.

## Capability package structure

| Area | File |
|---|---|
| Definition | `01-capability-definition.md` |
| Business rules | `02-business-rules.md` |
| Decision tables | `03-decision-tables.md` |
| State machines | `04-state-machines.md` |
| BPMN / Processes | `05-processes-bpmn.md` |
| Event Storming | `06-event-storming.md` |
| DDD model | `07-domain-model.md` |
| Entities | `08-entities.md` |
| Domain events | `09-domain-events.md` |
| Commands and queries | `10-commands-queries.md` |
| User stories | `11-user-stories.md` |
| OpenAPI contract scope | `12-openapi-contract.md` |
| UI specification | `13-ui-specification.md` |
| Mobile specification | `14-mobile-specification.md` |
| AI use cases | `15-ai-use-cases.md` |
| QA specification | `16-test-specification.md` |
| KPIs | `17-kpis.md` |
| Compliance | `18-compliance.md` |
| Traceability | `19-traceability.md` |
| Machine-readable model | `capability.yaml` |

## Source of truth priority

1. Business rules govern behavior.
2. Decision tables govern conditional decisions.
3. State machines govern lifecycle transitions.
4. OpenAPI governs API contracts.
5. Domain model governs backend implementation.
6. UI/Mobile specs govern channel experience.
7. QA spec validates all artifacts.

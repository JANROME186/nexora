---
artifact:
  id: HOP-PROMPT-HOP-COM-PROMPT-002
  type: auxiliary-prompt
  status: active
  optimization: atomic_context
---

# HOP-COM-PROMPT-002 Auxiliary Prompt

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: HOP-COM-PROMPT-002
name: Generate capability package models
intent: Create all required capability package model artifacts before compilation or implementation.
input:
- HOP_COMMERCIAL_PRODUCT_BACKLOG.md
- ../../01-product-definition/business-capabilities/bcm-001/business-capability-map.md
- ../../01-product-definition/business-capabilities/bcm-002/capability-dependency-map.md
- ../../02-domain-definition/domain-foundation/context-map/context-map.md
- ../../02-domain-definition/domain-foundation/aggregates/aggregate-catalog.md
expected_output:
- capability-package.md
- business-model.md
- business-rules.md
- processes.md
- events.md
- openapi-source.md
- permissions.md
- ui-model.md
- mobile-model.md
- test-model.md
- observability-model.md
- generation-plan.md
- traceability.md
prompt: 'Generate the complete Capability Package model set for every capability in the selected HOP roadmap group.

  Use the commercial product backlog as sequencing context, BCM-001 for capabilities, BCM-002 for dependency profiles, and
  the domain foundation for bounded contexts and aggregate ownership.

  Place each package under 01-product-definition/business-capabilities/packages/.

  Do not implement code during this step.

  Do not manually define CRUD, DTO, controller, repository, SDK, Swagger, repetitive documentation or repetitive test artifacts
  as implementation work; capture them in generation-plan.md as generated outputs.

  Existing machine-executable YAML remains valid. New task handoffs must be Markdown with minimal frontmatter; new structured
  artifacts may use YAML only when automation requires it.

  '
```

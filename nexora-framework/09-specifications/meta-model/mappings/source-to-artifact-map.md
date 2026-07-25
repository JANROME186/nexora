---
id: source-to-artifact-map
format: markdown_structured_payload
version: 0.15.0
---

# Source To Artifact Map

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
version: 0.15.0
sourceOfTruthMappings:
  businessBehavior:
    source: business capabilities, processes and rules
    generatedArtifacts:
    - user stories
    - domain events
    - QA tests
  apiBehavior:
    source: OpenAPI contracts
    generatedArtifacts:
    - backend controllers
    - SDKs
    - contract tests
    - API documentation
  dataBehavior:
    source: DDD domains and entities
    generatedArtifacts:
    - database migrations
    - repositories
    - seed data
    - data validation tests
  userExperience:
    source: journeys, stories and design system
    generatedArtifacts:
    - web screens
    - mobile screens
    - UX tests
  securityBehavior:
    source: security standards and IAM rules
    generatedArtifacts:
    - authorization policies
    - security tests
    - audit rules
```

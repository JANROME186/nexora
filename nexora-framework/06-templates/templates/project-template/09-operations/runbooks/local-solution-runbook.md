# Local Solution Runbook

This runbook is the single human entry point for starting, validating and stopping the complete local solution.

Component README files may contain deeper detail, but basic local review should not require opening
each component manually.

## Prerequisites

Document required local tools here.

## Startup Order

1. Prepare local environment file.
2. Start infrastructure services.
3. Start backend API.
4. Start frontend or webapp.
5. Validate mobile/client packages when applicable.
6. Run integrated smoke validation.

## Feedback

If this runbook is incomplete or hard to follow, create feedback under:

```text
08-qa/framework-feedback/
```

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: PROJECT-LOCAL-RUNBOOK
  type: integrated-local-solution-runbook
  version: 0.1.0
  status: draft
  human_readable: local-solution-runbook.md
  machine_readable: local-solution-runbook.md
  standard: ../../../../nexora-framework/02-standards/standards/integrated-local-solution-runbook-standard.md
project:
  name: TBD
  slug: TBD
  intended_audience:
  - human_reviewer
  - qa_reviewer
  - development_agent
prerequisites: []
environment_setup: []
component_inventory: []
startup_order:
- local_environment_file
- infrastructure_services
- backend_api
- frontend_or_webapp
- mobile_or_client_validation
- integrated_smoke_validation
steps:
  start: []
  health_checks: []
  smoke_validation: []
  quality_validation: []
  stop: []
  reset: []
troubleshooting: []
known_limitations: []
component_readmes: []
feedback_capture:
  project_feedback_index: 08-qa/framework-feedback/framework-feedback-index.md
  instruction: Capture feedback when the runbook is ambiguous, incomplete or requires
    hidden manual steps.
```

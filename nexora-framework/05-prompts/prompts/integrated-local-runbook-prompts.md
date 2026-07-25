# Integrated Local Solution Runbook Prompts

**Artifact ID:** `NXF-LOCAL-RUNBOOK-PROMPTS-001`
**Status:** Approved
**Machine-readable source:** `integrated-local-runbook-prompts.md`
**Version:** `1.0.0`

Use these prompts to create or validate the one-stop local runbook for a project.

## Create Or Update

Create or update:

```text
09-operations/runbooks/local-solution-runbook.md
09-operations/runbooks/local-solution-runbook.md
```

The runbook must cover prerequisites, environment setup, dependency order, infrastructure, backend,
frontend/webapp, mobile when applicable, health checks, smoke validation, quality validation, stop
steps, reset steps, troubleshooting and feedback capture.

## Validate

Follow the runbook as a human reviewer would. Every command must have an explicit working directory,
ports and URLs must be visible, and component README files must not be required for basic startup.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: NXF-LOCAL-RUNBOOK-PROMPTS-001
  type: prompt-playbook
  name: Integrated Local Solution Runbook Prompts
  version: 1.0.0
  status: approved
  human_readable: integrated-local-runbook-prompts.md
  machine_readable: integrated-local-runbook-prompts.md
  owner: Nexora Engineering
purpose: Provide agent-agnostic prompts for creating, validating and updating the
  integrated local solution runbook.
required_context:
- nexora-framework/02-standards/standards/integrated-local-solution-runbook-standard.md
- target_project/SOURCE_OF_TRUTH.md
- target_project/PROJECT_STATE.md
- target_project/07-implementation/
- target_project/09-operations/runbooks/
prompts:
- id: PROMPT-LOCAL-RUNBOOK-001
  name: Create or update integrated local solution runbook
  intent: Keep one human-facing and one machine-readable runbook for starting and
    validating the complete local solution.
  expected_output:
  - 09-operations/runbooks/local-solution-runbook.md
  - 09-operations/runbooks/local-solution-runbook.md
  - SOURCE_OF_TRUTH.md updates
  - PROJECT_STATE.md updates when readiness changes
  prompt: 'Load the Integrated Local Solution Runbook Standard and the target project
    source of truth.

    Inspect current infrastructure, backend, frontend, webapp, mobile and validation
    commands.

    Create or update 09-operations/runbooks/local-solution-runbook.md and .md so
    a human reviewer can start, validate, stop and reset the complete local solution
    from a single guide.

    Document prerequisites, environment setup, dependency order, component inventory,
    ports, URLs, health checks, smoke checks, quality checks, stop/reset steps, troubleshooting,
    known limitations and component README references.

    Do not remove component README files; link to them for deeper detail.

    Update SOURCE_OF_TRUTH.md and PROJECT_STATE.md when the runbook becomes authoritative
    or readiness changes.

    If the runbook cannot be made executable because implementation is missing, document
    the boundary and create project technical debt or framework feedback when appropriate.

    '
- id: PROMPT-LOCAL-RUNBOOK-002
  name: Validate integrated local solution runbook
  intent: Validate that the runbook is usable for human local review.
  expected_output:
  - runbook_validation_result
  - blocking_gaps
  - technical_debt_or_framework_feedback_items
  prompt: 'Load the integrated local solution runbook and follow it as a reviewer
    would.

    Confirm each command has an explicit working directory, prerequisites are listed,
    ports and URLs are visible, startup order is correct, and stop/reset steps are
    separate.

    Execute commands when safe and available. If execution is not possible, perform
    a dry-run review and document why.

    Register blocking gaps when a reviewer cannot start the solution with the documented
    steps.

    Register non-blocking improvements as technical debt or framework feedback.

    '
```

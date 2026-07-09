# Integrated Local Solution Runbook Prompts

**Artifact ID:** `NXF-LOCAL-RUNBOOK-PROMPTS-001`  
**Status:** Approved  
**Machine-readable source:** `integrated-local-runbook-prompts.yaml`  
**Version:** `1.0.0`

Use these prompts to create or validate the one-stop local runbook for a project.

## Create Or Update

Create or update:

```text
09-operations/runbooks/local-solution-runbook.yaml
09-operations/runbooks/local-solution-runbook.md
```

The runbook must cover prerequisites, environment setup, dependency order, infrastructure, backend,
frontend/webapp, mobile when applicable, health checks, smoke validation, quality validation, stop
steps, reset steps, troubleshooting and feedback capture.

## Validate

Follow the runbook as a human reviewer would. Every command must have an explicit working directory,
ports and URLs must be visible, and component README files must not be required for basic startup.

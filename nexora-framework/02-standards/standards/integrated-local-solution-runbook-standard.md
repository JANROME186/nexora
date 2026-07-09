# Integrated Local Solution Runbook Standard

**Artifact ID:** `NXF-LOCAL-RUNBOOK-001`  
**Status:** Approved  
**Machine-readable source:** `integrated-local-solution-runbook-standard.yaml`  
**Version:** `1.0.0`

## Purpose

Every Nexora project must maintain one integrated local runbook so a human reviewer can start,
validate and stop the full local solution without opening each component folder manually.

Component README files still exist, but they are supporting detail. The integrated runbook is the
first entry point for local review.

## Required Artifacts

Each project must include:

```text
09-operations/runbooks/local-solution-runbook.yaml
09-operations/runbooks/local-solution-runbook.md
```

## Required Content

The runbook must include:

- Prerequisites.
- Component inventory.
- Dependency startup order.
- Environment setup.
- Infrastructure startup.
- Backend startup.
- Frontend or webapp startup.
- Mobile startup or validation steps when applicable.
- Health checks.
- Smoke validation.
- Quality validation.
- Stop and reset steps.
- Troubleshooting.
- Known limitations.
- Links to component README files for deeper detail.
- Feedback capture instructions.

## Iteration Rule

Every implementation iteration must update or explicitly confirm the integrated runbook when it
changes infrastructure, backend, frontend, webapp, mobile, ports, environment variables or
validation commands.

## Human Review

A reviewer should be able to go from clean checkout to local validation using one document. The
runbook must not depend on previous chat context, hidden startup order or guessing ports.

## Feedback

If the runbook is hard to use, incomplete or repeatedly requires manual correction, agents must
capture project feedback under:

```text
08-qa/framework-feedback/
```

Reusable framework improvements may be proposed in:

```text
nexora-framework/07-governance/framework-improvement-backlog/
```

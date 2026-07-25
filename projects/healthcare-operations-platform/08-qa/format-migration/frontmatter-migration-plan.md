---
id: NXF-FMT-002-migration-plan
status: approved
backlog_item: NXF-FMT-002
created_date: 2026-07-24
paused_functional_backlog_item: COM-MOD-017-BE-002
---

# Frontmatter Migration Plan

## Objective

Reduce commercial-token consumption by making compact Markdown/frontmatter prompts, reports and
handoffs the default execution surface for agents, while preserving YAML only where it remains an
authoritative automation registry.

## Completed Batch

| Batch | Scope | Result |
|---|---|---|
| 0 | Migration reporting | Replaced full YAML migration reports with compact Markdown/frontmatter reports. |
| 0 | Reference cleanup | Updated official pointers to `.md` reports and removed stale report YAML references. |
| 0 | Framework rule | Official migration reports now prohibit full per-file inventories by default. |

## Migration Tiers

| Tier | Artifact Type | Handling |
|---|---|---|
| 0 | Generated backlog prompts and handoffs | Must be Markdown/frontmatter and compact. Agents read these first. |
| 1 | PROJECT_STATE and SOURCE_OF_TRUTH | Remain YAML until a dedicated parser/backward-compatibility backlog migrates them. Agents must load targeted keys only. |
| 2 | Product backlog and execution prompt registries | Remain YAML while used by the Python orchestrator; compact generated prompts are the agent-facing output. |
| 3 | QA/security evidence, requirements, capability packages and runbooks with MD companions | Convert gradually when touched by normal backlog work; do not create new YAML/MD duplicate pairs. |
| 4 | Historical evidence | Keep as archival data unless it is loaded by active prompts; convert only when referenced by an active backlog. |

## Agent Loading Rule

Agents must start from the generated prompt or compact handoff for the active backlog. They must not
preload broad YAML registries, evidence indexes or full capability catalogs. When a registry is
needed, agents must use targeted search and read only the specific keys or files required for the
active backlog.

## Remaining Non-Blocking Work

| Area | Follow-Up |
|---|---|
| YAML registry parser | Add frontmatter-aware readers before migrating PROJECT_STATE, SOURCE_OF_TRUTH or product backlog registries. |
| Capability packages | Convert package YAML to frontmatter Markdown as packages are modified by future backlog items. |
| Evidence archive | Convert active evidence only when referenced by current prompt or validation flow. |

## Closure Decision

`NXF-FMT-002` can close because the token-heavy migration evidence has been replaced, the framework
now prevents recurrence, the active agent entrypoint is compact, and remaining YAML is explicitly
classified as non-blocking gradual migration work.

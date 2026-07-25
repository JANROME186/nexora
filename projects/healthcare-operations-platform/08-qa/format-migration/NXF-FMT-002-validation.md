---
id: NXF-FMT-002-validation
status: closed
backlog_item: NXF-FMT-002
paused_functional_backlog_item: COM-MOD-017-BE-002
created_date: 2026-07-24
---

# NXF-FMT-002 Validation

HOP functional development is paused while Nexora Framework and HOP artifact formats are optimized
for commercial-token reduction.

## Current Baseline

| Scope | Candidates | Planned | Written | Errors | Reference Issues |
|---|---:|---:|---:|---:|---:|
| HOP | 2187 | 2151 | 0 | 0 | 0 |
| Nexora Framework | 205 | 198 | 0 | 0 | 0 |

## Status

The migration is closed for execution purposes. The migration tool no longer emits full YAML
inventories as official evidence, and the previous heavy YAML reports were replaced by compact
Markdown/frontmatter reports.

## Batch 0 Evidence

| Evidence | Result |
|---|---|
| HOP compact report | `08-qa/format-migration/frontmatter-migration-report-projects-healthcare-operations-platform.md` |
| Framework compact report | `08-qa/format-migration/frontmatter-migration-report-nexora-framework.md` |
| HOP report size | ~5 KB, replacing the previous ~1.6 MB YAML report |
| Framework report size | ~3 KB, replacing the previous ~107 KB YAML report |
| Official YAML report references | Removed |

## Migration Plan

The remaining YAML artifacts are classified in `08-qa/format-migration/frontmatter-migration-plan.md`.
Authoritative automation registries remain YAML until a dedicated compatibility backlog migrates
their readers. Agents must use generated compact prompts/handoffs as the active execution surface
and must not preload broad YAML registries.

## Closure

`TD-FMT-001` is reduced from blocking to non-blocking gradual migration debt. Functional HOP
development may resume at `COM-MOD-017-BE-002`.

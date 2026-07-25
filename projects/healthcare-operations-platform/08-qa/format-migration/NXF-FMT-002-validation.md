---
id: NXF-FMT-002-validation
status: active_batch_0_completed
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

The migration is active. Batch 0 is complete: the migration tool no longer emits full YAML
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

No mass conversion, source archive or authoritative pointer replacement has been applied yet. The
next execution must process controlled batches using local Python/PyYAML and Ollama only.

# COM-MOD-011-DEF Security Quality Evidence

**Status:** passed
**Backlog item:** COM-MOD-011-DEF (definition-only, no code changed)

## Scope note

This backlog item creates no new capability package. It confirms all 7 COM-MOD-011 capabilities
(BCM-SVC-001/002/003/005, BCM-ATT-001/006, BCM-PLT-005) are reused from already-modeled/compiled
capability packages, extends each with a public_website surface, materially reduces TD-BE-015,
and updates project registries. No backend, frontend or mobile code was created or modified, so
build/test/coverage/SAST/dependency/DAST gates do not apply and are unchanged from their last
measured values.

## Checks

| Check | Result |
| --- | --- |
| Tests / SAST / dependency scan | not applicable — no code changed |
| Coverage | passed — no regression |
| Secrets scan | passed (0 matches) |
| Message externalization / i18n review | passed — no new rendered UI text |
| YAML parse | passed (1,152 repo-wide files, 0 failures) |
| Agent-agnostic scan | passed (0 matches) |
| Stale pointer sweep | passed |
| `git diff --check` | passed |

## Anonymous public surface review

This is the first HOP backlog item to model an anonymous, rate-limited public surface. Read
operations return only `status=published` snapshots, never drafts. Write operations
(`appointment.request.public`, `quotation.request.public`) can only create a requested/draft-state
record from a `ProspectiveContact` and can never confirm, issue, accept, convert or read other
actors' records under the public scope. All public operations are governed by BCM-PLT-005
`ApiSurfaceRegistration` classification and `RateLimitPolicy`, with a new `RN-007`/
`consumerIdentificationMethod` field modeling IP-address/session-token identification for
anonymous rate-limiting. Runtime enforcement remains a COM-MOD-011-BE-001 task.

## Open-source-first

No proprietary runtime dependency or new dependency introduced. New public operations reuse
existing schemas (`PublishedServiceSnapshot`, `PublishedTestSnapshot`, `PublishedPanelSnapshot`,
`PublishedPreparationSnapshot`, `ProspectiveContact`) and the existing `RateLimitPolicy` entity.

## Technical debt

**TD-BE-015** (rate-limit enforcement scoped to partner-API-key-bearing requests only) was
materially reduced through a modeling-stage decision: `RN-007` and
`RateLimitPolicy.consumerIdentificationMethod` model IP-address/session-token identification for
the public tier. No code was written; status is `materially_reduced`, not closed.

## Stale pointers and defects found and corrected

- Three pre-existing stale roadmap/status pointers in BCM-ATT-001, BCM-ATT-006 and BCM-PLT-005's
  `capability-package.yaml`/`traceability.yaml` (stuck at COM-MOD-009/"pending"/"modeled" though
  their owning modules had long since closed).
- A pre-existing YAML-validity defect in project `SOURCE_OF_TRUTH.yaml` (an unescaped colon
  inside a plain scalar, unreadable by a strict YAML parser).

## Coverage baselines (unchanged)

| Stack | Coverage |
| --- | --- |
| Backend (Java/Maven) | 83.73% |
| Employee portal | 88.24% |
| Mobile app | 99.21% |
| Patient portal | 94.11% |
| Doctor portal | 96.28% |

## Commercial readiness disclosure

HOP is **not** commercially complete or GA-ready. COM-MOD-011-BE-001 through
COM-MOD-011-CLOSEOUT and all subsequent REL-002/003/004 modules remain planned.

## Readiness

Ready for **COM-MOD-011-BE-001** — Compile public catalog, location and request outputs.

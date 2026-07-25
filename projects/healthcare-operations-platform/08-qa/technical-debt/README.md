# HOP Technical Debt

This folder tracks modernization, migration, dependency, framework, security and tooling debt found
during Healthcare Operations Platform backlog execution. Some items are non-blocking, but enterprise
quality alignment items can block functional development until remediated or explicitly accepted.

Agents must review technology debt during every code-changing backlog item, module closeout and
release readiness gate. The initial stack is the current baseline, not a permanent constraint.

Context-efficient execution adds one extra format rule: new task handoffs should be compact
Markdown with minimal frontmatter. Existing YAML remains supported until migrated, but monolithic
YAML task/state artifacts must be tracked as format migration debt (`TD-FMT-001`) when they are
touched.

When a better open source, safer, more maintainable or more cost-effective option is detected,
register or update a debt item here. If the finding affects mandatory quality gates, vulnerability
evidence, message externalization, DAST or safe development, promote it to blocking or immediate
quality alignment work.

Missing mandatory validation tooling is technical debt. If a backlog touches a stack or runnable
surface and HOP lacks an executable duplicate-code, complexity, SAST/static analysis,
OWASP/secure-code, dependency, secrets, coverage, i18n, accessibility, DAST, SBOM or license gate,
the agent must create or update a debt item here before closure. "If configured", "if scripts
exist" and informal `not_applicable` notes are not valid when the surface exists.

## Quality Alignment Debt (closed)

- `TD-I18N-001` - message externalization and magic-string remediation baseline. Closed by
  `HOP-QA-ALIGN-005`.
- `TD-QA-001` - DAST automation for runnable web and API surfaces. Closed by `HOP-QA-ALIGN-004`.

These items were addressed by:

`06-delivery/commercial-product/HOP_QUALITY_ALIGNMENT_BACKLOG.md`

(Corrected during `MVP-MOD-005-CLOSEOUT`: both entries had been closed in
`technical-debt-index.md` since `HOP-QA-ALIGN-CLOSEOUT` but this section still listed them as
active/blocking.)

## Final Closure Debt

HOP cannot be marked commercially complete, GA-ready or finally closed while any technical-debt item
remains open. Coverage-related final closure debt includes:

- `TD-BE-003` - backend coverage must improve from 67.47% to at least 80%.
- `TD-FE-004` - closed; frontend coverage reached 80.66% in MVP-MOD-005-QA-001 (was 76.51%).
- `TD-APP-002` - mobile coverage must become measurable and reach at least 80%.

Until 80% is reached, the previous measured coverage is the hard lower bound for the next
iteration. Coverage must not decrease.

# HOP Technical Debt

This folder tracks modernization, migration, dependency, framework, security and tooling debt found
during Healthcare Operations Platform backlog execution. Some items are non-blocking, but enterprise
quality alignment items can block functional development until remediated or explicitly accepted.

Agents must review technology debt during every code-changing backlog item, module closeout and
release readiness gate. The initial stack is the current baseline, not a permanent constraint.

When a better open source, safer, more maintainable or more cost-effective option is detected,
register or update a debt item here. If the finding affects mandatory quality gates, vulnerability
evidence, message externalization, DAST or safe development, promote it to blocking or immediate
quality alignment work.

## Active Blocking Quality Alignment Debt

- `TD-I18N-001` - message externalization and magic-string remediation baseline.
- `TD-QA-001` - DAST automation for runnable web and API surfaces.

These items are addressed by:

`06-delivery/commercial-product/HOP_QUALITY_ALIGNMENT_BACKLOG.yaml`

## Final Closure Debt

HOP cannot be marked commercially complete, GA-ready or finally closed while any technical-debt item
remains open. Coverage-related final closure debt includes:

- `TD-BE-003` - backend coverage must improve from 67.47% to at least 80%.
- `TD-FE-004` - frontend coverage must improve from 76.51% to at least 80%.
- `TD-APP-002` - mobile coverage must become measurable and reach at least 80%.

Until 80% is reached, the previous measured coverage is the hard lower bound for the next
iteration. Coverage must not decrease.

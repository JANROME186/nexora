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

- `TD-FE-003` - frontend enterprise quality profile.
- `TD-APP-001` - mobile quality baseline.
- `TD-QA-003` - all-severity vulnerability evidence.
- `TD-I18N-001` - message externalization and magic-string remediation baseline.

These items are addressed by:

`06-delivery/commercial-product/HOP_QUALITY_ALIGNMENT_BACKLOG.yaml`

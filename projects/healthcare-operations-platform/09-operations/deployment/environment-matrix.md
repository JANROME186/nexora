# HOP Environment Matrix

Backlog item: `COM-MOD-012-OPS-001`

HOP uses five promotion environments: `local`, `dev`, `qa`, `staging` and `prod`.

| Environment | Purpose | Data | Approval | Observability |
| --- | --- | --- | --- | --- |
| `local` | Single-machine developer and reviewer execution | Synthetic only | No | Local logs, optional metrics |
| `dev` | Shared integration | Synthetic/generated | No | Basic metrics, logs and traces |
| `qa` | Automated regression, contract, security and integration evidence | Synthetic or masked | QA owner | Full validation telemetry |
| `staging` | Production rehearsal | Masked production-like data | Product and operations owners | Production-equivalent |
| `prod` | Customer-facing SaaS | Customer data | Release and operations owners | Production telemetry |

Every environment must use immutable artifacts, explicit configuration, secret-provider integration, deployment audit events, health/readiness/liveness checks, rollback plans, vulnerability evidence with zero unresolved findings and no coverage regression.

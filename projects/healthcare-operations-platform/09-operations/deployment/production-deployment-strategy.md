# HOP Production Deployment and Environment Strategy

Backlog item: `COM-MOD-012-OPS-001`

This strategy defines how HOP moves from local execution to production-like SaaS operation without binding the product to a specific agent, vendor runtime or proprietary platform. The preferred path is open-source-first, reproducible and evidence-driven.

## Scope

The strategy covers environment profiles, deployment units, configuration, secrets, tenant onboarding, release promotion, rollback, observability entry points and operational quality gates.

It is tied to the COM-MOD-012 capability packages:

- `BCM-ORG-001` tenant lifecycle, subscription state and isolation.
- `BCM-PLT-001` IAM, permissions and privileged operational access.
- `BCM-PLT-002` environment configuration and feature flags.
- `BCM-PLT-005` API hardening, rate limits and gateway policy.
- `BCM-PLT-006` observability, probes, metrics, logs and traces.
- `BCM-PLT-007` audit trail and evidence retention.
- `BCM-PLT-008` operational documents and runbooks.
- `BCM-PLT-009` deployment, approval, rollback and incident workflows.

## Environment Path

HOP must promote through `local`, `dev`, `qa`, `staging` and `prod`.

Local execution remains Docker Compose based for developer and reviewer reproducibility. Shared environments must be Kubernetes-compatible so deployment assets can move toward production without redesign. Production must use immutable images, explicit configuration, managed or HA PostgreSQL, OpenTelemetry-compatible telemetry, Prometheus-compatible metrics and auditable release evidence.

## Deployment Units

The backend is released as a container image. Web portals and the public website are released as static web bundles or static web containers. The mobile app remains a mobile build artifact with its own quality gate and compatibility validation.

Every unit must publish a release identity that includes the git commit SHA, artifact version, image digest when applicable, SBOM path, migration version and evidence bundle path.

## Configuration And Secrets

Only non-secret defaults, resource classes, schema versions and feature flag definitions can live in source control. Passwords, signing keys, partner tokens and customer data are forbidden in source control.

Configuration precedence is:

1. Immutable release defaults.
2. Environment profile.
3. Tenant configuration.
4. Runtime secret provider.
5. Emergency break-glass override with audit.

## Promotion And Rollback

A release can move forward only when quality gates pass, migration dry-runs succeed, rollback instructions exist, vulnerability evidence has zero unresolved findings, tenant impact is documented and observability assets are ready.

Rollback must prefer blue/green or canary rollback by image digest. Database changes must be forward-only unless an ADR approves a destructive path with backup and restore rehearsal.

## Tenant Onboarding

Tenant onboarding starts from `BCM-ORG-001` and must create the tenant profile, subscription state, locale/country/currency defaults, isolation policy, first administrator, feature flags, observability target and onboarding evidence.

The base SaaS configuration must support `es-MX`, `en-US`, `MXN` and `USD`.

## Technical Debt

`TD-STACK-001` is materially reduced by this strategy because HOP now has runtime modernization lanes, upgrade triggers and rollback controls. It remains open until component-specific stack upgrades are executed and validated.

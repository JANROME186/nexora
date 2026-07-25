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

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OPS-DEPLOY-001
  type: production-deployment-strategy
  name: HOP Production Deployment and Environment Strategy
  version: 1.0.0
  status: approved
  backlog_item: COM-MOD-012-OPS-001
  module: COM-MOD-012 Platform Hardening and SaaS Operations
  human_readable: production-deployment-strategy.md
  machine_readable: production-deployment-strategy.md
  owner: Nexora Platform Operations
  date: 2026-07-22
principles:
  agent_agnostic: true
  open_source_first: true
  model_driven_source_of_truth: true
  reproducible_environments: true
  immutable_release_artifacts: true
  explicit_environment_promotion: true
  zero_untracked_runtime_configuration: true
capability_traceability:
  BCM-ORG-001:
    responsibility: Tenant lifecycle, subscription state, tenant isolation and onboarding
      controls.
  BCM-PLT-001:
    responsibility: IAM, role/permission enforcement, session context and privileged
      operational access.
  BCM-PLT-002:
    responsibility: Environment configuration, tenant overrides, feature flags and
      maintenance mode.
  BCM-PLT-005:
    responsibility: API gateway policy, CORS, CSP, HSTS, rate limits and external
      API governance.
  BCM-PLT-006:
    responsibility: Metrics, logs, traces, health probes, dashboards and alerts.
  BCM-PLT-007:
    responsibility: Append-only audit trail and operational evidence retention.
  BCM-PLT-008:
    responsibility: Operational document templates, generated runbooks and release
      evidence documents.
  BCM-PLT-009:
    responsibility: Operational workflows for deployment, approval, rollback and incident
      handoff.
target_runtime_topology:
  local:
    purpose: Developer and reviewer execution on one workstation.
    orchestration: Docker Compose.
    database: PostgreSQL 16 container.
    observability: Local logs plus optional Prometheus/Grafana profile.
  dev:
    purpose: Shared integration for active development.
    orchestration: Kubernetes-compatible manifests or Compose-equivalent profile.
    database: PostgreSQL managed or self-hosted instance with isolated schema/database
      per environment.
    observability: OpenTelemetry collector, Prometheus, Grafana and Loki-compatible
      log store.
  qa:
    purpose: Automated integration, security, contract and regression validation.
    orchestration: Kubernetes-compatible manifests.
    database: Restorable production-like PostgreSQL dataset with synthetic or masked
      data only.
    observability: Full telemetry and alert dry-runs.
  staging:
    purpose: Production rehearsal and release candidate validation.
    orchestration: Kubernetes-compatible manifests promoted from QA.
    database: Production-like topology with backup/restore rehearsal.
    observability: Production-equivalent dashboards, alerts and SLO checks.
  prod:
    purpose: Customer-facing SaaS operation.
    orchestration: Kubernetes-compatible production cluster.
    database: PostgreSQL HA deployment or managed PostgreSQL service.
    observability: Required metrics, logs, traces, SLO alerts and incident notifications.
deployment_units:
  backend:
    artifact_type: container_image
    source: 07-implementation/backend
    required_gates:
    - mvn -Pquality -Dhop.local-db-tests=true clean verify
    - OWASP Dependency-Check using local advisory database
    - Trivy filesystem and image scan across all severities
    - CycloneDX SBOM
  employee_portal:
    artifact_type: static_web_container_or_cdn_bundle
    source: 07-implementation/employee-portal
    required_gates:
    - npm run quality
    - npm audit across all severities
    - Trivy filesystem scan
  public_website:
    artifact_type: static_web_container_or_cdn_bundle
    source: 07-implementation/public-website
    required_gates:
    - npm run quality
    - npm run build
    - npm audit across all severities
    - Trivy filesystem scan
  patient_portal:
    artifact_type: static_web_container_or_cdn_bundle
    source: 07-implementation/patient-portal
    required_gates:
    - npm run quality
    - npm audit across all severities
  doctor_portal:
    artifact_type: static_web_container_or_cdn_bundle
    source: 07-implementation/doctor-portal
    required_gates:
    - npm run quality
    - npm audit across all severities
  mobile_app:
    artifact_type: mobile_build_artifact
    source: 07-implementation/mobile-app
    required_gates:
    - npm run quality
    - npm audit across all severities
environment_configuration_policy:
  source_control_allowed:
  - non_secret_defaults
  - schema_versions
  - feature_flag_definitions_without_customer_secrets
  - public_endpoint_routes
  - resource_classes
  source_control_forbidden:
  - database_passwords
  - signing_keys
  - SMTP_passwords
  - external_partner_tokens
  - production_customer_data
  secret_management:
    local: .env files excluded from git plus documented sample files.
    shared_environments: Kubernetes Secrets or compatible open-source sealed-secret
      mechanism.
    production: External secret store or sealed secret workflow with rotation audit.
  configuration_precedence:
  - immutable_release_defaults
  - environment_profile
  - tenant_configuration
  - runtime_secret_provider
  - emergency_break_glass_override_with_audit
promotion_strategy:
  stages:
  - local
  - dev
  - qa
  - staging
  - prod
  promotion_requirements:
  - All required quality gates pass for changed deployment units.
  - Database migration dry-run succeeds against staging-like schema.
  - Rollback plan is attached to the release candidate.
  - Release SBOM and vulnerability evidence show zero unresolved vulnerabilities.
  - Tenant-impact assessment is approved for multi-tenant changes.
  - Observability dashboards and alerts exist for new operational paths.
  release_artifact_identity:
    required:
    - git_commit_sha
    - semantic_version_or_release_candidate_id
    - container_image_digest
    - sbom_path
    - migration_version
    - evidence_bundle_path
rollback_strategy:
  application:
    preferred: Blue/green or canary rollout with previous image digest retained.
    required_controls:
    - feature_flag_disable_path
    - previous_image_digest
    - smoke_test_after_rollback
    - audit_event_for_rollback
  database:
    preferred: Forward-only migrations with compensating scripts.
    destructive_change_policy: Prohibited without backup, restore rehearsal and explicit
      ADR.
    required_controls:
    - pre_migration_backup
    - migration_id
    - compatibility_window
    - restore_runbook_reference
  tenant:
    preferred: Tenant-by-tenant activation for risky operational changes.
    required_controls:
    - tenant_allowlist
    - tenant_status_check
    - tenant_specific_feature_flag
tenant_onboarding_strategy:
  workflow_owner: BCM-ORG-001
  minimum_steps:
  - create_tenant_profile
  - assign_subscription_state
  - configure default locale, country and currency
  - apply tenant isolation policy
  - create initial administrator account
  - assign least-privilege permissions
  - configure feature flags
  - verify health, audit and observability targets
  - generate onboarding evidence document
  base_locales:
  - es-MX
  - en-US
  base_currencies:
  - MXN
  - USD
technical_debt_reduction:
  item: TD-STACK-001
  disposition: materially_reduced
  reason: 'This strategy defines modernization trigger points, supported runtime lanes,
    upgrade windows, rollback controls and production environment compatibility checks.
    Actual framework/runtime upgrades remain gradual and component-specific.

    '
closure:
  current_backlog_item: COM-MOD-012-OPS-001
  next_backlog_item: COM-MOD-012-OPS-002
  status: closed
```

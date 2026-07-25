# COM-MOD-012-BE-001 Security and Quality Evidence

## Overview

Backend compilation of BCM-ORG-001 tenant provisioning/listing/status-transition operations, the new BCM-PLT-002 Platform Configuration and Feature Flags module, and BCM-PLT-006 observability extensions (Prometheus metrics, liveness/readiness health groups, tenant/user/trace MDC logging context). Materially reduces TD-IAM-002 and TD-DB-004; further reduces TD-I18N-002. Registers TD-BE-016, TD-BE-017 and TD-IAM-003 for honestly-scoped deferred capability-package extensions (BCM-PLT-001/005/007/008/009 were not fully implemented — see the QA validation evidence for the scoping rationale).

## Security controls

### Authorization

- All new/extended endpoints sit under `/api/platform/**`, already enforced by `HopAuthorizationInterceptor`.
- Two new `EndpointPermissionRegistry` entries (`/api/platform/config`, `/api/platform/feature-flags`) map to a new `PermissionCode.SCREEN_PLATFORM_CONFIGURATION`, granted to `ADMIN` only via the existing deny-by-default `RolePermissionCatalog`.
- `listTenants` and `updateTenantStatus` automatically inherit the pre-existing `/api/platform/tenants` prefix rule; no registry change was needed.

### Privileged-operation audit

- `updateTenantStatus` → `TenantStatusChanged` audit event (previous/new status, reason).
- `updateFeatureFlag` → `FeatureFlagUpdated` audit event (enabledByDefault, rolloutPercentage, updatedBy).
- Both reuse the existing BCM-PLT-007 `AuditRecorder` — the PF-BE-004 mechanism, not a new one.

### Header input hardening (real finding, fixed)

SpotBugs/FindSecBugs flagged `SERVLET_HEADER` (CWE-807) on the new `RequestObservabilityContextFilter`, which reads `X-HOP-TENANT-ID`, `X-HOP-USER-ID` and `traceparent` request headers to populate logging context. Two concrete risks were identified and closed in code, not suppressed:

1. **Log injection**: a header value containing CR/LF could forge extra log lines. `sanitizeForLogging()` strips every control character and bounds the value to 120 characters before it reaches MDC.
2. **Header reflection**: the `traceparent` trace-id segment was previously echoed back verbatim in the `X-Trace-Id` response header. `resolveTraceId()` now only reuses it when it matches the strict W3C 32-lowercase-hex format; anything else is discarded and a fresh id is minted.

Both are covered by dedicated tests (`stripsControlCharactersFromTenantAndUserHeadersBeforeLogging`, `rejectsMalformedTraceparentAndMintsANewTraceIdInstead`). SpotBugs still reports the rule at low priority (rank 15) because it fires on any direct header read — the identical rule already fires on the pre-existing, accepted `HopAuthenticationResolver` — so this is a residual, non-blocking, already-accepted class of finding, not a new risk.

### Message externalization

New i18n keys added to `messages.properties`, `messages_es_MX.properties`, `messages_en_US.properties`:

- `organizationmanagement.error.entity_not_found`
- `organizationmanagement.error.organization_command_invalid`
- `organizationmanagement.error.tenant_code_conflict`
- `platformconfiguration.error.command_invalid`

No hardcoded error strings were introduced by this backlog item's new code paths.

### Input validation

- `jakarta.validation @NotBlank` on the new request DTOs' required fields.
- Tenant `code` uniqueness enforced by an application-level pre-check (`findTenantByCode`) plus a database unique index (`tenants_code_key`) as a defense-in-depth backstop against a race condition.
- Feature flag `flagKey` validated against the `platform.<area>.<name>` namespace regex; `rolloutPercentage` bounded to 0-100.
- Tenant `status` validated against the four-value enum.
- `OrganizationManagementExceptionHandler` and `PlatformConfigurationExceptionHandler` map every invalid-command/not-found/conflict exception to a structured `{status, code, messageKey, message, occurredAt}` envelope.

### Cross-module boundaries

`platformconfiguration`'s `package-info.java` declares `allowedDependencies=[auditcompliance]` only — it does not reach into `organizationmanagement` or `identityaccess` internals; tenant scoping for `evaluateFeatureFlags` is a caller-supplied request parameter, not a cross-module security-context read. `PlatformFoundationModulithTest.moduleBoundariesAreValid` passes with the new module and the two extended modules.

## Quality gates

| Gate | Result |
| --- | --- |
| Backend tests (`mvn -Pquality clean verify -Dhop.local-db-tests=true`) | 362 tests, 0 failures, 0 errors, 0 skipped |
| Backend line coverage (JaCoCo) | 84.11% (10,039 / 11,935 lines) — above the 83.99% floor (+0.12pp) |
| Checkstyle | 22 findings, 0 in new/touched files |
| PMD/CPD | 480 findings / 2 duplications; 6 in touched files, all pre-existing accepted patterns |
| SpotBugs/FindSecBugs | 35 findings; 2 new (`SERVLET_HEADER`, low priority, mitigated in code) |
| Duplicate-finder | passed, no new classpath conflicts |
| SBOM (CycloneDX) | 110 components |
| Spring Modulith boundary check | 0 violations |
| OWASP Dependency-Check | 115 dependencies scanned, 0 vulnerabilities |
| Trivy filesystem scan (v0.72.0, all severities, vuln+secret+misconfig) | 0 vulns, 0 secrets, 0 misconfigurations |
| YAML parse | 1,248 files, 0 errors |
| Agent-agnostic scan | 70 documentation/CSS mentions; **0** real agent-vendor references in source code |
| Secrets scan (Trivy) | 0 findings |
| Stale-pointer sweep | 0 stale pointers |
| `git diff --check` | 0 whitespace errors (LF/CRLF normalization warnings only) |

## Technical debt

- **Materially reduced**: TD-IAM-002 (permission granularity — two new endpoint-level registry entries), TD-DB-004 (tenant isolation — `isolationStrategy` field persisted, real suspend/archive containment control added to `tenant-impact-triage-runbook.md`), TD-I18N-002 (message externalization — two new i18n namespaces).
- **Registered (new, honestly-scoped deferrals)**: TD-BE-016 (BCM-PLT-007 search/export not fully compiled, low risk), TD-BE-017 (BCM-PLT-009 Workflow Engine not implemented, medium risk), TD-IAM-003 (BCM-PLT-001 MFA/service-account/scope-grammar not implemented, low risk).

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-SQ-COM-MOD-012-BE-001
  type: security-quality-evidence
  name: COM-MOD-012-BE-001 Security and Quality Evidence
  version: 1.0.0
  status: passed
  captured_on: 2026-07-23
scope: Backend compilation of BCM-ORG-001 tenant provisioning/listing/status-transition
  operations, the new BCM-PLT-002 Platform Configuration and Feature Flags module,
  and BCM-PLT-006 observability extensions (Prometheus metrics, liveness/readiness
  health groups, tenant/user/trace MDC logging context). Materially reduces TD-IAM-002
  and TD-DB-004; further reduces TD-I18N-002. Registers TD-BE-016, TD-BE-017 and TD-IAM-003
  for honestly-scoped deferred capability-package extensions.
security_controls:
  authentication:
    enforcement_unchanged: All four tenant-management endpoints and both feature-flag/config
      endpoints sit under /api/platform/**, already covered by HopAuthorizationInterceptor
      via EndpointPermissionRegistry. No authentication mechanism was added or bypassed
      by this backlog item.
  authorization:
    permission_registry_entries_added:
    - path_prefix: /api/platform/config
      permission: SCREEN_PLATFORM_CONFIGURATION
      capability: BCM-PLT-002
    - path_prefix: /api/platform/feature-flags
      permission: SCREEN_PLATFORM_CONFIGURATION
      capability: BCM-PLT-002
    pre_existing_entry_reused:
      path_prefix: /api/platform/tenants
      permission: SCREEN_TENANTS
      capability: BCM-ORG-001
      note: listTenants and updateTenantStatus automatically inherit this pre-existing
        prefix-matched rule; no registry change was needed for them.
    deny_by_default: SCREEN_PLATFORM_CONFIGURATION is granted to ADMIN only via RolePermissionCatalog's
      EnumSet.allOf(PermissionCode.class); every other role is denied by default (no
      explicit grant added).
  privileged_operation_audit:
  - operation: updateTenantStatus
    action: TenantStatusChanged
    mechanism: existing BCM-PLT-007 AuditRecorder.recordSystemEvent, tenant-scoped
    metadata: previousStatus, newStatus, reason
  - operation: updateFeatureFlag
    action: FeatureFlagUpdated
    mechanism: existing BCM-PLT-007 AuditRecorder.recordSystemEvent, platform-scoped
      (tenantId=null)
    metadata: enabledByDefault, rolloutPercentage, updatedBy
  header_input_hardening:
    finding: SpotBugs/FindSecBugs SERVLET_HEADER (CWE-807) on the new RequestObservabilityContextFilter,
      which reads X-HOP-TENANT-ID, X-HOP-USER-ID and traceparent request headers.
    mitigation:
      log_injection_prevention: sanitizeForLogging() strips every Unicode control
        character (including CR/LF) from tenant/user header values and bounds them
        to 120 characters before they are ever put into MDC, preventing a hostile
        header from forging additional log lines or exhausting log storage.
      header_reflection_prevention: resolveTraceId() only reuses the inbound traceparent
        trace-id segment when it matches the strict W3C Trace Context format (32 lowercase
        hex characters); any other value is discarded and a fresh UUID-derived id
        is minted instead, so an attacker-controlled value is never reflected back
        in the X-Trace-Id response header.
    test_coverage:
    - RequestObservabilityContextFilterTest.stripsControlCharactersFromTenantAndUserHeadersBeforeLogging
    - RequestObservabilityContextFilterTest.rejectsMalformedTraceparentAndMintsANewTraceIdInstead
    residual_finding_disposition: SpotBugs continues to report SERVLET_HEADER at low
      priority (rank 15) because the rule fires on any direct header read regardless
      of downstream handling; the identical rule already fires on the pre-existing,
      accepted HopAuthenticationResolver. quality.failOnViolation=false per the project's
      established policy; the real risk (log/header injection) is closed in code.
  message_externalization:
    hardcoded_error_strings_added: 0
    new_i18n_keys:
    - organizationmanagement.error.entity_not_found
    - organizationmanagement.error.organization_command_invalid
    - organizationmanagement.error.tenant_code_conflict
    - platformconfiguration.error.command_invalid
    locales_covered:
    - es-MX
    - en-US
    - default
  input_validation:
    request_body_validation: jakarta.validation @Valid + @NotBlank on ProvisionTenantRequest.legalName
      (via service-layer requiredNameText), UpdateTenantStatusRequest.status, UpdateFeatureFlagRequest.flagKey/updatedBy
    tenant_code_uniqueness: pre-check via findTenantByCode plus a database unique
      index (tenants_code_key) as a defense-in-depth backstop against a race condition
    feature_flag_namespace_validation: flagKey must match ^[a-z][a-z0-9]*(\.[a-z0-9_]+)+$
    feature_flag_rollout_bounds: rolloutPercentage must be 0-100
    tenant_status_enum_validation: status must be one of PENDING_PROVISIONING/ACTIVE/SUSPENDED/ARCHIVED
    error_handlers: OrganizationManagementExceptionHandler and PlatformConfigurationExceptionHandler
      map every invalid-command/not-found/conflict exception to a structured {status,
      code, messageKey, message, occurredAt} envelope
  cross_module_boundaries:
    platformconfiguration_module: allowedDependencies=[auditcompliance] only; no direct
      dependency on organizationmanagement or identityaccess internals (tenant scoping
      for evaluateFeatureFlags is caller-supplied via a request parameter, not resolved
      via a cross-module security context read)
    modulith_boundary_test: PlatformFoundationModulithTest.moduleBoundariesAreValid
      passes with the new platformconfiguration module and the extended organizationmanagement/observability
      modules
evidence_commands:
  backend_verify:
    command: mvn -Pquality clean verify -Dhop.local-db-tests=true checkstyle:checkstyle
      pmd:pmd pmd:cpd spotbugs:spotbugs cyclonedx:makeAggregateBom duplicate-finder:check
    environment: compose.local.json with postgres:16-alpine reachable at localhost:5432
      (container hop-local-postgres)
    result: BUILD SUCCESS; 362 tests, 0 failures/errors/skipped
  jacoco_coverage:
    file: 07-implementation/backend/target/site/jacoco/index.html
    line_coverage: 84.11%
    line_covered_of_total: 10039/11935
    previous_baseline: 83.99%
    delta: +0.12pp
  checkstyle:
    command: mvn -Pquality checkstyle:checkstyle
    findings_total: 22
    findings_in_touched_files: 0
  pmd_cpd:
    command: mvn -Pquality pmd:pmd pmd:cpd
    findings_total: 480
    cpd_duplications: 2
    findings_in_new_files: 6
    disposition: mirrors pre-existing accepted patterns (RowMapper unused rowNumber
      parameter, SimplifyBooleanReturns); no new violation class
  spotbugs_findsecbugs:
    command: mvn -Pquality spotbugs:spotbugs
    findings_total: 35
    findings_in_new_files: 2
    detail: see security_controls.header_input_hardening above
  duplicate_finder:
    command: mvn -Pquality duplicate-finder:check
    result: passed, no new classpath conflicts
  sbom_cyclonedx:
    command: mvn -Pquality cyclonedx:makeAggregateBom
    components: 110
    files:
    - target/bom.xml
    - target/bom.json
  owasp_dependency_check:
    command: mvn org.owasp:dependency-check-maven:check -DautoUpdate=false
    dependencies: 115
    vulnerable: 0
    findings: 0
    report: 07-implementation/backend/target/dependency-check-report.html
  trivy_filesystem_scan:
    command: trivy fs --skip-dirs target --skip-dirs node_modules --skip-dirs .m2
      --scanners vuln,secret,misconfig --severity UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL
      --exit-code 0 --timeout 15m projects/healthcare-operations-platform/07-implementation/backend
    version: 0.72.0
    vulnerabilities: 0
    secrets: 0
    misconfigurations: 0
    note: an untracked, gitignored 665MB local .m2/repository cache under the backend
      directory caused the first scan attempt to time out on filesystem walk alone;
      --skip-dirs .m2 (no source content) resolved it.
  yaml_parse:
    method: Python yaml.safe_load_all over every .yml/.yaml under the repository (excluding
      .git, node_modules, target, build, dist, coverage, .venv, .m2)
    files_parsed: 1248
    errors: 0
  agent_agnostic_scan:
    method: Python case-insensitive regex over source files under projects/healthcare-operations-platform
      (excluding .m2, target, node_modules, build, dist, coverage, .git)
    forbidden_patterns:
    - openai
    - claude
    - cursor
    - gemini
    - copilot
    total_matches: 70
    disposition:
    - matches in QA evidence, security-quality evidence and steering YAML/MD documenting
      the agent-agnostic scan patterns themselves.
    - CSS cursor pseudo-property matches (values pointer/not-allowed) in employee-portal
      and public-website styles.css.
    - a pre-existing jscpd HTML report incidentally containing the word "cursor".
    real_source_code_hits: 0
  secrets_scan:
    tool: trivy secret scanner (part of the fs scan above)
    findings: 0
  stale_pointer_sweep:
    method: grep repository-wide for the removed CreateTenantCommand type and the
      runbook known_gaps_and_forward_pointers entries naming COM-MOD-012-BE-001
    findings: 0 stale pointers (all updated by this backlog item's tracking edits)
  git_diff_check:
    command: git diff --check
    result: 0 whitespace errors (only Git LF/CRLF normalization warnings, exit code
      0)
closure:
  technical_debt_materially_reduced:
  - id: TD-IAM-002
    status_before: materially_reduced
    status_after: materially_reduced
    evidence: two new EndpointPermissionRegistry entries (SCREEN_PLATFORM_CONFIGURATION)
      continuing the endpoint-level permission grammar
  - id: TD-DB-004
    status_before: materially_reduced
    status_after: materially_reduced
    evidence: Tenant.isolationStrategy persisted per tenant; updateTenantStatus gives
      tenant-impact-triage-runbook.md TRIAGE-STEP-004B a real containment control
  - id: TD-I18N-002
    status_before: materially_reduced
    status_after: materially_reduced
    evidence: organizationmanagement.error.* and platformconfiguration.error.* i18n
      namespaces added
  technical_debt_registered:
  - id: TD-BE-016
    title: BCM-PLT-007 searchAuditEvents/exportAuditEvents openapi-source.md operations
      not fully compiled
    risk_level: low
  - id: TD-BE-017
    title: BCM-PLT-009 Workflow Engine not implemented
    risk_level: medium
  - id: TD-IAM-003
    title: BCM-PLT-001 MFA/service-account/action.scope grammar not implemented
    risk_level: low
```

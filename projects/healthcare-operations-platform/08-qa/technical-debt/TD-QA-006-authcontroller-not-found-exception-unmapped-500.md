---
id: TD-QA-006
format: markdown_structured_payload
type: technical-debt-item
name: AuthController.initiateAssistance returned an unhandled 500 for a nonexistent
  assistedUserId instead of 404
version: 1.0.0
status: closed
---

# Authcontroller.Initiateassistance Returned An Unhandled 500 For A Nonexistent Assisteduserid Instead Of 404

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-QA-006
  type: technical-debt-item
  name: AuthController.initiateAssistance returned an unhandled 500 for a nonexistent
    assistedUserId instead of 404
  version: 1.0.0
  status: closed
  created_date: 2026-07-23
  closed_date: 2026-07-23
  closed_by: COM-MOD-012-QA-001
source:
  discovered_during_backlog_item: COM-MOD-012-QA-001
  module: identityaccess (discovered by a DAST run scoped to COM-MOD-012; the affected
    endpoint belongs to an unrelated, already-shipped module)
  evidence: 08-qa/security-quality/COM-MOD-012-QA-001/zap-backend-api-run1.json (ZAP
    alerts "A Server Error response code was returned by the server", "Information
    Disclosure - Debug Error Messages" and "Application Error Disclosure" on POST
    /api/auth/assistance)
classification:
  category: backend_unhandled_exception
  affected_area: identityaccess_exception_advice_scope
  affected_components:
  - POST /api/auth/assistance
  risk_level: low
  urgency: low
  blocking: false
  reason_non_blocking: Confirmed the client response leaked no stack trace, exception
    class name or internal path -- only the standard Spring Boot default error body.
    Low risk, but a real behavioral defect (wrong HTTP status for a routine not-found
    case), fixed the same iteration it was found.
current_state:
  issue: IdentityAccessExceptionHandler is a @RestControllerAdvice(assignableTypes
    = IdentityAccessController.class) -- scoped to only one controller class. AuthController
    is a separate controller in the same identityaccess module whose initiateAssistance
    operation can also throw IdentityEntityNotFoundException (when assistedUserId
    does not resolve to a real user), but that advice's scoping meant the exception
    was never caught there, so it propagated to Spring's default handler as an unhandled
    500 instead of the intended 404.
resolution:
  fix: Widened IdentityAccessExceptionHandler's assignableTypes to { IdentityAccessController.class,
    AuthController.class }, so AuthController's operations now also get the existing
    IdentityEntityNotFoundException -> 404 and InvalidIdentityCommandException/MethodArgumentNotValidException
    -> 400 mappings. No new exception-handling logic was added; the existing, already-tested
    mapping was simply extended to cover the controller it was missing.
  regression_tests:
  - AuthControllerTest.assistanceForANonexistentAssistedUserReturnsNotFoundInsteadOfServerError
  verified_live: curl -X POST http://localhost:8090/api/auth/assistance with a nonexistent
    assistedUserId (00000000-0000-0000-0000-000000000000) returned 404 (previously
    500) after the fix, rebuilt and restarted.
remediation:
  strategy: fixed_in_COM_MOD_012_QA_001
  owner: backend_team
  estimated_effort: small
  estimated_cost_impact: low
  acceptance_criteria:
  - POST /api/auth/assistance with a nonexistent assistedUserId returns 404 with the
    standard IdentityAccessExceptionHandler error envelope, not a 500.
  - Existing AuthController happy-path and lockout tests remain green (unregressed).
```

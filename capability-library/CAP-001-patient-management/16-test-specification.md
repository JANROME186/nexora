# CAP-001 Patient Management - Test Specification

## Test levels

| Level | Scope |
|---|---|
| Unit | Domain rules, state transitions, value objects. |
| Contract | OpenAPI request/response validation. |
| Integration | Repository, event publishing, duplicate detection. |
| E2E | Patient registration, search, update, consent, guardian. |
| Security | Permission checks, masking, tenant isolation. |
| Performance | Search and registration response times. |
| Compatibility | Web/mobile low-resource flows. |

## Required test scenarios

- Register adult patient with complete data.
- Register minor patient without guardian must fail.
- Register minor patient with guardian must succeed.
- Duplicate detection warning must appear.
- Inactive patient cannot be used for new order.
- Unauthorized user cannot view sensitive fields.
- Patient update creates audit log.
- Consent recording is versioned.
- API contract matches OpenAPI.
- Patient search is tenant-isolated.

## Quality gates

- Domain unit tests must pass before API implementation is accepted.
- Contract tests must pass before frontend/mobile integration.
- Security tests must pass before release candidate.

# 18 Compliance

## Compliance Considerations

- IAM is a control plane for clinical, financial and administrative privacy.
- Every sensitive action must have a user, tenant, branch and timestamp when applicable.
- Access reviews support compliance evidence.
- Audit records for permissions and roles must be tamper-evident.
- Country packs may define retention, identity verification and electronic signature requirements.

## Audit Requirements

Audit must capture:

- Actor user.
- Tenant.
- Branch when applicable.
- Target user/employee/role/permission.
- Previous value.
- New value.
- Reason.
- Approval reference for high-risk changes.
- Correlation ID.

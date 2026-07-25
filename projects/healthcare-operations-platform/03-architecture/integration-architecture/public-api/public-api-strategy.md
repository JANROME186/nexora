# Public API Strategy

**Artifact ID:** IIA-API-001
**Version:** 0.20.0

## Purpose

Nexora Public APIs allow laboratories, physicians, partners, marketplaces, billing providers, patient apps and external systems to integrate with the platform.

## Principles

- API Contract First.
- Versioned APIs.
- Backward compatibility by default.
- OAuth2/OIDC security.
- Tenant-aware API access.
- Rate limiting.
- Auditability.
- Developer documentation.
- SDK generation from OpenAPI.

## API Versioning

Initial public API path convention:

```text
/api/v1/{resource}
```

Breaking changes require a new major version.

## API Categories

- Patient API.
- Orders API.
- Results API.
- Catalogs API.
- Billing API.
- Physicians API.
- Webhooks API.
- Integration API.
- Marketplace API.

## Public API Governance

No public endpoint can be implemented without:

- OpenAPI contract.
- Security model.
- Rate-limit policy.
- Error model.
- Pagination model when applicable.
- Test cases.
- Documentation.

# API Compatibility and Deprecation Policy

**Artifact ID:** ADP-001  
**Status:** Draft  
**Version:** 0.22.0

## Purpose

Nexora APIs must evolve without breaking existing web, mobile, integration and customer implementations.

## Versioning Rules

1. Public APIs must be versioned.
2. Breaking changes require a new major version.
3. Non-breaking additions may be added to the current version.
4. Deprecated endpoints must remain available during the deprecation window.
5. OpenAPI is the source of truth for versioning, deprecation and compatibility.

## Deprecation Metadata

OpenAPI operations should include:

```yaml
deprecated: true
x-nexora-deprecation:
  deprecatedIn: 1.4.0
  removalNotBefore: 2.0.0
  replacement: GET /v2/patients/{id}
  reason: Replaced by normalized patient resource model.
```

## Compatibility Matrix

Each release must define compatibility across:

- Backend version.
- Web version.
- Mobile version.
- Public API version.
- Integration connector version.
- Database migration version.

## Rule

No API may be removed without documented migration path.

# ADR-016: Catalog and Test Configuration as a Configuration Engine

## Status

Accepted

## Context

Nexora must support multiple laboratories, branches, countries, clinical specialties and report formats. Hardcoding studies, analytes, reference ranges, sample requirements or report layouts would make the product difficult to adapt and expensive to maintain.

## Decision

Nexora will treat catalogs, studies, analytes, reference ranges, patient preparation instructions, report schemas and related clinical configuration as governed, versioned configuration artifacts.

Published configuration versions are immutable. Orders and results must reference the published configuration version used at the time of the clinical transaction.

## Consequences

- Clinical configuration can evolve without code changes.
- Orders and results remain historically traceable.
- Configuration workflows require validation, approval and auditability.
- Backend, frontend and mobile must render behavior from metadata where appropriate.
- The system requires stronger validation and governance before publication.

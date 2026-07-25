# Capability Dependency Validator

## Purpose

Validate BCM-002 before an agent uses it to create implementation work.

## Inputs

- `01-product-definition/business-capabilities/bcm-001/business-capability-map.md`
- `01-product-definition/business-capabilities/bcm-002/capability-dependency-map.md`
- `02-domain-definition/domain-foundation/context-map/context-map.md`
- `02-domain-definition/domain-foundation/aggregates/aggregate-catalog.md`

## Required Checks

1. `artifact.id` must equal `BCM-002`.
2. `artifact.depends_on` must include `BCM-001`, `CTX-MAP-001` and `AGG-CATALOG-001`.
3. Every capability id in BCM-001 must appear exactly once in `capabilities`.
4. No capability id may appear in BCM-002 if it does not exist in BCM-001, except symbolic downstream markers documented as platform-wide markers.
5. Every capability must define:
   - `id`
   - `name`
   - `profile`
   - `mvp_phase`
   - `product_area`
   - `related_bounded_contexts`
   - `related_aggregates`
   - `downstream_dependencies`
   - `ai_opportunities`
6. Every `profile` must exist in `dependency_profiles`.
7. Every `mvp_phase` must be one of `MVP1`, `MVP2`, `MVP3`.
8. Every `product_area` must exist in `product_areas`.
9. Related aggregates should match aggregate names from AGG-CATALOG-001 when an aggregate exists.
10. Related bounded contexts should match known context names or documented cross-cutting placeholders.

## Platform-Wide Markers

The following downstream dependency markers are allowed because they describe broad platform dependency roles rather than single BCM-001 capabilities:

- `all-protected-capabilities`
- `all-runtime-capabilities`
- `all-regulated-capabilities`
- `public-api-consumers`
- `partner-integrations`

## Failure Handling

Validation failures must block implementation planning until resolved or documented through an ADR.

Agents must not silently ignore:

- Missing capability mappings.
- Unknown capability ids.
- Missing MVP phase.
- Unknown product area.
- Cross-context aggregate ownership violations.

## Suggested Output

The validator should report:

- Total BCM-001 capabilities.
- Total BCM-002 capabilities.
- Missing ids.
- Duplicate ids.
- Unknown dependency ids.
- Unknown profiles.
- Unknown product areas.
- Unknown aggregate references.
- Unknown bounded context references.

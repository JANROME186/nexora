# Nexora Shared Kernel

## Purpose

The Shared Kernel contains stable, minimal and reusable concepts shared across bounded contexts.

It does not contain business behavior.

## Allowed Concepts

- Identifiers.
- Money.
- Person names.
- Email and phone.
- Address.
- Date ranges.
- Audit metadata.
- Locale and timezone.
- External references.

## Forbidden Concepts

The following must never be moved to the Shared Kernel:

- Clinical validation rules.
- Billing fiscal behavior.
- Pricing logic.
- Result interpretation.
- Workflow orchestration.
- AI provider behavior.
- Persistence implementation details.

## Governance

Any new shared concept must prove that it is:

1. Used by at least three bounded contexts.
2. Stable across countries and deployment models.
3. Context-neutral.
4. Backward compatible.
5. Reviewed by the Architecture Domain Team.

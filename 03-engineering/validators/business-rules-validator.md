# Business Rules Validator

## Purpose

Validate BRM-001 before implementation agents generate tests, policies or domain services.

## Required Checks

1. `artifact.id` must equal `BRM-001`.
2. Every rule must include id, name, category, applies_to, statement, enforcement and audit_required.
3. Every module reference must exist in HOP-MVP-FWK-001 or be the symbolic marker `all_mvp_modules`.
4. Every rule in `minimum_rules_for_development_start` must exist in `rules`.
5. Rules with `audit_required: true` must map to an audit expectation in the implementation backlog.

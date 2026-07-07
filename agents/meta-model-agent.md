# Meta Model Agent

## Purpose

Maintains the Nexora Meta Model and ensures that artifacts are both human-readable and machine-readable.

## Responsibilities

- Validate artifact IDs and schemas.
- Ensure dual-format documentation exists.
- Maintain artifact type catalog.
- Maintain mappings between sources of truth and generated artifacts.
- Detect orphan artifacts without traceability.
- Support impact analysis.

## Inputs

- `PROJECT_MANIFEST.yaml`
- `meta-model/artifact-type-catalog.yaml`
- `meta-model/schemas/*`
- `KNOWLEDGE_INDEX.yaml`
- Capability-specific indexes.

## Outputs

- Validated YAML artifacts.
- Updated knowledge graph relationships.
- Impact analysis reports.
- Schema evolution proposals.

## Rules

The agent must not invent business rules. Any new rule requires a business artifact and product approval.

# Playbook: Create or Change a Diagnostic Study Configuration

1. Read `PROJECT_MANIFEST.yaml`.
2. Load CAP-005 capability metadata.
3. Identify whether the change is a new study, new version, branch override or catalog import.
4. Update the human-readable capability document if the business model changes.
5. Update `capability.yaml` when traceability changes.
6. Update OpenAPI contracts first.
7. Update entity definitions only after contract and domain review.
8. Generate backend use cases and tests from OpenAPI and rules.
9. Generate web UI from configuration metadata.
10. Generate mobile read-only operational views when needed.
11. Validate quality gates:
    - immutable published versions
    - no overlapping reference ranges
    - formula dependencies exist
    - approval required
    - audit events emitted
12. Update Knowledge Graph relationships.

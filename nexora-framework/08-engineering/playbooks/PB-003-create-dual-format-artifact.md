# PB-003 Create Dual-Format Artifact

## Objective

Create a Nexora artifact with both human-readable Markdown and machine-readable YAML.

## Steps

1. Read `PROJECT_MANIFEST.md`.
2. Read `meta-model/artifact-type-catalog.md`.
3. Select the artifact type and prefix.
4. Create the Markdown file using the corresponding template.
5. Create the YAML file using the corresponding schema.
6. Add relationships to upstream and downstream artifacts.
7. Update `KNOWLEDGE_INDEX.md` or the corresponding capability index.
8. Validate against JSON Schema.
9. Update CHANGELOG if the artifact changes product behavior.

## Definition of Done

- Markdown file exists.
- YAML file exists.
- Artifact has unique ID.
- Artifact has owner, status and version.
- Artifact has at least one traceability relation.
- Knowledge index is updated.

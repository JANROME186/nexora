# Business Requirement to YAML Prompt

## Purpose

Use this prompt to transform a human-written `BUSINESS_REQUIREMENT.md` into `BUSINESS_REQUIREMENT.yaml`.

The Markdown file remains the requester-supplied business source. The YAML file is a structured index for agents.

## Prompt

You are transforming a requester-supplied business requirement into a machine-readable YAML index for agentic analysis and implementation.

Input:

`projects/<project-slug>/BUSINESS_REQUIREMENT.md`

Output:

`projects/<project-slug>/BUSINESS_REQUIREMENT.yaml`

Rules:

1. Do not invent business facts.
2. Preserve the intent, language and constraints of the requester.
3. Normalize names, lists and sections into predictable YAML keys.
4. If information is missing, place it under `requires_clarification`.
5. Do not convert assumptions into facts.
6. Do not introduce agent-specific, cloud-specific, model-specific or provider-specific dependencies.
7. Keep Markdown as the human source and YAML as the structured agent index.
8. Include references to downstream artifacts only when they already exist or are explicitly requested.
9. Validate that the YAML parses.
10. If the Markdown source is missing, stop and request it.

Required YAML sections:

- `artifact`
- `input_governance`
- `project`
- `executive_summary`
- `business_context`
- `business_opportunity`
- `problem_statement`
- `user_need`
- `actors`
- `product_scope`
- `business_capability_map`
- `engineering_model`
- `mvp`
- `commercial_product`
- `marketplace`
- `open_data_ingestion`
- `business_rules`
- `data_privacy_audit`
- `integrations`
- `digital_channels`
- `ai`
- `constraints`
- `out_of_scope_initial`
- `success_criteria`
- `requires_clarification`
- `future_project_template`
- `downstream_source_artifacts`

If a section does not apply, include it with `applicable: false` and a short reason.

After generating the YAML, report:

- Whether YAML parsing passed.
- Which fields require clarification.
- Whether the output remains agent-agnostic.
- Whether `SOURCE_OF_TRUTH.yaml` should be updated.

## Generic Instruction to Give an Agent

Transform `projects/<project-slug>/BUSINESS_REQUIREMENT.md` into `BUSINESS_REQUIREMENT.yaml` using `projects/<project-slug>/04-requirements/prompts/business-requirement-to-yaml-prompt.yaml`. Do not invent missing business facts; place gaps under `requires_clarification`; validate YAML parsing; keep the result agent-agnostic.

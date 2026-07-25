# Business Requirement to YAML Prompt

## Purpose

Use this prompt to transform a human-written `BUSINESS_REQUIREMENT.md` into `BUSINESS_REQUIREMENT.md`.

The Markdown file remains the requester-supplied business source. The YAML file is a structured index for agents.

## Prompt

You are transforming a requester-supplied business requirement into a machine-readable YAML index for agentic analysis and implementation.

Input:

`projects/<project-slug>/BUSINESS_REQUIREMENT.md`

Output:

`projects/<project-slug>/BUSINESS_REQUIREMENT.md`

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
- Whether `SOURCE_OF_TRUTH.md` should be updated.

## Generic Instruction to Give an Agent

Transform `projects/<project-slug>/BUSINESS_REQUIREMENT.md` into `BUSINESS_REQUIREMENT.md` using `projects/<project-slug>/04-requirements/prompts/business-requirement-to-yaml-prompt.md`. Do not invent missing business facts; place gaps under `requires_clarification`; validate YAML parsing; keep the result agent-agnostic.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROMPT-BR-YAML-001
  type: prompt
  name: Business Requirement to YAML Prompt
  version: 1.0.0
  status: approved
  human_readable: business-requirement-to-yaml-prompt.md
  machine_readable: business-requirement-to-yaml-prompt.md
purpose: Transform a requester-supplied BUSINESS_REQUIREMENT.md into BUSINESS_REQUIREMENT.md
  for agentic analysis and implementation.
inputs:
  required:
  - projects/<project-slug>/BUSINESS_REQUIREMENT.md
  optional:
  - projects/<project-slug>/SOURCE_OF_TRUTH.md
  - projects/<project-slug>/PROJECT_STATE.md
  - projects/<project-slug>/PROJECT_BRIEF.md
outputs:
  required:
  - projects/<project-slug>/BUSINESS_REQUIREMENT.md
source_policy:
  markdown_is_human_source: true
  yaml_is_agent_index: true
  agent_may_invent_business_facts: false
  missing_markdown_behavior: stop_and_request_business_requirement
transformation_rules:
- Preserve requester intent.
- Do not invent missing business facts.
- Normalize sections into predictable YAML keys.
- Put missing information under requires_clarification.
- Do not convert assumptions into facts.
- Do not introduce agent-specific dependencies.
- Do not introduce cloud-specific dependencies unless explicitly stated.
- Do not introduce provider-specific dependencies unless explicitly stated.
- Keep downstream artifact references only when already present or explicitly requested.
- Validate YAML parsing before reporting completion.
required_yaml_sections:
- artifact
- input_governance
- project
- executive_summary
- business_context
- business_opportunity
- problem_statement
- user_need
- actors
- product_scope
- business_capability_map
- engineering_model
- mvp
- commercial_product
- marketplace
- open_data_ingestion
- business_rules
- data_privacy_audit
- integrations
- digital_channels
- ai
- constraints
- out_of_scope_initial
- success_criteria
- requires_clarification
- future_project_template
- downstream_source_artifacts
not_applicable_rule:
  format:
    applicable: false
    reason: Short business reason.
validation_report:
  must_include:
  - yaml_parsing_status
  - clarification_fields
  - agent_agnostic_status
  - source_of_truth_update_needed
generic_instruction: 'Transform projects/<project-slug>/BUSINESS_REQUIREMENT.md into
  BUSINESS_REQUIREMENT.md using this prompt. Do not invent missing business facts;
  place gaps under requires_clarification; validate YAML parsing; keep the result
  agent-agnostic.

  '
```

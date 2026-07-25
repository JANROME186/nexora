# Nexora Auxiliary Development Prompts

## Purpose

These prompts support development after the generic MVP development prompt has selected a project, module and backlog item.

They do not replace the main lifecycle prompts:

- `nexora-framework/05-prompts/prompts/generic-project-lifecycle-prompts.md`

If there is a conflict, the project source artifacts and the generic MVP development prompt win.

## Prompt Hierarchy

1. Project `BUSINESS_REQUIREMENT.md` and project source-of-truth files.
2. Target module package files.
3. Generic MVP development prompt.
4. Project-specific backlog execution playbook, when present.
5. Auxiliary development prompts.

## Available Auxiliary Prompts

- `AUX-DEV-001`: Module development kickoff.
- `AUX-DEV-002`: Implement selected backlog slice.
- `AUX-DEV-003`: Backend slice implementation.
- `AUX-DEV-004`: Web slice implementation.
- `AUX-DEV-005`: Mobile slice implementation.
- `AUX-DEV-006`: QA and module closeout.

The machine-readable source is:

```text
nexora-framework/05-prompts/prompts/auxiliary-development-prompts.md
```

## Minimal User Prompts

Module kickoff:

```text
Prepare development kickoff for <module-id> in projects/<project-slug>/ using the Nexora auxiliary development prompts.
```

Backlog slice:

```text
Implement backlog item <backlog-item-id> for <module-id> in projects/<project-slug>/ using the Nexora auxiliary development prompts.
```

QA and closeout:

```text
Validate and close <module-id> in projects/<project-slug>/ using the Nexora auxiliary development prompts.
```

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: NXF-PROMPTS-002
  type: auxiliary-development-prompts
  name: Nexora Auxiliary Development Prompts
  version: 1.0.0
  status: approved
  human_readable: auxiliary-development-prompts.md
  machine_readable: auxiliary-development-prompts.md
  owner: Nexora Engineering
purpose: Provide optional development prompts that support the generic MVP development
  prompt without overriding it.
prompt_hierarchy:
  primary_prompt: nexora-framework/05-prompts/prompts/generic-project-lifecycle-prompts.md#prompts.mvp_development
  auxiliary_role: These prompts refine execution for one development slice after the
    primary development prompt has selected the project, module and backlog item.
  precedence:
  - Project BUSINESS_REQUIREMENT.md and project source-of-truth files.
  - Target module package files.
  - Generic MVP development prompt.
  - Project-specific backlog execution playbook, when present.
  - This auxiliary prompt playbook.
  conflict_rule: If an auxiliary prompt conflicts with the generic development prompt
    or project source artifacts, the auxiliary prompt loses and the agent must report
    the conflict.
shared_loading_order:
- AGENT_BOOTSTRAP.md
- SOURCE_OF_TRUTH.md
- PROJECT_STATE.md
- nexora-framework/05-prompts/prompts/generic-project-lifecycle-prompts.md
- nexora-framework/05-prompts/prompts/auxiliary-development-prompts.md
- projects/<project-slug>/SOURCE_OF_TRUTH.md
- projects/<project-slug>/PROJECT_STATE.md
- projects/<project-slug>/ORDERED_DEVELOPMENT_GUIDE.md
- target module package files
shared_rules:
- Use only after the project has passed framework compliance validation.
- Do not modify BUSINESS_REQUIREMENT.md.
- Do not redesign product scope or MVP module boundaries.
- Implement only the selected backlog item or slice.
- Keep code under the project implementation boundary.
- Update tests, traceability and project state only after verified progress.
- Stop and report blockers when required definitions are missing or contradictory.
auxiliary_prompts:
  module_kickoff:
    id: AUX-DEV-001
    name: Module development kickoff
    purpose: Prepare the first implementation iteration for a selected module.
    prompt: 'Use the primary MVP development prompt first.


      Target project folder:

      projects/<project-slug>/


      Target module:

      <module-id>


      Task:

      Prepare the module development kickoff.


      Steps:

      1. Load PROJECT_STATE.md and SOURCE_OF_TRUTH.md from the target project.

      2. Confirm development_readiness.status is ready and blocking_definition_gaps
      is empty.

      3. Locate the target module package.

      4. Load module-definition.md, domain-model.md, api-contract.openapi.md,
      database-migration-plan.md, ui-screen-map.md, security-and-audit-rules.md,
      test-plan.md and traceability.md.

      5. If a project-specific backlog execution playbook exists, load it and use
      its backlog order.

      6. Identify the first pending backlog item.

      7. Create or update the implementation README with the module scope, local commands
      placeholder and progress tracking section.

      8. Do not implement business functionality yet unless the selected backlog item
      explicitly requires it.


      Finish by reporting the selected backlog item, implementation boundary, loaded
      files and blockers.

      '
  backlog_slice:
    id: AUX-DEV-002
    name: Implement selected backlog slice
    purpose: Implement one backlog item or module slice in isolation.
    prompt: 'Use the primary MVP development prompt first.


      Target project folder:

      projects/<project-slug>/


      Target module:

      <module-id>


      Backlog item:

      <backlog-item-id>


      Task:

      Implement only the selected backlog item.


      Steps:

      1. Load the target module package and any project-specific backlog execution
      playbook.

      2. Confirm the backlog item exists and is the first pending item unless the
      user explicitly selected a later item.

      3. Map the backlog item to domain model, API contract, database plan, UI map,
      security rules, test plan and traceability.

      4. Implement the smallest complete vertical slice required by the backlog item.

      5. Add or update tests required by test-plan.md.

      6. Run local validation commands when available.

      7. Update traceability and implementation README with completed work, commands
      and known gaps.

      8. Update PROJECT_STATE.md only if progress is verified.


      Finish by reporting files changed, validations run, next backlog item and blockers.

      '
  backend_slice:
    id: AUX-DEV-003
    name: Backend slice implementation
    purpose: Guide backend work while preserving module contracts and domain boundaries.
    prompt: 'Use the primary MVP development prompt first.


      Target project folder:

      projects/<project-slug>/


      Target module:

      <module-id>


      Backlog item:

      <backlog-item-id>


      Task:

      Implement the backend portion of the selected backlog item.


      Rules:

      - Follow module-definition.md for language, framework and architecture.

      - Follow domain-model.md for bounded contexts, aggregates, value objects and
      invariants.

      - Follow api-contract.openapi.md for API behavior.

      - Follow database-migration-plan.md for persistence.

      - Follow security-and-audit-rules.md for authorization and audit.

      - Do not invent endpoints, tables or domain concepts outside the module package.


      Required result:

      Backend code, migrations/configuration, tests and README updates needed for
      the selected slice.

      '
  web_slice:
    id: AUX-DEV-004
    name: Web slice implementation
    purpose: Guide web UI work while staying aligned with backend contracts and UI
      map.
    prompt: 'Use the primary MVP development prompt first.


      Target project folder:

      projects/<project-slug>/


      Target module:

      <module-id>


      Backlog item:

      <backlog-item-id>


      Task:

      Implement the web portion of the selected backlog item.


      Rules:

      - Follow ui-screen-map.md for screens, actors and UX requirements.

      - Follow api-contract.openapi.md for API integration.

      - Follow security-and-audit-rules.md for permission-sensitive UI behavior.

      - Add loading, error, success and permission-denied states where relevant.

      - Do not add marketing pages or unrelated screens.


      Required result:

      Web code, API client updates, tests and README updates needed for the selected
      slice.

      '
  mobile_slice:
    id: AUX-DEV-005
    name: Mobile slice implementation
    purpose: Guide mobile app work when the selected module includes a mobile foundation
      or mobile workflow.
    prompt: 'Use the primary MVP development prompt first.


      Target project folder:

      projects/<project-slug>/


      Target module:

      <module-id>


      Backlog item:

      <backlog-item-id>


      Task:

      Implement the mobile portion of the selected backlog item.


      Rules:

      - Implement only mobile functionality included in the module package.

      - Follow api-contract.openapi.md for backend communication.

      - Follow security-and-audit-rules.md for session, authorization and audit-sensitive
      flows.

      - Keep the app prepared for future modules without implementing future scope.


      Required result:

      Mobile code, basic tests and README updates needed for the selected slice.

      '
  qa_and_closeout:
    id: AUX-DEV-006
    name: QA and module closeout
    purpose: Validate implementation work and close a module only when evidence supports
      it.
    prompt: 'Use the primary MVP development prompt first.


      Target project folder:

      projects/<project-slug>/


      Target module:

      <module-id>


      Task:

      Validate the implemented module or backlog slice.


      Steps:

      1. Load test-plan.md and traceability.md.

      2. Confirm tests cover the implemented backend, web, mobile, contract, security
      and audit scope where applicable.

      3. Run available validation commands.

      4. Record QA evidence or summaries under the project QA boundary.

      5. Confirm BUSINESS_REQUIREMENT.md was not modified.

      6. Confirm no unnumbered project-root folders were created.

      7. Update PROJECT_STATE.md only when the module or slice is truly verified.


      If validation fails, do not close the module. Record blockers and the next corrective
      action.

      '
minimal_user_prompts:
  module_kickoff: Prepare development kickoff for <module-id> in projects/<project-slug>/
    using the Nexora auxiliary development prompts.
  backlog_slice: Implement backlog item <backlog-item-id> for <module-id> in projects/<project-slug>/
    using the Nexora auxiliary development prompts.
  qa_and_closeout: Validate and close <module-id> in projects/<project-slug>/ using
    the Nexora auxiliary development prompts.
```

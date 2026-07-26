---
artifact:
  id: HOP-PROMPT-RULES
  type: execution-rule-index
  status: active
  optimization: atomic_context
---

# Commercial Backlog Execution Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
rules:
- These prompts are operational instructions only; source artifacts remain authoritative.
- Agents must load YAML files before Markdown for legacy structured automation artifacts; agents must load compact Markdown
  handoffs first when a <TASK_ID>-summary.md exists.
- Agents must not continue if BUSINESS_REQUIREMENT.md is missing.
- Agents must not require a vendor-specific agent, runtime, prompt extension or configuration.
- Agents must prefer open source, self-hostable and standards-based technologies unless an ADR approves an exception.
- Agents must produce security quality evidence for every code-changing backlog item.
- Before feature implementation in any code-changing backlog item, agents must review 08-qa/technical-debt/technical-debt-index.md
  and resolve or materially reduce at least one open technical-debt item, unless no open debt exists.
- As HOP advances, agents must increase technical-debt burn-down intensity; late module, release and commercial-readiness
  iterations must reduce multiple relevant debt items when open debt remains.
- HOP cannot be marked commercially complete, GA-ready or finally closed while any technical debt item remains open.
- Agents must run stack-appropriate checks for best practices, coding standards, duplicate code, complexity, OWASP or equivalent
  secure coding, dependency vulnerabilities across all severities, secrets, coverage and message externalization/i18n.
- Agents must load 03-architecture/technology-architecture/local-toolchain-inventory.md before running build, test, quality,
  security or local runtime commands, and must use or update its tool paths instead of rediscovering them every iteration.
- Agents must use nexora-framework/08-engineering/agents/context-orchestrator/context_orchestrator.py before handing a backlog
  to any execution agent.
- Ollama with an approved open source model is the primary Nexora Framework prompt orchestration runtime; missing Ollama/model
  is a framework bootstrap blocker, not an optional skip.
- Agents must use lazy loading for continuation context: prefer the latest <TASK_ID>-summary.md handoff, then inspect only
    relevant lines with rg or targeted reads; do not paste complete YAML/MD files into prompts unless explicitly required.
- New task handoffs must be Markdown with minimal YAML frontmatter and must stay under 200 tokens where practical.
- New monolithic YAML task/state artifacts are discouraged; existing YAML remains supported until migrated through registered
  technical debt.
- If a required quality category applies to a changed stack but HOP lacks an executable script, plugin or tool configuration,
  the agent must create or update technical debt before closure; "if configured", "if scripts exist" and undocumented not-applicable
  dispositions are not valid closure evidence.
- Agents must target at least 80 percent line coverage for every applicable delivered stack. If a stack is below 80 percent
  during an intermediate iteration, the previous measured coverage is the hard lower bound and coverage must never decrease.
- If a changed stack remains below 80 percent line coverage, agents must target a 3 to 5 percentage point improvement in that
  iteration; smaller improvements require explicit justification, maximum meaningful in-scope tests and immediate coverage
  debt.
- Agents must enforce enterprise product foundations before continuing customer-facing portal/app work: es-MX/en-US localization,
    language switching, IAM permission mapping, dynamic menus/actions, login/session context, product database deliverables,
    UX/UI design baseline, code documentation, persistence architecture and OpenAPI/contract-first generation review.
- Agents must externalize new or changed user-facing text, validation copy, error prose, status labels, error codes and repeated
  magic values through message catalogs, constants, configuration or policy providers.
- Agents must not close or advance a backlog item with passed_with_execution_limitation, closed_with_execution_limitation,
  not_executed mandatory gates, blocked toolchains, unsupported runtimes or blocked dependency/audit endpoints. Manual source
  review is only a compensating control and cannot replace executable tests, build, coverage, audit or required backend validation
  gates.
- Agents must not claim done, finished, closed or ready for the next backlog until the verifiable HOP backlog closure audit
  passes and is recorded in evidence or handoff.
- A valid HOP closure requires YAML parse, stale-pointer sweep, evidence-state sweep, git diff --check, synchronized PROJECT_STATE.md/SOURCE_OF_TRUTH.md/prompt/runbook/
  traceability pointers, matching command-output metrics, commit hash and clean git status.
- Agents must execute capability packages in dependency order unless an explicit mock strategy is documented.
- Agents must not manually create repetitive CRUD, DTO, controller, repository, SDK, Swagger, documentation or test artifacts
  when they can be generated from models.
- Agents must update project registries and QA evidence before closing any backlog item.
```

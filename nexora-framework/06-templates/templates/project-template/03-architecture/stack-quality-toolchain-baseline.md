---
id: PROJECT-STACK-QUALITY-TOOLCHAIN
format: markdown_structured_payload
type: stack-quality-toolchain-baseline
version: 0.1.0
status: draft
---

# Project Stack Quality Toolchain

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: PROJECT-STACK-QUALITY-TOOLCHAIN
  type: stack-quality-toolchain-baseline
  version: 0.1.0
  status: draft
  standard: ../../nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md
purpose: Define applicable open source security and quality tools for the selected
  project stack.
selected_stack_reference: 03-architecture/client-stack-market-validation.md
toolchain_by_stack:
  java_maven:
    applicable: false
    tools:
    - category: IDE
      tool: SonarLint
      status: pending
    - category: Quality and bugs
      tool: SpotBugs
      status: pending
    - category: Code security
      tool: Find Security Bugs
      status: pending
    - category: Style
      tool: Checkstyle
      status: pending
    - category: Static analysis
      tool: PMD
      status: pending
    - category: Duplication
      tool: PMD CPD
      status: pending
    - category: Coverage
      tool: JaCoCo
      status: pending
    - category: Dependency vulnerabilities
      tool: OWASP Dependency-Check
      status: pending
    - category: Containers, filesystem and secrets
      tool: Trivy
      status: pending
    - category: SBOM
      tool: CycloneDX Maven Plugin
      status: pending
    - category: Build rules
      tool: Maven Enforcer
      status: pending
    - category: Licenses
      tool: License Maven Plugin
      status: pending
    - category: Advanced tests
      tool: PIT or Pitest
      status: pending
    - category: Architecture
      tool: ArchUnit
      status: pending
    - category: Refactor and technical debt
      tool: OpenRewrite
      status: pending
    - category: Additional SAST
      tool: Semgrep CE
      status: pending
quality_gates:
  backlog_item_gate: []
  module_closeout_gate: []
  release_gate: []
unavailable_or_deferred_tools: []
technical_debt_items_created_or_updated: []
decision:
  status: pending
  ready_for_backlog_execution: false
```

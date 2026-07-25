---
id: PROJECT_MANIFEST
format: markdown_structured_payload
---

# Project Manifest

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
repository:
  id: nexora
  name: Nexora
  type: multi-project-agent-ready-definition-repository
  version: 0.76.0
  status: ready
company:
  name: Nexora
  focus:
  - software_development
  - artificial_intelligence
  - ai_assisted_engineering
  - business_automation
  - saas_platforms
  - deployable_enterprise_software
framework:
  path: nexora-framework/
  purpose: Defines how Nexora documents and prepares AI-agent-assisted software solutions.
  usage_guide: NEXORA_FRAMEWORK_USAGE_GUIDE.md
  execution_sequence: nexora-framework/00-start-here/FRAMEWORK_EXECUTION_SEQUENCE.md
  vision:
    final_vision: nexora-framework/00-start-here/docs/vision/NEXORA_FINAL_VISION.md
    strategic_handoff: nexora-framework/00-start-here/docs/vision/NEXORA_STRATEGIC_HANDOFF.md
  standards:
    project_folder: nexora-framework/02-standards/standards/project-folder-standard.md
    documentation: nexora-framework/02-standards/standards/documentation-standard.md
    agent_agnostic: nexora-framework/02-standards/standards/agent-agnostic-standard.md
    model_driven_product_engineering: nexora-framework/02-standards/standards/model-driven-product-engineering-standard.md
    capability_package: nexora-framework/02-standards/standards/capability-package-standard.md
    open_data_ingestion: nexora-framework/02-standards/standards/open-data-ingestion-standard.md
    product_marketplace: nexora-framework/02-standards/standards/product-marketplace-standard.md
    business_requirement_versioning: nexora-framework/02-standards/standards/business-requirement-versioning-standard.md
    open_source_first_security_quality: nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md
    integrated_local_solution_runbook: nexora-framework/02-standards/standards/integrated-local-solution-runbook-standard.md
    framework_feedback_continuous_improvement: nexora-framework/02-standards/standards/framework-feedback-continuous-improvement-standard.md
    technology_debt_backlog_policy: nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md
    framework_improvement_backlog_policy: nexora-framework/02-standards/standards/framework-feedback-continuous-improvement-standard.md
  recipe:
    agent_to_mvp: nexora-framework/04-recipes/recipes/agent-to-mvp-recipe.md
  prompts:
    generic_project_lifecycle: nexora-framework/05-prompts/prompts/generic-project-lifecycle-prompts.md
    auxiliary_development: nexora-framework/05-prompts/prompts/auxiliary-development-prompts.md
    business_requirement_impact: nexora-framework/05-prompts/prompts/business-requirement-impact-prompts.md
    security_quality_gate: nexora-framework/05-prompts/prompts/security-quality-gate-prompts.md
    integrated_local_runbook: nexora-framework/05-prompts/prompts/integrated-local-runbook-prompts.md
    framework_feedback: nexora-framework/05-prompts/prompts/framework-feedback-prompts.md
  orchestration:
    project_analysis_to_mvp: nexora-framework/03-orchestration/project-orchestration/project-analysis-and-mvp-workflow.md
  governance:
    framework_improvement_backlog: nexora-framework/07-governance/framework-improvement-backlog/framework-improvement-backlog.md
  template:
    new_project: nexora-framework/06-templates/templates/project-template/
projects:
  healthcare_operations_platform:
    path: projects/healthcare-operations-platform/
    status: commercial_product_development_in_progress
    business_requirement: projects/healthcare-operations-platform/BUSINESS_REQUIREMENT.md
    business_requirement_yaml: projects/healthcare-operations-platform/BUSINESS_REQUIREMENT.md
    business_requirement_index: projects/healthcare-operations-platform/00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.md
    business_requirement_to_yaml_prompt: projects/healthcare-operations-platform/04-requirements/prompts/business-requirement-to-yaml-prompt.md
    project_brief: projects/healthcare-operations-platform/PROJECT_BRIEF.md
    project_brief_yaml: projects/healthcare-operations-platform/PROJECT_BRIEF.md
    ordered_development_guide: projects/healthcare-operations-platform/ORDERED_DEVELOPMENT_GUIDE.md
    ordered_development_guide_yaml: projects/healthcare-operations-platform/ORDERED_DEVELOPMENT_GUIDE.md
    mvp_backlog_execution_prompts: projects/healthcare-operations-platform/06-delivery/mvp/MVP_BACKLOG_EXECUTION_PROMPTS.md
    mvp_backlog_execution_prompts_yaml: projects/healthcare-operations-platform/06-delivery/mvp/MVP_BACKLOG_EXECUTION_PROMPTS.md
    mvp_development_readiness_decision: projects/healthcare-operations-platform/06-delivery/mvp/MVP_DEVELOPMENT_READINESS_DECISION.md
    commercial_product_backlog: projects/healthcare-operations-platform/06-delivery/commercial-product/HOP_COMMERCIAL_PRODUCT_BACKLOG.md
    commercial_backlog_execution_prompts: projects/healthcare-operations-platform/06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md
    capability_package_index: projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/capability-package-index.md
    open_data_ingestion_contract: projects/healthcare-operations-platform/05-contracts/import-export/open-data-ingestion/open-data-ingestion-contract.md
    open_data_ingestion_requirements: projects/healthcare-operations-platform/04-requirements/capabilities/bcm-plt-010-open-data-ingestion-and-migration/requirements.md
    product_marketplace_contract: projects/healthcare-operations-platform/05-contracts/marketplace/product-marketplace/product-marketplace-contract.md
    product_marketplace_requirements: projects/healthcare-operations-platform/04-requirements/capabilities/bcm-plt-011-product-marketplace-and-entitlements/requirements.md
    source_of_truth: projects/healthcare-operations-platform/SOURCE_OF_TRUTH.md
    technical_debt_index: projects/healthcare-operations-platform/08-qa/technical-debt/technical-debt-index.md
    framework_feedback_index: projects/healthcare-operations-platform/08-qa/framework-feedback/framework-feedback-index.md
    local_solution_runbook: projects/healthcare-operations-platform/09-operations/runbooks/local-solution-runbook.md
    local_solution_runbook_yaml: projects/healthcare-operations-platform/09-operations/runbooks/local-solution-runbook.md
    active_module: MVP-MOD-003
    completed_backlog_items:
    - PF-BE-001
    - PF-OPS-001
    - PF-BE-002
    - PF-BE-003
    - PF-BE-004
    - PF-FE-001
    - PF-APP-001
    - PF-QA-001
    - MVP-MOD-001-CLOSEOUT
    - MVP-MOD-002-DEF
    - MVP-MOD-002-BE-001
    - MVP-MOD-002-BE-002
    - MVP-MOD-002-FE-001
    - MVP-MOD-002-QA-001
    - MVP-MOD-002-CLOSEOUT
    active_backlog_item: MVP-MOD-003-DEF
agent_policy:
  agent_agnostic: true
  requester_supplied_business_requirement_required: true
  repository_loading_order:
  - AGENT_BOOTSTRAP.md
  - NEXORA_FRAMEWORK_USAGE_GUIDE.md
  - SOURCE_OF_TRUTH.md
  - nexora-framework/README.md
  - nexora-framework/02-standards/standards/agent-agnostic-standard.md
  - nexora-framework/02-standards/standards/model-driven-product-engineering-standard.md
  - nexora-framework/02-standards/standards/capability-package-standard.md
  - nexora-framework/02-standards/standards/product-marketplace-standard.md
  - nexora-framework/02-standards/standards/business-requirement-versioning-standard.md
  - nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md
  - nexora-framework/02-standards/standards/integrated-local-solution-runbook-standard.md
  - nexora-framework/02-standards/standards/framework-feedback-continuous-improvement-standard.md
  - nexora-framework/03-orchestration/project-orchestration/project-analysis-and-mvp-workflow.md
  - nexora-framework/04-recipes/recipes/agent-to-mvp-recipe.md
  - nexora-framework/05-prompts/prompts/business-requirement-impact-prompts.md
  - nexora-framework/05-prompts/prompts/security-quality-gate-prompts.md
  - nexora-framework/05-prompts/prompts/integrated-local-runbook-prompts.md
  - nexora-framework/05-prompts/prompts/framework-feedback-prompts.md
  - target_project/00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.md
  - target_project/BUSINESS_REQUIREMENT.md
  - target_project/SOURCE_OF_TRUTH.md
  - target_project/PROJECT_BRIEF.md
```

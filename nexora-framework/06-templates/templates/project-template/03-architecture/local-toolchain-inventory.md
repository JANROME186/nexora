# Project Local Toolchain Inventory

This file documents the local paths, versions and generic commands for the tools required to work on
this project.

Agents must load `local-toolchain-inventory.md` before running build, test, quality, security or
local runtime commands. Keep this file concise: it is not a command history.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: PROJECT-LOCAL-TOOLCHAIN-001
  type: local-toolchain-inventory
  name: Project Local Toolchain Inventory
  version: 0.1.0
  status: draft
  standard: ../../nexora-framework/02-standards/standards/local-toolchain-inventory-standard.md
  human_readable: local-toolchain-inventory.md
  machine_readable: local-toolchain-inventory.md
project:
  name: TBD
  slug: TBD
repository:
  local_path: TBD
  git_remote: TBD
  default_branch: TBD
operating_system:
  family: TBD
  shell: TBD
environment_variables:
  required:
  - name: JAVA_HOME
    value_or_resolution: TBD
    required_for:
    - backend_java_maven
tools: []
command_templates:
  repository:
    status: git status --short
    recent_commits: git log -5 --oneline
  yaml_validation:
    parse_project_yaml: python -c "from pathlib import Path; import yaml; [yaml.safe_load(p.read_text(encoding='utf-8'))
      for p in Path('.').rglob('*.yaml') if not any(part in {'.git','node_modules','target','dist','build'}
      for part in p.parts)]; print('YAML OK')"
maintenance_policy:
  update_required_when:
  - selected_stack_changes
  - required_tool_path_changes
  - tool_version_changes_materially
  - new_quality_gate_tool_is_added
  not_a_command_history: true
validation:
  last_verified_date: TBD
  verified_by_role: TBD
  notes: []
```

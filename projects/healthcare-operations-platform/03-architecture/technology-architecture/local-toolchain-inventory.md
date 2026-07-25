# HOP Local Toolchain Inventory

Context orchestration is now a required Nexora Framework capability through Ollama and the Python helper:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/context_orchestrator.py `
  --root C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora `
  --task-id <TASK_ID> `
  --title "<TITLE>"
```

Ollama is the primary open-source local orchestrator. The bootstrap default required model is
`qwen2.5-coder:0.5b`.

Install or refresh the required model with:

```powershell
ollama pull qwen2.5-coder:0.5b
```

The Python helper renders the final prompt from canonical fields and reuses a cache keyed by the
canonical context hash, so repeated executions produce the same prompt while the active backlog
context does not change.

**Machine-readable source:** `local-toolchain-inventory.md`
**Status:** Active
**Last verified:** 2026-07-24

This inventory records where the main local development tools are installed on this workstation and
which generic commands agents should use before running HOP backlog work.

Agents must load this inventory before code-changing backlog execution, quality gates, local runtime
validation or module closeout. It is not a command history; backlog evidence remains under `08-qa/`.

## Repository

- Repo: `C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora`
- Project: `C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform`
- Implementation: `C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/07-implementation`

## Tools

| Tool | Version | Path |
|---|---:|---|
| Java | 21.0.7 LTS | `C:/Program Files/Java/jdk-21/bin/java.exe` |
| Maven | 3.9.11 | `C:/ProgramData/chocolatey/lib/maven/apache-maven-3.9.11/bin/mvn.cmd` |
| Node.js | 24.8.0 | `C:/Program Files/nodejs/node.exe` |
| npm | 11.6.0 | `C:/Program Files/nodejs/npm.ps1` |
| Git | 2.51.0.windows.1 | `C:/Program Files/Git/cmd/git.exe` |
| Docker | 29.6.1 | `C:/Program Files/Docker/Docker/resources/bin/docker.exe` |
| Trivy | 0.72.0 | `C:/ProgramData/chocolatey/bin/trivy.exe` |
| OWASP ZAP | stable | Docker image `ghcr.io/zaproxy/zaproxy:stable` (`zap-baseline.py`, `zap-api-scan.py`); no local install |
| ripgrep | 15.1.0 | Resolve with `Get-Command rg`; do not depend on named-agent bundled paths |
| Python | 3.13.7 | `C:/Python313/python.exe` |
| Python package PyYAML | 6.0.3 | Required for deterministic frontmatter migration |
| Python package ollama | 0.6.2 | Required for local SLLM narrative migration |
| Ollama | 0.32.3 | `C:/Users/Administrator/AppData/Local/Programs/Ollama/ollama.exe` |
| Ollama bootstrap model | qwen2.5-coder:0.5b | local model digest `4ff64a7f502a08b7616edb8ca0a79eb1853fc363d842b7df4b46915d11a3fb09` |

The reproducible prompt generated for `COM-MOD-017-BE-001` is stored at
`08-qa/generated-prompts/COM-MOD-017-BE-001-prompt.md` with SHA256
`62adf7b643aff753adde833d97b62c85c5426c46902cad0d6e3ebe33e8adaf60`.

Frontmatter migration tooling is available through
`nexora-framework/08-engineering/agents/context-orchestrator/frontmatter_migrator.py`. The current
HOP inventory report is stored at
`08-qa/format-migration/frontmatter-migration-report-projects-healthcare-operations-platform.md`;
the framework inventory report is stored at
`08-qa/format-migration/frontmatter-migration-report-nexora-framework.md`.

## Generic Commands

Backend:

```powershell
mvn --settings .mvn/settings.xml test
mvn --settings .mvn/settings.xml -Pquality clean verify
mvn --settings .mvn/settings.xml -Pquality -Dhop.local-db-tests=true clean verify
```

Frontend/app TypeScript stacks:

```powershell
npm install
npm run typecheck
npm run quality
npm run test:coverage
npm run build
npm audit --audit-level=low
```

Repository closure:

```powershell
git status --short
git diff --check
git log -5 --oneline
```

Local infrastructure:

```powershell
docker compose -f compose.local.json up -d
docker compose -f compose.local.json ps
docker compose -f compose.local.json down
```

## Maintenance

Update this inventory when tool paths change, versions materially change, a required quality gate
tool is added, or the local runbook introduces a new prerequisite. If a required tool is missing and
cannot be made available, register or update technical debt before closing the backlog.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-LOCAL-TOOLCHAIN-001
  type: local-toolchain-inventory
  name: HOP Local Toolchain Inventory
  version: 1.0.0
  status: active
  standard: ../../../../nexora-framework/02-standards/standards/local-toolchain-inventory-standard.md
  human_readable: local-toolchain-inventory.md
  machine_readable: local-toolchain-inventory.md
  last_verified_date: 2026-07-24
  owner: Nexora Engineering
project:
  name: Healthcare Operations Platform
  slug: healthcare-operations-platform
repository:
  local_path: C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora
  project_path: C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform
  implementation_path: C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/07-implementation
  default_branch: master
  status_command: git status --short
operating_system:
  family: Windows
  shell: PowerShell
  path_separator: /
  notes:
  - Paths are recorded with forward slashes for YAML portability.
  - Agents may execute the same commands from PowerShell unless a component README
    states otherwise.
environment_variables:
  required:
  - name: JAVA_HOME
    value_or_resolution: C:/Program Files/Java/jdk-21
    required_for:
    - backend_java_maven
  - name: PATH
    value_or_resolution: Must include Java, Maven, Node.js, Git, Docker and scanner
      tool locations or use absolute executable paths from this inventory.
    required_for:
    - all_stacks
  project_local:
  - name: OWASP_DEPENDENCY_CHECK_DATA_DIRECTORY
    value_or_resolution: C:/Documents/Proyectos/Laboratorio/dependency-check-data
    required_for:
    - backend_dependency_vulnerability_scan
    refresh_policy: manual_once_per_day_by_project_operator_or_security_reviewer
tools:
- id: java
  category: runtime
  executable_name: java
  path: C:/Program Files/Java/jdk-21/bin/java.exe
  home_path: C:/Program Files/Java/jdk-21
  version: 21.0.7 LTS
  required_for:
  - backend_java_maven
  detection_command: Get-Command java
  version_command: java -version
  generic_commands:
  - java -version
  status: available
- id: maven
  category: build_tool
  executable_name: mvn
  path: C:/ProgramData/chocolatey/lib/maven/apache-maven-3.9.11/bin/mvn.cmd
  home_path: C:/ProgramData/chocolatey/lib/maven/apache-maven-3.9.11
  version: 3.9.11
  required_for:
  - backend_java_maven
  - backend_quality_profile
  detection_command: Get-Command mvn
  version_command: mvn -version
  settings_files:
  - projects/healthcare-operations-platform/07-implementation/backend/.mvn/settings.xml
  - projects/healthcare-operations-platform/07-implementation/backend/.mvn/global-settings.xml
  generic_commands:
  - mvn --settings .mvn/settings.xml test
  - mvn --settings .mvn/settings.xml -Pquality clean verify
  - mvn --settings .mvn/settings.xml -Pquality -Dhop.local-db-tests=true clean verify
  - mvn --settings .mvn/settings.xml -Pquality org.owasp:dependency-check-maven:check
  status: available
- id: node
  category: runtime
  executable_name: node
  path: C:/Program Files/nodejs/node.exe
  version: 24.8.0
  required_for:
  - typescript_web
  - mobile_typescript_foundation
  detection_command: Get-Command node
  version_command: node --version
  generic_commands:
  - node --version
  status: available
- id: npm
  category: package_manager
  executable_name: npm
  path: C:/Program Files/nodejs/npm.ps1
  version: 11.6.0
  required_for:
  - typescript_web
  - mobile_typescript_foundation
  detection_command: Get-Command npm
  version_command: npm --version
  generic_commands:
  - npm install
  - npm run typecheck
  - npm run test:coverage
  - npm run build
  - npm run quality
  - npm audit --audit-level=low
  status: available
- id: git
  category: source_control
  executable_name: git
  path: C:/Program Files/Git/cmd/git.exe
  version: 2.51.0.windows.1
  required_for:
  - repository_management
  - backlog_closure
  detection_command: Get-Command git
  version_command: git --version
  generic_commands:
  - git status --short
  - git log -5 --oneline
  - git diff --check
  - git add <changed-files>
  - git commit -m "<type(scope): summary>"
  status: available
- id: docker
  category: container_runtime
  executable_name: docker
  path: C:/Program Files/Docker/Docker/resources/bin/docker.exe
  version: 29.6.1
  required_for:
  - local_database
  - integrated_local_runtime
  - container_scans_when_images_exist
  detection_command: Get-Command docker
  version_command: docker --version
  generic_commands:
  - docker --version
  - docker compose -f 07-implementation/compose.local.json up -d
  - docker compose -f 07-implementation/compose.local.json ps
  - docker compose -f 07-implementation/compose.local.json down
  status: available
- id: trivy
  category: vulnerability_secret_misconfiguration_scanner
  executable_name: trivy
  path: C:/ProgramData/chocolatey/bin/trivy.exe
  version: 0.72.0
  required_for:
  - filesystem_vulnerability_scan
  - secrets_scan
  - misconfiguration_scan
  detection_command: Get-Command trivy
  version_command: trivy --version
  generic_commands:
  - trivy --version
  - trivy fs --scanners vuln,secret,misconfig --severity UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL
    --exit-code 1 --timeout 10m .
  status: available
- id: owasp-zap
  category: dast_scanner
  executable_name: docker run ghcr.io/zaproxy/zaproxy:stable
  path: Docker image ghcr.io/zaproxy/zaproxy:stable (zap-baseline.py, zap-api-scan.py);
    no local install, pulled/run via Docker on demand
  version: stable (tracks the ghcr.io/zaproxy/zaproxy:stable tag)
  required_for:
  - backend_api_dast_scan
  - employee_portal_and_public_website_baseline_dast_scan
  detection_command: docker image inspect ghcr.io/zaproxy/zaproxy:stable
  version_command: docker run --rm ghcr.io/zaproxy/zaproxy:stable zap-baseline.py --version
  generic_commands:
  - docker run --rm --add-host=host.docker.internal:host-gateway -v <repo>/<evidence-dir>:/zap/wrk
    ghcr.io/zaproxy/zaproxy:stable zap-api-scan.py -t http://host.docker.internal:<backend-port>/v3/api-docs
  - docker run --rm --add-host=host.docker.internal:host-gateway -v <repo>/<evidence-dir>:/zap/wrk
    ghcr.io/zaproxy/zaproxy:stable zap-baseline.py -t http://host.docker.internal:<frontend-port>
  status: available
  proven_usage:
  - HOP-QA-ALIGN-004
  - COM-MOD-012-QA-001 (353 backend URLs)
  - COM-MOD-013-QA-001 (939 backend URLs, 125 employee-portal URLs)
- id: ripgrep
  category: search_tool
  executable_name: rg
  path: resolved_from_current_shell_with_Get-Command_rg
  version: 15.1.0
  required_for:
  - repository_search
  - stale_pointer_sweep
  - agent_agnostic_scan
  - secrets_scan_patterns
  detection_command: Get-Command rg
  version_command: rg --version
  generic_commands:
  - rg --version
  - rg -n "<pattern>" <paths>
  - rg --files
  status: optional_available_in_current_shell
  notes:
  - Do not depend on a named-agent bundled path for this tool. If unavailable, use
    any installed ripgrep executable or register missing search-tool debt only when
    required scans cannot run.
- id: python
  category: scripting_runtime
  executable_name: python
  path: C:/Python313/python.exe
  version: 3.13.7
  required_for:
  - yaml_parse_validation
  - local_automation_helpers
  detection_command: Get-Command python
  version_command: python --version
  generic_commands:
  - python --version
  - python -m pip install -r nexora-framework/08-engineering/agents/context-orchestrator/requirements.txt
  - python -c "from pathlib import Path; import yaml; [yaml.safe_load(p.read_text(encoding='utf-8'))
    for p in Path('.').rglob('*.yaml') if not any(part in {'.git','node_modules','target','dist','build'}
    for part in p.parts)]; print('YAML OK')"
  status: available
  installed_packages:
    PyYAML: 6.0.3
    ollama: 0.6.2
  notes:
  - PyYAML is required for deterministic frontmatter migration.
  - The official Python ollama package is required for local SLLM narrative migration.
- id: ollama
  category: required_local_llm_runtime
  executable_name: ollama
  path: C:/Users/Administrator/AppData/Local/Programs/Ollama/ollama.exe
  version: 0.32.3
  required_for:
  - framework_prompt_orchestration
  - backlog_prompt_generation
  - local_context_routing
  detection_command: Get-Command ollama or Test-Path C:/Users/Administrator/AppData/Local/Programs/Ollama/ollama.exe
  version_command: ollama --version
  generic_commands:
  - ollama --version
  - ollama list
  - ollama pull qwen2.5-coder:0.5b
  - ollama run qwen2.5-coder:0.5b
  status: required
  notes:
  - Ollama is the primary open-source local prompt orchestrator for Nexora Framework.
  - qwen2.5-coder:0.5b is the bootstrap default required model for deterministic backlog
    prompt orchestration.
  - Missing Ollama or missing approved model means framework bootstrap is incomplete.
command_templates:
  backend_java_maven:
    working_directory: projects/healthcare-operations-platform/07-implementation/backend
    version_check:
    - java -version
    - mvn -version
    unit_tests: mvn --settings .mvn/settings.xml test
    quality_verify: mvn --settings .mvn/settings.xml -Pquality clean verify
    local_database_quality_verify: mvn --settings .mvn/settings.xml -Pquality -Dhop.local-db-tests=true
      clean verify
    dependency_check: mvn --settings .mvn/settings.xml -Pquality org.owasp:dependency-check-maven:check
  employee_portal:
    working_directory: projects/healthcare-operations-platform/07-implementation/employee-portal
    version_check:
    - node --version
    - npm --version
    install: npm install
    typecheck: npm run typecheck
    quality: npm run quality
    coverage: npm run test:coverage
    build: npm run build
    audit: npm audit --audit-level=low
  patient_portal:
    working_directory: projects/healthcare-operations-platform/07-implementation/patient-portal
    version_check:
    - node --version
    - npm --version
    quality: npm run quality
    coverage: npm run test:coverage
    build: npm run build
  doctor_portal:
    working_directory: projects/healthcare-operations-platform/07-implementation/doctor-portal
    version_check:
    - node --version
    - npm --version
    quality: npm run quality
    coverage: npm run test:coverage
    build: npm run build
  public_website:
    working_directory: projects/healthcare-operations-platform/07-implementation/public-website
    version_check:
    - node --version
    - npm --version
    quality: npm run quality
    coverage: npm run test:coverage
    build: npm run build
  mobile_typescript_foundation:
    working_directory: projects/healthcare-operations-platform/07-implementation/mobile-app
    version_check:
    - node --version
    - npm --version
    quality: npm run quality
    coverage: npm run test:coverage
  repository:
    working_directory: C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora
    status: git status --short
    recent_commits: git log -5 --oneline
    whitespace_check: git diff --check
  local_runtime:
    working_directory: projects/healthcare-operations-platform/07-implementation
    start_infrastructure: docker compose -f compose.local.json up -d
    status_infrastructure: docker compose -f compose.local.json ps
    stop_infrastructure: docker compose -f compose.local.json down
  yaml_validation:
    working_directory: C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora
    parse_project_yaml: python -c "from pathlib import Path; import yaml; [yaml.safe_load(p.read_text(encoding='utf-8'))
      for p in Path('projects/healthcare-operations-platform').rglob('*.yaml') if
      not any(part in {'.git','node_modules','target','dist','build'} for part in
      p.parts)]; print('HOP YAML OK')"
  context_orchestration:
    working_directory: C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora
    generate_compact_prompt: python nexora-framework/08-engineering/agents/context-orchestrator/context_orchestrator.py
      --root C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora --task-id <TASK_ID>
      --title "<TITLE>"
    required_ollama_model: qwen2.5-coder:0.5b
maintenance_policy:
  update_required_when:
  - selected_stack_changes
  - local_tool_path_changes
  - tool_version_changes_materially
  - required_quality_gate_tool_is_added
  - runbook_prerequisite_changes
  not_a_command_history: true
  agents_must_verify_paths_before_use: true
  missing_required_tool_disposition: register_or_update_technical_debt_before_closure
validation:
  last_verified_date: 2026-07-24
  verified_commands:
  - Get-Command java; java -version
  - Get-Command mvn; mvn -version
  - Get-Command node; node --version
  - Get-Command npm; npm --version
  - Get-Command git; git --version
  - Get-Command docker; docker --version
  - Get-Command trivy; trivy --version
  - Get-Command rg; rg --version
  - Get-Command python; python --version
  - python -c "import importlib.metadata as m; print(m.version('ollama'))"
  - python nexora-framework/08-engineering/agents/context-orchestrator/frontmatter_migrator.py
    --root C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora --scope projects/healthcare-operations-platform
    --limit 20
  - Test-Path C:/Users/Administrator/AppData/Local/Programs/Ollama/ollama.exe
  - C:/Users/Administrator/AppData/Local/Programs/Ollama/ollama.exe --version
  - C:/Users/Administrator/AppData/Local/Programs/Ollama/ollama.exe list
  - python nexora-framework/08-engineering/agents/context-orchestrator/context_orchestrator.py
    --root C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora --refresh
  - Get-FileHash projects/healthcare-operations-platform/08-qa/generated-prompts/COM-MOD-017-BE-001-prompt.md
    -Algorithm SHA256
  notes:
  - Inventory records generic commands only; backlog execution evidence remains under
    08-qa.
  - qwen2.5-coder:0.5b is installed locally and validated as the mandatory bootstrap
    model.
  - COM-MOD-017-BE-001 generated prompt SHA256 is 62adf7b643aff753adde833d97b62c85c5426c46902cad0d6e3ebe33e8adaf60.
  - Frontmatter migration pilot inventory completed with 20 candidates, 20 planned,
    0 written, 0 errors.
```

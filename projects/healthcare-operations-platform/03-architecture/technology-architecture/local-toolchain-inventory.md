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

**Machine-readable source:** `local-toolchain-inventory.yaml`  
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
pilot inventory report is stored at `08-qa/format-migration/frontmatter-migration-report.yaml`.

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
docker compose -f compose.local.yml up -d
docker compose -f compose.local.yml ps
docker compose -f compose.local.yml down
```

## Maintenance

Update this inventory when tool paths change, versions materially change, a required quality gate
tool is added, or the local runbook introduces a new prerequisite. If a required tool is missing and
cannot be made available, register or update technical debt before closing the backlog.

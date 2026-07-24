# HOP Local Toolchain Inventory

Context orchestration is now supported through the framework Python helper:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/context_orchestrator.py `
  --root C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora `
  --task-id <TASK_ID> `
  --title "<TITLE>"
```

Ollama is optional. When present, it may compress or route context locally with models such as
`llama3.2` or `qwen2.5-coder`. When absent, agents must use the deterministic Python fallback and
continue without creating a vendor-specific dependency.

**Machine-readable source:** `local-toolchain-inventory.yaml`  
**Status:** Active  
**Last verified:** 2026-07-23

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

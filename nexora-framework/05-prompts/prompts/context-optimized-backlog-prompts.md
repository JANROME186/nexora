# Context Optimized Backlog Prompts

Use this playbook when preparing a backlog prompt for a commercial execution agent.

The commercial prompt should contain pointers and commands, not full source files. Prefer the latest
`<TASK_ID>-summary.md` handoff, define `ROOT` once, and instruct the agent to inspect only the
active backlog lines.

```text
# TASK: [ID_TAREA] - [TITULO]
ROOT: [RUTA_BASE]

## 1. Alcance / Objetivos Directos
- Ejecuta solo esta tarea.
- Usa lazy loading; no leas archivos completos salvo necesidad explícita.
- Mantén ejecución agent-agnostic y open-source-first.

## 2. Contexto Inmediato (Punteros)
- Handoff: [ruta a <TASK_ID>-summary.md previo si existe]
- Estado activo: usa `rg -n "active_backlog_item|current_backlog_item|next_backlog_item" PROJECT_STATE.md projects/[project]/PROJECT_STATE.md`.
- Prompt operativo: inspecciona solo el bloque del backlog activo.

## 3. Entregables
- Cambios del backlog.
- Evidencia QA/security si aplica.
- Registros y punteros sincronizados.
- `[TASK_ID]-summary.md` con Status, Cambios Clave, Deuda Técnica Creada y Siguiente Paso.

## 4. Criterios de Cierre
- Gates obligatorios ejecutados o bloqueo formal sin avanzar punteros.
- Validaciones de formato y punteros limpias.
- Commit Conventional Commit y `git status --short` limpio si no hay bloqueantes.
```

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: NXF-PROMPT-CTX-001
  type: prompt-playbook
  name: Context Optimized Backlog Prompt Playbook
  version: 1.0.0
  status: approved
  human_readable: context-optimized-backlog-prompts.md
  machine_readable: context-optimized-backlog-prompts.md
  standard: ../../02-standards/standards/context-efficient-execution-standard.md
rules:
- Define ROOT once.
- Prefer the latest <TASK_ID>-summary.md handoff.
- Use rg/read commands for targeted context instead of pasting full files.
- Keep the commercial-agent prompt around 300 to 600 tokens when practical.
- Preserve mandatory quality gates and closure audits.
- Create a compact summary handoff at closure.
prompt_template:
  format: markdown
  content: '# TASK: [ID_TAREA] - [TITULO]

    ROOT: [RUTA_BASE]


    ## 1. Alcance / Objetivos Directos

    - Ejecuta solo esta tarea.

    - Usa lazy loading; no leas archivos completos salvo necesidad explícita.

    - Mantén ejecución agent-agnostic y open-source-first.


    ## 2. Contexto Inmediato (Punteros)

    - Handoff: [ruta a <TASK_ID>-summary.md previo si existe]

    - Estado activo: usa `rg -n "active_backlog_item|current_backlog_item|next_backlog_item"
    PROJECT_STATE.md projects/[project]/PROJECT_STATE.md`.

    - Prompt operativo: inspecciona solo el bloque del backlog activo.


    ## 3. Entregables

    - Cambios del backlog.

    - Evidencia QA/security si aplica.

    - Registros y punteros sincronizados.

    - `[TASK_ID]-summary.md` con Status, Cambios Clave, Deuda Técnica Creada y Siguiente
    Paso.


    ## 4. Criterios de Cierre

    - Gates obligatorios ejecutados o bloqueo formal sin avanzar punteros.

    - Validaciones de formato y punteros limpias.

    - Commit Conventional Commit y `git status --short` limpio si no hay bloqueantes.

    '
```

# Nexora Architecture Refactoring (NAR)

NAR es el proceso de refactorización arquitectónica de la especificación. Su objetivo es evitar deuda documental, duplicidad de fuentes de verdad y conceptos obsoletos que puedan confundir a personas o agentes.

## Objetivos

1. Consolidar fuentes de verdad.
2. Normalizar vocabulario.
3. Clasificar artefactos como SOURCE, GENERATED, DERIVED, DEPRECATED o ARCHIVED.
4. Sustituir conceptos específicos de proveedor por abstracciones agnósticas.
5. Preparar el repositorio para el Nexora Domain Model.

## Política de eliminación

Nada se elimina directamente. El ciclo correcto es:

```mermaid
flowchart LR
A[Active] --> B[Deprecated]
B --> C[Archived]
C --> D[Removed]
```

Un artefacto puede eliminarse solo si:

- Tiene reemplazo documentado o justificación de eliminación.
- No es referenciado por el Knowledge Graph.
- No es fuente de verdad de otro artefacto.
- Pasó por revisión de Specification Governance.

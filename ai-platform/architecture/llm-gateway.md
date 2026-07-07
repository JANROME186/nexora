# LLM Gateway Architecture

El LLM Gateway es la puerta única para consumo de modelos generativos.

## Responsabilidades

- Aplicar políticas de privacidad.
- Resolver proveedor/modelo según tenant, licencia, país y caso de uso.
- Registrar auditoría y costo.
- Aplicar guardrails.
- Ejecutar fallback.
- Estandarizar respuestas.

## Flujo

```mermaid
flowchart LR
  App[Application Use Case] --> Port[AI Capability Port]
  Port --> Gateway[LLM Gateway]
  Gateway --> Policy[Policy & Privacy Filter]
  Policy --> Provider[Provider Adapter]
  Provider --> Model[AI Model]
  Model --> Guardrails[Output Guardrails]
  Guardrails --> Audit[Audit & Cost Log]
  Audit --> App
```

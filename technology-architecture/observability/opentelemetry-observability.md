---
id: TECH-OBS-001
name: OpenTelemetry Observability Architecture
version: 0.18.0
status: Draft
owner: Platform Engineering
---

# OpenTelemetry Observability Architecture

Nexora usará OpenTelemetry como estándar de observabilidad para evitar dependencia de herramientas específicas.

## Señales obligatorias

- Logs estructurados.
- Métricas.
- Trazas distribuidas.
- Correlation ID por request.
- Tenant ID en metadatos observables, sin exponer datos sensibles.

## Backends compatibles

- Jaeger / Tempo para trazas.
- Prometheus para métricas.
- Grafana para visualización.
- Loki para logs.
- Datadog / New Relic mediante exportadores.

## Reglas

- No registrar datos clínicos sensibles en logs.
- No registrar contraseñas, tokens o secretos.
- Cada API debe generar correlation ID.
- Cada evento asíncrono debe preservar trace context cuando sea posible.
- Cada servicio debe exponer `/health` o equivalente.

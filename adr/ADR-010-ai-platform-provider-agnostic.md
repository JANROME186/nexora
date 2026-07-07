# ADR-010: AI Platform Provider Agnostic

## Estado

Aceptado

## Contexto

Nexora incorporará IA en múltiples procesos administrativos, operativos y clínicos. El mercado de IA cambia rápidamente, por lo que depender de un proveedor específico crearía riesgo técnico, económico y estratégico.

## Decisión

Todas las capacidades de IA deberán invocarse mediante puertos, gateways y adaptadores. Ningún dominio ni caso de uso podrá depender directamente de un SDK o API concreta de un proveedor.

## Consecuencias

- Se podrá cambiar de proveedor sin reescribir lógica de negocio.
- Se podrán combinar modelos cloud y modelos locales.
- Se incrementa la complejidad inicial por la capa de abstracción.
- Se mejora la portabilidad on-premise/cloud.

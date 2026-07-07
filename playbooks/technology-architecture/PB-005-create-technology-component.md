---
id: PB-005
name: Create Technology Component
version: 0.18.0
status: Draft
---

# Playbook: Create Technology Component

## Propósito

Definir un nuevo componente tecnológico sin romper los principios de portabilidad y agnosticismo de Nexora.

## Pasos

1. Leer `CONSTITUTION.md`.
2. Leer `technology-architecture/technology-architecture.yaml`.
3. Identificar si el componente pertenece a identidad, almacenamiento, mensajería, observabilidad, gateway, compute, seguridad o datos.
4. Definir la abstracción antes que la implementación concreta.
5. Documentar al menos una implementación local.
6. Documentar al menos una implementación enterprise/cloud.
7. Verificar que el dominio no importe SDKs del proveedor.
8. Actualizar el mapa de fuentes de verdad si aplica.
9. Crear o actualizar ADR.
10. Actualizar `PROJECT_STATE.yaml` y `CHANGELOG.md`.

## Criterios de aceptación

- El componente puede ejecutarse localmente.
- El componente puede reemplazarse por otro proveedor.
- La configuración no contiene secretos hardcoded.
- Existen health checks u observabilidad cuando aplique.
- Existe documentación humana y representación YAML si es un artefacto principal.

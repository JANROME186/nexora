# Technology Architecture

Este volumen define la arquitectura tecnológica de Nexora bajo los principios **Anywhere First**, **Cloud Agnostic**, **Platform Agnostic**, **Compute Agnostic**, **Container Native** y **Local Development First**.

La tecnología debe permitir que Nexora pueda ejecutarse en:

- Una laptop de desarrollo con Docker Compose.
- Un servidor local/on-premise.
- Docker Swarm para instalaciones medianas.
- Kubernetes para despliegues enterprise.
- Cualquier nube pública o privada sin acoplar la lógica de negocio al proveedor.

## Artefactos principales

| Artefacto | Propósito |
|---|---|
| `technology-architecture.md` | Descripción humana de la arquitectura tecnológica. |
| `technology-architecture.yaml` | Modelo computable para agentes y validadores. |
| `deployment-profiles/deployment-profiles.md` | Perfiles Local, Team, Enterprise, SaaS y On-Premise. |
| `runtime/container-runtime.md` | Estándar de ejecución con contenedores OCI. |
| `storage/storage-abstraction.md` | Abstracción de almacenamiento. |
| `observability/opentelemetry-observability.md` | Observabilidad portable basada en OpenTelemetry. |
| `local-development/local-development.md` | Ambiente local reproducible. |
| `local-toolchain-inventory.yaml` | Inventario local de rutas, versiones y comandos base para herramientas de desarrollo y validación. |

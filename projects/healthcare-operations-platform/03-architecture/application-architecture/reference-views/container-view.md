# Application Container View

**ID:** APP-C4-001
**Estado:** Draft
**Versión:** 0.17.0

```mermaid
flowchart TD
    Web[Next.js Web Apps]
    Mobile[React Native Apps]
    PublicAPI[Public API / API Gateway]
    BFF[BFF Layer]
    Core[Nexora Core Application]
    Workflow[Workflow Engine]
    AI[AI Service Layer]
    Integration[Integration Gateway]
    DB[(PostgreSQL)]
    DocDB[(Document DB)]
    Cache[(Redis)]
    Object[(Object Storage)]
    EventBus[(Event Bus)]
    Observability[OpenTelemetry]

    Web --> BFF
    Mobile --> BFF
    BFF --> PublicAPI
    PublicAPI --> Core
    Core --> DB
    Core --> DocDB
    Core --> Cache
    Core --> Object
    Core --> EventBus
    Core --> Workflow
    Core --> AI
    EventBus --> Integration
    Core --> Observability
    BFF --> Observability
    Integration --> Observability
```

## Nota

Esta vista describe contenedores lógicos. No obliga a una tecnología o proveedor específico. Cada contenedor puede mapearse a Docker Compose, Docker Swarm, Kubernetes, serverless functions o despliegue on-premise según el perfil de instalación.

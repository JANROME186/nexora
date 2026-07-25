# BCM-PLT-001: Identity and Access Management

Este paquete de capacidad de negocio define el modelado de identidad, autenticación, autorización y contexto de sesión para todos los canales de la plataforma HOP (Employee Portal, Patient Portal, Doctor Portal y Mobile App).

## Propósito
El propósito es gobernar el acceso seguro y la autorización basada en roles (RBAC) y atributos (ABAC), asegurando que todas las operaciones cumplan con los principios de mínimo privilegio y auditoría inmutable.

## Contenido del Paquete
- `capability-package.md`: Metadatos del paquete.
- `business-model.md`: Modelo del agregado `UserAccount` (AGG-004) y objetos de valor asociados.
- `business-rules.md`: Reglas de negocio críticas para control de acceso, bloqueos, sesiones y asistencia técnica.
- `processes.md`: Flujos de login, logout y asistencia de soporte.
- `events.md`: Eventos de dominio inmutables como `UserAuthenticated` y `SupportSessionAssisted`.
- `openapi-source.md`: Definición de endpoints de autenticación y asistencia.
- `permissions.md`: Catálogo de permisos gruesos y finos para pantallas y operaciones.
- `ui-model.md` / `mobile-model.md`: Esquemas y componentes de interfaz para portales y móvil.
- `traceability.md`: Matriz de trazabilidad a requerimientos, HRP, reglas y pruebas.

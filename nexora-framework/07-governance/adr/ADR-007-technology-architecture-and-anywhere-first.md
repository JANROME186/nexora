# ADR-007: Technology Architecture and Anywhere First

## Estado

Accepted

## Contexto

Nexora debe poder ejecutarse desde ambientes locales de desarrollo hasta instalaciones on-premise, Docker Swarm, Kubernetes y nubes públicas. La plataforma no debe quedar anclada a un proveedor ni a un modelo de cómputo único.

## Decisión

Adoptamos una arquitectura tecnológica basada en:

- Anywhere First.
- Cloud Agnostic.
- Platform Agnostic.
- Compute Agnostic.
- Local Development First.
- Container Native.
- Open Standards First.

El empaquetado estándar será mediante contenedores OCI. Los perfiles iniciales de despliegue serán Local, Team, On-Premise, Enterprise Kubernetes y SaaS Cloud.

## Consecuencias

- Toda dependencia de infraestructura debe estar detrás de puertos/adaptadores.
- No se permite lógica de negocio dependiente de SDKs cloud.
- Se deben mantener alternativas locales para desarrollo.
- Serverless se considera una opción de cómputo, no una obligación universal.
- Kubernetes será una opción enterprise, no requisito para desarrollo local.

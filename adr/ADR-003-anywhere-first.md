# ADR-003: Anywhere First

## Estado

Approved

## Contexto

Nexora debe poder operar en laboratorios pequeños, cadenas regionales, ambientes on-premise, VPS, cloud, Docker, Docker Swarm y Kubernetes.

## Decisión

La arquitectura será cloud agnostic, platform agnostic y compute agnostic. Se priorizarán contenedores OCI, abstracciones de infraestructura y desarrollo local con Docker Compose.

## Consecuencias

- Evita lock-in de proveedor.
- Facilita adopción en mercados diversos.
- Obliga a separar dominio de infraestructura.

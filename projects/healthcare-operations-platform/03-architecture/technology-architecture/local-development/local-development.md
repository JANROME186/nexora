---
id: TECH-LOCAL-001
name: Local Development Environment
version: 0.18.0
status: Draft
owner: Engineering
---

# Local Development Environment

El ambiente local debe permitir desarrollar Nexora en una computadora convencional sin depender de cloud.

## Objetivos

- Clonar el repositorio.
- Levantar servicios con Docker Compose.
- Ejecutar pruebas locales.
- Probar APIs desde OpenAPI.
- Ejecutar frontend y mobile contra servicios locales.

## Servicios locales mínimos

```text
PostgreSQL
Redis-compatible cache
MinIO object storage
Mail catcher
API service
Web application
Worker service
```

## Servicios opcionales

```text
RabbitMQ/NATS
OpenTelemetry collector
Jaeger
Prometheus
Grafana
Keycloak
```

## Comandos objetivo

```bash
git clone <repo>
cd nexora-platform
cp .env.example .env
docker compose up -d
```

## Regla

Ninguna funcionalidad crítica del MVP debe requerir credenciales cloud para desarrollarse localmente.

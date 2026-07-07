---
id: TECH-DEPLOY-001
name: Nexora Deployment Profiles
version: 0.18.0
status: Draft
owner: Platform Engineering
---

# Deployment Profiles

Nexora soporta perfiles de despliegue para evitar que el producto dependa de una sola infraestructura.

## 1. Local Profile

Diseñado para desarrollo en computadoras convencionales.

Características:

- Docker Compose.
- Servicios mínimos locales.
- Base de datos local.
- Object storage local con MinIO.
- Mail catcher local.
- Observabilidad opcional.
- Sin dependencia de cloud.

## 2. Team Profile

Diseñado para equipos pequeños o ambientes internos compartidos.

Características:

- Docker Compose extendido o Docker Swarm.
- Base de datos persistente.
- Backups programados.
- Reverse proxy.
- TLS interno o externo.

## 3. On-Premise Profile

Diseñado para laboratorios que requieren instalación en su propia infraestructura.

Características:

- Docker Swarm o Kubernetes.
- Integración con red local.
- Backups locales.
- Modo offline parcial cuando aplique.
- Conectores para equipos LIS/RIS locales.

## 4. Enterprise Kubernetes Profile

Diseñado para cadenas grandes o SaaS privado.

Características:

- Kubernetes.
- Helm charts.
- Ingress controller.
- Horizontal scaling.
- Observabilidad completa.
- Secret management.
- Network policies.

## 5. SaaS Cloud Profile

Diseñado para operación multi-tenant administrada por Nexora.

Características:

- Kubernetes administrado o infraestructura equivalente.
- Servicios administrados opcionales.
- Multi-tenant isolation.
- Backups y disaster recovery.
- Autoscaling.
- Observabilidad completa.

## Regla de portabilidad

Toda diferencia entre perfiles debe resolverse en la capa de configuración e infraestructura, no en la lógica del negocio.

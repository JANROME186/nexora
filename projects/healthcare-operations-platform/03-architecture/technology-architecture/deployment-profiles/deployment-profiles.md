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

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: TECH-DEPLOY-001
artifact_type: deployment_profiles
version: 0.18.0
profiles:
  local:
    orchestrator: docker_compose
    requires_cloud: false
    target: developer_laptop
    required_services:
    - api
    - web
    - postgresql
    - redis
    - object_storage
    - mail_catcher
  team:
    orchestrator: docker_compose_or_swarm
    requires_cloud: false
    target: shared_server
    required_services:
    - api
    - web
    - workers
    - postgresql
    - redis
    - object_storage
    - reverse_proxy
  on_premise:
    orchestrator: docker_swarm_or_kubernetes
    requires_cloud: false
    target: customer_datacenter
    required_services:
    - api
    - web
    - workers
    - postgresql
    - redis
    - object_storage
    - messaging
    - observability
  enterprise_kubernetes:
    orchestrator: kubernetes
    requires_cloud: false
    target: enterprise_cluster
    required_services:
    - api
    - web
    - workers
    - postgresql
    - redis
    - object_storage
    - messaging
    - observability
    - ingress
  saas_cloud:
    orchestrator: kubernetes_or_equivalent
    requires_cloud: optional
    target: managed_saas
    required_services:
    - api
    - web
    - workers
    - managed_database
    - managed_cache
    - managed_object_storage
    - observability
portability_rule: environment_differences_must_be_configuration_or_infrastructure_only
```

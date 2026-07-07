# Bounded Context Catalog

El catálogo de Bounded Contexts define los límites oficiales del dominio de Nexora. Su objetivo es evitar duplicidad de reglas, modelos y lenguaje entre capacidades.

## Contextos principales

| ID | Contexto | Tipo | Propósito |
|---|---|---|---|
| BC-CLINICAL | Clinical Operations | Core | Ciclo diagnóstico clínico. |
| BC-ORGANIZATION | Organization & Tenant Management | Core | Laboratorios, sucursales y empleados. |
| BC-IAM | Identity & Access Management | Supporting | Usuarios, roles y permisos. |
| BC-MEDICAL-NETWORK | Medical Network | Supporting | Médicos internos, externos y referidores. |
| BC-CATALOGS | Catalog & Test Configuration | Core | Estudios, pruebas y valores de referencia. |
| BC-REVENUE | Revenue Operations | Supporting | Caja, ventas, pagos y facturación. |
| BC-SUPPLY | Supply & Inventory | Supporting | Inventario, proveedores y compras. |
| BC-INTEGRATION | Integration & Interoperability | Platform | APIs, conectores, estándares y equipos. |
| BC-MIGRATION | Data Migration & Portability | Platform | Ingesta, exportación y migración de datos. |
| BC-AI | AI Platform | Platform | IA, agentes, proveedores y guardrails. |
| BC-WORKFLOW | Workflow & Automation | Platform | Orquestación y automatización. |
| BC-AUDIT | Audit & Compliance | Platform | Auditoría, evidencia y cumplimiento. |

## Regla central

Un Aggregate Root solo puede ser propiedad de un Bounded Context. Los demás contextos deben integrarse mediante eventos, contratos publicados, queries autorizadas o Anti-Corruption Layers.

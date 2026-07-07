# Master Data Management

## Objetivo

Definir los datos maestros de Nexora y las reglas para evitar duplicidad, inconsistencia y pérdida de trazabilidad.

## Datos maestros iniciales

| ID | Master Data | Dominio | Dueño funcional |
|---|---|---|---|
| MDM-001 | Patient | Patient Management | Operaciones clínicas |
| MDM-002 | Physician | Physician Management | Relaciones médicas |
| MDM-003 | Laboratory | Organization Management | Administración SaaS |
| MDM-004 | Branch | Branch Management | Operaciones |
| MDM-005 | Clinical Test | Test Configuration | Dirección técnica |
| MDM-006 | Supplier | Inventory & Procurement | Compras |
| MDM-007 | User | Identity & Access | Seguridad |
| MDM-008 | Employee | Human Operations | Administración |

## Reglas MDM

- Cada dato maestro debe tener identificador global interno.
- Cada dato maestro debe pertenecer a un tenant cuando aplique.
- La duplicidad debe detectarse mediante reglas configurables.
- La fusión de registros debe quedar auditada.
- Las fuentes externas deben mapearse sin reemplazar la identidad interna.

## Ejemplo: Patient

Un paciente puede registrarse desde recepción, portal, app móvil, API, campaña o importación. Todos esos canales deben resolver hacia una identidad única de paciente dentro del tenant.

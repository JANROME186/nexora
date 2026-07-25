# Application Services Map

**ID:** APP-SVC-001
**Estado:** Draft
**Versión:** 0.17.0

## Propósito

Definir los servicios de aplicación iniciales que orquestan casos de uso del MVP.

| Servicio | Capacidad | Responsabilidad |
|---|---|---|
| IdentityAccessApplicationService | IAM | Login, usuarios, roles, permisos, sesiones y políticas de acceso. |
| LaboratoryOrganizationApplicationService | Organization | Laboratorios, sucursales, organigrama y configuración. |
| PatientApplicationService | Patient Management | Registro, búsqueda, actualización, documentos y consentimientos. |
| DoctorApplicationService | Doctor Management | Médicos, especialidades, referidos y portal médico. |
| CatalogApplicationService | Catalogs | Catálogos globales, país, sucursal y laboratorio. |
| OrderApplicationService | Orders | Creación de órdenes, estudios, precios, estatus y workflow. |
| SampleCollectionApplicationService | Sample Collection | Toma de muestra, etiquetas, rechazo, recepción y trazabilidad. |
| ResultApplicationService | Results | Captura, validación, firma, publicación y entrega de resultados. |
| CashierApplicationService | Cashier | Ventas, pagos, cancelaciones, cortes y conciliación. |
| BillingApplicationService | Billing | Facturación, notas, impuestos y country packs fiscales. |
| NotificationApplicationService | Notifications | WhatsApp, email, SMS, push y plantillas multilenguaje. |
| AuditApplicationService | Audit | Auditoría funcional y técnica. |

## Regla

Los Application Services no contienen lógica de dominio compleja. Orquestan dominios, permisos, transacciones, eventos e infraestructura.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: APP-SVC-001
name: Application Services Map
version: 0.17.0
status: Draft
services:
- id: AS-IAM-001
  name: IdentityAccessApplicationService
  capability: IAM
  responsibilities:
  - login
  - users
  - roles
  - permissions
  - sessions
  - accessPolicies
- id: AS-ORG-001
  name: LaboratoryOrganizationApplicationService
  capability: OrganizationManagement
  responsibilities:
  - laboratories
  - branches
  - hierarchy
  - configuration
- id: AS-PAT-001
  name: PatientApplicationService
  capability: PatientManagement
  responsibilities:
  - registration
  - search
  - update
  - documents
  - consents
- id: AS-DOC-001
  name: DoctorApplicationService
  capability: DoctorManagement
  responsibilities:
  - doctorRegistry
  - specialties
  - referrals
  - doctorPortal
- id: AS-CAT-001
  name: CatalogApplicationService
  capability: CatalogManagement
  responsibilities:
  - globalCatalogs
  - countryCatalogs
  - tenantCatalogs
- id: AS-ORD-001
  name: OrderApplicationService
  capability: OrderManagement
  responsibilities:
  - createOrder
  - studies
  - pricing
  - statuses
  - workflow
- id: AS-SMP-001
  name: SampleCollectionApplicationService
  capability: SampleCollection
  responsibilities:
  - collection
  - labels
  - rejection
  - reception
  - traceability
- id: AS-RES-001
  name: ResultApplicationService
  capability: ResultManagement
  responsibilities:
  - capture
  - validation
  - signature
  - publication
  - delivery
- id: AS-CASH-001
  name: CashierApplicationService
  capability: CashierManagement
  responsibilities:
  - sales
  - payments
  - cancellations
  - cashClose
  - reconciliation
- id: AS-BILL-001
  name: BillingApplicationService
  capability: BillingManagement
  responsibilities:
  - invoices
  - creditNotes
  - taxes
  - countryPacks
- id: AS-NOT-001
  name: NotificationApplicationService
  capability: NotificationManagement
  responsibilities:
  - whatsapp
  - email
  - sms
  - push
  - templates
- id: AS-AUD-001
  name: AuditApplicationService
  capability: AuditManagement
  responsibilities:
  - functionalAudit
  - technicalAudit
  - traceability
```

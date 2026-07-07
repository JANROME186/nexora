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

# Enterprise Data Architecture Baseline

## Propósito

La arquitectura de datos de Nexora establece cómo se define, clasifica, protege, almacena, intercambia, audita y explota la información dentro de la plataforma.

Nexora administra información clínica, administrativa, financiera, operativa, regulatoria y analítica. Por ello, el diseño de datos debe permitir operar desde un laboratorio pequeño hasta una red multiempresa y multisucursal, sin perder trazabilidad ni cumplimiento.

## Categorías principales de información

| Categoría | Descripción | Ejemplos |
|---|---|---|
| Master Data | Datos estables y reutilizables | Paciente, médico, laboratorio, sucursal, proveedor, estudio |
| Reference Data | Catálogos controlados | País, estado, sexo, especialidad, unidad de medida |
| Transaction Data | Datos generados por operación | Orden, pago, factura, muestra, resultado |
| Operational Data | Datos de ejecución | Turnos, estados de proceso, colas, tareas |
| Audit Data | Evidencia de cambios y accesos | Bitácora, firma, trazabilidad, acceso a expediente |
| Analytical Data | Datos para BI/IA | KPIs, agregados, modelos predictivos |
| Document Data | Documentos y archivos | PDF de resultados, consentimientos, identificaciones |
| Imaging Data | Estudios de imagen | DICOM, reportes radiológicos, PACS metadata |

## Fuentes de verdad

| Tipo | Fuente de verdad |
|---|---|
| Conceptos de negocio | Business Architecture |
| Reglas | Business Rules |
| Entidades | Domain Model + Data Architecture |
| Contratos de APIs | OpenAPI |
| Esquemas físicos | Database Migrations |
| Documentos clínicos | Document Management + Audit |
| Imágenes | PACS/DICOM Metadata + Object Storage |

## Principio clave

El modelo físico de base de datos no gobierna el producto. El producto es gobernado por capacidades, procesos, dominios, reglas y contratos. La base de datos es una implementación derivada.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: DATA-ARCH-001
name: Enterprise Data Architecture Baseline
version: 0.16.0
status: Draft
owner: Data Architecture
principles:
- Information is an enterprise asset
- Business model before physical schema
- Privacy and security by design
- Auditability for clinical and financial data
- Master data governance
- Country-aware retention policies
data_categories:
  master_data:
    examples:
    - Patient
    - Physician
    - Laboratory
    - Branch
    - Supplier
    - ClinicalTest
  reference_data:
    examples:
    - Country
    - State
    - Gender
    - Specialty
    - UnitOfMeasure
  transaction_data:
    examples:
    - Order
    - Payment
    - Invoice
    - Sample
    - Result
  operational_data:
    examples:
    - WorkflowTask
    - QueueItem
    - AppointmentSlot
  audit_data:
    examples:
    - AuditLog
    - AccessLog
    - SignatureLog
  analytical_data:
    examples:
    - KPI
    - Aggregation
    - Prediction
  document_data:
    examples:
    - ResultPDF
    - ConsentForm
    - IdentificationDocument
  imaging_data:
    examples:
    - DICOMStudy
    - ImagingReport
    - PACSMetadata
source_of_truth:
  business_concepts: business-architecture
  business_rules: business-rules
  entities: domain-model-and-data-architecture
  api_contracts: openapi
  physical_schema: database-migrations
  imaging: pacs-dicom-metadata
relationships:
  depends_on:
  - NMM-001
  - CAP-001
  informs:
  - database-model
  - data-retention-policy
  - analytics-model
  - ai-data-policy
```

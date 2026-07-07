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

# Data Classification Model

## Objetivo

Definir una clasificación estándar para todos los datos administrados por Nexora.

## Clasificación por tipo

| Tipo | Definición | Ejemplos |
|---|---|---|
| Master | Identidades y objetos estables | Paciente, médico, proveedor |
| Reference | Catálogos comunes | Sexo, país, método de pago |
| Transaction | Eventos de negocio | Orden, factura, resultado |
| Document | Archivos adjuntos | PDF, consentimiento, identificación |
| Imaging | Información de imagenología | DICOM, metadata PACS |
| Audit | Evidencia de operación | Bitácora, accesos, cambios |
| Configuration | Configuración por tenant | permisos, plantillas, flujos |
| Analytical | Información derivada | métricas, dashboards, predicciones |

## Clasificación por sensibilidad

| Nivel | Descripción | Ejemplos |
|---|---|---|
| Public | Información pública | Nombre comercial del laboratorio |
| Internal | Información interna no sensible | Configuración operativa general |
| Confidential | Información administrativa sensible | precios, proveedores, usuarios |
| Sensitive Personal | Datos personales | nombre, fecha de nacimiento, contacto |
| Clinical Sensitive | Datos clínicos | resultados, diagnósticos, estudios |
| Financial Sensitive | Datos fiscales/financieros | facturas, pagos, RFC, impuestos |
| Security Critical | Credenciales y secretos | tokens, claves, certificados |

## Reglas generales

- Todo dato debe tener clasificación antes de implementarse.
- Los datos clínicos y financieros deben ser auditables.
- Los datos sensibles no deben exponerse en logs.
- Los datos usados por IA deben pasar por políticas explícitas de privacidad.
- Los datos exportados deben respetar permisos, país y tenant.

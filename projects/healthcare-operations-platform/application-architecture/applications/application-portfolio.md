# Nexora Application Portfolio

**ID:** APP-PORT-001  
**Estado:** Draft  
**Versión:** 0.17.0

## Objetivo

Definir el portafolio inicial de aplicaciones que conforman el ecosistema Nexora.

## Aplicaciones por audiencia

### Público general
- Nexora Public Website.
- Cotizador público.
- Solicitud de cita.
- Acceso a portales.

### Empleados del laboratorio
- Nexora Admin Portal.
- Recepción.
- Caja.
- Toma de muestra.
- Laboratorio.
- Resultados.
- Inventario.
- Facturación.
- Administración.

### Pacientes
- Patient Portal Web.
- Patient Mobile App.
- Resultados.
- Citas.
- Facturas.
- Notificaciones.

### Médicos
- Doctor Portal Web.
- Doctor Mobile App.
- Pacientes referidos.
- Resultados.
- Alertas críticas.

### Sistemas externos
- Public API.
- Integration Gateway.
- Webhooks.
- ASTM/HL7/FHIR/DICOM.

## Criterios de diseño

Cada aplicación debe:

- Consumir contratos de APIs versionados.
- Respetar permisos y roles centralizados.
- Soportar internacionalización.
- Usar componentes del design system.
- Soportar degradación progresiva cuando el dispositivo o navegador tenga capacidades limitadas.
- Emitir trazas y métricas mínimas.

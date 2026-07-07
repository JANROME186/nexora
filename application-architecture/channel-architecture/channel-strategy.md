# Channel Architecture Strategy

**ID:** APP-CH-001  
**Estado:** Approved  
**Versión:** 0.17.0

## Principio

Nexora debe soportar múltiples canales sin duplicar lógica de negocio.

## Canales iniciales

| Canal | Aplicaciones | Característica principal |
|---|---|---|
| Public | Website | Bajo peso, SEO, captación. |
| Employee | Admin Portal | Operación completa y permisos finos. |
| Patient | Portal + Mobile | Autogestión, resultados, citas y notificaciones. |
| Doctor | Portal + Mobile | Consulta clínica y seguimiento. |
| Partner | Public API | Integraciones de terceros. |
| Device/System | Integration Gateway | Equipos, HIS, PACS, ERP, facturación y webhooks. |

## Reglas

- El canal no define el dominio.
- La lógica vive en Application Services y Domain Layer.
- Los canales pueden usar BFFs si requieren respuestas optimizadas.
- Mobile debe poder operar con conectividad intermitente para funciones seleccionadas.
- Web debe mantener compatibilidad con navegadores comerciales comunes sin depender de características experimentales.
- IA debe ser una mejora progresiva, no una dependencia obligatoria para operar.

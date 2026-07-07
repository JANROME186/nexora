# ADR-006 - Application Architecture as Enterprise Layer

**Estado:** Approved  
**Fecha:** 2026-07-07  
**Versión:** 0.17.0

## Contexto

Nexora no debe organizarse únicamente alrededor de pantallas o servicios técnicos. La plataforma debe soportar múltiples canales, aplicaciones, integraciones y despliegues sin duplicar lógica de negocio.

## Decisión

Se adopta una capa formal de **Application Architecture** que define aplicaciones, canales, servicios de aplicación, flujos, APIs, eventos e integración con capacidades de negocio.

## Consecuencias

- Las aplicaciones quedan separadas de los dominios.
- Los canales consumen capacidades compartidas.
- Los servicios de aplicación orquestan casos de uso, pero no implementan reglas de dominio complejas.
- La arquitectura facilita web, mobile, API pública, portales e integraciones sin duplicar reglas.
- Los agentes deben consultar la Application Architecture antes de generar servicios, BFFs o aplicaciones.

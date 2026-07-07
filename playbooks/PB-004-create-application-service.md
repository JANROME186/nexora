# PB-004 - Create Application Service

**ID:** PB-004  
**Estado:** Draft  
**Versión:** 0.17.0

## Objetivo

Crear o modificar un servicio de aplicación sin romper DDD, contratos OpenAPI ni trazabilidad.

## Pasos

1. Leer `PROJECT_MANIFEST.yaml`.
2. Identificar la capacidad de negocio relacionada.
3. Identificar dominio DDD y agregados afectados.
4. Validar historias de usuario y reglas de negocio.
5. Validar o crear contrato OpenAPI.
6. Registrar servicio en `application-architecture/services/application-services-map.yaml`.
7. Definir casos de uso y comandos/queries.
8. Definir eventos publicados o consumidos.
9. Definir permisos requeridos.
10. Definir pruebas contractuales y de aplicación.
11. Actualizar Knowledge Graph.

## Criterios de aceptación

- El servicio no contiene lógica de dominio compleja.
- El servicio no accede directamente desde UI sin pasar por API/BFF.
- El servicio no depende de un proveedor cloud concreto.
- El servicio tiene trazabilidad hacia capacidad, reglas, historias, API y pruebas.

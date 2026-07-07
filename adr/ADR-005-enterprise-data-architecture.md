# ADR-005: Enterprise Data Architecture and Information Lifecycle

## Estado

Accepted

## Contexto

Nexora manejará datos clínicos, financieros, administrativos, operativos, analíticos, documentos e imagenología. Un diseño basado únicamente en tablas o CRUD no es suficiente para un producto SaaS multiempresa, multisucursal, global y preparado para IA.

## Decisión

Nexora adoptará una Arquitectura Empresarial de Datos donde la información se modela antes que las entidades físicas. Cada dato relevante deberá clasificarse por tipo, sensibilidad, propietario, ciclo de vida y reglas de retención.

## Consecuencias

- El modelo ER será derivado de modelos de información, dominios y capacidades.
- Los datos sensibles deberán clasificarse desde el diseño.
- Las políticas de privacidad, retención y auditoría serán parte de la especificación.
- Los agentes de IA deberán consultar la arquitectura de datos antes de generar entidades, APIs o modelos analíticos.

## Alternativas consideradas

- Diseñar directamente tablas por módulo.
- Dejar privacidad y retención para fases posteriores.
- Usar únicamente convenciones ORM.

Estas alternativas fueron rechazadas porque generan deuda técnica, duplicidad y riesgo regulatorio.

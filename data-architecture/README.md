# Data Architecture

Este volumen define la arquitectura de datos e información de Nexora.

La arquitectura de datos responde a preguntas como:

- ¿Qué información administra Nexora?
- ¿Qué datos son maestros, transaccionales, de referencia, auditables o analíticos?
- ¿Quién es dueño de cada dato?
- ¿Cuál es su ciclo de vida?
- ¿Qué datos son sensibles?
- ¿Cuánto tiempo deben conservarse?
- ¿Qué datos pueden anonimizarse?
- ¿Qué datos alimentan IA, BI, auditoría, APIs y procesos operativos?

## Principios

1. La información es un activo empresarial.
2. El modelo de datos deriva del negocio, no de pantallas CRUD.
3. Todo dato sensible debe clasificarse desde el diseño.
4. Todo dato clínico debe ser trazable y auditable.
5. Los datos maestros deben evitar duplicidad.
6. Las reglas de retención deben configurarse por país, laboratorio y tipo de dato.
7. Los agentes de IA solo podrán usar datos permitidos por política.

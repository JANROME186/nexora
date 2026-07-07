# Nexora Constitution

La Constitución de Nexora define los principios no negociables del producto, la empresa y la ingeniería. Todo artefacto, decisión, módulo, API, modelo de datos, agente o despliegue debe alinearse con este documento.

## 1. Business First

Nexora se diseña desde procesos reales de diagnóstico clínico, no desde pantallas ni tablas.

## 2. Process Driven

Todo módulo debe derivarse de procesos de negocio, value streams, journeys, reglas y eventos.

## 3. Specification Driven Development

Ningún desarrollo inicia sin especificación mínima aprobada: proceso, capacidad, reglas, historias, contrato, dominio y criterios de aceptación.

## 4. API Contract First

OpenAPI/Swagger es la fuente de verdad para APIs. Todo cambio debe versionarse, revisarse y migrarse de forma controlada.

## 5. Domain Driven Design

La lógica de negocio vive en el dominio. La infraestructura, frameworks y proveedores son detalles reemplazables.

## 6. Agent Agnostic

Nexora no depende de ningún agente, asistente, plataforma o runtime específico. Los agentes consumen manifiestos, playbooks y artefactos neutrales.

## 7. Anywhere First

Nexora debe poder ejecutarse en local, on-premise, VPS, Docker, Docker Swarm, Kubernetes o cualquier nube sin cambiar la lógica de negocio.

## 8. Compute Agnostic

Un dominio puede desplegarse como monolito modular, microservicio o función serverless según el contexto operativo y financiero.

## 9. Infrastructure Abstraction

El dominio no conoce S3, SQS, Cognito, CloudWatch ni proveedores concretos. Conoce capacidades: almacenamiento, mensajería, identidad, observabilidad, IA, pagos.

## 10. Local Development First

Un desarrollador debe poder levantar el entorno completo en una computadora convencional con Docker Compose y datos demo.

## 11. Security & Privacy by Design

Seguridad, privacidad, auditoría, trazabilidad, cifrado y mínimo privilegio se diseñan desde el inicio.

## 12. Progressive Capability & Anti-Obsolescence

Nexora soporta dispositivos modestos y navegadores comerciales comunes, pero no sacrifica IA, seguridad ni evolución técnica por compatibilidad extrema con plataformas obsoletas.

## 13. Accessible & Low-Resource First

La experiencia debe ser clara, ligera, funcional y usable en condiciones de bajo recurso, especialmente para pacientes y médicos.

## 14. Open Standards First

Nexora favorece estándares abiertos: OpenAPI, OAuth2, OpenID Connect, OpenTelemetry, HL7, FHIR, ASTM, DICOM.

## 15. Configuration over Customization

Configurar antes que programar: estudios, reportes, etiquetas, permisos, flujos, formularios, reglas y notificaciones deben ser configurables cuando sea posible.

## 16. Living Documentation

La documentación es parte del producto. Si cambia una API, regla, entidad o pantalla, cambia también el conocimiento asociado.

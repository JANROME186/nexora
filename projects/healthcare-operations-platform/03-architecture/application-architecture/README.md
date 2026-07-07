# Application Architecture

Este volumen define la arquitectura de aplicaciones de Nexora como una capa empresarial, no como una lista de pantallas o servicios técnicos.

La arquitectura de aplicaciones responde:

- Qué aplicaciones existen en el ecosistema Nexora.
- Qué canales consumen las capacidades de negocio.
- Qué servicios de aplicación exponen dichas capacidades.
- Qué APIs, eventos e integraciones conectan el ecosistema.
- Cómo se mantiene la independencia entre canal, dominio, infraestructura y despliegue.

## Principios

1. **Capability Driven Applications**: las aplicaciones consumen capacidades de negocio, no duplican lógica.
2. **API Contract First**: toda interacción externa o interna expuesta como API debe estar gobernada por OpenAPI.
3. **Channel Independence**: web, mobile, portal médico, portal paciente y API pública consumen el mismo núcleo funcional.
4. **Deployable Unit Agnostic**: una capacidad puede ejecutarse dentro de un monolito modular, microservicio o función serverless.
5. **Integration Ready**: todo módulo debe prepararse para integrarse con eventos, webhooks, colas y estándares abiertos.

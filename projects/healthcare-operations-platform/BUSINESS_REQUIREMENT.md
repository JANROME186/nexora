# Healthcare Operations Platform — Business Requirement

## 1. Propósito del Documento

Este documento describe, desde una perspectiva de negocio, la necesidad del producto **Healthcare Operations Platform**.

Su objetivo es que una persona que requiere el sistema pueda explicar con claridad:

- Qué problema de negocio se quiere resolver.
- Para quién se construye el producto.
- Qué operación debe soportar.
- Qué resultados se esperan.
- Qué límites, riesgos y reglas deben respetarse.
- Qué capacidades comerciales debe tener el producto.
- Qué información necesita un agente para convertir este requerimiento en artefactos de análisis, definición, YAML ejecutable y backlog de implementación.

Este documento es la materia prima del proyecto. En proyectos futuros, el solicitante debe proporcionar un documento equivalente antes de que cualquier agente genere análisis, propuesta de solución, MVP, YAML, backlog o implementación.

## 2. Cómo Debe Usarse Este Documento

Este archivo debe ser leído primero por personas de negocio, producto y arquitectura.

Después, un agente puede transformarlo en `BUSINESS_REQUIREMENT.yaml` usando el prompt definido en:

`04-requirements/prompts/business-requirement-to-yaml-prompt.yaml`

Reglas de uso:

- El agente puede estructurar, resumir y normalizar este contenido.
- El agente no debe inventar necesidades de negocio ausentes.
- El agente debe marcar como `requires_clarification` cualquier dato faltante que sea necesario para análisis o desarrollo.
- El YAML resultante debe ser un índice estructurado de este requerimiento, no un reemplazo del documento humano.
- Si este archivo no existe en un proyecto, el agente debe detenerse y solicitarlo.

## 3. Resumen Ejecutivo

Healthcare Operations Platform, abreviado como **HOP**, es una plataforma de operaciones para organizaciones de diagnóstico médico.

El producto debe permitir que laboratorios clínicos, centros de imagenología y organizaciones diagnósticas multi-sucursal administren su operación desde la configuración organizacional hasta la entrega segura de resultados.

El producto debe cubrir:

- Operación administrativa.
- Gestión de pacientes.
- Gestión de médicos.
- Catálogo diagnóstico.
- Recepción y admisión.
- Órdenes.
- Caja y solicitudes de facturación.
- Toma y trazabilidad de muestras.
- Procesamiento de laboratorio.
- Validación técnica y médica.
- Liberación y entrega digital de resultados.
- Integraciones.
- Migración de datos.
- Auditoría.
- Seguridad.
- Evolución comercial mediante paquetes y marketplace.
- Extensiones futuras de imagenología, calidad, inventario e inteligencia artificial.

HOP también debe funcionar como primer producto de referencia de Nexora para demostrar cómo una necesidad de negocio se convierte en un producto dirigido por modelos y desarrollado incrementalmente con agentes de IA de manera agnóstica a herramientas.

## 4. Contexto de Negocio

Las organizaciones de diagnóstico médico operan con una cadena de actividades que involucra pacientes, médicos, personal administrativo, personal clínico, laboratorios, sucursales, equipos, integraciones, pagos, facturación, reportes y resultados.

En muchas organizaciones esta operación está fragmentada:

- Una herramienta registra pacientes.
- Otra administra órdenes.
- Otra maneja resultados.
- El cobro se controla por separado.
- Las muestras se trazan manualmente.
- Las integraciones se resuelven de forma puntual.
- Los resultados se entregan por canales poco controlados.
- Las migraciones desde sistemas anteriores son difíciles y riesgosas.

Esta fragmentación reduce trazabilidad, dificulta auditorías, aumenta errores operativos y limita la capacidad de crecer.

## 5. Oportunidad de Negocio

La oportunidad es construir una plataforma comercializable que permita a organizaciones diagnósticas operar de forma integrada, segura, auditable y extensible.

La oportunidad para Nexora es doble:

- Crear un producto de salud que pueda venderse, instalarse, operar y evolucionar comercialmente.
- Crear una referencia reutilizable para que futuros productos Nexora sean definidos con el mismo framework, usando requerimientos de negocio, modelos, capabilities, contratos, backlog, validación y generación incremental.

El producto debe estar preparado para venderse como solución SaaS o como software empresarial desplegable, sin depender de un proveedor cloud, agente de IA, modelo específico, runtime propietario o canal único de comercialización.

## 6. Problema a Resolver

Las organizaciones diagnósticas necesitan controlar toda la operación desde un solo modelo coherente.

Problemas actuales:

- Duplicidad e inconsistencia de datos de pacientes.
- Falta de trazabilidad entre cita, recepción, orden, pago, muestra, resultado y entrega.
- Catálogos de pruebas y precios difíciles de gobernar.
- Validaciones clínicas no siempre separadas por rol y responsabilidad.
- Resultados entregados sin suficiente control de autorización.
- Procesos manuales en caja, recepción y toma de muestras.
- Sucursales con prácticas operativas distintas.
- Integraciones punto a punto difíciles de mantener.
- Migraciones desde sistemas existentes sin validación suficiente.
- Auditorías complicadas por ausencia de eventos inmutables.
- Falta de canales digitales robustos para pacientes y médicos.
- Dificultad para agregar nuevas funcionalidades comerciales sin modificar el núcleo del producto.

## 7. Necesidad del Usuario

Los usuarios necesitan una plataforma que les permita operar de forma continua, trazable y segura.

Los administradores necesitan configurar organizaciones, laboratorios, sucursales, usuarios, roles, permisos y reglas operativas.

El personal de recepción necesita registrar pacientes, gestionar citas, crear órdenes, admitir pacientes y coordinar el flujo de atención.

El personal de caja necesita gestionar pagos, sesiones de caja, ventas y solicitudes de facturación sin mezclar la lógica fiscal con la operación clínica.

El personal de laboratorio necesita tomar muestras, etiquetarlas, recibirlas, procesarlas y capturar resultados con trazabilidad completa.

Los validadores técnicos y médicos necesitan separar responsabilidades, revisar resultados, validar hallazgos y liberar únicamente resultados autorizados.

Los pacientes necesitan recibir resultados de forma segura y entender el estado de sus estudios.

Los médicos referidores necesitan consultar resultados autorizados de sus pacientes.

Los responsables comerciales y de operación necesitan agregar capacidades, paquetes, integraciones y extensiones sin crear forks del producto.

## 8. Usuarios y Actores Principales

Actores internos:

- Administrador de plataforma.
- Administrador de tenant.
- Administrador de laboratorio.
- Administrador de sucursal.
- Responsable de catálogo.
- Recepcionista.
- Cajero.
- Tomador de muestra.
- Técnico de laboratorio.
- Validador técnico.
- Validador médico.
- Supervisor operativo.
- Personal de soporte.

Actores externos:

- Paciente.
- Representante del paciente.
- Médico referidor.
- Empresa o convenio.
- Proveedor.
- Partner de integración.
- Autoridad fiscal o regulatoria a través de adaptadores.

Sistemas externos:

- Equipos de laboratorio.
- Sistemas legados.
- Sistemas fiscales.
- Sistemas de notificación.
- Portales externos.
- Sistemas de pago.
- Sistemas de marketplace o billing, cuando aplique.

## 9. Alcance del Producto

HOP debe cubrir el ciclo operativo diagnóstico completo.

Alcance funcional principal:

- Gestión organizacional.
- Gestión de personas.
- Gestión de pacientes.
- Gestión de médicos.
- Gestión de empresas y convenios.
- Catálogo de servicios diagnósticos.
- Catálogo de pruebas, paneles, analitos, muestras y contenedores.
- Preparaciones de paciente.
- Rangos de referencia.
- Tarifas y precios.
- Agenda.
- Recepción.
- Admisión.
- Cotizaciones.
- Órdenes.
- Caja.
- Solicitudes de facturación.
- Toma de muestra.
- Etiquetado.
- Recepción de muestra.
- Procesamiento.
- Validación técnica.
- Validación médica.
- Liberación de resultados.
- Reportes PDF.
- Entrega digital.
- Notificaciones.
- Auditoría.
- Configuración.
- Integraciones.
- API management.
- Ingesta abierta de datos y migración.
- Marketplace de producto y derechos de uso.

Alcance de expansión:

- Inventario.
- Reactivos.
- Equipos.
- Calidad.
- CAPA.
- Auditorías avanzadas.
- Imagenología.
- DICOM.
- PACS.
- Dictado médico.
- Firma radiológica.
- IA administrativa.
- IA clínica asistida.
- OCR inteligente.
- Búsqueda semántica.
- Motor RAG.
- Agentes especializados.

## 10. Business Capability Map Esperado

El producto debe organizarse por capacidades de negocio, no por pantallas ni por CRUD.

El Business Capability Map actual de HOP contiene:

- 11 dominios.
- 92 capacidades de negocio.
- 460 requerimientos funcionales.
- 460 historias de usuario.

Dominios principales:

1. Organization.
2. People.
3. Diagnostic Services.
4. Care Delivery.
5. Clinical Operations.
6. Imaging.
7. Results.
8. Inventory.
9. Quality.
10. Platform.
11. Artificial Intelligence.

Cada capacidad debe evolucionar como un paquete autónomo con modelo de negocio, reglas, procesos, eventos, contratos, permisos, UI, mobile, pruebas, observabilidad, trazabilidad y documentación.

## 11. Enfoque de Ingeniería del Producto

HOP debe seguir **Model Driven Product Engineering**.

Esto significa:

- Los modelos son la fuente editable.
- Los artefactos repetitivos se generan o derivan de los modelos.
- El desarrollo se organiza por Business Capability Packages.
- Los módulos son agrupadores de roadmap, no la fuente de verdad.
- La implementación debe seguir la secuencia `Model -> Compile -> Implement Rules -> Validate -> Release`.

No se deben escribir manualmente como fuente primaria:

- CRUD repetitivo.
- DTOs repetitivos.
- Controllers repetitivos.
- Repositories repetitivos.
- Swagger derivado.
- SDKs derivados.
- Documentación repetitiva.
- Casos de prueba repetitivos.
- Modelos duplicados.

Sí se deben escribir y gobernar cuidadosamente:

- Modelo de negocio.
- Reglas de negocio.
- Procesos.
- Decisiones de dominio.
- Contratos OpenAPI fuente.
- Reglas no generables.
- Adaptadores externos.
- Plantillas de generación.
- ADRs.

## 12. MVP Esperado

El MVP debe demostrar que una organización diagnóstica puede operar el flujo mínimo completo de laboratorio clínico.

Módulos MVP:

| Módulo | Nombre | Propósito de negocio |
| --- | --- | --- |
| MVP-MOD-001 | Platform Foundation | Establecer tenants, laboratorios, sucursales, identidad, permisos, auditoría y observabilidad. |
| MVP-MOD-002 | Diagnostic Catalog | Configurar servicios, pruebas, paneles, analitos, muestras, preparaciones, rangos y precios. |
| MVP-MOD-003 | People and Clinical Master Data | Gestionar pacientes, médicos y personas. |
| MVP-MOD-004 | Front Desk and Care Delivery | Gestionar agenda, recepción, admisión, cotizaciones y órdenes. |
| MVP-MOD-005 | Cashier and Billing Request | Gestionar caja, pagos, ventas y solicitudes de facturación. |
| MVP-MOD-006 | Laboratory Workflow | Gestionar toma, etiquetado, recepción, procesamiento y validación de muestras. |
| MVP-MOD-007 | Results and Digital Delivery | Generar reportes y entregar resultados liberados a canales autorizados. |
| MVP-MOD-008 | Integration and Migration Readiness | Definir integraciones, APIs, importación, validación, reconciliación y migración. |

Estado actual:

- `MVP-MOD-001 Platform Foundation` ya fue implementado y cerrado técnicamente.
- El siguiente paso activo es `MVP-MOD-002-DEF`, que debe generar los Business Capability Packages del catálogo diagnóstico.

## 13. Producto Comercial Completo

Después del MVP, HOP debe evolucionar hasta ser un producto comercializable.

El producto comercial debe incluir:

- Portales de pacientes y médicos.
- Mobile app base.
- Inventario e calidad interna.
- Sitio público y crecimiento digital.
- Operación SaaS.
- Seguridad productiva.
- Observabilidad productiva.
- Backup y restore.
- Soporte.
- Onboarding.
- Documentación de cliente.
- Marketplace de funcionalidades.
- Paquetes de expansión.
- Imagenología.
- IA asistida.
- Cumplimiento y calidad avanzada.

El backlog comercial vigente está en:

`06-delivery/commercial-product/HOP_COMMERCIAL_PRODUCT_BACKLOG.yaml`

## 14. Marketplace y Extensibilidad Comercial

HOP debe permitir que nuevas funcionalidades se publiquen, compren, asignen, instalen, activen y consuman por cliente o tenant.

El marketplace no debe ser una idea posterior. Debe formar parte del modelo de producto.

El producto debe soportar:

- Catálogo de paquetes.
- Ofertas comerciales.
- Trials.
- Bundles.
- Planes de licencia.
- Entitlements por tenant.
- Instalación por tenant.
- Activación.
- Suspensión.
- Upgrade.
- Rollback.
- Uninstall.
- Retiro.
- Auditoría.
- Observabilidad.
- Adaptadores de billing y pago reemplazables.

Tipos de paquetes esperados:

- Capability packages.
- Integration adapters.
- UI extensions.
- Mobile extensions.
- AI extensions.
- Report templates.
- Country packs.
- Data ingestion adapters.
- Workflow templates.

Reglas:

- Comprar una funcionalidad no otorga permisos por sí mismo.
- Todo uso debe validar entitlement, IAM, permisos, auditoría y reglas de negocio.
- Un paquete no puede debilitar validaciones clínicas, financieras, fiscales o de privacidad.
- Los proveedores de billing, pagos y marketplace deben ser adaptadores reemplazables.

## 15. Migración e Ingesta Abierta

HOP debe facilitar que clientes que ya tienen un sistema puedan migrar datos a la plataforma.

La migración debe basarse en formatos simples que cualquier proveedor incumbente pueda entregar razonablemente:

- CSV.
- XLSX.
- JSON.
- NDJSON.
- ZIP con manifiesto.

La plataforma debe soportar:

- Paquetes de importación.
- Manifiesto.
- Validación previa.
- Dry run.
- Reconciliación.
- Reportes de errores.
- Trazabilidad origen-destino.
- Auditoría.
- Reintentos.
- Ejecución mediante comandos de dominio.

La migración no debe insertar datos directamente saltándose reglas de negocio.

## 16. Reglas de Negocio Críticas

Reglas base:

- Toda acción protegida requiere actor autenticado.
- Todo acceso debe estar limitado por tenant, laboratorio, sucursal y rol.
- Las asignaciones de rol deben tener alcance.
- Los pacientes son master data y no deben ser mutados por órdenes o resultados.
- Las órdenes deben usar snapshots de paciente, catálogo y precio.
- Solo servicios publicados pueden ser ordenados.
- Los pagos requieren sesión de caja activa.
- La facturación fiscal debe pasar por adaptadores de país.
- Toda muestra debe trazar a orden, paciente, sucursal, tomador y tiempo de toma.
- Una muestra rechazada bloquea resultados dependientes salvo proceso de reemplazo u override autorizado.
- La validación técnica precede a la validación médica, salvo regla explícita.
- La validación médica es requerida antes de liberar resultados externamente.
- Resultados críticos deben generar notificación o escalamiento trazable.
- Pacientes y médicos solo ven resultados liberados y autorizados.
- Integraciones y migraciones deben pasar por capas anti-corrupción.
- IA puede asistir, pero no validar, liberar, corregir ni diagnosticar resultados clínicos.
- Los eventos de auditoría son append-only.

## 17. Datos, Privacidad y Auditoría

El producto maneja información sensible.

Debe proteger:

- Datos personales.
- Datos clínicos.
- Resultados.
- Historial de resultados.
- Pagos.
- Información fiscal.
- Documentos.
- Usuarios y permisos.
- Evidencia de auditoría.

Expectativas:

- Acceso mínimo necesario.
- Auditoría de acciones críticas.
- Evidencia inmutable.
- Correcciones mediante nuevos eventos.
- Trazabilidad de liberación de resultados.
- Control de acceso a portales.
- Separación entre autorización clínica, administrativa y comercial.
- Protección de información importada durante migraciones.

## 18. Integraciones

HOP debe integrarse con sistemas externos sin acoplar el dominio interno a protocolos o proveedores.

Integraciones esperadas:

- Equipos de laboratorio.
- Sistemas fiscales.
- Sistemas de pago.
- Sistemas de notificación.
- Portales.
- APIs públicas.
- Webhooks.
- Sistemas legados.
- Marketplace o billing provider.

Reglas:

- Todo protocolo externo debe traducirse antes de entrar al dominio.
- Ninguna integración debe mutar agregados de otro bounded context directamente.
- Los errores deben ser observables y auditables.
- Las integraciones deben poder reemplazarse.

## 19. Canales Digitales

HOP debe soportar diferentes superficies de usuario:

- Employee portal.
- Patient portal.
- Doctor portal.
- Public website.
- Mobile app.
- Operations console.

Los canales deben respetar la misma política de seguridad, privacidad, auditoría y entitlement.

## 20. Inteligencia Artificial

IA es una capacidad progresiva, no una dependencia del núcleo operativo.

IA puede apoyar:

- Resúmenes administrativos.
- Asistencia clínica supervisada.
- OCR.
- Búsqueda semántica.
- RAG.
- Recomendaciones operativas.
- Revisión de calidad de datos.
- Explicación de errores de migración.

IA no puede:

- Diagnosticar.
- Validar resultados.
- Liberar resultados.
- Modificar resultados clínicos.
- Saltarse reglas de privacidad.
- Tomar decisiones clínicas autónomas.
- Ser requisito para continuidad operativa básica.

## 21. Restricciones

Restricciones obligatorias:

- El proyecto debe ser agent agnostic.
- El proyecto debe ser cloud agnostic.
- El proyecto debe ser provider agnostic.
- El proyecto debe poder entenderse desde el repositorio, sin historial de conversación.
- Los artefactos fuente viven dentro del proyecto.
- El framework vive en `nexora-framework/`.
- El código vive bajo `07-implementation/`.
- Los modelos son fuente de verdad.
- Los artefactos generados no deben editarse manualmente como fuente primaria.
- Cambios de arquitectura deben documentarse con ADR.

## 22. Fuera de Alcance Inicial

Fuera del MVP operativo:

- PACS completo.
- DICOM completo.
- Dictado radiológico avanzado.
- Firma radiológica avanzada.
- Inventario completo.
- Compras completas.
- CAPA completo.
- Control externo de calidad completo.
- Conectores fiscales específicos.
- Conectores de equipo específicos.
- Marketplace UI avanzado.
- IA avanzada obligatoria.

Estas capacidades pueden desarrollarse después si el core operativo ya es estable.

## 23. Criterios de Éxito

El producto será exitoso cuando:

- Un laboratorio pueda configurar su organización, sucursales, usuarios y permisos.
- Se pueda configurar catálogo diagnóstico versionado.
- Se puedan registrar pacientes y médicos.
- Se puedan crear órdenes.
- Se puedan cobrar servicios.
- Se puedan tomar y procesar muestras.
- Se puedan validar resultados.
- Se puedan liberar resultados.
- Pacientes y médicos puedan consultar resultados autorizados.
- Integraciones y migraciones estén gobernadas.
- Todo evento crítico sea auditable.
- Se puedan agregar capacidades comerciales mediante paquetes.
- El producto pueda evolucionar por modelos.
- Un agente pueda continuar el desarrollo leyendo solamente el repositorio.

## 24. Información Faltante o a Confirmar

El solicitante del negocio deberá confirmar en etapas posteriores:

- País inicial de operación comercial.
- Reglas fiscales específicas del primer país.
- Tipos de laboratorio objetivo para el primer cliente.
- Volumen esperado de órdenes, resultados y sucursales.
- Canales de notificación prioritarios.
- Integraciones obligatorias para el primer cliente.
- Paquetes comerciales iniciales.
- Modelo de precios.
- Estrategia de despliegue preferida para primeros clientes.
- Requisitos regulatorios específicos por país.

El agente debe registrar estos puntos como aclaraciones pendientes, no inventarlos.

## 25. Estructura Reutilizable para Futuros Proyectos

Para un nuevo proyecto Nexora, quien solicita el sistema debe documentar un `BUSINESS_REQUIREMENT.md` siguiendo esta estructura mínima:

1. Propósito del documento.
2. Resumen ejecutivo.
3. Contexto de negocio.
4. Oportunidad de negocio.
5. Problema a resolver.
6. Necesidad del usuario.
7. Usuarios y actores.
8. Alcance del producto.
9. Capacidades de negocio esperadas.
10. MVP esperado.
11. Producto comercial completo.
12. Extensibilidad o marketplace, si aplica.
13. Migración e ingesta, si aplica.
14. Reglas críticas.
15. Datos, privacidad y auditoría.
16. Integraciones.
17. Canales digitales.
18. IA y automatización, si aplica.
19. Restricciones.
20. Fuera de alcance.
21. Criterios de éxito.
22. Información faltante o por confirmar.

## 26. Prompt para Transformar este Documento a YAML

El prompt oficial vive en:

`04-requirements/prompts/business-requirement-to-yaml-prompt.md`

Uso esperado:

1. El solicitante escribe o actualiza `BUSINESS_REQUIREMENT.md`.
2. El agente carga el prompt.
3. El agente transforma el documento en `BUSINESS_REQUIREMENT.yaml`.
4. El agente no inventa datos faltantes.
5. El agente marca aclaraciones.
6. El agente valida que el YAML sea legible por otros agentes.
7. El agente actualiza `SOURCE_OF_TRUTH.yaml` si corresponde.

## 27. Versionado del Requerimiento de Negocio

El negocio puede incrementar o modificar este requerimiento con nuevas versiones.

La versión vigente debe resolverse mediante:

`00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.yaml`

Reglas:

- `BUSINESS_REQUIREMENT.md` en la raíz del proyecto representa el requerimiento vigente para lectura humana.
- `BUSINESS_REQUIREMENT.yaml` representa el índice estructurado derivado de la versión vigente.
- El índice de versiones declara cuál versión debe usar el agente.
- Si el negocio modifica el requerimiento, el agente debe comparar la versión vigente contra la versión anterior.
- Antes de modificar artefactos derivados o código, el agente debe generar un análisis de impacto.
- El análisis de impacto debe estimar componentes afectados, esfuerzo, tiempo y costo.
- Si no existe una tarifa o rate card, el costo debe marcarse como pendiente de tarifa y no inventarse.

Los análisis de impacto deben guardarse en:

`00-intake/business-requirements/impact-assessments/<version>/`

## 28. Artefactos Relacionados

Artefactos principales:

- `BUSINESS_REQUIREMENT.yaml`
- `00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.yaml`
- `PROJECT_BRIEF.md`
- `PROJECT_BRIEF.yaml`
- `SOURCE_OF_TRUTH.yaml`
- `PROJECT_STATE.yaml`
- `ORDERED_DEVELOPMENT_GUIDE.md`
- `01-product-definition/business-capabilities/bcm-001/business-capability-map.yaml`
- `01-product-definition/business-capabilities/bcm-002/capability-dependency-map.yaml`
- `01-product-definition/business-capabilities/packages/capability-package-index.yaml`
- `02-domain-definition/actors/acm-001/actor-catalog.yaml`
- `02-domain-definition/business-rules/brm-001/business-rules-catalog.yaml`
- `02-domain-definition/domain-foundation/context-map/context-map.yaml`
- `04-requirements/requirements-manifest.yaml`
- `05-contracts/import-export/open-data-ingestion/open-data-ingestion-contract.yaml`
- `05-contracts/marketplace/product-marketplace/product-marketplace-contract.yaml`
- `06-delivery/mvp/healthcare-operations-platform-mvp-framework.yaml`
- `06-delivery/commercial-product/HOP_COMMERCIAL_PRODUCT_BACKLOG.yaml`

## 29. Declaración Final

Healthcare Operations Platform debe convertirse en un producto comercial, extensible y gobernado por modelos.

El objetivo no es construir pantallas aisladas ni CRUDs sueltos.

El objetivo es construir capacidades de negocio completas, trazables, versionables y comercializables, de forma que Nexora pueda repetir este método en futuros productos.

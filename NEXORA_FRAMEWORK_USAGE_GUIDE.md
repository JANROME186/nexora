# Guia de Uso del Framework Nexora

## Proposito

Esta guia explica, paso a paso, como usar el framework de Nexora para convertir una necesidad de negocio de alto nivel en una definicion de proyecto lista para MVP y, despues, entregar ese proyecto a un agente de desarrollo.

Esta pensada para una persona o equipo que llega al repositorio sin contexto previo y necesita saber exactamente que hacer, que pedirle al agente, que debe generar el agente, como validar el resultado y como iniciar la etapa de implementacion.

El flujo completo es:

1. Crear o seleccionar una carpeta de proyecto dentro de `projects/`.
2. Recibir del solicitante el requerimiento de negocio de alto nivel.
3. Colocar ese requerimiento en `BUSINESS_REQUIREMENT.md`.
4. Iniciar un agente de analisis para aplicar el framework Nexora.
5. Validar que el agente genero todas las definiciones requeridas.
6. Iniciar un agente de desarrollo para implementar el primer modulo del MVP usando solo artefactos del repositorio.

## Prompts Genericos Oficiales

El framework incluye un playbook de prompts reutilizable para cualquier proyecto:

- `nexora-framework/05-prompts/prompts/generic-project-lifecycle-prompts.md`
- `nexora-framework/05-prompts/prompts/generic-project-lifecycle-prompts.yaml`
- `nexora-framework/05-prompts/prompts/auxiliary-development-prompts.md`
- `nexora-framework/05-prompts/prompts/auxiliary-development-prompts.yaml`
- `nexora-framework/05-prompts/prompts/security-quality-gate-prompts.md`
- `nexora-framework/05-prompts/prompts/security-quality-gate-prompts.yaml`
- `nexora-framework/05-prompts/prompts/framework-feedback-prompts.md`
- `nexora-framework/05-prompts/prompts/framework-feedback-prompts.yaml`

La version YAML es la fuente operativa para agentes. La version Markdown es para lectura humana.

Los prompts auxiliares de desarrollo ayudan a ejecutar kickoff de modulo, slices de backlog, backend, web, movil, QA y cierre. No reemplazan a los prompts genericos; solo se usan despues de que el prompt generico de desarrollo selecciona proyecto, modulo y slice.

Los prompts de security quality gate aplican la politica open-source-first y las validaciones de seguridad, dependencias, cobertura y buenas practicas para cada backlog que cambia codigo.

Los prompts de framework feedback capturan aprendizaje de ejecucion para proponer mejoras al framework. Los agentes pueden crear feedback y propuestas de backlog de framework, pero no deben implementar esas mejoras salvo que Nexora lo asigne explicitamente.

Para usar el framework con un nuevo proyecto, la persona usuaria solo debe:

1. Crear o seleccionar `projects/<project-slug>/`.
2. Colocar `BUSINESS_REQUIREMENT.md` dentro de esa carpeta.
3. Pedir al agente que aplique el prompt generico de analisis.
4. Pedir al agente que aplique el prompt generico de validacion.
5. Si la validacion aprueba, pedir al agente que aplique el prompt generico de desarrollo.

Prompt minimo de analisis:

```text
Apply the Nexora framework to projects/<project-slug>/ and generate all MVP-ready definitions from BUSINESS_REQUIREMENT.md.
```

Prompt minimo de validacion:

```text
Validate projects/<project-slug>/ against the Nexora framework and report whether it is ready for MVP development.
```

Prompt minimo de desarrollo:

```text
Develop the MVP for projects/<project-slug>/ using its PROJECT_STATE.yaml, SOURCE_OF_TRUTH.yaml and ordered module package.
```

En los tres casos, reemplazar `<project-slug>` por la carpeta real del proyecto.

## Regla Principal

El repositorio es la fuente de verdad.

Un agente puede usar herramientas, prompts y automatizacion local para trabajar mas rapido, pero todo conocimiento durable debe quedar escrito en archivos del repositorio. Si un requerimiento, decision, modulo, API, prueba o restriccion existe solo en una conversacion, no es una fuente valida.

## Niveles del Repositorio

El repositorio tiene tres niveles de responsabilidad.

### Raiz del Repositorio

La raiz describe a Nexora, el estado general del repositorio y la forma de usar el framework.

Archivos importantes:

- `AGENT_BOOTSTRAP.md`
- `README.md`
- `NEXORA_FRAMEWORK_USAGE_GUIDE.md`
- `SOURCE_OF_TRUTH.yaml`
- `PROJECT_STATE.yaml`
- `PROJECT_MANIFEST.yaml`
- `CHANGELOG.md`

No se deben colocar definiciones especificas de un proyecto en la raiz.

### Framework Nexora

`nexora-framework/` contiene el metodo reutilizable para analizar, documentar y preparar soluciones.

Archivos importantes:

- `nexora-framework/README.md`
- `nexora-framework/02-standards/standards/project-folder-standard.yaml`
- `nexora-framework/02-standards/standards/documentation-standard.yaml`
- `nexora-framework/02-standards/standards/agent-agnostic-standard.yaml`
- `nexora-framework/02-standards/standards/model-driven-product-engineering-standard.yaml`
- `nexora-framework/02-standards/standards/capability-package-standard.yaml`
- `nexora-framework/02-standards/standards/open-data-ingestion-standard.yaml`
- `nexora-framework/02-standards/standards/product-marketplace-standard.yaml`
- `nexora-framework/02-standards/standards/business-requirement-versioning-standard.yaml`
- `nexora-framework/02-standards/standards/open-source-first-security-quality-standard.yaml`
- `nexora-framework/03-orchestration/project-orchestration/project-analysis-and-mvp-workflow.yaml`
- `nexora-framework/04-recipes/recipes/agent-to-mvp-recipe.yaml`
- `nexora-framework/05-prompts/prompts/business-requirement-impact-prompts.yaml`
- `nexora-framework/05-prompts/prompts/security-quality-gate-prompts.yaml`
- `nexora-framework/06-templates/templates/project-template/`

Todo agente debe cargar el framework antes de analizar o implementar un proyecto.

### Carpetas de Proyecto

Cada solucion vive dentro de:

`projects/<project-slug>/`

Cada proyecto debe ser autocontenido. Un nuevo agente debe poder entenderlo leyendo los archivos del proyecto y el framework Nexora reutilizable.

Archivos requeridos en la raiz de cada proyecto:

- `BUSINESS_REQUIREMENT.md`
- `PROJECT_BRIEF.md`
- `PROJECT_BRIEF.yaml`
- `SOURCE_OF_TRUTH.yaml`
- `PROJECT_STATE.yaml`
- `README.md`

Regla de formato:

- Los archivos `.md` son para lectura, revision y explicacion humana.
- Los archivos `.yaml` son para consumo operativo de agentes y automatizacion.
- Todo artefacto que un agente deba ejecutar o seguir debe tener version YAML cuando aplique.
- `BUSINESS_REQUIREMENT.md` siempre lo provee el solicitante; si existe `BUSINESS_REQUIREMENT.yaml`, es un indice estructurado derivado, no un reemplazo del requerimiento original.

Carpetas requeridas por proyecto:

- `00-intake/`
- `01-product-definition/`
- `02-domain-definition/`
- `03-architecture/`
- `04-requirements/`
- `05-contracts/`
- `06-delivery/`
- `07-implementation/`
- `08-qa/`
- `09-operations/`
- `10-generated/`
- `99-legacy/`

## Etapa 1: Crear o Seleccionar el Proyecto

Para iniciar una nueva solucion, crear una carpeta bajo:

`projects/<project-slug>/`

Usar un identificador en minusculas y con guiones. Ejemplo:

`projects/healthcare-operations-platform/`

Para un proyecto nuevo, copiar o reproducir la estructura de:

`nexora-framework/06-templates/templates/project-template/`

El primer archivo obligatorio del proyecto es:

`projects/<project-slug>/BUSINESS_REQUIREMENT.md`

Este archivo lo provee quien requiere el sistema. No lo genera el agente.

Sin ese archivo, el agente no debe iniciar el analisis, no debe crear `PROJECT_BRIEF.md`, no debe crear mapas de capacidades, no debe proponer MVP y no debe generar paquetes de modulo.

## Etapa 2: Documentar el Requerimiento de Negocio

`BUSINESS_REQUIREMENT.md` es la materia prima del framework y el primer artefacto fuente especifico del proyecto.

Debe ser proporcionado por la persona, empresa o equipo que requiere el sistema.

El agente puede leerlo, detectar si esta incompleto y pedir aclaraciones, pero no debe generarlo desde cero ni sustituirlo con supuestos.

Debe explicar la necesidad de negocio antes de crear una propuesta de solucion, arquitectura, alcance MVP o backlog de desarrollo.

Un requerimiento de negocio completo debe incluir:

- Contexto de negocio.
- Oportunidad de negocio.
- Necesidad del usuario.
- Dolor actual.
- Resultado deseado.
- Usuarios, actores o grupos operativos.
- Areas de capacidades esperadas.
- Expectativa del MVP.
- Expectativa del primer modulo, si ya se conoce.
- Reglas de negocio y guardrails.
- Principios de dominio e integracion.
- Expectativas de arquitectura.
- Expectativas de datos, privacidad y auditoria.
- Expectativas de IA y automatizacion, si aplican.
- Fuera de alcance.
- Restricciones.
- Criterios de exito.
- Referencias a artefactos posteriores, cuando ya existan.

El ejemplo de referencia actual es:

`projects/healthcare-operations-platform/BUSINESS_REQUIREMENT.md`

## Etapa 3: Iniciar el Agente de Analisis

El agente de analisis convierte el requerimiento de negocio en una definicion de proyecto lista para MVP.

El agente de analisis no debe crear codigo de aplicacion.

Su responsabilidad es crear o completar artefactos fuente, artefactos de entrega, paquetes de modulos y metadata de readiness.

### Instruccion Inicial para el Agente de Analisis

Usar esta instruccion al iniciar el agente de analisis:

```text
Lee AGENT_BOOTSTRAP.md.
Lee NEXORA_FRAMEWORK_USAGE_GUIDE.md.
Lee SOURCE_OF_TRUTH.yaml y PROJECT_STATE.yaml.
Carga el framework Nexora desde nexora-framework/.
Aplica nexora-framework/03-orchestration/project-orchestration/project-analysis-and-mvp-workflow.yaml.
Aplica nexora-framework/04-recipes/recipes/agent-to-mvp-recipe.yaml.

Proyecto objetivo:
projects/<project-slug>/

Archivo inicial:
projects/<project-slug>/BUSINESS_REQUIREMENT.md

Tu objetivo es analizar el proyecto, completar todas las definiciones faltantes, crear el framework de MVP, crear el primer paquete de modulo y dejar PROJECT_STATE.yaml sin bloqueos de definicion.

No implementes codigo de aplicacion.
No dependas del historial de conversacion.
No introduzcas requisitos especificos de agente, proveedor o nube salvo que el requerimiento de negocio lo exija explicitamente.
Escribe todas las decisiones durables en archivos del repositorio.
```

Reemplazar `<project-slug>` por la carpeta real del proyecto.

### Orden Obligatorio de Carga para Analisis

El agente de analisis debe cargar estos archivos en este orden:

1. `AGENT_BOOTSTRAP.md`
2. `NEXORA_FRAMEWORK_USAGE_GUIDE.md`
3. `SOURCE_OF_TRUTH.yaml`
4. `PROJECT_STATE.yaml`
5. `nexora-framework/README.md`
6. `nexora-framework/02-standards/standards/project-folder-standard.yaml`
7. `nexora-framework/02-standards/standards/documentation-standard.yaml`
8. `nexora-framework/02-standards/standards/agent-agnostic-standard.yaml`
9. `nexora-framework/02-standards/standards/model-driven-product-engineering-standard.yaml`
10. `nexora-framework/02-standards/standards/capability-package-standard.yaml`
11. `nexora-framework/02-standards/standards/open-data-ingestion-standard.yaml`
12. `nexora-framework/02-standards/standards/product-marketplace-standard.yaml`
13. `nexora-framework/02-standards/standards/business-requirement-versioning-standard.yaml`
14. `nexora-framework/02-standards/standards/open-source-first-security-quality-standard.yaml`
15. `nexora-framework/02-standards/standards/framework-feedback-continuous-improvement-standard.yaml`
16. `nexora-framework/03-orchestration/project-orchestration/project-analysis-and-mvp-workflow.yaml`
17. `nexora-framework/04-recipes/recipes/agent-to-mvp-recipe.yaml`
18. `nexora-framework/05-prompts/prompts/business-requirement-impact-prompts.yaml`
19. `nexora-framework/05-prompts/prompts/security-quality-gate-prompts.yaml`
20. `nexora-framework/05-prompts/prompts/framework-feedback-prompts.yaml`
21. `projects/<project-slug>/00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.yaml`, si existe
22. `projects/<project-slug>/BUSINESS_REQUIREMENT.md`
23. `projects/<project-slug>/SOURCE_OF_TRUTH.yaml`, si existe
24. `projects/<project-slug>/PROJECT_BRIEF.md`, si existe
25. `projects/<project-slug>/PROJECT_STATE.yaml`, si existe

Si faltan archivos de control del proyecto, el agente debe crearlos usando el template del framework.

Si falta `BUSINESS_REQUIREMENT.md`, el analisis queda bloqueado. El agente no debe inventar silenciosamente el requerimiento de negocio.

En ese caso, el agente debe detenerse y responder que necesita que el solicitante proporcione `projects/<project-slug>/BUSINESS_REQUIREMENT.md`.

Si existe `00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.yaml`, el agente debe usar la version actual declarada en ese indice. Si la version actual cambio desde el ultimo analisis, debe generar un analisis de impacto antes de modificar artefactos derivados o codigo.

## Etapa 4: Que Debe Generar el Agente de Analisis

El agente de analisis debe completar la carpeta del proyecto hasta que pueda entregarse a agentes especializados de desarrollo.

Los nombres exactos pueden variar si `SOURCE_OF_TRUTH.yaml` documenta la variacion, pero las categorias de definicion son obligatorias.

### Archivos de Control del Proyecto

Salidas requeridas:

- `BUSINESS_REQUIREMENT.md` proporcionado por el solicitante
- `PROJECT_BRIEF.md`
- `SOURCE_OF_TRUTH.yaml`
- `PROJECT_STATE.yaml`
- `README.md`
- `ORDERED_DEVELOPMENT_GUIDE.md`

Proposito:

- `BUSINESS_REQUIREMENT.md` explica la necesidad de negocio y actua como materia prima externa del framework.
- `PROJECT_BRIEF.md` estructura esa necesidad en contexto de producto.
- `SOURCE_OF_TRUTH.yaml` enumera los artefactos autoritativos.
- `PROJECT_STATE.yaml` declara readiness y bloqueos.
- `ORDERED_DEVELOPMENT_GUIDE.md` indica a futuros agentes como cargar y trabajar incrementalmente.

### Definicion de Producto

Carpeta esperada:

`01-product-definition/`

Salidas esperadas:

- Definicion del producto.
- Mapa de capacidades de negocio.
- Mapa de dependencias entre capacidades.
- Alcance MVP y limites no-MVP.
- Personas, usuarios o referencias a actores.
- Notas de evolucion del producto cuando apliquen.

El mapa de capacidades define que debe hacer el producto.

El mapa de dependencias define secuencia, riesgos y dependencias de implementacion.

### Definicion de Dominio

Carpeta esperada:

`02-domain-definition/`

Salidas esperadas:

- Mapa de contexto o definicion de bounded contexts.
- Catalogo de agregados.
- Shared kernel o vocabulario compartido.
- Catalogo de actores.
- Procesos de referencia.
- Catalogo de reglas de negocio.
- Vocabulario canonico.

La definicion de dominio debe dejar clara la propiedad de conceptos. Un agente de implementacion debe saber que contexto es propietario de cada concepto y cuales contextos solo pueden referenciar ids, snapshots, eventos o APIs.

### Definicion de Arquitectura

Carpeta esperada:

`03-architecture/`

Salidas esperadas:

- Arquitectura de aplicacion.
- Arquitectura de datos.
- Arquitectura de seguridad y cumplimiento.
- Arquitectura de integracion.
- Arquitectura tecnologica o de despliegue.
- Arquitectura de IA, solo si aplica.
- Expectativas de observabilidad y ambiente local.

La arquitectura debe ser reemplazable por diseno. Evitar dependencias obligatorias a una nube, proveedor, runtime de modelo o plataforma de agente, salvo que el requerimiento de negocio lo exija explicitamente.

La seleccion tecnologica debe seguir open-source-first: frameworks, librerias, herramientas y runtimes abiertos, self-hostable y basados en estandares. Cualquier dependencia propietaria obligatoria requiere ADR de excepcion con alternativas open source evaluadas, costo total, riesgo de lock-in y estrategia de salida.

### Requerimientos y UX

Carpeta esperada:

`04-requirements/`

Salidas esperadas:

- Historias de usuario.
- Criterios de aceptacion.
- Flujos de negocio.
- Definiciones de pantallas web.
- Definiciones de pantallas moviles, si aplican.
- Trazabilidad desde usuarios y capacidades hacia requerimientos.

### Contratos

Carpeta esperada:

`05-contracts/`

Salidas esperadas:

- Contratos OpenAPI.
- Contratos de eventos.
- Contratos de importacion y exportacion.
- Contratos de adaptadores.
- Definiciones de webhooks o APIs de partners, si aplican.

Los contratos deben clasificar superficies como publicas, partner, internas o de sistema.

### Framework de Entrega MVP

Carpeta esperada:

`06-delivery/`

Salidas esperadas:

- Framework MVP.
- Modulos MVP ordenados.
- Primer paquete de modulo.
- Release notes cuando apliquen.
- Trazabilidad desde el requerimiento de negocio hasta el modulo.

El primer paquete de modulo es la unidad de handoff para el agente de desarrollo.

### Carpeta de Implementacion

Carpeta esperada:

`07-implementation/`

El agente de analisis no debe implementar codigo de aplicacion. Puede dejar instrucciones, un README o un placeholder explicando donde vivira el codigo si la implementacion queda colocada dentro del mismo repositorio.

### QA

Carpeta esperada:

`08-qa/`

Salidas esperadas:

- Estrategia de pruebas.
- Expectativas de contract tests.
- Definiciones de acceptance tests.
- Estructura de fixtures o evidencia de calidad cuando aplique.
- Estructura `security-quality/<backlog-item-id>/` para evidencia de SAST/static analysis, DAST cuando aplique, analisis de dependencias, vulnerabilidades, secrets scan y cobertura.
- Estructura `framework-feedback/` para aprendizaje reutilizable que pueda convertirse en mejoras al framework Nexora.

### Operaciones

Carpeta esperada:

`09-operations/`

Salidas esperadas:

- Gobernanza de ingenieria.
- ADRs.
- Validadores.
- Notas de runtime.
- Expectativas de observabilidad y runbooks.

### Artefactos Generados

Carpeta esperada:

`10-generated/`

Puede contener diagramas, indices, resumenes markdown o paquetes de contexto para agentes.

Los artefactos generados no reemplazan a los artefactos fuente. Si existe conflicto entre un generado y una fuente, gana el artefacto fuente.

## Etapa 5: Validar Readiness Despues del Analisis

Cuando el agente de analisis diga que el proyecto esta listo, validar antes de iniciar implementacion.

### Estado de Readiness

Abrir:

`projects/<project-slug>/PROJECT_STATE.yaml`

Debe contener:

```yaml
development_readiness:
  status: ready
  blocking_definition_gaps: []
```

Tambien debe identificar el primer modulo:

```yaml
development_readiness:
  ready_to_start_module: <module-id>
```

Si `blocking_definition_gaps` no esta vacio, la implementacion no debe iniciar.

### Fuente de Verdad del Proyecto

Abrir:

`projects/<project-slug>/SOURCE_OF_TRUTH.yaml`

Debe listar artefactos autoritativos para:

- Requerimiento de negocio.
- Project brief.
- Estado del proyecto.
- Definicion de producto.
- Mapa de capacidades.
- Definicion de dominio.
- Arquitectura.
- Requerimientos.
- Contratos.
- Framework MVP.
- Primer paquete de modulo.

Si un artefacto es suficientemente importante para que un agente de implementacion lo cargue, debe estar listado en `SOURCE_OF_TRUTH.yaml`.

### Paquete del Primer Modulo

El primer modulo MVP debe incluir:

- `module-definition.yaml`
- `domain-model.md`
- `api-contract.openapi.yaml`
- `database-migration-plan.md`
- `ui-screen-map.md`
- `security-and-audit-rules.md`
- `test-plan.md`
- `traceability.yaml`

La ruta del paquete debe estar documentada en `PROJECT_STATE.yaml`.

### Trazabilidad Requerida

El agente debe mostrar trazabilidad desde:

- Requerimiento de negocio.
- Capacidad.
- Actor o usuario.
- Bounded context.
- API o evento.
- UI o workflow.
- Regla de seguridad y auditoria.
- Prueba.

Si esta cadena no es visible, el proyecto no esta listo.

### Validacion Agent-Agnostic

El proyecto no debe requerir un agente de IA, asistente de codigo, proveedor de modelo, proveedor de nube o runtime especifico para entender o implementar la solucion.

Usar el estandar:

`nexora-framework/02-standards/standards/agent-agnostic-standard.yaml`

Expectativa:

- Ningun artefacto fuente vuelve obligatorio un agente o proveedor especifico.
- Cualquier helper de herramienta es opcional y se trata como adaptador.
- Otro agente capaz puede iniciar usando solo archivos del repositorio.

### Validacion Estructural

La raiz del proyecto no debe acumular carpetas arbitrarias.

La raiz esperada del proyecto contiene:

- Archivos de control.
- Carpetas numeradas.
- `99-legacy/` solo para material archivado.

Si un artefacto nuevo no encaja, debe ir dentro de la carpeta numerada mas cercana y se debe actualizar `SOURCE_OF_TRUTH.yaml`.

## Etapa 6: Que Debe Reportar el Agente al Finalizar Analisis

Al terminar el analisis, el agente debe producir un reporte corto en la conversacion y actualizaciones durables en archivos del repositorio.

El reporte debe incluir:

- Project slug.
- Archivo `BUSINESS_REQUIREMENT.md` usado.
- Si los archivos de control estan completos.
- Modulos MVP creados.
- Primer modulo a implementar.
- Ruta del primer paquete de modulo.
- Estado de readiness.
- Bloqueos restantes, si existen.
- Validaciones ejecutadas.
- Hash de commit, si se hizo commit.

Ejemplo:

```text
Analisis completado para projects/<project-slug>/.

Listo para iniciar implementacion: si.
Primer modulo: MVP-MOD-001 Platform Foundation.
Paquete del modulo:
projects/<project-slug>/06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/

Validado:
- PROJECT_STATE.yaml tiene development_readiness.status: ready.
- blocking_definition_gaps esta vacio.
- SOURCE_OF_TRUTH.yaml lista artefactos autoritativos.
- El primer paquete de modulo incluye definicion, modelo de dominio, contrato API, plan de base de datos, mapa UI, reglas de seguridad, plan de pruebas y trazabilidad.
- Validacion agent-agnostic aprobada.

Commit: <hash>
```

## Etapa 7: Iniciar el Agente de Desarrollo

El agente de desarrollo implementa un modulo MVP seleccionado.

El agente de desarrollo no debe volver a analizar todo el producto, salvo que encuentre una inconsistencia bloqueante.

Debe implementar desde el paquete del modulo y actualizar pruebas, trazabilidad y estado.

### Instruccion Inicial para el Agente de Desarrollo

Usar esta instruccion:

```text
Lee AGENT_BOOTSTRAP.md.
Lee NEXORA_FRAMEWORK_USAGE_GUIDE.md.
Lee SOURCE_OF_TRUTH.yaml y PROJECT_STATE.yaml.
Carga SOURCE_OF_TRUTH.yaml y PROJECT_STATE.yaml del proyecto objetivo.
Resuelve la version vigente de BUSINESS_REQUIREMENT usando el indice del proyecto si existe.

Proyecto objetivo:
projects/<project-slug>/

Modulo objetivo:
<module-id>

Inicia desde el paquete de modulo documentado en PROJECT_STATE.yaml y SOURCE_OF_TRUTH.yaml.

No redisenes el producto.
No omitas el paquete de definicion del modulo.
No infieras requerimientos desde el historial de conversacion.
No implementes si hay una version nueva de BUSINESS_REQUIREMENT sin analisis de impacto resuelto.
Implementa solo el slice del modulo seleccionado.
Actualiza pruebas, trazabilidad y PROJECT_STATE.yaml despues de avances significativos.
Preserva limites agent-agnostic, provider-agnostic y cloud-agnostic salvo que los artefactos fuente indiquen lo contrario.
```

Reemplazar `<project-slug>` y `<module-id>` por los valores reales.

### Orden Obligatorio de Carga para Desarrollo

El agente de desarrollo debe cargar:

1. `AGENT_BOOTSTRAP.md`
2. `NEXORA_FRAMEWORK_USAGE_GUIDE.md`
3. `SOURCE_OF_TRUTH.yaml`
4. `PROJECT_STATE.yaml`
5. `nexora-framework/02-standards/standards/agent-agnostic-standard.yaml`
6. `nexora-framework/02-standards/standards/business-requirement-versioning-standard.yaml`
7. `nexora-framework/02-standards/standards/open-source-first-security-quality-standard.yaml`
8. `nexora-framework/02-standards/standards/framework-feedback-continuous-improvement-standard.yaml`
9. `nexora-framework/05-prompts/prompts/business-requirement-impact-prompts.yaml`
10. `nexora-framework/05-prompts/prompts/security-quality-gate-prompts.yaml`
11. `nexora-framework/05-prompts/prompts/framework-feedback-prompts.yaml`
12. `projects/<project-slug>/00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.yaml`, si existe
13. `projects/<project-slug>/BUSINESS_REQUIREMENT.md`
14. `projects/<project-slug>/PROJECT_BRIEF.md`
15. `projects/<project-slug>/SOURCE_OF_TRUTH.yaml`
16. `projects/<project-slug>/PROJECT_STATE.yaml`
17. `projects/<project-slug>/ORDERED_DEVELOPMENT_GUIDE.md`
18. `module-definition.yaml` del modulo objetivo
19. `domain-model.md` del modulo objetivo
20. `api-contract.openapi.yaml` del modulo objetivo
21. `database-migration-plan.md` del modulo objetivo
22. `ui-screen-map.md` del modulo objetivo
23. `security-and-audit-rules.md` del modulo objetivo
24. `test-plan.md` del modulo objetivo
25. `traceability.yaml` del modulo objetivo

El agente tambien debe cargar cualquier artefacto de producto, dominio, arquitectura, contrato o QA referenciado por el paquete del modulo.

### Reglas de Trabajo para Desarrollo

El agente de desarrollo debe:

- Implementar solo el modulo seleccionado.
- Preservar propiedad de bounded contexts.
- Respetar reglas de negocio y auditoria.
- Mantener contratos sincronizados con implementacion.
- Agregar o actualizar pruebas.
- Preferir frameworks y herramientas open source.
- Ejecutar o documentar SAST/static analysis, DAST cuando aplique, analisis de dependencias, vulnerabilidades, secrets scan y cobertura para cada backlog que cambia codigo.
- Escribir evidencia en `08-qa/security-quality/<backlog-item-id>/`.
- Registrar feedback de framework en `08-qa/framework-feedback/` cuando la ejecucion revele ambiguedades, plantillas faltantes, prompts faltantes, trabajo repetitivo o mejoras reutilizables.
- Actualizar trazabilidad.
- Actualizar notas de operaciones cuando cambie el comportamiento runtime.
- Actualizar estado del proyecto despues de avances significativos.

El agente de desarrollo no debe:

- Iniciar desde un scaffold vacio sin leer el paquete del modulo.
- Inventar ids de capacidades.
- Mover artefactos especificos del proyecto a la raiz del repositorio.
- Tratar artefactos generados como autoritativos por encima de fuentes.
- Introducir dependencia obligatoria a un agente, proveedor o nube especifica.
- Introducir dependencias propietarias obligatorias sin ADR de excepcion.
- Saltarse boundaries de adaptadores o anti-corruption layers.

## Etapa 8.1: Feedback de Mejora Continua del Framework

Al finalizar analisis, validacion, implementacion de backlog, cierre de modulo o release readiness, el agente debe revisar si la ejecucion dejo aprendizaje reutilizable para mejorar el framework.

Si no hay aprendizaje util, no debe inventar feedback.

Si hay aprendizaje util, debe crear un item en:

`projects/<project-slug>/08-qa/framework-feedback/`

y actualizar:

`projects/<project-slug>/08-qa/framework-feedback/framework-feedback-index.yaml`

Si el aprendizaje aplica a varios proyectos o al framework en general, el agente puede crear una propuesta en:

`nexora-framework/07-governance/framework-improvement-backlog/items/`

El agente no debe implementar esa mejora al framework salvo que Nexora lo asigne explicitamente como backlog de framework.

## Etapa 8: Validar Handoff de Desarrollo

Antes de iniciar desarrollo, confirmar:

- El modulo objetivo existe.
- La definicion del modulo permite iniciar implementacion.
- El modulo tiene todos los archivos requeridos.
- El contrato API existe.
- El plan de pruebas existe.
- Las reglas de seguridad y auditoria existen.
- La trazabilidad existe.
- El estado del proyecto no tiene bloqueos de definicion.

Para Healthcare Operations Platform, el primer objetivo de desarrollo es:

`projects/healthcare-operations-platform/06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/`

## Etapa 9: Control de Cambios

Si un agente encuentra una definicion faltante o contradictoria durante implementacion:

1. Detener el slice de implementacion afectado.
2. Registrar el gap en `PROJECT_STATE.yaml`.
3. Actualizar el artefacto fuente correspondiente.
4. Agregar o actualizar un ADR si cambia arquitectura.
5. Actualizar `SOURCE_OF_TRUTH.yaml` si se crea un nuevo artefacto autoritativo.
6. Reanudar implementacion solo cuando el modulo vuelva a estar listo.

No ocultar gaps de definicion dentro del codigo.

## Etapa 10: Disciplina Git

Los agentes deben commitear hitos coherentes.

Limites de commit recomendados:

- Actualizacion de framework o estandar.
- Nuevo scaffold de proyecto.
- Etapa de analisis completada.
- Framework MVP completado.
- Paquete de modulo completado.
- Slice de implementacion.
- Actualizacion de pruebas o validacion.

Cada commit debe dejar el repositorio en un estado legible. Si una validacion no pudo ejecutarse, la respuesta final debe decirlo.

## Checklist Completo

Usar este checklist al aplicar el framework.

Readiness de analisis:

- `BUSINESS_REQUIREMENT.md` existe, fue proporcionado por el solicitante y tiene contenido suficiente.
- `PROJECT_BRIEF.md` estructura el requerimiento.
- La carpeta del proyecto sigue el estandar.
- `SOURCE_OF_TRUTH.yaml` existe.
- `PROJECT_STATE.yaml` existe.
- Existe definicion de producto.
- Existe mapa de capacidades.
- Existe mapa de dependencias de capacidades.
- Existe foundation de dominio.
- Existe baseline de arquitectura.
- Existen requerimientos y contratos.
- Existe framework MVP.
- Existe primer paquete de modulo.
- Existe trazabilidad.
- `blocking_definition_gaps` esta vacio.

Handoff de desarrollo:

- El modulo objetivo esta seleccionado.
- El paquete de modulo tiene todos los archivos requeridos.
- Los artefactos fuente del modulo estan en source of truth o referenciados por el framework MVP.
- Reglas de seguridad, auditoria y privacidad son visibles.
- El plan de pruebas es visible.
- El agente de desarrollo tiene orden de carga exacto.
- El alcance de implementacion esta limitado a un slice de modulo.

## Comandos Minimos de Validacion Local

Desde la raiz del repositorio:

```powershell
git status --short
```

```powershell
python -c "import pathlib,yaml; files=['SOURCE_OF_TRUTH.yaml','PROJECT_STATE.yaml','PROJECT_MANIFEST.yaml']; [yaml.safe_load(pathlib.Path(f).read_text()) for f in files]; print('YAML OK')"
```

Tambien se debe ejecutar una auditoria textual del repositorio para detectar nombres conocidos de agentes, asistentes, proveedores de modelos y runtimes de plataforma.

La auditoria no debe encontrar coincidencias en artefactos fuente, salvo que un ADR aprobado documente una excepcion temporal.

## Principio Final

El framework Nexora funciona correctamente cuando una persona proporciona un requerimiento de negocio, pide a un agente de analisis que complete la definicion del proyecto, valida readiness y despues pide a un agente de desarrollo diferente que implemente el primer modulo MVP sin perder contexto.

Si el siguiente agente necesita una conversacion previa para entender que hacer, el framework no fue aplicado completamente.

# Knowledge Layer

La capa `knowledge/` contiene el **Product Knowledge Graph** de Nexora.

Su objetivo es convertir la especificación en un conjunto de nodos trazables, versionados y relacionados entre sí. Cada nodo representa un artefacto del producto: capacidad de negocio, dominio, regla, historia, API, entidad, evento, pantalla, prueba, decisión arquitectónica o playbook.

## Principios

- Todo artefacto importante debe tener un identificador único.
- Todo cambio debe poder rastrearse hacia negocio, arquitectura, datos, APIs, UI, pruebas y documentación.
- Ningún agente debe improvisar contexto: debe consultar primero el grafo de conocimiento.
- El grafo debe ser legible por humanos y consumible por agentes de IA.

## Estructura

```text
knowledge/
├── graph/
├── indexes/
├── nodes/
├── relations/
├── impact-analysis/
└── context-builder/
```

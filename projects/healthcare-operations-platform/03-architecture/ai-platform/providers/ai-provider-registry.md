# AI Provider Registry

Nexora debe soportar múltiples proveedores mediante adaptadores.

## Familias de proveedores

- Cloud LLM providers.
- Modelos open-source autoalojados.
- OCR providers.
- Speech providers.
- Embedding providers.
- Vector database providers.

## Regla de arquitectura

Ningún caso de uso debe invocar directamente un SDK de proveedor. Toda integración debe pasar por el `AI Gateway` y sus puertos/adaptadores.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: AI-PROV-REG-001
  type: ai_provider_registry
  version: 0.21.0
providers:
  llm:
  - id: llm-provider-api
    type: generic_llm_api
    deployment: cloud_or_self_hosted
  - id: llm-local-runtime
    type: local_model_runtime
    deployment: on_premise
  embeddings:
  - id: embeddings-provider-api
    type: generic_embedding_api
  - id: embeddings-local-runtime
    type: local_embedding_model
  vector_store:
  - id: pgvector
    type: postgresql_extension
  - id: qdrant
    type: vector_database
  - id: opensearch-vector
    type: search_engine_vector
```

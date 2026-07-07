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

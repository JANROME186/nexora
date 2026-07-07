# RAG Architecture

RAG será usado para consultas sobre conocimiento interno, manuales, guías operativas, documentación del producto y soporte contextual.

## Fuentes iniciales

- Manuales internos de Nexora.
- Base de conocimiento del laboratorio.
- Catálogos configurados.
- Ayuda contextual del sistema.
- Documentación regulatoria/country packs autorizada.

## Restricciones

- No usar RAG para emitir diagnóstico automático.
- Toda fuente debe estar versionada y tener propietario.
- Las respuestas deben citar fuente interna cuando aplique.
- Debe existir fallback a búsqueda tradicional.

---
id: TECH-STORAGE-001
name: Storage Abstraction Strategy
version: 0.18.0
status: Draft
owner: Architecture
---

# Storage Abstraction Strategy

Nexora debe abstraer el almacenamiento para evitar dependencia directa de un proveedor.

## Tipos de almacenamiento

| Tipo | Uso | Abstracción |
|---|---|---|
| Relacional | Operación transaccional | Repository / Unit of Work |
| Documental | Documentos clínicos, snapshots, plantillas | Document Repository |
| Object Storage | PDFs, imágenes, adjuntos, DICOM si aplica | Object Storage Port |
| Cache | Sesiones, tokens temporales, lecturas frecuentes | Cache Port |
| Search | Búsqueda avanzada | Search Port |

## Object Storage

El dominio nunca debe conocer S3, Azure Blob, GCS o MinIO directamente.

Debe conocer únicamente un puerto como:

```text
ObjectStoragePort
  putObject()
  getObject()
  deleteObject()
  generateSignedUrl()
```

## Implementaciones iniciales

- MinIO para local y on-premise.
- S3-compatible para cloud.
- Adaptadores futuros para Azure Blob y GCS.

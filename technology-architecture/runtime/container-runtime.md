---
id: TECH-RUNTIME-001
name: Container Runtime Standard
version: 0.18.0
status: Draft
owner: Platform Engineering
---

# Container Runtime Standard

Nexora adopta contenedores OCI como formato estándar de empaquetado.

## Reglas

- Cada unidad desplegable debe tener una imagen reproducible.
- Las imágenes deben ser pequeñas y seguras.
- No se deben incluir secretos dentro de imágenes.
- Las imágenes deben ejecutar procesos no privilegiados cuando sea posible.
- Cada servicio debe soportar variables de entorno para configuración.
- Cada imagen debe exponer health checks o endpoints equivalentes.
- La misma imagen debe poder ejecutarse localmente, en Swarm o Kubernetes.

## Estructura esperada por servicio

```text
/service
  Dockerfile
  .dockerignore
  package.json
  src/
  test/
```

## Versionado de imágenes

Las imágenes deben etiquetarse por:

- Versión semántica.
- SHA de commit.
- Ambiente destino cuando aplique.

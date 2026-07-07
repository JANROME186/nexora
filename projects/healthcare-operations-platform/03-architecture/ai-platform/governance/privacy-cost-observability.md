# AI Privacy, Cost & Observability

## Privacidad

- Minimización de datos antes de invocar IA.
- No enviar datos personales si el caso de uso puede resolverse con datos anonimizados.
- Registro del propósito de procesamiento.
- Configuración por país y tenant.

## Costos

Cada invocación debe registrar:

- Tenant.
- Sucursal.
- Usuario o proceso.
- Capacidad de IA.
- Proveedor/modelo.
- Unidades consumidas.
- Costo estimado.
- Resultado.

## Observabilidad

- Latencia por proveedor.
- Errores por capacidad.
- Calidad percibida.
- Tasa de fallback.
- Reintentos.
- Bloqueos por guardrails.

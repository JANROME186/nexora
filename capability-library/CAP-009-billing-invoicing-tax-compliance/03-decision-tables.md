# 03 Decision Tables

## DT-BILL-001 Invoice eligibility

| Venta pagada | Datos fiscales completos | País soportado | Resultado |
|---|---|---|---|
| Sí | Sí | Sí | Permitir emisión fiscal |
| Sí | No | Sí | Solicitar datos fiscales |
| Sí | Sí | No | Generar recibo interno |
| No | Cualquiera | Cualquiera | Bloquear emisión |

## DT-BILL-002 Cancellation handling

| Estado factura | Timbrada/emitida externamente | Motivo válido | Acción |
|---|---|---|---|
| Draft | No | No requerido | Eliminar borrador |
| Issued | Sí | Sí | Solicitar cancelación fiscal |
| Issued | Sí | No | Bloquear cancelación |
| Failed | No | Sí | Marcar como anulada administrativa |
| Cancelled | Cualquiera | Cualquiera | Bloquear operación |

## DT-BILL-003 Document delivery

| Estado fiscal | Canal paciente | Acción |
|---|---|---|
| Issued | Email | Enviar PDF/XML/representación fiscal |
| Issued | Portal | Publicar documento |
| Pending | Cualquiera | Mostrar pendiente |
| Failed | Cualquiera | Notificar error interno, no enviar documento fiscal |

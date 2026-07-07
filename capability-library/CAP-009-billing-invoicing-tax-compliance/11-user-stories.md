# 11 User Stories

## Fiscal profile

### US-BILL-001 Registrar datos fiscales
Como recepcionista quiero registrar los datos fiscales de un paciente o empresa para poder emitir facturas correctamente.

**Acceptance Criteria**
- Debe validar campos obligatorios según país.
- Debe guardar snapshot y auditoría de cambios.
- Debe asociarse a paciente, empresa o pagador.

### US-BILL-002 Validar datos fiscales
Como administrador quiero validar los datos fiscales antes de emitir una factura para reducir rechazos del proveedor fiscal.

## Invoice issuing

### US-BILL-003 Emitir factura desde una venta pagada
Como cajero quiero emitir una factura desde una venta pagada para entregar comprobante fiscal al paciente.

**Acceptance Criteria**
- Solo ventas confirmadas pueden facturarse.
- La factura debe conservar relación con venta, pago y orden.
- Debe generar evento InvoiceIssueRequested.

### US-BILL-004 Reintentar emisión fallida
Como supervisor quiero reintentar una factura fallida sin duplicar folios ni documentos.

### US-BILL-005 Consultar facturas
Como usuario autorizado quiero buscar facturas por paciente, fecha, sucursal, estado o folio.

## Cancellation

### US-BILL-006 Cancelar factura
Como supervisor quiero cancelar una factura indicando motivo para cumplir con reglas fiscales y auditoría.

### US-BILL-007 Consultar acuse de cancelación
Como administrador quiero consultar el acuse de cancelación para comprobar el estado fiscal del documento.

## Delivery

### US-BILL-008 Descargar documentos fiscales
Como paciente quiero descargar mi factura desde el portal para conservar mis comprobantes.

### US-BILL-009 Enviar factura por correo
Como cajero quiero enviar la factura por correo al paciente.

## Configuration

### US-BILL-010 Configurar series y folios
Como administrador quiero configurar series y folios por sucursal para controlar la emisión documental.

### US-BILL-011 Configurar proveedor fiscal
Como administrador técnico quiero configurar el adaptador fiscal por país o laboratorio.

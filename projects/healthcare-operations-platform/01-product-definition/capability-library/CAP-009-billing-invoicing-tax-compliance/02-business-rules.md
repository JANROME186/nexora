# 02 Business Rules

| ID | Regla | Prioridad |
|---|---|---:|
| BR-BILL-001 | Una factura solo puede generarse a partir de una venta confirmada o pago autorizado. | Alta |
| BR-BILL-002 | Una factura debe pertenecer a un laboratorio y, cuando aplique, a una sucursal. | Alta |
| BR-BILL-003 | Los datos fiscales obligatorios dependen del Country Pack activo. | Alta |
| BR-BILL-004 | Una factura emitida no puede editarse; cualquier corrección debe realizarse mediante cancelación, sustitución o nota de crédito según la política fiscal. | Alta |
| BR-BILL-005 | Cada factura debe estar vinculada a al menos una venta, orden, pago o concepto facturable. | Alta |
| BR-BILL-006 | La cancelación fiscal requiere motivo, usuario autorizado y validación del Country Pack. | Alta |
| BR-BILL-007 | Los folios deben ser únicos por laboratorio, sucursal, serie y tipo de documento. | Alta |
| BR-BILL-008 | Los impuestos se calculan por concepto, no únicamente por total global. | Alta |
| BR-BILL-009 | El sistema debe conservar la versión del XML/JSON/acuse/documento fiscal recibido del proveedor. | Alta |
| BR-BILL-010 | La entrega al paciente solo debe ocurrir cuando el documento esté emitido o confirmado según el país. | Media |
| BR-BILL-011 | Una nota de crédito debe referenciar el documento que afecta. | Alta |
| BR-BILL-012 | Los documentos fiscales deben respetar retención, privacidad y auditoría del país aplicable. | Alta |
| BR-BILL-013 | La facturación electrónica debe ejecutarse mediante adaptador de proveedor, nunca desde el dominio fiscal. | Alta |
| BR-BILL-014 | Los errores del proveedor fiscal no deben duplicar facturas ni folios. | Alta |
| BR-BILL-015 | Cada intento de emisión debe ser idempotente por venta y solicitud fiscal. | Alta |

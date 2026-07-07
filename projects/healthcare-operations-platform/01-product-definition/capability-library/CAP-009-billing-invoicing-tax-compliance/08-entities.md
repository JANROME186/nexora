# 08 Entities

| Entity | Type | Description |
|---|---|---|
| Invoice | Aggregate Root | Documento comercial/fiscal emitido por venta u orden. |
| InvoiceLine | Entity | Concepto facturado. |
| TaxLine | Entity | Impuesto aplicado por línea. |
| FiscalProfile | Aggregate Root | Datos fiscales de paciente, empresa o pagador. |
| FiscalAddress | Entity | Dirección fiscal validada por país. |
| FiscalDocument | Entity | XML, PDF, JSON, acuse o representación fiscal. |
| FiscalProviderSubmission | Entity | Intento de emisión/cancelación ante proveedor. |
| FolioSequence | Aggregate Root | Serie y folio por laboratorio/sucursal/tipo. |
| TaxConfiguration | Aggregate Root | Configuración de impuestos por país, concepto y vigencia. |
| CreditNote | Aggregate Root | Documento que afecta una factura previa. |
| InvoiceDelivery | Entity | Registro de entrega por email, portal o webhook. |
| CancellationRequest | Entity | Solicitud de cancelación con motivo, usuario y respuesta. |

## Initial relational tables

- billing_invoice
- billing_invoice_line
- billing_tax_line
- billing_fiscal_profile
- billing_fiscal_address
- billing_fiscal_document
- billing_provider_submission
- billing_folio_sequence
- billing_tax_configuration
- billing_credit_note
- billing_invoice_delivery
- billing_cancellation_request

# 09 Domain Events

| Event ID | Event Name | Trigger |
|---|---|---|
| EVT-BILL-001 | InvoiceIssueRequested | Usuario solicita factura. |
| EVT-BILL-002 | InvoiceDraftCreated | Se crea factura preliminar. |
| EVT-BILL-003 | TaxCalculated | Se calculan impuestos. |
| EVT-BILL-004 | FolioReserved | Se reserva serie/folio. |
| EVT-BILL-005 | InvoiceSubmittedToProvider | Se envía al proveedor fiscal. |
| EVT-BILL-006 | InvoiceIssued | El proveedor confirma emisión. |
| EVT-BILL-007 | InvoiceIssueFailed | El proveedor rechaza o falla. |
| EVT-BILL-008 | InvoiceDeliveryRequested | Se solicita entrega al paciente/empresa. |
| EVT-BILL-009 | InvoiceDelivered | Documento entregado. |
| EVT-BILL-010 | InvoiceCancellationRequested | Se solicita cancelación. |
| EVT-BILL-011 | InvoiceCancelled | Proveedor confirma cancelación. |
| EVT-BILL-012 | InvoiceCancellationRejected | Proveedor rechaza cancelación. |
| EVT-BILL-013 | CreditNoteIssued | Se genera nota de crédito. |
| EVT-BILL-014 | FiscalProfileUpdated | Se actualiza perfil fiscal. |

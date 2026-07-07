# 06 Event Storming

## Events

1. SaleConfirmed
2. FiscalProfileRequested
3. FiscalProfileCompleted
4. InvoiceIssueRequested
5. InvoiceDraftCreated
6. TaxCalculated
7. FolioReserved
8. FiscalProviderSubmissionRequested
9. InvoiceIssued
10. InvoiceIssueFailed
11. InvoiceDeliveryRequested
12. InvoiceDelivered
13. InvoiceCancellationRequested
14. InvoiceCancelled
15. InvoiceCancellationRejected
16. CreditNoteIssued
17. FiscalDocumentArchived

## Commands

- RequestInvoiceIssue
- CompleteFiscalProfile
- CalculateTax
- ReserveFolio
- SubmitInvoiceToProvider
- RetryInvoiceIssue
- RequestInvoiceCancellation
- IssueCreditNote
- DeliverFiscalDocument

## Hotspots

- Reglas fiscales variables por país.
- Idempotencia al reintentar emisión.
- Sincronización con caja y cancelaciones.
- Conservación de documentos fiscales.
- Manejo de proveedores externos caídos.

# 19 Traceability

| Source | Target |
|---|---|
| CAP-009 | BR-BILL-001..015 |
| BR-BILL-001 | US-BILL-003, API POST /billing/invoices/{id}/issue |
| BR-BILL-004 | US-BILL-006, Invoice State Machine |
| BR-BILL-007 | FolioSequence, EVT-BILL-004 |
| BR-BILL-013 | FiscalProviderGateway, Integration Architecture |
| Invoice | billing_invoice, Results/Cash dependencies |
| InvoiceIssued | Patient Portal, Email Delivery, Audit Trail |
| Country Pack | FiscalProfile validation, TaxConfiguration |

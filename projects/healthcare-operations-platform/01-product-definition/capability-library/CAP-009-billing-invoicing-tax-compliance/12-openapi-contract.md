# 12 OpenAPI Contract Scope

Contrato inicial ubicado en:

`contracts/billing/openapi.md`

## Endpoints MVP

- `POST /billing/fiscal-profiles`
- `GET /billing/fiscal-profiles/{id}`
- `PUT /billing/fiscal-profiles/{id}`
- `POST /billing/invoices`
- `GET /billing/invoices`
- `GET /billing/invoices/{id}`
- `POST /billing/invoices/{id}/issue`
- `POST /billing/invoices/{id}/retry`
- `POST /billing/invoices/{id}/cancel`
- `GET /billing/invoices/{id}/documents`
- `POST /billing/invoices/{id}/deliver`
- `GET /billing/folio-sequences`
- `POST /billing/folio-sequences`
- `GET /billing/tax-configurations`

## Contract rules

- OpenAPI es fuente de verdad.
- Toda respuesta de error usa esquema estándar.
- Los endpoints deben incluir `tenantId` resuelto por contexto, no por cuerpo público.
- Toda operación fiscal debe ser idempotente donde aplique.

# 07 Domain Model

## Aggregate Roots

- Invoice
- FiscalProfile
- FolioSequence
- TaxConfiguration
- CreditNote

## Entities

- InvoiceLine
- TaxLine
- FiscalDocument
- FiscalProviderSubmission
- InvoiceDelivery
- CancellationRequest
- FiscalAddress

## Value Objects

- TaxIdentifier
- FiscalName
- FiscalRegime
- InvoiceNumber
- Folio
- Series
- Money
- TaxAmount
- Currency
- CountryCode
- ProviderReference

## Domain Services

- TaxCalculationService
- FiscalValidationService
- InvoiceNumberingService
- FiscalProviderGateway
- InvoiceDeliveryService

## Invariants

- Una factura emitida es inmutable.
- Un folio reservado no puede asignarse a otra factura.
- La factura conserva su snapshot fiscal aunque el perfil del paciente cambie después.
- El cálculo fiscal debe ser reproducible con la configuración vigente al momento de emisión.

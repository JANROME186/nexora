# Domain Model

## Aggregate Roots
- ResultOrder
- ResultPanel
- ResultReport
- CriticalAlert

## Entities
- ResultItem
- ResultVersion
- ResultValidation
- ResultDelivery
- ReportTemplate
- ReportSignature

## Value Objects
- ResultValue
- ReferenceRange
- CriticalRange
- MeasurementUnit
- ResultInterpretation
- DeliveryChannel

## Invariants
- Published reports must be associated with a validated or release-approved result set.
- Amendments must not overwrite prior released versions.
- Critical alerts must remain auditable even after acknowledgment.

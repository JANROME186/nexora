# Entities

| Entity | Type | Description |
|---|---|---|
| ResultOrder | Aggregate Root | Clinical result lifecycle for an order. |
| ResultPanel | Entity | Group of related result items. |
| ResultItem | Entity | Individual analyte/result value. |
| ResultVersion | Entity | Immutable snapshot of released/amended results. |
| ResultValidation | Entity | Technical or clinical validation record. |
| CriticalAlert | Aggregate Root | Critical result alert and acknowledgment lifecycle. |
| ResultReport | Aggregate Root | Generated clinical report. |
| ReportSignature | Entity | Professional signature metadata. |
| ResultDelivery | Entity | Delivery record to patient, doctor or external channel. |
| ReportTemplate | Entity | Configurable report template. |

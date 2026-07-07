# Event Storming

## Domain Events
- SampleAcceptedForProcessing
- ResultEntryStarted
- ResultReceivedFromAnalyzer
- ResultEnteredManually
- ResultCalculated
- ReferenceRangeEvaluated
- CriticalValueDetected
- CriticalAlertAcknowledged
- ResultTechnicallyValidated
- ResultClinicallyValidated
- ResultRejected
- ResultReportGenerated
- ResultReportSigned
- ResultReleased
- ResultDelivered
- ResultAmended
- ResultVersionCreated

## Commands
- StartResultEntry
- ImportAnalyzerResult
- EnterManualResult
- RecalculateResult
- ValidateResultTechnically
- ValidateResultClinically
- GenerateResultReport
- SignResultReport
- ReleaseResult
- DeliverResult
- AmendResult

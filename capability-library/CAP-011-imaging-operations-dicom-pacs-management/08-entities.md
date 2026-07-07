# Entities

| Entity | Type | Description |
|---|---|---|
| ImagingStudy | Transaction Data | Imaging study linked to order and patient. |
| ImagingAppointment | Transaction Data | Scheduled imaging event. |
| ImagingService | Master Data | Imaging service configured in catalog. |
| ImagingRoom | Master Data | Physical or logical imaging room. |
| Modality | Master Data | Imaging modality/device. |
| ModalityMaintenance | Transaction Data | Maintenance history and availability. |
| AcquisitionTask | Transaction Data | Technician work item for acquisition. |
| DicomStudy | Transaction Data | DICOM study metadata and state. |
| DicomSeries | Transaction Data | DICOM series metadata. |
| DicomInstance | Transaction Data | Individual DICOM object metadata. |
| PacsObjectReference | Transaction Data | Storage reference to PACS/object storage. |
| DicomReconciliationTask | Transaction Data | Conflict/unmatched review task. |
| ImagingReport | Transaction Data | Radiology report. |
| ImagingReportVersion | Transaction Data | Versioned report content. |
| ImagingFinding | Transaction Data | Structured finding or observation. |
| ReportSignature | Transaction Data | Signature metadata. |
| ViewerAccessLink | Transaction Data | Secure image/report access link. |
| ViewerAccessAudit | Audit Data | Viewer access record. |
| ImagingCriticalAlert | Operational Event | Critical finding alert. |
| ImagingProtocol | Reference Data | Acquisition/reporting protocol. |

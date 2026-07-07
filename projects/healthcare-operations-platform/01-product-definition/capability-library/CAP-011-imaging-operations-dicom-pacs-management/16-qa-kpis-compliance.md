# QA, KPIs & Compliance

## QA Strategy

- Contract tests for Imaging API.
- Authorization tests for patient, physician, radiologist and admin access.
- State transition tests for imaging study/report/DICOM lifecycle.
- DICOM reconciliation validation tests.
- Viewer link expiration and revocation tests.
- Report immutability and amendment audit tests.
- Performance tests for worklists and metadata search.

## KPIs

- Average time from appointment check-in to acquisition completion.
- Average time from acquisition to report signature.
- Percentage of unmatched DICOM studies.
- Radiologist turnaround time.
- Viewer link access success rate.
- Critical finding notification time.
- Modality utilization rate.
- No-show rate by modality and branch.

## Compliance Considerations

- Imaging reports are clinical records.
- DICOM metadata may contain protected personal/clinical data.
- Access to images must be audited.
- Report amendments must preserve previous versions.
- Retention policies must be configurable by country pack and organization policy.
- Secure sharing must avoid public, non-expiring URLs.

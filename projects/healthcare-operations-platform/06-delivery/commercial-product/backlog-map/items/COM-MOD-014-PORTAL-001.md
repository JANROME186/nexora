---
artifact:
  id: HOP-BACKLOG-ITEM-COM-MOD-014-PORTAL-001
  type: backlog-item-record
  status: closed
  optimization: atomic_context
---

# COM-MOD-014-PORTAL-001 Backlog Item

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: COM-MOD-014-PORTAL-001
name: Imaging study delivery views
status: closed
module_id: COM-MOD-014
module_name: Imaging Operations
release: REL-004
closed_under_backlog_item: HOP-HARD-APP-001
closed_under_module: HOP-FINAL-HARDENING
summary: 'Patient-portal and doctor-portal each gained a read-only Imaging tab consuming
  the existing BCM-IMG-007/BCM-IMG-008 backend endpoints (RadiologySignatureController,
  ImagingStudyDeliveryController), scoped to the caller''s own delivered studies via
  a new callerRoleCode/callerId self-access convention mirroring patientResultHistoryApi.
  The backend was hardened to actually enforce that scoping (previously these endpoints
  had no patient/doctor-role-scoped authorization beyond the generic tenant header):
  new PORTAL_PATIENT_IMAGING_VIEW/PORTAL_DOCTOR_IMAGING_VIEW permission codes, new
  HopAuthorizationInterceptor self-access bypass blocks, and new ImagingStudyDeliveryService/RadiologySignatureService
  overloads that verify package/report ownership (patient self-match or ReferringDoctorAuthorizationPort
  referral check), throwing a new ImagingAccessDeniedException (403) on mismatch.'
evidence:
  qa: 08-qa/qa/final-hardening/HOP-HARD-APP-001-validation.md
  security_quality: 08-qa/security-quality/HOP-HARD-APP-001/security-quality-evidence.md
```

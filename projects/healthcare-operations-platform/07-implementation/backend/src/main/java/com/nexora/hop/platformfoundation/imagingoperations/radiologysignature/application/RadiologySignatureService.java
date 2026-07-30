package com.nexora.hop.platformfoundation.imagingoperations.radiologysignature.application;

import com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.ReferringDoctorAuthorizationPort;
import com.nexora.hop.platformfoundation.imagingoperations.radiologysignature.domain.RadiologyReport;
import com.nexora.hop.platformfoundation.imagingoperations.radiologysignature.domain.RadiologyReportRepository;
import com.nexora.hop.platformfoundation.imagingoperations.shared.ImagingAccessDeniedException;
import com.nexora.hop.platformfoundation.imagingoperations.shared.ImagingErrorCode;
import com.nexora.hop.platformfoundation.imagingoperations.shared.ImagingNotFoundException;
import com.nexora.hop.platformfoundation.imagingoperations.studymanagement.domain.ImagingStudy;
import com.nexora.hop.platformfoundation.imagingoperations.studymanagement.domain.ImagingStudyRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class RadiologySignatureService {

    /** Mirrors {@code ResultHistoryService.REFERRING_DOCTOR_ROLE}; kept local for the same reason. */
    private static final String PATIENT_ROLE = "PATIENT";
    private static final String REFERRING_DOCTOR_ROLE = "REFERRING_DOCTOR";

    private final RadiologyReportRepository repository;
    private final ImagingStudyRepository studyRepository;
    private final ReferringDoctorAuthorizationPort referringDoctorAuthorizationPort;

    public RadiologySignatureService(
            RadiologyReportRepository repository,
            ImagingStudyRepository studyRepository,
            ReferringDoctorAuthorizationPort referringDoctorAuthorizationPort) {
        this.repository = repository;
        this.studyRepository = studyRepository;
        this.referringDoctorAuthorizationPort = referringDoctorAuthorizationPort;
    }

    public RadiologyReport createDraftReport(
            String tenantId,
            String studyId,
            String findingsText,
            String impressionText,
            String actorId) {
        String reportId = UUID.randomUUID().toString();
        Instant now = Instant.now();
        RadiologyReport report = new RadiologyReport(
                reportId, tenantId, studyId, actorId, findingsText, impressionText,
                "DRAFT", null, null, actorId, now, actorId, now
        );
        return repository.save(report);
    }

    public RadiologyReport signReport(String tenantId, String reportId, String actorId) {
        RadiologyReport existing = getReport(tenantId, reportId);
        Instant now = Instant.now();
        String signatureHash = UUID.nameUUIDFromBytes((reportId + ":" + actorId + ":" + now).getBytes()).toString();
        RadiologyReport signed = new RadiologyReport(
                existing.reportId(), existing.tenantId(), existing.studyId(), existing.radiologistId(),
                existing.findingsText(), existing.impressionText(), "FINAL_SIGNED", now, signatureHash,
                existing.createdBy(), existing.createdAt(), actorId, now
        );
        return repository.save(signed);
    }

    public RadiologyReport getReport(String tenantId, String reportId) {
        return repository.findById(tenantId, reportId)
                .orElseThrow(() -> new ImagingNotFoundException(ImagingErrorCode.REPORT_NOT_FOUND, "Radiology report " + reportId + " not found"));
    }

    /**
     * HOP-HARD-APP-001 imaging delivery hardening: patient-portal/doctor-portal self-access
     * variant. Resolves the report's owning patient through its study (reports do not carry
     * patientId directly), then applies the same PATIENT-ownership / REFERRING_DOCTOR-referral
     * check as {@link com.nexora.hop.platformfoundation.imagingoperations.studydelivery.application.ImagingStudyDeliveryService}.
     */
    public RadiologyReport getReport(String tenantId, String reportId, String callerRoleCode, String callerId) {
        RadiologyReport report = getReport(tenantId, reportId);
        enforceReportOwnership(tenantId, report.studyId(), callerRoleCode, callerId);
        return report;
    }

    public List<RadiologyReport> listReportsForStudy(String tenantId, String studyId) {
        return repository.findByStudyId(tenantId, studyId);
    }

    /** HOP-HARD-APP-001 imaging delivery hardening: see {@link #getReport(String, String, String, String)}. */
    public List<RadiologyReport> listReportsForStudy(
            String tenantId, String studyId, String callerRoleCode, String callerId) {
        enforceReportOwnership(tenantId, studyId, callerRoleCode, callerId);
        return listReportsForStudy(tenantId, studyId);
    }

    private void enforceReportOwnership(
            String tenantId, String studyId, String callerRoleCode, String callerId) {
        if (!PATIENT_ROLE.equals(callerRoleCode) && !REFERRING_DOCTOR_ROLE.equals(callerRoleCode)) {
            return;
        }
        String patientId = studyRepository.findById(tenantId, studyId)
                .map(ImagingStudy::patientId)
                .orElseThrow(() -> new ImagingNotFoundException(
                        ImagingErrorCode.STUDY_NOT_FOUND, "Imaging study " + studyId + " not found"));
        if (PATIENT_ROLE.equals(callerRoleCode) && !patientId.equals(callerId)) {
            throw new ImagingAccessDeniedException(
                    ImagingErrorCode.REPORT_ACCESS_DENIED,
                    "The requesting patient does not own the study behind this radiology report.");
        }
        if (REFERRING_DOCTOR_ROLE.equals(callerRoleCode)
                && !referringDoctorAuthorizationPort.isPatientReferredByDoctor(tenantId, callerId, patientId)) {
            throw new ImagingAccessDeniedException(
                    ImagingErrorCode.REPORT_ACCESS_DENIED,
                    "The requesting doctor has not referred the patient behind this radiology report.");
        }
    }
}

package com.nexora.hop.platformfoundation.imagingoperations.radiologysignature.application;

import com.nexora.hop.platformfoundation.imagingoperations.radiologysignature.domain.RadiologyReport;
import com.nexora.hop.platformfoundation.imagingoperations.radiologysignature.domain.RadiologyReportRepository;
import com.nexora.hop.platformfoundation.imagingoperations.shared.ImagingErrorCode;
import com.nexora.hop.platformfoundation.imagingoperations.shared.ImagingNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class RadiologySignatureService {

    private final RadiologyReportRepository repository;

    public RadiologySignatureService(RadiologyReportRepository repository) {
        this.repository = repository;
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

    public List<RadiologyReport> listReportsForStudy(String tenantId, String studyId) {
        return repository.findByStudyId(tenantId, studyId);
    }
}

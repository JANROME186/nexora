package com.nexora.hop.platformfoundation.imagingoperations.studydelivery.application;

import com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.ReferringDoctorAuthorizationPort;
import com.nexora.hop.platformfoundation.imagingoperations.shared.ImagingAccessDeniedException;
import com.nexora.hop.platformfoundation.imagingoperations.shared.ImagingErrorCode;
import com.nexora.hop.platformfoundation.imagingoperations.shared.ImagingNotFoundException;
import com.nexora.hop.platformfoundation.imagingoperations.studydelivery.domain.ImagingDeliveryPackage;
import com.nexora.hop.platformfoundation.imagingoperations.studydelivery.domain.ImagingDeliveryPackageRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ImagingStudyDeliveryService {

    /** Mirrors {@code ResultHistoryService.REFERRING_DOCTOR_ROLE}; kept local for the same reason. */
    private static final String PATIENT_ROLE = "PATIENT";
    private static final String REFERRING_DOCTOR_ROLE = "REFERRING_DOCTOR";

    private final ImagingDeliveryPackageRepository repository;
    private final ReferringDoctorAuthorizationPort referringDoctorAuthorizationPort;

    public ImagingStudyDeliveryService(
            ImagingDeliveryPackageRepository repository,
            ReferringDoctorAuthorizationPort referringDoctorAuthorizationPort) {
        this.repository = repository;
        this.referringDoctorAuthorizationPort = referringDoctorAuthorizationPort;
    }

    public ImagingDeliveryPackage createDeliveryPackage(
            String tenantId,
            String studyId,
            String patientId,
            String deliveryFormat,
            String actorId) {
        String packageId = UUID.randomUUID().toString();
        String portalToken = UUID.randomUUID().toString();
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(7 * 24 * 3600L); // 7 days
        ImagingDeliveryPackage deliveryPackage = new ImagingDeliveryPackage(
                packageId, tenantId, studyId, patientId, deliveryFormat,
                "PREPARED", portalToken, expiresAt, actorId, now, actorId, now
        );
        return repository.save(deliveryPackage);
    }

    public ImagingDeliveryPackage getDeliveryPackage(String tenantId, String packageId) {
        return repository.findById(tenantId, packageId)
                .orElseThrow(() -> new ImagingNotFoundException(ImagingErrorCode.DELIVERY_PACKAGE_NOT_FOUND, "Imaging delivery package " + packageId + " not found"));
    }

    /**
     * HOP-HARD-APP-001 imaging delivery hardening: patient-portal/doctor-portal self-access variant.
     * When {@code callerRoleCode} is {@code PATIENT}, the caller must own the package's patientId.
     * When it is {@code REFERRING_DOCTOR}, the caller must have referred the package's patient,
     * mirroring {@code ResultHistoryService}'s referral check. Any other caller role (including the
     * employee-portal's null/absent role) is not subject to this additional check.
     */
    public ImagingDeliveryPackage getDeliveryPackage(
            String tenantId, String packageId, String callerRoleCode, String callerId) {
        ImagingDeliveryPackage deliveryPackage = getDeliveryPackage(tenantId, packageId);
        enforceDeliveryPackageOwnership(tenantId, deliveryPackage.patientId(), callerRoleCode, callerId);
        return deliveryPackage;
    }

    public List<ImagingDeliveryPackage> listDeliveryPackagesForPatient(String tenantId, String patientId) {
        return repository.findByTenantAndPatient(tenantId, patientId);
    }

    /** HOP-HARD-APP-001 imaging delivery hardening: see {@link #getDeliveryPackage(String, String, String, String)}. */
    public List<ImagingDeliveryPackage> listDeliveryPackagesForPatient(
            String tenantId, String patientId, String callerRoleCode, String callerId) {
        enforceDeliveryPackageOwnership(tenantId, patientId, callerRoleCode, callerId);
        return listDeliveryPackagesForPatient(tenantId, patientId);
    }

    private void enforceDeliveryPackageOwnership(
            String tenantId, String patientId, String callerRoleCode, String callerId) {
        if (PATIENT_ROLE.equals(callerRoleCode) && !patientId.equals(callerId)) {
            throw new ImagingAccessDeniedException(
                    ImagingErrorCode.DELIVERY_PACKAGE_ACCESS_DENIED,
                    "The requesting patient does not own this imaging delivery package.");
        }
        if (REFERRING_DOCTOR_ROLE.equals(callerRoleCode)
                && !referringDoctorAuthorizationPort.isPatientReferredByDoctor(tenantId, callerId, patientId)) {
            throw new ImagingAccessDeniedException(
                    ImagingErrorCode.DELIVERY_PACKAGE_ACCESS_DENIED,
                    "The requesting doctor has not referred this patient.");
        }
    }

    public ImagingDeliveryPackage markDelivered(String tenantId, String packageId, String actorId) {
        ImagingDeliveryPackage existing = getDeliveryPackage(tenantId, packageId);
        ImagingDeliveryPackage updated = new ImagingDeliveryPackage(
                existing.packageId(), existing.tenantId(), existing.studyId(), existing.patientId(),
                existing.deliveryFormat(), "DELIVERED", existing.portalAccessToken(), existing.expiresAt(),
                existing.createdBy(), existing.createdAt(), actorId, Instant.now()
        );
        return repository.save(updated);
    }
}

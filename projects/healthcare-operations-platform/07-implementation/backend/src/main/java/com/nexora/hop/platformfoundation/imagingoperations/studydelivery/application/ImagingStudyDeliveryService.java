package com.nexora.hop.platformfoundation.imagingoperations.studydelivery.application;

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

    private final ImagingDeliveryPackageRepository repository;

    public ImagingStudyDeliveryService(ImagingDeliveryPackageRepository repository) {
        this.repository = repository;
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

    public List<ImagingDeliveryPackage> listDeliveryPackagesForPatient(String tenantId, String patientId) {
        return repository.findByTenantAndPatient(tenantId, patientId);
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

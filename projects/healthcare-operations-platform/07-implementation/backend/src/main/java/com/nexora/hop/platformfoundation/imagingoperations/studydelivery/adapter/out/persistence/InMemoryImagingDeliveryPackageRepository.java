package com.nexora.hop.platformfoundation.imagingoperations.studydelivery.adapter.out.persistence;

import com.nexora.hop.platformfoundation.imagingoperations.studydelivery.domain.ImagingDeliveryPackage;
import com.nexora.hop.platformfoundation.imagingoperations.studydelivery.domain.ImagingDeliveryPackageRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!local")
public class InMemoryImagingDeliveryPackageRepository implements ImagingDeliveryPackageRepository {

    private final Map<String, ImagingDeliveryPackage> store = new ConcurrentHashMap<>();

    @Override
    public ImagingDeliveryPackage save(ImagingDeliveryPackage deliveryPackage) {
        store.put(deliveryPackage.tenantId() + ":" + deliveryPackage.packageId(), deliveryPackage);
        return deliveryPackage;
    }

    @Override
    public Optional<ImagingDeliveryPackage> findById(String tenantId, String packageId) {
        return Optional.ofNullable(store.get(tenantId + ":" + packageId));
    }

    @Override
    public List<ImagingDeliveryPackage> findByStudyId(String tenantId, String studyId) {
        return store.values().stream()
                .filter(p -> p.tenantId().equals(tenantId) && p.studyId().equals(studyId))
                .toList();
    }

    @Override
    public List<ImagingDeliveryPackage> findByTenantAndPatient(String tenantId, String patientId) {
        return store.values().stream()
                .filter(p -> p.tenantId().equals(tenantId) && p.patientId().equals(patientId))
                .toList();
    }
}

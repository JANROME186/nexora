package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain;

import java.time.Instant;

import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonAddress;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonDocument;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonName;

/**
 * Doctor aggregate root (AGG-005) owned by the {@code medical-staff} bounded context. Modeled by
 * {@code bcm-per-003-doctor-management/business-model.md} ENT-DOC-001. Only the medical-staff
 * context may mutate this aggregate; downstream contexts must reference {@link DoctorSnapshot}
 * (BCM-PER-003 RN-003).
 */
public record Doctor(
        String doctorId,
        String tenantId,
        String laboratoryId,
        String doctorCode,
        PersonName name,
        PersonDocument primaryDocument,
        PersonAddress address,
        String doctorType,
        String status,
        String portalStatus,
        String portalEmail,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_SUSPENDED = "suspended";
    public static final String STATUS_RETIRED = "retired";

    public static final String TYPE_REFERRING_EXTERNAL = "referring_external";
    public static final String TYPE_INTERNAL_MEDICAL_VALIDATOR = "internal_medical_validator";
    public static final String TYPE_BOTH = "both";

    public static final String PORTAL_STATUS_NOT_PROVISIONED = "not_provisioned";
    public static final String PORTAL_STATUS_READY = "ready_for_provisioning";
    public static final String PORTAL_STATUS_PROVISIONED = "provisioned";
    public static final String PORTAL_STATUS_DISABLED = "disabled";
}

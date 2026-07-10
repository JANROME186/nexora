package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.application;

import org.springframework.stereotype.Component;

import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.DoctorRepository;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.PatientRepository;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleConflictException;

/**
 * Implements BCM-PER-001 RN-002: a primary personal document number must be unique within a
 * tenant across the Patient and Doctor scopes. This policy depends only on the repository ports
 * (not on {@code PatientManagementService}/{@code DoctorManagementService}) so both aggregate
 * services can call it during registration without introducing a circular application-service
 * dependency between patient-management, medical-staff and person-management.
 */
@Component
public class PersonDocumentUniquenessPolicy {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public PersonDocumentUniquenessPolicy(PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    /**
     * @param excludePatientId patient id to exclude from the patient-side check (used when
     *                         re-validating an existing patient); {@code null} when registering.
     * @param excludeDoctorId  doctor id to exclude from the doctor-side check; {@code null} when
     *                         registering.
     */
    public void ensureUnique(String tenantId, String documentType, String documentNumber,
            String excludePatientId, String excludeDoctorId) {
        if (documentType == null || documentType.isBlank() || documentNumber == null || documentNumber.isBlank()) {
            return;
        }
        if (patientRepository.existsByPrimaryDocument(tenantId, documentType, documentNumber, excludePatientId)) {
            throw new PeopleConflictException(
                    "Primary document number is already registered to another patient in this tenant.");
        }
        if (doctorRepository.existsByPrimaryDocument(tenantId, documentType, documentNumber, excludeDoctorId)) {
            throw new PeopleConflictException(
                    "Primary document number is already registered to another doctor in this tenant.");
        }
    }
}

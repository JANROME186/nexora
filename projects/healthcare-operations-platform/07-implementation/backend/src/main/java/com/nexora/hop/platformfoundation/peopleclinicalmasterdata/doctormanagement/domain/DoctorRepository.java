package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DoctorRepository {

    Doctor save(Doctor doctor);

    Optional<Doctor> findById(String doctorId);

    List<Doctor> findByLaboratoryId(String laboratoryId);

    List<Doctor> searchByNaturalKey(
            String tenantId,
            String normalizedFamilyName,
            String normalizedGivenName,
            LocalDate birthDate);

    boolean existsByDoctorCode(String tenantId, String doctorCode, String excludeDoctorId);

    void saveCredential(ProfessionalCredential credential);

    List<ProfessionalCredential> findCredentials(String doctorId);

    Optional<ProfessionalCredential> findCredentialById(String credentialId);

    void saveSpecialty(SpecialtyAssignment specialty);

    List<SpecialtyAssignment> findSpecialties(String doctorId);

    void deleteSpecialty(String assignmentId);
}

package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.adapter.out.memory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.Doctor;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.DoctorRepository;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.ProfessionalCredential;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.SpecialtyAssignment;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation;

@Repository
@Profile("!local")
class InMemoryDoctorRepository implements DoctorRepository {

    private final Map<String, Doctor> doctors = new ConcurrentHashMap<>();
    private final Map<String, List<ProfessionalCredential>> credentials = new ConcurrentHashMap<>();
    private final Map<String, List<SpecialtyAssignment>> specialties = new ConcurrentHashMap<>();

    @Override
    public Doctor save(Doctor doctor) {
        doctors.put(doctor.doctorId(), doctor);
        return doctor;
    }

    @Override
    public Optional<Doctor> findById(String doctorId) {
        return Optional.ofNullable(doctors.get(doctorId));
    }

    @Override
    public List<Doctor> findByLaboratoryId(String laboratoryId) {
        return doctors.values().stream()
                .filter(doctor -> doctor.laboratoryId().equals(laboratoryId))
                .toList();
    }

    @Override
    public List<Doctor> searchByNaturalKey(String tenantId, String normalizedFamilyName,
            String normalizedGivenName, LocalDate birthDate) {
        return doctors.values().stream()
                .filter(doctor -> doctor.tenantId().equals(tenantId))
                .filter(doctor -> normalizedFamilyName == null
                        || normalizedFamilyName.equals(PeopleValidation.normalizeNaturalKeyToken(
                                doctor.name() == null ? null : doctor.name().familyName())))
                .filter(doctor -> normalizedGivenName == null
                        || normalizedGivenName.equals(PeopleValidation.normalizeNaturalKeyToken(
                                doctor.name() == null ? null : doctor.name().givenName())))
                .toList();
    }

    @Override
    public boolean existsByDoctorCode(String tenantId, String doctorCode, String excludeDoctorId) {
        return doctors.values().stream()
                .anyMatch(doctor -> doctor.tenantId().equals(tenantId)
                        && doctor.doctorCode().equals(doctorCode)
                        && !doctor.doctorId().equals(excludeDoctorId));
    }

    @Override
    public boolean existsByPrimaryDocument(String tenantId, String documentType, String documentNumber,
            String excludeDoctorId) {
        return doctors.values().stream()
                .anyMatch(doctor -> doctor.tenantId().equals(tenantId)
                        && doctor.primaryDocument() != null
                        && documentType.equals(doctor.primaryDocument().documentType())
                        && documentNumber.equals(doctor.primaryDocument().documentNumber())
                        && !doctor.doctorId().equals(excludeDoctorId));
    }

    @Override
    public void saveCredential(ProfessionalCredential credential) {
        List<ProfessionalCredential> doctorCredentials =
                credentials.computeIfAbsent(credential.doctorId(), key -> new ArrayList<>());
        doctorCredentials.removeIf(existing -> existing.credentialId().equals(credential.credentialId()));
        doctorCredentials.add(credential);
    }

    @Override
    public List<ProfessionalCredential> findCredentials(String doctorId) {
        return List.copyOf(credentials.getOrDefault(doctorId, List.of()));
    }

    @Override
    public Optional<ProfessionalCredential> findCredentialById(String credentialId) {
        return credentials.values().stream()
                .flatMap(List::stream)
                .filter(credential -> credential.credentialId().equals(credentialId))
                .findFirst();
    }

    @Override
    public void saveSpecialty(SpecialtyAssignment specialty) {
        specialties.computeIfAbsent(specialty.doctorId(), key -> new ArrayList<>()).add(specialty);
    }

    @Override
    public List<SpecialtyAssignment> findSpecialties(String doctorId) {
        return List.copyOf(specialties.getOrDefault(doctorId, List.of()));
    }

    @Override
    public void deleteSpecialty(String assignmentId) {
        specialties.values().forEach(list -> list.removeIf(a -> a.assignmentId().equals(assignmentId)));
    }
}

package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain;

/**
 * Association between a doctor and a medical specialty. Modeled as ENT-DOC-003.
 */
public record SpecialtyAssignment(
        String assignmentId,
        String doctorId,
        String specialtyCode,
        boolean primary) {
}

package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain;

/** Discriminator for the unified person search index (RM-PER-001). */
public final class PersonKind {

    public static final String PATIENT = "patient";
    public static final String DOCTOR = "doctor";

    private PersonKind() {
    }
}

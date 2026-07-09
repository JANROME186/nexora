package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared;

/**
 * Raised when a People and Clinical Master Data command cannot be applied because it conflicts
 * with the current durable state of an aggregate: for example a duplicate patient or doctor code,
 * updating a deceased patient, or committing a registration whose scope does not match the actor.
 */
public class PeopleConflictException extends RuntimeException {

    public PeopleConflictException(String message) {
        super(message);
    }
}

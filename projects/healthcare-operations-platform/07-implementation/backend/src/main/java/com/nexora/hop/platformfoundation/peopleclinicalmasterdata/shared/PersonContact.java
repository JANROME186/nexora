package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared;

/**
 * Reachable contact channel for a person, modeled as {@code VO-PER-004} in the Person Management
 * business model. Consent-to-contact is captured at intake because tenants require explicit
 * opt-in per BCM-PER-002 consent policy.
 */
public record PersonContact(
        String channelType,
        String value,
        boolean preferred,
        boolean consentToContact,
        String locale) {

    public static final String CHANNEL_EMAIL = "email";
    public static final String CHANNEL_PHONE_MOBILE = "phone_mobile";
    public static final String CHANNEL_PHONE_LANDLINE = "phone_landline";
    public static final String CHANNEL_WHATSAPP = "whatsapp";
    public static final String CHANNEL_OTHER = "other";
}

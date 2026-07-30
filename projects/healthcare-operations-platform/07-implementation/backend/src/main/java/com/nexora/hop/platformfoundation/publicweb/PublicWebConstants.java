package com.nexora.hop.platformfoundation.publicweb;

/**
 * Shared constants for the COM-MOD-011 Public Website and Digital Growth backend surface. All
 * constants are declared once here so controllers, interceptors and services never hardcode
 * literal paths, header names, channel identifiers or classification tiers.
 *
 * <p>This package hosts the compiled outputs for the {@code public_surface} operations modeled
 * by COM-MOD-011-DEF in BCM-SVC-001/002/003/005, BCM-ATT-001/006 and BCM-PLT-005 (RN-007,
 * consumer-identification method for public traffic). No new capability package, aggregate or
 * schema is created; every operation reuses the existing implementations compiled under
 * MVP-MOD-002 (Diagnostic Catalog), MVP-MOD-004 (Front Desk and Care Delivery) and MVP-MOD-008
 * (Integration and API Management).
 */
public final class PublicWebConstants {

    /** Base path for every COM-MOD-011 public-classified operation. */
    public static final String API_BASE_PATH = "/api/public";

    /** Public catalog base sub-path. */
    public static final String CATALOG_BASE_PATH = API_BASE_PATH + "/catalog";

    /** Public marketplace discovery base sub-path. */
    public static final String MARKETPLACE_BASE_PATH = API_BASE_PATH + "/marketplace";

    /** Public request-intake base sub-path. */
    public static final String CARE_DELIVERY_BASE_PATH = API_BASE_PATH + "/care-delivery";

    /** Public location/contact discovery base sub-path (BCM-ORG-003 branch directory, published only). */
    public static final String LOCATION_BASE_PATH = API_BASE_PATH + "/locations";

    /** URL glob (Spring {@code AntPathMatcher} style) selecting every public endpoint. */
    public static final String API_PATH_PATTERN = API_BASE_PATH + "/**";

    /** Session-token header name honored by {@code PublicApiRateLimitInterceptor}. */
    public static final String SESSION_TOKEN_HEADER = "X-Public-Session-Token";

    /**
     * The channel value used to mark AppointmentSlot/QuotationRequest records created by anonymous
     * public-website callers (BCM-ATT-001 RN-008, BCM-ATT-006 RN-009).
     */
    public static final String CHANNEL_PUBLIC_WEBSITE = "public_website";

    private PublicWebConstants() {
    }
}

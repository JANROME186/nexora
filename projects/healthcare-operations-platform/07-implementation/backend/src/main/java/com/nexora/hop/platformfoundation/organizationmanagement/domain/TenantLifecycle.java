package com.nexora.hop.platformfoundation.organizationmanagement.domain;

import java.util.Set;

/**
 * BCM-ORG-001 {@code TenantRoot} enumerations (business-model.yaml): the tenant provisioning
 * lifecycle, subscription tier and data-isolation strategy. Kept as validated string constants
 * (see {@link Tenant}) instead of Java enums so the JDBC/in-memory adapters persist and compare
 * them the same way every other status field in this module already does.
 */
public final class TenantLifecycle {

    public static final String STATUS_PENDING_PROVISIONING = "PENDING_PROVISIONING";
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_SUSPENDED = "SUSPENDED";
    public static final String STATUS_ARCHIVED = "ARCHIVED";

    public static final Set<String> VALID_STATUSES = Set.of(
            STATUS_PENDING_PROVISIONING, STATUS_ACTIVE, STATUS_SUSPENDED, STATUS_ARCHIVED);

    public static final String TIER_STARTER = "STARTER";
    public static final String TIER_PROFESSIONAL = "PROFESSIONAL";
    public static final String TIER_ENTERPRISE = "ENTERPRISE";

    public static final Set<String> VALID_TIERS = Set.of(TIER_STARTER, TIER_PROFESSIONAL, TIER_ENTERPRISE);

    public static final String ISOLATION_SCHEMA_PER_TENANT = "SCHEMA_PER_TENANT";
    public static final String ISOLATION_DISCRIMINATOR_WITH_RLS = "DISCRIMINATOR_WITH_RLS";

    public static final Set<String> VALID_ISOLATION_STRATEGIES =
            Set.of(ISOLATION_SCHEMA_PER_TENANT, ISOLATION_DISCRIMINATOR_WITH_RLS);

    private TenantLifecycle() {
    }
}

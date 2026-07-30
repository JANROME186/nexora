package com.nexora.hop.platformfoundation.publicweb.marketplace;

import java.util.List;

/**
 * Public, read-only snapshot DTO for published marketplace packages (BCM-PLT-011 public_surface).
 * Strips tenant identifiers, internal audit details, and lifecycle administrative fields.
 */
public record PublicMarketplacePackageSnapshot(
        String packageId,
        String code,
        String name,
        String category,
        List<String> capabilityMappings,
        String status) {
}

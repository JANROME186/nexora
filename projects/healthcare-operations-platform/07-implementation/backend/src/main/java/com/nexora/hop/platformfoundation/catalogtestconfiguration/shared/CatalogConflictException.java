package com.nexora.hop.platformfoundation.catalogtestconfiguration.shared;

/**
 * Raised when a catalog command cannot be applied because it conflicts with the current durable
 * state of an aggregate, for example directly editing a published (immutable) catalog entry or
 * publishing an effective-dated entry whose validity window overlaps an already published sibling.
 *
 * <p>Introduced by MVP-MOD-002-BE-002 to replace the former
 * {@link CatalogCustomRuleNotImplementedException} HTTP 501 hooks with functional conflict
 * semantics (HTTP 409) for the publication, versioning and effective-resolution business rules.</p>
 */
public class CatalogConflictException extends RuntimeException {

    public CatalogConflictException(String message) {
        super(message);
    }
}

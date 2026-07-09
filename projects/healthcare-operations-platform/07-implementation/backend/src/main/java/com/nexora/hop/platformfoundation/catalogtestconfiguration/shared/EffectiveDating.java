package com.nexora.hop.platformfoundation.catalogtestconfiguration.shared;

import java.time.LocalDate;

/**
 * Effective-dated versioning helpers shared by the reference range (BCM-SVC-006) and price list
 * (BCM-SVC-009) custom rules implemented in MVP-MOD-002-BE-002. Both capabilities model a
 * validity window {@code [effectiveFrom, effectiveTo]} where an open (null) {@code effectiveTo}
 * means the window stays effective indefinitely.
 */
public final class EffectiveDating {

    private EffectiveDating() {
    }

    /** An open ({@code null}) upper bound is treated as {@link LocalDate#MAX}. */
    public static LocalDate endOrMax(LocalDate effectiveTo) {
        return effectiveTo == null ? LocalDate.MAX : effectiveTo;
    }

    /** True when the two inclusive validity windows share at least one day. */
    public static boolean windowsOverlap(LocalDate aFrom, LocalDate aTo, LocalDate bFrom, LocalDate bTo) {
        LocalDate aEnd = endOrMax(aTo);
        LocalDate bEnd = endOrMax(bTo);
        return !aFrom.isAfter(bEnd) && !bFrom.isAfter(aEnd);
    }

    /** True when {@code date} falls within the inclusive validity window. */
    public static boolean isEffectiveOn(LocalDate effectiveFrom, LocalDate effectiveTo, LocalDate date) {
        return !date.isBefore(effectiveFrom) && !date.isAfter(endOrMax(effectiveTo));
    }
}

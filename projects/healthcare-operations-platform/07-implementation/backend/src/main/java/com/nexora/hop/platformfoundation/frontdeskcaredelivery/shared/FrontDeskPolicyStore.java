package com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/**
 * Tenant-scoped policy overrides for the Front Desk and Care Delivery custom rules that the
 * capability packages explicitly model as tenant-configurable (MVP-MOD-004-BE-002):
 * <ul>
 *   <li>BCM-ATT-001 RN-006 no-show grace period (in days, consistent with AppointmentSlot's
 *       date-only scheduling granularity) and branch daily appointment capacity.</li>
 *   <li>BCM-ATT-004 RN-003 which admission acknowledgements (consent, sample requirements) are
 *       mandatory before an order can be committed.</li>
 *   <li>BCM-ATT-006 RN-003 the standard and override-authorized maximum discount percentage.</li>
 * </ul>
 * No capability package declares a dedicated REST surface for editing these policies, so this
 * store is an in-memory, process-local registry with safe defaults, mirroring
 * {@code peopleclinicalmasterdata.personmanagement.application.TenantPeoplePolicyStore}. It is
 * exposed as a Spring bean so a future administrative capability (or a test) can override a
 * tenant's policy without changing application code.
 */
@Component
public class FrontDeskPolicyStore {

    public static final int DEFAULT_NO_SHOW_GRACE_DAYS = 0;
    public static final int DEFAULT_BRANCH_DAILY_APPOINTMENT_CAPACITY = 200;
    public static final String ACK_CONSENT = "consent";
    public static final String ACK_SAMPLE_REQUIREMENTS = "sample_requirements";
    public static final Set<String> DEFAULT_REQUIRED_ADMISSION_ACKNOWLEDGEMENTS =
            Set.of(ACK_CONSENT, ACK_SAMPLE_REQUIREMENTS);
    public static final BigDecimal DEFAULT_STANDARD_MAX_DISCOUNT_PERCENTAGE = BigDecimal.valueOf(20);
    public static final BigDecimal DEFAULT_OVERRIDE_MAX_DISCOUNT_PERCENTAGE = BigDecimal.valueOf(50);

    private final Map<String, Integer> noShowGraceDaysOverrides = new ConcurrentHashMap<>();
    private final Map<String, Integer> branchDailyCapacityOverrides = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> requiredAdmissionAcknowledgementOverrides = new ConcurrentHashMap<>();
    private final Map<String, BigDecimal> standardMaxDiscountOverrides = new ConcurrentHashMap<>();
    private final Map<String, BigDecimal> overrideMaxDiscountOverrides = new ConcurrentHashMap<>();

    public int noShowGraceDaysFor(String tenantId) {
        return noShowGraceDaysOverrides.getOrDefault(tenantId, DEFAULT_NO_SHOW_GRACE_DAYS);
    }

    public void setNoShowGraceDays(String tenantId, int graceDays) {
        if (tenantId == null || graceDays < 0) {
            return;
        }
        noShowGraceDaysOverrides.put(tenantId, graceDays);
    }

    public int branchDailyAppointmentCapacityFor(String tenantId) {
        return branchDailyCapacityOverrides.getOrDefault(tenantId, DEFAULT_BRANCH_DAILY_APPOINTMENT_CAPACITY);
    }

    public void setBranchDailyAppointmentCapacity(String tenantId, int capacity) {
        if (tenantId == null || capacity <= 0) {
            return;
        }
        branchDailyCapacityOverrides.put(tenantId, capacity);
    }

    public Set<String> requiredAdmissionAcknowledgementsFor(String tenantId) {
        return requiredAdmissionAcknowledgementOverrides.getOrDefault(
                tenantId, DEFAULT_REQUIRED_ADMISSION_ACKNOWLEDGEMENTS);
    }

    public void setRequiredAdmissionAcknowledgements(String tenantId, Set<String> acknowledgementTypes) {
        if (tenantId == null || acknowledgementTypes == null) {
            return;
        }
        requiredAdmissionAcknowledgementOverrides.put(tenantId, Set.copyOf(acknowledgementTypes));
    }

    public BigDecimal standardMaxDiscountPercentageFor(String tenantId) {
        return standardMaxDiscountOverrides.getOrDefault(tenantId, DEFAULT_STANDARD_MAX_DISCOUNT_PERCENTAGE);
    }

    public void setStandardMaxDiscountPercentage(String tenantId, BigDecimal percentage) {
        if (tenantId == null || percentage == null || percentage.signum() < 0) {
            return;
        }
        standardMaxDiscountOverrides.put(tenantId, percentage);
    }

    public BigDecimal overrideMaxDiscountPercentageFor(String tenantId) {
        return overrideMaxDiscountOverrides.getOrDefault(tenantId, DEFAULT_OVERRIDE_MAX_DISCOUNT_PERCENTAGE);
    }

    public void setOverrideMaxDiscountPercentage(String tenantId, BigDecimal percentage) {
        if (tenantId == null || percentage == null || percentage.signum() < 0) {
            return;
        }
        overrideMaxDiscountOverrides.put(tenantId, percentage);
    }
}

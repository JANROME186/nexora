package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.application;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.PatientConsent;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonDocument.DocumentNumberMaskingPolicy;

/**
 * Tenant-scoped policy overrides for the People and Clinical Master Data custom rules that the
 * capability packages explicitly model as tenant-configurable:
 * <ul>
 *   <li>BCM-PER-001 RN-003 duplicate-detection confidence weights.</li>
 *   <li>BCM-ATT-002 RN-008 age-of-majority threshold used to default a registration to the
 *       representative-registration kind.</li>
 *   <li>BCM-ATT-002 RN-005 mandatory consent types that must be captured before a registration
 *       commits.</li>
 *   <li>BCM-PER-002 RN-008 / BCM-PER-003 RN-008 document/credential number masking policy
 *       (visible-character count and mask character) applied by read-model projections.</li>
 * </ul>
 * No capability package declares a dedicated REST surface for editing these policies, so this
 * store is an in-memory, process-local registry with safe defaults. It is intentionally exposed as
 * a Spring bean so a future administrative capability (or a test) can override a tenant's policy
 * without changing application code.
 */
@Component
public class TenantPeoplePolicyStore {

    public static final int DEFAULT_AGE_OF_MAJORITY_YEARS = 18;
    public static final Set<String> DEFAULT_MANDATORY_CONSENT_TYPES = Set.of(PatientConsent.TYPE_DATA_PROCESSING);

    private final Map<String, PersonDuplicateScoringPolicy> scoringOverrides = new ConcurrentHashMap<>();
    private final Map<String, Integer> ageOfMajorityOverrides = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> mandatoryConsentOverrides = new ConcurrentHashMap<>();
    private final Map<String, DocumentNumberMaskingPolicy> documentMaskingOverrides = new ConcurrentHashMap<>();

    public PersonDuplicateScoringPolicy scoringPolicyFor(String tenantId) {
        return scoringOverrides.getOrDefault(tenantId, PersonDuplicateScoringPolicy.DEFAULT);
    }

    public void setScoringPolicy(String tenantId, PersonDuplicateScoringPolicy policy) {
        if (tenantId == null || policy == null) {
            return;
        }
        scoringOverrides.put(tenantId, policy);
    }

    public int ageOfMajorityYearsFor(String tenantId) {
        return ageOfMajorityOverrides.getOrDefault(tenantId, DEFAULT_AGE_OF_MAJORITY_YEARS);
    }

    public void setAgeOfMajorityYears(String tenantId, int years) {
        if (tenantId == null || years <= 0) {
            return;
        }
        ageOfMajorityOverrides.put(tenantId, years);
    }

    public Set<String> mandatoryConsentTypesFor(String tenantId) {
        return mandatoryConsentOverrides.getOrDefault(tenantId, DEFAULT_MANDATORY_CONSENT_TYPES);
    }

    public void setMandatoryConsentTypes(String tenantId, Set<String> consentTypes) {
        if (tenantId == null || consentTypes == null) {
            return;
        }
        mandatoryConsentOverrides.put(tenantId, Set.copyOf(consentTypes));
    }

    public DocumentNumberMaskingPolicy documentMaskingPolicyFor(String tenantId) {
        return documentMaskingOverrides.getOrDefault(tenantId, DocumentNumberMaskingPolicy.DEFAULT);
    }

    public void setDocumentMaskingPolicy(String tenantId, DocumentNumberMaskingPolicy policy) {
        if (tenantId == null || policy == null) {
            return;
        }
        documentMaskingOverrides.put(tenantId, policy);
    }
}

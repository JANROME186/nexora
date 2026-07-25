package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared;

/**
 * Explicit hook for a People and Clinical Master Data business rule that is declared as a
 * {@code custom_implementation_point} in generation-plan.md and deferred to MVP-MOD-003-BE-002.
 * <p>
 * Compiling MVP-MOD-003-BE-001 must never invent custom behavior. Endpoints that expose those
 * deferred rules raise this exception so the follow-up backlog item has a discoverable, testable
 * plug-in point instead of silent placeholder logic. The default {@link #backlogItem()} is
 * {@code MVP-MOD-003-BE-002}.
 */
public class PeopleCustomRuleNotImplementedException extends RuntimeException {

    private final String ruleId;
    private final String backlogItem;

    public PeopleCustomRuleNotImplementedException(String ruleId, String message) {
        this(ruleId, "MVP-MOD-003-BE-002", message);
    }

    public PeopleCustomRuleNotImplementedException(String ruleId, String backlogItem, String message) {
        super(message);
        this.ruleId = ruleId;
        this.backlogItem = backlogItem;
    }

    public String ruleId() {
        return ruleId;
    }

    public String backlogItem() {
        return backlogItem;
    }
}

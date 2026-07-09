package com.nexora.hop.platformfoundation.catalogtestconfiguration.shared;

/**
 * Explicit hook for a business rule or operation that generation-plan.yaml declares as a
 * custom_implementation_point deferred to MVP-MOD-002-BE-002. Compiling MVP-MOD-002-BE-001
 * must not implement complex custom business rules; this exception marks precisely where
 * the follow-up backlog item must plug in real behavior.
 */
public class CatalogCustomRuleNotImplementedException extends RuntimeException {

    private final String ruleId;
    private final String backlogItem;

    public CatalogCustomRuleNotImplementedException(String ruleId, String message) {
        this(ruleId, "MVP-MOD-002-BE-002", message);
    }

    public CatalogCustomRuleNotImplementedException(String ruleId, String backlogItem, String message) {
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

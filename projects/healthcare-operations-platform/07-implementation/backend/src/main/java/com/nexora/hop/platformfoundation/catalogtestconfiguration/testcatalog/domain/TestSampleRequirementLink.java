package com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.domain;

/** References a sample requirement needed to run the test (ENT-TST-003). */
public record TestSampleRequirementLink(String linkId, String testDefinitionId, String sampleRequirementRefId) {
}

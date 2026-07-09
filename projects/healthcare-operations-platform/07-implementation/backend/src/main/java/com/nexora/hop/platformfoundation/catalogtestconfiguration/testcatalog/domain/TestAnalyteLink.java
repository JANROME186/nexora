package com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.domain;

/** References an analyte definition that composes the test (ENT-TST-002). */
public record TestAnalyteLink(String linkId, String testDefinitionId, String analyteRefId, Integer displayOrder) {
}

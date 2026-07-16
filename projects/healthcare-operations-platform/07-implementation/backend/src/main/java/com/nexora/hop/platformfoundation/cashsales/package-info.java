/**
 * Cash Sales bounded context generated from MVP-MOD-005 Cashier and Billing Request capability
 * packages. Owns cashier sessions, sales, payment allocations and provider-agnostic billing
 * requests. Fiscal adapter submission remains an explicit later boundary.
 */
@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {
            "auditcompliance",
            "catalogtestconfiguration",
            "frontdeskcaredelivery"
        })
package com.nexora.hop.platformfoundation.cashsales;

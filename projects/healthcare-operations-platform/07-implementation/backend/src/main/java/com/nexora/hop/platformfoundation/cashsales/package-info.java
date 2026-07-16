/**
 * Cash Sales bounded context generated from MVP-MOD-005 Cashier and Billing Request capability
 * packages. Owns cashier sessions, sales, payment allocations and provider-agnostic billing
 * requests. Reads diagnostic order and quotation source snapshots from FrontDeskCareDelivery via
 * the stable named {@code sale-source-port} interface (TD-BE-011 closed). Fiscal adapter
 * submission is an explicit replaceable boundary (MVP-MOD-005-BE-002).
 */
@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {
            "auditcompliance",
            "catalogtestconfiguration",
            "frontdeskcaredelivery::sale-source-port"
        })
package com.nexora.hop.platformfoundation.cashsales;

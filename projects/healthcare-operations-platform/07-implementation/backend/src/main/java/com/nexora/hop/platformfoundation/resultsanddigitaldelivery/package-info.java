@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {
        "sharedkernel",
        "laboratoryworkflow::laboratory-results-domain",
        "documentmanagement::document-service",
        "documentmanagement::document-domain",
        "notificationmanagement::notification-service",
        "notificationmanagement::notification-domain",
        "peopleclinicalmasterdata",
        "frontdeskcaredelivery::sale-source-port"
    }
)
package com.nexora.hop.platformfoundation.resultsanddigitaldelivery;

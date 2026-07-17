@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {
        "sharedkernel",
        "laboratoryworkflow::laboratory-results-domain",
        "documentmanagement",
        "notificationmanagement::notification-service",
        "notificationmanagement::notification-domain",
        "peopleclinicalmasterdata",
        "frontdeskcaredelivery::sale-source-port"
    }
)
package com.nexora.hop.platformfoundation.resultsanddigitaldelivery;

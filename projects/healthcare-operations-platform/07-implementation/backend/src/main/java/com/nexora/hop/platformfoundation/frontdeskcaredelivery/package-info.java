/**
 * Front Desk and Care Delivery application module (MVP-MOD-004). Hosts the DiagnosticOrder
 * aggregate owner (BCM-LAB-001, AGG-007) and the front-desk orchestration capabilities that
 * delegate order mutation to it: Appointment Scheduling (BCM-ATT-001), Reception Management
 * (BCM-ATT-003), Admission Management (BCM-ATT-004) and Quotation Management (BCM-ATT-006).
 * Bounded context {@code orders-samples} (diagnostic order, appointment, reception, admission)
 * and {@code cash-sales} (quotation) are kept as sibling packages inside this Spring Modulith
 * module, mirroring how peopleclinicalmasterdata hosts patient-management and medical-staff.
 * Cross-module dependencies are limited to organization-management (tenant/branch lookup),
 * peopleclinicalmasterdata (patient/doctor snapshot lookup), catalogtestconfiguration
 * (published test/panel/price-list lookup) and audit-compliance (append-only audit sink).
 *
 * <p>The {@code sale-source-port} named interface (declared on the {@code application}
 * sub-package) exposes the stable public boundary that CashSales depends on. Internal service,
 * domain and adapter packages remain private. Closes TD-BE-011.</p>
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "Front Desk and Care Delivery",
        allowedDependencies = {
                "organizationmanagement", "peopleclinicalmasterdata", "catalogtestconfiguration", "auditcompliance" })
package com.nexora.hop.platformfoundation.frontdeskcaredelivery;

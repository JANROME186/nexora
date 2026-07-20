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
 * (published test/panel/price-list lookup), auditcompliance (append-only audit sink) and
 * laboratoryworkflow (read-only Sample state via the {@code sample-read-port} named interface).
 *
 * <p>The {@code sale-source-port} named interface (declared on the {@code application}
 * sub-package) exposes the stable public boundary that CashSales depends on. Internal service,
 * domain and adapter packages remain private. Closes TD-BE-011.</p>
 *
 * <p>{@link com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement
 * .application.DiagnosticOrderManagementService#cancel} consumes
 * {@code laboratoryworkflow.shared.SampleReadPort} to check real collected-sample state instead
 * of relying solely on order status as a proxy (closes TD-BE-010).</p>
 *
 * <p>{@link com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement
 * .application.DiagnosticOrderManagementService} also implements the
 * {@code referring-doctor-authorization-port} named interface (COM-MOD-009-PORTAL-002), exposing
 * the doctor/patient referral relationship captured on DiagnosticOrder snapshots so
 * resultsanddigitaldelivery can enforce a real least-privilege boundary for the doctor portal.</p>
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "Front Desk and Care Delivery",
        allowedDependencies = {
                "organizationmanagement", "peopleclinicalmasterdata", "catalogtestconfiguration",
                "auditcompliance", "laboratoryworkflow::sample-read-port" })
package com.nexora.hop.platformfoundation.frontdeskcaredelivery;

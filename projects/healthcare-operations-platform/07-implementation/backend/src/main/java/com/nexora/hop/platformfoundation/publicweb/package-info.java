/**
 * COM-MOD-011 Public Website and Digital Growth backend surface. Consists of anonymous public
 * catalog reads (BCM-SVC-001/002/003/005) and public request-intake (BCM-ATT-001 RN-008,
 * BCM-ATT-006 RN-009) controllers that call into two module boundaries:
 * <ul>
 *   <li>{@code catalogtestconfiguration::catalog-public-read-port} for published-only catalog
 *       reads.</li>
 *   <li>{@code frontdeskcaredelivery::public-intake-port} for anonymous appointment and
 *       quotation intake.</li>
 * </ul>
 * Rate-limit enforcement for anonymous traffic (BCM-PLT-005 RN-007) remains owned by the
 * integrationinteroperability module's ApiManagementWebConfig; publicweb declares no direct
 * dependency on any api-management internal type.
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "Public Website and Digital Growth",
        allowedDependencies = {
            "sharedkernel",
            "catalogtestconfiguration::catalog-public-read-port",
            "frontdeskcaredelivery::public-intake-port"
        })
package com.nexora.hop.platformfoundation.publicweb;

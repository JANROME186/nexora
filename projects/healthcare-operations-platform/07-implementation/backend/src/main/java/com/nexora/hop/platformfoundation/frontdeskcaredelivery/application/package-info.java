/**
 * Named interface package exposing the stable public sale-source-port boundary of the
 * FrontDeskCareDelivery module. Only the {@link FrontDeskSaleSourcePort} interface and its
 * supporting exception types are part of this named interface; internal services, domain
 * aggregates, and adapters remain private to the frontdeskcaredelivery module.
 *
 * <p>This named interface closes TD-BE-011 by providing CashSales a stable, read-only surface
 * to access diagnostic order and quotation data without depending on internal classes.</p>
 */
@org.springframework.modulith.NamedInterface("sale-source-port")
package com.nexora.hop.platformfoundation.frontdeskcaredelivery.application;

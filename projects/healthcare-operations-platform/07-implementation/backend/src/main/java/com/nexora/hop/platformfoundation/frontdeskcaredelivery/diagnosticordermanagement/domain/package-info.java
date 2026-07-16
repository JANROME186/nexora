/**
 * Exposes the diagnostic order domain types as part of the {@code sale-source-port} named
 * interface. Only read-only domain records (aggregate state, order lines, patient snapshot,
 * pricing snapshot) are referenced by the CashSales bounded context through the
 * {@link com.nexora.hop.platformfoundation.frontdeskcaredelivery.application.FrontDeskSaleSourcePort}
 * interface. Mutation of diagnostic order aggregates remains private to the
 * FrontDeskCareDelivery module.
 */
@org.springframework.modulith.NamedInterface("sale-source-port")
package com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain;

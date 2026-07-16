/**
 * Exposes the quotation management domain types as part of the {@code sale-source-port} named
 * interface. Only read-only domain records (quotation state, quotation lines) are referenced by
 * the CashSales bounded context through the
 * {@link com.nexora.hop.platformfoundation.frontdeskcaredelivery.application.FrontDeskSaleSourcePort}
 * interface. Mutation of quotation aggregates remains private to the FrontDeskCareDelivery module.
 */
@org.springframework.modulith.NamedInterface("sale-source-port")
package com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.domain;

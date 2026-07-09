package com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.domain;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.Money;

/** A price for a catalog item within a price list (ENT-PRC-002). */
public record PriceEntry(String entryId, String priceListId, String itemType, String itemRefId, Money price) {

    public static final String ITEM_SERVICE = "service";
    public static final String ITEM_TEST = "test";
    public static final String ITEM_PANEL = "panel";
}

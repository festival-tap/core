package com.festivaltap.core.ports.out;

/**
 * Outbound configuration port for retrieving the current price per litre of beer (€/L).
 */
public interface PricingPort {

    /**
     * Returns the current price per litre in euros (defaults to {@code 12.25}).
     */
    default double getPricePerLiter() {
        return 12.25;
    }
}

package com.newland.nsdk.core.api.common.card.contact;

/**
 * This class is the configuration of contact card to be set.
 */
public class ContactCardConfig {
    private SAMClkFrequency samClkFrequency;

    /**
     * Gets the SAM CLK Frequency to be set.
     * @return The SAM CLK Frequency to be set.
     */
    public SAMClkFrequency getSamClkFrequency() {
        return samClkFrequency;
    }

    /**
     * Sets the SAM CLK Frequency to be set.
     * @param samClkFrequency The SAM CLK Frequency to be set.
     */
    public void setSamClkFrequency(SAMClkFrequency samClkFrequency) {
        this.samClkFrequency = samClkFrequency;
    }
}

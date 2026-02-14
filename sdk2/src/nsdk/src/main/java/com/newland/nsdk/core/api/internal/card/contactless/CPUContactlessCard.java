package com.newland.nsdk.core.api.internal.card.contactless;

import com.newland.nsdk.core.api.common.exception.NSDKException;

/**
 * Provides the ability to communicate with CPU card.
 *
 * <p>How to create a CPUContactlessCard instance:</p>
 * <pre>
 *     // Note: You need to open card reader and wait the card to be tapped before you create the card instance.
 *     CPUContactlessCard cpuContactlessCard = new CPUContactlessCardImpl()
 * </pre>
 */
public interface CPUContactlessCard extends ContactlessCard {
    /**
     * Performs APDU command.
     *
     * <p>Note: This shall be called after the card is activated.</p>
     *
     * @param command <b>[Required]</b>APDU command data.
     * @return APDU response data.
     * @throws NSDKException
     */
    byte[] performAPDU(byte[] command) throws NSDKException;
}

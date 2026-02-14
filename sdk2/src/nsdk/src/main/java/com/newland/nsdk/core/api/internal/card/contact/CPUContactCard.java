package com.newland.nsdk.core.api.internal.card.contact;

import com.newland.nsdk.core.api.common.exception.NSDKException;

/**
 * Provides the ability to communicate with CPU card.
 *
 * <p>How to create a CPUContactCard instance:</p>
 * <pre>
 *     // Contact card slot is required.
 *     // Note:
 *     // - For IC1 slot, you need to open card reader and wait the card to be inserted before you create the card instance.
 *     // - For IC2 slot, you need to open card reader and wait the card to be inserted before you create the card instance.
 *     // - For SAM slot, card instance could be created directly.
 *     CPUContactCard cpuContactCard1 = new CPUContactCardImpl(ContactCardSlot.IC1)
 *     CPUContactCard cpuContactCard2 = new CPUContactCardImpl(ContactCardSlot.SAM1)
 *     //I2C slot is selectively supported on N950 devices
 *     CPUContactCard cpuContactCard3 = new CPUContactCardImpl(ContactCardSlot.IC2)
 * </pre>
 */
public interface CPUContactCard extends ContactCard {
    /**
     * Performs APDU command after the card is powered up.
     *
     * @param command <b>[Required]</b> APDU command data.
     * @return APDU response data.
     * @throws NSDKException
     */
    byte[] performAPDU(byte[] command) throws NSDKException;
}

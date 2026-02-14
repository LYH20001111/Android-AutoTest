package com.newland.nsdk.core.api.internal.card.contact;

import com.newland.nsdk.core.api.common.card.Card;
import com.newland.nsdk.core.api.common.card.contact.ContactCardConfig;
import com.newland.nsdk.core.api.common.exception.NSDKException;

/**
 * Provides the ability to operate contact cards.
 *
 * <p>Note: Usually concrete card instances(e.g., CPUContactCard) are created instead of ContactCard instances.</p>
 *
 * <p>How to create a ContactCard instance:</p>
 * <pre>
 *     // Contact card type and slot are required to new a contact card instance.
 *     // Note:
 *     // - For IC1 slot, you need to open card reader and wait the card to be inserted before you create the card instance.
 *     // - For IC2 slot, you need to open card reader and wait the card to be inserted before you create the card instance.
 *     // - For SAM slot, card instance could be created directly.
 *     ContactCard contactCard1 = new ContactCardImpl(ContactCardSlot.IC1, ContactCardType.CPU)
 *     ContactCard contactCard2 = new ContactCardImpl(ContactCardSlot.SAM1, ContactCardType.CPU)
 *     //I2C slot is selectively supported on N950 devices.
 *     ContactCard contactCard3 = new ContactCardImpl(ContactCardSlot.IC2, ContactCardType.CPU)
 * </pre>
 */
public interface ContactCard extends Card {

    /**
     * Sets the configuration of Contact card.
     * @param config <b[Required] The configuration of the contact card.
     * @throws NSDKException
     */
    void setConfig(ContactCardConfig config) throws NSDKException;
    /**
     * Powers up the card.
     *
     * @return ATR data.
     * @throws NSDKException
     */
    byte[] powerUp() throws NSDKException;

    /**
     * Powers down the card.
     *
     * @throws NSDKException
     */
    void powerDown() throws NSDKException;
}
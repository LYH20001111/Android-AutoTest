package com.newland.nsdk.core.api.external.card.contact;

import com.newland.nsdk.core.api.common.card.Card;
import com.newland.nsdk.core.api.common.exception.NSDKException;

/**
 * Provides the ability to operate contact cards.
 *
 * <p>Note: Usually concrete card instances(e.g., ExtCPUContactCard) are created instead of ExtContactCard instances.</p>
 *
 * <p>How to create a ExtContactCard instance:</p>
 * <pre>
 *     // Contact card type and slot are required to new a contact card instance.
 *     // Note:
 *     // - For IC1 slot, you need to open card reader and wait the card to be inserted before you create the card instance.
 *     // - For SAM slot, card instance could be created directly.
 *     ExtContactCard extContactCard1 = new ExtContactCardImpl(ContactCardSlot.IC1, ContactCardType.CPU)
 *     ExtContactCard extContactCard2 = new ExtContactCardImpl(ContactCardSlot.SAM1, ContactCardType.CPU)
 * </pre>
 */
public interface ExtContactCard extends Card {
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

package com.newland.nsdk.core.api.internal.card.contactless;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.card.Card;
import com.newland.nsdk.core.api.common.card.contactless.ActivationResult;
import com.newland.nsdk.core.api.common.cardreader.CardReaderListener;
import com.newland.nsdk.core.api.common.cardreader.CardReaderParameters;
import com.newland.nsdk.core.api.common.cardreader.CardType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.internal.cardreader.CardReader;

/**
 * Provides the ability to operate contactless cards.
 *
 * <p>Note: Usually concrete card instances(e.g., CPUContactlessCard, M1Card) are created instead of ContactlessCard instances.</p>
 *
 * <p>How to create a ContactlessCard instance:</p>
 * <pre>
 *     // Contactless card type is required to new a contactless card instance.
 *     // Note: You need to open card reader and wait the card to be tapped before you create the card instance.
 *     ContactlessCard contactlessCard1 = new ContactlessCardImpl(SubContactlessCardType.CPU)
 *     ContactlessCard contactlessCard2 = new ContactlessCardImpl(SubContactlessCardType.M1)
 * </pre>
 */
public interface ContactlessCard extends Card {
    /**
     * Activates the card.
     *
     * <ul>Note:
     * <li>This shall be called after using {@link CardReader#openCardReader(CardType[], int, CardReaderParameters, CardReaderListener)} to get a contactless card.</li>
     * <li>No need to activate Felica card </li>
     * <li>If a CPU card is already activated, it will throw {@link ErrorCode#RFID_UPED} when activating the card again.</li>
     * </ul>
     *
     * @return Activation data, see {@link ActivationResult}.
     * @throws NSDKException
     */
    ActivationResult activate() throws NSDKException;

    /**
     * Deactivates the card.
     *
     * @throws NSDKException
     */
    void deactivate() throws NSDKException;
}

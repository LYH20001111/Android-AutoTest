package com.newland.nsdk.core.external.command.cardreader;

import com.newland.nsdk.core.api.common.card.contactless.ContactlessCardInfo;
import com.newland.nsdk.core.api.common.card.contactless.ContactlessCardType;
import com.newland.nsdk.core.api.common.card.magcard.MagCardInfo;
import com.newland.nsdk.core.api.common.cardreader.CardType;

/**
 * The information of the detected card.
 */
public class DetectedCardInfo {
    private CardType cardType;
    private ContactlessCardType contactlessCardType;
    private MagCardInfo magCardInfo;
    private ContactlessCardInfo contactlessCardInfo;

    /**
     * Get the type of detected card.
     *
     * @return The type of detected card, see {@link CardType}.
     */
    public CardType getCardType() {
        return cardType;
    }

    /**
     * Set the type of detected card.
     *
     * @param cardType The type of detected card, see {@link CardType}.
     */
    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    /**
     * Gets card type of tapped contactless card.
     *
     * @return Card type of tapped contactless card.
     */
    public ContactlessCardType getContactlessCardType() {
        return contactlessCardType;
    }

    /**
     * Sets card type of tapped contactless card.
     *
     * @param contactlessCardType Card type of tapped contactless card.
     */
    public void setContactlessCardType(ContactlessCardType contactlessCardType) {
        this.contactlessCardType = contactlessCardType;
    }

    /**
     * Gets card info of swiped mag card.
     *
     * @return Card info of swiped mag card.
     */
    public MagCardInfo getMagCardInfo() {
        return magCardInfo;
    }

    /**
     * Sets card info of swiped mag card.
     *
     * @param magCardInfo Card info of swiped mag card.
     */
    public void setMagCardInfo(MagCardInfo magCardInfo) {
        this.magCardInfo = magCardInfo;
    }

    public ContactlessCardInfo getContactlessCardInfo() {
        return contactlessCardInfo;
    }

    public void setContactlessCardInfo(ContactlessCardInfo contactlessCardInfo) {
        this.contactlessCardInfo = contactlessCardInfo;
    }
}

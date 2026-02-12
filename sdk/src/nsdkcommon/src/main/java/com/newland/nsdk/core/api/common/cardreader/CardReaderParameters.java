package com.newland.nsdk.core.api.common.cardreader;

import com.newland.nsdk.core.api.common.card.contact.ContactCardSlot;
import com.newland.nsdk.core.api.common.card.contactless.CardSearchMode;
import com.newland.nsdk.core.api.common.card.contactless.ContactlessCardType;

/**
 * Parameters used to open card readers.
 */
public class CardReaderParameters {
    private ContactlessCardType[] contactlessCardTypes = new ContactlessCardType[]{ContactlessCardType.TYPE_A, ContactlessCardType.TYPE_B};
    private ContactCardSlot[] cardSlots = new ContactCardSlot[] {ContactCardSlot.IC1};
    private CardSearchMode cardSearchMode = CardSearchMode.DEFAULT;
    private boolean isVerifyTrack = true;
    private byte[] typeFParameters;
    private byte[] typeVParameters;

    /**
     * Gets contactless card types.
     *
     * @return Contactless card types. See {@link ContactlessCardType}.
     */
    public ContactlessCardType[] getContactlessCardTypes() {
        return contactlessCardTypes;
    }

    /**
     * Sets expected contactless card types.
     *
     * @param contactlessCardTypes Contactless card types. See {@link ContactlessCardType}.
     */
    public void setContactlessCardTypes(ContactlessCardType[] contactlessCardTypes) {
        this.contactlessCardTypes = contactlessCardTypes;
    }

    /**
     * Whether or not to verify track data.
     *
     * @return Whether or not to verify track data. It is 'true' by default.
     */
    public boolean isVerifyTrack() {
        return isVerifyTrack;
    }

    /**
     * Sets whether or not to verify track data.
     *
     * @param verifyTrack Whether or not to verify track data. It is 'true' by default.
     */
    public void setVerifyTrack(boolean verifyTrack) {
        isVerifyTrack = verifyTrack;
    }

    /**
     * Gets parameters for type F card.
     *
     * @return Parameters for type F card.
     */
    public byte[] getTypeFParameters() {
        return typeFParameters;
    }

    /**
     * Sets parameters for type F card.
     *
     * @param typeFParameters Parameters for type F card.
     */
    public void setTypeFParameters(byte[] typeFParameters) {
        this.typeFParameters = typeFParameters;
    }

    /**
     * Gets parameters for type V card.
     *
     * @return Parameters for type V card.
     */
    public byte[] getTypeVParameters() {
        return typeVParameters;
    }

    /**
     * Sets parameters for type V card.
     *
     * @param typeVParameters Parameters for type V card.
     */
    public void setTypeVParameters(byte[] typeVParameters) {
        this.typeVParameters = typeVParameters;
    }

    /**
     * Gets the contact card slots to be search for.
     * @return The contact card slots to be search for.
     */
    public ContactCardSlot[] getCardSlots() {
        return cardSlots;
    }

    /**
     * Sets the contact card slots to be search for
     * @param cardSlots Contact card slots to be search for, default is {@link ContactCardSlot#IC1}.
     */
    public void setCardSlots(ContactCardSlot[] cardSlots) {
        this.cardSlots = cardSlots;
    }

    /**
     * Gets the current card search mode.
     * @return The current card search mode.
     */
    public CardSearchMode getCardSearchMode() {
        return cardSearchMode;
    }

    /**
     * Sets the card search mode for the next card detection process.
     * @param cardSearchMode The card search mode for the next card detection process.See {@link CardSearchMode}.
     */
    public void setCardSearchMode(CardSearchMode cardSearchMode) {
        this.cardSearchMode = cardSearchMode;
    }
}

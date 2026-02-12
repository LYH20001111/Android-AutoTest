package com.newland.nsdk.core.external.command.contactlesscard;

import com.newland.nsdk.core.api.common.card.contactless.ActivationResult;
import com.newland.nsdk.core.api.common.card.contactless.ContactlessCardType;

public class ContactlessCardResult extends ActivationResult {
    private ContactlessCardType cardType;

    public ContactlessCardType getCardType() {
        return cardType;
    }

    public void setCardType(ContactlessCardType cardType) {
        this.cardType = cardType;
    }
}

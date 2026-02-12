package com.newland.nsdk.core.external.card.contact;

import com.newland.nsdk.core.api.common.card.contact.ContactCardSlot;
import com.newland.nsdk.core.api.common.card.contact.ContactCardType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.external.card.contact.ExtContactCard;
import com.newland.nsdk.core.external.command.smartcard.ExternalSmartCardModule;

public class ExtContactCardImpl implements ExtContactCard {
    private ContactCardType cardType;
    private ContactCardSlot slot;
    private ExternalSmartCardModule externalSmartCardModule;
    public ExtContactCardImpl(ContactCardSlot slot, ContactCardType cardType){
        this.slot = slot;
        this.cardType = cardType;
        this.externalSmartCardModule = new ExternalSmartCardModule();
    }

    @Override
    public byte[] powerUp() throws NSDKException{
        return externalSmartCardModule.powerUp(0, null);
    }

    @Override
    public void powerDown() throws NSDKException{
        externalSmartCardModule.powerDown();
    }

    public ContactCardSlot getSlot() {
        return this.slot;
    }

    public ContactCardType getCardType() {
        return this.cardType;
    }
}

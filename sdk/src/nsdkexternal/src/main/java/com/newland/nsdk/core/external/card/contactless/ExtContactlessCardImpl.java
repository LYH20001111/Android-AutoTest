package com.newland.nsdk.core.external.card.contactless;

import com.newland.nsdk.core.api.common.card.contactless.ActivationResult;
import com.newland.nsdk.core.api.common.card.contactless.ContactlessCardType;
import com.newland.nsdk.core.api.common.card.contactless.SubContactlessCardType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.external.card.contactless.ExtContactlessCard;
import com.newland.nsdk.core.external.command.contactlesscard.ExternalContactlessCardModule;

public class ExtContactlessCardImpl implements ExtContactlessCard {
    protected ExternalContactlessCardModule externalContactlessCardModule;
    private SubContactlessCardType cardType;
    public ExtContactlessCardImpl(SubContactlessCardType cardType){
        this.cardType = cardType;
        this.externalContactlessCardModule = new ExternalContactlessCardModule();
    }

    @Override
    public ActivationResult activate() throws NSDKException {
        return externalContactlessCardModule.activate(this.cardType);
    }

    @Override
    public void deactivate() throws NSDKException{
        externalContactlessCardModule.deactivate();
    }

    public ContactlessCardType getCardType() {
        return null;
    }
}

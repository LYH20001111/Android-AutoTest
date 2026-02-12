package com.newland.nsdk.plugin.card.external.contactless;

import com.newland.nsdk.core.api.common.card.contactless.ActivationResult;
import com.newland.nsdk.core.api.common.card.contactless.SubContactlessCardType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.external.command.contactlesscard.ExternalContactlessCardModule;
import com.newland.nsdk.core.external.card.contactless.ExtContactlessCardImpl;
import com.newland.nsdk.plugin.card.api.external.contactless.ExtFelicaCard;

public class ExtFelicaCardImpl implements ExtFelicaCard {
    private ExtContactlessCardImpl extContactlessCard;
    private ExternalContactlessCardModule externalContactlessCardModule;
    public ExtFelicaCardImpl() {
        this.extContactlessCard = new ExtContactlessCardImpl(SubContactlessCardType.FELICA);
        this.externalContactlessCardModule = new ExternalContactlessCardModule();
    }

    @Override
    public byte[] transmit(byte[] command) throws NSDKException {
        if (command == null) {
            throw new NSDKIllegalParameterException("Command should not be null!");
        }

        return externalContactlessCardModule.exchangeFelicaAPDU(command);
    }

    @Override
    public ActivationResult activate() throws NSDKException {
        // No need to activate Felica card.
        return null;
    }

    @Override
    public void deactivate() throws NSDKException {
        this.extContactlessCard.deactivate();
    }
}

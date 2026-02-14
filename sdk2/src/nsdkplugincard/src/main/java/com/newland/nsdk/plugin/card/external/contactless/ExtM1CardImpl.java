package com.newland.nsdk.plugin.card.external.contactless;

import com.newland.nsdk.core.api.common.card.contactless.ActivationResult;
import com.newland.nsdk.core.api.common.card.contactless.SubContactlessCardType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.external.command.contactlesscard.ExternalContactlessCardModule;
import com.newland.nsdk.core.external.card.contactless.ExtContactlessCardImpl;
import com.newland.nsdk.plugin.card.api.common.contactless.ContactlessKeyMode;
import com.newland.nsdk.plugin.card.api.external.contactless.ExtM1Card;

public class ExtM1CardImpl implements ExtM1Card {
    private ExtContactlessCardImpl extContactlessCard;
    private ExternalContactlessCardModule externalContactlessCardModule;

    public ExtM1CardImpl() {
        this.extContactlessCard = new ExtContactlessCardImpl(SubContactlessCardType.M1);
        this.externalContactlessCardModule = new ExternalContactlessCardModule();
    }

    @Override
    public void authenticate(ContactlessKeyMode keyMode, byte[] uid, byte blockNo, byte[] key) throws NSDKException {
        if (keyMode == null || uid == null || key == null) {
            throw new NSDKIllegalParameterException("Contactless key mode, uid and key should not be null!");
        }

        externalContactlessCardModule.authenticateWithExternalKey((byte) keyMode.getCode(), uid, blockNo, key);
    }

    @Override
    public byte[] readBlockData(byte blockNo) throws NSDKException {
        return externalContactlessCardModule.readBlock(blockNo);
    }

    @Override
    public void writeBlockData(byte blockNo, byte[] data) throws NSDKException {
        if (data == null) {
            throw new NSDKIllegalParameterException("Data should not be null !");
        }
        externalContactlessCardModule.writeBlock(blockNo, data);
    }

    @Override
    public void increment(byte blockNo, byte[] data) throws NSDKException {
        if (data == null) {
            throw new NSDKIllegalParameterException("Data should not be null !");
        }
        externalContactlessCardModule.increment(blockNo, data);
    }

    @Override
    public void decrement(byte blockNo, byte[] data) throws NSDKException {
        if (data == null) {
            throw new NSDKIllegalParameterException("Data should not be null !");
        }

        externalContactlessCardModule.decrement(blockNo, data);
    }

    @Override
    public void transfer(byte blockNo) throws NSDKException {
        externalContactlessCardModule.transfer(blockNo);
    }

    @Override
    public void restore(byte blockNo) throws NSDKException {
        externalContactlessCardModule.restore(blockNo);
    }

    @Override
    public ActivationResult activate() throws NSDKException {
        return this.extContactlessCard.activate();
    }

    @Override
    public void deactivate() throws NSDKException {
        this.extContactlessCard.deactivate();
    }
}

package com.newland.nsdk.core.external.card.contactless;

import com.newland.nsdk.core.api.common.card.contactless.ActivationResult;
import com.newland.nsdk.core.api.common.card.contactless.ContactlessCardType;
import com.newland.nsdk.core.api.common.card.contactless.SubContactlessCardType;
import com.newland.nsdk.core.api.common.crypto.AlgorithmParameters;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.keymanager.Key;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.external.card.ExtAPDUOutput;
import com.newland.nsdk.core.api.external.card.contactless.ExtCPUContactlessCard;
import com.newland.nsdk.core.external.command.contactlesscard.ExternalContactlessCardModule;

public class ExtCPUContactlessCardImpl implements ExtCPUContactlessCard {
    private ExtContactlessCardImpl extContactlessCard;
    private ExternalContactlessCardModule externalContactlessCardCommand;
    public ExtCPUContactlessCardImpl() {
        this.extContactlessCard = new ExtContactlessCardImpl(SubContactlessCardType.CPU);
        this.externalContactlessCardCommand = new ExternalContactlessCardModule();
    }

    @Override
    public ExtAPDUOutput performAPDU(Key key, AlgorithmParameters algorithmParameters, byte[] command) throws NSDKException {
        if (command == null) {
            throw new NSDKIllegalParameterException("Command should not be null!");
        }

        if (key == null || key.getKeyID() == 0) {
            ExtAPDUOutput result = new ExtAPDUOutput();
            byte[] resultData = externalContactlessCardCommand.exchangeClearAPDU(command);
            result.setData(resultData);
            if (resultData != null && resultData.length > 0) {
                result.setDataLen(resultData.length);
            }
            return result;
        }
        return externalContactlessCardCommand.exchangeAPDU(key.getKeyID(), command);
    }

    @Override
    public ExtAPDUOutput performAPDU(Key key, AlgorithmParameters algorithmParameters, int actualLen, byte[] command) throws NSDKException {
        if (command == null || command.length == 0) {
            throw new NSDKIllegalParameterException("Command should not be null!");
        }
        if(actualLen <= 0 || actualLen > command.length) {
            throw new NSDKIllegalParameterException("The actual length error!");
        }
        if (key == null || key.getKeyID() == 0) {
            if(actualLen != command.length) {
                throw new NSDKIllegalParameterException("The actual length error!");
            }
            ExtAPDUOutput result = new ExtAPDUOutput();
            byte[] resultData = externalContactlessCardCommand.exchangeClearAPDU(command);
            result.setData(resultData);
            if (resultData != null && resultData.length > 0) {
                result.setDataLen(resultData.length);
            }
            return result;
        }
        return externalContactlessCardCommand.exchangeAPDU(key.getKeyID(), actualLen, command);
    }


    @Override
    public ActivationResult activate() throws NSDKException {
        return this.extContactlessCard.activate();
    }

    @Override
    public void deactivate() throws NSDKException {
        this.extContactlessCard.deactivate();
    }

    public ContactlessCardType getCardType() {
        return this.extContactlessCard.getCardType();
    }
}

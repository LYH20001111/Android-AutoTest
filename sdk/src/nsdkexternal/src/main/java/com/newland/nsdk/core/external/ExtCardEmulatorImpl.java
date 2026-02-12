package com.newland.nsdk.core.external;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.external.cardemulator.EmulateCardStatus;
import com.newland.nsdk.core.api.external.cardemulator.EmulateCardType;
import com.newland.nsdk.core.api.external.cardemulator.EmulateConfig;
import com.newland.nsdk.core.api.external.cardemulator.EmulateEventType;
import com.newland.nsdk.core.api.external.cardemulator.EmulateFileType;
import com.newland.nsdk.core.api.external.cardemulator.ExtCardEmulator;
import com.newland.nsdk.core.external.command.cardemulator.ExtCardEmulatorModule;
import com.newland.nsdk.core.external.command.message.ExternalMessage;

public class ExtCardEmulatorImpl implements ExtCardEmulator {
    private ExtCardEmulatorModule extCardEmulatorModule;
    private static volatile ExtCardEmulatorImpl instance;

    public static ExtCardEmulatorImpl getInstance() {
        if (instance == null) {
            synchronized (ExtCardEmulatorImpl.class) {
                if (instance == null) {
                    instance = new ExtCardEmulatorImpl();
                }
            }
        }
        return instance;
    }

    public ExtCardEmulatorImpl() {
        this.extCardEmulatorModule = new ExtCardEmulatorModule();
    }
    @Override
    public void init() throws NSDKException {
        extCardEmulatorModule.init();
    }

    @Override
    public void start(EmulateCardType cardType) throws NSDKException {
        if (cardType == null) {
            throw new NSDKIllegalParameterException("Emulate card type shall not be null");
        }
        extCardEmulatorModule.start(cardType.ordinal());

    }

    @Override
    public EmulateCardStatus getStatus(EmulateCardType cardType) throws NSDKException {
        if (cardType == null) {
            throw new NSDKIllegalParameterException("Emulate card type shall not be null");
        }
        int status = extCardEmulatorModule.getStatus(cardType.ordinal());
        for (EmulateCardStatus emulateCardStatus : EmulateCardStatus.values()) {
            if (emulateCardStatus.getCode() == status) {
                return emulateCardStatus;
            }
        }
        return null;
    }

    @Override
    public void setConfig(EmulateCardType cardType, EmulateConfig config) throws NSDKException {
        if (cardType == null) {
            throw new NSDKIllegalParameterException("Emulate card type shall not be null");
        }
        if (config == null) {
            throw new NSDKIllegalParameterException("Emulate config shall not be null.");
        }
        extCardEmulatorModule.writeConfig(cardType.ordinal(), config);
    }

    @Override
    public void writeData(EmulateFileType fileType, byte[] data) throws NSDKException {
        if (fileType == null) {
            throw new NSDKIllegalParameterException("Emulate file type shall not be null.");
        }
        if (data == null || data.length == 0) {
            throw new NSDKIllegalParameterException("Data shall not be null.");
        }
        extCardEmulatorModule.writeData(fileType.ordinal(), data);
    }

    @Override
    public byte[] readData(EmulateFileType fileType, int readLength) throws NSDKException {
        if (fileType == null) {
            throw new NSDKIllegalParameterException("Emulate file type shall not be null.");
        }
        return extCardEmulatorModule.readData(fileType.ordinal(), readLength);
    }

    @Override
    public EmulateConfig getConfig(EmulateCardType cardType) throws NSDKException {
        if (cardType == null) {
            throw new NSDKIllegalParameterException("Emulate card type shall not be null.");
        }
        return extCardEmulatorModule.getConfig(cardType.ordinal());
    }

    @Override
    public void finish() throws NSDKException {
        extCardEmulatorModule.finish();
    }

    @Override
    public byte[] getEvent(EmulateEventType eventType) throws NSDKException {
        if (eventType == null) {
            throw new NSDKIllegalParameterException("Emulate event type shall not be null");
        }

        return extCardEmulatorModule.getEvent(eventType.ordinal() + 1);
    }
}

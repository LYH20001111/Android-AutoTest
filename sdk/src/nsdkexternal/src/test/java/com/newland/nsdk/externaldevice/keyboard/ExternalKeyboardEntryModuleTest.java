package com.newland.nsdk.externaldevice.keyboard;


import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.pinentry.PINBlockMode;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicator;
import com.newland.nsdk.core.api.external.keyboard.KeyboardMode;
import com.newland.nsdk.core.api.external.keyboard.KeyboardParameters;
import com.newland.nsdk.core.api.external.keyboard.PromptID;
import com.newland.nsdk.core.api.external.pinentry.CipherPAN;
import com.newland.nsdk.core.api.external.pinentry.ExtPINEntryParameters;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.communication.mock.MockExternalCommunicator;
import com.newland.nsdk.core.external.command.keyboard.ExternalKeyboardEntryModule;
import com.newland.nsdk.core.external.command.keyboard.KeyboardEntryResult;

import org.junit.Before;
import org.junit.Test;

public class ExternalKeyboardEntryModuleTest {

    private ExternalKeyboardEntryModule keyboardEntryModule = new ExternalKeyboardEntryModule();

    @Before
    public void init(){
        ExternalCommunicator communicator = new MockExternalCommunicator();
        ExternalCommunicationManager.getInstance().setCommunicator(communicator);
    }

    @Test
    public void setPinLine() {
        try {
//            keyboardEntryModule.setPinLine((byte) 0);
//            keyboardEntryModule.setPinLine((byte) 6);
            keyboardEntryModule.setPinLine((byte) 4);
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void pinEntry() {
        byte[] pan = new byte[]{0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F};
        SymmetricKey pinKey = new SymmetricKey();
        pinKey.setKeyID((byte)1);
        pinKey.setKeyType(KeyType.DES);

        CipherPAN cipherPan = new CipherPAN();
        SymmetricKey panKey = new SymmetricKey();
        panKey.setKeyID((byte) 2);
        panKey.setKeyType(KeyType.AES);
        cipherPan.setPANKey(panKey);
        cipherPan.setCipherPAN(pan);

        int timeout = 60;

        ExtPINEntryParameters parameter = new ExtPINEntryParameters();
        parameter.setMaxPINLen((byte)6);
        parameter.setAutoComplete(false);
        parameter.setPINBlockMode(PINBlockMode.ISO9564_1);
        parameter.setDisplayMessages(new String[]{"line1", null, "line3"});
        try {
            KeyboardEntryResult result = keyboardEntryModule.pinEntry(pinKey, null, cipherPan, parameter, timeout);
            if (result.getEncryptedPinBlock() != null) {
                System.out.println("Encrypted PIN block: " + ISOUtils.hexString(result.getEncryptedPinBlock()));
            }

            if (result.getKsn() != null) {
                System.out.println("KSN: " + ISOUtils.hexString(result.getKsn()));
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void cancelPinEntry() {
        try {
            keyboardEntryModule.cancelPinEntry();
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void extendedPinEntry() {
        String pan = "6214055910236311";
        SymmetricKey key = new SymmetricKey();
        key.setKeyID((byte)4);
        key.setKeyType(KeyType.DES);
        ExtPINEntryParameters parameter = new ExtPINEntryParameters();
        parameter.setPINBlockMode(PINBlockMode.ISO9564_0);
        parameter.setMaxPINLen((byte)6);
        parameter.setAutoComplete(false);
        parameter.setDisplayMessages(new String[]{"line1", null, "line3"});
        try {
            KeyboardEntryResult result = keyboardEntryModule.extendedPinEntry(key, pan, parameter, 15, true);
            if (result.getEncryptedPinBlock() != null) {
                System.out.println("Encrypted PIN block: " + ISOUtils.hexString(result.getEncryptedPinBlock()));
            }

            if (result.getEncryptedRandomPinKey() != null) {
                System.out.println("Random PIN key: " + ISOUtils.hexString(result.getEncryptedRandomPinKey()));
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sensitiveDataEntry() {
        KeyboardParameters parameter = new KeyboardParameters();
        parameter.setPromptID(PromptID.PHONE_NUMBER);
        parameter.setKeyboardMode(KeyboardMode.ALL_CHARACTERS);
        parameter.setMinLen((byte)6);
        parameter.setMaxLen((byte)10);
        SymmetricKey dataKey = new SymmetricKey();
        dataKey.setKeyID((byte) 4);
        dataKey.setKeyType(KeyType.DES);
        try {
            KeyboardEntryResult result = keyboardEntryModule.sensitiveDataEntry(dataKey,(byte)10,parameter);
            System.out.println("Data len: " + result.getDataLen());

            if (result.getEncryptedData() != null) {
                System.out.println("Encrypted data: " + ISOUtils.hexString(result.getEncryptedData()));
            }
        } catch (NSDKException e) {
            e.printStackTrace();
        }
    }
}
package com.newland.nsdk.plugin.dukptmacverification;


import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.crypto.MACOutput;
import com.newland.nsdk.core.api.common.crypto.MACType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.keymanager.KeyInfoID;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.internal.NSDKModuleManagerImpl;
import com.newland.nsdk.core.api.external.communication.DukptMacHandler;
import com.newland.nsdk.core.external.command.exception.ExternalMessageException;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;
import com.newland.nsdk.core.api.internal.crypto.Crypto;
import com.newland.nsdk.core.api.internal.keymanager.KeyManager;

import java.util.Arrays;

public class NSDKDukptMacHandler implements DukptMacHandler {
    public static final String TAG = "NsdkDukptMacHelper";
    private Crypto cipher;
    private KeyManager keyManager;
    private SymmetricKey dukptKey;
    private MACType macType;
    public NSDKDukptMacHandler(){
        dukptKey = new SymmetricKey();
        dukptKey.setKeyID((byte) 1);
        dukptKey.setKeyType(KeyType.DES);
        dukptKey.setKeyUsage(KeyUsage.DUKPT);

        macType = MACType.DUKPT_X99;
    }

    @Override
    public byte[] getKsn() throws NSDKException {
        if (keyManager == null) {
            keyManager = (KeyManager) NSDKModuleManagerImpl.getInstance().getModule(ModuleType.KEY_MANAGER);
        }
        return keyManager.getKeyInfo(KeyInfoID.KSN, dukptKey);
    }

    @Override
    public void increaseKsn() throws NSDKException {
        keyManager.increaseKSN(dukptKey.getKeyID());
    }

    @Override
    public byte[] generateMac(byte[] data) throws NSDKException {
        if (cipher == null) {
            cipher = (Crypto) NSDKModuleManagerImpl.getInstance().getModule(ModuleType.CRYPTO);
        }

        LogUtils.d(TAG, String.format("******** Data used to generate Mac: %s", ISOUtils.hexString(data)));
        LogUtils.d(TAG, String.format("******** Data length: %d", data.length));
        LogUtils.d(TAG, String.format("******** Key id used to generate Mac: %s", dukptKey.getKeyID()));
        LogUtils.d(TAG, String.format("******** Mac type used to generate Mac: %s", macType));
        MACOutput result = cipher.generateMAC(dukptKey.getKeyID(), macType, null, data);
        LogUtils.d(TAG, String.format("******** Generated Mac: %s", ISOUtils.hexString(result.getData())));
        return result.getData();
    }

    @Override
    public void checkKsn(byte[] ksn, String messageType) throws NSDKException {
        byte[] currentKsn = getKsn();
        boolean isEqual = Arrays.equals(ksn, currentKsn);
        boolean isValid = isEqual;
        if (ExternalMessageType.DUKPT_KSN_INCREASE_RESPONSE.equals(messageType)) {
            isValid = !isEqual;
        }

        if (!isValid) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_INVALID_KSN,
                    String.format("Response KSN(%s) is not valid(%s).",
                            ISOUtils.hexString(ksn),
                            ISOUtils.hexString(currentKsn)));
        }
    }

    @Override
    public void checkMac(byte[] data, byte[] mac, String messageType) throws NSDKException {
        if (ExternalMessageType.DUKPT_KSN_INCREASE_RESPONSE.equals(messageType)) {
            try {
                increaseKsn();
            } catch (Exception e) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_INVALID_KSN, "Failed to increase upper device KSN.", e);
            }
        }
        byte[] tempMac = generateMac(data);
        boolean isEqual = Arrays.equals(mac, tempMac);
        if (!isEqual) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_INVALID_KSN,
                    String.format("Response MAC(%s) is not equal with calculated MAC(%s).",
                            ISOUtils.hexString(mac),
                            ISOUtils.hexString(tempMac)));
        }
    }

    public SymmetricKey getDukptKey() {
        return dukptKey;
    }

    public void setDukptKey(SymmetricKey dukptKey) {
        this.dukptKey = dukptKey;
    }

    public MACType getMacType() {
        return macType;
    }

    public void setMacType(MACType macType) {
        this.macType = macType;
    }
}

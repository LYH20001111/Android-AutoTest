package com.newland.nsdk.core.internal.card.contactless;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.card.contactless.ActivationResult;
import com.newland.nsdk.core.api.common.card.contactless.SubContactlessCardType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.card.contactless.ContactlessCard;
import com.newland.nsdk.core.api.internal.devicemanager.DeviceInfo;
import com.newland.nsdk.core.api.internal.devicemanager.DeviceManager;
import com.newland.nsdk.core.api.internal.exception.NSDKNDKException;
import com.newland.nsdk.core.internal.NSDKModuleManagerImpl;
import com.newland.nsdk.core.internal.jni.NSDKJni;

import java.util.Locale;

public class ContactlessCardImpl implements ContactlessCard {
    private static final String TAG = "ContactlessCardImpl";
    protected SubContactlessCardType cardType;

    public boolean isSupported;

    public ContactlessCardImpl(SubContactlessCardType cardType){
        this.cardType = cardType;
        try {
            DeviceManager deviceManager = (DeviceManager) NSDKModuleManagerImpl.getInstance().getModule(ModuleType.DEVICE_MANAGER);
            DeviceInfo deviceInfo = deviceManager.getDeviceInfo();
            isSupported = deviceInfo.isSupportQuickPass();
        } catch (NSDKException e){
            this.isSupported = false;
        }
    }

    private void isSupported() throws NSDKException {
        if(!isSupported){
            throw new NSDKException(ErrorCode.UNSUPPORTED, "UnSupported ContactlessCard Module");
        }
    }

    @Override
    public ActivationResult activate() throws NSDKException {
        isSupported();

        JNIActivationResult jniActivationResult = new JNIActivationResult();
        int ret = NSDKJni.getInstance().RFActivate(this.cardType.ordinal(), jniActivationResult);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            if (ret == ErrorCode.RFID_UPED) {
                throw new NSDKNDKException(ret, "Please deactivate card-reader first");
            } else {
                throw new NSDKNDKException(ret, "Failed to activate.");
            }

        }

        ActivationResult result = new ActivationResult();
        int len = jniActivationResult.getUidLen();
        byte[] tmp = null;
        if (len > 0) {
            tmp = new byte[len];
            System.arraycopy(jniActivationResult.getUID(), 0, tmp, 0, len);
        }
        result.setUID(tmp);
        LogUtils.d(TAG, String.format(Locale.US, "UID=%s", tmp == null ? "null" : ISOUtils.hexString(tmp)));

        tmp = null;
        len = jniActivationResult.getAtqaLen();
        if (len > 0) {
            tmp = new byte[len];
            System.arraycopy(jniActivationResult.getATQA(), 0, tmp, 0, len);
        }
        result.setATQA(tmp);
        LogUtils.d(TAG, String.format(Locale.US, "ATQA=%s", tmp == null ? "null" : ISOUtils.hexString(tmp)));

        tmp = null;
        len = jniActivationResult.getAtsLen();
        if (len > 0) {
            tmp = new byte[len];
            System.arraycopy(jniActivationResult.getATS(), 0, tmp, 0, len);
        }
        result.setATS(tmp);
        LogUtils.d(TAG, String.format(Locale.US, "ATS=%s", tmp == null ? "null" : ISOUtils.hexString(tmp)));

        tmp = null;
        len = jniActivationResult.getAtqbLen();
        if (len > 0) {
            tmp = new byte[len];
            System.arraycopy(jniActivationResult.getATQB(), 0, tmp, 0, len);
        }
        result.setATQB(tmp);
        LogUtils.d(TAG, String.format(Locale.US, "ATQB=%s", tmp == null ? "null" : ISOUtils.hexString(tmp)));

        tmp = null;
        len = jniActivationResult.getSakLen();
        if (len > 0) {
            tmp = new byte[len];
            System.arraycopy(jniActivationResult.getSAK(), 0, tmp, 0, len);
        }
        result.setSAK(tmp);
        LogUtils.d(TAG, String.format(Locale.US, "SAK=%s", tmp == null ? "null" : ISOUtils.hexString(tmp)));

        return result;
    }

    @Override
    public void deactivate() throws NSDKException{
        isSupported();

        int ret = NSDKJni.getInstance().RFDeactivate();
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to deactivate card, result code = %d.", ret));
        }
    }

    public SubContactlessCardType getCardType() {
        return this.cardType;
    }
}

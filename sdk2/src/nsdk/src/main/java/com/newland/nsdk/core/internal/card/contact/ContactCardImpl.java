package com.newland.nsdk.core.internal.card.contact;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.card.contact.ContactCardConfig;
import com.newland.nsdk.core.api.common.card.contact.ContactCardSlot;
import com.newland.nsdk.core.api.common.card.contact.ContactCardType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.internal.card.contact.ContactCard;
import com.newland.nsdk.core.api.internal.devicemanager.DeviceInfo;
import com.newland.nsdk.core.api.internal.devicemanager.DeviceManager;
import com.newland.nsdk.core.api.internal.exception.NSDKNDKException;
import com.newland.nsdk.core.internal.NSDKModuleManagerImpl;
import com.newland.nsdk.core.internal.jni.NSDKJni;
import com.newland.nsdk.core.internal.system.SystemPropertyUtil;

import java.util.Locale;

public class ContactCardImpl implements ContactCard {
    protected ContactCardType cardType;
    protected ContactCardSlot slot;

    public boolean isSupportedIC;
    public boolean isSupportedSAM;

    public ContactCardImpl(ContactCardSlot slot, ContactCardType cardType){
        this.slot = slot;
        this.cardType = cardType;
        try {
            DeviceManager deviceManager = (DeviceManager) NSDKModuleManagerImpl.getInstance().getModule(ModuleType.DEVICE_MANAGER);
            DeviceInfo deviceInfo = deviceManager.getDeviceInfo();
            this.isSupportedIC = deviceInfo.isSupportICCard();
            this.isSupportedSAM = deviceInfo.isSupportSam();
        } catch (NSDKException e) {
            this.isSupportedIC = false;
            this.isSupportedSAM = false;
        }
    }

    private void isSupported() throws NSDKException {
        if((slot.name().contains("IC") && !isSupportedIC) || (slot.name().contains("SAM") && !isSupportedSAM)){
            throw new NSDKException(ErrorCode.UNSUPPORTED, "UnSupported ContactCard Module");
        }
    }

    @Override
    public void setConfig(ContactCardConfig config) throws NSDKException {
        isSupported();
        if (config == null) {
            throw new NSDKIllegalParameterException("ContactCardConfig shall not be null");
        }
        String enableClkChange = SystemPropertyUtil.getProperty("persist.sys.samspeed_limit", "disable");
        if ("enable".equals(enableClkChange)) {
            int ret = NSDKJni.getInstance().ICSetConfig(ContactCardSlot.SAM1.ordinal(), 5, config.getSamClkFrequency().getCode());
            if (ret != ErrorCode.OK) {
                throw new NSDKException(ret, String.format(Locale.US, "Failed to set config, ret = %d", ret));
            }
            ret = NSDKJni.getInstance().ICSetConfig(ContactCardSlot.SAM2.ordinal(), 5, config.getSamClkFrequency().getCode());
            if (ret != ErrorCode.OK) {
                throw new NSDKException(ret, String.format(Locale.US, "Failed to set config, ret = %d", ret));
            }
        }
    }

    @Override
    public byte[] powerUp() throws NSDKException{
        isSupported();

        byte[] atr = new byte[512];
        int[] atrLen = new int[1];
        int ret = NSDKJni.getInstance().ICPowerUp(this.slot.ordinal(), this.cardType.getCode(), atr, atrLen);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to power up, result code = %d", ret));
        }

        byte[] atrResult = new byte[atrLen[0]];
        System.arraycopy(atr, 0, atrResult, 0, atrLen[0]);
        return atrResult;
    }

    @Override
    public void powerDown() throws NSDKException{
        isSupported();

        int ret = NSDKJni.getInstance().ICPowerDown(this.slot.ordinal(), this.cardType.getCode());
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to power down, result code = %d", ret));
        }
    }

    public ContactCardSlot getSlot() {
        return this.slot;
    }

    public ContactCardType getCardType() {
        return this.cardType;
    }
}

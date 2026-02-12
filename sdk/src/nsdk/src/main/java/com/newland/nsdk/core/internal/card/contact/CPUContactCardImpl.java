package com.newland.nsdk.core.internal.card.contact;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.card.contact.ContactCardConfig;
import com.newland.nsdk.core.api.common.card.contact.ContactCardSlot;
import com.newland.nsdk.core.api.common.card.contact.ContactCardType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.internal.card.contact.CPUContactCard;
import com.newland.nsdk.core.api.internal.exception.NSDKNDKException;
import com.newland.nsdk.core.internal.jni.NSDKJni;

import java.util.Locale;

public class CPUContactCardImpl implements CPUContactCard {
    private ContactCardImpl contactCard;

    public CPUContactCardImpl(ContactCardSlot slot) {
        contactCard = new ContactCardImpl(slot, ContactCardType.CPU);
    }

    private void isSupported() throws NSDKException {
        if((contactCard.slot.name().contains("IC") && !contactCard.isSupportedIC) || (contactCard.slot.name().contains("SAM") && !contactCard.isSupportedSAM)){
            throw new NSDKException(ErrorCode.UNSUPPORTED, "UnSupported CPUContactCard Module");
        }
    }

    @Override
    public byte[] performAPDU(byte[] command) throws NSDKException {
        isSupported();

        if (command == null) {
            throw new NSDKIllegalParameterException("Command should not be null!");
        }
        byte[] recv = new byte[8192];
        int[] len = new int[1];
        int ret = NSDKJni.getInstance().ICPerformAPDU(this.contactCard.getSlot().ordinal(), this.contactCard.getCardType().getCode(), command, command.length, recv, len);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to perform APDU command, result code = %d", ret));
        }

        byte[] recvTarget = new byte[len[0]];
        System.arraycopy(recv, 0, recvTarget, 0, len[0]);

        return recvTarget;
    }

    @Override
    public void setConfig(ContactCardConfig config) throws NSDKException {
        isSupported();
        contactCard.setConfig(config);
    }

    @Override
    public byte[] powerUp() throws NSDKException {
        isSupported();

        return contactCard.powerUp();
    }

    @Override
    public void powerDown() throws NSDKException {
        isSupported();

        contactCard.powerDown();
    }
}

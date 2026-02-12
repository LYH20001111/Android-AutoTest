package com.newland.nsdk.core.internal.card.contactless;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.card.contactless.ActivationResult;
import com.newland.nsdk.core.api.common.card.contactless.SubContactlessCardType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.internal.card.contactless.CPUContactlessCard;
import com.newland.nsdk.core.api.internal.exception.NSDKNDKException;
import com.newland.nsdk.core.internal.jni.NSDKJni;

public class CPUContactlessCardImpl implements CPUContactlessCard {
    private ContactlessCardImpl contactlessCard;

    public CPUContactlessCardImpl() {
        contactlessCard = new ContactlessCardImpl(SubContactlessCardType.CPU);
    }

    private void isSupported() throws NSDKException {
        if(!contactlessCard.isSupported){
            throw new NSDKException(ErrorCode.UNSUPPORTED, "UnSupported CPUContactlessCard Module");
        }
    }

    @Override
    public byte[] performAPDU(byte[] command) throws NSDKException {
        isSupported();

        if (command == null) {
            throw new NSDKIllegalParameterException();
        }
        byte[] recv = new byte[8192];
        int[] recvLen = new int[1];
        int ret = NSDKJni.getInstance().RFPerformAPDU(command, command.length, recv, recvLen);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, "Failed to perform APDU command.");
        }
        byte[] recvBuf = null;
        int len = recvLen[0];
        if (len > 0) {
            recvBuf = new byte[len];
            System.arraycopy(recv, 0, recvBuf, 0, len);
        }
        return recvBuf;
    }

    @Override
    public ActivationResult activate() throws NSDKException {
        isSupported();

        return contactlessCard.activate();
    }

    @Override
    public void deactivate() throws NSDKException {
        isSupported();

        contactlessCard.deactivate();
    }
}

package com.newland.nsdk.plugin.card.internal.contactless;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.card.contactless.ActivationResult;
import com.newland.nsdk.core.api.common.card.contactless.SubContactlessCardType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.internal.exception.NSDKNDKException;
import com.newland.nsdk.core.internal.card.contactless.ContactlessCardImpl;
import com.newland.nsdk.core.internal.jni.NSDKJni;
import com.newland.nsdk.plugin.card.api.internal.contactless.M0Card;

public class M0CardImpl implements M0Card {
    private ContactlessCardImpl contactlessCard;
    public M0CardImpl() {
        this.contactlessCard = new ContactlessCardImpl(SubContactlessCardType.M0);
    }
    @Override
    public void authenticate(byte[] key) throws NSDKException {
        if (key == null) {
            throw new NSDKIllegalParameterException();
        }
        int ret = NSDKJni.getInstance().RFM0Authenticate(key);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format("Failed to authenticate M0 card, result code = %d.", ret));
        }
    }

    @Override
    public byte[] readBlockData(byte blockNo) throws NSDKException {
        int blockNoInt = blockNo & 0xFF;
        if (blockNoInt < 0) {
            throw new NSDKIllegalParameterException("Block number shall be >=0.");
        }
        byte[] recv = new byte[4096];
        int[] recvLen = new int[1];
        int ret = NSDKJni.getInstance().RFM0ReadBlockData(blockNoInt, recv, recvLen);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format("Failed to read block data of M0 card, result code = %d.", ret));
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
    public void writeBlockData(byte blockNo, byte[] data) throws NSDKException {
        int blockNoInt = blockNo & 0xFF;
        if (blockNoInt < 0) {
            throw new NSDKIllegalParameterException("Block number shall be >=0.");
        }
        if (data == null) {
            throw new NSDKIllegalParameterException();
        }
        int ret = NSDKJni.getInstance().RFM0WriteBlockData(blockNoInt, data);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format("Failed to write block data of M0 card, result code = %d.", ret));
        }
    }

    @Override
    public ActivationResult activate() throws NSDKException {
        return this.contactlessCard.activate();
    }

    @Override
    public void deactivate() throws NSDKException {
        this.contactlessCard.deactivate();
    }
}

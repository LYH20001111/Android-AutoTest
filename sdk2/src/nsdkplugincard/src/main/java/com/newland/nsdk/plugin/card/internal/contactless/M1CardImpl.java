package com.newland.nsdk.plugin.card.internal.contactless;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.card.contactless.ActivationResult;
import com.newland.nsdk.core.api.common.card.contactless.SubContactlessCardType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.internal.exception.NSDKNDKException;
import com.newland.nsdk.core.internal.card.contactless.ContactlessCardImpl;
import com.newland.nsdk.core.internal.jni.NSDKJni;
import com.newland.nsdk.plugin.card.api.common.contactless.ContactlessKeyMode;
import com.newland.nsdk.plugin.card.api.internal.contactless.M1Card;

public class M1CardImpl implements M1Card {
    private ContactlessCardImpl contactlessCard;
    public M1CardImpl() {
        this.contactlessCard = new ContactlessCardImpl(SubContactlessCardType.M1);
    }

    @Override
    public void authenticate(ContactlessKeyMode keyMode, byte[] uid, byte blockNo, byte[] key) throws NSDKException {
        if (keyMode == null || uid == null || key == null) {
            throw new NSDKIllegalParameterException("Key mode, uid, key shall not be null.");
        }

        int blockNoInt = blockNo & 0xFF;
        if (blockNoInt < 0) {
            throw new NSDKIllegalParameterException("Block number shall be >=0.");
        }
        if (uid.length != 4 || key.length != 6) {
            throw new NSDKIllegalParameterException("UID length shall be 4, and key length shall be 6.");
        }
        int ret = NSDKJni.getInstance().RFM1Authenticate(keyMode.getCode(), uid, blockNoInt, key);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format("Failed to authenticate M1 card, result code = %d.", ret));
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
        int ret = NSDKJni.getInstance().RFM1ReadBlockData(blockNoInt, recv, recvLen);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format("Failed to read block data of M1 card, result code = %d.", ret));
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
            throw new NSDKIllegalParameterException("Data is null.");
        }
        int ret = NSDKJni.getInstance().RFM1WriteBlockData(blockNoInt, data);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format("Failed to write block data to M1 card, result code = %d.", ret));
        }
    }

    @Override
    public void increment(byte blockNo, byte[] data) throws NSDKException {
        int blockNoInt = blockNo & 0xFF;
        if (blockNoInt < 0) {
            throw new NSDKIllegalParameterException("Block number shall be >=0.");
        }
        if (data == null) {
            throw new NSDKIllegalParameterException("Data is null.");
        }
        if (data.length < 4) {
            throw new NSDKIllegalParameterException("Data length shall be no less than 4.");
        }
        int ret = NSDKJni.getInstance().RFM1Increment(blockNoInt, data);

        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format("Increment failed, result code = %d.", ret));
        }
    }

    @Override
    public void decrement(byte blockNo, byte[] data) throws NSDKException {
        int blockNoInt = blockNo & 0xFF;
        if (blockNoInt < 0) {
            throw new NSDKIllegalParameterException("Block number shall be >=0.");
        }
        if (data == null) {
            throw new NSDKIllegalParameterException("Data is null.");
        }
        if (data.length < 4) {
            throw new NSDKIllegalParameterException("Data length shall be no less than 4.");
        }
        int ret = NSDKJni.getInstance().RFM1Decrement(blockNoInt, data);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format("Decrement failed, result code = %d.", ret));
        }
    }

    @Override
    public void transfer(byte blockNo) throws NSDKException {
        int blockNoInt = blockNo & 0xFF;
        if (blockNoInt < 0) {
            throw new NSDKIllegalParameterException("Block number shall be >=0.");
        }
        int ret = NSDKJni.getInstance().RFM1Transfer(blockNoInt);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format("Failed to transfer, result code = %d.", ret));
        }
    }

    @Override
    public void restore(byte blockNo) throws NSDKException {
        int blockNoInt = blockNo & 0xFF;
        if (blockNoInt < 0) {
            throw new NSDKIllegalParameterException("Block number shall be >=0.");
        }
        int ret = NSDKJni.getInstance().RFM1Restore(blockNoInt);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format("Failed to restore, result code = %d.", ret));
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

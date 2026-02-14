package com.newland.nsdk.plugin.card.internal.contactless;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.card.contactless.ActivationResult;
import com.newland.nsdk.core.api.common.card.contactless.SubContactlessCardType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.internal.exception.NSDKNDKException;
import com.newland.nsdk.core.internal.card.contactless.ContactlessCardImpl;
import com.newland.nsdk.core.internal.jni.NSDKJni;
import com.newland.nsdk.plugin.card.api.internal.contactless.FelicaCard;

import java.util.Arrays;
import java.util.Locale;

public class FelicaCardImpl implements FelicaCard {
    private ContactlessCardImpl contactlessCard;
    public FelicaCardImpl() {
        this.contactlessCard = new ContactlessCardImpl(SubContactlessCardType.FELICA);
    }
    @Override
    public byte[] transmit(byte[] command) throws NSDKException {
        if (command == null) {
            throw new NSDKIllegalParameterException();
        }
        byte[] recv = new byte[4096];
        int[] recvLen = new int[1];
        int ret = NSDKJni.getInstance().RFFelicaTransmit(command, recv, recvLen);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, "Failed to perform APDU command with Felica card.");
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
    public byte[] transmit(byte[] command, int timeout) throws NSDKException {
        if (command == null || command.length == 0) {
            throw new NSDKIllegalParameterException("Command data shall not be null.");
        }
        if (timeout < 0) {
            throw new NSDKIllegalParameterException("Timeout shall be >=0.");
        }
        int ret = NSDKJni.getInstance().RFFelicaSetTimeout(timeout);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, "Failed to set FelicaCard Timeout, ret = %d", ret);
        }
        byte[] recv = new byte[4096];
        int[] recvLen = new int[1];
        ret = NSDKJni.getInstance().RFFelicaTransmit(command, recv, recvLen);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, "Failed to perform APDU command with Felica card.");
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
    public byte[] transmit(byte[] command, int timeout, int retryTimes) throws NSDKException {
        if (command == null || command.length == 0) {
            throw new NSDKIllegalParameterException("Command data shall not be null.");
        }
        if (timeout < 0) {
            throw new NSDKIllegalParameterException("Timeout shall be >=0.");
        }
        if (retryTimes < 0) {
            throw new NSDKIllegalParameterException("Retry times shall be >=0.");
        }
        byte[] recv = new byte[4096];
        int[] recvLen = new int[1];
        byte[] recvBuf = null;
        int ret = NSDKJni.getInstance().RFFelicaTransmit2(command, recv, recvLen, retryTimes, timeout);
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to perform APDU command with Felica card, ret = %d", ret));
        }

        int len = recvLen[0];
        if (len > 0) {
            recvBuf = new byte[len];
            System.arraycopy(recv, 0, recvBuf, 0, len);
        }
        return recvBuf;
    }

    @Override
    public byte[] polling(byte[] systemCode, byte requestCode, byte timeslot) throws NSDKException {
        if (systemCode == null || systemCode.length != 2) {
            throw new NSDKIllegalParameterException("SystemCode shall be 2 bytes.");
        }

        byte[] receiveData = new byte[128];
        int[] receiveDataLen = new int[1];
        int ret = NSDKJni.getInstance().RFFelicaPolling(systemCode, requestCode, timeslot, receiveData, receiveDataLen);
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to polling Felica card, ret = %d", ret));
        }
        int length = receiveDataLen[0];
        byte[] result = null;
        if (length > 0) {
            result = new byte[length];
            System.arraycopy(receiveData, 0, result, 0, length);
        }

        return result;
    }

    @Override
    public byte[] polling(byte[] systemCode, byte requestCode, byte timeslot, int timeout) throws NSDKException {
        if (systemCode == null || systemCode.length != 2) {
            throw new NSDKIllegalParameterException("SystemCode shall be 2 bytes.");
        }
        if (timeout < 0 || timeout > 5000) {
            throw new NSDKIllegalParameterException("Time out shall be between 0 to 5000 ms.");
        }

        byte[] receiveData = new byte[1024];
        int[] receiveDataLen = new int[1];

        int ret = NSDKJni.getInstance().RFFelicaSetTimeout(timeout);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to set Felica polling timeout, result code = %d", ret));
        }
        ret = NSDKJni.getInstance().RFFelicaPolling(systemCode, requestCode, timeslot, receiveData, receiveDataLen);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != 0) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to polling felica card[%d].", ret));
        }
        int len = receiveDataLen[0];
        if (len > 0) {
            return Arrays.copyOf(receiveData, receiveDataLen[0]);
        }
        return null;
    }

    @Override
    public ActivationResult activate() throws NSDKException {
        // No need to activate Felica card.
        return null;
    }

    @Override
    public void deactivate() throws NSDKException {
        this.contactlessCard.deactivate();
    }
}

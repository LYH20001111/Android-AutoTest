package com.newland.nsdk.core.external.command.signature;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.exception.ExternalMessageException;

public class HandshakeResult {
    private boolean ready;
    private byte[] softwareVersion;
    private String serialNumber;
    private boolean storageSupported;

    public static HandshakeResult unpack(byte[] data) throws NSDKException {
        if (data == null || data.length == 0) {
            throw new NSDKIllegalParameterException("Handshake result data is null or empty.");
        }

        int offset = 0;
        HandshakeResult result = new HandshakeResult();
        int dataLen = data.length;
        result.ready = data[0] == 1;
        offset++;

        if (!ExternalMessage.isDataEnough(offset, dataLen, 6)) {
            return result;
        }

        result.softwareVersion = new byte[6];
        System.arraycopy(data, offset, result.softwareVersion, 0, 6);
        offset += 6;

        if (!ExternalMessage.isDataEnough(offset, dataLen, 2)) {
            return result;
        }

        int snLen = Integer.valueOf(new String(new byte[]{data[offset], data[offset + 1]}));
        offset += 2;

        if (snLen > 0) {
            if (snLen > dataLen - offset) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
            }

            byte[] sn = new byte[snLen];
            System.arraycopy(data, offset, sn, 0, snLen);
            result.serialNumber = new String(sn);
            offset += snLen;
        }


        if (!ExternalMessage.isDataEnough(offset, dataLen, 1)) {
            return result;
        }

        result.storageSupported = data[offset] == 1;
        return result;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public byte[] getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(byte[] softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public boolean isStorageSupported() {
        return storageSupported;
    }

    public void setStorageSupported(boolean storageSupported) {
        this.storageSupported = storageSupported;
    }
}

package com.newland.nsdk.core.external;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.keymanager.InstalledKeyInfo;
import com.newland.nsdk.core.api.external.futurex.ExtFutureX;
import com.newland.nsdk.core.external.command.common.ExternalCommonModule;
import com.newland.nsdk.core.external.command.common.FileTransferUtil;
import com.newland.nsdk.core.external.command.futurex.ExternalFutureXModule;
import com.newland.nsdk.core.external.command.futurex.FutureXCommandType;

import java.security.MessageDigest;
import java.util.List;

public class ExtFutureXImpl implements ExtFutureX {
    private static final int MAX_DATA_LEN = 2000;
    private ExternalFutureXModule futurexModule;
    private ExternalCommonModule commonModule;
    private volatile static ExtFutureXImpl instance;
    public static ExtFutureXImpl getInstance() {
        if (instance == null) {
            synchronized (ExtFutureXImpl.class) {
                if (instance == null) {
                    instance = new ExtFutureXImpl();
                }
            }
        }
        return instance;
    }
    private ExtFutureXImpl(){
        futurexModule = new ExternalFutureXModule();
        commonModule = new ExternalCommonModule();
    }

    @Override
    public byte[] getPEDI() throws NSDKException {
        return futurexModule.get(FutureXCommandType.PEDI);
    }

    @Override
    public void setPEDI(byte[] data) throws NSDKException {
        if (data == null) {
            throw new NSDKIllegalParameterException("Data shall not be null.");
        }

        if (data.length > MAX_DATA_LEN) {
            transferFile(data);
            // 当数据超过最大限制，先用传输文件指令将数据发送下去，然后 setPEDI 的时候就不用再传数据下去了
            data = new byte[0];
        }

        futurexModule.set(FutureXCommandType.PEDI, data);
    }

    @Override
    public byte[] getPEDK() throws NSDKException {
        return futurexModule.get(FutureXCommandType.PEDK);
    }

    @Override
    public void setPEDK(byte[] data) throws NSDKException {
        if (data == null) {
            throw new NSDKIllegalParameterException("Data shall not be null.");
        }

        if (data.length > MAX_DATA_LEN) {
            transferFile(data);
            // 当数据超过最大限制，先用传输文件指令将数据发送下去，然后 setPEDK 的时候就不用再传数据下去了
            data = new byte[0];
        }

        futurexModule.set(FutureXCommandType.PEDK, data);
    }

    @Override
    public byte[] getPEDV() throws NSDKException {
        return futurexModule.get(FutureXCommandType.PEDV);
    }

    @Override
    public void setPEDV(byte[] data) throws NSDKException {
        if (data == null) {
            throw new NSDKIllegalParameterException("Data shall not be null.");
        }

        if (data.length > MAX_DATA_LEN) {
            transferFile(data);
            // 当数据超过最大限制，先用传输文件指令将数据发送下去，然后 setPEDV 的时候就不用再传数据下去了
            data = new byte[0];
        }

        futurexModule.set(FutureXCommandType.PEDV, data);
    }

    @Override
    public int getInstalledKeyNum() throws NSDKException {
        return futurexModule.getInstalledKeyNum();
    }

    @Override
    public List<InstalledKeyInfo> getInstalledKeyInfo() throws NSDKException {
        return futurexModule.getInstalledKeyInfo();
    }

    @Override
    public void setDeviceSignCertIndex(byte index) throws NSDKException {
        futurexModule.setDeviceInfo((byte) 1, new byte[]{index});
    }

    @Override
    public void setDeviceGroup(String name) throws NSDKException {
        futurexModule.setDeviceInfo((byte) 2, name.getBytes());
    }

    private void transferFile(byte[] data) throws NSDKException {
        List<byte[]> dataList = FileTransferUtil.splitData(data, MAX_DATA_LEN);
        byte[] hash;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            hash = digest.digest(data);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NSDKException(ErrorCode.EXT_ERROR, String.format("Failed to digest data: %s", e.getMessage()));
        }

        byte fileType = 6;
        int offset = 0;
        for (int i = 0; i < dataList.size(); i++) {
            commonModule.transferFile(fileType, ExternalCommonModule.FILE_WRITE_MODE_DATA, offset, dataList.get(i));
            offset += dataList.get(i).length;
        }

        commonModule.transferFile(fileType, ExternalCommonModule.FILE_WRITE_MODE_SHA1, 0, hash);
    }
}

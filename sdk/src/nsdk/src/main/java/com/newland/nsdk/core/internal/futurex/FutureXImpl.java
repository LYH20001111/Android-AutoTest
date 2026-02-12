package com.newland.nsdk.core.internal.futurex;

import android.text.TextUtils;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.keymanager.InstalledKeyInfo;
import com.newland.nsdk.core.api.internal.exception.NSDKNDKException;
import com.newland.nsdk.core.api.internal.futurex.FutureX;
import com.newland.nsdk.core.internal.jni.NSDKJni;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FutureXImpl implements FutureX {
    public boolean isSupported;

    private volatile static FutureXImpl instance;

    public static FutureXImpl getInstance(boolean isSupported) {
        if (instance == null) {
            synchronized (FutureXImpl.class) {
                if (instance == null || instance.isSupported != isSupported) {
                    instance = new FutureXImpl(isSupported);
                }
            }
        } else {
            if (instance.isSupported != isSupported) {
                instance = new FutureXImpl(isSupported);
            }
        }
        return instance;
    }

    private FutureXImpl(){
        this.isSupported = true;
    }

    private FutureXImpl(boolean isSupported){
        this.isSupported = isSupported;
    }

    private void isSupported() throws NSDKException {
        if(!isSupported){
            throw new NSDKException(ErrorCode.UNSUPPORTED, "UnSupported FutureX Module");
        }
    }

    @Override
    public byte[] getPEDI() throws NSDKException {
        isSupported();

        byte[] outInfo = new byte[6*1024];
        int[] outInfoLen = new int[1];
        byte[] errMsg = new byte[512];
        int[] errMsgLen = new int[1];
        int ret;
        try {
            ret = NSDKJni.getInstance().NDK_KmlRkiGetPediRequest(outInfo, outInfoLen, errMsg, errMsgLen);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            String errorMessage = String.format(Locale.US, "Failed to get PEDI from device, result code = %d", ret);
            if (errMsgLen[0] > 0) {
                errorMessage = String.format(Locale.US, "%s, error message = %s", errorMessage, new String(Arrays.copyOf(errMsg, errMsgLen[0])));
            }
            throw new NSDKNDKException(ret, errorMessage);
        }

        if (outInfoLen[0] > 0) {
            byte[] result = Arrays.copyOf(outInfo, outInfoLen[0]);
            return result;
        }
        return new byte[0];
    }

    @Override
    public void setPEDI(byte[] data) throws NSDKException {
        isSupported();

        byte[] errMsg = new byte[512];
        int[] errMsgLen = new int[1];
        if (data == null || data.length == 0) {
            throw new NSDKIllegalParameterException("No data to set.");
        }
        int ret = NSDKJni.getInstance().NDK_KmlRkiSetPediResponse(data, data.length, errMsg, errMsgLen);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            String errorMessage = String.format(Locale.US, "Failed to set PEDI to device, result code = %d", ret);
            if (errMsgLen[0] > 0) {
                errorMessage = String.format("%s, error message = %s", errorMessage, new String(Arrays.copyOf(errMsg, errMsgLen[0])));
            }
            throw new NSDKNDKException(ret, errorMessage);
        }
    }

    @Override
    public byte[] getPEDK() throws NSDKException {
        isSupported();

        byte[] outInfo = new byte[6*1024];
        int[] outInfoLen = new int[1];
        byte[] errMsg = new byte[512];
        int[] errMsgLen = new int[1];
        int ret = NSDKJni.getInstance().NDK_KmlRkiGetPedkInitialRequest(outInfo, outInfoLen, errMsg, errMsgLen);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            String errorMessage = String.format(Locale.US, "Failed to get PEDK from device, result code = %d", ret);
            if (errMsgLen[0] > 0) {
                errorMessage = String.format("%s, error message = %s", errorMessage, new String(Arrays.copyOf(errMsg, errMsgLen[0])));
            }
            throw new NSDKNDKException(ret, errorMessage);
        }

        if (outInfoLen[0] > 0) {
            return Arrays.copyOf(outInfo, outInfoLen[0]);
        }
        return new byte[0];
    }

    @Override
    public void setPEDK(byte[] data) throws NSDKException {
        isSupported();

        byte[] errMsg = new byte[512];
        int[] errMsgLen = new int[1];
        if (data == null || data.length == 0) {
            throw new NSDKIllegalParameterException("No data to set.");
        }
        int ret = NSDKJni.getInstance().NDK_KmlRkiSetPedkResponse(data, data.length, errMsg, errMsgLen);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            String errorMessage = String.format(Locale.US, "Failed to set PEDK to device, result code = %d", ret);
            if (errMsgLen[0] > 0) {
                errorMessage = String.format(Locale.US, "%s, error message = %s", errorMessage, new String(Arrays.copyOf(errMsg, errMsgLen[0])));
            }
            throw new NSDKNDKException(ret, errorMessage);
        }
    }

    @Override
    public byte[] getPEDV() throws NSDKException {
        isSupported();

        byte[] outInfo = new byte[6*1024];
        int[] outInfoLen = new int[1];
        byte[] errMsg = new byte[512];
        int[] errMsgLen = new int[1];
        int ret = NSDKJni.getInstance().NDK_KmlRkiGetPedvRequest(outInfo, outInfoLen, errMsg, errMsgLen);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            String errorMessage = String.format(Locale.US, "Failed to get PEDV from device, result code = %d", ret);
            if (errMsgLen[0] > 0) {
                errorMessage = String.format(Locale.US, "%s, error message = %s", errorMessage, new String(Arrays.copyOf(errMsg, errMsgLen[0])));
            }
            throw new NSDKNDKException(ret, errorMessage);
        }

        if (outInfoLen[0] > 0) {
            return Arrays.copyOf(outInfo, outInfoLen[0]);
        }
        return new byte[0];
    }

    @Override
    public void setPEDV(byte[] data) throws NSDKException {
        isSupported();

        byte[] errMsg = new byte[512];
        int[] errMsgLen = new int[1];
        if (data == null || data.length == 0) {
            throw new NSDKIllegalParameterException("No data to set.");
        }
        int ret = NSDKJni.getInstance().NDK_KmlRkiSetPedvResponse(data, data.length, errMsg, errMsgLen);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            String errorMessage = String.format(Locale.US, "Failed to set PEDV to device, result code = %d", ret);
            if (errMsgLen[0] > 0) {
                errorMessage = String.format(Locale.US, "%s, error message = %s", errorMessage, new String(Arrays.copyOf(errMsg, errMsgLen[0])));
            }
            throw new NSDKNDKException(ret, errorMessage);
        }
    }

    @Override
    public int getInstalledKeyNum() throws NSDKException {
        isSupported();

        int[] out = new int[1];
        int ret = NSDKJni.getInstance().NDK_KmlRkiGetInstallKeyNum(out);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to get installed key number, result code = %d", ret));
        }


        return out[0];
    }

    @Override
    public List<InstalledKeyInfo> getInstalledKeyInfo() throws NSDKException {
        isSupported();

        List<InstalledKeyInfo> keyInfos = new ArrayList<>();

        int[] len = new int[1];
        byte[] keyInfoData = new byte[512];
        int ret = NSDKJni.getInstance().NDK_KmlRkiGetInstalledKeyInfo(len, keyInfoData);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to get installed key info, result code = %d", ret));
        }

        byte[] result = Arrays.copyOf(keyInfoData, len[0]);

        try {
            if (result.length > 0) {
                int offset = 0;
                while (offset < result.length) {
                    InstalledKeyInfo info = new InstalledKeyInfo();
                    info.setIndex(keyInfoData[offset++]);
                    info.setType(keyInfoData[offset++]);
                    info.setUsage(keyInfoData[offset++]);
                    int kcvLen = keyInfoData[offset] & 0xFF;
                    offset++;
                    if (kcvLen > 0) {
                        byte[] kcv = new byte[kcvLen];
                        System.arraycopy(keyInfoData, offset, kcv, 0, kcvLen);
                        offset += kcvLen;
                        info.setKCV(kcv);
                    }
                    keyInfos.add(info);
                }
            }
        } catch (Exception e) {
            throw new NSDKException(ErrorCode.ERROR, "Failed to extract installed key info data.", e);
        }

        return keyInfos;
    }

    @Override
    public void setDeviceSignCertIndex(byte index) throws NSDKException {
        isSupported();

        int ret = NSDKJni.getInstance().NDK_KmlRkiSetDeviceSignCertIndex(index);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to set device sign cert index, result code = %d", ret));
        }
    }

    @Override
    public void setDeviceGroup(String name) throws NSDKException {
        isSupported();
        if(TextUtils.isEmpty(name)) {
            throw new NSDKIllegalParameterException("Device group name shall not be null or empty.");
        }

        int ret = NSDKJni.getInstance().NDK_KmlRkiSetDeviceGroup(name);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to set device group name, result code = %d", ret));
        }
    }

    @Override
    public void setWorkDirectory(String directory) throws NSDKException {
        isSupported();

        if(TextUtils.isEmpty(directory)) {
            throw new NSDKIllegalParameterException("directory shall not be null or empty.");
        }

        int ret = NSDKJni.getInstance().NDK_KmlRkiSetWorkDirectory(directory);

        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to set directory, result code = %d", ret));
        }
    }
}

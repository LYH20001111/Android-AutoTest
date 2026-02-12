package com.newland.nsdk.core.external;

import android.text.TextUtils;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.external.devicemanager.BluetoothInfo;
import com.newland.nsdk.core.api.external.devicemanager.DeviceConfiguration;
import com.newland.nsdk.core.api.external.devicemanager.DeviceConnectMode;
import com.newland.nsdk.core.api.external.devicemanager.ExtDeviceInfo;
import com.newland.nsdk.core.api.external.devicemanager.ExtDeviceManager;
import com.newland.nsdk.core.api.external.devicemanager.FileInfo;
import com.newland.nsdk.core.api.external.devicemanager.TimeConfiguration;
import com.newland.nsdk.core.api.external.devicemanager.UpdateListener;
import com.newland.nsdk.core.api.external.devicemanager.LogoType;
import com.newland.nsdk.core.api.external.devicemanager.UpdateFiles;
import com.newland.nsdk.core.common.NSDKExecutors;
import com.newland.nsdk.core.external.command.common.ExternalCommonModule;
import com.newland.nsdk.core.external.command.common.FileTransferUtil;
import com.newland.nsdk.core.external.command.updater.ExternalUpdaterModule;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import static com.newland.nsdk.core.external.command.common.ExternalCommonModule.OPERATION_GET;
import static com.newland.nsdk.core.external.command.common.ExternalCommonModule.OPERATION_SET;

/**
 * @author hlh
 * @date 2020/7/7
 */
public class ExtDeviceManagerImpl implements ExtDeviceManager {
    private static final String TAG = "ExtDeviceBasicImpl";
    private static final int FILE_SINGLE_PACKAGE_MAX_LEN = 4000;
    private ExternalCommonModule externalCommonModule;
    private ExternalUpdaterModule externalUpdaterModule;
    private boolean isCreateFile = false;

    private volatile static ExtDeviceManagerImpl instance;
    public static ExtDeviceManagerImpl getInstance() {
        if (instance == null) {
            synchronized (ExtDeviceManagerImpl.class) {
                if (instance == null) {
                    instance = new ExtDeviceManagerImpl();
                }
            }
        }
        return instance;
    }

    private ExtDeviceManagerImpl() {
        externalCommonModule = new ExternalCommonModule();
        externalUpdaterModule = new ExternalUpdaterModule();
        isCreateFile = false;
    }

    @Override
    public String getVersionNumber() throws NSDKException {
        return externalCommonModule.getVersionNumber();
    }

    @Override
    public String getSerialNumber() throws NSDKException {
        return externalCommonModule.getSerialNumber();
    }

    @Override
    public void setDeviceConfiguration(DeviceConfiguration deviceConfiguration) throws NSDKException {
        if (deviceConfiguration == null) {
            throw new NSDKIllegalParameterException();
        }

        externalCommonModule.loadConfiguration(deviceConfiguration);
    }

    @Override
    public DeviceConfiguration getDeviceConfiguration() throws NSDKException {
        return externalCommonModule.getConfiguration();
    }

    @Override
    public void loadFirmwareOrAppFile(final String appName, final byte[] data, final UpdateListener listener) throws NSDKException {
        if (listener == null) {
            throw new NSDKIllegalParameterException("Listener should not be null!");
        }

        if (TextUtils.isEmpty(appName)) {
            throw new NSDKIllegalParameterException("App name should not be null!");
        }

        if (appName.length() > 12) {
            throw new NSDKIllegalParameterException("Max length of app name is 12!");
        }

        if (data == null) {
            throw new NSDKIllegalParameterException("App data should not be null!");
        }

        NSDKExecutors.threadStart(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!isCreateFile) {
                        externalUpdaterModule.createFile(appName.getBytes());
                        isCreateFile = true;
                    }

                    int maxDataLen = 2048;
                    int totalLen = data.length;
                    listener.onFileTransferProgress(0);
                    if (totalLen > maxDataLen) {
                        int offset = 0;
                        while (offset < totalLen) {
                            int tempDataLen = maxDataLen;
                            if (totalLen - offset < maxDataLen) {
                                tempDataLen = totalLen - offset;
                            }
                            byte[] tempData = new byte[tempDataLen];
                            System.arraycopy(data, offset, tempData, 0, tempData.length);
                            offset += tempDataLen;
                            externalUpdaterModule.loadApp(tempData);
                            listener.onFileTransferProgress(offset * 100 / totalLen);
                        }
                    } else {
                        externalUpdaterModule.loadApp(data);
                        listener.onFileTransferProgress(100);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e instanceof NSDKException) {
                        listener.onError(((NSDKException) e).getCode(), e.getMessage());
                    } else {
                        listener.onError(ErrorCode.EXT_ERROR, e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void updateFirmwareOrApp(String appName, boolean isReboot) throws NSDKException {
        if (appName == null || appName.length() == 0) {
            throw new NSDKIllegalParameterException("Please set file name.");
        }
        externalUpdaterModule.updateApp(appName.getBytes(), isReboot);
        isCreateFile = false;
    }

    @Override
    public void reboot() throws NSDKException {
        externalCommonModule.reboot();
    }

    @Override
    public int getBatteryPercentage() throws NSDKException {
        return externalCommonModule.getBatteryPercentage();
    }

    @Override
    public ExtDeviceInfo getDeviceInfo() throws NSDKException {
        return externalCommonModule.getDeviceInfo();
    }

    @Override
    public void setDatetime(String datetime) throws NSDKException {
        externalCommonModule.operateDatetime(OPERATION_SET, datetime);
    }

    @Override
    public String getDatetime() throws NSDKException {
        return externalCommonModule.operateDatetime(OPERATION_GET, null);
    }

    @Override
    public void setBluetoothName(String name) throws NSDKException {
        externalCommonModule.operateBluetoothInfo(OPERATION_SET, name);
    }

    @Override
    public BluetoothInfo getBluetoothInfo() throws NSDKException {
        return externalCommonModule.operateBluetoothInfo(OPERATION_GET, null);
    }

    @Override
    public void setConnectMode(DeviceConnectMode mode) throws NSDKException {
        externalCommonModule.setConnectMode(mode);
    }

    @Override
    public void setLogoIcon(final LogoType logoType, final byte[] data, final UpdateListener listener) throws NSDKException {
        if (logoType == null || data == null || data.length == 0 ) {
            throw new NSDKIllegalParameterException("Logo type and data shall not be null or empty.");
        }

        if (listener == null) {
            throw new NSDKIllegalParameterException("Listener shall not be null.");
        }

        NSDKExecutors.threadStart(new Runnable() {
            @Override
            public void run() {
                transferFile(logoType.getCode(), data, listener);

                // logo 的设置只要文件传输完就可以了
                listener.onComplete();
            }
        });
    }

    private void transferFile(byte fileType, byte[] data, UpdateListener listener) {
        List<byte[]> dataList = FileTransferUtil.splitData(data, FILE_SINGLE_PACKAGE_MAX_LEN);
        byte[] hash;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            hash = digest.digest(data);
        } catch (Exception e) {
            e.printStackTrace();
            listener.onError(ErrorCode.EXT_ERROR, String.format("Failed to digest data: %s", e.getMessage()));
            return;
        }

        int totalLen = data.length + hash.length;
        listener.onFileTransferProgress(0);
        int offset = 0;
        try {
            for (int i = 0; i < dataList.size(); i++) {
                externalCommonModule.transferFile(fileType, ExternalCommonModule.FILE_WRITE_MODE_DATA, offset, dataList.get(i));
                offset += dataList.get(i).length;
                listener.onFileTransferProgress(offset * 100 / totalLen);
            }

            externalCommonModule.transferFile(fileType, ExternalCommonModule.FILE_WRITE_MODE_SHA1, 0, hash);
            listener.onFileTransferProgress(100);
        } catch (NSDKException e) {
            e.printStackTrace();
            listener.onError(e.getCode(), String.format("Failed to transfer file: %s", e.getMessage()));
            return;
        }
    }

    @Override
    public void setLanguage(final byte[] language, final UpdateListener listener) throws NSDKException {
        if (language == null || language.length == 0 ) {
            throw new NSDKIllegalParameterException("Language data shall not be null or empty.");
        }

        if (listener == null) {
            throw new NSDKIllegalParameterException("Listener shall not be null.");
        }

        NSDKExecutors.threadStart(new Runnable() {
            @Override
            public void run() {
                transferFile((byte) 0, language, listener);

                try {
                    externalCommonModule.operateLanguage(OPERATION_SET);
                } catch (NSDKException e) {
                    e.printStackTrace();
                    listener.onError(e.getCode(), String.format("Failed to set language: %s", e.getMessage()));
                    return;
                }

                listener.onComplete();
            }
        });
    }

    @Override
    public String getLanguage() throws NSDKException {
        return externalCommonModule.operateLanguage(OPERATION_GET);
    }

    @Override
    public void update(final UpdateFiles files, final UpdateListener listener) throws NSDKException {
        if (files == null) {
            throw new NSDKIllegalParameterException("Please set files to update.");
        }

        if (listener == null) {
            throw new NSDKIllegalParameterException("Listener shall not be null.");
        }

        NSDKExecutors.threadStart(new Runnable() {
            @Override
            public void run() {
                byte updateMode = 0x00;
                int totalLen = 0;
                byte[] application = files.getApplicationFile();
                byte[] firmware = files.getFirmwareFile();

                List<byte[]> applicationDataList = null;
                List<byte[]> firmwareDataList = null;
                byte[] applicationHash = null;
                byte[] firmwareHash = null;
                MessageDigest digest;

                try {
                    digest = MessageDigest.getInstance("SHA-1");
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onError(ErrorCode.EXT_ERROR, String.format("Failed to get message digest: %s", e.getMessage()));
                    return;
                }

                if (application != null && application.length > 0) {
                    applicationDataList = FileTransferUtil.splitData(application, FILE_SINGLE_PACKAGE_MAX_LEN);
                    applicationHash = digest.digest(application);
                    totalLen = totalLen + application.length + applicationHash.length;
                }

                if (firmware != null && firmware.length > 0) {
                    updateMode = 0x01;
                    firmwareDataList = FileTransferUtil.splitData(firmware, FILE_SINGLE_PACKAGE_MAX_LEN);
                    firmwareHash = digest.digest(firmware);
                    totalLen = totalLen + firmware.length + firmwareHash.length;
                }

                LogUtils.d(TAG, "******+++++ Total len is " + totalLen);
                listener.onFileTransferProgress(0);
                int progress = 0;
                if (applicationDataList != null && applicationDataList.size() > 0) {
                    int offset = 0;
                    try {
                        for (int i = 0; i < applicationDataList.size(); i++) {
                            externalCommonModule.transferFile((byte) 1, ExternalCommonModule.FILE_WRITE_MODE_DATA, offset, applicationDataList.get(i));
                            offset += applicationDataList.get(i).length;
                            listener.onFileTransferProgress(offset * 100 / totalLen);
                        }
                        externalCommonModule.transferFile((byte) 1, ExternalCommonModule.FILE_WRITE_MODE_SHA1, 0, applicationHash);
                        progress = application.length + applicationHash.length;
                        listener.onFileTransferProgress(progress * 100 / totalLen);
                    } catch (NSDKException e) {
                        e.printStackTrace();
                        listener.onError(e.getCode(), String.format("Failed to transfer file: %s", e.getMessage()));
                        return;
                    }
                }

                if (firmwareDataList != null && firmwareDataList.size() > 0) {
                    int offset = 0;
                    try {
                        for (int i = 0; i < firmwareDataList.size(); i++) {
                            externalCommonModule.transferFile((byte) 2, ExternalCommonModule.FILE_WRITE_MODE_DATA, offset, firmwareDataList.get(i));
                            offset += firmwareDataList.get(i).length;
                            listener.onFileTransferProgress((progress + offset) * 100 / totalLen);
                        }
                        externalCommonModule.transferFile((byte) 2, ExternalCommonModule.FILE_WRITE_MODE_SHA1, 0, firmwareHash);
                        listener.onFileTransferProgress(100);
                    } catch (NSDKException e) {
                        e.printStackTrace();
                        listener.onError(e.getCode(), String.format("Failed to transfer file: %s", e.getMessage()));
                        return;
                    }
                }

                try {
                    externalCommonModule.updateAppFirmware(updateMode);
                } catch (NSDKException e) {
                    e.printStackTrace();
                    listener.onError(e.getCode(), String.format("Failed to update: %s", e.getMessage()));
                    return;
                }

                listener.onComplete();
            }
        });
    }

    @Override
    public void setTimeConfiguration(TimeConfiguration configuration) throws NSDKException {
        if (configuration == null) {
            throw new NSDKIllegalParameterException("Configuration shall not be null.");
        }

        externalCommonModule.operateTimes(OPERATION_SET, configuration);
    }

    @Override
    public TimeConfiguration getTimeConfiguration() throws NSDKException {
        return externalCommonModule.operateTimes(OPERATION_GET, null);
    }

    @Override
    public ArrayList<FileInfo> getFileList(String keyword, byte[] tlvData) throws NSDKException {
        if (TextUtils.isEmpty(keyword)) {
            throw new NSDKIllegalParameterException("Key word shall not be null.");
        }
        return externalCommonModule.getFileList(keyword, tlvData);
    }
}

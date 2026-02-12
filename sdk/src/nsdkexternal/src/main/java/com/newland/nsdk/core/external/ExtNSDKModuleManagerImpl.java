package com.newland.nsdk.core.external;

import android.content.Context;
import android.os.Build;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.crypto.AlgorithmParameters;
import com.newland.nsdk.core.api.common.exception.NSDKCommunicationException;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.uart3.UART3Config;
import com.newland.nsdk.core.api.common.uart3.UART3Type;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogLevel;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.external.ExtNSDKModuleManager;
import com.newland.nsdk.core.api.external.communication.CommunicatorListener;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicator;
import com.newland.nsdk.core.api.external.communication.ExternalCommunicatorType;
import com.newland.nsdk.core.api.external.communication.NSDKCommunicator;
import com.newland.nsdk.core.common.NSDKExecutors;
import com.newland.nsdk.core.external.command.ExternalCommandConfig;
import com.newland.nsdk.core.external.command.ExternalCommandType;
import com.newland.nsdk.core.external.command.common.ExternalCommonModule;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.communication.bluetooth.BLECommunicator;
import com.newland.nsdk.core.external.command.communication.bluetooth.BluetoothCommunicator;
import com.newland.nsdk.core.external.command.communication.usbhost.USBCommunicator;
import com.newland.nsdk.core.external.command.communication.uart.UART3PortCommunicator;
import com.newland.nsdk.external.BuildConfig;

import java.util.HashMap;

public class ExtNSDKModuleManagerImpl implements ExtNSDKModuleManager {

    public static final String TAG = "ExtNSDKModuleManagerImpl";
    private static HashMap<String, Module> modules = new HashMap<String, Module>();
    private static ExtNSDKModuleManagerImpl moduleManage;
    private static ExternalCommonModule externalCommonModule;
    private volatile boolean isExtDeviceInit;
    private ExternalCommandType externalCommandType = ExternalCommandType.NDK;

    private ExtNSDKModuleManagerImpl() {
        isExtDeviceInit = false;
        externalCommonModule = new ExternalCommonModule();
    }

    public static ExtNSDKModuleManagerImpl getInstance() {
        if (moduleManage == null) {
            synchronized (ExtNSDKModuleManagerImpl.class) {
                if (moduleManage == null) {
                    moduleManage = new ExtNSDKModuleManagerImpl();
                }
            }
        }
        return moduleManage;
    }

    /**
     * <p>Get the device module.</p>
     *
     * @return
     */
    @Override
    public Module getModule(String moduleName) {
        if (!isExtDeviceInit && moduleName.contains("EXT_")) {
            try {
                initExternalModules();
            } catch (NSDKException e) {
                LogUtils.e(TAG, "Failed to init external modules.");
            }
        }

        return modules.get(moduleName);
    }

    @Override
    public synchronized void initExternalModules() throws NSDKException {
        if (!ping()) {
            throw new NSDKCommunicationException("Can not ping external device.");
        }

        if (isExtDeviceInit) {
            LogUtils.d(TAG, "Ext NSDK modules already initialized.");
            return;
        }

        ExtDeviceManagerImpl deviceManager = ExtDeviceManagerImpl.getInstance();

        String versionStr = deviceManager.getVersionNumber();
        LogUtils.d(TAG, String.format("External device version number: %s", versionStr));
        ExternalCommandConfig config = ExternalCommandConfig.create(versionStr, deviceManager.getDeviceInfo().getModel());
        ExternalCommunicationManager.getInstance().setConfig(config);
        String sdkVersion = BuildConfig.VERSION_NAME;
        boolean isValid = checkFWTypeAndSDKVersion(sdkVersion, config, deviceManager);
        if (!isValid) {
            modules.clear();
            throw new NSDKException(String.format("Ext NSDK version is %s, not official release, only can be used on DEV devices.", sdkVersion));
        }

        modules.put(ModuleType.EXT_DISPLAY, ExtDisplayImpl.getInstance());
        modules.put(ModuleType.EXT_DEVICE_MANAGER, deviceManager);
        modules.put(ModuleType.EXT_SCANNER, ExtScanImpl.getInstance());
        modules.put(ModuleType.EXT_CARD_READER, ExtCardReaderImpl.getInstance());
        modules.put(ModuleType.EXT_CRYPTO, ExtCryptoImpl.getInstance());
        modules.put(ModuleType.EXT_KEY_MANAGER, ExtKeyManagerImpl.getInstance());
        modules.put(ModuleType.EXT_PIN_ENTRY, ExtPINEntryImpl.getInstance());
        modules.put(ModuleType.EXT_KEYBOARD, ExtKeyboardImpl.getInstance());
        modules.put(ModuleType.EXT_LED, ExtLEDImpl.getInstance());
        modules.put(ModuleType.EXT_BEEPER, ExtBeeperImpl.getInstance());
        modules.put(ModuleType.EXT_FUTUREX, ExtFutureXImpl.getInstance());
        modules.put(ModuleType.EXT_SETTING, ExtSettingsManagerImpl.getInstance());
        modules.put(ModuleType.EXT_ESIGNATURE, ExtESignatureImpl.getInstance());
        modules.put(ModuleType.EXT_CARD_EMULATOR, ExtCardEmulatorImpl.getInstance());

        // 放在版本判断之前设置为已初始化好，是为了当用户不需要用非接模块时，可以忽略版本问题使用其他模块
        isExtDeviceInit = true;

        if (config.isNeedUpdate()) {
            throw new NSDKException(ErrorCode.NEED_UPDATE, "Need to update firmware.");
        }
    }

    private boolean checkFWTypeAndSDKVersion(String sdkVersion, ExternalCommandConfig config, ExtDeviceManagerImpl deviceManager) throws NSDKException {
        boolean isPro = false;
        if("ME30SU".equalsIgnoreCase(deviceManager.getDeviceInfo().getModel())) {
            isPro = deviceManager.getDeviceInfo().getBuildDevCFGVersion().startsWith("V");
        }
        // todo 添加其他类型设备的固件判断
        String[] sdkVersions = sdkVersion.split("\\.");
        boolean isTempVersion = false;
        try {
            Integer.parseInt(sdkVersions[2]);
        } catch (Exception e) {
            isTempVersion = true;
        }
        if (isTempVersion) {
            LogUtils.d(TAG, String.format("Ext NSDK version is %s, not official release, only can be used on DEV devices.", sdkVersion));
            // 临时版本 nsdk 不能用在正式机上
            if (isPro) {
                return false;
            }
            return true;
        }
        // 正式版本 nsdk 可以用在开发和正式机上
        LogUtils.d(TAG, String.format("Ext NSDK version is %s, official release, can be used on both PRO and DEV devices.", sdkVersion));
        return true;
    }

    /**
     * release the device resources.
     *
     * @return
     */
    @Override
    public void destroy() {
        moduleManage = null;
        modules.clear();
        isExtDeviceInit = false;
        NSDKExecutors.release();
    }

    @Override
    public NSDKCommunicator getNSDKCommunicator(Context context, ExternalCommunicatorType type, CommunicatorListener listener) throws NSDKException {
        if (context == null) {
            throw new NSDKIllegalParameterException("Context should not be null!");
        }

        if (listener == null) {
            throw new NSDKIllegalParameterException("Listener should not be null!");
        }

        NSDKCommunicator communicator = null;
        switch (type) {
            case UART3PORT:
                if(!"newland".equalsIgnoreCase(Build.MANUFACTURER)){
                    throw new NSDKException("Unsupported ExternalCommunicatorType.");
                }

                communicator = UART3PortCommunicator.getInstance(listener);
                break;
            case USB:
                communicator = USBCommunicator.getInstance(context, listener);
                break;
            case BLUETOOTH_CLASSIC:
                communicator = BluetoothCommunicator.getInstance(context, listener);
                break;
            case BLUETOOTH_LOW_ENERGY:
                communicator = BLECommunicator.getInstance(context, listener);
                break;
            default:
                break;
        }

        setCommunicator(communicator);

        return communicator;
    }

    @Override
    public void setUART3Config(UART3Type type, UART3Config config) throws NSDKException {
        ExternalCommunicator communicator = ExternalCommunicationManager.getInstance().getCommunicator();
        if (communicator instanceof UART3PortCommunicator) {
            ((UART3PortCommunicator)communicator).setUART3Config(type,config);
        }
    }

    @Override
    public void setDebugMode(LogLevel level) {
        if (level == null) {
            level = LogLevel.OFF;
        }

        LogUtils.setLogLevel(level);
    }

    @Override
    public void setCommunicator(ExternalCommunicator externalCommunicator) {
        ExternalCommunicationManager.getInstance().setCommunicator(externalCommunicator);
    }

    @Override
    public void setCommunicationTimeout(int sendTimeout, int receiveTimeout) throws NSDKException {
        if(sendTimeout < 0 || receiveTimeout < 0) {
            throw new NSDKIllegalParameterException("The time shall >= 0");
        }
        ExternalCommunicationManager.getInstance().setSendTimeout(sendTimeout);
        ExternalCommunicationManager.getInstance().setReceiveTimeout(receiveTimeout);
    }

    @Override
    public boolean ping() {
        ExternalCommunicator communicator = ExternalCommunicationManager.getInstance().getCommunicator();
        if (communicator == null) {
            return false;
        }
        //改用 NSDK 收发，保证收发顺序不会出错
        try {
            ExtDeviceManagerImpl.getInstance().getVersionNumber();
            return true;
        } catch (NSDKException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void setCryptoMode(SymmetricKey key, AlgorithmParameters params) throws NSDKException {
        externalCommonModule.setCryptoMode(key, params);
    }

    @Override
    public String getErrMsg(int errCode) {
            String errMsg = "";
            switch(errCode) {
                case ErrorCode.EXT_ERROR:
                    errMsg = "External error";
                    break;
                case ErrorCode.EXT_COMMAND_FAILED:
                    errMsg = "External error: Command failed";
                    break;
                case ErrorCode.EXT_INVALID_COMMAND_SEQUENCE:
                    errMsg = "External error: Invalid command sequence";
                    break;
                case ErrorCode.EXT_COMMAND_LENGTH_ERROR:
                    errMsg = "External error: Command length error";
                    break;
                case ErrorCode.EXT_DEVICE_INIT_ERROR:
                    errMsg = "External error: Device init error";
                    break;
                case ErrorCode.EXT_DEVICE_OPEN_ERROR:
                    errMsg = "External error: Device open error";
                    break;
                case ErrorCode.EXT_UNKNOWN_ERROR:
                    errMsg = "External error: Unknown error";
                    break;
                case ErrorCode.EXT_ICON_SET_ERROR:
                    errMsg = "External error: Failed to set icon";
                    break;
                case ErrorCode.EXT_FILE_OPEN_ERROR:
                    errMsg = "External error: Failed to open file";
                    break;
                case ErrorCode.EXT_FILE_WRITE_ERROR:
                    errMsg = "External error: Failed to write file";
                    break;
                case ErrorCode.EXT_FILE_WRONG_OFFSET:
                    errMsg = "External error: Wrong offset";
                    break;
                case ErrorCode.EXT_FILE_SHA1_ERROR:
                    errMsg = "External error: SHA1 error";
                    break;
                case ErrorCode.EXT_MESSAGE_ERROR:
                    errMsg = "External message error";
                    break;
                case ErrorCode.EXT_MESSAGE_INVALID_MESSAGE_TYPE:
                    errMsg = "External message error: Invalid message type";
                    break;
                case ErrorCode.EXT_MESSAGE_INVALID_FUNCTION_ID:
                    errMsg = "External message error: Invalid function ID";
                    break;
                case ErrorCode.EXT_MESSAGE_EXCEED_MAX_LENGTH:
                    errMsg = "External message error: Exceed max data length";
                    break;
                case ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR:
                    errMsg = "External message error: The value of length field is bigger than length of actual data";
                    break;
                case ErrorCode.EXT_MESSAGE_INVALID_LRC:
                    errMsg = "External message error: Invalid LRC";
                    break;
                case ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE:
                    errMsg = "External message error: No response code";
                    break;
                case ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA:
                    errMsg = "External message error: No response data";
                    break;
                case ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH:
                    errMsg = "External message error: Data length is not enough";
                    break;
                case ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_CORRECT:
                    errMsg = "External message error: Data length not correct, too long or too short";
                    break;
                case ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR:
                    errMsg = "External message error: This happens when writing byte array stream";
                    break;
                case ErrorCode.EXT_MESSAGE_INVALID_RESPONSE_DATA:
                    errMsg = "External message error: Response data not started with STX";
                    break;
                case ErrorCode.EXT_MESSAGE_INVALID_KSN:
                    errMsg = "External message error: KSN is not correct";
                    break;
                case ErrorCode.EXT_MESSAGE_INVALID_MAC:
                    errMsg = "External message error: MAC is not correct";
                    break;
                case ErrorCode.EXT_COMMUNICATION_ERROR:
                    errMsg = "Communication base error";
                    break;
                case ErrorCode.EXT_COMMUNICATION_NO_RESPONSE_DATA:
                    errMsg = "Communication error code: No response data";
                    break;
                case ErrorCode.EXT_COMMUNICATION_NOT_INITIALIZED:
                    errMsg = "Communication error code: Communicator not initialized";
                    break;
                case ErrorCode.EXT_COMMUNICATION_RECEIVE_DATA_TIMEOUT:
                    errMsg = "Communication error code: Receiving data timeout";
                    break;
                case ErrorCode.EXT_COMMUNICATION_OPEN_ERROR:
                    errMsg = "Communication error code: Failed to open";
                    break;
                case ErrorCode.EXT_COMMUNICATION_CLOSE_ERROR:
                    errMsg = "Communication error code: Failed to close";
                    break;
                case ErrorCode.EXT_COMMUNICATION_SEND_ERROR:
                    errMsg = "Communication error code: Failed to send data";
                    break;
                case ErrorCode.EXT_COMMUNICATION_RECEIVE_ERROR:
                    errMsg = "Communication error code: Failed to receive data";
                    break;
                case ErrorCode.EXT_COMMUNICATION_NO_DATA_TO_SEND:
                    errMsg = "Communication error code: No data to send";
                    break;
                case ErrorCode.EXT_COMMUNICATION_BLUETHOOTH_NOT_SUPPORTED:
                    errMsg = "Communication error code: Bluetooth not supported";
                    break;
                case ErrorCode.EXT_COMMUNICATION_BLUETHOOTH_DISABLED:
                    errMsg = "Communication error code: Bluetooth disabled";
                    break;
                case ErrorCode.EXT_COMMUNICATION_BLUETHOOTH_NOT_PAIRED:
                    errMsg = "Communication error code: Bluetooth not paired";
                    break;
                case ErrorCode.EXT_COMMUNICATION_BLUETHOOTH_DISCONNECTED:
                    errMsg = "Communication error code: Bluetooth disconnected";
                    break;
                case ErrorCode.EXT_COMMUNICATION_BLUETHOOTH_DEVICE_NOT_CHOSEN:
                    errMsg = "Communication error code: User shall choose a bluetooth device from the list";
                    break;
                case ErrorCode.EXT_PINPAD_ERROR:
                    errMsg = "External PIN pad error";
                    break;
                case ErrorCode.EXT_PINPAD_BAD_KEY_TAG:
                    errMsg = "External PIN pad error: Bad key tag";
                    break;
                case ErrorCode.EXT_PINPAD_BAD_KEY_INDEX:
                    errMsg = "External PIN pad error: Bad key index";
                    break;
                case ErrorCode.EXT_PINPAD_BAD_KEY_USAGE:
                    errMsg = "External PIN pad error: Bad key usage";
                    break;
                case ErrorCode.EXT_PINPAD_KEY_MODE_ERROR:
                    errMsg = "External PIN pad error: Key mode error";
                    break;
                case ErrorCode.EXT_PINPAD_LINE_NUMBER_ERROR:
                    errMsg = "External PIN pad error: Line number error";
                    break;
                case ErrorCode.EXT_PINPAD_BAD_DATA_LENGTH:
                    errMsg = "External PIN pad error: Bad data length";
                    break;
                case ErrorCode.EXT_PINPAD_INVALID_BLOCK:
                    errMsg = "External PIN pad error: Invalid block";
                    break;
                case ErrorCode.EXT_PINPAD_KEY_TYPE_ERROR:
                    errMsg = "External PIN pad error: Key type error";
                    break;
                case ErrorCode.EXT_PINPAD_FORMAT_ERROR:
                    errMsg = "External PIN pad error: Format error";
                    break;
                case ErrorCode.EXT_PINPAD_DELETE_ERROR:
                    errMsg = "External PIN pad error: Failed to delete key";
                    break;
                case ErrorCode.EXT_PINPAD_BAD_CMD_LENGTH:
                    errMsg = "External PIN pad error: Bad cmd length";
                    break;
                case ErrorCode.EXT_PINPAD_BAD_IV_LENGTH:
                    errMsg = "External PIN pad error: Bad IV length";
                    break;
                case ErrorCode.EXT_PINPAD_KCV_ERROR:
                    errMsg = "External PIN pad error: KCV error";
                    break;
                case ErrorCode.EXT_PINPAD_KEY_EXIST:
                    errMsg = "External PIN pad error: Key already exist";
                    break;
                case ErrorCode.EXT_PINPAD_INSTALLED_KEY_NUM_ERROR:
                    errMsg = "External PIN pad error: Failed to get installed key number";
                    break;
                case ErrorCode.EXT_CARD_READER_ERROR:
                    errMsg = "External card reader error";
                    break;
                case ErrorCode.EXT_CARD_READER_PAN_GETTING_ERROR:
                    errMsg = "External card reader error: Getting PAN error";
                    break;
                case ErrorCode.EXT_CARD_READER_PAN_ENCRYPTION_ERROR:
                    errMsg = "External card reader error: PAN encryption error";
                    break;
                case ErrorCode.EXT_IC_CARD_ERROR:
                    errMsg = "External contact card error";
                    break;
                case ErrorCode.EXT_IC_CARD_READ_ERROR:
                    errMsg = "External contact card error: Read error";
                    break;
                case ErrorCode.EXT_IC_CARD_ENCRYPTION_ERROR:
                    errMsg = "External contact card error: Encryption error";
                    break;
                case ErrorCode.EXT_IC_CARD_DECRYPTION_ERROR:
                    errMsg = "External contact card error: Decryption error";
                    break;
                case ErrorCode.EXT_IC_CARD_NO_CARD:
                    errMsg = "External contact card error: No card";
                    break;
                case ErrorCode.EXT_RF_CARD_ERROR:
                    errMsg = "External contactless card error";
                    break;
                case ErrorCode.EXT_RF_CARD_NOT_PRESENT:
                    errMsg = "External contactless card error: Card not present";
                    break;
                case ErrorCode.EXT_RF_CARD_MULTI_CARDS:
                    errMsg = "External contactless card error: Multi cards";
                    break;
                case ErrorCode.EXT_RF_CARD_ANTI_COLLISION_FAILED:
                    errMsg = "External contactless card error: Anti-collision failed";
                    break;
                case ErrorCode.EXT_RF_CARD_SELECT_CARD_FAILED:
                    errMsg = "External contactless card error: Failed to select card";
                    break;
                case ErrorCode.EXT_RF_CARD_READER_NOT_CONFIGURED:
                    errMsg = "External contactless card error: Card reader not configured";
                    break;
                case ErrorCode.EXT_RF_CARD_READER_AUTH_ERROR:
                    errMsg = "External contactless card error: Authentication error";
                    break;
                case ErrorCode.EXT_RF_CARD_READER_NOT_AUTH:
                    errMsg = "External contactless card error: Not authenticated";
                    break;
                case ErrorCode.EXT_RF_CARD_ACTIVATE_FAIL:
                    errMsg = "External contactless card error: Not authenticated";
                    break;
                case ErrorCode.EXT_RF_CARD_GET_INFO_ERROR:
                    errMsg = "External contactless card error: Not authenticated";
                    break;
                case ErrorCode.EXT_MAG_CARD_ERROR:
                    errMsg = "External mag card error";
                    break;
                case ErrorCode.EXT_MAG_GET_TRACK_DATA_ERROR:
                    errMsg = "External mag card error: Failed to get track data";
                    break;
                case ErrorCode.EXT_MAG_READ_ERROR:
                    errMsg = "External mag card error: Read error";
                    break;
                case ErrorCode.EXT_MAG_TRACK2_ERROR:
                    errMsg = "External mag card error: Track 2 error";
                    break;
                case ErrorCode.EXT_MAG_TRACK_ENCRYPTION_ERROR:
                    errMsg = "External mag card error： Track encryption error";
                    break;
                case ErrorCode.EXT_MAG_TRACK3_ERROR:
                    errMsg = "External mag card error: Track 3 error";
                    break;
                case ErrorCode.EXT_EMV_ERROR:
                    errMsg = "External EMV error";
                    break;
                case ErrorCode.EXT_EMV_CANCELLED_BY_HOST:
                    errMsg = "External EMV error: Cancelled by host";
                    break;
                case ErrorCode.EXT_APP_ERROR:
                    errMsg = "External app error";
                    break;
                case ErrorCode.EXT_APP_FILE_CREATE_ERROR:
                    errMsg = "External app error: Failed to create file";
                    break;
                case ErrorCode.EXT_APP_FILE_OPEN_ERROR:
                    errMsg = "External app error: Failed to open file";
                    break;
                case ErrorCode.EXT_APP_FILE_WRITE_ERROR:
                    errMsg = "External app error: Failed to write file";
                    break;
                case ErrorCode.EXT_APP_FILE_DELETE_ERROR:
                    errMsg = "External app error: Failed to delete file";
                    break;
                case ErrorCode.EXT_APP_NLD_INFO_ERROR:
                    errMsg = "External app error: File info error";
                    break;
                case ErrorCode.EXT_APP_SIGN_DECRYPTION_ERROR:
                    errMsg = "External app error: Signature decryption error";
                    break;
                case ErrorCode.EXT_APP_SIGN_CHECK_ERROR:
                    errMsg = "External app error: Signature check error";
                    break;
                case ErrorCode.EXT_APP_UPDATE_ERROR:
                    errMsg = "External app error: Failed to update";
                    break;
                case ErrorCode.EXT_SIGNATURE_ERROR:
                    errMsg = "External signature error";
                    break;
                case ErrorCode.EXT_SIGNATURE_GET_SN_FAILED:
                    errMsg = "External signature error: Failed to get SN";
                    break;
                case ErrorCode.EXT_SIGNATURE_NOT_SUPPORTED:
                    errMsg = "External signature error: Not supported";
                    break;
                case ErrorCode.EXT_SIGNATURE_SIGN_FAILED:
                    errMsg = "External signature error: Failed to sign";
                    break;
                case ErrorCode.EXT_SCANNER_ERROR:
                    errMsg = "External scanner error";
                    break;
                case ErrorCode.EXT_SCANNER_SCANNING_ERROR:
                    errMsg = "External scanner error: Scanning";
                    break;
                case ErrorCode.EXT_SCANNER_SCANNING_HEAD_NOT_SUPPORTED:
                    errMsg = "External scanner error: Scanning head not supported";
                    break;
                case ErrorCode.EXT_SCANNER_SCANNING_STOPPED:
                    errMsg = "External scanner error: scanning is stopped";
                    break;
                case ErrorCode.EXT_SCANNER_STOP_SCANNING_ERROR:
                    errMsg = "External scanner error: Failed to stop scanning";
                    break;
                case ErrorCode.EXT_DISPLAY_ERROR:
                    errMsg = "External display error";
                    break;
                case ErrorCode.EXT_DISPLAY_CUSTOMER_CARD_ERROR:
                    errMsg = "External display error: Customer card error";
                    break;
                case ErrorCode.EXT_DISPLAY_BEYOND_SCREEN_RANGE:
                    errMsg = "External display error: Beyond screen range";
                    break;
                case ErrorCode. EXT_DISPLAY_FILE_OPERATE_ERROR:
                    errMsg = "External display error: Failed to operate file";
                    break;
                case ErrorCode.EXT_DISPLAY_LATTICE_DATA_ERROR:
                    errMsg = "External display error: Lattice data error";
                    break;
                case ErrorCode.EXT_DISPLAY_MODE_ERROR:
                    errMsg = "External display error: Mode error";
                    break;
                case ErrorCode.EXT_DISPLAY_DATA_ERROR:
                    errMsg = "External display error: Data error";
                    break;
                case ErrorCode.EXT_DISPLAY_QR_CODE_ERROR_CORRECT_LEVEL:
                    errMsg = "External display error: QR code correct level error";
                    break;
                case ErrorCode.EXT_DISPLAY_QR_CODE_MASK_NUMBER_ERROR:
                    errMsg = "External display error: QR code mask number error";
                    break;
                case ErrorCode.EXT_DISPLAY_QR_CODE_TYPE_ERROR:
                    errMsg = "External display error: QR code type error";
                    break;
                case ErrorCode.EXT_DISPLAY_QR_CODE_AUTO_CENTER_ERROR:
                    errMsg = "External display error: QR code auto centering error";
                    break;
                case ErrorCode.EXT_DISPLAY_QR_CODE_INITIAL_ABSCISSA_ERROR:
                    errMsg = "External display error: QR code initial abscissa error";
                    break;
                case ErrorCode.EXT_DISPLAY_QR_CODE_INITIAL_ORDINATE_ERROR:
                    errMsg = "External display error: QR code initial ordinate error";
                    break;
                case ErrorCode.EXT_DISPLAY_QR_CODE_TEXT_POSITION_ERROR:
                    errMsg = "External display error: QR code text position error";
                    break;
                case ErrorCode.EXT_DISPLAY_QR_CODE_TEXT_LENGTH_ERROR:
                    errMsg = "External display error: QR code text length error";
                    break;
                case ErrorCode.EXT_DISPLAY_QR_CODE_LENGTH_ERROR:
                    errMsg = "External display error: QR code length error";
                    break;
                case ErrorCode.EXT_DISPLAY_QR_CODE_WIDTH_ERROR:
                    errMsg = "External display error: QR code width error";
                    break;
                case ErrorCode.EXT_DISPLAY_QR_CODE_HEIGHT_ERROR:
                    errMsg = "External display error: QR code height error";
                    break;
                case ErrorCode.EXT_DISPLAY_TEXT_HEIGHT_ERROR:
                    errMsg = "External display error: The height of text is out of screen range";
                    break;
                case ErrorCode.EXT_LED_ERROR:
                    errMsg = "External LED error";
                    break;
                case ErrorCode.EXT_LED_LATTICE_DATA_ERROR:
                    errMsg = "External LED error: Lattice data error";
                    break;
                case ErrorCode.EXT_LED_MODE_ERROR:
                    errMsg = "External LED error: Mode error";
                    break;
                case ErrorCode.EXT_LED_DATA_ERROR:
                    errMsg = "External LED error: Data error";
                    break;
                case ErrorCode.EXT_UNSUPPORTED:
                    errMsg = "External error: Unsupported";
                    break;
                default:
                    errMsg = "Unknown Error";
                    break;
            }
            return errMsg;
        }
}


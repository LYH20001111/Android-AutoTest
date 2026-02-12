package com.newland.nsdk.core.external.command.common;

import android.text.TextUtils;
import android.util.Log;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.crypto.AlgorithmParameters;
import com.newland.nsdk.core.api.common.crypto.CipherType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;
import com.newland.nsdk.core.api.common.led.LEDColor;
import com.newland.nsdk.core.api.common.led.LEDState;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.external.devicemanager.BluetoothInfo;
import com.newland.nsdk.core.api.external.devicemanager.DeviceAttribute;
import com.newland.nsdk.core.api.external.devicemanager.DeviceConnectMode;
import com.newland.nsdk.core.api.external.devicemanager.ExtDeviceInfo;
import com.newland.nsdk.core.api.external.devicemanager.FileInfo;
import com.newland.nsdk.core.api.external.devicemanager.TimeConfiguration;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.message.functionId.ContactlessCardFunctionId;
import com.newland.nsdk.core.api.external.devicemanager.BaudRateMode;
import com.newland.nsdk.core.api.external.devicemanager.BeeperControl;
import com.newland.nsdk.core.api.external.devicemanager.BeeperTone;
import com.newland.nsdk.core.api.external.devicemanager.DecryptionMode;
import com.newland.nsdk.core.api.external.devicemanager.DeviceConfiguration;
import com.newland.nsdk.core.api.external.exception.NSDKExternalDeviceException;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;
import com.newland.nsdk.core.external.command.exception.ExternalMessageException;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Provides some common functions of external device.
 */
public class ExternalCommonModule {
    public static final byte OPERATION_GET = 0;
    public static final byte OPERATION_SET = 1;
    public static final byte FILE_WRITE_MODE_SHA1 = 0;
    public static final byte FILE_WRITE_MODE_DATA = 1;

    /**
     * Get software version number of the external device.
     *
     * @return Software version number of the external device.
     * @throws NSDKException If error occurs.
     *                       <ul>
     *                           <li>ExternalDeviceException: </li>
     *                           <ul>
     *                               <li></li>
     *                           </ul>
     *                       </ul>
     */
    public String getVersionNumber() throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.GET_VERSION_NUMBER_REQUEST);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.GET_VERSION_NUMBER_RESPONSE, null);

        responseMessage.checkMessageType(ExternalMessageType.GET_VERSION_NUMBER_RESPONSE);

        byte[] responseMessageData = responseMessage.getMessageData();

        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        byte[] responseCodeBuf = new byte[2];
        System.arraycopy(responseMessageData, 0, responseCodeBuf, 0, 2);
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(responseCodeBuf);

        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
        }

        if (responseMessageData.length > 2) {
            // Message data = Response code(2 bytes) + Software version len(1 byte) + Software version
            int versionNumberLen = responseMessageData[2] & 0xFF;
            if (versionNumberLen > 0) {
                if (versionNumberLen > (responseMessageData.length - 3)) {
                    versionNumberLen = responseMessageData.length - 3;
                }

                byte[] versionNumber = new byte[versionNumberLen];
                System.arraycopy(responseMessageData, 3, versionNumber, 0, versionNumberLen);
                return new String(versionNumber);
            }
        }

        return null;
    }

    /**
     * Make the specified light to blink.
     *
     * @param light Indicates which light to blink, see {@link LEDColor}
     * @param time  Time for blinking. Value range: [0-9999], Unit: 10ms, e.g., if set this parameter to 100, means blink for 1 second.
     * @throws NSDKException
     * @deprecated Call {@link }
     */
    public void flashLED(LEDColor light, int time) throws NSDKException {
        if (light == null) {
            throw new NSDKIllegalParameterException("Please set which light to flash.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CONTACTLESS_CARD_REQUEST);
        // Request message data = Function ID(1 byte) + LED ID(1 byte) + Timeout(2 bytes)
        byte[] requestMessageData = new byte[4];
        requestMessageData[0] = ContactlessCardFunctionId.FLASH_LED;
        switch (light) {
            case BLUE:
                requestMessageData[1] = 1;
                break;
            case YELLOW:
                requestMessageData[1] = 2;
                break;
            case GREEN:
                requestMessageData[1] = 3;
                break;
            case RED:
                requestMessageData[1] = 4;
                break;
            default:
                break;
        }
        System.arraycopy(ExternalMessage.intToBcdBuffer(time), 0, requestMessageData, 2, 2);
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CONTACTLESS_CARD_RESPONSE, ContactlessCardFunctionId.FLASH_LED);
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 3) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        byte[] responseCodeBuf = new byte[]{responseMessageData[1], responseMessageData[2]};
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(responseCodeBuf);
        responseMessage.setResponseCode(responseCode);
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 3);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_FAILED, ExternalErrorMessage.COMMAND_FAILED, innerErrorCode);
            }

            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_RF_CARD_READER_NOT_CONFIGURED, "Reader not configured.", innerErrorCode);
            }

            if (10 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_RF_CARD_NOT_PRESENT, ExternalErrorMessage.CONTACTLESS_CARD_NOT_PRESENT, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }

    public void setLedState(LEDColor[] lights, LEDState state) throws NSDKException {
        if (lights == null || state == null) {
            throw new NSDKIllegalParameterException("Light colors and state shall not be null !");
        }

        byte status = 0, lightColors = 0;
        if (state == LEDState.ON) {
            status = 1;
        } else if (state == LEDState.OFF) {
            status = 2;
        } else if (state == LEDState.BLINK) {
            status = 3;
        }
        for (LEDColor color : lights) {
            if (color == LEDColor.RED || color == LEDColor.FORTH) {
                lightColors |= 0x01;
            } else if (color == LEDColor.YELLOW || color == LEDColor.SECOND) {
                lightColors |= 0x02;
            } else if (color == LEDColor.GREEN || color == LEDColor.THIRD) {
                lightColors |= 0x04;
            } else if (color == LEDColor.BLUE || color == LEDColor.FIRST) {
                lightColors |= 0x08;
            }
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.LED_SET_REQUEST);
        // Request message data = LED type(1 byte) + State(1 byte) + Timeout(2 bytes)
        byte[] requestMessageData = new byte[4];
        requestMessageData[0] = lightColors;
        requestMessageData[1] = status;
        // Timeout is reserved, fixed to 0
        byte[] timeout = ExternalMessage.intToBcdBuffer(0);
        System.arraycopy(requestMessageData, 2, timeout, 0, timeout.length);
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.LED_SET_RESPONSE, null);
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        responseMessage.setResponseCode(responseCode);
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_LED_LATTICE_DATA_ERROR, "Lattice data error.", innerErrorCode);
            }

            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_LED_MODE_ERROR, "Mode error.", innerErrorCode);
            }

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_LED_DATA_ERROR, "Data error.", innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }

    /**
     * Beep.
     *
     * <p>Examples:</p>
     * <pre>
     *     try {
     *         // Alert beeping for 3 seconds.
     *         externalCommon.beep(ExternalBeeperTone.ALERT, 300);
     *
     *         // Success beeping for 2 seconds.
     *         externalCommon.beep(ExternalBeeperTone.SUCCESS, 200);
     *     } catch(NSDKException e) {
     *        // Handle the exception according to different exception types.
     *     }
     *
     * </pre>
     *
     * @param tone     The frequency of beeping. See {@link BeeperTone}
     * @param duration The period of time to beep.
     *                 <ul>
     *                 <li>Unit: 10ms</li>
     *                 <li>Max value: 9999</li>
     *                 </ul>
     * @throws NSDKExternalDeviceException If error occurs.
     */
    public void beep(BeeperTone tone, int duration) throws NSDKException {
        if (tone == null) {
            throw new NSDKIllegalParameterException("Please set beeper tone.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.BEEP_REQUEST);

        // Request message data = Tone(1 byte) + Duration(2 bytes)
        byte[] requestMessageData = new byte[3];
        requestMessageData[0] = (byte) tone.ordinal();
        byte[] durationBuf = ExternalMessage.intToBcdBuffer(duration);
        System.arraycopy(durationBuf, 0, requestMessageData, 1, durationBuf.length);
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, null, null);
        if (responseMessage.getResponseCode() != ErrorCode.OK) {
            // beep 指令的响应只有 0x06（成功）或者 0x15（失败）
            throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR);
        }
    }

    public void beep(int frequency, int duration) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.BUZZER_BEEP_REQUEST);
        try (ByteArrayOutputStream messageStream = new ByteArrayOutputStream()){
            messageStream.write(ExternalMessage.intToHexBuf(frequency));
            messageStream.write(ExternalMessage.intTo4BytesHex(duration));
            requestMessage.setMessageData(messageStream.toByteArray());
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.BUZZER_BEEP_RESPONSE, null);
        byte[] responseData = responseMessage.getMessageData();
        if (responseData == null || responseData.length < 2) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseData, 2));
        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseData, 2);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }
            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }
            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Bad command length.", innerErrorCode);
            }
            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }

    /**
     * Get the external PIN pad serial number.
     *
     * @return External PIN pad serial number.
     * @throws NSDKExternalDeviceException If error occurs.
     */
    public String getSerialNumber() throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.GET_SET_SERIAL_NUMBER_REQUEST);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.GET_SET_SERIAL_NUMBER_RESPONSE, null);

        // Response message data = Serial number
        byte[] responseMessageData = responseMessage.getMessageData();

        if (responseMessageData == null || responseMessageData.length == 0) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        for (byte data : responseMessageData) {
            if (data != 00) {
                baos.write(data);
            }
        }

        return new String(baos.toByteArray());
    }

    /**
     * Set the serial number to PIN pad.
     *
     * @param serialNumber The serial number set to PIN pad. The length of serial number shall be <= 16.
     * @throws NSDKExternalDeviceException If error occurs.
     * @deprecated
     */
    public void setSerialNumber(String serialNumber) throws NSDKException {
        if (serialNumber == null || serialNumber.isEmpty() || serialNumber.length() > 16) {
            throw new NSDKIllegalParameterException("Invalid serial number.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.GET_SET_SERIAL_NUMBER_REQUEST);
        requestMessage.setMessageData(String.format("%-16s", serialNumber).getBytes());

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.GET_SET_SERIAL_NUMBER_RESPONSE, null);

        // Response message data = Serial number
        byte[] responseMessageData = responseMessage.getMessageData();

        if (responseMessageData == null || responseMessageData.length == 0) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        String responseSerialNumber = new String(responseMessageData);

        if (!serialNumber.equals(responseSerialNumber)) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, "Failed to set serial number.");
        }
    }

    /**
     * Load configuration to the external device.
     *
     * <p>Example:</p>
     * <pre>
     *     // Load configuration with specified parameters.
     *     ExternalConfiguration configuration = new ExternalConfiguration();
     *     configuration.setBaudRateMode(BaudRateMode.MODE_115200_8_N_1);
     *     configuration.setWorkingKeyDecryptionMode(WorkingKeyDecryptionMode.TDEA_ECB);
     *     configuration.setBeeperControl(BeeperControl.OFF);
     *
     *     try {
     *         externalCommon.loadConfiguration(configuration);
     *     } catch (NSDKException e) {
     *         // Handle the exception according to different exception types.
     *     }
     * </pre>
     *
     * @param configuration Configuration to load. See {@link DeviceConfiguration}
     * @throws NSDKExternalDeviceException If error occurs.
     */
    public void loadConfiguration(DeviceConfiguration configuration) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CONFIGURATION_LOAD_REQUEST);
        byte[] configBuf = new byte[4];
        if (configuration.getBaudRateMode() == null
                || configuration.getBeeperControl() == null
                || configuration.getWorkingKeyDecryptionMode() == null) {
            throw new NSDKIllegalParameterException("Please set baud rate, working key decrypt mode, beeper control.");
        }

        // Request message data = Baud rate mode(2 bytes) + Working key decryption Mode(1 byte) + Beeper control(1 byte)
        System.arraycopy(configuration.getBaudRateMode().getCode().getBytes(), 0, configBuf, 0, 2);
        // Convert 0 to 0x30, 1 to 0x31, 2 to 0x32
        configBuf[2] = (byte) (configuration.getWorkingKeyDecryptionMode().ordinal() + '0');
        configBuf[3] = (byte) (configuration.getBeeperControl().ordinal() + '0');
        requestMessage.setMessageData(configBuf);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CONFIGURATION_LOAD_RESPONSE, null);

        // Response message data = Response code
        byte[] responseMessageData = responseMessage.getMessageData();

        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        responseMessage.setResponseCode(responseCode);

        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            if (5 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR);
        }
    }

    /**
     * Get external configuration.
     *
     * @return External configuration. See {@link DeviceConfiguration}
     * @throws NSDKExternalDeviceException If error occurs.
     */
    public DeviceConfiguration getConfiguration() throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.GET_CONFIGURATION_REQUEST);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.GET_CONFIGURATION_RESPONSE, null);

        // Response message data = Baud rate(2 bytes) + Working key decrypt mode(1 byte) + Beeper control(1 byte)
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        if (responseMessageData.length < 4) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
        }

        DeviceConfiguration config = new DeviceConfiguration();
        config.setBaudRateMode(BaudRateMode.fromString(new String(new byte[]{responseMessageData[0], responseMessageData[1]})));
        byte workingKeyDecryptMode = responseMessageData[2];
        config.setWorkingKeyDecryptionMode(workingKeyDecryptMode == 0x30 ? DecryptionMode.TDEA_CBC : DecryptionMode.TDEA_ECB);
        byte beeperControlMode = responseMessageData[3];
        if (beeperControlMode == 0x30) {
            config.setBeeperControl(BeeperControl.NORMAL);
        } else if (beeperControlMode == 0x31) {
            config.setBeeperControl(BeeperControl.KEY_PAD_ONLY);
        } else {
            config.setBeeperControl(BeeperControl.OFF);
        }
        return config;
    }

    /**
     * Reboot the external device.
     *
     * @throws NSDKException
     */
    public void reboot() throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.REBOOT_REQUEST);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.REBOOT_RESPONSE, null);

        // Response message data = Response code(2 bytes)
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        responseMessage.setResponseCode(responseCode);
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_FAILED, ExternalErrorMessage.COMMAND_FAILED, innerErrorCode);
            }

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_INVALID_COMMAND_SEQUENCE, ExternalErrorMessage.INVALID_COMMAND_SEQUENCE, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }

    /**
     * Reboot the external device.
     *
     * @throws NSDKException
     */
    public int getBatteryPercentage() throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.GET_BATTERY_PERCENTAGE_REQUEST);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.GET_BATTERY_PERCENTAGE_RESPONSE, null);

        // Response message data = Response code(2 bytes) + Value(1 byte)
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        responseMessage.setResponseCode(responseCode);
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_FAILED, ExternalErrorMessage.COMMAND_FAILED, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        if (responseMessageData.length < 3) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
        }

        return responseMessageData[2] & 0xFF;
    }

    public ExtDeviceInfo getDeviceInfo() throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.GET_DEVICE_INFO_REQUEST);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.GET_DEVICE_INFO_RESPONSE, null);

        // Response message data = Response code(2 bytes) + TLV len(2 byte) + TLV data
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        responseMessage.setResponseCode(responseCode);
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.UNSUPPORTED, ExternalErrorMessage.NOT_SUPPORTED, innerErrorCode);
            }

            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Command length error.", innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        try {
            byte[] tlvData = new byte[responseMessageData.length - 2];
            System.arraycopy(responseMessageData, 2, tlvData, 0, tlvData.length);
            int len = ExternalMessage.hexBuffer2Int(Arrays.copyOf(tlvData, 2));
            ExtDeviceInfo deviceInfo = new ExtDeviceInfo();
            if (len > 0) {
                int offset = 2;
                byte[] tag = new byte[2];
                int tLen;
                byte[] tData;
                do {
                    System.arraycopy(tlvData, offset, tag, 0, 2);
                    String tagStr = ISOUtils.hexString(tag);
                    offset += 2;
                    tLen = tlvData[offset];
                    offset++;
                    if (tLen <= 0) {
                        continue;
                    }

                    tData = new byte[tLen];
                    System.arraycopy(tlvData, offset, tData, 0, tData.length);
                    offset += tLen;
                    switch (tagStr) {
                        case ExtDeviceInfoTag.SW_VERSION:
                            deviceInfo.setSoftwareVersion(new String(tData));
                            break;
                        case ExtDeviceInfoTag.POS_SN:
                            deviceInfo.setPosSN(new String(tData));
                            break;
                        case ExtDeviceInfoTag.POS_PN:
                            deviceInfo.setPosPN(new String(tData));
                            break;
                        case ExtDeviceInfoTag.BUILD_OS_VERSION:
                            deviceInfo.setBuildOSVersion(new String(tData));
                            break;
                        case ExtDeviceInfoTag.POS_HW:
                            deviceInfo.setHardware(new String(tData));
                            break;
                        case ExtDeviceInfoTag.NAPI_API_VERSION:
                            deviceInfo.setNapiAPIVersion(new String(tData));
                            break;
                        case ExtDeviceInfoTag.NAPI_LIB_VERSION:
                            deviceInfo.setNapiLibVersion(new String(tData));
                            break;
                        case ExtDeviceInfoTag.BUILD_BOOT_VERSION:
                            deviceInfo.setBuildBootVersion(new String(tData));
                            break;
                        case ExtDeviceInfoTag.BUILD_DEVCFG_VERSION:
                            deviceInfo.setBuildDevCFGVersion(new String(tData));
                            break;
                        case ExtDeviceInfoTag.BUILD_MODEL:
                            deviceInfo.setModel(new String(tData));
                            break;
                        case ExtDeviceInfoTag.BUILD_PCI_FW_VERSION:
                            deviceInfo.setBuildPCIFirmwareVersion(new String(tData));
                            break;
                        case ExtDeviceInfoTag.BUILD_PCI_HW_VERSION:
                            deviceInfo.setBuildPCIHardwareVersion(new String(tData));
                            break;
                        case ExtDeviceInfoTag.POS_CPU_TYPE:
                            deviceInfo.setPosCPUType(new String(tData));
                            break;
                        case ExtDeviceInfoTag.POS_BOARD_VER:
                            deviceInfo.setPosBoardVersion(new String(tData));
                            break;
                        case ExtDeviceInfoTag.POS_BOARD_NUM:
                            deviceInfo.setPosBoardNumber(new String(tData));
                            break;
                        case ExtDeviceInfoTag.RFID_TYPE:
                            deviceInfo.setRfType(new String(tData));
                            break;
                        case ExtDeviceInfoTag.RFID_VERSION:
                            deviceInfo.setRfVersion(new String(tData));
                            break;
                        case ExtDeviceInfoTag.WIFI_DRV_VERSION:
                            deviceInfo.setWifiDrvVersion(new String(tData));
                            break;
//                        case ExtDeviceInfoTag.POS_CUSTOMERID:
//                            deviceInfo.setCustomerID(new String(tData));
//                            break;
//                        case ExtDeviceInfoTag.SYS_LANGUAGE:
//                            deviceInfo.setLanguage(new String(tData));
//                            break;
//                        case ExtDeviceInfoTag.SYS_PRINTER_GREY_LEVEL:
//                            deviceInfo.setPrinterGreyLevel(new String(tData));
//                            break;
//                        case ExtDeviceInfoTag.SYS_AUTORUN:
//                            deviceInfo.setAutoRun(new String(tData));
//                            break;
//                        case ExtDeviceInfoTag.SYS_AUTOSLEEP:
//                            deviceInfo.setAutoSleep(new String(tData));
//                            break;
//                        case ExtDeviceInfoTag.SYS_BRIGHTNESS:
//                            deviceInfo.setBrightness(new String(tData));
//                            break;
//                        case ExtDeviceInfoTag.SYS_BACKLIGHT_ONOFF:
//                            deviceInfo.setBackLightOn(tData[0] == 1 ? true : false);
//                            break;
//                        case ExtDeviceInfoTag.SYS_KEYVOL:
//                            deviceInfo.setKeyVol(tData[0]);
//                            break;
//                        case ExtDeviceInfoTag.SYS_BOOTUP_PROMPT_BMP:
//                            deviceInfo.setBootUpPromptBmp(new String(tData));
//                            break;
//                        case ExtDeviceInfoTag.SYS_SHUTDOWN_PROMPT_BMP:
//                            deviceInfo.setShutdownPromptBmp(new String(tData));
//                            break;
//                        case ExtDeviceInfoTag.SYS_SHUTDOWN_PROMPT_BMP_IN_CHARGING:
//                            deviceInfo.setShutdownPromptBmpInCharging(new String(tData));
//                            break;
//                        case ExtDeviceInfoTag.SYS_SHUTDOWN_PROMPT_BMP_IN_FULLCHARGING:
//                            deviceInfo.setShutdownPromptBmpInFullCharging(new String(tData));
//                            break;
//                        case ExtDeviceInfoTag.SYS_SHUTDOWN_PROMPT_BMP_IN_LOWPOWER:
//                            deviceInfo.setShutdownPromptBmpInLowPower(new String(tData));
//                            break;
//                        case ExtDeviceInfoTag.POWER_MODE:
//                            deviceInfo.setPowerMode(tData[0]);
//                            break;
//                        case ExtDeviceInfoTag.BATTERY_STATUS:
//                            deviceInfo.setBatteryStatus(new String(tData));
//                            break;
//                        case ExtDeviceInfoTag.BATTERY_LEVEL:
//                            deviceInfo.setBatteryLevel(tData[0]);
//                            break;
//                        case ExtDeviceInfoTag.POWER_AUTO_POWEROFF:
//                            deviceInfo.setAutoPowerOff(tData[0] == 1 ? true : false);
//                            break;
//                        case ExtDeviceInfoTag.POWER_AUTO_WAKEUP:
//                            deviceInfo.setAutoWakeUp(tData[0] == 1 ? true : false);
//                            break;
//                        case ExtDeviceInfoTag.PRINT_LEN:
//                            deviceInfo.setPrintLen(tData[0]);
//                            break;
//                        case ExtDeviceInfoTag.POWER_RUN_TIME:
//                            deviceInfo.setPowerRunTime(new String(tData));
//                            break;
                        default:
                            break;
                    }
                } while (offset < tlvData.length);
            }
            DeviceAttribute deviceAttribute = getDeviceAttributes();
            deviceInfo.setDeviceAttribute(deviceAttribute);
            return deviceInfo;
        } catch (Exception e) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_MESSAGE_ERROR, ExternalErrorMessage.MESSAGE_EXTRACT_ERROR, e);
        }
    }

    /**
     * Set or get device datetime.
     *
     * @param operation 0-Get datetime; 1-Set datetime.
     * @param datetime  Required when operation is 1. Format: YYYYMMDDHHMMSS.
     * @return Datetime when operation is 0.
     * @throws NSDKException
     */
    public String operateDatetime(int operation, String datetime) throws NSDKException {
        if (operation == OPERATION_SET && TextUtils.isEmpty(datetime)) {
            throw new NSDKIllegalParameterException("Datetime is required when setting datetime.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.SET_GET_DATETIME_REQUEST);
        ByteArrayOutputStream messageStream = new ByteArrayOutputStream();
        try {
            messageStream.write(operation);
            if (operation == OPERATION_SET) {
                messageStream.write(datetime.getBytes());
            }
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }
        requestMessage.setMessageData(messageStream.toByteArray());

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.SET_GET_DATETIME_RESPONSE, null);

        // Response message data = Response code(2 bytes) + Current time(19 bytes)
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        responseMessage.setResponseCode(responseCode);

        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_PINPAD_FORMAT_ERROR, "Format error.", innerErrorCode);
            }

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Command length error.", innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        int len = responseMessageData.length - 2;
        if (operation == OPERATION_SET || len == 0) {
            return null;
        }

        byte[] timeBuf = new byte[len];
        System.arraycopy(responseMessageData, 2, timeBuf, 0, len);
        return new String(timeBuf);
    }

    public void setConnectMode(DeviceConnectMode mode) throws NSDKException {
        if (mode == null) {
            throw new NSDKIllegalParameterException("Mode shall not be null.");
        }
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.SET_CONNECT_MODE_REQUEST);
        requestMessage.setMessageData(new byte[]{(byte) mode.ordinal()});

        int originalReceiveTimeout = ExternalCommunicationManager.getInstance().getReceiveTimeout();
        // 设置连接模式如果成功，会断开连接，此处设置接收时间让接口快点返回
        if (originalReceiveTimeout > 5000) {
            ExternalCommunicationManager.getInstance().setReceiveTimeout(5000);
        }

        ExternalMessage responseMessage;
        try {
            responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.SET_CONNECT_MODE_RESPONSE, null);
        } catch (Exception e) {
            // 恢复原有的接收时间
            ExternalCommunicationManager.getInstance().setReceiveTimeout(originalReceiveTimeout);
            throw e;
        }

        // Response message data = Response code(2 bytes)
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        responseMessage.setResponseCode(responseCode);
        // 2 是当前模式已经是用户想要设置的模式了，可以返回成功
        if (ErrorCode.OK != responseCode && 2 != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Command length error.", innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }

    /**
     * Set or get bluetooth info.
     *
     * @param operation 0-Get; 1-Set
     * @param name      Required when operation is 1.
     * @return Bluetooth info when operation is 0.
     * @throws NSDKException
     */
    public BluetoothInfo operateBluetoothInfo(int operation, String name) throws NSDKException {
        if (operation == OPERATION_SET && TextUtils.isEmpty(name)) {
            throw new NSDKIllegalParameterException("Bluetooth name is required when setting.");
        }
        if (operation == OPERATION_SET && name.length() > 21) {
            throw new NSDKIllegalParameterException("Bluetooth name length shall not be more than 21.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.SET_GET_BLUETOOTH_REQUEST);

        ByteArrayOutputStream messageStream = new ByteArrayOutputStream();
        int originalReceiveTimeout = ExternalCommunicationManager.getInstance().getReceiveTimeout();

        try {
            messageStream.write(operation);
            if (operation == OPERATION_SET) {
                byte[] nameData = name.getBytes();
                messageStream.write(nameData.length);
                messageStream.write(nameData);
                // 设置蓝牙名称如果成功，会断开连接，此处设置接收时间让接口快点返回
                if (originalReceiveTimeout > 5000) {
                    ExternalCommunicationManager.getInstance().setReceiveTimeout(5000);
                }
            }
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }
        requestMessage.setMessageData(messageStream.toByteArray());

        ExternalMessage responseMessage;
        try {
            responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.SET_GET_BLUETOOTH_RESPONSE, null);
        } catch (Exception e) {
            if (operation == OPERATION_SET) {
                // 恢复原有的接收时间
                ExternalCommunicationManager.getInstance().setReceiveTimeout(originalReceiveTimeout);
            }
            throw e;
        }

        // Response message data = Response code(2 bytes) + BT name len(1 byte) + BT name + MAC address len(1 byte) + MAC address
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        responseMessage.setResponseCode(responseCode);

        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Command length error.", innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        int len = responseMessageData.length - 2;
        BluetoothInfo bluetoothInfo = new BluetoothInfo();
        if (operation == OPERATION_SET || len == 0) {
            return bluetoothInfo;
        }

        try {
            byte[] data = new byte[responseMessageData.length - 2];
            System.arraycopy(responseMessageData, 2, data, 0, data.length);
            int offset = 0;
            int tempLen = data[offset];
            byte[] tempBuf;
            offset++;
            if (tempLen > 0) {
                // bluetooth name
                tempBuf = new byte[tempLen];
                System.arraycopy(data, offset, tempBuf, 0, tempBuf.length);
                bluetoothInfo.setName(new String(tempBuf));
                offset += tempLen;
            }
            tempLen = data.length - offset;
            if (tempLen > 0) {
                // bluetooth mac address
                tempBuf = new byte[tempLen];
                System.arraycopy(data, offset, tempBuf, 0, tempBuf.length);
                bluetoothInfo.setMacAddress(new String(tempBuf));
            }
            return bluetoothInfo;
        } catch (Exception e) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_MESSAGE_ERROR, ExternalErrorMessage.MESSAGE_EXTRACT_ERROR, e);
        }
    }

    /**
     * @param fileType  <ul>
     *                  <li>0-language</li>
     *                  <li>1-application</li>
     *                  <li>2-firmware</li>
     *                  <li>3-power on icon</li>
     *                  <li>4-power off icon</li>
     *                  <li>5-charging icon when power off</li>
     *                  <li>6-Temp buff</li>
     *                  </ul>
     * @param writeMode 1-Write data; 2-Finish writing
     * @param offset    When write mode is 2, default value is 0
     * @param data      Data to write. When write mode is 2, this is SHA1 of the whole file.
     * @throws NSDKException
     */
    public void transferFile(byte fileType, byte writeMode, int offset, byte[] data) throws NSDKException {
        if (data == null) {
            throw new NSDKIllegalParameterException("File type and data shall not be null.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.FILE_TRANSMIT_REQUEST);

        ByteArrayOutputStream messageStream = new ByteArrayOutputStream();
        try {
            messageStream.write(fileType);
            messageStream.write(writeMode);
            if (writeMode == FILE_WRITE_MODE_SHA1) {
                messageStream.write(new byte[]{0x00, 0x00, 0x00, 0x00});
            } else {
                messageStream.write(ExternalMessage.intTo4BytesHex(offset));
            }
            if (data.length > 0) {
                messageStream.write(ExternalMessage.intToHexBuf(data.length));
                messageStream.write(data);
            } else {
                messageStream.write(ExternalMessage.intToHexBuf(0));
            }
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }
        requestMessage.setMessageData(messageStream.toByteArray());

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.FILE_TRANSMIT_RESPONSE, null);

        // Response message data = Response code(2 bytes)
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        responseMessage.setResponseCode(responseCode);

        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ICON_SET_ERROR, "Failed to set icon", innerErrorCode);
            }

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            if (5 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_FILE_OPEN_ERROR, "Failed to open file", innerErrorCode);
            }

            if (6 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_FILE_WRONG_OFFSET, "Wrong offset", innerErrorCode);
            }

            if (7 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_FILE_WRITE_ERROR, "Failed to write file", innerErrorCode);
            }

            if (8 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_FILE_SHA1_ERROR, "SHA1 error", innerErrorCode);
            }

            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Command length error.", innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }

    /**
     * @param updateType 0-Update application; 1-Update firmware.
     * @throws NSDKException
     */
    public void updateAppFirmware(byte updateType) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.UPDATE_APP_FW_REQUEST);
        requestMessage.setMessageData(new byte[]{updateType});

        // 启动更新后，只接收一个 06 即完成此指令了。至于是否有更新成功，需要用户自行检查设备
        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.UPDATE_APP_FW_RESPONSE, null);

        // Response message data = Response code(2 bytes)
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        responseMessage.setResponseCode(responseCode);

        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_APP_FILE_CREATE_ERROR, "File transmission is not completed.", innerErrorCode);
            }

            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_APP_UPDATE_ERROR, "Failed to update.", innerErrorCode);
            }

            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Command length error.", innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }

    /**
     * @param operation 0-Get current language type; 1-Update language which has been transferred to device before.
     * @return
     * @throws NSDKException
     */
    public String operateLanguage(byte operation) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.SET_GET_LANGUAGE_REQUEST);
        requestMessage.setMessageData(new byte[]{operation});

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.SET_GET_LANGUAGE_RESPONSE, null);

        // Response message data = Response code(2 bytes) + Language name len(1 byte) + Language name
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        responseMessage.setResponseCode(responseCode);

        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Command length error.", innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        if (responseMessageData.length <= 3) {
            return "";
        }

        byte[] nameBuf = new byte[responseMessageData.length - 3];
        System.arraycopy(responseMessageData, 3, nameBuf, 0, nameBuf.length);
        return new String(nameBuf);
    }

    /**
     * @param timeConfiguration
     * @throws NSDKException
     */
    public TimeConfiguration operateTimes(byte operation, TimeConfiguration timeConfiguration) throws NSDKException {
        if (operation == OPERATION_SET) {
            if (timeConfiguration == null) {
                throw new NSDKIllegalParameterException("Time configuration shall not be null.");
            }
            if (timeConfiguration.getAutoBacklightOffTime() < 0 || timeConfiguration.getAutoSleepTime() < 0 || timeConfiguration.getAutoTurnOffTime() < 0) {
                throw new NSDKIllegalParameterException("Time shall be >0");
            }
        }
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.OPERATE_TIMES_REQUEST);
        ByteArrayOutputStream messageStream = new ByteArrayOutputStream();
        try {
            messageStream.write(operation);
            if (operation == OPERATION_SET) {
                messageStream.write(ExternalMessage.intToHexBuf(timeConfiguration.getAutoBacklightOffTime()));
                messageStream.write(ExternalMessage.intToHexBuf(timeConfiguration.getAutoSleepTime()));
                messageStream.write(ExternalMessage.intToHexBuf(timeConfiguration.getAutoTurnOffTime()));
            }
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }
        requestMessage.setMessageData(messageStream.toByteArray());

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.OPERATE_TIMES_RESPONSE, null);

        // Response message data = Response code(2 bytes)
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        responseMessage.setResponseCode(responseCode);
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }

            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Command length error.", innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        TimeConfiguration configuration = new TimeConfiguration();
        if (operation == OPERATION_GET) {
            try {
                configuration.setAutoBacklightOffTime(ExternalMessage.hexBuffer2Int(new byte[]{responseMessageData[2], responseMessageData[3]}));
                configuration.setAutoSleepTime(ExternalMessage.hexBuffer2Int(new byte[]{responseMessageData[4], responseMessageData[5]}));
                configuration.setAutoTurnOffTime(ExternalMessage.hexBuffer2Int(new byte[]{responseMessageData[6], responseMessageData[7]}));
            } catch (Exception e) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_MESSAGE_ERROR, ExternalErrorMessage.MESSAGE_EXTRACT_ERROR, e);
            }
        }
        return configuration;
    }

    public void setCryptoMode(SymmetricKey key, AlgorithmParameters params) throws NSDKException {
        if(key == null || params == null || key.getKeyUsage() == null || params.getPaddingMode() == null) {
            throw new NSDKIllegalParameterException();
        }
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.SET_ENCRYPTION_CMD_REQUEST);

        byte[] iv = params.getIV();
        CipherType cipherType = ExtToolUtils.combineCipherType(key, params);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(cipherType.getCode());
        outputStream.write(key.getKeyUsage().getCode());
        outputStream.write(params.getPaddingMode().getCode());
        try {
            if(iv != null && iv.length > 0) {
                outputStream.write(iv.length);
                outputStream.write(iv);
            } else {
                outputStream.write(0);
            }
            outputStream.write(new byte[]{0x00, 0x00});
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }
        requestMessage.setMessageData(outputStream.toByteArray());

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.SET_ENCRYPTION_CMD_RESPONSE, null);

        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        responseMessage.setResponseCode(responseCode);
        if(ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if(1 == responseCode) {
                throw new NSDKIllegalParameterException();
            }
            if(2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_FAILED, ExternalErrorMessage.COMMAND_FAILED, innerErrorCode);
            }
            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }

    public void setProperty(String key, String value) throws NSDKException {
        if(key == null || value == null) {
            throw new NSDKIllegalParameterException();
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.SET_PROPERTY_REQUEST);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            outputStream.write(key.length());
            outputStream.write(key.getBytes(StandardCharsets.UTF_8));

            outputStream.write(ExternalMessage.intToHexBuf(value.length()));
            outputStream.write(value.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }
        requestMessage.setMessageData(outputStream.toByteArray());

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.SET_PROPERTY_RESPONSE, null);

        byte[] responseMessageData = responseMessage.getMessageData();
        if(responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        responseMessage.setResponseCode(responseCode);
        if(ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if(4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }
            if(45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Command length error.", innerErrorCode);
            }
            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

    }

    public String getProperty(String key) throws NSDKException {
        if(key == null) {
            throw new NSDKIllegalParameterException();
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.GET_PROPERTY_REQUEST);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            outputStream.write(key.length());
            outputStream.write(key.getBytes(StandardCharsets.UTF_8));

            outputStream.write(ExternalMessage.intToHexBuf(256));
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }
        requestMessage.setMessageData(outputStream.toByteArray());

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.GET_PROPERTY_RESPONSE, null);

        byte[] responseMessageData = responseMessage.getMessageData();
        if(responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        responseMessage.setResponseCode(responseCode);
        if(ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if(4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }
            if(45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Command length error.", innerErrorCode);
            }
            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }

        if(responseMessageData.length > 2) {
            int valueLen = ExternalMessage.hexBuffer2Int(new byte[]{responseMessageData[2], responseMessageData[3]});
            if(valueLen <= 0) {
                return null;
            }
            if(valueLen > responseMessageData.length - 4) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LEN_FIELD_ERROR, ExternalErrorMessage.DATA_LEN_FIELD_ERROR);
            }
            byte[] value = new byte[valueLen];
            System.arraycopy(responseMessageData, 4, value, 0, valueLen);
            return new String(value);
        }

        return null;
    }

    public ArrayList<FileInfo> getFileList(String keyword, byte[] tlvData) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.GET_FILE_LIST_REQUEST);
        try (ByteArrayOutputStream messageStream = new ByteArrayOutputStream()) {
            if (tlvData == null || tlvData.length == 0) {
                messageStream.write(ExternalMessage.intToHexBuf(0));
            } else {
                messageStream.write(ExternalMessage.intToHexBuf(tlvData.length));
                messageStream.write(tlvData);
            }
            messageStream.write(keyword.getBytes().length);
            messageStream.write(keyword.getBytes());
            requestMessage.setMessageData(messageStream.toByteArray());
        } catch (IOException e) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.GET_FILE_LIST_RESPONSE, null);
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, "Failed to open dir.", innerErrorCode);
            }
            if (5 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, "Too much data to display.", innerErrorCode);
            }
            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Command Length Error.", innerErrorCode);
            }
        }
        byte[] fileData = new byte[responseMessageData.length - 2];
        System.arraycopy(responseMessageData, 2, fileData, 0, fileData.length);
        byte[] number = Arrays.copyOf(fileData, 2);
        int fileAccount = ExternalMessage.hexBuffer2Int(number);
        ArrayList<FileInfo> fileInfoList = new ArrayList<>();
        int offset = 2;
        for (int i = 0; i < fileAccount; i++) {
            int fileNameLength = fileData[offset];
            Log.d("debug", String.format(Locale.US, "fileNameLength: %d, fileData[%d]:%02x", fileNameLength, offset, fileData[offset]));
            offset++;
            byte[] fileName = new byte[fileNameLength];
            System.arraycopy(fileData, offset, fileName, 0, fileNameLength);
            offset += fileNameLength;
            int fileInfoLength = fileData[offset];
            Log.d("debug", String.format(Locale.US, "fileInfoLength: %d, fileData[%d]:%02x", fileInfoLength, offset, fileData[offset]));
            offset++;
            byte[] fileInfoData = new byte[fileInfoLength];
            System.arraycopy(fileData, offset, fileInfoData, 0, fileInfoLength);
            offset += fileInfoLength;
            FileInfo fileInfo = new FileInfo();
            fileInfo.setInfo(fileInfoData);
            fileInfo.setName(new String(fileName));
            fileInfoList.add(fileInfo);
        }
        return fileInfoList;
    }

    private DeviceAttribute getDeviceAttributes() throws NSDKException {
        DeviceAttribute deviceAttribute = new DeviceAttribute();
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.DEVICE_ATTRIBUTE_REQUEST);
        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.DEVICE_ATTRIBUTE_RESPONSE, null);
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new NSDKException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        byte[] featureBitmap = new byte[2];
        System.arraycopy(responseMessageData, 0, featureBitmap, 0, 2);
        byte featureBitmapValue = featureBitmap[1];
        deviceAttribute.setSupportSmartCard((featureBitmapValue & 0x01) == 0x01);
        deviceAttribute.setSupportContactlessCard((featureBitmapValue & 0x02) == 0x02);
        deviceAttribute.setSupportMagCard((featureBitmapValue & 0x04) == 0x04);
        deviceAttribute.setSupportGraphicalDisplay((featureBitmapValue & 0x08) == 0x08);
        deviceAttribute.setSupportColourDisplay((featureBitmapValue & 0x10) == 0x10);
        deviceAttribute.setSupportBeeper((featureBitmapValue & 0x20) == 0x20);
        deviceAttribute.setSupportBacklight((featureBitmapValue & 0x40) == 0x40);
        byte[] pciFirmwareID = new byte[responseMessageData.length - 2];
        System.arraycopy(responseMessageData, 2, pciFirmwareID, 0, responseMessageData.length - 2);
        deviceAttribute.setPciFirmwareID(new String(pciFirmwareID));
        return deviceAttribute;
    }
}

package com.newland.nsdk.core.external.command.updater;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;
import com.newland.nsdk.core.api.external.exception.NSDKExternalDeviceException;
import com.newland.nsdk.core.external.command.exception.ExternalMessageException;

/**
 * Provides the ability to update firmware or apps.
 *
 * <p>The process to update an app or firmware:</p>
 * <ol>
 *     <li>Create file.</li>
 *     <li>Load file data.</li>
 *     <li>Update. </li>
 * </ol>
 */
public class ExternalUpdaterModule {
    /**
     * Create file before loading it.
     *
     * <p>If the device is turned off after file created successfully, the file shall be created again when the device is turned on.</p>
     *
     * @param appName App name for loading.
     * @throws NSDKException
     */
    public void createFile(byte[] appName) throws NSDKException {
        if (appName == null || appName.length == 0) {
            throw new NSDKIllegalParameterException("Please set app name.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.FILE_CREATE_REQUEST);
        // Request message data = App name len(1 byte) + App name
        byte[] requestMessageData = new byte[1 + appName.length];
        requestMessageData[0] = (byte) appName.length;
        System.arraycopy(appName, 0, requestMessageData, 1, appName.length);
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.FILE_CREATE_RESPONSE, null);
        checkResponseCode(responseMessage);
    }

    /**
     * Load firmware/app file.
     *
     * @param fileData File data.
     * @throws NSDKException
     */
    public void loadApp(byte[] fileData) throws NSDKException {
        if (fileData == null || fileData.length == 0) {
            throw new NSDKIllegalParameterException("Please set file data.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.APP_LOAD_REQUEST);
        // Request message data = Data len(2 byte) + File data
        byte[] requestMessageData = new byte[2 + fileData.length];
        System.arraycopy(ExternalMessage.intToHexBuf(fileData.length), 0, requestMessageData, 0, 2);
        System.arraycopy(fileData, 0, requestMessageData, 2, fileData.length);
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.APP_LOAD_RESPONSE, null);
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(new byte[]{responseMessageData[0], responseMessageData[1]});
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_APP_FILE_DELETE_ERROR, ExternalErrorMessage.UPDATER_DELETE_ERROR, innerErrorCode);
            }

            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_APP_FILE_OPEN_ERROR, ExternalErrorMessage.UPDATER_OPEN_FAILED, innerErrorCode);
            }

            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_APP_FILE_WRITE_ERROR, ExternalErrorMessage.UPDATER_WRITE_FAILED, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }

    /**
     * Update firmware/app.
     *
     * <p>The firmware/app file will be deleted after updating successfully.</p>
     *
     * @param fileName          File data.
     * @param rebootAfterUpdate Whether reboot after update or not.
     *                          <ul>
     *                          <li>true: Reboot after update.</li>
     *                          <li>false:Not reboot after update.</li>
     *                          </ul>
     */
    public void updateApp(byte[] fileName, boolean rebootAfterUpdate) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.APP_UPDATE_REQUEST);
        // Request message data = App name len(1 byte) + App name + Reboot flag
        byte[] requestMessageData = new byte[1 + fileName.length + 1];
        requestMessageData[0] = (byte) fileName.length;
        System.arraycopy(fileName, 0, requestMessageData, 1, fileName.length);
        requestMessageData[requestMessageData.length - 1] = (byte) (rebootAfterUpdate ? 1 : 0);
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.APP_UPDATE_RESPONSE, null);
        checkResponseCode(responseMessage);
    }

    private void checkResponseCode(ExternalMessage responseMessage) throws NSDKException {
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(new byte[]{responseMessageData[0], responseMessageData[1]});
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_APP_FILE_DELETE_ERROR, ExternalErrorMessage.UPDATER_DELETE_ERROR, innerErrorCode);
            }

            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_APP_FILE_CREATE_ERROR, ExternalErrorMessage.UPDATER_CREATE_ERROR, innerErrorCode);
            }

            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_DEVICE_OPEN_ERROR, "Device open error.", innerErrorCode);
            }

            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_APP_NLD_INFO_ERROR, "NLD info error.", innerErrorCode);
            }

            if (5 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_APP_SIGN_DECRYPTION_ERROR, "Sign decryption error.", innerErrorCode);
            }

            if (6 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_APP_SIGN_CHECK_ERROR, "Sign check error.", innerErrorCode);
            }

            if (7 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_APP_UPDATE_ERROR, "Update error.", innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }
}


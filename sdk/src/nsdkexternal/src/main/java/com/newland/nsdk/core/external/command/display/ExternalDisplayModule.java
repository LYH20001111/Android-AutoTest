package com.newland.nsdk.core.external.command.display;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.external.display.ButtonCode;
import com.newland.nsdk.core.api.external.display.ButtonParameters;
import com.newland.nsdk.core.api.external.display.PictureParameters;
import com.newland.nsdk.core.api.external.display.SelectionCallback;
import com.newland.nsdk.core.api.external.display.TitleParameters;
import com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager;
import com.newland.nsdk.core.api.external.display.AlignType;
import com.newland.nsdk.core.api.external.display.CNTextNotifyType;
import com.newland.nsdk.core.api.external.display.DiaplayQRImageParameters;
import com.newland.nsdk.core.api.external.display.FontSize;
import com.newland.nsdk.core.api.external.exception.NSDKExternalDeviceException;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;
import com.newland.nsdk.core.external.command.exception.ExternalMessageException;
import com.newland.nsdk.core.external.command.message.ExternalMessage;
import com.newland.nsdk.core.external.command.message.ExternalMessageType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Provides display related functions.
 */
public class ExternalDisplayModule {
    public static final int MAX_IMAGE_DATA_LEN = 4000;

    /**
     * Sets PIN/text display mode.
     *
     * @param mode Display mode which indicates whether to clean screen before displaying PIN/text.
     *             <ul>
     *             <li>Set mode to 1: it will clean screen first, then display PIN/text to the specified lines. In this case, the screen will only have the PIN/text displayed.</li>
     *             <li>Set mode to any other value: it will not clean screen before displaying PIN/text. In this case, the screen will still have some old information displayed with the PIN/text.</li>
     *             </ul>
     * @throws NSDKException
     */
    public void setDisplayMode(byte mode) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.SET_PIN_TEXT_DISPLAY_MODE_REQUEST);


        byte[] modeBuf = new byte[1];
        modeBuf[0] = mode;
        requestMessage.setMessageData(modeBuf);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.SET_PIN_TEXT_DISPLAY_MODE_RESPONSE, null);

        byte[] responseMessageData = responseMessage.getMessageData();

        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));

        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
        }
    }

    /**
     * Display texts on the screen of the external device.
     *
     * <p>Example:</p>
     * <pre>
     *     try {
     *         // Display messages on all lines.
     *         String[] messagesAll = new String[]{"Message for line 1", "Message for line 2", "Message for line 3", "Message for line 4"};
     *         externalDisplay.displayText(messagesAll);
     *
     *         // Display messages on line 1 and 3.
     *         String[] messagesLine1and3 = new String[]{"Message for line 1", null, "Message for line 3", ""};
     *         externalDisplay.displayText(messagesLine1and3);
     *     } catch(NSDKException e) {
     *         if (e instanceof NSDKIlligalParameterException) {
     *         // Do something when invalid parameters
     *         } else {
     *             // Handle other types of exception
     *         }
     *     }
     * </pre>
     *
     * @param messages The messages that will display on the screen of the external device.
     *                 <ul>
     *                 <li>Each string of the "messages" parameter will be displayed on one line. 4 lines supported.</li>
     *                 <li>Messages shall be encoded in UTF8 format to be able to support multiple languages.</li>
     *                 <li>If the string is null or empty, means it is an empty line. </li>
     *                 </ul>
     * @throws NSDKException
     */
    public void displayText(String[] messages) throws NSDKException {

        if (messages == null || messages.length > 4) {
            throw new NSDKIllegalParameterException("Invalid messages.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.DISPLAY_TEXT_REQUEST);

        ByteArrayOutputStream messageStream = new ByteArrayOutputStream();
        for (int i = 0; i < messages.length; i++) {
            try {
                if (messages[i] != null && !messages[i].isEmpty()) {
                    messageStream.write(messages[i].getBytes());
                }
                messageStream.write(0x1c);
            } catch (IOException e) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
            }
        }

        byte[] messageBuf;
        messageBuf = messageStream.toByteArray();

        requestMessage.setMessageData(messageBuf);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, null, null);

        if (responseMessage.getResponseCode() != ErrorCode.OK) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR);
        }
    }


    /**
     * Display the image which is already loaded to the external device.
     *
     * @param imageId   The ID of the image to display. Value range:
     *                  <ul>
     *                  <li>0: Show the image with the following image data.</li>
     *                  <li>[1-255]: The image shall already be loaded to the external device before.</li>
     *                  </ul>
     * @param imageData Image data for displaying when image ID is set to 0, otherwise set image data to null.
     * @param x         Display X coordinate.
     * @param y         Display Y coordinate.
     * @throws NSDKException
     */
    public void displayImage(byte imageId, byte[] imageData, int x, int y) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.DISPLAY_IMAGE_REQUEST);

        byte[] xBuf = ExternalMessage.intToHexBuf(x);
        byte[] yBuf = ExternalMessage.intToHexBuf(y);
        ByteArrayOutputStream messageStream = new ByteArrayOutputStream();
        try {
            messageStream.write(imageId);
            messageStream.write(xBuf);
            messageStream.write(yBuf);
            if (imageData != null) {
                int len = imageData.length;
                byte[] lenBuf = ExternalMessage.intToHexBuf(len);
                messageStream.write(lenBuf);
                if (len > 0) {
                    messageStream.write(imageData);
                }
            }
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        byte[] imageBuf = messageStream.toByteArray();


        requestMessage.setMessageData(imageBuf);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, null, null);

        if (responseMessage.getResponseCode() != ErrorCode.OK) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR);
        }
    }

    /**
     * Load the image to the specified ID.
     *
     * @param imageId   Load the image to this ID.
     * @param imageData Image data.
     * @throws NSDKException
     */
    public void loadImage(byte imageId, byte[] imageData) throws NSDKException {

        if (imageData == null) {
            throw new NSDKIllegalParameterException("Invalid image data.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.LOAD_IMAGE_REQUEST);

        int len = imageData.length;
        byte[] lenBuf = ExternalMessage.intToHexBuf(len);

        ByteArrayOutputStream messageStream = new ByteArrayOutputStream();
        try {
            messageStream.write(imageId);
            messageStream.write(lenBuf);
            messageStream.write(imageData);

        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        byte[] imageBuf = messageStream.toByteArray();

        requestMessage.setMessageData(imageBuf);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.LOAD_IMAGE_RESPONSE, null);
        byte[] responseMessageData = responseMessage.getMessageData();

        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));

        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
        }
    }

    /**
     * Set color to the target.
     *
     * <p>Example:</p>
     * <pre>
     *     int someColor = 0xF0F1;
     *     try {
     *         externalDisplay.setFontColor(someColor, 0);
     *     } catch (NSDKException e) {
     *         if (e instanceof NSDKIlligalParameterException) {
     *             // Do something when invalid parameters
     *         } else {
     *             // Handle other types of exception
     *         }
     *     }
     *
     * </pre>
     *
     * @param color  Color code. Value range: 0x0000~0xFFFF.
     * @param target Set the color to this target.
     *               <ul>
     *                   <li>target == 0: Set font color</li>
     *                   <li>Other values: Set color to others, e.g., highlight color. Not supported now.</li>
     *               </ul>
     * @throws NSDKException
     */
    public void setFontColor(int color, byte target) throws NSDKException {

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.SET_FONT_COLOR_REQUEST);

        byte[] requestMessageData = new byte[3];
        byte[] colorBuf = ExternalMessage.intToHexBuf(color);
        System.arraycopy(colorBuf, 0, requestMessageData, 0, 2);
        requestMessageData[2] = target;
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.SET_FONT_COLOR_RESPONSE, null);

        // Response data = Response Code
        byte[] responseMessageData = responseMessage.getMessageData();

        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));

        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
        }
    }

    /**
     * Clear the external device's screen.
     *
     * @throws NSDKException
     */
    public void clearScreen() throws NSDKException {

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CLEAR_SCREEN_REQUEST);


        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CLEAR_SCREEN_RESPONSE, null);

        byte[] responseMessageData = responseMessage.getMessageData();

        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, "Failed to clear screen.", innerErrorCode);
        }
    }

    /**
     * Set font size.
     *
     * @param fontSize The font size to set. See {@link FontSize}
     * @throws NSDKException
     */
    public void setFontSize(FontSize fontSize) throws NSDKException {

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.SET_FONT_SIZE_REQUEST);

        byte[] requestMessageData = new byte[1];
        requestMessageData[0] = (byte) fontSize.getCode();
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.SET_FONT_SIZE_RESPONSE, null);

        // Response data = Response Code
        byte[] responseMessageData = responseMessage.getMessageData();

        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
        }
    }

    /**
     * Set display direction.
     *
     * @param alignType Display direction. See {@link AlignType}
     * @throws NSDKException
     */
    public void setDisplayDirection(AlignType alignType) throws NSDKException {

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.SET_DISPLAY_DIRECTION_REQUEST);

        byte[] requestMessageData = new byte[1];
        requestMessageData[0] = (byte) alignType.ordinal();
        requestMessage.setMessageData(requestMessageData);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.SET_DISPLAY_DIRECTION_RESPONSE, null);

        // Response data = Response Code
        byte[] responseMessageData = responseMessage.getMessageData();

        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
        }
    }

    /**
     * Display Chinese message.
     *
     * @param mode               Chinese text display mode.
     *                           <ul>
     *                               <li>0: Using display info defined by external device. See {@link CNTextNotifyType}.</li>
     *                               <li>1: Custom display, using content set by chineseMessageData.</li>
     *                           </ul>
     * @param type               Predefined Chinese message. See {@link CNTextNotifyType}.
     * @param chineseMessageData Custom Chinese message data.
     *                           <ul>
     *                               <li>If custom Chinese message data is null, it will display the Chinese message of the specified type, see {@link CNTextNotifyType}.</li>
     *                               <li>If custom Chinese message data is not null, it will display custom Chinese message instead of the pre-defined Chinese message.</li>
     *                           </ul>
     * @param x                  Display X coordinate.
     * @param y                  Display Y coordinate.
     * @param timeout
     * @return throws NSDKException
     */
    public void displayChinese(byte mode, CNTextNotifyType type, byte[] chineseMessageData, int x, int y, int timeout) throws NSDKException {

        if (mode == 0 && type != null && chineseMessageData == null) {

            ExternalMessage requestMessage = new ExternalMessage();
            requestMessage.setMessageType(ExternalMessageType.DISPLAY_HZ_REQUEST);


            byte[] xBuf = ExternalMessage.intToHexBuf(x);
            byte[] yBuf = ExternalMessage.intToHexBuf(y);
            byte[] timeBuf = ExternalMessage.intToHexBuf(timeout);

            ByteArrayOutputStream messageStream = new ByteArrayOutputStream();
            try {
                messageStream.write(mode);
                messageStream.write((byte) type.ordinal());
                messageStream.write(xBuf);
                messageStream.write(yBuf);
                messageStream.write(timeBuf);
            } catch (IOException e) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
            }

            byte[] chineseBuf = messageStream.toByteArray();

            requestMessage.setMessageData(chineseBuf);

            ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.DISPLAY_HZ_RESPONSE, null);

            byte[] responseMessageData = responseMessage.getMessageData();

            if (responseMessageData == null || responseMessageData.length < 2) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
            }

            int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));

            if (responseCode != ErrorCode.OK) {
                int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }
        } else if (mode == 1 && type == null && chineseMessageData != null) {
            ExternalMessage requestMessage = new ExternalMessage();
            requestMessage.setMessageType(ExternalMessageType.DISPLAY_HZ_REQUEST);

            int len = chineseMessageData.length;
            byte[] xBuf = ExternalMessage.intToHexBuf(x);
            byte[] yBuf = ExternalMessage.intToHexBuf(y);
            byte[] timeBuf = ExternalMessage.intToHexBuf(timeout);
            byte[] lenBuf = ExternalMessage.intToHexBuf(len);

            ByteArrayOutputStream messageStream = new ByteArrayOutputStream();
            try {
                messageStream.write(mode);
                messageStream.write(xBuf);
                messageStream.write(yBuf);
                messageStream.write(timeBuf);
                messageStream.write(lenBuf);
                messageStream.write(chineseMessageData);
            } catch (IOException e) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
            }

            byte[] chineseBuf = messageStream.toByteArray();

            requestMessage.setMessageData(chineseBuf);

            ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.DISPLAY_HZ_RESPONSE, null);

            byte[] responseMessageData = responseMessage.getMessageData();

            if (responseMessageData == null || responseMessageData.length < 2) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
            }

            int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
            if (responseCode != ErrorCode.OK) {
                int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
                throw new NSDKExternalDeviceException(ErrorCode.EXT_DISPLAY_LATTICE_DATA_ERROR, ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }
        } else {
            throw new NSDKIllegalParameterException("Invalid mode,type or Chinese message data.");
        }

    }

    /**
     * Prompt and get selected key code.
     *
     * <p>Note: This interface can be interrupted by calling any other interface from the host device to the external device.</p>
     *
     * <p>Example:</p>
     * <pre>
     *     // Only display messages on line 1 and line 2.
     *     String[] messages = new String[]{"Message for line 1", null, "Message for line 3", ""};
     *
     *     try {
     *         KeyCode keyCode = externalCommon.optionSelect(messages);
     *     } catch (NSDKException e) {
     *         if (e instanceof NSDKTimeoutException) {
     *             // Do something when timeout
     *         } else if (e instanceof NSDKCancelledException) {
     *             // Do something when cancelled
     *         } else {
     *             // Handle other types of exception
     *         }
     *     }
     *
     * </pre>
     *
     * @param messages The messages that will display on the screen of the external device.
     *                 <ul>
     *                 <li>Each string of the "messages" parameter will be displayed in one line. 4 lines supported.</li>
     *                 <li>Messages shall be encoded in UTF8 format to be able to support multiple languages.</li>
     *                 <li>If the string is null or empty, means it is an empty line. </li>
     *                 </ul>
     * @return Selected key code, see {@link KeyCode}.
     * @throws NSDKException
     * @deprecated 这个指令跟 display text 指令的区别在于：这个指令是阻塞的，要等用户按键以后才会返回。目前只有特殊场景才会用到这个指令，暂时不需要对外提供这个指令。
     */
    public KeyCode optionSelect(String[] messages) throws NSDKException {

        if (messages == null || messages.length > 4) {
            throw new NSDKIllegalParameterException("Invalid messages.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.OPTION_SELECT_REQUEST);

        ByteArrayOutputStream messageStream = new ByteArrayOutputStream();
        for (int i = 0; i < messages.length; i++) {
            try {
                if (messages[i] != null && !messages[i].isEmpty()) {
                    messageStream.write(messages[i].getBytes());
                }
                messageStream.write(0x1c);
            } catch (IOException e) {
                throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
            }
        }

        byte[] messageBuf;
        messageBuf = messageStream.toByteArray();

        requestMessage.setMessageData(messageBuf);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveAsync(requestMessage, ExternalMessageType.OPTION_SELECT_RESPONSE, null);

        byte[] responseMessageData = responseMessage.getMessageData();

        if (responseMessageData == null) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }
        if (responseMessageData.length < 1) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_DATA_LENGTH_NOT_ENOUGH, ExternalErrorMessage.DATA_LENGTH_NOT_ENOUGH);
        }

        KeyCode keyCode;
        switch (responseMessageData[0]) {
            case 0x30:
                keyCode = KeyCode.ENTER;
                break;
            case 0x31:
                keyCode = KeyCode.F1;
                break;
            case 0x32:
                keyCode = KeyCode.F2;
                break;
            case 0x33:
                keyCode = KeyCode.F3;
                break;
            case 0x35:
                keyCode = KeyCode.CLEAR;
                break;
            case 0x36:
                keyCode = KeyCode.CANCEL;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + responseMessageData[0]);
        }
        return keyCode;
    }

    /**
     * Display picture.
     *
     * @param x                    Display X coordinate(Pixel).
     * @param y                    Display Y coordinate(Pixel).
     * @param width                Picture width(Pixel)
     * @param height               Picture height (Pixel)
     * @param currentPackageNumber The number of current package.
     * @param nextPackageNumber    The number of next package.
     * @param data                 Picture data. It is fixed to 256 bytes in each package except for the last package.
     * @throws NSDKException
     * @deprecated
     */
    public void displayPicture(int x, int y, int width, int height, int currentPackageNumber, int nextPackageNumber, byte[] data) throws NSDKException {

        if (data == null || data.length > 256) {
            throw new NSDKIllegalParameterException("Invalid data.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.DISPLAY_PICTURE_REQUEST);

        byte[] xBuf = ExternalMessage.intToHexBuf(x);
        byte[] yBuf = ExternalMessage.intToHexBuf(y);
        byte[] widthBuf = ExternalMessage.intToHexBuf(width);
        byte[] heightBuf = ExternalMessage.intToHexBuf(height);
        byte[] currentBuf = ExternalMessage.intToHexBuf(currentPackageNumber);
        byte[] nextBuf = ExternalMessage.intToHexBuf(nextPackageNumber);

        ByteArrayOutputStream messageStream = new ByteArrayOutputStream();
        try {
            messageStream.write(xBuf);
            messageStream.write(yBuf);
            messageStream.write(widthBuf);
            messageStream.write(heightBuf);
            messageStream.write(currentBuf);
            messageStream.write(nextBuf);
            messageStream.write(data);

        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        byte[] imageBuf = messageStream.toByteArray();


        requestMessage.setMessageData(imageBuf);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.DISPLAY_PICTURE_RESPONSE, null);

        byte[] responseMessageData = responseMessage.getMessageData();

        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        int responseCode;
        try {
            responseCode = Integer.parseInt(new String(Arrays.copyOf(responseMessageData, 2)));
        } catch (Exception e) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (responseMessageData[0] == 0 && responseMessageData[1] == 0) {
                return;
            } else if (responseMessageData[0] == 0 && responseMessageData[1] == 1) {
                throw new NSDKIllegalParameterException(ErrorCode.PARAM_ERROR, ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            } else {
                throw new NSDKExternalDeviceException(String.format("Unknown error code: %s", ISOUtils.hexString(responseMessageData)), innerErrorCode);
            }
        }
        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
        }
    }

    /**
     * Display picture.
     *
     * @param mode                 Mode:
     *                             <ul>
     *                             <li>0 - only display</li>
     *                             <li>1 - display and when width = 320&height=240 set it as background image</li>
     *                             <li>2 - only load image to sp100</li>
     *                             <li>3 - display the image that mode 2 load</li>
     *                             </ul>
     * @param x                    Display X coordinate(Pixel).
     * @param y                    Display Y coordinate(Pixel).
     * @param width                Picture width(Pixel)
     * @param height               Picture height (Pixel)
     * @param imageId              Image id, [0, 1]
     * @param currentPackageNumber The number of current package.
     * @param nextPackageNumber    The number of next package.
     * @param data                 Picture data. It is fixed to 256 bytes in each package except for the last package.
     * @param timeout              Timeout. Unit: ms
     * @throws NSDKException
     */
    public void displayColorImage(byte mode, int x, int y, int width, int height, byte imageId, int currentPackageNumber, int nextPackageNumber, byte[] data, int timeout) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.DISPLAY_COLOR_IMAGE_REQUEST);

        ByteArrayOutputStream messageStream = new ByteArrayOutputStream();
        try {
            messageStream.write(mode);
            if (mode != 2) {
                byte[] xBuf = ExternalMessage.intToHexBuf(x);
                byte[] yBuf = ExternalMessage.intToHexBuf(y);
                byte[] widthBuf = ExternalMessage.intToHexBuf(width);
                byte[] heightBuf = ExternalMessage.intToHexBuf(height);
                messageStream.write(xBuf);
                messageStream.write(yBuf);
                messageStream.write(widthBuf);
                messageStream.write(heightBuf);
            }

            if (mode == 2 || mode == 3) {
                messageStream.write(imageId);
            }

            if (mode != 3) {
                byte[] currentBuf = ExternalMessage.intToHexBuf(currentPackageNumber);
                byte[] nextBuf = ExternalMessage.intToHexBuf(nextPackageNumber);
                messageStream.write(currentBuf);
                messageStream.write(nextBuf);
                if (data == null || data.length > MAX_IMAGE_DATA_LEN) {
                    throw new NSDKIllegalParameterException("Invalid data.");
                }
                messageStream.write(data);
            }

            if (mode == 3) {
                byte[] timeoutBuf = ExternalMessage.intToHexBuf(timeout);
                messageStream.write(timeoutBuf);
            }
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        requestMessage.setMessageData(messageStream.toByteArray());

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.DISPLAY_COLOR_IMAGE_RESPONSE, null);

        byte[] responseMessageData = responseMessage.getMessageData();

        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_DATA, ExternalErrorMessage.NO_RESPONSE_DATA);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (responseCode == 1) {
                throw new NSDKIllegalParameterException(ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }
            if (responseCode == 2) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_DISPLAY_BEYOND_SCREEN_RANGE, "Beyond screen range.", innerErrorCode);
            }
            if (responseCode == 3) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_DISPLAY_FILE_OPERATE_ERROR, "File operation failed.", innerErrorCode);
            }
            if (responseCode == 4) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, ExternalErrorMessage.GENERAL_ERROR, innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }

    /**
     * Returns to main menu.
     *
     * @throws NSDKException
     */
    public void returnMainMenu() throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.RETURN_MAIN_MENU_REQUEST);

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.RETURN_MAIN_MENU_RESPONSE, null);

        // Response message data = Response code(2 bytes)
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        responseMessage.setResponseCode(responseCode);
        if (ErrorCode.OK != responseCode) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }

    public void setReturnToHome(byte config) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.CONTROL_OF_PAGE_JUMP_REQUEST);
        requestMessage.setMessageData(new byte[] {config});

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.CONTROL_OF_PAGE_JUMP_RESPONSE, null);
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new NSDKException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKException(ErrorCode.PARAM_ERROR, ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }
            if (45 == responseCode) {
                throw new NSDKException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Command Length Error.", innerErrorCode);
            }
        }
    }

    public void setUIMode(byte mode) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.SET_UI_MODE_REQUEST);
        byte[] requestMessageData = new byte[4];
        requestMessageData[0] = mode;
        requestMessage.setMessageData(requestMessageData);
        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.SET_UI_MODE_RESPONSE, null);
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new NSDKException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKException(ErrorCode.PARAM_ERROR, ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }
            if (2 == responseCode) {
                throw new NSDKException(ErrorCode.EXT_DISPLAY_FILE_OPERATE_ERROR, "File operate failed.", innerErrorCode);
            }
            if (45 == responseCode) {
                throw new NSDKException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Command Length Error.", innerErrorCode);
            }
        }
    }

    public void displayVersion(byte isDisplay) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.VERSION_DISPLAY_REQUEST);
        requestMessage.setMessageData(new byte[] {isDisplay});

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.VERSION_DISPLAY_RESPONSE, null);
        byte[] responseMessageData = responseMessage.getMessageData();
        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new NSDKException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }
        int responseCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKException(ErrorCode.PARAM_ERROR, ExternalErrorMessage.INVALID_PARAMETER, innerErrorCode);
            }
            if (45 == responseCode) {
                throw new NSDKException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Bad Command Length.", innerErrorCode);
            }
        }
    }

    /**
     * Display QR code.
     *
     * @throws NSDKException
     */
    public void displayQRCode(byte[] imageData, DiaplayQRImageParameters parameter) throws NSDKException {
        if (imageData == null || imageData.length == 0 || imageData.length > 512) {
            throw new NSDKIllegalParameterException("QR data length shall be >0 and <=512.");
        }

        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.DISPLAY_QR_CODE_REQUEST);

        ByteArrayOutputStream messageStream = new ByteArrayOutputStream();
        messageStream.write(parameter.getLevel().ordinal());
        messageStream.write(parameter.getMask().ordinal());
        messageStream.write(parameter.getVersion());

        if (parameter.isAutoCenter()) {
            messageStream.write(1);
        } else {
            messageStream.write(0);
            messageStream.write(parameter.getXCoordinate());
            messageStream.write(parameter.getYCoordinate());
        }
        messageStream.write(parameter.getPosition().ordinal());

        try {
            int tLen = 0;
            if (parameter.getTextData() != null) {
                tLen = parameter.getTextData().length;
                if (tLen > 256) {
                    throw new NSDKIllegalParameterException("Text length shall be <=256.");
                }
            }
            messageStream.write(tLen + 1);
            if (tLen > 1) {
                messageStream.write(parameter.getTextData());
            }


            messageStream.write(ExternalMessage.intToHexBuf(imageData.length));
            messageStream.write(imageData);
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        requestMessage.setMessageData(messageStream.toByteArray());

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.DISPLAY_QR_CODE_RESPONSE, null);

        // Response message data = Response code
        byte[] responseMessageData = responseMessage.getMessageData();

        if (responseMessageData == null || responseMessageData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }

        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseMessageData, 2));
        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseMessageData, 2);
            if (1 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_DISPLAY_QR_CODE_ERROR_CORRECT_LEVEL, "QR code correct level error.", innerErrorCode);
            }
            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_DISPLAY_QR_CODE_MASK_NUMBER_ERROR, "QR code mask number error.", innerErrorCode);
            }
            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_DISPLAY_QR_CODE_TYPE_ERROR, "QR code type error.", innerErrorCode);
            }
            if (4 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_DISPLAY_QR_CODE_AUTO_CENTER_ERROR, "QR code auto center error.", innerErrorCode);
            }
            if (5 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_DISPLAY_QR_CODE_INITIAL_ABSCISSA_ERROR, "QR code initial abscissa error.", innerErrorCode);
            }
            if (6 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_DISPLAY_QR_CODE_INITIAL_ORDINATE_ERROR, "QR code initial ordinate error.", innerErrorCode);
            }
            if (7 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_DISPLAY_QR_CODE_TEXT_POSITION_ERROR, "QR code text position error.", innerErrorCode);
            }
            if (8 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_DISPLAY_QR_CODE_TEXT_LENGTH_ERROR, "QR code text length error.", innerErrorCode);
            }
            if (9 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_DISPLAY_QR_CODE_LENGTH_ERROR, "QR code length error.", innerErrorCode);
            }
            if (10 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_DISPLAY_QR_CODE_WIDTH_ERROR, "Width of QR code image is out of screen range.", innerErrorCode);
            }
            if (11 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_DISPLAY_QR_CODE_HEIGHT_ERROR, "Height of QR code image is out of screen range.", innerErrorCode);
            }
            if (12 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_DISPLAY_TEXT_HEIGHT_ERROR, "Height of text is out of screen range.", innerErrorCode);
            }

            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }

    public void displayMenu(int timeout, String title, String[] menus, SelectionCallback callback) throws NSDKException{
        if (title.length() > 32) {
            throw new NSDKIllegalParameterException("Title length shall not be more than 32.");
        }
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.DISPLAY_MENU_OPTION_REQUEST);
        try (ByteArrayOutputStream messageStream = new ByteArrayOutputStream()) {
            byte[] titleData = title.getBytes();
            messageStream.write(titleData.length);
            messageStream.write(titleData);
            messageStream.write(timeout);
            messageStream.write(menus.length);
            for (String menu : menus) {
                if (TextUtils.isEmpty(menu)) {
                    messageStream.write(0x00);
                    continue;
                }
                messageStream.write(menu.getBytes());
                messageStream.write(0x00);
            }
            requestMessage.setMessageData(messageStream.toByteArray());
        } catch (IOException e) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }
        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceive(requestMessage, ExternalMessageType.DISPLAY_MENU_OPTION_RESPONSE, null, timeout * 1000);
        byte[] responseData = responseMessage.getMessageData();
        if (responseData == null || responseData.length < 2) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseData, 2));
        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseData, 2);
            if (1 == responseCode) {
                callback.onError(ErrorCode.PARAM_ERROR, ExternalErrorMessage.INVALID_PARAMETER);
                return;
            }
            if (2 == responseCode || 55 == responseCode) {
                callback.onError(ErrorCode.EXT_UNSUPPORTED, ExternalErrorMessage.NOT_SUPPORTED);
                return;
            }
            if (3 == responseCode) {
                callback.onTimeout();
                return;
            }
            if (5 == responseCode) {
                callback.onCancel();
                return;
            }
            if (45 == responseCode) {
                callback.onError(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Command length error");
                return;
            }
            callback.onError(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR);
            return;
        }
        int selectedOption = Integer.parseInt(String.valueOf(responseData[2]));
        callback.onSelected(selectedOption);
    }

    public void displayButtons(TitleParameters titleParameters, ButtonParameters[] buttonParameters, int timeout, boolean isReturnHome, SelectionCallback callback) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.GET_BUTTON_OPTION_REQUEST);
        try (ByteArrayOutputStream messageStream = new ByteArrayOutputStream()) {
            ByteArrayOutputStream tlvDataStream = new ByteArrayOutputStream();
            int buttonNumber = buttonParameters.length;
            for (int i = 0; i < buttonNumber; i++) {
                if (buttonParameters[i] == null) {
                    throw new NSDKIllegalParameterException("ButtonParameters shall not be null.");
                }
                tlvDataStream.write(ISOUtils.hex2byte("DF"));
                tlvDataStream.write(i + 1);
                tlvDataStream.write(1);
                ButtonCode buttonCode = buttonParameters[i].getButtonCode();
                if (buttonCode != null) {
                    tlvDataStream.write(buttonCode.getCode());
                } else {
                    throw new NSDKIllegalParameterException("ButtonCode shall not be null when buttonParameters is not null.");
                }
            }
            tlvDataStream.write(ISOUtils.hex2byte("DF8101"));
            tlvDataStream.write(2);
            tlvDataStream.write(ExternalMessage.intToHexBuf(timeout));
            messageStream.write(ExternalMessage.intToHexBuf(tlvDataStream.size()));
            messageStream.write(tlvDataStream.toByteArray());
            if (isReturnHome) {
                messageStream.write(0x01);
            } else {
                messageStream.write(0x00);
            }
            String title = titleParameters.getTitleText();
            if (TextUtils.isEmpty(title)) {
                messageStream.write(0);
            } else {
                byte[] titleData = title.getBytes();
                messageStream.write(titleData.length);
                messageStream.write(titleData);
            }
            messageStream.write(titleParameters.getX());
            messageStream.write(titleParameters.getY());
            String text = titleParameters.getText();
            if (TextUtils.isEmpty(text)) {
                messageStream.write(0x00);
            } else {
                byte[] textData = text.getBytes();
                messageStream.write(textData.length);
                messageStream.write(textData);
            }
            messageStream.write(buttonNumber);
            for (ButtonParameters buttonParameter : buttonParameters) {
                messageStream.write(buttonParameter.getId());
                messageStream.write(ExternalMessage.intToHexBuf(buttonParameter.getX()));
                messageStream.write(ExternalMessage.intToHexBuf(buttonParameter.getY()));
                messageStream.write(ExternalMessage.intToHexBuf(buttonParameter.getWidth()));
                messageStream.write(ExternalMessage.intToHexBuf(buttonParameter.getHeight()));
            }
            requestMessage.setMessageData(messageStream.toByteArray());
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceive(requestMessage, ExternalMessageType.GET_BUTTON_OPTION_RESPONSE, null, timeout * 1000);
        byte[] responseData = responseMessage.getMessageData();
        if (responseData == null || responseData.length < 2) {
            callback.onError(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
            return;
        }
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseData, 2));
        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseData, 2);
            if (1 == responseCode) {
                callback.onError(ErrorCode.PARAM_ERROR, ExternalErrorMessage.INVALID_PARAMETER);
                return;
            } else if (4 == responseCode) {
                callback.onError(ErrorCode.EXT_ERROR, "Failed to display buttons.");
                return;
            } else if (5 == responseCode) {
                callback.onCancel();
                return;
            } else if (6 == responseCode) {
                callback.onTimeout();
                return;
            } else if (45 == responseCode) {
                callback.onError(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Command Length Error.");
                return;
            }
            callback.onError(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR);
            return;
        }
        if (responseData.length < 3) {
            callback.onError(ErrorCode.EXT_ERROR, ExternalErrorMessage.NO_RESPONSE_CODE);
            return;
        }
        callback.onSelected(responseData[2]);
    }

    public void displayView(byte config, String[] messages, PictureParameters[] pictures) throws NSDKException {
        ExternalMessage requestMessage = new ExternalMessage();
        requestMessage.setMessageType(ExternalMessageType.DISPLAY_VIEW_REQUEST);

        try (ByteArrayOutputStream messageStream = new ByteArrayOutputStream()) {
            messageStream.write(config);
            int textLen = 0;
            if (messages != null && messages.length != 0) {
                for (String message : messages) {
                    if (!TextUtils.isEmpty(message)) {
                        textLen += message.getBytes().length;
                    }
                }
                textLen += messages.length - 1;
                messageStream.write(ExternalMessage.intToHexBuf(textLen));
                for (int i = 0; i < messages.length - 1; i++) {
                    if (!TextUtils.isEmpty(messages[i])) {
                        messageStream.write(messages[i].getBytes());
                    }
                    messageStream.write(0x1C);
                }
                messageStream.write(messages[messages.length - 1].getBytes());
            } else {
                messageStream.write(ExternalMessage.intToHexBuf(0));
            }

            if (pictures != null && pictures.length != 0) {
                int tlvDataLen = pictures.length * 10 + 4;
                messageStream.write(ExternalMessage.intToHexBuf(tlvDataLen));
                messageStream.write(ISOUtils.hex2byte("DF01"));
                messageStream.write(pictures.length * 10 + 1);
                messageStream.write(pictures.length);
                for (PictureParameters picture : pictures) {
                    messageStream.write(picture.getId());
                    messageStream.write(picture.getPictureType().ordinal());
                    messageStream.write(ExternalMessage.intToHexBuf(picture.getWidth()));
                    messageStream.write(ExternalMessage.intToHexBuf(picture.getHeight()));
                    messageStream.write(ExternalMessage.intToHexBuf(picture.getX()));
                    messageStream.write(ExternalMessage.intToHexBuf(picture.getY()));
                }
            } else {
                messageStream.write(ExternalMessage.intToHexBuf(0));
            }
            requestMessage.setMessageData(messageStream.toByteArray());
        } catch (IOException e) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        ExternalMessage responseMessage = ExternalCommunicationManager.getInstance().sendAndReceiveSync(requestMessage, ExternalMessageType.DISPLAY_VIEW_RESPONSE, null);
        byte[] responseData = responseMessage.getMessageData();

        if (responseData == null || responseData.length < 2) {
            throw new NSDKExternalDeviceException(ErrorCode.EXT_MESSAGE_NO_RESPONSE_CODE, ExternalErrorMessage.NO_RESPONSE_CODE);
        }
        int responseCode = ExternalMessage.convertResponseCodeBufToInt(Arrays.copyOf(responseData, 2));
        if (responseCode != ErrorCode.OK) {
            int innerErrorCode = ExternalMessage.getInnerErrorCode(responseData, 2);
            if (1 == responseCode) {
                throw new NSDKIllegalParameterException();
            }
            if (2 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, "The picture will display out of the screen.", innerErrorCode);
            }
            if (3 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_ERROR, "Failed to open the bmp file", innerErrorCode);
            }
            if (45 == responseCode) {
                throw new NSDKExternalDeviceException(ErrorCode.EXT_COMMAND_LENGTH_ERROR, "Command Length Error.", innerErrorCode);
            }
            throw new NSDKExternalDeviceException(ErrorCode.EXT_UNKNOWN_ERROR, ExternalErrorMessage.UNKNOWN_ERROR, innerErrorCode);
        }
    }



}

package com.newland.sdk.me.module.externalKeyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.newland.NLUART3Manager;
import android.newland.content.NlContext;

import com.newland.sdk.me.module.serialport.MESerial;
import com.newland.sdk.module.serialport.Baudrate;
import com.newland.sdk.module.serialport.PortType;
import com.newland.sdk.module.serialport.SerialPortModule;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.utils.ISOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyBoardHelper {
    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("KeyBoardHelper");
    private int mFrameNoFlag = 0x01; // 帧序号
    private final byte STX = 0x03; // 起始标志
    private final byte ETX = 0x03; // 结束标志
    private final byte CMD_INDICATOR_BIT = 0x2F; // 指示位
    private int changeLen;//可变数据长度

    private final byte[] CMD_SHOW_CLEAR = {(byte) 0xA1,0x01}; //清屏
    private final byte[] CMD_SHOW_BIT_DATA = {0x00 ,0x0C ,0x00 ,0x0C};//汉字的宽高
    private final byte[] CMD_SHOW_BIT_NUMBER = {0x00 ,0x10 ,0x00 ,0x15};//数字的宽高
    private final byte[] CMD_SHOW_BIT_LL = {0x00 ,0x20};
    private final byte[] CMD_SHOW_BIT = {(byte) 0xA1,0x07}; //显示位图
    private final byte[] CMD_KEY_VLAUE = {(byte) 0xB1,0x02}; //获取按键值
    private final byte[] CMD_KEY_TIME_OUT = {0x00,0x00,0x02, (byte) 0x58}; //规定的时间里返回按键值,超时时间600ms.
    private final byte[] CMD_KEY_VLAUE1 = {(byte) 0xB1,0x01}; //获取按键值,立即返回

    private final byte[] CMD_KEY_TONE = {(byte) 0xB1,0x21};
    public final int ACK_OK = 0;
    public final int ACK_RWERR = -1;
    public final int ACK_CHECKERR = -2;
    public final int ACK_PAUSE = -3;
//    private NLUART3Manager uart3Manager;
    private SerialPortModule serialOper;
    private static Object KeyBoarSync = new Object();
    private boolean isOpen = false;
    @SuppressLint("WrongConstant")
    public KeyBoardHelper(AbstractDevice device, Context context){
        serialOper = new MESerial(device,context);
    }

    public boolean isEnabled(){
        synchronized (KeyBoarSync){
            try {
                if(!openUART3()){
                    return false;
                }
                byte[] tempBuf = byteMerge(intToBytes(0),new byte[]{0x00,0x1F});
                int ackCode = dealFrame(CMD_SHOW_CLEAR,tempBuf,null,1);
                devicelogger.debug("[isEnabled] dealFrame ackCode-20181106="+ackCode);
                if(ackCode == ACK_OK){
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                closeUART3();
            }
            return false;
        }
    }
    public boolean showChinese(String str,final InputStream is){
        synchronized (KeyBoarSync){
            try {
                devicelogger.debug("[ShowChinese] dealFrame start.");
                openUART3();
                byte[] tempBuf = byteMerge(intToBytes(0),new byte[]{0x00,0x1F});
                dealFrame(CMD_SHOW_CLEAR,tempBuf,null,0);

                int ackCode = ACK_CHECKERR;
                int sumLen = 86,offset = 0,size = 24,offsetHz = 0;
                int len = str.length();

                byte[] lenData,bitStartX,bitStartY = new byte[]{0x00,0x0B},buffer = null;
                if (!isChinese(str)){
                    return false;
                }
                sumLen =(128 - (128-len*12)/2);
                int startX = sumLen - len*12;
                for (int i = 0; i < str.length(); i++) {
                    byte[] hz = str.substring(i,i+1).getBytes("GBK");
                    buffer = new byte[size];
                    offsetHz = ((hz[0] - (byte)0x81)*190 + (byteArrayToInt(hz[1]) - (byte)0x41))*size;
                    is.reset();
                    is.skip(offsetHz);
                    is.read(buffer);
                    bitStartX = intToBytes(startX + offset);
                    lenData = byteMerge(bitStartX,bitStartY);
                    lenData = byteMerge(lenData,CMD_SHOW_BIT_DATA);
                    lenData = byteMerge(lenData,CMD_SHOW_BIT_LL);
                    lenData = byteMerge(lenData, buffer);
                    offset+= 12;
                    ackCode = dealFrame(CMD_SHOW_BIT,lenData,null,0);
                }
                devicelogger.debug("[showChinese] dealFrame ackCode...="+ackCode);
                if(ackCode == ACK_OK){
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if (is != null){
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
        }
    }
    public int getKeyValue(byte[] keyCode){
        MEExternalKeyboard.isPauseGetKeyCode = false;
        synchronized (KeyBoarSync){
            try {
                openUART3();
//                int ackCode = dealFrame(CMD_KEY_VLAUE,CMD_KEY_TIME_OUT,keyCode,2);
                int ackCode = dealFrame(CMD_KEY_VLAUE1,null,keyCode,2);
                //devicelogger.debug(">>>getKeyValue dealFrame ackCode="+ackCode);
                return ackCode;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ACK_CHECKERR;
        }
    }

    public void showNumber(String sNum){
        synchronized (KeyBoarSync){
            try {
                devicelogger.debug("[showNumber] dealFrame start.");
                openUART3();
                byte[] tempBuf = byteMerge(intToBytes(0),new byte[]{0x00,0x1F});
                dealFrame(CMD_SHOW_CLEAR,tempBuf,null,0);

                int offset = 0,sumLen = 128-14,i,startX;
                int len = sNum.length();
                byte[] bitStartY = new byte[]{0x00,0x0B};
                byte[] lenData,bitStartX;
                boolean bool = false;
                if (sNum.contains(".")){
                    startX = sumLen - (len - 1)*14 - 5;
                }else {
                    startX = sumLen - len*14;
                }
                for(i=0;i<len;i++) {
                    bitStartX = intToBytes(startX + offset);
                    lenData = byteMerge(bitStartX,bitStartY);
                    lenData = byteMerge(lenData, CMD_SHOW_BIT_NUMBER);
                    String sTemp = sNum.substring(i,i+1);
                    if (".".equals(sTemp)){
                        lenData = byteMerge(lenData,CMD_SHOW_BIT_LL);
                        lenData = byteMerge(lenData,szDot);
                        offset+= 5;
                    }else {
                        lenData = byteMerge(lenData,CMD_SHOW_BIT_LL);
                        lenData = byteMerge(lenData,szBigNumber[Integer.valueOf(sTemp)]);
                        offset+= 14 ;
                    }
                    int ackCode = dealFrame(CMD_SHOW_BIT,lenData,null,0);
                    devicelogger.debug("[showNumber]ShowBit dealFrame ackCode="+ackCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void clearScreen(){
        synchronized (KeyBoarSync){
            openUART3();
            byte[] tempBuf = byteMerge(intToBytes(0),new byte[]{0x00,0x1F});
            int ackCode = dealFrame(CMD_SHOW_CLEAR,tempBuf,null,0);
            devicelogger.debug("[ClearScreen] dealFrame ackCode="+ackCode);
        }
    }

    public void startKeyBoardInput(){
        devicelogger.debug("[startKeyBoardInput]");
        synchronized(KeyBoarSync){
            openUART3();
            clearScreen();
        }
    }
    public void stopKeyBoardInput(){
        devicelogger.debug("[stopKeyBoardInput]");
        synchronized (KeyBoarSync){
            clearScreen();
            closeUART3();
        }
    }

    private int dealFrame(byte[] frameType,byte[] arrayInput,byte[] arrayReturn,int mode){
        try {
            clearKeyBoard();
            int ackCode = sendData(frameType,arrayInput);
            if(ackCode != ACK_OK){
                devicelogger.debug("[dealFrame] sendData Error ackCode="+ackCode);
                return ackCode;
            }
            if(mode == 1 || mode == 2) {
                ackCode = getAckData(mode, arrayReturn);
                if (ackCode != ACK_OK) {
                    devicelogger.debug("[dealFrame] getAckData Error ackCode=" + ackCode);
                    return ackCode;
                }
            }
            return ACK_OK;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ACK_RWERR;
    }

    private int sendData(byte[] frameType,byte[] arrayInput) {
        try {
            devicelogger.debug("[sendData] frameType:"+(frameType==null?null:ISOUtils.hexString(frameType))+"; arrayInput:"+(arrayInput==null?null:ISOUtils.hexString(arrayInput)));
            ByteBuffer buffer;
            int length;
            if (arrayInput == null){
                buffer = ByteBuffer.allocate(9);
                length = 4;
            }else {
                buffer = ByteBuffer.allocate(arrayInput.length + 9);
                length = 4 + arrayInput.length;
            }
            buffer.put(STX);
            byte[] len = DecToBCDArray(length,2);
            buffer.put(len);
            buffer.put(frameType);
            buffer.put(CMD_INDICATOR_BIT);
            buffer.put((byte) mFrameNoFlag);
            if (arrayInput != null){
                buffer.put(arrayInput);
            }
            buffer.put(ETX);
            byte[] lrc = new byte[1];
            int actualLrc = 0;
            byte[] temp = new byte[buffer.array().length - 2];
            System.arraycopy(buffer.array(),1,temp,0,buffer.array().length - 2);
            for (int i = 0; i < temp.length; i++) {
                actualLrc ^= temp[i];
            }
            lrc[0] = (byte) (actualLrc & 0xff);
            buffer.put(lrc);
            byte[] data = toEscape(buffer.array());
            //devicelogger.debug(">>>sendData HexDump:"+this.getHexDump(data));
            int ret = 0;//uart3Manager.ioctl(0x540B, new byte[] { 0x02 });
            if(ret != 0){
                devicelogger.debug("[sendData]clear buffer error ret="+ret);
            }
            ret = serialOper.write(data,data.length,0);
            //devicelogger.debug(">>>sendData write ret="+ret);
            if(ret <= 0){
                devicelogger.debug("[sendData] write error. ret="+ret);
                return ACK_RWERR;
            }
            return ACK_OK;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ACK_RWERR;
    }

    private int getAckData(int mode,byte[] arrayReturn){
        byte[] buffer = new byte[256];
        int ret = -1,count = 0,readCount = 0;
        if(mode == 2){//getKeyCode
            do{
                if(MEExternalKeyboard.isPauseGetKeyCode){
                    devicelogger.debug("[getAckData]PauseGetKeyCode......ACK_PAUSE.");
                    return ACK_PAUSE;
                }
                ret = serialOper.read(buffer, buffer.length, 0);
                try {
                    if(ret <= 0){
                        readCount++;
                        Thread.sleep(20);
                    }else{
                        readCount = 0;
                    }
                    //devicelogger.debug(">>>getAckData1 readCount="+readCount+" ret="+ret);
                    if(readCount > 38){
                        devicelogger.debug("[getAckData] deal exception readCount="+readCount+" ret="+ret);
                        readCount = 0;
                        closeUART3();
                        Thread.sleep(50);
                        openUART3();
                        clearKeyBoard();
                        int ackCode = sendData(CMD_KEY_VLAUE,CMD_KEY_TIME_OUT);
                        devicelogger.debug("[getAckData] deal exception sendData ackCode="+ackCode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //devicelogger.debug(">>>getAckData read ret1="+ret);
                if(!MEExternalKeyboard.getKeyboardRunning()){
                    devicelogger.debug("[getAckData] KeyboardRuning==false ACK_OK.");
                    return ACK_OK;
                }
            }while (ret <= 0);
        }else if(mode == 0){//other
            do{
                ret = serialOper.read(buffer, buffer.length, 0);
                try {
                    if(ret <= 0){
                        readCount++;
                        Thread.sleep(20);
                    }else{
                        readCount = 0;
                    }
                    //devicelogger.debug(">>>getAckData1 readCount="+readCount+" ret="+ret);
                    if(readCount > 38){
                        devicelogger.debug("[getAckData] deal exception readCount="+readCount+" ret="+ret);
                        readCount = 0;
                        closeUART3();
                        Thread.sleep(50);
                        openUART3();
                        clearKeyBoard();
                        int ackCode = sendData(CMD_KEY_VLAUE,CMD_KEY_TIME_OUT);
                        devicelogger.debug("[getAckData] deal exception sendData ackCode="+ackCode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //devicelogger.debug(">>>getAckData read ret1="+ret);
                if(!MEExternalKeyboard.getKeyboardRunning()){
                    devicelogger.debug("[getAckData] KeyboardRuning==false ACK_OK.");
                    return ACK_OK;
                }
            }while (ret <= 0);
        }else if(mode == 1){//isEnabled
            do{
                count++;
                if(count > 50){
                    devicelogger.debug("[getAckData] count="+count);
                    return ACK_RWERR;
                }
                ret = serialOper.read(buffer, buffer.length, 0);
                try {
                    if(ret <= 0)
                        Thread.sleep(20);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                devicelogger.debug(">>>getAckData read ret2="+ret+" count="+count);
            }while (ret <= 0);
        }else {
            devicelogger.debug("[getAckData] timeoutSec Error.");
            return ACK_RWERR;
        }
        if(ret <= 0){
            devicelogger.debug("[getAckData] read error ret="+ret);
            return ACK_RWERR;
        }
        byte[] data = new byte[ret];
        System.arraycopy(buffer,0,data,0,ret);
        data = fromEscape(data);
        //devicelogger.debug(">>>getAckData HexDump:"+this.getHexDump(data)+" len="+ret);
        ret = checkData(data);
        if (ret != 0){
            devicelogger.debug("[getAckData] checkData error ret="+ret);
            return ACK_CHECKERR;
        }
        if (ret == 0 && changeLen > 0 && arrayReturn!=null){
            System.arraycopy(data, 9, arrayReturn, 0, 1);
        }
        addFrameNo();
        return ACK_OK;
    }

    private int checkData(byte[] data){
        try {
            if(data[0] != (byte)0x03){
                devicelogger.debug("[checkData] Error.");
                return 5;
            }
            int length = ((data[1] & 0xff) * 256) + (data[2] & 0xff);
            changeLen = length - 6;
            byte[] lrc = byteCut(data,1, data.length -2);
            byte[] code = byteCut(data,7, 2);
            return Integer.valueOf(new String(code));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 6;
    }
    private boolean openUART3(){
        if(isOpen){
            //devicelogger.debug(">>>uart3 already open!!!");
            return true;
        }
        int ret = serialOper.open(PortType.RS232, Baudrate.BPS115200,null);
        devicelogger.info("[openUART3]uart3 open ret="+ret);
        if(ret < 0){
            devicelogger.error("[openUART3]uart3 open failed ret="+ret);
            return false;
        }
        isOpen = true;
        return true;
    }
    private boolean closeUART3(){
        isOpen = false;
        int ret = serialOper.close();
        if(ret < 0){
            devicelogger.error("[closeUART3]uart3 close failed ret="+ret);
            return false;
        }
        return true;
    }
    private boolean isChinese(String name) {
        try {
            int n = 0;
            for(int i = 0; i < name.length(); i++) {
                n = (int)name.charAt(i);
                if(!(19968 <= n && n <40869)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    private int byteArrayToInt(byte b) {
        int num = 0;
        num |= (b & 0xff);
        return num;
    }
    private byte[] byteMerge(byte[] byte1, byte[] byte2) {
        int byteLen1 = byte1.length;
        int byteLen2 = byte2.length;
        byte[] out = new byte[byteLen1 + byteLen2];
        System.arraycopy(byte1, 0, out, 0, byteLen1);
        System.arraycopy(byte2, 0, out, byteLen1, byteLen2);
        return out;
    }
    private byte[] intToBytes(int num) {
        byte[] temp = new byte[2];
        for (int i = 0; i < 2; i++) {
            temp[i] = (byte) ((num >>> (8 - i * 8)) & 0xFF);
        }
        return temp;
    }
    private void addFrameNo() {
        if (mFrameNoFlag == 0xff) {
            mFrameNoFlag = 0;
        } else {
            mFrameNoFlag++;
            mFrameNoFlag++;
        }
    }

    private byte[] DecToBCDArray(long num) {
        int digits = 0;
        long temp = num;
        while (temp != 0) {
            digits++;
            temp /= 10;
        }
        int byteLen = digits % 2 == 0 ? digits / 2 : (digits + 1) / 2;
        boolean isOdd = digits % 2 != 0;
        byte bcd[] = new byte[byteLen];
        for (int i = 0; i < digits; i++) {
            byte tmp = (byte) (num % 10);
            if (i == digits - 1 && isOdd)
                bcd[i / 2] = tmp;
            else if (i % 2 == 0)
                bcd[i / 2] = tmp;
            else {
                byte foo = (byte) (tmp << 4);
                bcd[i / 2] |= foo;
            }
            num /= 10;
        }
        for (int i = 0; i < byteLen / 2; i++) {
            byte tmp = bcd[i];
            bcd[i] = bcd[byteLen - i - 1];
            bcd[byteLen - i - 1] = tmp;
        }
        return bcd;
    }
    private byte[] DecToBCDArray(long num, int bcdArrayLenth){
        byte[] bcd = DecToBCDArray(num);
        ByteBuffer byteBuffer = ByteBuffer.allocate(bcdArrayLenth);
        int fill = bcdArrayLenth - bcd.length;
        while(fill-->0){
            byteBuffer.put((byte) 0);
        }
        byteBuffer.put(bcd);
        return byteBuffer.array();
    }
    private byte[] toEscape(byte[] in) {
        int inLen = in.length;
        byte[] out = new byte[inLen * 2];
        int outLen = 0;
        for (int i = 0; i < inLen; i++) {
            if (in[i] == 0x02) {
                out[outLen] = (byte) 0xaa;
                out[outLen + 1] = (byte) 0xfd;
                outLen++;
            } else if (in[i] == (byte)0xaa) {
                out[outLen] = (byte) 0xaa;
                out[outLen + 1] = 0x55;
                outLen++;
            } else if (in[i] == (byte)0xef) {
                out[outLen] = (byte) 0xaa;
                out[outLen + 1] = 0x10;
                outLen++;
            }else {
                out[outLen] = in[i];
            }
            outLen++;
        }
        out = byteCut(out, 0, outLen);
        return out;
    }
    private byte[] fromEscape(byte[] in) {
        int inLen = in.length;
        int outLen = 0;
        byte[] out = new byte[inLen];
        for (int i = 0; i < inLen; i++) {
            if (in[i] == (byte)0xaa) {
                if (in[i + 1] == (byte)0xfd) {
                    out[outLen] = 0x02;
                    i++;
                } else if (in[i + 1] == (byte)0x55) {
                    out[outLen] = (byte) 0xaa;
                    i++;
                } else if (in[i + 1] == (byte)0x10) {
                    out[outLen] = (byte) 0xef;
                    i++;
                }else {
                    out[outLen] = in[i];
                }
            } else {
                out[outLen] = in[i];
            }
            outLen++;
        }
        out = byteCut(out, 0, outLen);
        return out;
    }
    private byte[] byteCut(byte[] in, int start, int len) {
        if (start + len > in.length) {
            len = in.length - start;
        }
        byte[] out = new byte[len];
        System.arraycopy(in, start, out, 0, len);
        return out;
    }

    private String getHexDump(byte[] bytes, int offset, int length) {
        if (bytes == null || bytes.length == 0)
            return "empty";
        if (offset >= bytes.length) {
            return "out of length,totallen:" + bytes.length + ",offset:"
                    + offset;
        }
        StringBuffer out = new StringBuffer();

        int byteValue = bytes[offset] & 0xFF;
        out.append((char) highDigits[byteValue]);
        out.append((char) lowDigits[byteValue]);

        for (int i = offset + 1; (i < bytes.length && (i - offset) < length); i++) {
            out.append(' ');
            byteValue = bytes[i] & 0xFF;
            out.append((char) highDigits[byteValue]);
            out.append((char) lowDigits[byteValue]);
        }
        return out.toString();
    }
    private static final byte[] highDigits;
    private static final byte[] lowDigits;

    static {
        final byte[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        int i;
        byte[] high = new byte[256];
        byte[] low = new byte[256];
        for (i = 0; i < 256; i++) {
            high[i] = digits[i >>> 4];
            low[i] = digits[i & 0x0F];
        }
        highDigits = high;
        lowDigits = low;
    }

    private String getHexDump(byte[] bytes) {
        return getHexDump(bytes, 0, bytes.length);
    }

    private void clearKeyBoard() {
        int repeatTimes = 3;
        byte[] buffer = new byte[256];
        int ret;
        while (repeatTimes > 0) {
            ret = serialOper.read(buffer, buffer.length, 0);
            if (ret > 0) {
                repeatTimes = 3;
            } else {
                repeatTimes--;
            }
        }
    }

    /**
     * 黑体常规五号字体
     * 8*14
     * small去掉末位2个字节 0x00，让高度为12 8*12
     */
    static byte[][] szBigNumber_sm=
            {
                    { 0x00,0x38, (byte) 0xEC, (byte) 0xEE, (byte) 0xCE, (byte) 0xCE, (byte) 0xCE, (byte) 0xCE, (byte) 0xEE, (byte) 0xFC,0x38,0x00,},
                    { 0x00,0x18,0x38,0x78,0x38,0x38,0x38,0x38,0x38,0x38,0x38,0x00,},
                    { 0x00,0x7C, (byte) 0xEE, (byte) 0xCE,0x0E,0x0C,0x1C,0x38,0x70, (byte) 0xE0, (byte) 0xFE,0x00,},
                    { 0x00,0x3C, (byte) 0xEE, (byte) 0xEE,0x0E,0x3C,0x1C,0x0E, (byte) 0xCE, (byte) 0xEE,0x78,0x00,},
                    {0x00,0x0C,0x1C,0x3C,0x3C,0x7C, (byte) 0xEC, (byte) 0xCC, (byte) 0xFE,0x0C,0x0C,0x00,},
                    { 0x00,0x7E,0x60, (byte) 0xE0, (byte) 0xF8, (byte) 0xFC,0x0E,0x0E, (byte) 0xCE, (byte) 0xFC,0x78,0x00,},
                    {0x00,0x1C,0x38,0x30,0x70, (byte) 0xFE, (byte) 0xE6, (byte) 0xC6, (byte) 0xE6, (byte) 0xEE,0x7C,0x00,},
                    { 0x00, (byte) 0xFE,0x06,0x0E,0x1C,0x1C,0x38,0x38,0x30,0x70,0x70,0x00,},
                    {0x00,0x78, (byte) 0xEE, (byte) 0xCE, (byte) 0xEE,0x7C, (byte) 0xFE, (byte) 0xCE, (byte) 0xCE, (byte) 0xEE,0x7C,0x00,},
                    {0x00,0x78, (byte) 0xFE, (byte) 0xCE, (byte) 0xCE, (byte) 0xCE, (byte) 0xFC,0x1C,0x38,0x30,0x70,0x00,},
            };

    byte szDot_sm[]={0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00, (byte) 0xE0,0x00,};


    /**
     * 宋体常规三号 16*21
     * 大的汉字，去掉末尾两个字节，使高度为40   16*20
     */
    static byte[][] szBigNumber=
            {
                    {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x1F,0x00,0x3B, (byte) 0x80,0x71, (byte) 0xC0,0x71, (byte) 0xC0,
                            0x71,(byte) 0xC0,(byte) 0xE1,(byte) 0xE0,(byte) 0xE0,(byte) 0xE0,(byte) 0xE0,(byte) 0xE0,(byte) 0xE1, (byte) 0xE0,0x71, (byte) 0xC0,0x71, (byte) 0xC0,0x71, (byte) 0xC0,
                            0x3B,(byte) 0x80,0x1F,0x00,0x00,0x00,0x00,0x00,},
                    {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x06,0x00,0x3E,0x00,0x0E,0x00,0x0E,0x00,
                            0x0E,0x00,0x0E,0x00,0x0E,0x00,0x0E,0x00,0x0E,0x00,0x0E,0x00,0x0E,0x00,0x0E,0x00,
                            0x0E,0x00,0x3F, (byte) 0x80,0x00,0x00,0x00,0x00,},
                    {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x1F, (byte) 0x80,0x73, (byte) 0xC0,0x61, (byte) 0xC0,0x71, (byte) 0xC0,
                            0x71, (byte) 0xC0,0x01, (byte) 0xC0,0x03, (byte) 0x80,0x07,0x00,0x0E,0x00,0x1C,0x00,0x38,0x00,0x70, (byte) 0xC0,
                            0x60, (byte) 0xC0, (byte) 0xFF, (byte) 0xC0,0x00,0x00,0x00,0x00,},
                    {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x3F,0x00,0x73,(byte)0x80,0x71,(byte)0xC0,0x71,(byte)0xC0,
                            0x01,(byte)0x80,0x03,(byte)0x80,0x0F,0x00,0x03,(byte)0x80,0x01,(byte)0xC0,0x01,(byte)0xC0,0x71,(byte)0xC0,0x71,(byte)0xC0,
                            0x73,(byte)0x80,0x3F,0x00,0x00,0x00,0x00,0x00,},
                    {0x00,0x00,0x00,0x00,0x00,0x00,0x03,0x00,0x07,0x00,0x07,0x00,0x0F,0x00,0x1F,0x00,
                            0x1F,0x00,0x37,0x00,0x37,0x00,0x67,0x00, (byte) 0xC7,0x00, (byte) 0xFF, (byte) 0xC0,0x07,0x00,0x07,0x00,
                            0x07,0x00,0x1F, (byte) 0xC0,0x00,0x00,0x00,0x00,
                    },
                    {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x3F, (byte) 0xC0,0x30,0x00,0x60,0x00,0x60,0x00,
                            0x60,0x00,0x7F, (byte) 0x80,0x73, (byte) 0xC0,0x21, (byte) 0xC0,0x01, (byte) 0xC0,0x00, (byte) 0xC0,0x71, (byte) 0xC0,0x71, (byte) 0xC0,
                            0x73, (byte) 0x80,0x3F,0x00,0x00,0x00,0x00,0x00,},
                    {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x1F, (byte) 0x80,0x39, (byte) 0xC0,0x71, (byte) 0xC0,0x70,0x00,
                            0x70,0x00,(byte)0xFF,(byte)0x80,(byte)0xF9,(byte)0xC0,(byte)0xF1,(byte)0xC0,(byte)0xE0,(byte)0xE0,(byte)0xE0,(byte)0xE0,0x70, (byte) 0xE0,0x71, (byte) 0xC0,
                            0x39,(byte)0xC0,0x1F,0x00,0x00,0x00,0x00,0x00,},
                    {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x7F, (byte) 0xC0,0x61, (byte) 0x80,0x61, (byte) 0x80,0x03,0x00,
                            0x03,0x00,0x06,0x00,0x06,0x00,0x0E,0x00,0x0C,0x00,0x1C,0x00,0x1C,0x00,0x1C,0x00,
                            0x1C,0x00,0x1C,0x00,0x00,0x00,0x00,0x00,
                    },
                    {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x3F, (byte) 0x80,0x71, (byte) 0xC0,0x61, (byte) 0xC0,0x60, (byte) 0xC0,
                            0x71, (byte) 0xC0,0x39, (byte) 0x80,0x1F,0x00,0x33, (byte) 0x80,0x61, (byte) 0xC0, (byte) 0xE0, (byte) 0xC0, (byte) 0xE0, (byte) 0xC0, (byte) 0xE1, (byte) 0xC0,
                            0x71, (byte) 0xC0,0x3F,0x00,0x00,0x00,0x00,0x00,
                    },
                    {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x3F,0x00,0x73, (byte) 0x80,0x71, (byte) 0xC0, (byte) 0xE1, (byte) 0xC0,
                            (byte)0xE1,(byte)0xC0,(byte)0xE1,(byte)0xE0,(byte)0xE1,(byte)0xE0,0x73,(byte)0xC0,0x3F,(byte)0xC0,0x01,(byte)0xC0,0x01,(byte)0xC0,0x73, (byte) 0x80,
                            0x77,(byte)0x80,0x3E,0x00,0x00,0x00,0x00,0x00,},
            };
    byte szDot[]={0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
            0x70,0x00,0x70,0x00,0x00,0x00,0x00,0x00};
    private final byte[] CMD_SHOW_BIT_LL2 = {0x10 , 0x00};
    private byte[] with;
    private byte[] height;

    public boolean setKeyTone(boolean isOn){
        try {
            synchronized (KeyBoarSync) {
                prepareDealFrame();
                byte[] value = new byte[1];
                if(isOn){
                    value[0] = 1;
                }else{
                    value[0] = 0;
                }
                int ackCode = dealFrame(CMD_KEY_TONE,value,null,0);
                devicelogger.debug(">>>setKeyTone ack="+ackCode);
                if(ackCode == ACK_OK){
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 显示大数字
     * @param sNum 总长128，每个字符 8
     * @return
     */
    public void showBigNumber(String sNum){
        synchronized (KeyBoarSync) {
            try {
                devicelogger.debug("[showBigNumber] dealFrame start.");
                prepareDealFrame();
                int startX = 128-sNum.length()*12;
                if(!sNum.contains(".")){
                    startX = startX - 7;
                }
                int ackCode = sendBigNumber(sNum,startX);
                devicelogger.debug("[showBigNumber] dealFrame ackCode="+ackCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showMessage(String str,final InputStream is){
        synchronized (KeyBoarSync) {
            try {
                prepareDealFrame();
                boolean isNumeric = isNumeric(str),isChinese = isChinese(str);
                int startX  = 0, ackCode = -1;
                devicelogger.debug("[showMessage] start isNum="+isNumeric+" isChinese="+isChinese);
                if(isNumeric){
                    startX = 128-str.length()*12;
                    if(!str.contains(".")){
                        startX = startX - 7;
                    }
                    ackCode = sendBigNumber(str,startX);
                    devicelogger.debug("[showMessage] bigNumber ackCode="+ackCode);
                }else if(isChinese){
                    startX = (128 - (12*str.length()))/2;
                    ackCode = sendChinese(str,startX,is);
                    devicelogger.debug("[showMessage] message ackCode="+ackCode);
                }else{
                    int numCount = numberCount(str),dotCount = 0,chineseCount = 0;
                    if(str.contains(".")){
                        dotCount = 1;
                    }
                    chineseCount = str.length() - numCount - dotCount;
                    startX = ((128 - (chineseCount*12+dotCount*5+numCount*8))/2);
                    Pattern p = Pattern.compile("[\\u4e00-\\u9fa5]+|\\.+|\\d+");
                    Matcher m = p.matcher(str);
                    while (m.find()) {
                        String sendStr = m.group();
                        char c = m.group().charAt(0);
                        if ((c>='0'&& c<='9') || c == '.'){
                            ackCode = sendSmallNumber(m.group(),startX);
                            if (sendStr.contains(".")){
                                startX += (sendStr.length() -1)*8 + 5;
                            }else {
                                startX += sendStr.length()*8;
                            }
                        }else {
                            ackCode = sendChinese(m.group(),startX,is);
                            startX += sendStr.length() * 12;
                        }
                    }
                    devicelogger.debug("[showMessage] mixture ackCode="+ackCode);
                }
                if (is != null){
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private boolean isNumeric(String str){
        int dotCount = 0;
        for (int i = str.length(); --i >= 0; ) {
            char ch = str.charAt(i);
            if ((!Character.isDigit(ch))&&(ch != '.')) {
                return false;
            }
            if(ch == '.'){
                dotCount++;
            }
        }
        if(dotCount >= 2){
            return false;
        }
        return true;
    }

    private int numberCount(String str){
        int count = 0;
        for (int i = str.length(); --i >= 0; ) {
            char ch = str.charAt(i);
            if (Character.isDigit(ch)) {
                count++;
            }
        }
        return count;
    }
    private void prepareDealFrame(){
        openUART3();
        byte[] tempBuf = byteMerge(intToBytes(0), new byte[]{0x00, 0x1F});
        dealFrame(CMD_SHOW_CLEAR, tempBuf, null, 0);
    }

    private int sendBigNumber(String sNum,int startX){
        int nRet;
//        if (sNum.contains(".")) {
//            String[] strings = sNum.split("\\.");
//            sendNum(strings[0], startX);
//            nRet = sendNum(".", startX + 12 * strings[0].length());
//            if (strings.length > 1) {
//                nRet = sendNum(strings[1], startX + 12 * strings[0].length() + 5);
//            }
//        } else {
            nRet = sendNum(sNum, startX);
//        }
        return nRet;
    }
    /**
     * 发送大数字
     * @param str 输入的字符串
     * @param startX 起始位置
     * @return
     */
    private int sendNum(String str,int startX){
        int nRet;
        byte[] lenData;
        byte[] bitStartY = new byte[]{0x00,0x08};
        byte[] sendTemp;
//        if (!str.contains(".")){
            sendTemp = splicNum(str);
//        }else {
//            sendTemp = szDot;
//        }
        with = intToBytes(12 * str.length());
        lenData = byteMerge(intToBytes(startX),bitStartY);
        height = new byte[]{0x00,0x14};
        lenData = byteMerge(lenData,with);
        lenData = byteMerge(lenData,height);
        lenData = byteMerge(lenData,CMD_SHOW_BIT_LL2);
        lenData = byteMerge(lenData,sendTemp);
        nRet = dealFrame(CMD_SHOW_BIT,lenData,null,0);
        return nRet;
    }

    /**
     * 切割大数字，拼接成一次发送的数据
     * @param sNum
     * @return
     */
    private byte[] splicNum(String sNum){
        int i;
        int len = sNum.length(); //数据长度
        int size = 40;
        byte[] buffer ;
        byte[] tempBuf = new byte[40 * len];
        byte[] sendTemp ;
        byte[] tmp = new byte[size * len];
        for ( i = 0; i < len; i++) {
            String sTemp = sNum.substring(i,i+1);
            if(sTemp.equals(".")){
                buffer = szDot;
            }else{
                buffer = szBigNumber[Integer.valueOf(sTemp)];
            }
            System.arraycopy(buffer,0,tempBuf,i*40,40);
        }
        if (len != 1){
            for (i = 0; i < 20; i++) {
                for (int j=0; j< sNum.length(); j++ ){
                    System.arraycopy(tempBuf,40*j + 2*i,tmp,2*j + 2*len*i,2);
                }
            }
            sendTemp = new byte[3*size/4 * len];
            with = intToBytes(12*len);
            if (len % 2 == 1) {//奇数
                sendTemp = new byte[3*size/4*(len-1) + size];
                int count = len / 2;
                int iLen = size*(len-1)/4/count;
                for (i = 0; i < iLen ; i++) {
                    for (int k = 0; k < count; k++) {
                        byte temp2 = (byte) ((tmp[4 * i*count + 1 + 2 * i + 4*k]) | ((tmp[4 * i*count + 2 + 2 * i+ 4*k] >> 4) & 0x0f));
                        byte temp3 = (byte) ((tmp[4 * i*count + 2 + 2 * i + 4*k] << 4) | ((tmp[4 * i*count + 3 + 2 * i+ 4*k] >> 4) & 0x0f));

                        sendTemp[3 * i*count  + 2*i + 3*k] = tmp[4 * i*count + 2*i + 4*k];
                        sendTemp[3 * i*count + 1  + 2*i + 3*k] = temp2;
                        sendTemp[3 * i*count + 2  + 2*i + 3*k] = temp3;
                    }
                    System.arraycopy(tmp, 4 * i*count + 4*count + 2*i, sendTemp, 3 * i*count + 3*count + 2*i, 2);
                }
            }else {
                for (i = 0; i < tmp.length/4; i++) {
                    byte temp2 = (byte) ((tmp[4*i+1]) | ((tmp[4*i+2] >> 4)  & 0x0f));
                    byte temp3 = (byte)((tmp[4*i+2] << 4) | ((tmp[4*i+3] >> 4) & 0x0f));

                    System.arraycopy(tmp,4*i,sendTemp,3*i,1);
                    sendTemp[3*i + 1] = temp2;
                    sendTemp[3*i + 2] = temp3;
                }
            }
        }else {
            sendTemp = tempBuf;
            with = new byte[]{0x00,0x10};
        }
        return sendTemp;
    }
    /**
     * 显示小数字
     * @param sNum 输入的数字
     * @param startX 起始位置
     * @return
     */
    private int sendSmallNumber(String sNum, int startX){
        int nRet;
        if (!sNum.contains(".")){
            nRet = sendSmallNum(sNum,startX);
        }else if (".".equals(sNum)){
            nRet = sendSmallNum(".",startX);
        }else {
            String[] strings = sNum.split("\\.");
            sendSmallNum(strings[0],startX);
            nRet = sendSmallNum(".",startX + 8*strings[0].length());
            if (strings.length > 1){
                nRet = sendSmallNum(strings[1],startX + 8*strings[0].length() + 5);
            }
        }
        return nRet;
    }
    private int sendSmallNum(String str,int startX){
        int nRet;
        byte[] lenData;
        byte[] bitStartY = new byte[]{0x00,0x0C};
        byte[] sendTemp;
        if (!str.contains(".")){
            sendTemp = spSmallNum(str);
        }else {
            sendTemp = szDot_sm;
        }
        lenData = byteMerge(intToBytes(startX),bitStartY);
        with = intToBytes(8 * str.length());
        height = new byte[]{0x00,0x0C};
        lenData = byteMerge(lenData,with);
        lenData = byteMerge(lenData,height);
        lenData = byteMerge(lenData,CMD_SHOW_BIT_LL);
        lenData = byteMerge(lenData,sendTemp);
        nRet = dealFrame(CMD_SHOW_BIT,lenData,null,0);
        return nRet;
    }
    private byte[] spSmallNum(String sNum){
        int nRet,i;
        byte[] buffer;
        int len = sNum.length();
        byte[] sendTemp = new byte[12*sNum.length()];
        byte[] tempBuf = new byte[12*sNum.length()];
        for ( i = 0; i < sNum.length(); i++) {
            String sTemp = sNum.substring(i,i+1);
            buffer = szBigNumber_sm[Integer.valueOf(sTemp)];
            System.arraycopy(buffer,0,tempBuf,i*12,12);
        }
        for (i = 0; i < 12; i++) {
            for (int j=0; j< sNum.length(); j++ ){
                System.arraycopy(tempBuf,12*j + i,sendTemp,j + len*i,1);
            }
        }
        return  sendTemp;
    }
//    /**
//     * 显示小数字
//     * @param sNum 输入的数字
//     * @param startX 起始位置
//     * @return
//     */
//    public int sendSmallNumber(String sNum, int startX){
//        int nRet,i;
//        byte[] lenData;
//        byte[] bitStartY = new byte[]{0x00,0x0C};
//        byte[] buffer;
//        int len = sNum.length();
//        byte[] sendTemp = new byte[12*sNum.length()];
//        byte[] tempBuf = new byte[12*sNum.length()];
//        if (!sNum.contains(".")){
//            for ( i = 0; i < sNum.length(); i++) {
//                String sTemp = sNum.substring(i,i+1);
//                buffer = szBigNumber_sm[Integer.valueOf(sTemp)];
//                System.arraycopy(buffer,0,tempBuf,i*12,12);
//            }
//            for (i = 0; i < 12; i++) {
//                for (int j=0; j< sNum.length(); j++ ){
//                    System.arraycopy(tempBuf,12*j + i,sendTemp,j + len*i,1);
//                }
//            }
//            with = intToBytes(8 * sNum.length());
//        }else {
//            sendTemp = szDot_sm;
//            with = intToBytes(5 * sNum.length());
//        }
//        lenData = byteMerge(intToBytes(startX),bitStartY);
//        height = new byte[]{0x00,0x0C};
//        lenData = byteMerge(lenData,with);
//        lenData = byteMerge(lenData,height);
//        lenData = byteMerge(lenData,CMD_SHOW_BIT_LL2);
//        lenData = byteMerge(lenData,sendTemp);
//        nRet = dealFrame(CMD_SHOW_BIT,lenData,null,0);
//        return nRet;
//    }
    //中文显示
    /**
     * 一次性发送汉字
     * @param str 输入的字符串，需中文
     * @param startX 中文的起始位置
     * @return
     */
    private int sendChinese(String str,int startX,final InputStream is){

        int nRet;
        byte[] lenData;
        byte[] bitStartY = new byte[]{0x00,0x0B};

        byte[] sendTemp = cnSplite(str,is);
        lenData = byteMerge(intToBytes(startX),bitStartY);

        byte[] height = new byte[]{0x00,0x0C};
        lenData = byteMerge(lenData,with);
        lenData = byteMerge(lenData,height);
        lenData = byteMerge(lenData,CMD_SHOW_BIT_LL2);
        lenData = byteMerge(lenData,sendTemp);
        nRet = dealFrame(CMD_SHOW_BIT,lenData,null,0);
        return nRet;
    }
    /**
     * 切割拼接中文
     * @param str
     * @return
     */
    private byte[] cnSplite(String str,final InputStream is){
        try {
            int size = 24;
            int offsetHz ;
            byte[] buffer;
            byte[] tempBuf = new byte[size* str.length()];

            int len = str.length(); //数据长度
            try {
                byte[] hz;
                for (int i = 0; i < len; i++) {
                    hz = str.substring(i,i+1).getBytes("GBK");
                    buffer = new byte[size];
                    offsetHz = ((hz[0] - (byte)0x81)*190 + (byteArrayToInt(hz[1]) - (byte)0x41))*size;
//                    Log.e(TAG, "onCreate: " + Dump.getHexDump(hz) + " size:" +size + "offset:" + offsetHz );

                    is.skip(offsetHz);
                    if (is.read(buffer) != -1){
//                        Log.e(TAG, "onCreate: " + Dump.getHexDump(buffer) );
                        is.reset();
                    }
                    System.arraycopy(buffer,0,tempBuf,i*24,24);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }
            byte[] tmp = new byte[size * len];
            byte[] sendTemp;
            if (len != 1){
                for (int i = 0; i < size/2; i++) {
                    for (int j=0; j<str.length(); j++ ){
                        System.arraycopy(tempBuf,24*j + 2*i,tmp,2*j + 2*len*i,2);
                    }
                }
                sendTemp = new byte[18 * len];
                with = intToBytes(12*len);
                if (len % 2 == 1) {//奇数
                    sendTemp = new byte[18*(len-1) + 24];
                    int count = len / 2;
                    int iLen = size*(len-1)/4/count;
//                    Log.e(TAG, "showChinese: count:" + count );
                    for (int i = 0; i < iLen ; i++) {
                        for (int k = 0; k < count; k++) {
                            byte temp2 = (byte) ((tmp[4 * i*count + 1 + 2 * i + 4*k]) | ((tmp[4 * i*count + 2 + 2 * i+ 4*k] >> 4) & 0x0f));
                            byte temp3 = (byte) ((tmp[4 * i*count + 2 + 2 * i + 4*k] << 4) | ((tmp[4 * i*count + 3 + 2 * i+ 4*k] >> 4) & 0x0f));

                            sendTemp[3 * i*count  + 2*i + 3*k] = tmp[4 * i*count + 2*i + 4*k];
                            sendTemp[3 * i*count + 1  + 2*i + 3*k] = temp2;
                            sendTemp[3 * i*count + 2  + 2*i + 3*k] = temp3;
                        }
                        System.arraycopy(tmp, 4 * i*count + 4*count + 2*i, sendTemp, 3 * i*count + 3*count + 2*i, 2);
                    }

                }else {
                    for (int i = 0; i < tmp.length/4; i++) {
                        byte temp2 = (byte) ((tmp[4*i+1]) | ((tmp[4*i+2] >> 4)  & 0x0f));
                        byte temp3 = (byte)((tmp[4*i+2] << 4) | ((tmp[4*i+3] >> 4) & 0x0f));

                        System.arraycopy(tmp,4*i,sendTemp,3*i,1);
                        sendTemp[3*i + 1] = temp2;
                        sendTemp[3*i + 2] = temp3;
                    }
                }
            }else {
                sendTemp = tempBuf;
                with = new byte[]{0x00,0x0C};
            }
            return sendTemp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}


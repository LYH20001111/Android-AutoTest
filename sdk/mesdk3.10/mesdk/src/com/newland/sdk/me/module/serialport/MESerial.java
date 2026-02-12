package com.newland.sdk.me.module.serialport;

import android.annotation.SuppressLint;
import android.content.Context;
import android.newland.AnalogSerialManager;
import android.newland.NLUART3Manager;
import android.newland.content.NlContext;
import android.newland.os.NlBuild;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedReader;
import com.newland.ndk.NdkApiManager;
import com.newland.ndk.h.EM_PORT_NUM;
import com.newland.sdk.me.module.externalPininput.BleBasePackage;
import com.newland.sdk.me.module.externalPininput.PinpadPackage;
import com.newland.sdk.me.module.usb.USBSafeBuffer;
import com.newland.sdk.me.utils.FileIOUtils;
import com.newland.sdk.module.externalPin.PinpadInitExtParams;
import com.newland.sdk.module.serialport.Baudrate;
import com.newland.sdk.module.serialport.PortType;
import com.newland.sdk.module.serialport.SerialExtParams;
import com.newland.sdk.module.serialport.SerialPortModule;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;
import com.newland.sdk.utils.ISOUtils;
import com.newland.uartport.Node;
import com.newland.uartport.UartPort;

import static com.newland.ndk.h.EM_PORT_NUM.PORT_NUM_COM1;
import static com.newland.ndk.h.EM_PORT_NUM.PORT_NUM_COM2;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MESerial extends AbstractModule implements SerialPortModule {

    private AnalogSerialManager mAnalogSerialManager;// min usb
    private NLUART3Manager uart3Manager;// rs232
    private int fd232 = 0;
    private int fdpinpad = 0;
    private PortType portType = PortType.RS232;
    private NdkApiManager ndkApiManager;
    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger("MESerial");
    private int mUartPortFd = -1;
    private int mUartPortFd2 = -1;
    private BleBasePackage bleBasePackage;
    private Context context;
    private SerialExtParams params;
    private USBSafeBuffer bleBaseSerialSafeBuffer;

    @SuppressLint("WrongConstant")
    public MESerial(AbstractDevice device, Context context) {
        super(device);
        this.context = context;
        mAnalogSerialManager = (AnalogSerialManager) context.getSystemService(NlContext.ANALOG_SERIAL_SERVICE);
        uart3Manager = (NLUART3Manager) context.getSystemService(NlContext.UART3_SERVICE);
        ndkApiManager = NdkApiManager.getNdkApiManager();
        bleBaseSerialSafeBuffer = new USBSafeBuffer();
    }

    @Override
    public boolean isStandardModule() {
        return true;
    }

    @Override
    public ModuleType getStandardModuleType() {
        return ModuleType.USB_SERIALPORT;
    }

    @Override
    public String getExModuleType() {
        return null;
    }

//    SerialPortModule getinstance(int id){
//        Map<Integer, MESerial> map=new HashMap<Integer, MESerial>();
//        if(map.get(id)==null){
//            map.put(id,new MESerial());
//        }
//    }

    @Override
    public int open(PortType portType, Baudrate baudrate, SerialExtParams params) {
        deviceLogger.debug("[open] portType=" + portType + " baudrate=" + baudrate + " params=" + params);
        this.params = params;
        this.portType = portType;
        int baudrateData = 115200;
        String bitString = "8N1NN";// 数据位+校验位+停止位+红外通讯防止反射串扰+是否开启读写阻塞
        int resultCode = -1;
        mUartPortFd  = -1;
        mUartPortFd2 = -1;
        if(baudrate != null){
            switch (baudrate) {
                case BPS300:
                    baudrateData = 300;
                    break;
                case BPS1200:
                    baudrateData = 1200;
                    break;
                case BPS2400:
                    baudrateData = 2400;
                    break;
                case BPS4800:
                    baudrateData = 4800;
                    break;
                case BPS7200:
                    baudrateData = 7200;
                    break;
                case BPS9600:
                    baudrateData = 9600;
                    break;
                case BPS19200:
                    baudrateData = 19200;
                    break;
                case BPS38400:
                    baudrateData = 38400;
                    break;
                case BPS57600:
                    baudrateData = 57600;
                    break;
                case BPS115200:
                    baudrateData = 115200;
                    break;
                default:
                    break;
            }
        }
        if (params != null) {
            String dataNumBit = "8";
            switch (params.getDataBit()) {
                case DATA_BIT_8:
                    dataNumBit = "8";
                    break;
                case DATA_BIT_7:
                    dataNumBit = "7";
                    break;

                case DATA_BIT_6:
                    dataNumBit = "6";
                    break;
                case DATA_BIT_5:
                    dataNumBit = "5";
                    break;
                default:
                    break;
            }
            String checkBit = "N";
            switch (params.getOddEvenCheck()) {
                case NO_CHECK:
                    checkBit = "N";
                    break;
                case EVEN_CHECK:
                    checkBit = "E";
                    break;
                case ODD_CHECK:
                    checkBit = "O";
                    break;

                default:
                    break;
            }

            String stopBitType = "1";
            switch (params.getStopBit()) {
                case STOP_BIT_TWO:
                    stopBitType = "2";
                    break;
                case STOP_BIT_ONE_POINT_FIVE:
                    stopBitType = "1.5";
                    break;
                case STOP_BIT_ONE:
                    stopBitType = "1";
                    break;

                default:
                    break;
            }
            bitString = dataNumBit + checkBit + stopBitType + "NN";
        }

        switch (portType) {
            case RS232: {
                //N750的232口是模拟串口
                if(isN750()){
                    resultCode = mAnalogSerialManager.open(getNodes().get("DB9"));
                    if (resultCode > -1) {
                        resultCode = mAnalogSerialManager.setconfig(baudrateData, 0, bitString.getBytes());
                        return resultCode;
                    }
                }
                if(isU2000()){
                    if (params == null || !params.isRS232UART3()) {
                        mUartPortFd = UartPort.JNI_openPort(Node.PortHSL3.getValue(), baudrateData, bitString.getBytes(),null);
                    }
                } else if (isCPOS()) {
                    if (params == null || !params.isRS232UART3()) {
                        mUartPortFd = UartPort.JNI_openPort(Node.PortHSL2.getValue(), baudrateData, bitString.getBytes(),null);
                    }
                } else if (isN750POrP300()) {
                    String nodeName = getNodeNameFromFile(portType);
                    if(nodeName != null){
                        mUartPortFd = UartPort.JNI_openPort(-1, baudrateData, bitString.getBytes(),nodeName.getBytes());
                    }
                }else {
                    if (params == null || !params.isRS232UART3()) {
                        mUartPortFd = UartPort.JNI_openPort(Node.PortHSL0.getValue(), baudrateData, bitString.getBytes(),null);
                    }
                }
                deviceLogger.debug("[open] JNI_openPort mUartPortFd:" + mUartPortFd);
                if (mUartPortFd < 0) {
                    if(isU2000()){
                        deviceLogger.debug("[open] uart3Manager.open 63");
                        resultCode = uart3Manager.open(63);
                        fd232 = resultCode;
                        if (resultCode > -1) {
                            resultCode = uart3Manager.setconfig(baudrateData, 0, bitString.getBytes());
                        }
                    }else if (isCPOS()) {
                        if (Build.MODEL.equals("CPOS X1")) {
                            deviceLogger.debug("[open] uart3Manager.open 60");
                            resultCode = uart3Manager.open(60);
                        } else {
                            deviceLogger.debug("[open] uart3Manager.open 62");
                            resultCode = uart3Manager.open(62);//PINPAD口是open().....RS232口是open(62)，针对cpos
                        }
                        fd232 = resultCode;
                        if (resultCode > -1) {
                            resultCode = uart3Manager.setconfig(baudrateData, 0, bitString.getBytes());
                        }
                    } else {
                        deviceLogger.debug("[open] uart3Manager.open");
                        resultCode = uart3Manager.open();
                        if (resultCode > -1) {
                            resultCode = uart3Manager.setconfig(baudrateData, 0, bitString.getBytes());
                        }
                    }
                } else {
                    return mUartPortFd;
                }
                break;
            }
            case PINPAD:
                if(isU2000()){
                    if (params == null || !params.isRS232UART3()) {
                        mUartPortFd2 = UartPort.JNI_openPort(Node.PortHSL4.getValue(), baudrateData, bitString.getBytes(),null);
                    }
                    deviceLogger.debug("[open] PINPAD JNI_openPort mUartPortFd2:" + mUartPortFd2);
                    if(mUartPortFd2 < 0){
                        deviceLogger.debug("[open] uart3Manager.open 64");
                        resultCode = uart3Manager.open(64);
                        fdpinpad = resultCode;
                        if (resultCode > -1) {
                            resultCode = uart3Manager.setconfig(baudrateData, 0, bitString.getBytes());
                        }
                    }else {
                        return mUartPortFd2;
                    }
                }else if (isCPOS() || isN550()) {
                    mUartPortFd = UartPort.JNI_openPort(Node.PortHSL0.getValue(), baudrateData, bitString.getBytes(),null);
                    deviceLogger.debug("[open] mUartPortFd:" + mUartPortFd);
                    if (mUartPortFd < 0) {
                        resultCode = uart3Manager.open();
                        fdpinpad = resultCode;
                        if (resultCode > -1) {
                            resultCode = uart3Manager.setconfig(baudrateData, 0, bitString.getBytes());
                        }
                    } else {
                        return mUartPortFd;
                    }
                } else {
                    String pszAttr = baudrateData + ",";
                    String bufS = new String(bitString);
                    StringBuilder sb = new StringBuilder(bufS);
                    for (int i = sb.length(); i >= 2; i--) {
                        sb.insert(i - 1, ",");
                    }
                    deviceLogger.debug("[open] pszAttr:" + pszAttr + sb.toString());
                    resultCode = ndkApiManager.getSerialPort().NDK_PortOpen(getNDKPortNum(), (pszAttr + sb.toString()).getBytes());
                }

                break;
            case MIN_USB:
                if (isN750POrP300()) {
                    String nodeName = getNodeNameFromFile(portType);
                    if(nodeName != null){
                        mUartPortFd2 = UartPort.JNI_openPort(-1, baudrateData, bitString.getBytes(),nodeName.getBytes());
                    }
                    deviceLogger.debug("[open] JNI_openPort MIN_USB mUartPortFd2:" + mUartPortFd2);
                }
                if (mUartPortFd2 < 0) {
                    resultCode = mAnalogSerialManager.open();
                    if (resultCode > -1) {
                        resultCode = mAnalogSerialManager.setconfig(baudrateData, 0, bitString.getBytes());
                    }
                }else {
                    return mUartPortFd2;
                }
                break;
            case BLEBASE_USB1:
            case BLEBASE_USB2:
            case BLEBASE_RS232:
            {
                try {
                    bleBasePackage = BleBasePackage.getInstance();
                    bleBasePackage.setBleBaseSerialSafeBuffer(bleBaseSerialSafeBuffer);

                    PinpadInitExtParams pinpadInitExtParams = new PinpadInitExtParams(portType,
                            params==null?null:params.getBleName(),
                            params==null?null:params.getBleAddress(),baudrate);
                    if(params!=null && params.getBleBaseParams()!=null){
                        pinpadInitExtParams.setBleBaseParams(params.getBleBaseParams());
                    }
                    if(params!=null && params.getBleBaseParams() !=null){
                        pinpadInitExtParams.setHostMode(params.getBleBaseParams().isHostMode());
                    }
                    boolean isSucess = bleBasePackage.init(context, pinpadInitExtParams, false);
                    if(isSucess){
                        resultCode = 0;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }catch (Error r){
                    r.printStackTrace();
                }
            }
            break;
        }
        deviceLogger.debug("[open] resultCode: " + resultCode + ", fdpinpad: " + fdpinpad + ", fd232: " + fd232);

        return resultCode;
    }


    @Override
    public String getVersion() {
        String version = "UNKNOWN";
        switch (portType) {
            case RS232:
                if(isN750()){
                    version = mAnalogSerialManager.getVersion();
                    return version;
                }
                version = uart3Manager.getVersion();
                break;
            case PINPAD:
                if (isCPOS() || isN550()) {
                    return uart3Manager.getVersion();
                }
                version = "UNKNOWN";
                break;
            case MIN_USB:
                version = mAnalogSerialManager.getVersion();
                break;
        }
        return version;
    }

    @Override
    public int read(byte[] outputData, final int lengthMax, int timeOut) {

        int dataLength = 0;
        int isSucc = 0;

        int srcTimeOut = timeOut;
        if (timeOut < 1000 && timeOut != 0 && timeOut != PinpadPackage.ECHO_TEST_TIMEOUT_MS) {
            timeOut = 1000;
        }
//        deviceLogger.debug("[read] read start portType=" + portType + " srcTimeOut=" + srcTimeOut + " timeOut=" + timeOut + " lengthMax=" + lengthMax + " UartPortFd=" + mUartPortFd+ " UartPortFd2=" + mUartPortFd2);

        switch (portType) {
            case RS232:
                if(isN750()){
                    dataLength = mAnalogSerialManager.read(outputData, lengthMax, timeOut / 1000);
                    return dataLength;
                }
                if (mUartPortFd < 0) {
                    if ((isCPOS() && NlBuild.VERSION.NL_FIRMWARE.compareTo("V1.0.30") >= 0)) {
                        uart3Manager.update(fd232);
                    }
                    dataLength = uart3Manager.read(outputData, lengthMax, timeOut / 1000);
                } else {
                    dataLength = UartPort.JNI_read(mUartPortFd, outputData, lengthMax, srcTimeOut);
                }
                break;
            case PINPAD:
                if(isU2000()){
                    if (mUartPortFd2 < 0) {
                        //uart3Manager.update(fdpinpad);
                        dataLength = uart3Manager.read(outputData, lengthMax, timeOut / 1000);
                    }else {
                        dataLength = UartPort.JNI_read(mUartPortFd2, outputData, lengthMax, srcTimeOut);
                    }
                } else if (isCPOS() || isN550()) {
                    if (mUartPortFd < 0) {
                        if (isCPOS()) {
                            if (NlBuild.VERSION.NL_FIRMWARE.compareTo("V1.0.30") >= 0) {
                                uart3Manager.update(fdpinpad);
                            }
                            dataLength = uart3Manager.read(outputData, lengthMax, timeOut / 1000);
                        } else if (isN550()) {
                            dataLength = uart3Manager.read(outputData, lengthMax, timeOut / 1000);
                        }
                    } else {
                        dataLength = UartPort.JNI_read(mUartPortFd, outputData, lengthMax, srcTimeOut);
                    }
                } else {
                    int[] readLen = new int[1];
                    isSucc = ndkApiManager.getSerialPort().NDK_PortRead(getNDKPortNum(), lengthMax, outputData, srcTimeOut, readLen);
                    if (isSucc == 0) {
                        dataLength = readLen[0];
                    } else {
                        return 0;
                    }
                }

                break;
            case MIN_USB:
                if(isN750POrP300() && mUartPortFd2 > 0){
                    dataLength = UartPort.JNI_read(mUartPortFd2, outputData, lengthMax, srcTimeOut);
                }else {
                    dataLength = mAnalogSerialManager.read(outputData, lengthMax, timeOut / 1000);
                }
                break;
            case BLEBASE_USB1:
            case BLEBASE_USB2:
            case BLEBASE_RS232:
            {
//                deviceLogger.debug("--------读取蓝牙底座串口数据-----");
                if(portType==PortType.BLEBASE_RS232){
                    long dis = 0;
                    Date start = new Date();
                    int stepTime = 10;
                    int bufferLen = bleBaseSerialSafeBuffer.getLen();
                    while ((bufferLen < lengthMax) && (dis < timeOut)) {
                        bleBaseSerialSafeBuffer.waitRead(stepTime);
                        Date end = new Date();
                        dis = end.getTime() - start.getTime();
                        bufferLen = bleBaseSerialSafeBuffer.getLen();
                    }
                    int len = bleBaseSerialSafeBuffer.read(outputData, lengthMax);
                    if (len > 0) {
                        deviceLogger.info("[read] read portType=" + portType + " len=" + len + " outputData=" + (outputData == null ? "null" : ISOUtils.hexString(outputData)));
                    }
                    return len;
                }else{
                    byte[] data = bleBasePackage.readUSBProtData(srcTimeOut,portType,lengthMax);
                    if(data==null || data.length<=0){
                        return 0;
                    }else{
                        deviceLogger.debug("[read] read len=" + data.length);
                        System.arraycopy(data,0,outputData,0,data.length);
                        return data.length;
                    }
                }
            }
        }
        if (dataLength > 0) {
            deviceLogger.info("[read] read end portType=" + portType + " dataLength=" + dataLength + " outputData=" + (null == outputData ? null : ISOUtils.hexString(outputData)));
        }

        return dataLength;
    }


    @Override
    public int write(byte[] buf, int lengthMax, int timeOut) {
        int dataLength = 0;
        int isSucc = 0;
        int srcTimeOut = timeOut;
        if (timeOut < 1000 && timeOut != 0) {
            timeOut = 1000;
        }
        deviceLogger.info("[write] write start portType=" + portType + " srcTimeOut=" + srcTimeOut + " timeOut=" + timeOut + " lengthMax=" + lengthMax + " inputData=" + (buf == null ? "null" : ISOUtils.hexString(buf)) + " UartPortFd=" + mUartPortFd+ " UartPortFd2=" + mUartPortFd2);
        switch (portType) {
            case RS232:
                if(isN750()){
                    dataLength = mAnalogSerialManager.write(buf, lengthMax, timeOut / 1000);
                    return dataLength;
                }
                if (mUartPortFd < 0) {
                    if ((isCPOS() && NlBuild.VERSION.NL_FIRMWARE.compareTo("V1.0.30") >= 0)) {
                        uart3Manager.update(fd232);
                    }
                    dataLength = uart3Manager.write(buf, lengthMax, timeOut / 1000);
                } else {
                    dataLength = UartPort.JNI_write(mUartPortFd, buf, lengthMax, srcTimeOut);
                }
                break;
            case PINPAD:
                if(isU2000()){
                    if (mUartPortFd2 < 0) {
                        //uart3Manager.update(fdpinpad);
                        dataLength = uart3Manager.write(buf, lengthMax, timeOut / 1000);
                    }else {
                        dataLength = UartPort.JNI_write(mUartPortFd2, buf, lengthMax, srcTimeOut);
                    }
                } else if (isCPOS() || isN550()) {
                    if (mUartPortFd < 0) {
                        if (isCPOS()) {
                            if (NlBuild.VERSION.NL_FIRMWARE.compareTo("V1.0.30") >= 0) {
                                uart3Manager.update(fdpinpad);
                            }
                            dataLength = uart3Manager.write(buf, lengthMax, timeOut / 1000);
                        } else if (isN550()) {
                            dataLength = uart3Manager.write(buf, lengthMax, timeOut / 1000);
                        }
                    } else {
                        dataLength = UartPort.JNI_write(mUartPortFd, buf, lengthMax, srcTimeOut);
                    }
                } else {
                    isSucc = ndkApiManager.getSerialPort().NDK_PortWrite(getNDKPortNum(), lengthMax, buf);
                    if (isSucc == 0) {
                        dataLength = lengthMax;
                    } else {
                        return 0;
                    }

                }

                break;
            case MIN_USB:
                if(isN750POrP300() && mUartPortFd2 > 0){
                    dataLength = UartPort.JNI_write(mUartPortFd2, buf, lengthMax, srcTimeOut);
                }else {
                    dataLength = mAnalogSerialManager.write(buf, lengthMax, timeOut / 1000);
                }
                break;
            case BLEBASE_USB1:
            case BLEBASE_USB2:
            case BLEBASE_RS232:
                dataLength = bleBasePackage.write(buf, lengthMax, timeOut, portType);
                break;
        }
        deviceLogger.info("[write] write end dataLength=" + dataLength);

        return dataLength;
    }

    @Override
    public int close() {
        deviceLogger.debug("[close] portType=" + portType);

        int resultCode = 0;
        switch (portType) {
            case RS232:
                if(isN750()){
                    resultCode = mAnalogSerialManager.close();
                    return resultCode;
                }
                if (mUartPortFd < 0) {
                    if ((isCPOS() && NlBuild.VERSION.NL_FIRMWARE.compareTo("V1.0.30") >= 0)) {
                        uart3Manager.update(fd232);
                    }
                    resultCode = uart3Manager.close();
                } else {
                    resultCode = UartPort.JNI_close(mUartPortFd);
                }
                mUartPortFd = -1;
                break;
            case PINPAD:
                if(isU2000()){
                    if (mUartPortFd2 < 0) {
                        //uart3Manager.update(fdpinpad);
                        resultCode = uart3Manager.close();
                    }else {
                        resultCode = UartPort.JNI_close(mUartPortFd2);
                    }
                } else if (isCPOS() || (isN550())) {
                    if (mUartPortFd < 0) {
                        if (isCPOS()) {
                            if (NlBuild.VERSION.NL_FIRMWARE.compareTo("V1.0.30") >= 0) {
                                uart3Manager.update(fdpinpad);
                            }
                            resultCode = uart3Manager.close();
                        } else if (isN550()) {
                            resultCode = uart3Manager.close();
                        }
                    } else {
                        resultCode = UartPort.JNI_close(mUartPortFd);
                    }
                } else {
                    resultCode = ndkApiManager.getSerialPort().NDK_PortClose(getNDKPortNum());
                }
                mUartPortFd = -1;
                mUartPortFd2 = -1;
                break;
            case MIN_USB:
                if(isN750POrP300() && mUartPortFd2 > 0){
                    resultCode = UartPort.JNI_close(mUartPortFd2);
                }else {
                    resultCode = mAnalogSerialManager.close();
                }
                mUartPortFd2 = -1;
                break;
            case BLEBASE_USB1:
            case BLEBASE_USB2:
            case BLEBASE_RS232:
                if(portType==PortType.BLEBASE_RS232){
                    bleBaseSerialSafeBuffer.clear();
                }else{
                    boolean result = bleBasePackage.clearUSBProtData(portType);
                    if(!result){
                        return -1;
                    }
                }
                break;
        }
        deviceLogger.debug("[close] resultCode:" + resultCode);
        return resultCode;
    }

    @Override
    public boolean clearBuffer(int type) {
        // cmd固定为0x540B, args[0]=0表示清串口输入缓冲,
        // args[0]=1表示清串口输出缓冲,args[0]=2表示清串口输入和输出缓冲
        deviceLogger.debug("[clearBuffer] type:" + type + ";portType:" + portType);

        int resultCode = 0;
        switch (portType) {
            case RS232:
                if (isN750()){
                    resultCode = mAnalogSerialManager.ioctl(0x540B, new byte[]{(byte) type});
                    if (resultCode == 0)
                        return true;
                    return false;
                }
                if (mUartPortFd < 0) {
                    if ((isCPOS() && NlBuild.VERSION.NL_FIRMWARE.compareTo("V1.0.30") >= 0)) {
                        uart3Manager.update(fd232);
                    }
                    resultCode = uart3Manager.ioctl(0x540B, new byte[]{(byte) type});
                } else {
                    resultCode = UartPort.JNI_clearBuf(mUartPortFd, type);
                }
                break;
            case PINPAD:
                if(isU2000()){
                    if (mUartPortFd2 < 0) {
                        //uart3Manager.update(fdpinpad);
                        resultCode = uart3Manager.ioctl(0x540B, new byte[]{(byte) type});
                    }else {
                        resultCode = UartPort.JNI_clearBuf(mUartPortFd2, type);
                    }
                } else if (isCPOS() || isN550()) {
                    if (mUartPortFd < 0) {
                        if (isCPOS() && NlBuild.VERSION.NL_FIRMWARE.compareTo("V1.0.30") >= 0) {
                            uart3Manager.update(fdpinpad);
                        }
                        resultCode = uart3Manager.ioctl(0x540B, new byte[]{(byte) type});

                    } else {
                        resultCode = UartPort.JNI_clearBuf(mUartPortFd, type);
                    }
                } else {
                    resultCode = ndkApiManager.getSerialPort().NDK_PortClrBuf(getNDKPortNum());
                }
                break;
            case MIN_USB:
                if(isN750POrP300() && mUartPortFd2 > 0){
                    resultCode = UartPort.JNI_clearBuf(mUartPortFd2, type);
                }else {
                    resultCode = mAnalogSerialManager.ioctl(0x540B, new byte[]{(byte) type});
                }
                break;
            case BLEBASE_RS232:
            case BLEBASE_USB1:
            case BLEBASE_USB2:
            {
                deviceLogger.debug("--------清空蓝牙底座串口数据-----");
                if(portType==PortType.BLEBASE_RS232){
                    bleBaseSerialSafeBuffer.clear();
                }else{
                    bleBasePackage.clearUSBProtData(portType);
                }
            }
            break;
        }
        deviceLogger.debug("[clearBuffer] ..resultCode:" + resultCode);

        if (resultCode == 0)
            return true;
        return false;
    }

    @Override
    public boolean isBufferEmpty(int type) {
        deviceLogger.debug("[isBufferEmpty]..type:" + type);

        int rlst = 0;
        switch (portType) {
            case RS232:
                if(isN750()){
                    rlst = mAnalogSerialManager.ioctl(0x541B, new byte[]{(byte) type});
                    if (rlst == 0)
                        return true;
                    return false;
                }
                if (mUartPortFd < 0) {
                    if ((isCPOS() && NlBuild.VERSION.NL_FIRMWARE.compareTo("V1.0.30") >= 0)) {
                        uart3Manager.update(fd232);
                    }
                    rlst = uart3Manager.ioctl(0x541B, new byte[]{(byte) type});
                } else {
                    rlst = UartPort.JNI_isBufferEmpty(mUartPortFd, type);
                }
                break;
            case PINPAD:
                // 判断指定串口发送缓冲区是否为空,清K21的串口接收缓存，K21串口没有发送缓存，
                // NDK调用操作的串口都指850密钥键盘的K21外接串口，那个串口发送是实时发送的没有缓存，
                // 密码键盘回传的数据在K21的接收缓存里

                if(isU2000()){
                    if (mUartPortFd2 < 0) {
                        //uart3Manager.update(fdpinpad);
                        rlst = uart3Manager.ioctl(0x541B, new byte[]{(byte) type});
                    }else {
                        rlst = UartPort.JNI_isBufferEmpty(mUartPortFd2, type);
                    }
                } else if (isCPOS() || isN550()) {
                    if (mUartPortFd < 0) {
                        if (isCPOS() && NlBuild.VERSION.NL_FIRMWARE.compareTo("V1.0.30") >= 0) {
                            uart3Manager.update(fdpinpad);
                        }
                        rlst = uart3Manager.ioctl(0x541B, new byte[]{(byte) type});
                    } else {
                        rlst = UartPort.JNI_isBufferEmpty(mUartPortFd, type);
                    }

                } else {
                    rlst = ndkApiManager.getSerialPort().NDK_PortTxSendOver(getNDKPortNum());
                }
                break;
            case MIN_USB:
                if(isN750POrP300() && mUartPortFd2 > 0){
                    rlst = UartPort.JNI_isBufferEmpty(mUartPortFd2, type);
                }else {
                    rlst = mAnalogSerialManager.ioctl(0x541B, new byte[]{(byte) type});
                }
                break;
            case BLEBASE_RS232:
                rlst = bleBaseSerialSafeBuffer.getLen();
                break;
            case BLEBASE_USB1:
                if(bleBasePackage.getBleBaseUSB1SafeBuffer()!=null){
                    rlst = bleBasePackage.getBleBaseUSB1SafeBuffer().getLen();
                }
                break;
            case BLEBASE_USB2:
                if(bleBasePackage.getBleBaseUSB2SafeBuffer()!=null){
                    rlst = bleBasePackage.getBleBaseUSB2SafeBuffer().getLen();
                }
                break;
        }
        deviceLogger.debug("[isBufferEmpty]..rlst:" + rlst);
        if (rlst == 0)
            return true;
        return false;
    }

    @Override
    public int getBufferLength(PortType portType) {
        if((portType == PortType.PINPAD)){
            int[] len = new int[1];
            int ret = ndkApiManager.getSerialPort().NDK_PortReadLen(getNDKPortNum(),len);
            if(ret == 0){
                deviceLogger.debug("[getBufferLength] len:" + len[0]);
                return len[0];
            }
        }
        return -1;
    }


    private boolean isCPOS() {
        if ((Build.MODEL.equals("CPOS X5") || Build.MODEL.equals("CPOS X3") || Build.MODEL.equals("CPOS X1")) || android.os.Build.MODEL.equals("STAR A-6300")
        ) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isU2000() {
        if (Build.MODEL.equals("U2000")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isN750(){
        if(Build.MODEL.equals("N750")){
            return true;
        }else{
            return false;
        }
    }

    private boolean isN750POrP300(){
        if(Build.MODEL.equals("N750P") || Build.MODEL.equals("P300")){
            return true;
        }else{
            return false;
        }
    }

    private boolean isN550() {
        if (Build.MODEL.equals("N550")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isSupportMs0() {
        if (portType == PortType.RS232) {
            if (mUartPortFd < 0) {
                return false;
            } else {
                return true;
            }
        } else if (portType == PortType.PINPAD) {
            if (isCPOS() || isN550()) {
                if (mUartPortFd < 0) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } else if (portType == PortType.MIN_USB) {
            return false;
        }
        return false;
    }


    private Map<String, String> getNodes(){
        final String PATH_USBSERIAL = "/proc/tty/driver/usbserial";
        File fileUsbSerial = new File(PATH_USBSERIAL);
        Map<String, String> nodes = new HashMap<>();
        String content = FileIOUtils.readFileByLines(fileUsbSerial);
        deviceLogger.debug("fileUsbSerial content " + content);
        if (!TextUtils.isEmpty(content)){
            String[] strings = content.split("\n");
            for (String temStr : strings) {
                deviceLogger.debug("temStr: " + temStr);
                if (temStr.trim().endsWith("1.3")) {
                    nodes.put("DB9", "ttyUSB" + temStr.charAt(0));
                } else if (temStr.trim().endsWith("1.4")) {
                    nodes.put("USBTTL", "ttyUSB" + temStr.charAt(0));
                }
            }
        }
        return nodes;
    }

    private EM_PORT_NUM getNDKPortNum(){
        if(Build.MODEL.equals("N950S-C")){
            return PORT_NUM_COM1;
        }else {
            return PORT_NUM_COM2;
        }
    }

    private String getNodeNameFromFile(PortType type){
        try {
            String[] fileContent = new String[3];
            File file = new File("proc/tty/driver/usbserial");
            if (!file.exists()) {
                deviceLogger.error("No available file.");
                return null;
            }
            FileInputStream fio = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fio);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = "";
            int i = 0;
            while ((line = bufferedReader.readLine()) != null) {
                fileContent[i] = line;
                i++;
            }
            if (i < 2) {
                deviceLogger.error("Please pull out USB OTG cable, and call this interface again.");
                return null;
            }
            bufferedReader.close();
            inputStreamReader.close();
            fio.close();
            String nodeNamePrix = "/dev/ttyUSB";
            if (type == PortType.MIN_USB) {
                String rs232NodeNumber = fileContent[2].split(": name:")[0];
                nodeNamePrix += rs232NodeNumber;
            } else if (type == PortType.RS232){
                String usbNodeNumber = fileContent[1].split(": name:")[0];
                nodeNamePrix += usbNodeNumber;
            }
            Log.d("SerialPortManagerImpl", "nodeName:" + nodeNamePrix);
            return nodeNamePrix;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

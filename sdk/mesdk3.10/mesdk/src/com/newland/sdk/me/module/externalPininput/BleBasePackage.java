package com.newland.sdk.me.module.externalPininput;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.newland.NlBluetooth.aidl.OnSearchListener;
import com.newland.NlBluetooth.control.BluetoothController;
import com.newland.NlBluetooth.util.Const;
import com.newland.sdk.me.conn.SimpleDeviceManager;
import com.newland.sdk.me.module.usb.USBSafeBuffer;
import com.newland.sdk.me.utils.PreferenceUtils;
import com.newland.sdk.module.externalPin.BleBaseStatusListener;
import com.newland.sdk.module.externalPin.PinpadInitExtParams;
import com.newland.sdk.module.serialport.Baudrate;
import com.newland.sdk.module.serialport.PinpadModel;
import com.newland.sdk.module.serialport.PortType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.Dump;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.utils.ISOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 蓝牙底座通讯类
 */
public class BleBasePackage {
    private String receivedData = "";
    private PinpadInitExtParams params;
    private boolean isConnectSucess;
    private PortType portType = PortType.BLEBASE_RS232;
    private PinpadModel model = PinpadModel.SP_OVERSEAS;
    private static final byte STX = 0x02;
    private static final byte ETX = 0x03;

    private final byte[] STX_OVERSEAS = new byte[]{0x02};
    private final byte[] ETX_OVERSEAS = new byte[]{0x03};

    private static final byte[] SEPARATOR_SLASH = new byte[]{0x2F}; // "/"

    private static final byte[] SEPARATOR_POINT = new byte[]{0x2E};// "."
    private static final byte ACK = 0x06;

    private static final byte NAK = 0x15;

    private static final int LEN_STX = 1;

    private static final int LEN_SEPARATOR = 1;

    private static final int LEN_MESSAGETYPE = 2;

    private static final int LEN_LENGTH = 2; // 数据长度2字节的bcd

    private static final int LEN_LRC = 1;
    private WaitThreat waitThreat = new WaitThreat();
    private int bleBasePort = -1;
    private static final int LEN_ETX = 1;
    private static BleBasePackage bleBasePackage;
    private Context context;
    private static List<String> ackcmdList = new ArrayList<String>();
    private Handler mainHandler;
    private String serialData;
    private static DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("BleBasePackage");

    private final int PORT = 10;
    private int USB2Type = 0x10001;
    private int USB1Type = 0x01;

    private int USB2DeviceType = 14;
    private int USB1DeviceType = 8;
    private  boolean isUSB2Opened = false;
    private  boolean isUSB1Opened = false;
    private volatile boolean isCancel = false;//true,取消USB1口数据读取，读取USB2口的数据
    private BleBsaeDataRevListener bleBsaeDataRevListener;
    private static Handler cancelHandler;
    private PortType tempPortType = PortType.BLEBASE_USB1;
    private int CancelMsg = -1;//蓝牙搜索页面按返回键
    private int OKMsg = 1;//蓝牙搜索页面，选择蓝牙地址
    private String BLE_NAME = "BLE_NAME";
    private String BLE_ADDRESS = "BLE_ADDRESS";
    private Object USB2Object = new Object();//USB2口读写操作同步处理

    private volatile boolean isCancelBle;//取消蓝牙连接

    public static final int BLE_TYPE_UART = 0;
    public static final int BLE_TYPE_USB = 10;
    private volatile boolean isSingleThreadStrted = false;//读写线程是否已经开启，isSingleChannelThread 这个接口已经弃用，要用回调onThreadState判断
    private USBSafeBuffer bleBaseSerialSafeBuffer,bleBaseUSB1SafeBuffer,bleBaseUSB2SafeBuffer;//蓝牙底座串口缓冲区，存储串口数据
    private BleBaseStatusListener bleBaseStatusListener;

    static {
        //返回报文只包含ack应答的指令
        ackcmdList.add("3343");
        ackcmdList.add("4138");
        ackcmdList.add("3339");
        ackcmdList.add("3336");
        ackcmdList.add("3353");

    }

    public synchronized static BleBasePackage getInstance() {
        devicelogger.debug("--------getInstance--------bleBasePackage:"+bleBasePackage);
        if (bleBasePackage == null) {
            bleBasePackage = new BleBasePackage();
        }
        return bleBasePackage;
    }

    /**
     * 获取蓝牙底座信息
     *
     * @param type         0 - 机器类型, 1 - 支持的硬件类型, 2 - BIOS版本信息, 3 - 机器序列号, 4 - 机器机器号, 5 - 主板号, 6 - 刷卡总数, 7 - 打印总长度, 8 - 开机运行时间, 9 - 按键次数, 10 - CPU类型, 11 - BOOT版本, 12 - BIOS版本补丁号, 13 - 公钥版本信息 14 - 固件版本时间, 15 - 补丁版本时间
     * @param stringBuffer
     * @return
     */
    public int getBleInfo(int type, StringBuffer stringBuffer) {
        return BluetoothController.getInstance().sysGetPosInfo(type, stringBuffer);
    }

    /**
     * 初始化外接键盘
     *
     * @param context
     * @param params
     * @param isSign
     * @return
     */
    public boolean init(final Context context, PinpadInitExtParams params, boolean isSign) {
        try {
            this.context = context;
            this.params = params;
            isCancelBle = false;
            this.mainHandler = new Handler(context.getMainLooper());
            if(params!=null && params.getBleBaseParams()!=null){
                bleBaseStatusListener = params.getBleBaseParams().getBleBaseStatusListener();
            }
            cancelHandler = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    devicelogger.debug("---------cancelHandler [handleMessage],msg.what:"+(msg==null?null:msg.what));
                    if(msg!=null && msg.what==CancelMsg){
                        try {
                            isCancelBle = true;
                            if (waitThreat != null) {
                                waitThreat.notifyThread();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else if(msg!=null && msg.what==OKMsg){
                        String bleName =  PreferenceUtils.getString(context,BLE_NAME);
                        String bleAddress = PreferenceUtils.getString(context,BLE_ADDRESS);
                        devicelogger.debug("连接选择的地址,bleName:"+bleName+";bleAddress:"+bleAddress);
                        BluetoothController.getInstance().startBluetoothConnA(bleName, bleAddress);
                    }
                }
            };
            if (BluetoothController.getInstance() != null && !BluetoothController.getInstance().isBind) {
                devicelogger.error("-----BluetoothController is unbinded---");
                //使用蓝牙底座接外接键盘
                BluetoothController.getInstance().initService();
                BluetoothController.getInstance().init(context, onSearchListener);
                int count = 0;
                while (!BluetoothController.getInstance().isBind) {
                    devicelogger.debug("[init]:isBind:" + BluetoothController.getInstance().isBind);
                    SystemClock.sleep(300);
                    count++;
                    if (count == 10) {
                        devicelogger.error("[init]BluetoothController.getInstance().isBind false, timeout");
                        break;
                    }
                }
            } else {
                devicelogger.debug("-----[init] BluetoothController.getInstance().init---");
                BluetoothController.getInstance().init(context, onSearchListener);
            }
            if (!BluetoothController.getInstance().isBind) {
                devicelogger.error("[init] bind bluetooth base failed-----------");
                return false;
            }

            boolean isConnected = BluetoothController.getInstance().isConnectedA();
            devicelogger.debug("[init] blebase isconnected:" + isConnected);
            if (!isConnected) {
                devicelogger.debug("[init] the com.newland.bluetooth is not connected.");
                String adress = params.getBleAddress();
                String bleName = params.getBleName();
                devicelogger.debug("[init] blebase adress:" + adress + ";bleName:" + bleName);

                if (adress != null && !"".equals(adress.trim())) {
                    devicelogger.debug("[init] the com.newland.bluetooth is not connected, connect to ble.");
                    isConnectSucess = BluetoothController.getInstance().startBluetoothConnA(bleName, adress);

                    waitThreat.waitForRslt(5000);

                    if (!isConnectSucess) {
                        devicelogger.error("[init] startBluetoothConnA failed. return");
                        return false;
                    }
                } else {
                    if((params!=null && params.getBleBaseParams().isStartSeetings()) || params ==null){
                        devicelogger.error("[init] the com.newland.bluetooth is not connected,open com.newland.bluetooth activity to connect.");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
//                            Intent intent = new Intent();
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.setClassName(context, "com.newland.sdk.ble.BluetoothBaseActivity");
//                            context.startActivity(intent);

                                try {
                                    Intent intent =  new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }catch (Exception |Error r){//不是在activity里调用，会崩溃
                                    r.printStackTrace();
                                }
                            }
                        }).start();
                        try {
                            waitThreat.waitForRslt(1200 * 1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            devicelogger.error("[init] exception.");
            return false;
        }
        if(isCancelBle){
            devicelogger.error("cancel bluetooth");
            return false;
        }
        if(params.getBleBaseParams()==null || params.getBleBaseParams().isChangePortType()){
            this.portType = params.getPortType();
        }
        this.model = PinpadModel.SP_OVERSEAS;
        devicelogger.debug("-[init] portType:"+portType+", params.getPortType():"+params.getPortType()+"; baudrate0="+params.getBaudrate());
        if(params.getPortType() == PortType.BLEBASE_RS232 && params.getBaudrate() != null && isConnectSucess ){
            try {
                devicelogger.debug("-[init] btSetTransPort,isSingleThreadStrted:"+isSingleThreadStrted);
                if(isSingleThreadStrted){//开启读写线程，不能设置波特率，要先取消
                   BluetoothController.getInstance().singleCancel();
                   Thread.sleep(100);
                }
                int count = 0;
                while (isSingleThreadStrted && count<3){
                    count++;
                    Thread.sleep(100);
                }
                devicelogger.debug("-[init] btSetTransPort,isSingleThreadStrted:"+isSingleThreadStrted);

                if(!isSingleThreadStrted){
                    String baudrate = params.getBaudrate().toValue()+"";
                    //通讯率和格式串,例"115200,8,N,1",如果只写波特率则缺省为"8,N,1"
                    BluetoothController.getInstance().btSetTransPort(baudrate);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(params!=null && params.getBleBaseParams().isBleBaseLogEnable()){
            BluetoothController.getInstance().setLog(true);//开启调试日志
        }else{
            BluetoothController.getInstance().setLog(false);//关闭调试日志
        }

        devicelogger.debug("------[init] isSingleThreadStrted:"+isSingleThreadStrted+";isUSB2Opened:"+isUSB2Opened+";portType:"+portType);
        isCancel = false;

        if(params!=null && !params.isBleBaseAutoConn()){
            BluetoothController.getInstance().setAutoConnect(false);//设置是否需要重连
        }

        if(params.getPortType() == PortType.BLEBASE_USB2){
            if(bleBaseUSB2SafeBuffer==null){//USB2数据缓冲区
                bleBaseUSB2SafeBuffer = new USBSafeBuffer();
            }
            if(!isUSB2Opened){
                int openRet = -1;
                if(params != null && !params.isHostMode()){
                    String baudrate = Baudrate.BPS115200.toValue()+"";
                    if(params.getBaudrate() != null){
                        baudrate = params.getBaudrate().toValue()+"";
                    }
                    openRet = BluetoothController.getInstance().portOpen(USB2DeviceType, baudrate+",8,N,1");
                    devicelogger.debug( "init: BluetoothController portOpen USB2DeviceType ret= "+openRet);
                }else {
                    openRet = BluetoothController.getInstance().usbOpenPort(USB2Type,0);
                    devicelogger.debug( "init: BluetoothController usbOpenPort USB2Type ret= "+openRet);
                }

                if(openRet>=0){
                    isUSB2Opened = true;
                }else{
                    isUSB2Opened = false;
                    devicelogger.error("-[init]usb2OpenPort failed ");
                    return false;
                }
            }
            devicelogger.debug("-----打开蓝牙底座USB2 口成功-----");
            return true;
        }

        if (params != null && (params.getPortType() == PortType.BLEBASE_USB1)) {
            if(bleBaseUSB1SafeBuffer==null){//USB1数据缓冲区
                bleBaseUSB1SafeBuffer = new USBSafeBuffer();
            }
            if(!isUSB1Opened){
                int openRet = -1;
                if(params != null && !params.isHostMode()){
                    String baudrate = Baudrate.BPS115200.toValue()+"";
                    if(params.getBaudrate() != null){
                        baudrate = params.getBaudrate().toValue()+"";
                    }
                    openRet = BluetoothController.getInstance().portOpen(USB1DeviceType, baudrate+",8,N,1");
                    devicelogger.debug( "init: BluetoothController portOpen USB1DeviceType ret= "+openRet);
                }else {
                    openRet = BluetoothController.getInstance().usbOpenPort(USB1Type,0);
                    devicelogger.debug( "init: BluetoothController usbOpenPort USB1Type ret= "+openRet);
                }
                if(openRet>=0){
                    isUSB1Opened = true;
                }else{
                    isUSB1Opened = false;
                    devicelogger.error("-[init]usb1OpenPort failed ");
                    return false;
                }
            }
        }
        boolean isConnected = BluetoothController.getInstance().isConnectedA();
        devicelogger.debug("[init] after connect blebase isconnected:" + isConnected);
        if(!isConnected){
            return false;
        }
        return true;
    }

    private boolean onlyACKCommand(byte[] messageType) {
        if (null != messageType && ackcmdList.contains(ISOUtils.hexString(messageType))) {
            return true;
        }
        return false;
    }


    /**
     * 国内外发送外接密码键盘命令数据
     *
     * @param data pinpad的命令数据
     * @return
     */
    public byte[] sendPinpadCmd(byte[] messageType, byte[] data, int originalTime, boolean isRead) {
        synchronized (SimpleDeviceManager.externalLock) {
            int result = 0;
            devicelogger.debug("[sendPinpadCmd] messageType=" + (messageType == null ? "null" : ISOUtils.hexString(messageType)) + " data=" + (data == null ? "null" : ISOUtils.hexString(data)) + " originalTime=" + originalTime + " isRead=" + isRead+"port:"+portType);
            try {
                byte[] pack;
                if (model == PinpadModel.SP_OVERSEAS) {
                    pack = makeupOverseas(messageType, data);
                } else {
                    pack = makeup(data);
                }
                int type = 10;
                if(portType == PortType.BLEBASE_RS232){
                    type = 0;
                }else if (portType==PortType.BLEBASE_USB1){
                    type = 10;
                }else if (portType==PortType.BLEBASE_USB2){
                    type = 2; // USB2口
                }
                if(type==10){//usb 口
                    setReceivedData(null);
                    clearUSBProtData(PortType.BLEBASE_USB1);
                }else if (type==0){
                    setSerialtReceivedData(null);
                }else if (portType==PortType.BLEBASE_USB2){
                    clearUSBProtData(PortType.BLEBASE_USB2);// USB2口
                }
                result = write(pack, pack.length, 0, portType);
                if(result<0){
                    devicelogger.error("------[sendPinpadCmd] write failed----");
                    return null;
                }
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                originalTime = originalTime + (3 * 1000);//底座反应比较慢，超时时间设置长些
                byte[] tmp = readUntilTimeout(originalTime, type);
                if(type==10){//usb 口
                    setReceivedData(null);
                }else if (type==0){
                    setSerialtReceivedData(null);
                }

                if (tmp == null || tmp.length <= 0) {
                    devicelogger.error("底座返回数据空");
                    return null;
                }
                if (type==2 || type==10){ // USB2
                    if(tmp!=null && Arrays.equals(tmp,new byte[]{0x06})) {//底座有时候06 和数据分开返回
                        devicelogger.debug("----------after 06------");
//                    setReceivedData(null);
                        if(onlyACKCommand(messageType)){
                            return tmp;
                        }else{
                            tmp = readUntilTimeout(originalTime, type);

                        }
                    }
                }

                int j = 0;
                while (true) {
                    if (onlyACKCommand(messageType) && tmp[j] == ACK) {
                        devicelogger.debug("--onlyACKCommand--" + InnerUtils.hexString(new byte[]{messageType[0], messageType[1]}));
                        devicelogger.debug("--onlyACKCommand---" + tmp[0]);
                        return tmp;
                    }
                    if (tmp[j] == NAK) { // SP100海外版专用
                        devicelogger.debug("----------NAK------");
//                        close();
                        return tmp;
                    }
                    if (tmp[j] == STX) {
                        try {
                            bos.write(tmp[j]);
                        } catch (Exception e) {
                            devicelogger.error("bos write excetion", e);
                            return null;
                        }
                        break;
                    }
                    j = j + 1;
                    if (j >= tmp.length) {
                        devicelogger.error("Read timeout!!!");
                        return null;
                    }
                }
                devicelogger.debug("-------j：" + j);
                byte[] lenB = new byte[2];
                System.arraycopy(tmp, j + 1, lenB, 0, lenB.length);
                devicelogger.debug("read length:" + ISOUtils.hexString(lenB));
                try {
                    bos.write(lenB);
                } catch (IOException e) {
                    devicelogger.error("bos write excetion", e);
                    return null;
                }
                // 从Command ID到ETX的长度
                int len;

                if (model == PinpadModel.SP_OVERSEAS) {
                    len = InnerUtils.bcdToInt(lenB, 0, 4, true);  //海外版sp100返回的长度是十进制的。
                    len = len + 1;//再读一个字节lrc
                } else {
                    len = ((0xFF & lenB[0]) << 8) + (lenB[1] & 0xFF);
                }

                for (int i = 0; i < len + 1; i += 4096) {
                    int needLen = 0;
                    if (len + 1 - i >= 4096) {
                        needLen = 4096;
                    } else {
                        needLen = len + 1 - i;
                    }
                    byte[] tmpData = new byte[needLen];
                    System.arraycopy(tmp, j + 3, tmpData, 0, needLen);

                    try {
                        bos.write(tmpData);
                    } catch (IOException e) {
                        devicelogger.error("bos write excetion", e);
                        return null;
                    }
                }
                byte[] resp = bos.toByteArray();
                if (resp == null) {
                    devicelogger.error("----resp == null------");
                    return null;
                }
                devicelogger.debug("[sendPinpadCmd] Receive data=" + (resp == null ? "null" : InnerUtils.hexString(resp)) + " len=" + resp.length);
                int position = 0;
                for (position = 0; position < resp.length; position++) {
                    if (resp[position] == STX) {
                        break;
                    }
                }
                if (position + 2 + 1 > resp.length) {
                    devicelogger.error("------position + 2 + 1 > resp.length----");
                    return null;
                }

                if (model == PinpadModel.SP_OVERSEAS) {
                    len = InnerUtils.bcdToInt(new byte[]{resp[position + 1], resp[position + 2]}, 0, 4, true);  //海外版sp100返回的长度是十进制的。
                } else {
                    len = ((0xFF & resp[position + 1]) << 8) + (resp[position + 2] & 0xFF);
                }
                if (position + 2 + len + 1 + 1 > resp.length) {
                    devicelogger.error("--------position + 2 + len + 1 + 1 > resp.length-------");
//                    close();
                    return null;
                }
                byte lrc = calcLRC(resp, position + 3, position + 2 + len);
                if (model == PinpadModel.SP_OVERSEAS) {
                    lrc = calcLRC(resp, 1, position + 3 + len);
                }
                if (model == PinpadModel.SP && lrc != resp[position + 2 + len + 1]) {
                    devicelogger.error("SP10 lrc not equal:" + lrc + " " + resp[position + 2 + len + 1]);
//                    close();
                    return null;
                } else if (model == PinpadModel.SP_OVERSEAS && lrc != resp[position + 2 + len + 2]) {
                    devicelogger.error("SP100_OVERSEAS lrc not equal:" + lrc + " " + resp[position + 2 + len + 1]);
//                    close();
                    return null;
                }
                byte[] tmpFinalData = new byte[len];
                System.arraycopy(resp, position + 2 + 1, tmpFinalData, 0, len);
                resp = tmpFinalData;
                devicelogger.debug("[sendPinpadCmd] end!!! resp=" + ISOUtils.hexString(resp));

//                if(resp.equals(ISOUtils.hex2byte("48312F333030"))){
//                    Thread.sleep(50);
//                  if(this.receivedData.equals("48312F333030")){
//                      devicelogger.info("----下电返回2次onDataReceive，清空数据----");
//                      setReceivedData(null);
//                  }
//                }
                return resp;
            } catch (Exception e1) {
                e1.printStackTrace();
                return null;
            }
        }
    }

    //海外版sp100 需要收到0x06响应后才不会一直发送响应数据
    public void getPinpadRspCode() {
        synchronized (SimpleDeviceManager.externalLock) {
            write(new byte[]{0x06}, 1, 0, params.getPortType());
            devicelogger.error("Write ACK");
        }
    }


    public int unblockSendCmd(byte[] messageType, byte[] data) {
        int result = -1;
        devicelogger.debug("unblockSendCmd,messageType:" + messageType + ";data:" + (data == null ? null : ISOUtils.hexString(data)));
        try {
            byte[] pack;
            if (model == PinpadModel.SP_OVERSEAS) {
                pack = makeupOverseas(messageType, data);
            } else {
                pack = makeup(data);
            }
            int type = 10;
            if(portType == PortType.BLEBASE_RS232){
                type = 0;
            }else if (portType==PortType.BLEBASE_USB1){
                type = 10;
            }else if (portType==PortType.BLEBASE_USB2){
                type = 2; // USB2口
            }
            if(type==10){//usb 口
                setReceivedData(null);
            }else{
                setSerialtReceivedData(null);
            }
            result = write(pack, pack.length, 0, params.getPortType());
        } catch (Exception ex) {
            ex.printStackTrace();
            result = -1;
        }
        return result;
    }

    /**
     * 国内外发送外接密码键盘命令数据
     *
     * @param data pinpad的命令数据
     * @return
     */
    public byte[] sendCmd(byte[] messageType, byte[] data, int originalTimeOut) {
        synchronized (SimpleDeviceManager.externalLock) {
            devicelogger.debug("[sendCmd] messageType=" + (messageType == null ? "null" : ISOUtils.hexString(messageType)) + " data=" + (data == null ? "null" : ISOUtils.hexString(data)) + " originalTimeOut=" + originalTimeOut+";port:"+portType);
            int result = 0;
            try {
                devicelogger.debug("send:" + (data == null ? "null" : InnerUtils.hexString(data)));
                if(portType==PortType.BLEBASE_USB1){//usb 口
                    setReceivedData(null);
                }else if (portType==PortType.BLEBASE_RS232){
                    setSerialtReceivedData(null);
                }
                result = write(data, data.length, 0, portType);

                if(result<0){
                    devicelogger.error("[sendCmd] write  failed");

                }
                if (Arrays.equals(data, new byte[]{0x1B, 0x5A, 0x0D, 0x0A})) { //国内版如果是撤销指令 直接取消读取数据，以防冲突
                    devicelogger.error("cancel command.");
                    return null;
                }

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                originalTimeOut = originalTimeOut + (2 * 1000);//底座反应比较慢，超时时间设置长些
                PortType type = params.getPortType();
                int portType = 10;
                if (type == PortType.BLEBASE_RS232) {
                    portType = 0;
                } else if (type==PortType.BLEBASE_USB1){
                    portType = 10;
                } else if (type==PortType.BLEBASE_USB2){
                    portType = 2;
                }
                byte[] tmp = readUntilTimeout(originalTimeOut, portType);
                if(portType==10){//usb 口
                    setReceivedData(null);
                }else{
                    setSerialtReceivedData(null);
                }
                if (tmp != null && Arrays.equals(tmp, new byte[]{0x06})) {//底座有时候06 和数据分开返回
                    if(portType==10){//usb 口
                        setReceivedData(null);
                    }else{
                        setSerialtReceivedData(null);
                    }
//                    tmp = readUntilTimeout(originalTimeOut, 10);
                    tmp = readUntilTimeout(originalTimeOut, portType);
                    if(portType==10){//usb 口
                        setReceivedData(null);
                    }else{
                        setSerialtReceivedData(null);
                    }
                }
                int j = 0;
                while (true) {
                    if (onlyACKCommand(messageType) && tmp[j] == ACK) {
                        devicelogger.debug("--onlyACKCommand--" + InnerUtils.hexString(new byte[]{messageType[0], messageType[1]}));
                        devicelogger.debug("--onlyACKCommand---" + tmp[0]);
                        return tmp;
                    }
                    if (tmp[j] == NAK) { // SP100海外版专用
                        devicelogger.debug("----------NAK------");
//                        close();
                        return tmp;
                    }
                    if (tmp[j] == STX) {
                        try {
                            bos.write(tmp[j]);
                        } catch (Exception e) {
                            devicelogger.error("bos write excetion", e);
                            return null;
                        }
                        break;
                    }
                    j = j + 1;
                    if (j >= tmp.length) {
                        devicelogger.error("Read timeout!!!");
                        return null;
                    }
                }
                byte[] lenB = new byte[2];
                System.arraycopy(tmp, j + 1, lenB, 0, lenB.length);
                try {
                    bos.write(lenB);
                } catch (IOException e) {
                    devicelogger.error("bos write excetion", e);
                    return null;
                }
                // 从Command ID到ETX的长度
                int len;

                if (model == PinpadModel.SP_OVERSEAS) {
                    len = InnerUtils.bcdToInt(lenB, 0, 4, true);  //海外版sp100返回的长度是十进制的。
                    len = len + 1;//再读一个字节lrc
                } else {
                    len = ((0xFF & lenB[0]) << 8) + (lenB[1] & 0xFF);
                }

//                int maxLen = 2 * 1024;
                int maxLen = 400;
                for (int i = 0; i < len + 1; i += maxLen) {
                    int needLen = 0;
                    if (len + 1 - i >= maxLen) {
                        needLen = maxLen;
                    } else {
                        needLen = len + 1 - i;
                    }
                    byte[] tmpData = new byte[needLen];
                    System.arraycopy(tmp, j + 3, tmpData, 0, needLen);

                    try {
                        bos.write(tmpData);
                    } catch (IOException e) {
                        devicelogger.error("bos write excetion", e);
                        return null;
                    }
                }

                byte[] resp = bos.toByteArray();
                if (resp == null) {
                    return null;
                }
                devicelogger.debug("[sendCmd] Receive data=" + (resp == null ? "null" : InnerUtils.hexString(resp)) + " len=" + resp.length);
                devicelogger.debug("[sendCmd] end!!!");
                return resp;
            } catch (Exception e1) {
                e1.printStackTrace();
                return null;
            }
        }

    }

    private int read(byte[] outputData, int lengthMax, int timeOut) {
        devicelogger.debug("[ExtPinPad read] channel  bluetoothbase=" + " lengthMax=" + lengthMax + " timeOut=" + timeOut);
        if (params != null ) {
            if (!BluetoothController.getInstance().isConnectedA()) {
                devicelogger.error("[read]  bleBase isConnectedA fasle");
                return -1;
            }
            PortType type = params.getPortType();
            int portType = BLE_TYPE_UART;
            if (type == PortType.BLEBASE_USB1 || type == PortType.BLEBASE_USB2) {
                portType = BLE_TYPE_USB;
            }
            byte[] data = readUntilTimeout(timeOut, portType);
            devicelogger.debug("[read] bleBase data:" + (data == null ? null : ISOUtils.hexString(data)));
            if (data != null && outputData != null && data.length >= lengthMax) {
                System.arraycopy(data, 0, outputData, 0, data.length);
                devicelogger.debug("[read] bleBase data:" + (outputData == null ? null : ISOUtils.hexString(outputData)));
                return 0;
            }
            return -1;
        }
        return -1;
    }

    public int write(byte[] inputData, int lengthMax, int timeOut,PortType portType) {
//        devicelogger.debug("[ExtPinPad write] channel Bluebase" + " lengthMax=" + lengthMax + " timeOut=" + timeOut+";portType:"+portType);
        int result = -1;
        int retryCount = 1;
        do {
//            devicelogger.debug("[write] data:" + (inputData == null ? null : ISOUtils.hexString(inputData)));
//            devicelogger.debug("[ExtPinPad write] retryCount=" + retryCount);
            if (params != null) {
                if (!BluetoothController.getInstance().isConnectedA()) {
                    devicelogger.error("[write]  bleBase isConnectedA fasle");
                    return -1;
                }
                if (portType ==PortType.BLEBASE_USB1) {
                    if(!isUSB1Opened){
                        devicelogger.debug("[write] isUSB1Opened false,open usb1 ");
                        int openRet = -1;
                        if(params != null && !params.isHostMode()){
                            String baudrate = Baudrate.BPS115200.toValue()+"";
                            if(params.getBaudrate() != null){
                                baudrate = params.getBaudrate().toValue()+"";
                            }
                            openRet = BluetoothController.getInstance().portOpen(USB1DeviceType, baudrate+",8,N,1");
                            devicelogger.debug( "init: BluetoothController portOpen USB1DeviceType ret= "+openRet);
                        }else {
                            openRet = BluetoothController.getInstance().usbOpenPort(USB1Type,0);
                            devicelogger.debug( "init: BluetoothController usbOpenPort USB1Type ret= "+openRet);
                        }
                        if(openRet<0){
                            isUSB1Opened = false;
                            devicelogger.error("-[write]usb1OpenPort failed ");
                            return -1;
                        }else{
                            isUSB1Opened = true;
                        }
                    }
                    if(params != null && !params.isHostMode()){
                        result = BluetoothController.getInstance().portWrite(USB1DeviceType,inputData.length,inputData);
                    }else {
                        result = BluetoothController.getInstance().usbPortWrite(USB1Type,0,inputData.length,inputData);
                    }
                    if(result<0 && retryCount > 0){
                        isUSB1Opened = false;
                    }
                } else if (portType == PortType.BLEBASE_RS232) {
//                    devicelogger.debug("[ExtPinPad write] PINPAD singleSend  start ===");
                    boolean isSucess = BluetoothController.getInstance().sendDataA(inputData);
                    if (!isSucess) {
                        devicelogger.error("-----PINPAD singleSend   failed---");
                        return -1;
                    }
                    result = 0;
                }else if(portType == PortType.BLEBASE_USB2){
                    if(!isUSB2Opened){
                        devicelogger.debug("[write] isUSB2Opened false,open usb2 ");
                        int openRet = -1;
                        if(params != null && !params.isHostMode()){
                            String baudrate = Baudrate.BPS115200.toValue()+"";
                            if(params.getBaudrate() != null){
                                baudrate = params.getBaudrate().toValue()+"";
                            }
                            openRet = BluetoothController.getInstance().portOpen(USB2DeviceType, baudrate+",8,N,1");
                            devicelogger.debug( "init: BluetoothController portOpen USB2DeviceType ret= "+openRet);
                        }else {
                            openRet = BluetoothController.getInstance().usbOpenPort(USB2Type,0);
                            devicelogger.debug( "init: BluetoothController usbOpenPort USB2Type ret= "+openRet);
                        }

                        if(openRet<0){
                            isUSB2Opened = false;
                            devicelogger.error("-[write]usb2OpenPort failed ");
                            return -1;
                        }else{
                            isUSB2Opened = true;
                        }
                    }
                    devicelogger.debug("[ExtPinPad write] 蓝牙底座USB2口 ===");
                    if(params != null && !params.isHostMode()){
                        result = BluetoothController.getInstance().portWrite(USB2DeviceType,inputData.length,inputData);
                    }else {
                        result = BluetoothController.getInstance().usbPortWrite(USB2Type,0,inputData.length,inputData);
                    }
                    devicelogger.debug("---------蓝牙底座USB2口写入结果："+result);
                    if(result<0 && retryCount > 0){
                        isUSB2Opened = false;
                    }
                }

//                devicelogger.debug("[write] write end-");
                if(result>=0){
                    return inputData.length;
                }
            }
        } while (retryCount-- > 0);
        return result;
    }

    /**
     * 组装pinpad报文：STX(0x02) + (2字节长度，长度包含pinpad命令数据长度+3) + 0xC0 + 0x01 + 0x01 +
     * pinpad命令数据data + lrc + ETX(0x03)
     *
     * @param data
     * @return
     */
    private byte[] makeup(byte[] data) {
        byte[] pack = new byte[data.length + 3 + 1 + 2 + 1 + 1];
        pack[0] = STX;

        System.arraycopy(intToB2(data.length + 3), 0, pack, 1, 2);

        System.arraycopy(new byte[]{(byte) 0xC0, 0x01, 0x01}, 0, pack, 3, 3);

        System.arraycopy(data, 0, pack, 6, data.length);

        byte[] lrcData = new byte[data.length + 3];
        System.arraycopy(new byte[]{(byte) 0xC0, 0x01, 0x01}, 0, lrcData, 0, 3);
        System.arraycopy(data, 0, lrcData, 3, data.length);
        byte lrc = calcLRC(lrcData, 0, lrcData.length - 1);
        devicelogger.debug("lrc data：" + (lrcData == null ? "null" : InnerUtils.hexString(lrcData)));
        pack[data.length + 6] = lrc;

        pack[data.length + 7] = ETX;
        return pack;
    }

    /**
     * 将int转成2字节的bcd
     *
     * @param data
     * @return
     */
    public static final byte[] intToB2(int data) {
        byte[] p = new byte[2];
        p[0] = (byte) ((data >> 8) & 0xFF);
        p[1] = (byte) ((data) & 0xFF);
        return p;
    }

    /**
     * 计算lrc，
     *
     * @param data
     * @param start
     * @param end
     * @return
     */
    private static final byte calcLRC(byte[] data, int start, int end) {
        byte lrc = data[start];
        for (int i = start + 1; i <= end; i++) {
            lrc = (byte) ((lrc ^ data[i]) & 0xFF);
        }
        return lrc;
    }

    /**
     * 组装海外版SP100报文：STX(0x02) + (2字节长度) + Message Type + Separator + Message
     * Data +ETX+LRC
     * *
     *
     * @return
     */
    private byte[] makeupOverseas(byte[] messageType, byte[] body) {

        int offset = 0;

        byte[] payload = new byte[LEN_STX + LEN_LENGTH + LEN_MESSAGETYPE + LEN_SEPARATOR + (body == null ? 0 : body.length) + LEN_ETX + LEN_LRC];

        devicelogger.debug("start make request payload...");
        devicelogger.debug("pack up stx[" + Dump.getHexDump(STX_OVERSEAS) + "]");
        System.arraycopy(STX_OVERSEAS, 0, payload, 0, LEN_STX);
        offset += LEN_STX;

        if (body != null) {
            int len = LEN_MESSAGETYPE + LEN_SEPARATOR + body.length;
            byte[] lenbs = InnerUtils.intToBCD(len, LEN_LENGTH * 2, true);
            System.arraycopy(lenbs, 0, payload, offset, LEN_LENGTH);
            devicelogger.debug("pack up len[" + Dump.getHexDump(lenbs) + "]");
            offset += LEN_LENGTH;
        } else {
            int len = LEN_MESSAGETYPE + LEN_SEPARATOR;
            byte[] lenbs = InnerUtils.intToBCD(len, LEN_LENGTH * 2, true);
            System.arraycopy(lenbs, 0, payload, offset, LEN_LENGTH);
            devicelogger.debug("pack up len[" + Dump.getHexDump(lenbs) + "]");
            offset += LEN_LENGTH;
        }

        devicelogger.debug("pack up cmd[" + Dump.getHexDump(messageType) + "]");
        System.arraycopy(messageType, 0, payload, offset, LEN_MESSAGETYPE);
        offset += LEN_MESSAGETYPE;

        devicelogger.debug("pack up signedSymbol[" + Dump.getHexDump(SEPARATOR_SLASH) + "]");
        System.arraycopy(SEPARATOR_SLASH, 0, payload, offset, LEN_SEPARATOR);
        offset += LEN_SEPARATOR;

        if (body != null) {
            devicelogger.debug("pack up body[" + Dump.getHexDump(body) + "]");
            System.arraycopy(body, 0, payload, offset, body.length);
            offset += body.length;
        }

        devicelogger.debug("pack up ETX[" + Dump.getHexDump(ETX_OVERSEAS) + "]");
        System.arraycopy(ETX_OVERSEAS, 0, payload, offset, LEN_ETX);
        offset += LEN_ETX;

        byte[] lrcData = new byte[payload.length - LEN_STX - LEN_LRC];
        System.arraycopy(payload, LEN_STX, lrcData, 0, lrcData.length);
        devicelogger.debug("pack up lrcData[" + Dump.getHexDump(lrcData) + "]");

        byte[] lrc = caculateLRC(lrcData);
        devicelogger.debug("pack up lrc[" + Dump.getHexDump(lrc) + "]");
        System.arraycopy(lrc, 0, payload, offset, LEN_LRC);

        devicelogger.debug("make payload finish...[" + Dump.getHexDump(payload) + "],total len:" + payload.length);
        return payload;
    }

    private byte[] caculateLRC(byte[] payload) {
        int offset = 0;
        byte lrc = payload[0];
        do {
            offset++;
            lrc ^= payload[offset];
        } while (offset < payload.length - 1);

        return new byte[]{lrc};
    }

    /**
     * 获取转换数据 格式转换规则：奇数字节 - 41H 作为一个字节的高位，偶数直接- 41H作为一个字节的低位
     *
     * @param dataSource
     * @param dataLen
     * @return
     */
    protected byte[] getData(byte[] dataSource, int dataLen) {
        if (dataLen > 256) {
            devicelogger.error("the length of data more than 256");
            return null;
        }
        byte pin;
        byte[] rsltData = new byte[dataLen / 2];
        for (int i = 0, j = 0; i < dataLen; i++) {
            pin = (byte) (dataSource[i] - 0x41);
            if (i % 2 == 0)/* 高位 */ {
                rsltData[j] = (byte) (pin << 4);
            } else/* 低位 */ {
                rsltData[j] |= pin & 0x0f;
                j++;
            }
        }
        return rsltData;
    }

    /**
     * 转换数据 格式转换规则: 字节的高四位 + 41H 为一个新的字节, 字节的低四位 + 41H 为一个新的字节.
     *
     * @param dataSource 需要转换的数据
     * @return
     */
    protected byte[] setData(byte[] dataSource, int dataLen) {
        byte[] outData = new byte[dataLen * 2];
        for (int i = 0, j = 0; i < dataLen; i++) {
            outData[j++] = (byte) (((dataSource[i] & 0xf0) >> 4) + 0x41);
            outData[j++] = (byte) ((dataSource[i] & 0x0f) + 0x41);
        }
        return outData;
    }


    /**
     * 字节补位
     *
     * @param sourceBytes 需要补充的byte数组
     * @return byte[] 补充完毕的byte数组
     */
    protected byte[] fillBytes(byte[] sourceBytes) {
        if (sourceBytes == null) {
            return null;
        }
        try {
            int mod = sourceBytes.length % 8;
            if (mod != 0) {
                byte[] sourceFilledBytes = new byte[sourceBytes.length + (8 - mod)];
                System.arraycopy(sourceBytes, 0, sourceFilledBytes, 0, sourceBytes.length);
                for (int i = 0; i < (8 - mod); i++) {
                    sourceFilledBytes[sourceBytes.length + i] = InnerUtils.hex2byte("00")[0];
                }
                return sourceFilledBytes;

            } else {
                return sourceBytes;
            }
        } catch (Exception e) {
            e.printStackTrace();
            devicelogger.error("fillBytes error" + e);
        }
        return null;
    }

    /**
     * 字节补位
     *
     * @param sourceBytes 需要补充的byte数组
     * @return byte[] 补充完毕的byte数组
     */
    protected byte[] fillBytesSM4(byte[] sourceBytes) {
        if (sourceBytes == null) {
            return null;
        }
        try {
            int mod = sourceBytes.length % 16;

            if (mod != 0) {
                byte[] sourceFilledBytes = new byte[sourceBytes.length + (16 - mod)];
                System.arraycopy(sourceBytes, 0, sourceFilledBytes, 0, sourceBytes.length);
                for (int i = 0; i < (16 - mod); i++) {
                    sourceFilledBytes[sourceBytes.length + i] = InnerUtils.hex2byte("00")[0];
                }
                return sourceFilledBytes;

            } else {
                return sourceBytes;
            }
        } catch (Exception e) {
            e.printStackTrace();
            devicelogger.error("fillBytesSM4 error" + e);
        }
        return null;
    }

    /**
     * 异或运算
     *
     * @param hexSource1 操作数1
     * @param hexSource2 操作数2
     * @return byte[] 异或结果(16进制数字符串)
     */
    protected byte[] xor(byte[] hexSource1, byte[] hexSource2) {
        if (hexSource1 == null || hexSource1.length < 1 || hexSource2 == null || hexSource2.length < 1) {
            return null;
        }
        try {
            int length = hexSource1.length;
            byte[] xor = new byte[length];
            for (int i = 0; i < length; i++) {
                xor[i] = (byte) (hexSource1[i] ^ hexSource2[i]);
            }
            return xor;
        } catch (Exception e) {
            e.printStackTrace();
            devicelogger.error("xor error:" + e);
        }
        return null;
    }

    OnSearchListener onSearchListener = new OnSearchListener.Stub() {

        @Override
        public void onDeviceFound(String name, String address) throws RemoteException {
            devicelogger.debug("[OnSearchListener]:[onDeviceFound]: " + "name：" + name + "address：" + address);

        }

        @Override
        public void onFinish() throws RemoteException {
            devicelogger.debug("[OnSearchListener]:[onFinish]: ");
        }

        @Override
        public void onStatusChange(final int newStatus, int oldStatus, String name, String address) throws RemoteException {

            devicelogger.debug("[OnSearchListener]:[onStatusChange]: " + "newStatus：" + newStatus + "旧状态：" + oldStatus + "; waitThreat" + waitThreat);
            if(bleBaseStatusListener!=null ){
                devicelogger.debug("[onSearchListener] onStatusChange callback:"+newStatus);
                bleBaseStatusListener.onStatusChange(newStatus);
            }else{
                devicelogger.debug("[onSearchListener] onStatusChange bleBaseStatusListener==null");
            }
            switch (newStatus) {
                case Const.StatusConst.BLUETOOTH_CONNECTED://连接成功
                    isCancelBle = false;
                    isConnectSucess = true;
                    try {
                        runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "connected", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e){
                        e.printStackTrace();
                    } finally {
                        try {
                            if (waitThreat != null) {
                                waitThreat.notifyThread();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    break;
                case Const.StatusConst.BLUETOOTH_DISCONNECTED:
                    devicelogger.debug("[onSearchListener]:bluetooth disconnected.");
                    try {
                        if (waitThreat != null) {
                            waitThreat.notifyThread();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        devicelogger.debug("------[onSearchListener]isSingleThreadStrted:"+isSingleThreadStrted);
                        if(isSingleThreadStrted){
                            BluetoothController.getInstance().singleCancel();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        isSingleThreadStrted = false;
                        isConnectSucess = false;
                    }
                    runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "disconnect", Toast.LENGTH_SHORT).show();
                        }
                    });

                    break;
            }

        }

        @Override
        public void onDataReceive(final int port, final byte[] data) throws RemoteException {
            devicelogger.debug("[OnSearchListener]:onDataReceive: " + "port：" + port + ";数据长度："+data.length+";data：" + (data == null ? "null" : ISOUtils.hexString(data))+";bleBsaeDataRevListener:"+bleBsaeDataRevListener);
            if(data.length>800){
                byte[] tempData;
                int count = data.length/800;
                for(int i=0;i<count;i++){
                    tempData = new byte[800];
                    System.arraycopy(data,i*800,tempData,0,800);
                    devicelogger.debug("-------tempData:"+(ISOUtils.hexString(tempData)));
                }
                if(data.length>count*800){
                    int finalLen = data.length-count*800;
                    byte[] fianlData = new byte[finalLen];
                    System.arraycopy(data,count*800,fianlData,0,finalLen);
                    devicelogger.debug("------fianlData:"+(ISOUtils.hexString(fianlData)));
                }
            }
            bleBasePort = port;
            if(bleBsaeDataRevListener!=null && (tempPortType == PortType.BLEBASE_USB1) && port==10){
                bleBsaeDataRevListener.onReceive(port,data);
            }
            if(port==0){
                setSerialtReceivedData(data);
                if(bleBaseSerialSafeBuffer!=null){
                    bleBaseSerialSafeBuffer.write(data);
                    bleBaseSerialSafeBuffer.notifyRead();
                }
            }else {
                setReceivedData(data);
                if(bleBaseUSB1SafeBuffer!=null){
                    bleBaseUSB1SafeBuffer.write(data);
                    bleBaseUSB1SafeBuffer.notifyRead();
                }
            }
        }

        @Override
        public void onOpenPortStatus(int i, boolean b) throws RemoteException {
            devicelogger.debug("[OnSearchListener]:[onOpenPortStatus],port:" + i + "; issucess:" + b);
        }

        // startSingleChannelThread开启一个线程，该线程是否在运行
        @Override
        public void onThreadState(boolean b) throws RemoteException {
            isSingleThreadStrted = b;
            devicelogger.debug("[OnSearchListener]:[onThreadState],isSingleThreadStrted:" + isSingleThreadStrted);
        }

        @Override
        public void onPanChannelStatus(boolean b) throws RemoteException {
            devicelogger.debug("[onPanChannelStatus]:" + b);

        }
    };

    private void runOnUIThread(Runnable run) {
        if (mainHandler == null) {
            devicelogger.error("[runOnUIThread]contextHelper should be init first!");
            return;
        }
        try {
            mainHandler.post(run);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 保存USB1口数据
     * @param receivedData
     */
    public void setReceivedData(byte[] receivedData) {
        devicelogger.debug("[setReceivedData]receivedData:" + (receivedData == null ? null : ISOUtils.hexString(receivedData))+";bleBasePort:"+this.portType);
        if (receivedData == null) {
            this.receivedData = "";
        } else {
            this.receivedData = this.receivedData + ISOUtils.hexString(receivedData);
        }
        devicelogger.debug("[setReceivedData]receivedData:" + this.receivedData);

    }

    /**
     * 保存串口的数据
     * @param receivedData
     */
    public void setSerialtReceivedData(byte[] receivedData) {
//        devicelogger.debug("[setSerialtReceivedData]串口 receivedData:" + (receivedData == null ? null : ISOUtils.hexString(receivedData)));

        if (receivedData == null) {
            this.serialData = "";
        } else {
            this.serialData  = this.serialData+ISOUtils.hexString(receivedData);
        }
//        devicelogger.debug("[setSerialtReceivedData]串口 receivedData:" + ISOUtils.hexString(receivedData));

    }

    /**
     * 判断USB1口，键盘响应数据是否结束
     * @return
     */
    private boolean isFinishReceive(int portType,byte[] data) {//
        if(portType == 10 || portType == 2){//USB口
            if(data==null || data.length<=0){
                return false;
            }
            int dataLen = ISOUtils.hexString(data).length();

            if(dataLen==2 && Arrays.equals(data,new byte[]{0x06})){
                return true;

            }
            if (dataLen < 12) {
                return false;
            }
            // 有时候返回的数据不止一个06，索性把06全部去掉
            if (ISOUtils.hexString(data).startsWith("06")) {
                int i = 0;
                for (int j = 0; j < data.length; j++) {
                    if (data[i] == 0x06) {
                        i++;
                    } else {
                        break;
                    }
                }
                byte[] newData = new byte[data.length - i];
                System.arraycopy(data, i, newData, 0, newData.length);
                data = newData;
                dataLen = ISOUtils.hexString(data).length();
            }
            if(ISOUtils.hexString(data).startsWith("06")){
                int len = Integer.valueOf(ISOUtils.hexString(data).substring(4, 8));
                if (dataLen >= 12 + len * 2 ) {
                    return true;
                } else {
                    return false;
                }
            }else if(ISOUtils.hexString(data).startsWith("02")){
                int len = Integer.valueOf(ISOUtils.hexString(data).substring(2, 6));
                if (dataLen >= 10 + len * 2) {
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }else{//串口
            if (TextUtils.isEmpty(this.serialData)) {
                return false;
            }
            int dataLen = this.serialData.length();
            if (dataLen < 12) {
                return false;
            }
            if(serialData.startsWith("06")){
                int len = Integer.valueOf(serialData.substring(4, 8));
                if (dataLen >= 12 + len * 2 ) {
                    return true;
                } else {
                    return false;
                }
            }else if(serialData.startsWith("02")){
                int len = Integer.valueOf(serialData.substring(2, 6));
                if (dataLen >= 10 + len * 2) {
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }

    }

    /**
     * 读取USB1口，外接键盘指令响应数据
     * @param timeout
     * @return
     */
    public byte[] readUntilTimeout(long timeout, int portType) {
        devicelogger.debug("[readUntilTimeout] timeout:" + timeout + ";portType:" + portType + ";currenct portType:" + this.portType+";isCancel:"+isCancel);
        if (portType==2){
           byte[] data = readUSB2ProtData(timeout,PortType.BLEBASE_USB2);
           return data;
        } else if(portType==10){//USB1 口
            byte[] data = readUSB2ProtData(timeout,PortType.BLEBASE_USB1);
            return data;
        }else {
            long startTimeStamp = System.currentTimeMillis();
            long endTimeStamp = System.currentTimeMillis();
            while ((endTimeStamp - startTimeStamp) < timeout) {
                endTimeStamp = System.currentTimeMillis();
                if (isFinishReceive(portType,null) && portType == bleBasePort) {
                    return ISOUtils.hex2byte(this.serialData);//串口
                }
                if(isCancel){
                    devicelogger.error("[readUntilTimeout] isCancel");
                    return null;
                }
/*            if (this.receivedData != null && this.receivedData.length() > 0) {
                return ISOUtils.hex2byte(this.receivedData);
            }*/
            }
            devicelogger.error("[readUntilTimeout] timeout,return null");

        }
        return null;
    }

    /**
     * 读取底座串口数据
     * @param timeout
     * @return
     */
    public byte[] readProtData(long timeout, int portType) {
        devicelogger.debug("[readProtData] timeout:" + timeout + ";portType:" + portType + ";currenct portType:" + bleBasePort);
        long startTimeStamp = System.currentTimeMillis();
        long endTimeStamp = System.currentTimeMillis();
        while ((endTimeStamp - startTimeStamp) < timeout) {
            endTimeStamp = System.currentTimeMillis();
            if (this.serialData != null && !this.serialData.equals("") && portType == BleBasePackage.BLE_TYPE_UART) {
                devicelogger.debug("[readProtData]this.serialData:" + this.serialData);
                return ISOUtils.hex2byte(this.serialData);
            }
            if(this.receivedData != null && !this.receivedData.equals("") && portType == BleBasePackage.BLE_TYPE_USB){
                devicelogger.debug("[readProtData]this.receivedData:" + this.receivedData);
                return ISOUtils.hex2byte(this.receivedData);
            }
        }
        devicelogger.error("[readProtData]"+ ";portType:" + portType + ";currenct portType:" + bleBasePort+"超时" + this.serialData);

        return null;
    }
    /**
     * 读取蓝牙底座USB口数据
     * @param timeout
     * @param portType
     * @return
     */
    public byte[] readUSB2ProtData(long timeout, PortType portType){
        try {
//            BluetoothController.getInstance().setLog(true);
            int usbType = -1;
            if(params!= null && !params.isHostMode()){
                usbType = USB2DeviceType;
                if(portType==PortType.BLEBASE_USB1){
                    usbType = USB1DeviceType;
                }
            }else {
                usbType = USB2Type;
                if(portType==PortType.BLEBASE_USB1){
                    usbType = USB1Type;
                }
            }


            devicelogger.debug("[readUSB2ProtData] 读取蓝牙底座USB口数据---portType:"+portType);
            StringBuffer outLen = new StringBuffer();
            int readLen = 0;
            int ret = -1;
            byte[] outData = null;
            String finalOutData = new String();
            long startTimeStamp = System.currentTimeMillis();
            long endTimeStamp = System.currentTimeMillis();
            while ((endTimeStamp - startTimeStamp) < timeout ) {
                endTimeStamp = System.currentTimeMillis();
                synchronized (USB2Object){
                    if(params!= null && !params.isHostMode()){
                        ret = BluetoothController.getInstance().portReadLen(usbType,outLen);
                    }else {
                        ret = BluetoothController.getInstance().usbPortReadLen(usbType,0,outLen);
                    }
                }
                devicelogger.debug("[readUSB2ProtData] 读取蓝牙底座USB口数据长度结果："+ret);
                if(ret!=0){
                    devicelogger.error("读取蓝牙底座失败，ret:"+ret+";portType:"+portType);
                    if(portType==PortType.BLEBASE_USB1){
                        isUSB1Opened = false;
                    }else{
                        isUSB2Opened = false;
                    }
                    return null;
                }
                readLen = Integer.parseInt(outLen.toString());
                devicelogger.debug("读取数据长度："+readLen);
                if(readLen>0){
                    outData = new byte[readLen];
                    int readRet = -1;
                    synchronized (USB2Object){
                        if(params!= null && !params.isHostMode()){
                            readRet = BluetoothController.getInstance().portRead(usbType,outData.length,(int)timeout,outLen,outData);
                        }else {
                            readRet = BluetoothController.getInstance().usbPortRead(usbType,0,outData.length,(int)timeout,outLen,outData);
                        }
                    }
                    if(readRet!=0){
                        devicelogger.error("--------读取底座USB口数据usbPortRead失败："+readRet+";portType:"+portType);
                        return null;
                    }
                    devicelogger.debug("--------读取底座USB口数据："+(outData==null?null:ISOUtils.hexString(outData)));
                    finalOutData = finalOutData + ISOUtils.hexString(outData);

                    boolean isFinish = isFinishReceive(10,ISOUtils.hex2byte(finalOutData));
                    devicelogger.debug("读取底座USB口数据,键盘指令响应数据是否结束："+isFinish);
                    if(isFinish){
                        break;
                    }else{
                        outLen = new StringBuffer();
                    }
                }
                if(isCancel){
                    devicelogger.error("--------取消读取----");
                    break;
                }
                Thread.sleep(10);
            }
            if(finalOutData.length()>0){
                devicelogger.debug("--------读取底座USB口最终数据："+finalOutData);
                return ISOUtils.hex2byte(finalOutData);
            }
            devicelogger.error("读取底座USB口读取数据长度为0,"+";portType:"+portType);
            return null;
        }catch (Exception e){
            e.printStackTrace();
        }catch (Error e){
            e.printStackTrace();
        }
       return null;
    }


    /**
     * 读取蓝牙底座USB口串口数据
     * @param timeout 超时时间
     * @param portType 端口
     * @param lengthMax 期望的最大长度
     * @return
     */
    public byte[] readUSBProtData(long timeout, PortType portType,int lengthMax){
        try {
            int usbType;

            int bufferLen = 0;
            if(portType==PortType.BLEBASE_USB1){
                if(params != null && !params.isHostMode()){
                    usbType = USB1DeviceType;
                }else {
                    usbType = USB1Type;
                }
                if(bleBaseUSB1SafeBuffer==null){
                    bleBaseUSB1SafeBuffer = new USBSafeBuffer();
                }
                bufferLen = bleBaseUSB1SafeBuffer.getLen();
            }else{
                if(params != null && !params.isHostMode()){
                    usbType = USB2DeviceType;
                }else {
                    usbType = USB2Type;
                }
                if(bleBaseUSB2SafeBuffer==null){
                    bleBaseUSB2SafeBuffer = new USBSafeBuffer();
                }
                bufferLen = bleBaseUSB2SafeBuffer.getLen();
            }
            devicelogger.debug("------[readUSBProtData] 读取蓝牙底座USB口数据----portType:"+portType+";lengthMax:"+lengthMax);
            StringBuffer outLen = new StringBuffer();
            int readLen = 0;
            int ret = -1;
            byte[] outData = null;
            long startTimeStamp = System.currentTimeMillis();
            long endTimeStamp = System.currentTimeMillis();
            while ((endTimeStamp - startTimeStamp) < timeout && bufferLen<lengthMax) {
                endTimeStamp = System.currentTimeMillis();
                synchronized (USB2Object){
                    outLen = new StringBuffer();
                    if(params!= null && !params.isHostMode()){
                        ret = BluetoothController.getInstance().portReadLen(usbType,outLen);
                    }else {
                        ret = BluetoothController.getInstance().usbPortReadLen(usbType,0,outLen);
                    }
                }
                devicelogger.debug("------[readUSBProtData] 读取蓝牙底座USB口数据长度结果："+ret);
                if(ret!=0){
                    devicelogger.error("-[readUSBProtData]读取蓝牙底座失败，ret:"+ret+";portType:"+portType);
                    if(portType==PortType.BLEBASE_USB1){
                        isUSB1Opened = false;
                    }else{
                        isUSB2Opened = false;
                    }
                    continue;
                }
                readLen = Integer.parseInt(outLen.toString());
                if(readLen>0){
                    outData = new byte[readLen];
                    int readRet = -1;
                    synchronized (USB2Object){
                        if(params!= null && !params.isHostMode()) {
                            readRet = BluetoothController.getInstance().portRead(usbType,  outData.length, (int) timeout, outLen, outData);
                        }else {
                            readRet = BluetoothController.getInstance().usbPortRead(usbType, 0, outData.length, (int) timeout, outLen, outData);
                        }
                    }
                    if(readRet!=0){
                        devicelogger.debug("[readUSBProtData]读取底座USB口数据失败："+readRet);
                        continue;
                    }
                    if(portType==PortType.BLEBASE_USB1){
                        bleBaseUSB1SafeBuffer.write(outData);
                        bleBaseUSB1SafeBuffer.notifyRead();
                    }else{
                        bleBaseUSB2SafeBuffer.write(outData);
                        bleBaseUSB2SafeBuffer.notifyRead();
                    }
                    if(portType==PortType.BLEBASE_USB2) {
                        bufferLen = bleBaseUSB2SafeBuffer.getLen();
                    }
                    if(portType==PortType.BLEBASE_USB1){
                        bufferLen = bleBaseUSB1SafeBuffer.getLen();
                    }
                }
                if(isCancel){
                    devicelogger.error("[readUSBProtData]取消读取----");
                    break;
                }
                Thread.sleep(10);
            }
            byte[] outputData = new byte[lengthMax];
            int outputLen = 0;
            if(portType==PortType.BLEBASE_USB1){
                outputLen = bleBaseUSB1SafeBuffer.read(outputData, lengthMax);
            }else{
                outputLen = bleBaseUSB2SafeBuffer.read(outputData, lengthMax);
            }
            if(outputLen>0){
                byte[] tempData = new byte[outputLen];
                System.arraycopy(outputData,0,tempData,0,outputLen);
                devicelogger.debug("[readUSBProtData]outputData:"+ISOUtils.hexString(tempData));
                return tempData;
            }
            devicelogger.error("[readUSBProtData]读取数据长度为0");
            return null;
        }catch (Exception e){
            e.printStackTrace();
        }catch (Error e){
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 清除蓝牙底座USB口串口缓冲数据
     * @param portType
     * @return
     */
    public boolean clearUSBProtData(PortType portType){
        try {
            devicelogger.debug("[clearUSBProtData]portType:"+portType);
            int ret;
            if(portType==PortType.BLEBASE_USB1){
                bleBaseUSB1SafeBuffer.clear();
                if(params != null && !params.isHostMode()){
                    ret = BluetoothController.getInstance().portClrBuf(USB1DeviceType);
                }else {
                    ret = BluetoothController.getInstance().usbPortClrBuf(USB1Type,0);
                }
            }else{
                bleBaseUSB2SafeBuffer.clear();
                if(params != null && !params.isHostMode()){
                    ret = BluetoothController.getInstance().portClrBuf(USB2DeviceType);
                }else {
                    ret = BluetoothController.getInstance().usbPortClrBuf(USB2Type,0);
                }

            }
            devicelogger.debug("[clearUSBProtData]ret:"+ret);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }



    public PinpadModel getModel() {
        devicelogger.debug("ble base getModel:" + model);
        return model;
    }

    public void setModel(PinpadModel model) {
        this.model = model;
    }

    // thread wait 、awake
    public static class WaitThreat {
        Object syncObj = new Object();

        public void waitForRslt(int timeout) throws InterruptedException {
            synchronized (syncObj) {
                syncObj.wait(timeout);
            }
        }

        public void notifyThread() {
            synchronized (syncObj) {
                syncObj.notify();
            }
        }
    }

    public OnSearchListener getOnSearchListener() {
        return onSearchListener;
    }

    public void setOnSearchListener(OnSearchListener onSearchListener) {
        this.onSearchListener = onSearchListener;
    }

    /**
     * 设置USB2口取消
     * @param isCancel
     */
    public void setCance(boolean isCancel){
        devicelogger.debug("------setCance:"+isCancel);
        this.isCancel = isCancel;
    }


    public void seBleBsaeDataRevListener(PortType portType,BleBsaeDataRevListener listener){
        devicelogger.debug("----seBleBsaeDataRevListener-----portType:"+portType);
        this.bleBsaeDataRevListener = listener;
        tempPortType = portType;
    }

    public static Handler getCancelHandler() {
        return cancelHandler;
    }

    public static void setBleBasePackage(BleBasePackage bleBasePackage) {
        BleBasePackage.bleBasePackage = bleBasePackage;
    }

    public void close(){
        try {
            if(bleBasePackage != null){
                bleBasePackage.setReceivedData(null);
                bleBasePackage.setSerialtReceivedData(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBleBaseSerialSafeBuffer(USBSafeBuffer bleBaseSerialSafeBuffer) {
        this.bleBaseSerialSafeBuffer = bleBaseSerialSafeBuffer;
    }

    public USBSafeBuffer getBleBaseUSB1SafeBuffer() {
        return bleBaseUSB1SafeBuffer;
    }

    public USBSafeBuffer getBleBaseUSB2SafeBuffer() {
        return bleBaseUSB2SafeBuffer;
    }




    //电子签名板子
    public byte[] boardTxn(byte[] messageType, byte functionId, byte[] data, int mTimeOut) {
        try {
            devicelogger.debug("[boardTxn] messageType=" + (messageType == null ? "null" : new String(messageType))
                    + " functionID=" + Integer.toHexString(functionId & 0xFF).toUpperCase()
                    + " data=" + (data == null ? "null" : InnerUtils.hexString(data)) + " mTimeOut=" + mTimeOut);

            int result = 0;
            byte[] pack=new byte[]{};
            if (model == PinpadModel.SP_OVERSEAS) {
                if (functionId == 0x00) {
                    pack = boardPackOversea(messageType, data);
                } else {
                    pack = boardPackOversea(messageType, functionId, data);
                }
            } else {
                pack = boardPack(functionId, data);
            }
            int type = 10;
            if(portType == PortType.BLEBASE_RS232){
                type = 0;
            }else if (portType==PortType.BLEBASE_USB1){
                type = 10;
            }else if (portType==PortType.BLEBASE_USB2){
                type = 2; // USB2口
            }
            if(type==10){//usb 口
                setReceivedData(null);
            }else{
                setSerialtReceivedData(null);
            }
            result = write(pack, pack.length, 0, portType);
            if(result<0){
                devicelogger.error("------[sendPinpadCmd] write failed----");
                return null;
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            mTimeOut = mTimeOut + (3 * 1000);//底座反应比较慢，超时时间设置长些
            byte[] tmp = readUntilTimeout(mTimeOut, type);
            devicelogger.debug("tmp:"+(tmp==null?null:ISOUtils.hexString(tmp)));
            //0602002853312FA030300156312E302E3031325034393330303030303036320103C8
            if(type==10){//usb 口
                setReceivedData(null);
            }else if (type==0){
                setSerialtReceivedData(null);
            }
            if (type==2 || type==10){ // USB2
                if(tmp!=null && Arrays.equals(tmp,new byte[]{0x06})) {//底座有时候06 和数据分开返回
                    devicelogger.debug("----------after 06------");
                    if(onlyACKCommand(messageType)){
                        return tmp;
                    }else{
                        tmp = readUntilTimeout(mTimeOut, type);
                    }
                }
            }
            int j = 0;
            while (true) {
                if (onlyACKCommand(messageType) && tmp[0] == ACK) {
                    devicelogger.debug("--onlyACKCommand--" + InnerUtils.hexString(new byte[]{messageType[0], messageType[1]}));
                    devicelogger.debug("--onlyACKCommand---" + tmp[0]);
                    return tmp;
                }

                if (tmp[j] == NAK) { // SP100海外版专用
                    return tmp;
                }
                if (tmp[j] == STX) {
                    try {
                       // bos.write(tmp);
                        bos.write(tmp[j]);
                    } catch (Exception e) {
                        devicelogger.error("bos write excetion", e);
                        return null;
                    }
                    break;
                }
                j = j + 1;
                if (j >= tmp.length) {
                    devicelogger.error("Read timeout!!!");
                    return null;
                }
            }
            byte[] lenB = new byte[2];
            System.arraycopy(tmp, j + 1, lenB, 0, lenB.length);
            devicelogger.debug("--lenB--" +(lenB==null?null:InnerUtils.hexString(lenB)) );
            try {
                bos.write(lenB);
            } catch (IOException e) {
                devicelogger.error("bos write excetion", e);
                return null;
            }
            // 从Command ID到ETX的长度
            int len;

            if (model == PinpadModel.SP_OVERSEAS) {
                len = InnerUtils.bcdToInt(lenB, 0, 4, true);  //海外版sp100返回的长度是十进制的。
                len = len + 1;//再读一个字节lrc
            } else {
                len = ((0xFF & lenB[0]) << 8) + (lenB[1] & 0xFF);
            }
            devicelogger.error("len"+len);

            for (int i = 0; i < len + 1; i += 4096) {
                int needLen = 0;
                if (len + 1 - i >= 4096) {
                    needLen = 4096;
                } else {
                    needLen = len + 1 - i;
                }
                byte[] tmpData = new byte[needLen];
                System.arraycopy(tmp, j + 3, tmpData, 0, needLen);
                try {
                    bos.write(tmpData);
                } catch (IOException e) {
                    devicelogger.error("bos write excetion", e);
                    return null;
                }
            }
            byte[] resp = bos.toByteArray();
            if (resp == null) {
                return null;
            }
            devicelogger.debug("[boardTxn] Receive data=" + (resp == null ? "null" : InnerUtils.hexString(resp)) + " len=" + resp.length);
            int position = 0;
            for (position = 0; position < resp.length; position++) {
                if (resp[position] == STX) {
                    break;
                }
            }
            if (position + 2 + 1 > resp.length) {
                return null;
            }

            if (model == PinpadModel.SP_OVERSEAS) {
                //海外版sp100返回的长度是十进制的。
                len = InnerUtils.bcdToInt(new byte[]{resp[position + 1], resp[position + 2]}, 0, 4, true);
            } else {
                len = ((0xFF & resp[position + 1]) << 8) + (resp[position + 2] & 0xFF);
            }
            if (position + 2 + len + 1 + 1 > resp.length) {
                return null;
            }
            devicelogger.debug("position:" + position + ", len：" + len);
            byte lrc;
            if (model == PinpadModel.SP_OVERSEAS) {
                lrc = calcLRC(resp, 1, position + 3 + len);
                devicelogger.debug("SP_OVERSEAS resp lrc:" + InnerUtils.hexString(new byte[]{resp[position + 2 + len + 2]}));
            } else {
                lrc = calcLRC(resp, position + 1, position + 2 + len);  //校验
                devicelogger.debug(" sp resp lrc:" + InnerUtils.hexString(new byte[]{resp[position + 2 + len + 1]}));
            }

            devicelogger.debug("cal lrc:" + InnerUtils.hexString(new byte[]{lrc}));

            if (model == PinpadModel.SP && lrc != resp[position + 2 + len + 1]) {
                devicelogger.error("SP lrc not equal:" + lrc + " " + resp[position + 2 + len + 1]);
                return null;
            } else if (model == PinpadModel.SP_OVERSEAS && lrc != resp[position + 2 + len + 2]) {
                devicelogger.error("SP_OVERSEAS lrc not equal:" + InnerUtils.hexString(new byte[]{lrc}) + ", " + InnerUtils.hexString(new byte[]{resp[position + 2 + len + 2]}));
                return null;
            }
            byte[] tmpFinalData = new byte[len];
            System.arraycopy(resp, position + 2 + 1, tmpFinalData, 0, len);
            resp = tmpFinalData;

            devicelogger.debug("[boardTxn] end!!!");
            return resp;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 组装海外版签名板报文：STX(0x02) + (2字节长度) + Message Type + Separator + Message Data + ETX+ LRC
     *
     * @return
     */
    private byte[] boardPackOversea(byte[] messageType, byte functionId, byte[] data) {
        byte[] pack = new byte[1 + 2 + 2 + 1 + 1 + (data == null ? 0 : data.length) + 1 + 1];
        pack[0] = STX;
        int len;
        if (data != null) {
            len = 2 + 1 + 1 + data.length;
        } else {
            len = 2 + 1 + 1;
        }
        byte[] lenbs = InnerUtils.intToBCD(len, 2 * 2, true);
        System.arraycopy(lenbs, 0, pack, 1, 2);
        devicelogger.debug("pack len = " + InnerUtils.hexString(lenbs));

        System.arraycopy(messageType, 0, pack, 3, messageType.length);
        pack[5] = 0x2F;
        pack[6] = functionId;
        System.arraycopy(data, 0, pack, 7, data.length);
        pack[data.length + 7] = ETX;
        byte lrc = calcLRC(pack, 1, data.length + 7);
        pack[data.length + 8] = lrc;
        return pack;
    }

    private byte[] boardPackOversea(byte[] messageType, byte[] data) {
        byte[] pack = new byte[1 + 2 + 2 + 1 + (data == null ? 0 : data.length) + 1 + 1];
        pack[0] = STX;
        int len;
        if (data != null) {
            len = 2 + 1 + data.length;
        } else {
            len = 2 + 1;
        }
        byte[] lenbs = InnerUtils.intToBCD(len, 2 * 2, true);
        System.arraycopy(lenbs, 0, pack, 1, 2);
        devicelogger.debug("pack len = " + InnerUtils.hexString(lenbs));

        System.arraycopy(messageType, 0, pack, 3, messageType.length);
        pack[5] = 0x2F;
        System.arraycopy(data, 0, pack, 6, data.length);
        pack[data.length + 6] = ETX;
        byte lrc = calcLRC(pack, 1, data.length + 6);
        pack[data.length + 7] = lrc;
        return pack;
    }

    private byte[] boardPack(byte command, byte[] data) {
        devicelogger.debug("-----boardPack------");
        byte[] pack = new byte[data.length + 1 + 2 + 1 + 1 + 1];
        pack[0] = STX;
        System.arraycopy(intToB2(data.length + 2), 0, pack, 1, 2);
        pack[3] = command;
        System.arraycopy(data, 0, pack, 4, data.length);
        pack[data.length + 4] = ETX;
        byte lrc = calcLRC(pack, 1, data.length + 4);
        pack[data.length + 5] = lrc;
        return pack;
    }


}

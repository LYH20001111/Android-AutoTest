package com.newland.sdk.me.module.usb.usbserial;

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbRequest;
import android.util.Log;

import com.newland.sdk.me.module.usb.usbserial.utils.SafeUsbRequest;
import com.newland.sdk.me.module.usb.usbserial.utils.Utils;

public class CDCSerialDevice extends UsbSerialDevice
{
    private static final String CLASS_ID = CDCSerialDevice.class.getSimpleName();

    private static final int CDC_REQTYPE_HOST2DEVICE = 0x21;
    private static final int CDC_REQTYPE_DEVICE2HOST = 0xA1;

    private static final int CDC_SET_LINE_CODING = 0x20;
    private static final int CDC_GET_LINE_CODING = 0x21;
    private static final int CDC_SET_CONTROL_LINE_STATE = 0x22;

    private static final int CDC_SET_CONTROL_LINE_STATE_RTS = 0x2;
    private static final int CDC_SET_CONTROL_LINE_STATE_DTR = 0x1;

    public static final int CDC_VID_05C6 = 0x05C6;
    public static final int CDC_PID_9102 = 0x9102;
    public static final int CDC_PID_9120 = 0x9120;

    public static final int CDC_VID_1E0E = 0x1E0E;
    public static final int CDC_PID_902E = 0x902E;//dev-4
    public static final int CDC_PID_9031 = 0x9031;//2

    /***
     *  Default Serial Configuration
     *  Baud rate: 115200
     *  Data bits: 8
     *  Stop bits: 1
     *  Parity: None
     *  Flow Control: Off
     */
    private static final byte[] CDC_DEFAULT_LINE_CODING = new byte[] {
            (byte) 0x00, // Offset 0:4 dwDTERate
            (byte) 0xC2,
            (byte) 0x01,
            (byte) 0x00,
            (byte) 0x00, // Offset 5 bCharFormat (1 Stop bit)
            (byte) 0x00, // bParityType (None)
            (byte) 0x08  // bDataBits (8)
    };

    private static final int CDC_CONTROL_LINE_ON = 0x0003;
    private static final int CDC_CONTROL_LINE_OFF = 0x0000;

    private final UsbInterface mInterface;
    private UsbEndpoint inEndpoint;
    private UsbEndpoint outEndpoint;

    private int initialBaudRate = 0;

    private int controlLineState = CDC_CONTROL_LINE_ON;

    public CDCSerialDevice(UsbDevice device, UsbDeviceConnection connection)
    {
        this(device, connection, -1);
    }

    public CDCSerialDevice(UsbDevice device, UsbDeviceConnection connection, int iface)
    {
        super(device, connection);
        mInterface = device.getInterface(iface >= 0 ? iface : findFirstCDC(device));
    }

    @Override
    public void setInitialBaudRate(int initialBaudRate) {
        this.initialBaudRate = initialBaudRate;
    }

    @Override
    public int getInitialBaudRate() {
        return initialBaudRate;
    }

    @Override
    public boolean open()
    {
        boolean ret = openCDC();

        if(ret)
        {
            // Initialize UsbRequest
            UsbRequest requestIN = new SafeUsbRequest();
            requestIN.initialize(connection, inEndpoint);

            // Restart the working thread if it has been killed before and  get and claim interface
            restartWorkingThread();
            restartWriteThread();

            // Pass references to the threads
            setThreadsParams(requestIN, outEndpoint);

            asyncMode = true;
            isOpen = true;

            return true;
        }else
        {
            isOpen = false;
            return false;
        }
    }

    @Override
    public void close()
    {
        setControlCommand(CDC_SET_CONTROL_LINE_STATE, CDC_CONTROL_LINE_OFF, null);
        killWorkingThread();
        killWriteThread();
        connection.releaseInterface(mInterface);
        connection.close();
        isOpen = false;
    }

    @Override
    public boolean syncOpen()
    {
        boolean ret = openCDC();
        if(ret)
        {
            setSyncParams(inEndpoint, outEndpoint);
            asyncMode = false;
            isOpen = true;

            // Init Streams
            inputStream = new SerialInputStream(this);
            outputStream = new SerialOutputStream(this);

            startReadThread2();
            setUsbEndpoint(inEndpoint);
            return true;
        }else
        {
            isOpen = false;
            return false;
        }
    }

    @Override
    public void syncClose()
    {
        setControlCommand(CDC_SET_CONTROL_LINE_STATE, CDC_CONTROL_LINE_OFF, null);
        killReadThread2();
        connection.releaseInterface(mInterface);
        connection.close();
        isOpen = false;
    }

    private ReadThread2 readThread2;
    private void startReadThread2()
    {
        Utils.d("CDCSerialDevice startReadThread2");
        readThread2 = new ReadThread2();
        readThread2.start();
        while(!readThread2.isAlive()){} // Busy waiting
    }
    private void setUsbEndpoint(UsbEndpoint inEndpoint){
        readThread2.setUsbEndpoint(inEndpoint);
    }
    public void setCallback(UsbReadCallback callback){
        Utils.d("CDCSerialDevice startReadThread2 setCallback "+readThread2);
        if(readThread2 == null){
            return;
        }
        if(readThread2 != null){
            readThread2.setCallback(callback);
        }
    }
    private class ReadThread2 extends AbstractWorkerThread
    {

        private UsbReadCallback callback;
        private UsbEndpoint inEndpoint;

        public void setCallback(UsbReadCallback callback)
        {
            this.callback = callback;
        }

        public void setUsbEndpoint(UsbEndpoint inEndpoint)
        {
            this.inEndpoint = inEndpoint;
        }

        @Override
        public void doRun()
        {
            byte[] dataReceived = null;
            int numberBytes;
            if(inEndpoint != null){
                long startTime = System.currentTimeMillis();
                byte[] readBuffer = new byte[SerialBuffer.DEFAULT_READ_BUFFER_SIZE];
                numberBytes = connection.bulkTransfer(inEndpoint, readBuffer,
                        SerialBuffer.DEFAULT_READ_BUFFER_SIZE, 0);
                long endTime = System.currentTimeMillis();
                Utils.d("CDCSerialDevice ReadThread2 bulkTransfer2 len="+numberBytes+" time="+(endTime-startTime));

                if(numberBytes >0 ){
                    byte[] targetBuffer = new byte[numberBytes];
                    System.arraycopy(readBuffer,0,targetBuffer,0,numberBytes);
                    serialBuffer.getReadBuffer().clear();
                    serialBuffer.getReadBuffer().put(targetBuffer);
                }
            } else{
                numberBytes = 0;
                Utils.d("CDCSerialDevice ReadThread2 bulkTransfer end numberBytes==0");
            }

            if(numberBytes > 0)
            {
                dataReceived = serialBuffer.getDataReceived();
//                Utils.d("CDCSerialDevice dataReceived="+ ISOUtils.hexString(dataReceived));
                onReceivedData(dataReceived);
            }
        }

        private void onReceivedData(byte[] data)
        {
            if(callback != null)
                callback.onReceivedData(data);
        }
    }

    private void killReadThread2()
    {
        Utils.d("CDCSerialDevice killReadThread2 readThread2="+readThread2);
        if(readThread2 != null) {
            readThread2.stopThread();
            readThread2 = null;
        }
    }

    @Override
    public void setBaudRate(int baudRate)
    {
        byte[] data = getLineCoding();

        data[0] = (byte) (baudRate & 0xff);
        data[1] = (byte) (baudRate >> 8 & 0xff);
        data[2] = (byte) (baudRate >> 16 & 0xff);
        data[3] = (byte) (baudRate >> 24 & 0xff);

        setControlCommand(CDC_SET_LINE_CODING, 0, data);
    }

    @Override
    public void setDataBits(int dataBits)
    {
        byte[] data = getLineCoding();
        switch(dataBits)
        {
            case UsbSerialInterface.DATA_BITS_5:
                data[6] = 0x05;
                break;
            case UsbSerialInterface.DATA_BITS_6:
                data[6] = 0x06;
                break;
            case UsbSerialInterface.DATA_BITS_7:
                data[6] = 0x07;
                break;
            case UsbSerialInterface.DATA_BITS_8:
                data[6] = 0x08;
                break;
            default:
                return;
        }

        setControlCommand(CDC_SET_LINE_CODING, 0, data);

    }

    @Override
    public void setStopBits(int stopBits)
    {
        byte[] data = getLineCoding();
        switch(stopBits)
        {
            case UsbSerialInterface.STOP_BITS_1:
                data[4] = 0x00;
                break;
            case UsbSerialInterface.STOP_BITS_15:
                data[4] = 0x01;
                break;
            case UsbSerialInterface.STOP_BITS_2:
                data[4] = 0x02;
                break;
            default:
                return;
        }

        setControlCommand(CDC_SET_LINE_CODING, 0, data);


    }

    @Override
    public void setParity(int parity)
    {
        byte[] data = getLineCoding();
        switch(parity)
        {
            case UsbSerialInterface.PARITY_NONE:
                data[5] = 0x00;
                break;
            case UsbSerialInterface.PARITY_ODD:
                data[5] = 0x01;
                break;
            case UsbSerialInterface.PARITY_EVEN:
                data[5] = 0x02;
                break;
            case UsbSerialInterface.PARITY_MARK:
                data[5] = 0x03;
                break;
            case UsbSerialInterface.PARITY_SPACE:
                data[5] = 0x04;
                break;
            default:
                return;
        }

        setControlCommand(CDC_SET_LINE_CODING, 0, data);

    }

    @Override
    public void setFlowControl(int flowControl)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBreak(boolean state)
    {
        //TODO
    }

    @Override
    public void setRTS(boolean state)
    {
        if (state)
            controlLineState |= CDC_SET_CONTROL_LINE_STATE_RTS;
        else
            controlLineState &= ~CDC_SET_CONTROL_LINE_STATE_RTS;
        setControlCommand(CDC_SET_CONTROL_LINE_STATE, controlLineState, null);

    }

    @Override
    public void setDTR(boolean state)
    {
        if (state)
            controlLineState |= CDC_SET_CONTROL_LINE_STATE_DTR;
        else
            controlLineState &= ~CDC_SET_CONTROL_LINE_STATE_DTR;
        setControlCommand(CDC_SET_CONTROL_LINE_STATE, controlLineState, null);
    }

    @Override
    public void getCTS(UsbCTSCallback ctsCallback)
    {
        //TODO
    }

    @Override
    public void getDSR(UsbDSRCallback dsrCallback)
    {
        //TODO
    }

    @Override
    public void getBreak(UsbBreakCallback breakCallback)
    {
        //TODO
    }

    @Override
    public void getFrame(UsbFrameCallback frameCallback)
    {
        //TODO
    }

    @Override
    public void getOverrun(UsbOverrunCallback overrunCallback)
    {
        //TODO
    }

    @Override
    public void getParity(UsbParityCallback parityCallback)
    {
        //TODO
    }

    private boolean openCDC()
    {
        if(connection.claimInterface(mInterface, true))
        {
            Log.i(CLASS_ID, "Interface succesfully claimed");
        }else
        {
            Log.i(CLASS_ID, "Interface could not be claimed");
            return false;
        }

        // Assign endpoints
        int numberEndpoints = mInterface.getEndpointCount();
        for(int i=0;i<=numberEndpoints-1;i++)
        {
            UsbEndpoint endpoint = mInterface.getEndpoint(i);
            if(endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK
                    && endpoint.getDirection() == UsbConstants.USB_DIR_IN)
            {
                inEndpoint = endpoint;
            }else if(endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK
                    && endpoint.getDirection() == UsbConstants.USB_DIR_OUT)
            {
                outEndpoint = endpoint;
            }
        }

        if(outEndpoint == null || inEndpoint == null)
        {
            Log.i(CLASS_ID, "Interface does not have an IN or OUT interface");
            return false;
        }

        // Default Setup
        setControlCommand(CDC_SET_LINE_CODING, 0, getInitialLineCoding());
        setControlCommand(CDC_SET_CONTROL_LINE_STATE, CDC_CONTROL_LINE_ON, null);

        return true;
    }

    protected byte[] getInitialLineCoding() {
        byte[] lineCoding;

        int initialBaudRate = getInitialBaudRate();

        if(initialBaudRate > 0) {
            lineCoding = CDC_DEFAULT_LINE_CODING.clone();
            for (int i = 0; i < 4; i++) {
                lineCoding[i] = (byte) (initialBaudRate >> i*8 & 0xFF);
            }
        } else {
            lineCoding = CDC_DEFAULT_LINE_CODING;
        }

        return lineCoding;
    }

    private int setControlCommand(int request, int value, byte[] data)
    {
        int dataLength = 0;
        if(data != null)
        {
            dataLength = data.length;
        }
        int response = connection.controlTransfer(CDC_REQTYPE_HOST2DEVICE, request, value, 0, data, dataLength, USB_TIMEOUT);
        Log.i(CLASS_ID,"Control Transfer Response: " + String.valueOf(response));
        return response;
    }

    private byte[] getLineCoding()
    {
        byte[] data = new byte[7];
        int response = connection.controlTransfer(CDC_REQTYPE_DEVICE2HOST, CDC_GET_LINE_CODING, 0, 0, data, data.length, USB_TIMEOUT);
        Log.i(CLASS_ID,"Control Transfer Response: " + String.valueOf(response));
        return data;
    }

    private static int findFirstCDC(UsbDevice device)
    {
        int interfaceCount = device.getInterfaceCount();

        int vid = device.getVendorId();
        int pid = device.getProductId();

        for (int iIndex = 0; iIndex < interfaceCount; ++iIndex)
        {
            if((iIndex==4 && vid == CDCSerialDevice.CDC_VID_1E0E && pid == CDCSerialDevice.CDC_PID_902E)||
                    (iIndex==2 && vid == CDCSerialDevice.CDC_VID_1E0E && pid == CDCSerialDevice.CDC_PID_9031)){
                return iIndex;
            }

            if(iIndex==2 && vid == CDCSerialDevice.CDC_VID_05C6 &&
                    (pid == CDCSerialDevice.CDC_PID_9102 || pid == CDCSerialDevice.CDC_PID_9120)){
                return iIndex;
            }

            if (device.getInterface(iIndex).getInterfaceClass() == UsbConstants.USB_CLASS_CDC_DATA)
            {
                return iIndex;
            }
        }

        Log.i(CLASS_ID, "There is no CDC class interface");
        return -1;
    }

}

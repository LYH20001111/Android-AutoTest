package com.newland.nsdk.core.common.uart3;

/**
 * Author by wuhh, Date on 2020/2/10.
 */
public class SerialPortJni {
    private static SerialPortJni serialPortJni;

    static {
        System.loadLibrary("serialport");
    }

    private SerialPortJni() {
    }

    public static SerialPortJni getInstance() {
        if (serialPortJni == null) {
            synchronized (SerialPortJni.class) {
                if (serialPortJni == null) {
                    serialPortJni = new SerialPortJni();
                }
            }
        }
        return serialPortJni;
    }

    //CardReaderImpl

    public native int portOpen(int port,int baud_rate, byte[] config);

    public native int portClose( int filefd);

    public native int portWrite(int filefd,byte[] buf,int count, int timeout);

    public native int portRead(int filefd,byte[] pszOutbuf,int count, int timeout);

    public native int portClearBuf(int filefd, int type);

    public native int portIsBufferEmpty(int filefd, int type);

    public native int portDebug(int filefd);

    public native int portIOCTL(int filefd, int cmd, byte[] args);

    public native int portReadLen(int filefd, int[] len);

    //N850的PINPAD口
    public native int portNDKOpen(int comNumber, String configStr);

    public native int portNDKClose(int comNumber);

    public native int portNDKRead(int comNumber, int maxLen, int timeout, byte[] outData, int[] outDataLen);

    public native int portNDKWrite(int comNumber, int length, byte[] data);

    public native int portNDKClrBuf(int comNumber);

    public native int portNDKReadLen(int comNumber, int[] readLen);

    //U2000 功能
    public native int awakeExternalDevice();

    public native int getExternalPowerSupply();

    public native int setRadarDetectionDistance(String gain, String delta);

    public native int enableRadarAndHeater(boolean isRadarEnable, boolean isHeaterEnable);

    public native int setEthernetMode(int mode);

    public native int getEthernetMode(int[] mode);

    //
    public native int portOpenWithNodeName(String portName, int baud_rate, byte[] config);

    public native int setDebugMode(int mode);
}
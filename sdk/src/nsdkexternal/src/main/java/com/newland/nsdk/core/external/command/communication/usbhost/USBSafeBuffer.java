package com.newland.nsdk.core.external.command.communication.usbhost;

import java.io.ByteArrayOutputStream;

/**
 * Author by bxy, Date on 2019/11/21.
 */
public class USBSafeBuffer {

    private Object waitReadLock = new Object();
    private ByteArrayOutputStream bos = new ByteArrayOutputStream();

    public void write(byte[] buf) {
        synchronized (this) {
            try {
                bos.write(buf);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int getLen() {
        synchronized (this) {
            return bos.toByteArray().length;
        }
    }

    public int read(byte[] buf, int len) {
        synchronized (this) {
            try {
                int validLen = bos.toByteArray().length;
                if (len <= 0 || validLen <= 0 || buf == null) {
                    return -1;
                }
                if (len <= validLen) {//3 10
                    System.arraycopy(bos.toByteArray(), 0, buf, 0, len);
                    byte[] temp = new byte[validLen - len];
                    System.arraycopy(bos.toByteArray(), len, temp, 0, temp.length);
                    bos.reset();
                    bos.write(temp);
                    return len;
                } else {//10 3
                    System.arraycopy(bos.toByteArray(), 0, buf, 0, validLen);
                    bos.reset();
                    return validLen;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return -1;
        }
    }

    public void clear() {
        synchronized (this) {
            bos.reset();
        }
    }


    public void waitRead(int timeOut) {
        try {
            synchronized (waitReadLock) {
                waitReadLock.wait(timeOut);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void notifyRead() {
        synchronized (waitReadLock) {
            waitReadLock.notify();
        }
    }
}

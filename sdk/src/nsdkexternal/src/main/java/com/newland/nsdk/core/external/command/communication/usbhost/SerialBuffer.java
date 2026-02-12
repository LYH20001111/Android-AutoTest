package com.newland.nsdk.core.external.command.communication.usbhost;


import android.os.Build;

import com.newland.nsdk.core.api.common.utils.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class SerialBuffer {

    private static final String TAG = "SerialBuffer";

    static int DEFAULT_READ_BUFFER_SIZE = 16 *1024;
    static int MAX_BULK_BUFFER = 16 *1024;
    private ByteBuffer readBuffer;

    private final SynchronizedBuffer writeBuffer;
    private byte[] readBufferCompatible; // Read buffer for android < 4.2
    private boolean debugging = false;

//    static {
//        if(Build.MANUFACTURER.equalsIgnoreCase("newland")){
//            DEFAULT_READ_BUFFER_SIZE = 16 *1024;
//            MAX_BULK_BUFFER = 16 *1024;
//        }else {
//            DEFAULT_READ_BUFFER_SIZE = 256;
//            MAX_BULK_BUFFER = 256;
//        }
//    }

    public SerialBuffer(boolean version) {
        writeBuffer = new SynchronizedBuffer();
        if (version) {
            readBuffer = ByteBuffer.allocate(DEFAULT_READ_BUFFER_SIZE);

        } else {
            readBufferCompatible = new byte[DEFAULT_READ_BUFFER_SIZE];
        }
    }

    /*
     * Print debug messages
     */
    public void debug(boolean value) {
        debugging = value;
    }

    public ByteBuffer getReadBuffer() {
        synchronized (this) {
            return readBuffer;
        }
    }


    public byte[] getDataReceived() {
        synchronized (this) {
            byte[] dst = new byte[readBuffer.position()];
            readBuffer.position(0);
            readBuffer.get(dst, 0, dst.length);
//            if(debugging)
//                UsbSerialDebugger.printReadLogGet(dst, true);
            return dst;
        }
    }

    public void clearReadBuffer() {
        synchronized (this) {
            readBuffer.clear();
        }
    }

    public byte[] getWriteBuffer() {
        return writeBuffer.get();
    }

    public void putWriteBuffer(byte[] data) {
        writeBuffer.put(data);
    }


    public byte[] getBufferCompatible() {
        return readBufferCompatible;
    }

    public byte[] getDataReceivedCompatible(int numberBytes) {
        return Arrays.copyOfRange(readBufferCompatible, 0, numberBytes);
    }

    private class SynchronizedBuffer {
        private final ByteArrayOutputStream buffer;

        SynchronizedBuffer() {
            this.buffer = new ByteArrayOutputStream();
        }

        synchronized void put(byte[] src) {
            try {
                if (src == null || src.length == 0) return;
                buffer.write(src);
                notify();
            } catch (IOException e) {
                LogUtils.d(TAG, "SynchronizedBuffer put");
//                e.printStackTrace();
            }
        }

        synchronized byte[] get() {
            if (buffer.size() == 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    LogUtils.d(TAG, "SynchronizedBuffer get");
//                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
            byte[] dst;
            if (buffer.size() <= MAX_BULK_BUFFER) {
                dst = buffer.toByteArray();
                buffer.reset();
            } else {
                try {
                    dst = new byte[MAX_BULK_BUFFER];
                    System.arraycopy(buffer.toByteArray(), 0, dst, 0, MAX_BULK_BUFFER);
                    byte[] temp = new byte[buffer.size() - MAX_BULK_BUFFER];
                    System.arraycopy(buffer.toByteArray(), MAX_BULK_BUFFER, temp, 0, temp.length);
                    buffer.reset();
                    buffer.write(temp);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new byte[0];
                }
            }
            return dst;
        }
    }

}

package com.newland.nsdk.core.external.command.communication.usbhost;

import com.newland.nsdk.core.api.common.utils.LogUtils;

import java.io.IOException;
import java.io.InputStream;

public class SerialInputStream extends InputStream {
    private int timeout = 0;

    private int maxBufferSize = 16 * 1024;

    private final byte[] buffer;
    private int pointer;
    private int bufferSize;

    protected final UsbSerialInterface device;

    public SerialInputStream(UsbSerialInterface device) {
        this.device = device;
        this.buffer = new byte[maxBufferSize];
        this.pointer = 0;
        this.bufferSize = -1;
    }

    public SerialInputStream(UsbSerialInterface device, int maxBufferSize) {
        this.device = device;
        this.maxBufferSize = maxBufferSize;
        this.buffer = new byte[this.maxBufferSize];
        this.pointer = 0;
        this.bufferSize = -1;
    }

    @Override
    public int read() {
        int value = checkFromBuffer();
        if (value >= 0)
            return value;

        int ret = device.syncRead(buffer, timeout);
        LogUtils.e(getClass().getName(),"syncRead:"+ret+"buffer:"+buffer.length);
        if (ret >= 0) {
            bufferSize = ret;
            return buffer[pointer++] & 0xff;
        } else {
            return -1;
        }
    }

    @Override
    public int read(byte[] b) {
        int ret = device.syncRead(b, timeout);
        LogUtils.e(getClass().getName(),"read() syncRead:"+ret+"buffer:"+buffer.length);
        return ret;
    }

    @Override
    public int read(byte[] b, int off, int len) {
        if (off < 0) {
            throw new IndexOutOfBoundsException("Offset must be >= 0");
        }

        if (len < 0) {
            throw new IndexOutOfBoundsException("Length must positive");
        }

        if (len > b.length - off) {
            throw new IndexOutOfBoundsException("Length greater than b.length - off");
        }

        if (off == 0 && len == b.length) {
            return read(b);
        }

        return device.syncRead(b, off, len, timeout);
    }

    @Override
    public int available() throws IOException {
        if (bufferSize > 0)
            return bufferSize - pointer;
        else
            return 0;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    private int checkFromBuffer() {
        if (bufferSize > 0 && pointer < bufferSize) {
            return buffer[pointer++] & 0xff;
        } else {
            pointer = 0;
            bufferSize = -1;
            return -1;
        }
    }
}

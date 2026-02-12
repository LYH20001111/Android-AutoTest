package com.newland.sdk.mtypex.usb;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;

import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtypex.cmd.CommandSerializer;
import com.newland.sdk.mtypex.cmd.packager.ReadTimeout;
import com.newland.sdk.mtypex.conn.AbstractDuplexDeviceConnection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;


public class UsbConnection extends AbstractDuplexDeviceConnection {

	private static final long READ_TIMEOUT = 1000;
	
	private DeviceLogger logger = DeviceLoggerFactory.getLogger("UsbConnection");
	
	private UsbDevice usbDevice;
	private UsbDeviceConnection usbConnection;
	
    private UsbInterface controlInterface;
    private UsbInterface dataInterface;

    private UsbEndpoint controlEndpoint;
    private UsbEndpoint readEndpoint;
    private UsbEndpoint writeEndpoint;
    
    public static final int DEFAULT_READ_BUFFER_SIZE = 16 * 1024;
    public static final int DEFAULT_WRITE_BUFFER_SIZE = 16 * 1024;
    
	private ByteBuffer readbuf = ByteBuffer.allocate(DEFAULT_READ_BUFFER_SIZE);
	
	private Thread readThread ;
	
	
	private class ReadThread implements Runnable{

		@Override
		public void run() {
			try{
				while(!Thread.currentThread().isInterrupted()){
					byte[] temp = new byte[DEFAULT_READ_BUFFER_SIZE];
					int numBytesRead = usbConnection.bulkTransfer(readEndpoint, temp, temp.length,
			                 0);
						
					if(numBytesRead <= 0){
						continue;
					}
					putReadBuffer(temp,0,numBytesRead);

					Thread.sleep(60);
				}
			}catch(Exception e){
				logger.warn("read inputstream failed!",e);
			}finally{
				try{
					UsbConnection.this.close();
				}catch(Exception e){
					logger.warn("close connection failed!",e);
				}
			}
		}
	}
	private void putReadBuffer(byte[] output ,int offset,int count){
    	synchronized (readbuf) {
//    		if(count > MAX_RESP_LENTH){
//    			logger.warn("out of length!max is "+MAX_RESP_LENTH+",but is "+output.length);
//    			readbuf.clear();
//    			return;
//    		}
    		try{
    			readbuf.put(output,offset,count);
    		}catch(Exception e){
    			logger.warn("failed to put buf:"+output.length+","+offset+","+count,e);
    			readbuf.clear();//若读入数据出错，则清理buffer
    		}
		} 	
    }
	public UsbConnection(CommandSerializer serializer, UsbDevice usbDevice, UsbDeviceConnection usbConnection) throws IOException, InterruptedException {
		super(serializer);
		this.usbDevice = usbDevice;
		this.usbConnection = usbConnection;
		open();
		Thread.sleep(200); 
		readThread = new Thread(new ReadThread());
		readThread.start();
		serviceStart();
	}
	
    public void open() throws IOException {
        logger.debug("[open]claiming interfaces, count=" + usbDevice.getInterfaceCount());

        logger.debug( "[open]Claiming control interface.");
        controlInterface = usbDevice.getInterface(0);
        logger.debug( "[open]Control iface=" + controlInterface);
        // class should be USB_CLASS_COMM

        if (!usbConnection.claimInterface(controlInterface, true)) {
            throw new IOException("[open]Could not claim control interface.");
        }
        controlEndpoint = controlInterface.getEndpoint(0);
        logger.debug( "[open]Control endpoint direction: " + controlEndpoint.getDirection());

        logger.debug( "[open]Claiming data interface.");
        dataInterface = usbDevice.getInterface(1);
        logger.debug( "[open]data iface=" + dataInterface);
        // class should be USB_CLASS_CDC_DATA

        if (!usbConnection.claimInterface(dataInterface, true)) {
            throw new IOException("[open]Could not claim data interface.");
        }
        readEndpoint = dataInterface.getEndpoint(1);
        logger.debug( "[open]Read endpoint direction: " + readEndpoint.getDirection());
        writeEndpoint = dataInterface.getEndpoint(0);
        logger.debug( "[open]Write endpoint direction: " + writeEndpoint.getDirection());
    }
    
	@Override
	protected void implClose() {
		if(usbConnection != null){
			try{
				if(controlInterface != null)
					usbConnection.releaseInterface(controlInterface);
			}catch(Exception e){
			}
			try{
				if(dataInterface != null)
					usbConnection.releaseInterface(dataInterface);
			}catch(Exception e){
			}
			try{
				usbConnection.close();
			}catch(Exception e){
			}
		}
		if(readThread != null){
			try{
				readThread.interrupt();
				readThread = null;
			}catch(Exception e){
			}
		}
	}
	private int readUntilTimeout(byte[] buffer,int offset,int len,long timeout,TimeUnit timeunit) throws ReadTimeout, IOException, InterruptedException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		long startTimeStamp = System.currentTimeMillis();
		while(bos.size() < len){
			int available = -1;
			synchronized (readbuf) {
				readbuf.flip();
				if((available = readbuf.remaining()) > 0){
					int expected = len - bos.size();
					byte[] temp = new byte[expected > available ? available : expected];
					readbuf.get(temp);
					bos.write(temp);
				}
				readbuf.compact();
			}

			if(bos.size() < len){//如果还没读满
				long endTimeStamp = System.currentTimeMillis();
				if((endTimeStamp - startTimeStamp) > timeunit.toMillis(timeout)){
					throw new ReadTimeout("read buffer timeout!expected len:"+len+",but "+bos.size());
				}
			}
			Thread.sleep(3);
		}
		System.arraycopy(bos.toByteArray(), 0, buffer, offset, len);
		
		return len;
	}

	@Override
	public int read(byte[] buffer) throws ReadTimeout, IOException,
			InterruptedException {
		return readUntilTimeout(buffer,0,buffer.length,READ_TIMEOUT,TimeUnit.MILLISECONDS);
	}

	@Override
	public int read(byte[] buffer, int offset, int len) throws ReadTimeout,
			IOException, InterruptedException {
		
		return readUntilTimeout(buffer,offset,len,READ_TIMEOUT,TimeUnit.MILLISECONDS);
	}

	@Override
	public void write(byte[] buffer) throws IOException {
		int amtWritten =  usbConnection.bulkTransfer(writeEndpoint, buffer, buffer.length,0);

		 if (amtWritten <= 0) {
             throw new IOException("Error writing " + buffer.length + " bytes!");
         }
	}

	@Override
	public void clearBuffer(int expectedMaxmium) throws IOException,
			InterruptedException {
		synchronized (readbuf) {
			readbuf.clear();
		}
	}

}

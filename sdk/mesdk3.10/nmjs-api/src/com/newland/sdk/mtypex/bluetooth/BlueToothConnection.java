package com.newland.sdk.mtypex.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.newland.ISettingsManager;
import android.util.Log;

import com.newland.sdk.common.RunningModel;
import com.newland.sdk.mtype.event.DeviceEventListener;
import com.newland.sdk.mtype.event.DeviceMenuEvent;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.cmd.CommandSerializer;
import com.newland.sdk.mtypex.cmd.packager.ReadTimeout;
import com.newland.sdk.mtypex.conn.AbstractDuplexDeviceConnection;
import com.newland.sdk.mtypex.conn.DirectMessageListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class BlueToothConnection extends AbstractDuplexDeviceConnection {
	
	private static final long READ_TIMEOUT = 1000;
	
	private DeviceLogger logger = DeviceLoggerFactory.getLogger("BlueToothConnection");
	
	private BluetoothSocket socket;
	
	private DisconnectedReceiver disconnectedReceiver = new DisconnectedReceiver();
	
	private static final int _BT_READ_BUFFER = 2048; //单次蓝牙读取缓存
	
	private static final int _READ_BUFFER = 8192; //最大的数据缓存空间
	
	private ByteBuffer readbuf = ByteBuffer.allocate(_READ_BUFFER);
	
	private Thread readThread ;
	
	private Object rwLock = new Object();
	
	private class ReadThread implements Runnable{
		
		private InputStream is;
		
		private ReadThread(InputStream is){
			this.is = is;
		}

		@Override
		public void run() {
			try{
				byte[] temp = new byte[_BT_READ_BUFFER];//固定开一个2048的空间，每次最长读2048
				while(!Thread.currentThread().isInterrupted()){
					int read = -1;
					if((read = readChannelAvailable(is)) > 0 && (read != 0xFFFF)){//只要存在数据,就尝试读取数据 ,排除0xFFFF
						synchronized (rwLock) {
							read = is.read(temp);
						}					
						if(read > 0){
							if(RunningModel.isDebugEnabled){
								byte[] bs = new byte[read];
								System.arraycopy(temp, 0, bs, 0, read);
								logger.debug("read output stream:"+ InnerUtils.hexString(bs));

							}
							putReadBuffer(temp,0,read);//读写到读缓存内
						}
					}
//					byte[] temp = new byte[1024];
//					int read = is.read(temp);
//					if(RunningModel.isDebugEnabled)
//						logger.debug("read output stream:"+read);
//					if(read > 0){
//						putReadBuffer(temp,0,read);
//					}
					Thread.sleep(39);
				}
			}catch(Exception e){
				logger.warn("read inputstream failed!",e);
			}finally{
				try{
					BlueToothConnection.this.close();
				}catch(Exception e){
					logger.warn("close connection failed!",e);
				}
			}
		}
	}
	private int readChannelAvailable(InputStream is) throws IOException{
//		synchronized (rwLock) { //荣耀3c会死锁
			return is.available();
//		}
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
//	private void putReadBuffer(byte[] output){
//    	synchronized (readbuf) {
//    		if(output.length > MAX_RESP_LENTH){
//    			logger.warn("out of length!max is "+MAX_RESP_LENTH+",but is "+output.length);
//    			readbuf.clear();
//    			return;
//    		}
//    		try{
//    			readbuf.put(output);
//    		}catch(Exception e){
//    			logger.warn("set recevie msg failed!length:"+output.length,e);
//    			readbuf.clear();
//    		}
//		} 	
//    }

	protected BlueToothConnection(Context context, CommandSerializer serializer, BluetoothSocket socket, final DeviceEventListener<DeviceMenuEvent> initiativeListener) throws IOException, InterruptedException{
		super(serializer);
		this.socket = socket;
		registerDisconncectedRecevier(context);
		if(null!=initiativeListener){
			registerDirectMessageListener(new DirectMessageListener() {
				@Override
				public void notify(byte[] cmdCode,byte[] payload) {
							if(Arrays.equals(cmdCode, new byte[]{(byte) 0xA1,0x11})){
								initiativeListener.onEvent(new DeviceMenuEvent(null, null,payload), null);
							}else{
								logger.warn("unknown directMessage!");
							}
				}
			});
		}
		Thread.sleep(150); //连接上后等待150秒.
		readThread = new Thread(new ReadThread(socket.getInputStream()));
		readThread.start();
		serviceStart();

	}
	private void registerDisconncectedRecevier(Context context) {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
		intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		
		context.getApplicationContext().registerReceiver(disconnectedReceiver, intentFilter);
	}
	private class DisconnectedReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(final Context context, Intent intent) {
	        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	        if (device != null && socket != null) {
	        	try{
		        	String expectedAddr = socket.getRemoteDevice().getAddress();
		            String addr = device.getAddress();
		            if(expectedAddr.equals(addr)){
		            	context.unregisterReceiver(disconnectedReceiver);
		            	logger.info("receive disconnected from device:"+expectedAddr);
		            	new Thread(new Runnable() {
							@Override
							public void run() {
								try{
				            		close();
				            	}catch(Exception e){
				            		logger.warn("close connection failed!", e);
				            	}
							}
						}).start();
		            }
	        	}catch(Exception e){
	        		logger.error("failed to process DisconnectReceiver!",e);
	        	}
	        }
		}
		
	}

	@Override
	public void implClose() {
		try {
			removeAllListeners();
			logger.debug("to stop read thread!");
			if(readThread != null){
				try{
					readThread.interrupt();
					readThread = null;
				}catch(Exception e){
				}finally{
					try {
						Thread.sleep(450);
					} catch (InterruptedException e) {
					}
				}
			}
		}finally{
			logger.debug("dealing bluetooth socket close!");
			if(socket != null){
				try{
					socket.close();
					socket = null;
				}catch(Exception e){
				}
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
			try {
				Thread.sleep(3);
			}catch (Exception e){
			}
		}
		System.arraycopy(bos.toByteArray(), 0, buffer, offset, len);
		
		return len;
	}

	@Override
	public int read(byte[] buffer) throws ReadTimeout, IOException, InterruptedException {
		return readUntilTimeout(buffer,0,buffer.length,READ_TIMEOUT,TimeUnit.MILLISECONDS);
	}

	@Override
	public int read(byte[] buffer, int offset, int len) throws ReadTimeout, IOException, InterruptedException {
		return readUntilTimeout(buffer,offset,len,READ_TIMEOUT,TimeUnit.MILLISECONDS);
	}

	@Override
	public void write(byte[] buffer) throws IOException {
		if(logger.isDebugEnabled())
			logger.debug("write output stream:"+ InnerUtils.hexString(buffer)+" socket="+socket);
		if(socket != null && socket.getOutputStream() != null){
			synchronized(rwLock){
				socket.getOutputStream().write(buffer);
				logger.debug("write success!!!");
			}
		}
	}

	@Override
	public void clearBuffer(int expectedMaxmium) throws IOException, InterruptedException {
		synchronized (readbuf) {
			try{
				readbuf.clear();
			}catch(Exception e){
				logger.warn("clear buffer failed!",e);
			}
		}
	}





}

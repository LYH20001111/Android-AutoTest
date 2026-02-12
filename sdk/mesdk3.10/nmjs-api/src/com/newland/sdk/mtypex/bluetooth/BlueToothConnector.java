package com.newland.sdk.mtypex.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Build;

import com.newland.sdk.mtype.DeviceOutofLineException;
import com.newland.sdk.mtype.conn.DeviceConnParams;
import com.newland.sdk.mtype.conn.DeviceConnType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtypex.bluetooth.BlueToothConnectForceBehavior.ConnectType;
import com.newland.sdk.mtypex.cmd.CommandSerializer;
import com.newland.sdk.mtypex.conn.DeviceConnection;
import com.newland.sdk.mtypex.conn.DeviceConnector;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class BlueToothConnector implements DeviceConnector {
	
	private DeviceLogger logger = DeviceLoggerFactory.getLogger("BlueToothConnector");
	
	public static final String PARAM_BLUETOOTH_REMOTEADDR = "PARAM_BLUETOOTH_REMOTEADDR";
	
	private CommandSerializer commandSerializer;

	public BlueToothConnector(CommandSerializer commandSerializer){
		this.commandSerializer = commandSerializer;
	}
	
	@Override
	public DeviceConnType[] getSupportConnType() {
		return new DeviceConnType[]{DeviceConnType.BLUETOOTH_V100};
	}

	@Override
	public DeviceConnection create(Context context, DeviceConnParams params) throws Exception {
		switch (params.getConnectType()) {
			case BLUETOOTH_V100:
				return createV100(context,(BlueToothConnParams) params);
			default:
				throw new UnsupportedOperationException("[create]This connect type is not supported:"+params.getConnectType());
		}
	}

	@SuppressLint("MissingPermission")
	private DeviceConnection createV100(Context context, BlueToothConnParams params) throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException, InterruptedException {
		
		String remoteAddr = params.getParam(PARAM_BLUETOOTH_REMOTEADDR);
		if(remoteAddr == null)
			throw new IllegalArgumentException("[createV100]PARAM_BLUETOOTH_REMOTEADDR should not be null!");
		
		
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if(!adapter.isEnabled()){
			throw new DeviceOutofLineException("[createV100]Bluetooth is not enabled!");
		}
		
		BluetoothDevice device = adapter.getRemoteDevice(remoteAddr);
		
		try {
			if(ConnectBehaviors.BEHAVIOR.isUsingCustomBond())
				doBTBond(context,device);
		} catch (InterruptedException e) {
			throw new DeviceOutofLineException("[createV100]Failed to connect bluetooth.addr:"+device.getAddress()+","+e.getMessage());
		}
		
		BluetoothSocket socket = null;
		
		
		if( (socket = doConnect(device,params)) == null)
			throw new DeviceOutofLineException("[createV100]Failed to connect bluetooth.addr:"+device.getAddress());
		
		return new BlueToothConnection(context,commandSerializer, socket,params.getInitiativeListener());
		
	}
	
	
	
	private class ConnectRunnable implements Runnable {
		
		boolean isSuccess = false;
		
		BluetoothDevice device = null;
		
		UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
		
		BlueToothConnParams params;
		
		BluetoothSocket socket = null;
		Method method = null;
		boolean couldUsingReflect = false;
		boolean usingInSecure = true;
//		boolean autoConnect = true;
		
		public ConnectRunnable(BluetoothDevice device, BlueToothConnParams params){
			this.device = device;
			this.params = params;
//			usingInSecure = params.isUsingInSecure();
//			autoConnect = params.isAutoConnect();
//			if(autoConnect || params.usingreflectToConnect()){
			if(ConnectBehaviors.BEHAVIOR.forceConnectType() == ConnectType.SECURE){
				usingInSecure = false;
			}
			try{
				String methodName = "createRfcommSocket";
				if(usingInSecure){
					methodName = "createInsecureRfcommSocket";
				}
				method = device.getClass().getMethod(methodName, new Class[] {int.class});
				if(method != null){
					couldUsingReflect = true;
				}
			}catch(Exception e){
			}
//			}
		}
		
		@SuppressLint("MissingPermission")
		private BluetoothSocket defaultToGetSocket() throws IOException{
			if(!usingInSecure){
				logger.debug("[defaultToGetSocket]Connect by createRfcommSocketToServiceRecord ,using uuid:"+uuid.toString());
				return device.createRfcommSocketToServiceRecord(uuid);
			}else{
				if(Build.VERSION.SDK_INT >= 10){
					logger.debug("[defaultToGetSocket]Connect by createInsecureRfcommSocketToServiceRecord ,using uuid:"+uuid.toString());
					return device.createInsecureRfcommSocketToServiceRecord(uuid);
				}else{
					logger.debug("[defaultToGetSocket]Connect by createRfcommSocketToServiceRecord ,using uuid:"+uuid.toString()+",sdk version:"+Build.VERSION.SDK_INT);
					return device.createRfcommSocketToServiceRecord(uuid);
				}
			}
		}
		
		private BluetoothSocket reflectToGetSocket() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
			int channel = params.getBTDefaultChannel();
			logger.debug("[reflectToGetSocket]Connect by reflect,using default channel:"+channel);
			return (BluetoothSocket) method.invoke(device, channel);			
		}
		
		private class SingleConnectionThread extends Thread{
			
			private BluetoothSocket socket;
			
			private boolean isSuccess = false;
			
			private Exception e;
			
			public SingleConnectionThread(BluetoothSocket socket){
				this.socket = socket;
			}
			
			public void run(){
				if(socket != null){
					try{
						socket.connect();
						isSuccess = true;
					}catch(Exception e){
						this.e = e;
						try {
							socket.close();
						} catch (Exception e1) {
						}
					}
				}
			}
			public void stopConnect(){
				if(socket != null){
					try {
						socket.close();
					} catch (Exception e) {
					}
				}
			}
			
			
		}
		
		@Override
		public void run() {
			try{
				int  times = 0;
				boolean reflectInvokingFlag = false;
				while(!Thread.currentThread().isInterrupted()){
//					if(isConnFinished){
//						return; 
//					}
					try{
						/**
						 * 使用反射连接满足3个条件：
						 * 1、具备反射能力
						 * 2、设置了反射标示（auto连接方式或者反射连接方式)
						 * 3、设备已经配对完成
						 */
						if(couldUsingReflect && (reflectInvokingFlag || ConnectBehaviors.BEHAVIOR.forceUsingReflect()) && !ConnectBehaviors.BEHAVIOR.forceUsingDefault()){
							socket = reflectToGetSocket();
						}else{
							socket = defaultToGetSocket();
						}
						
						BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
						Thread.sleep(3);
						
						SingleConnectionThread connectionThread = new SingleConnectionThread(socket); //开始一次链接线程
						connectionThread.start();
						connectionThread.join(9000); //等待该线程链接大约9秒的时间
						if(!connectionThread.isSuccess && connectionThread.isAlive()){ //若链接结果为：未成功，且线程仍然存活
							logger.debug("[SingleConnectionThread]wait until 9000 ms.try to close socket!");
							connectionThread.stopConnect(); //尝试调用socket关闭操作。（避免出现connect后无法中断出来的情况。）
							socket = null;
							connectionThread.join(300);//等待关闭操作结束
						}
						if(connectionThread.isSuccess){//如果该线程成功结束（已经链接上）
							isSuccess = true; //设置链接过程成功
							return;//返回结果
						}else{//链接失败
							if(connectionThread.e != null)//如果链接结果异常不为空
								logger.debug("[SingleConnectionThread]Failed to connect device :"+device.getAddress()+":"+connectionThread.e.getMessage());//打印失败原因
							
							if(device.getBondState() != BluetoothDevice.BOND_BONDED){ //如果判断是没有配对成功的失败,默认只会使用系统方式完成一次带配对的连接过程。（未配对设备，默认第一次都是使用非反射方式链接）
								return;
							}
						}
					}catch (Exception e) {//连接过程中,出现的异常,全部无视，只是尝试关闭socket链接
						if(socket != null){
							try {
								socket.close();
							} catch (IOException e1) {
							}
							socket = null; 
						}
						if(e instanceof InterruptedException){ //如果时InterruptedException，则直接返回
							return;
						}
					}finally{
//						if(autoConnect){//轮流切换链接方式
							reflectInvokingFlag = !reflectInvokingFlag;
//						}
						if(times >= 30 ){//30次失败则回收一次资源
							try{
								System.gc();
							}catch(Exception e){
							}
							times = 0;
						}else{
							times ++;
						}
					}
					try {
						Thread.sleep(450);
					} catch (InterruptedException e) {
						return;
					}
				}
			}finally{//整个结束后,建议个gc
				try{
					System.gc();
				}catch(Exception e){
				}
			}
		}
		
//		public void stop(){
//			isConnFinished = true;
//			if(!isSuccess && socket != null){//如果退出时连接不成功，且socket还未关闭，则主动关闭连接
//				try {
//					socket.close();
//					socket = null;
//				} catch (IOException e) {
//				}
//			}
//		}
	}
	
	private BluetoothSocket doConnect(BluetoothDevice device, BlueToothConnParams params){
		ConnectRunnable cr = new ConnectRunnable(device,params);
		Thread t = new Thread(cr);
		t.start();
		long start = System.currentTimeMillis();
		long end = start;
		try {
			do{
				t.join(15000);//等待15秒
				end = System.currentTimeMillis();
			}while(device != null && (BluetoothDevice.BOND_BONDING == device.getBondState()) && (end - start < 45 * 1000));//当设备的配对还未完成时，继续等待,最长等待45秒
		} catch (InterruptedException e) {
		}finally{
			if(t.isAlive()){ //等待时间未结束，则中断相关处理流程
				try{
					t.interrupt();
				}catch(Exception e){
				}
			}
		}
//		cr.stop();
		try {
			t.join(1000); //等待1秒后退出
		} catch (InterruptedException e) {
		}
		if( cr.isSuccess){
			return cr.socket;
		}
		return null;
	}
	

	private void doBTBond(Context context,final BluetoothDevice device) throws InterruptedException {
		boolean isBondInvoked = false;
		
		if(device.getBondState() == BluetoothDevice.BOND_BONDED)
			return;
		
		try{
			if(BluetoothDevice.BOND_NONE == device.getBondState()){ //如果状态是未发起配对
				try{
					doBTBond0(device);//发起一次配对
				}catch(Exception e){
					logger.debug("[doBTBond]Faild to do BT bond.",e);
				}
				isBondInvoked = true;
				
			}
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					long start = System.currentTimeMillis();
					while(true){
						long end = System.currentTimeMillis();
						if((end - start) > 60 * 1000){ //如果60秒内未完成配对,则返回失败
							return;
						}
						try {
							Thread.sleep(100);//每100ms判定一次.
						} catch (InterruptedException e) {
							logger.info("meet interrupt!"+e.getMessage());
							return;
						} 
						int bondstate = device.getBondState();

						if(BluetoothDevice.BOND_BONDING != bondstate){ //如果绑定,则返回
							if(BluetoothDevice.BOND_NONE == bondstate){ //如果4.5秒之后，判定到bond_none的状态，则退出。（bond没有发起） 
								if(end - start < 4500){ 
									continue;
								}
							}
							//一直要等待bond状态变化
							logger.info("try bond,but not start (no bond_bonding:"+BluetoothDevice.BOND_BONDING+" state), finished by :" + bondstate);
							return;
						}
					}
				}
			});
			t.start();
			t.join();//等待线程执行结束 
			if(isBondInvoked && device.getBondState() != BluetoothDevice.BOND_BONDED){ //如果执行过配对,但结果仍然未成功,则抛出异常,不再连接
				throw new DeviceOutofLineException("bond failed!may user cancel bt bond?or device is out of air?");
			}
		}catch (InterruptedException e) {
			throw e;
		}catch (DeviceOutofLineException e) {
			throw e;
		}catch(Exception e){
			logger.debug("failed to create bond for:"+device.getAddress(),e);
		}
	}
	
	private void removeBond(BluetoothDevice device) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method method = BluetoothDevice.class.getMethod("removeBond");
		if(method == null){
			if(logger.isDebugEnabled())
				logger.debug("[removeBond]Remove bt bond is not supported.");
			throw new RuntimeException("[removeBond]Remove bt bond is not supported.");
		}
		method.setAccessible(true);
		method.invoke(device);
	}

	private void doBTBond0(BluetoothDevice device) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method method = BluetoothDevice.class.getMethod("createBond");
		if(method == null){
			if(logger.isDebugEnabled())
				logger.debug("[doBTBond0]Bt bond is not supported.");
			throw new RuntimeException("doBTBond0]Bt bond is not supported.");
		}
		method.setAccessible(true);
		method.invoke(device);
	}
	
}

package com.newland.sdk.mtypex.usb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.newland.sdk.mtype.DeviceOutofLineException;
import com.newland.sdk.mtype.conn.DeviceConnParams;
import com.newland.sdk.mtype.conn.DeviceConnType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtypex.cmd.CommandSerializer;
import com.newland.sdk.mtypex.conn.DeviceConnection;
import com.newland.sdk.mtypex.conn.DeviceConnector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UsbConnector implements DeviceConnector {
	
	private DeviceLogger logger = DeviceLoggerFactory.getLogger("UsbConnector");
	
	private static Map<Integer,int[]> supportUsbDevices = new HashMap<Integer,int[]>();

	static{
		supportUsbDevices.put(0x0730, new int[]{0xDCBA});
	}
	
	private CommandSerializer commandSerializer;
	
	public UsbConnector(CommandSerializer commandSerializer){
		this.commandSerializer = commandSerializer;
	}
	
	@Override
	public DeviceConnType[] getSupportConnType() {
		return new DeviceConnType[] { DeviceConnType.USB_V100 };
	}

	@Override
	public DeviceConnection create(Context context, DeviceConnParams params)
			throws Exception {
		switch (params.getConnectType()) {
		case USB_V100:
			return createConnection(context, params);

		default:
			throw new RuntimeException("not support this connectType:" + params.getConnectType());
		}
	}

	private DeviceConnection createConnection(Context context,
                                              DeviceConnParams params) {
		UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
		HashMap<String, UsbDevice> deviceList =  usbManager.getDeviceList();
		for(String key:deviceList.keySet()){
			UsbDevice device = deviceList.get(key);
			if(isSupportDevice(device)){
				try {
					UsbDeviceConnection connection = null;
					if(usbManager.hasPermission(device)){
						connection = usbManager.openDevice(device);
					}else{
						mUsbReceiver = new DefaultUsbPermissionCheckReceiver();
						PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
						IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
						context.registerReceiver(mUsbReceiver, filter);
						usbManager.requestPermission(device, mPermissionIntent);
						mUsbReceiver.startWaiting();
						
						connection = mUsbReceiver.getConnection();
					}
					if(connection == null)
						throw new DeviceOutofLineException("failed to connect usb device!need System Permission.");
					return new UsbConnection(commandSerializer,device,connection);
				} catch (IOException e) {
					throw new DeviceOutofLineException("connect to device failed!",e);
				} catch (InterruptedException e){
					throw new DeviceOutofLineException("user cancel connect process!",e);
				}finally{
					if(mUsbReceiver != null){
						try{
							context.unregisterReceiver(mUsbReceiver);
						}catch(Exception e){
						}
					}
				}
			}
		}
		throw new DeviceOutofLineException("failed to find expected usb device!");
	}

	private static final String ACTION_USB_PERMISSION =
		    "com.newland.require.USB_PERMISSION";
	private DefaultUsbPermissionCheckReceiver mUsbReceiver;
			
			
	private class DefaultUsbPermissionCheckReceiver extends BroadcastReceiver{
		
		private Object syncObj = new Object();
		
		private UsbDeviceConnection connection = null;

	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        if (ACTION_USB_PERMISSION.equals(action)) {
	            synchronized (this) {
	                UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

	                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
	                    if(device != null){
	                    	UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
	                    	try{
	                    		connection = usbManager.openDevice(device);
	                    	}catch(Exception e){
	                    		logger.error("failed to open usb connection!",e);
	                    	}
	                    }
	                } else {
	                    logger.error("failed to get permission for device");
	                }
	            }
	            synchronized (syncObj) {
					syncObj.notify();
				}
	        }
	    }
	    
	    public UsbDeviceConnection getConnection(){
	    	return connection;
	    }
	    
	    public void startWaiting() throws InterruptedException{
	    	synchronized(syncObj){
	    		syncObj.wait(30000);
	    	}
	    }
	};
	
	private boolean isSupportDevice(UsbDevice device) {
		int[] products = supportUsbDevices.get(device.getVendorId());
		if(products == null)
			return false;
		for(int productId:products){
			if(productId == device.getProductId())
				return true;
		}
		return false;
	}
	
	

}

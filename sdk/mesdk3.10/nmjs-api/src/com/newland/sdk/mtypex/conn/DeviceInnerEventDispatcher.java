package com.newland.sdk.mtypex.conn;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.Handler;

import com.newland.sdk.mtype.event.DeviceEvent;
import com.newland.sdk.mtype.event.DeviceEventListener;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;

public class DeviceInnerEventDispatcher {
	
	private DeviceLogger logger = DeviceLoggerFactory.getLogger(DeviceInnerEventDispatcher.class);
	
	private Map<String,DeviceEventRegister> listeners = new HashMap<String,DeviceEventRegister>();	

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 64;
    private static final int KEEP_ALIVE = 1;

    private static final BlockingQueue<Runnable> sWorkQueue =
            new LinkedBlockingQueue<Runnable>(10);

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "EVENT DISPATCHER -" + mCount.getAndIncrement());
        }
    };

    private static final ThreadPoolExecutor sExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, sWorkQueue, sThreadFactory);
    
    private static DeviceInnerEventDispatcher instance;
    
    public static final DeviceInnerEventDispatcher instance(){
    	synchronized (sThreadFactory) {
			if(instance == null){
				instance = new DeviceInnerEventDispatcher();
			}
		}
    	return instance;
    }
    
    private DeviceInnerEventDispatcher(){
    }
    
    
    public boolean registerEvent(String eventName, DeviceEventListener<?> listener){
    	return registerEvent0(eventName, listener, false);
    }
    public DeviceEventListener<?> removeEvent(String eventName){
    	synchronized (listeners) {
    		DeviceEventRegister register = listeners.remove(eventName);
    		if(register != null)
    			return register.listener;
		}
    	return null;
    }
    
    private boolean registerEvent0(String eventName, DeviceEventListener<?> listener,boolean isOnce){
    	synchronized (listeners) {
			DeviceEventRegister register = listeners.get(eventName);
			if(register == null){
				if(logger.isDebugEnabled())
					logger.debug("register event:"+eventName);
				listeners.put(eventName, new DeviceEventRegister(listener, listener.getUIHandler(),isOnce));
				return true;
			}else{
				logger.warn("you should unregister device event:"+eventName);
			}
		}
    	return false;
    }
    
    public boolean registerOnceEvent(String eventName,DeviceEventListener<?> listener){
    	return registerEvent0(eventName, listener, true);
    }
    
    public void dispatchEvent(final DeviceEvent deviceEvent){
    	DeviceEventRegister register = null;
    	synchronized (listeners) {
    		String eventName = deviceEvent.getEventName();
    		register = listeners.get(eventName);
    		if(register == null){
    			logger.warn("no event found to dispatch:"+eventName);
    			return;
    		}
    		if(register.isOnce){ //如果是只执行一次的事件,执行前先删除事件.
    			listeners.remove(eventName);
    		}
		}
    	final DeviceEventRegister tgt = register;
    	sExecutor.execute(new Runnable() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				if(logger.isDebugEnabled())
					logger.debug("process event:"+deviceEvent.getEventName());
				tgt.listener.onEvent(deviceEvent, tgt.handler);
			}
		});
    }
    
    
    
    
    private class DeviceEventRegister{
    	
    	private boolean isOnce = false;
    	
    	@SuppressWarnings("rawtypes")
		private DeviceEventListener listener;
    	
    	private Handler handler;
    	
    	public DeviceEventRegister(DeviceEventListener<?> listener,Handler handler){
    		this.listener = listener;
    		this.handler = handler;
    	}
    	public DeviceEventRegister(DeviceEventListener<?> listener,Handler handler,boolean isOnce){
    		this(listener,handler);
    		this.isOnce = isOnce;
    	}
    }
	
	
}

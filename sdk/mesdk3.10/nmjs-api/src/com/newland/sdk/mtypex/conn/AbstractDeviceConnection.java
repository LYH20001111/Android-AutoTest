package com.newland.sdk.mtypex.conn;

import java.util.HashMap;
import java.util.Map;

import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.SimIdGenerator;
import com.newland.sdk.mtypex.cmd.CommandSerializer;
import com.newland.sdk.mtypex.cmd.packager.DeviceCommProtocalPackager;
import com.newland.sdk.mtypex.cmd.packager.NLMposProtocalPackager;

public abstract class AbstractDeviceConnection implements DeviceConnection{

	private final String id;
	
	private SimIdGenerator simIdGenerator = new SimIdGenerator(999);
	
	private static final Object idGenSync = new Object();
	
	private final Map<String ,DirectMessageListener> dmListenerMaps = new HashMap<String,DirectMessageListener>();
	
	protected DeviceCommProtocalPackager packager;
	
	private static DeviceLogger logger = DeviceLoggerFactory.getLogger(AbstractDeviceConnection.class);
	
	public AbstractDeviceConnection(CommandSerializer serializer){
		id = "DEVICE_CONN_"+simIdGenerator.getId(idGenSync);
		packager = new NLMposProtocalPackager(serializer);
	}
	
	@Override
	public String getId() {
		return id;
	}

	protected void notifyDirectMessage(final byte[] cmdCode,final byte[] payload){
		synchronized (dmListenerMaps){
			for(final DirectMessageListener listener:dmListenerMaps.values()){
				new Thread(new Runnable() {
					@Override
					public void run() {
						try{
							listener.notify(cmdCode,payload);
						}catch(Exception e){
							logger.info("notify msg meet error!",e);
						}
					}
				}).start();
			}
		}
	}
	/**
	 * Register a direct message listener
	 * 
	 * @param listener
	 */
	public void registerDirectMessageListener(DirectMessageListener listener){
		synchronized (dmListenerMaps) {
			if(dmListenerMaps.containsKey(listener.getListenerId())){
				if(logger.isDebugEnabled())
					logger.debug("listenerId "+listener.getListenerId()+" exists!will replace!");
			}
			dmListenerMaps.put(listener.getListenerId(), listener);
		}
	}
	
	/**
	 * Remove a direct message listener
	 * @param listener
	 */
	public void removeDirectMessageListener(DirectMessageListener listener){
		synchronized (dmListenerMaps) {
			dmListenerMaps.remove(listener.getListenerId());
		}
	}
	
	/**
	 * Remove all direct message listeners
	 */
	public void removeAllListeners(){
		synchronized (dmListenerMaps) {
			dmListenerMaps.clear();
		}
	}


}

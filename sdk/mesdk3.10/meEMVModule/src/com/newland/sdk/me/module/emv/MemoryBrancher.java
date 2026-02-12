package com.newland.sdk.me.module.emv;

import java.util.HashMap;
import java.util.Map;


public class MemoryBrancher {
	
	private Map<String,MemoryFileCacheHandler> filecontainer = new HashMap<String,MemoryFileCacheHandler>();
	
	private MemoryBrancher(){
	}
	
	public static final MemoryBrancher createInstance(){
		return new MemoryBrancher();
	}
	
	public MemoryFileCacheHandler findOrCreateMemoryFile(String filename){
		synchronized (filecontainer) {
			MemoryFileCacheHandler filehandler = filecontainer.get(filename);
			if(filehandler == null){
				filehandler = MemoryFileCacheHandler.createFile(filename);
				filecontainer.put(filename, filehandler);
			}
			return filehandler;
		}
	}
	
	public int delete(String filename){
		synchronized (filecontainer) {
			MemoryFileCacheHandler handler = filecontainer.get(filename); //TODO 有线程安全问题
			if(handler == null || handler.isOpened()){
				return -1;
			}
			filecontainer.remove(filename);
			return 0;
		}	
	}
	
	/**
	 * TODO TmpFile 的问题
	 * @param src
	 * @param dist
	 * @return
	 */
	public int rename(String src,String dist){
		synchronized (filecontainer) {
			MemoryFileCacheHandler handler = filecontainer.get(src); //TODO 有线程安全问题
			
			
			if(handler == null || handler.isOpened()){
				return -1;
			}
			
			handler = filecontainer.remove(src);
			if(handler != null){
				filecontainer.put(dist, handler);
			}
			return 0;
		}
	}
	
	public int truncate(String filename,int size){
		synchronized (filecontainer) {
			MemoryFileCacheHandler handler = filecontainer.get(filename); //TODO 有线程安全问题
			if(handler == null || handler.isOpened()){
				return -1;
			}
			
			return handler.truncate(size);
		}
	}
	


}

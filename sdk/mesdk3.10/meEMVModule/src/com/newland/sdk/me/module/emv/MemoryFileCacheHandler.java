package com.newland.sdk.me.module.emv;

import android.os.Environment;

import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;

public class MemoryFileCacheHandler implements FileHandler{
	
   private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger(MemoryFileCacheHandler.class);
	
   private static final int FILE_WRITE = 2;
   
   private static final int FILE_SEEK_SET = 0;
   private static final int FILE_SEEK_CUR = 1;
   private static final int FILE_SEEK_END = 2;
	
	private String filename;
	
	private byte[] buffer = new byte[8192];
	
	private int len = 0;
	
	private int offset = 0;
	
	private int rw = -1;
	
	private volatile Boolean isOpened = false;
	
	private MemoryFileCacheHandler(String filename){
		this.filename = filename;
	}
	
	public static final MemoryFileCacheHandler createFile(String filename){
		return new MemoryFileCacheHandler(filename);
	}
	

	@Override
	public String getFileName() {
		return filename;
	}

	@Override
	public String getSimpleName() {
		return filename;
	}

	@Override
	public int open(int rw){
		synchronized(isOpened){
			devicelogger.debug("open file:"+filename);
			this.rw = rw;
			this.offset = 0;
			this.isOpened = true;
			return 1;
		}
	}

	@Override
	public int close() {
		synchronized (isOpened) {
			devicelogger.debug("close file:"+filename);
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(Environment.getExternalStorageDirectory()+"/"+filename.substring(filename.lastIndexOf('/'),filename.length()));
				fos.write(buffer,0,len);
				fos.flush();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(fos != null){
					try {
						fos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			this.offset = 0;
			this.isOpened = false;
			return 1;
		}
	}
	
	

	@Override
	public int read(byte[] buffer, int size) {
		synchronized (isOpened) {
			devicelogger.debug("read buffer:"+filename+",size:"+size+",offset:"+this.offset);
			if(!isOpened){
				devicelogger.debug("file:"+filename+" not opened!");
				return -1;
			}
//			if(rw <= 0 || ((rw & FILE_READ) != FILE_READ) ){
			if(rw <= 0){ // rw标志不做判断在读的过程中
				devicelogger.debug("rw not match!"+rw);
				return -1;
			}
			int oldOffset = this.offset;
			try{
				
				if(offset + size > len){
					size = len - offset;
				}
				if(size < 0 || size > len){
					devicelogger.error("unexpected size:"+size);
					return -1;
				}
				System.arraycopy(this.buffer, offset, buffer, 0, size);
				
				offset += size;
				
				return size;
			}catch(Exception e){
				devicelogger.error("failed to read file:"+filename+",offset:"+this.offset+",len:"+this.len+",expected read:"+size,e);
				this.offset = oldOffset;
				return -1;
			}
		}
		
	}

	/**
	 * 该方法是覆盖的方法
	 * 而不是插入
	 */
	@Override
	public int write(byte[] buffer, int size) {
		synchronized (isOpened) {
			devicelogger.debug("write buffer:"+filename+",size:"+size +",len:"+this.len+",offset:"+this.offset);
			if(!isOpened){
				devicelogger.debug("file:"+filename+" not opened!");
				return -1;
			}
			if(rw <= 0 || ((rw & FILE_WRITE) != FILE_WRITE) ){
				devicelogger.debug("rw not match!"+rw);
				return -1;
			}
			int oldOffset = this.offset;
			try{
				byte[] temp = this.buffer;
				if(offset + size > this.buffer.length){ //判断当前偏移＋总长 和buffer的长度谁大
					this.buffer = new byte[offset + size + 8192];
				}
				if(this.buffer != temp){
					System.arraycopy(temp, 0, this.buffer, 0, offset );
				}
				System.arraycopy(buffer, 0, this.buffer, offset, size);
				
				offset += size; 
				if(offset > len){
					len = offset;
				}
				
				byte[] fileblock = new byte[len];
				System.arraycopy(this.buffer, 0, fileblock, 0, this.len);
				
				return size;
			}catch(Exception e){
				devicelogger.error("failed to write file:"+filename+",offset:"+this.offset+",len:"+this.len+",size:"+size,e);
				this.offset = oldOffset;
				return -1;
			}
		}
	}

	/*
     * 光标定位
     * int offset：偏移长度
     * int where：起始位置
     * #define FILE_SEEK_SET 0    start of stream 
	 * #define FILE_SEEK_CUR 1    current position in stream
     * #define FILE_SEEK_END 2    end of stream  
     * 返回值：
     * 失败小于0，成功返回当前光标位置
     */
	@Override
	public int seek(int offset, int where) {
		synchronized (isOpened) {
			devicelogger.debug("seek:"+filename+",seek:"+offset+",where:"+where+",offset:"+offset);
			if(!isOpened){
				devicelogger.debug("file:"+filename+" not opened!");
				return -1;
			}
			int oldOffset = this.offset;
			if(where == FILE_SEEK_SET){
				this.offset = offset;
			}else if(where == FILE_SEEK_CUR){
				this.offset += offset;
			}else if(where == FILE_SEEK_END){
				this.offset = len + offset;
			}
			if(this.offset >= 0 && this.offset <= len){
				devicelogger.debug("current offset:" + this.offset);
				return this.offset;
			}else{
				devicelogger.error("unknown offset:"+this.offset+",len:"+this.len);
				this.offset = oldOffset;
				return -1;
			}
		}
	}

	@Override
	public int truncate(int size) {
		synchronized (isOpened) {
			if(isOpened){
				devicelogger.debug("file:"+filename+" should not be opened!");
				return -1;
			}
			if(size > this.len || size < 0){
				devicelogger.error("error size:"+size+"("+this.len+")");
				return -1;
			}
			this.len = size;
			return size;
		}
		
	}

	@Override
	public boolean isOpened() {
		return isOpened;
	}
}

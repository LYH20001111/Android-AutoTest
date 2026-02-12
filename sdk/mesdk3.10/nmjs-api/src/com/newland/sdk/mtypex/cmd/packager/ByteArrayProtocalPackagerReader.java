package com.newland.sdk.mtypex.cmd.packager;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ByteArrayProtocalPackagerReader implements ProtocalPackagerReader{
	
	private ByteArrayInputStream bis;
	
	public ByteArrayProtocalPackagerReader(byte[] payload){
		bis = new ByteArrayInputStream(payload);
	}

	@Override
	public int read(byte[] buffer) throws ReadTimeout, IOException,
			InterruptedException {
		return bis.read(buffer);
	}

	@Override
	public int read(byte[] buffer, int offset, int len) throws ReadTimeout,
			IOException, InterruptedException {
		return bis.read(buffer, offset, len);
	}

	@Override
	public void clearBuffer(int expectedMaxmium) throws IOException,
			InterruptedException {
		bis.reset();
	}

}

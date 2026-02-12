package com.newland.sdk.module.emv;


import com.newland.sdk.me.module.emv.structure.AbstractEMVPackage;
import com.newland.sdk.me.module.emv.structure.EMVTagRef;

import java.util.List;
import java.util.Map;

/**
 * emv Packager
 * 
 *
 * @since v1.0
 */
public interface EmvPackager {
	
	public Map<Integer,EMVTagRef> getSupportTagMapping();
	
	public <T extends AbstractEMVPackage> byte[] pack(T pckg);
	
	public <T extends AbstractEMVPackage> T unpack(byte[] payload, Class<T> content, T context);

	public <T extends AbstractEMVPackage> List<T> unpackList(byte[] payload, Class<T> content, int reIndex);

}

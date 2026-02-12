package com.newland.sdk.me.cmd.serializer;

import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.mtypex.serializer.AbstractEnumSerializer;

/**
 * KeyManager Serializer
 * 
 * @author youjf
 *
 */
public class keyManagementSerializer  extends AbstractEnumSerializer{

	public keyManagementSerializer() {
		super(KeyManagement.class, new byte[][]{{0x00},{0x01},{0x02},{0x00}});
	}
}
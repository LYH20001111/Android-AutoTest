package com.newland.sdk.me.cmd.serializer;

import com.newland.sdk.module.pin.AccountInputType;
import com.newland.sdk.mtypex.serializer.AbstractEnumSerializer;

public class AccountInputTypeSerializer  extends AbstractEnumSerializer{

	public AccountInputTypeSerializer() {
		super(AccountInputType.class, new byte[][]{{0x00},{0x01},{0x02}});
	}
}

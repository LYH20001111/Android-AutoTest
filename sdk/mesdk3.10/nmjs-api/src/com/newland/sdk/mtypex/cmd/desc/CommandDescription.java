package com.newland.sdk.mtypex.cmd.desc;

import java.util.List;

/**
 * The format description of the command
 * @author chenliang
 *
 */
public class CommandDescription extends EntityDescription{
	
	private final byte[] cmdCode;
	
	private final ResponseDescription notificationResponseDescription;
	
	private final ResponseDescription responseDescription;
	
	public CommandDescription(byte[] cmdCode,ResponseDescription responseDescription ,ResponseDescription notificationResponseDescription,List<FieldDescription> fields){
		super(fields);
		this.cmdCode = cmdCode;
		this.responseDescription = responseDescription;
		this.notificationResponseDescription = notificationResponseDescription;
	}

	public byte[] getCmdCode() {
		return cmdCode;
	}

	public ResponseDescription getResponseDescription() {
		return responseDescription;
	}

	public ResponseDescription getNotificationResponseDescription() {
		return notificationResponseDescription;
	}


	
	
}

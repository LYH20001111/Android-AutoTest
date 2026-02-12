package com.newland.sdk.me.module.cardreader;

import com.newland.sdk.mtype.common.EventConst;

/**
 *  Card reader response event
 * <p>
 * 
 *
 * @since ver3.10.01
 */
public class K21CardReaderEvent extends OpenCardReaderEvent {

	private int returnKey;
	
	public K21CardReaderEvent(int returnKey){
		super(EventConst.EVENT_OPEN_CARDREADER_FINISH ,ProcessState.PROCESSING, null);
		this.returnKey = returnKey;
	}
	public K21CardReaderEvent(OpenCardReaderResult openCardReaderResult){
		super(openCardReaderResult);
	}
	public K21CardReaderEvent(Throwable e){
		super(e);
	}
	public K21CardReaderEvent(){
		super();
	}
	
	
	/**
	 * Get the keyboard input. If the returned key value is 0x80+key value, deduct 0x80 from the returned value.<p>
	 * @return
	 */
	public int getReturnKey() {
		int keyreturn = returnKey-128;
		return keyreturn;
	}
}

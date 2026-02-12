package com.newland.sdk.module.swiper;

/** 
 * Swiping result<p>
 * 
 *
 * @since v1.0
 */
public enum SwipResultCode {
	/** 
	 * Success
	 */
	SUCCESS("Success"),
	/** 
	 * Param error
	 */
	PARAM_ERROR("Param error"),
	/** 
	 * Data field length error
	 */
	DATALENGTH_ERROR("Data field length error"),
	/** 
	 * Length error
	 */
	LENGTH_ERROR("Length error"),
	/** 
	 * Type error 
	 */
	TYPE_ERROR("Type error "),
	/** 
	 *  Data format error of reading magnetic stripe card
	 */
	DATAFORMAT_ERROR("Data format error of reading magnetic stripe card"),
	/** 
	 *  Magnetic stripe card data reading timeout
	 */
	READTRACK_TIMEOUT("Magnetic stripe card data reading timeout"),
	/** 
	 * Magnetic stripe card swiping reading failed
	 */
	SWIP_FAILED("Magnetic stripe card swiping reading failed");

	private String description;
	
	SwipResultCode(String description){
		this.description = description;
	}
	
	public String toString(){
		return description;
	}

}

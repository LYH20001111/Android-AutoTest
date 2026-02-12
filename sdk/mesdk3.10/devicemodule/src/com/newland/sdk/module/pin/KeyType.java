package com.newland.sdk.module.pin;

/**
 * Key Type
 * @author youjf
 * @since 3.10.01
 */
public enum KeyType {
	/**
	 * TRANSPORT KEY
	 */
	TRANSPORT_KEY(-1,-1),
	/**
	 * master key
	 */
	MASTER_KEY(3,1),
	/**
	 * PIN KEY
	 */
	PIN_KEY(0,2),
	/**
	 * MAC  KEY
	 */
	MAC_KEY(1,3),
	/**
	 * TRACK KEY
	 */
	TRACK_KEY(2,4),

	;

	/*
	overseaIndex 用于oversea外接键盘删除key时传参
	 */
	private int overseaIndex;
	/*
	spIndex针对国内3.2.19版本以上外接密码键盘，用于删除密钥使用
	 */
	private int spIndex;
	private KeyType(int overseaIndex,int spIndex){
		this.overseaIndex = overseaIndex;
		this.spIndex = spIndex;
	}
	public int getOverseaIndex(){
		return this.overseaIndex;
	}
	public int getSpIndex(){
		return this.spIndex;
	}
}

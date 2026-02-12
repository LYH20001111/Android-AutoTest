package com.newland.sdk.module.pin;

/**
 * the algorithm choices
 * Created by youjf on 2019/7/25 10:52
 */
public enum AlgorithmMode {
    /**
     * DES Algorith mode
     */
    DES(0,1),
    /**
     * SM4 Algorith mode
     */
    SM4(2,3),
    /**
     * AES Algorith mode
     */
    AES(1,2),


    HMAC(-1,-1);
    /*
	overseaIndex 用于oversea外接键盘删除key时传参
	 */
    private int overseaIndex;
    /*
	spIndex针对国内3.2.19版本以上外接密码键盘，用于删除密钥使用
	 */

    private int spIndex;
    private AlgorithmMode(int overseaIndex,int spIndex){
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

package com.newland.ndk.h;

/**
 * Copyright © 2023 Fujian Newland Payment Technology Co., Ltd
 * Author: wuhh
 * Date: 2024/12/18 10:38
 * Description:
 * History:
 * <author> <time> <version> <desc>
 */
public class ST_NDK_SEC_DUKPT_DERIVATE_DATA {
    public EM_NDK_SEC_KEY_ALG KeyAlg; //指定派生密钥的算法
    public EM_NDK_SEC_DUKPT_DERIVATE_USAGE DerivateUsage; //指定派生密钥
    public int nKeyLen;//指定派生密钥的长度
}

package com.newland.sdk.module.ecdhe;

/**
 * Copyright © 2023 Fujian Newland Payment Technology Co., Ltd
 * Author: wuhh
 * Date: 2025/5/12 17:03
 * Description:
 * History:
 * <author> <time> <version> <desc>
 */
public class ST_SEC_ECDHE_KDF_INFO {
    public int kdfType;
    public int mdAlg;
    public int saltLen;
    public byte[] salt;
    public int infoLen;
    public byte[] info;
}

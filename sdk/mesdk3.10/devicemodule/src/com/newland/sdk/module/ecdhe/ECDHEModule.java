package com.newland.sdk.module.ecdhe;

import com.newland.ndk.napi.ST_SEC_KEYIN_DATA;

/**
 * Copyright © 2023 Fujian Newland Payment Technology Co., Ltd
 * Author: wuhh
 * Date: 2025/5/12 16:55
 * Description:
 * History:
 * <author> <time> <version> <desc>
 */
public interface ECDHEModule {
    public boolean init();
    public byte[] generateKeyPair(ECCType eccType);
    public boolean generateSK(ST_SEC_KEYIN_DATA keyinData,ST_SEC_ECDHE_KDF_INFO kdfInfo,byte[] datain);
    public boolean release();
}

package com.newland.ndk.h;

/**
 * Copyright © 2023 Fujian Newland Payment Technology Co., Ltd
 * Author: wuhh
 * Date: 2024/12/18 11:08
 * Description:
 * History:
 * <author> <time> <version> <desc>
 */
public class ST_AESDUKPT_KEYINFO {
    public byte keyIndex;         //密钥ID
    public byte keyType;          //密钥类型
    public int ksnlen;        //ksn长度
    public byte[] ksn = new byte[12];        //ksn
    public int keylen;        //密钥实际长度
    public int keydatalen;    //密钥数据长度
    public byte[] keyvalue = new byte[32];  //密钥数据

}

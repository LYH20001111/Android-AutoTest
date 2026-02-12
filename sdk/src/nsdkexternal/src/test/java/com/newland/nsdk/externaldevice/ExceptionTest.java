package com.newland.nsdk.externaldevice;


import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.exception.NSDKTimeoutException;
import com.newland.nsdk.core.api.common.keymanager.KeyType;

import org.junit.Test;

public class ExceptionTest {
    @Test
    public void tryCatchTest(){
        KeyType keyType = null;
        System.out.println(String.format("Invalid key type(%s), only support DES and AES now.", keyType));

//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        try {
//            outputStream.write(null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            testException(1);
//        } catch (NSDKException e) {
//            if (e instanceof NSDKIllegalParameterException) {
//                System.out.println("NSDKIlligalParameterException");
//            }
//            if (e instanceof NSDKTimeoutException) {
//                System.out.println("NSDKTimeoutException");
//            }
//        }
    }

    public void testException(int type) throws NSDKException {
        if (type == 1) {
            throw new NSDKIllegalParameterException();
        } else {
            throw new NSDKTimeoutException();
        }
    }
}

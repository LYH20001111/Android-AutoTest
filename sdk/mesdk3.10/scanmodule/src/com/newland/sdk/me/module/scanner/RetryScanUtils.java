package com.newland.sdk.me.module.scanner;

import android.newland.os.NlBuild;
import android.os.Build;

import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;


public class RetryScanUtils {
    public static String LastCode = "";
    private int retryCount = 0;//和上次码值一样，重新尝试解码
    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger(RetryScanUtils.class);

    public boolean isToDoRetry(String currentCode){
        if(LastCode==null || LastCode.equals("") || currentCode == null || currentCode.equals("")){
            RetryScanUtils.LastCode = currentCode;
            return false;
        }
        if(isNeedTryAgain() && currentCode.equals(LastCode) && retryCount<3){
            deviceLogger.error("[RetryScanUtils] 和上次码值一样:" + ";LastCode:" + LastCode + ";currentCode:" + currentCode + ";retryCount:" + retryCount);
            retryCount = retryCount+1;
            return true;
        }
        RetryScanUtils.LastCode = currentCode;
        return false;
    }

    /**
     * 是否需要重新尝试解码，规避码值重复问题
     * N910 pro机器V1.0.43固件之前，扫一维码有概率返回上一笔的码值
     * @return
     */
    private boolean isNeedTryAgain(){
        try {
            if(Build.MODEL!=null && Build.MODEL.equalsIgnoreCase("N910 Pro")){
                String firmVer = NlBuild.VERSION.NL_FIRMWARE;
                if(firmVer.startsWith("V") && (firmVer.compareToIgnoreCase("V1.0.43")<0) ){
                    return true;
                }
                if(firmVer.startsWith("T") && (firmVer.compareToIgnoreCase("T1.0.43")<0) ){
                    return true;
                }
            }
        }catch (Exception | Error e){
            e.printStackTrace();
        }
        return false;
    }
}

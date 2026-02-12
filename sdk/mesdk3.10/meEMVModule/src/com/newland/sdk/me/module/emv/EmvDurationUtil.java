package com.newland.sdk.me.module.emv;

public class EmvDurationUtil {
    private static long emvFlowTime = 0;//emv流程耗时
    private static boolean isStop = false;
    /**
     * 开始EMV,记录开始时间
     */
    public static void startRecordEMVTime() {
        isStop = false;
        emvFlowTime = 0;
    }

    /**
     * 获取EMV耗时，从PPSE到GPO的耗时,去除银联卡df71 9f51 9f77 这3个tag的耗时
     */
    public static long getEmvDuration() {
        return EmvDurationUtil.emvFlowTime;
    }


    /**

    /**
     * 添加emv流程耗时
     */
    public static void addEmvTime(long time) {
        if(isStop){
            return;
        }
        EmvDurationUtil.emvFlowTime += time;
    }

    /**
     * 停止EMV,记录结束时间
     */
    public static void stopRecordEMVTime() {
        isStop = true;
    }
}

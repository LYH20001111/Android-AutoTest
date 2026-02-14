#include <string.h>
#include <stdlib.h>
#include <comm.h>
#include "ndk.h"
#include "log.h"
#include "api.h"
#include "napi.h"
#include "crypto.h"

int FDevice_SetDateTime(unsigned char *pbuf, int len) {
    char pdata[5];
    int year, month, day, hour, minute, second;
    struct tm pstTime;
    int leap_flag;
    int leap[2][12] = {
            {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31},  //平年
            {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}   //闰年
    };

    int i = 0;

    for (i = 0; i < 14; i++) {
        if ((pbuf[i] < 0x30) || (pbuf[i] > 0x39)) {
            return NDK_ERR_PARA;
        }
    }
    memset(&pstTime, 0x00, sizeof(pstTime));

    // year
    memcpy(pdata, pbuf + 0, 4);
    pdata[4] = 0;
    year = atoi(pdata);
    if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0))
        leap_flag = 1;
    else
        leap_flag = 0;
    pstTime.tm_year = year - 1900;

    // month
    memcpy(pdata, pbuf + 4, 2);
    pdata[2] = 0;
    month = atoi(pdata);
    if (month > 12) {
        return NDK_ERR_PARA;//月份大于12则返回参数错误应答码
    }
    pstTime.tm_mon = month - 1;

    // date
    memcpy(pdata, pbuf + 6, 2);
    pdata[2] = 0;
    day = atoi(pdata);
    if (day > leap[leap_flag][month - 1]) {
        return NDK_ERR_PARA;//日期超过当月最大天数则返回参数错误应答码
    }
    pstTime.tm_mday = day;

    // tm_hour
    memcpy(pdata, pbuf + 8, 2);
    pdata[2] = 0;
    hour = atoi(pdata);
    if (hour > 24) {
        return NDK_ERR_PARA;//时超过24则返回参数错误应答码
    }
    pstTime.tm_hour = hour;

    // tm_min
    memcpy(pdata, pbuf + 10, 2);
    pdata[2] = 0;
    minute = atoi(pdata);
    if (minute >= 60) {
        return NDK_ERR_PARA;//分钟大于59则返回参数错误应答码
    }
    pstTime.tm_min = minute;

    // tm_sec
    memcpy(pdata, pbuf + 12, 2);
    pdata[2] = 0;
    second = atoi(pdata);
    if (second >= 60) {
        return NDK_ERR_PARA;//秒数大于59则返回参数错误应答码
    }
    pstTime.tm_sec = second;

    if(!EXEC_NDK("NDK_SysSetPosTime",NDK_SysSetPosTime(pstTime),NDK_OK)){
        return NDK_ERR;
    }
    return NDK_OK;
}

int FDevice_GetDateTime(unsigned char *pOut, int *len) {
    struct tm pstTime;
    if(!EXEC_NDK("NDK_SysGetPosTime",NDK_SysGetPosTime(&pstTime),NDK_OK)){
        return NDK_ERR;
    }
    char time[15] = {0};
    sprintf(time, "%04d%02d%02d%02d%02d%02d", \
            pstTime.tm_year + 1900, \
            pstTime.tm_mon + 1, \
            pstTime.tm_mday,\
            pstTime.tm_hour,\
            pstTime.tm_min,\
            pstTime.tm_sec);
    memcpy(pOut, time, 15);
    return NDK_OK;
}
int FDevice_GetSN(unsigned char *pOut) {
    int len;
    if(!EXEC_NDK("NAPI_SysGetInfo",NAPI_SysGetInfo(SN,pOut,&len),NDK_OK)){
        return NDK_ERR;
    }
    return len;
}

int NDK_GetSN(unsigned char *pOut) {
    int len;
    if(!EXEC_NDK("NDK_SysGetPosInfo",NDK_SysGetPosInfo(SYS_HWINFO_GET_POS_USN,&len,pOut),NDK_OK)){
        return NDK_ERR;
    }
    return len;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_setSysKeyVol(JNIEnv *env, jobject thiz,
                                                             jboolean is_open) {
    int sel = 0;
    if (is_open) {
        sel = 1;
    }


    int ret = NDK_SysKeyVolSet(sel);
    LOGD_FMT("NDK_SysKeyVolSet ret = %d, sel = %d", ret, sel);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_setSysBeep_1Extern(JNIEnv *env, jobject thiz,
                                                                   jint type, jint volume) {
    int ret = NDK_SysSetBeepVol_Extern(type, volume);
    LOGD_FMT("NDK_SysSetBeepVol_Extern ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1LedFuncModeSet(JNIEnv *env, jobject thiz,
                                                                    jint device_type,
                                                                    jint interval) {
    int ret = NDK_LedFuncModeSet(device_type, interval);
    LOGD_FMT("NDK_LedFuncModeSet ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1SysSetKeyLongPress(JNIEnv *env, jobject thiz,
                                                                        jint keys, jint status) {

    EM_SYS_KEY sysKey = (EM_SYS_KEY)keys;
    LOGD_FMT("sysKey[%d], status[%d]", sysKey, status);
    int ret = NDK_SysSetKeyLongPress(sysKey, (int)status);
    LOGD_FMT("NDK_SysSetKeyLongPress ret[%d]", ret);
    return (jint)ret;
}
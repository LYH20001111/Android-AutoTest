
#include <jni.h>
#include <stdint.h>
#include <crypto.h>
#include <string.h>
#include <malloc.h>
#include "api.h"
#include "crypto.h"
#include "threadtool.h"
#include "plugincard.h"
#include "log.h"
#include "card.h"
#include "printer.h"

JavaVM *gJavaVM = NULL;


/*
 * Class:     com_newland_nsdk_module_jni_NSDKJni
 * Method:    setDeviceDate
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_setDeviceDate
        (JNIEnv *env, jobject obj, jbyteArray date){
    int len  = (*env)->GetArrayLength(env,date);
    uchar *buf = (*env)->GetByteArrayElements(env,date,0);
    LOGD_STR("date",buf,len);
    int ret = FDevice_SetDateTime(buf,len);
    LOGD_FMT(">>>FDevice_SetDateTime ret[%d]",ret);
    (*env)->ReleaseByteArrayElements(env,date,buf,0);
    return ret;
}

/*
 * Class:     com_newland_nsdk_module_jni_NSDKJni
 * Method:    getDeviceDate
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_getDeviceDate
        (JNIEnv *env, jobject obj, jbyteArray date){
    uchar dateTime[20];
    memset(dateTime,0, sizeof(dateTime));
    int ret = FDevice_GetDateTime(dateTime,NULL);
    if(date!=NULL){
        (*env)->SetByteArrayRegion(env, date, 0, strlen(dateTime), dateTime);
    }
    LOGD_FMT(">>>FDevice_GetDateTime ret[%d]",ret);
    return ret;
}

JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_getDeviceSN
        (JNIEnv *env, jobject obj, jbyteArray sn){
    uchar snStr[30];
    int snLen = 0;
    memset(snStr,0, sizeof(snStr));
    int ret = FDevice_GetSN(snStr);
    if(sn!=NULL && ret > 0){
        (*env)->SetByteArrayRegion(env, sn, 0, ret, snStr);
    }
    LOGD_FMT(">>>FDevice_GetSN ret[%d]",ret);
    return ret;
}


/*
 * Class:     com_newland_nsdk_module_jni_NSDKJni
 * Method:    NDK_SysGetCapability
 * Signature: (I[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1SysGetCapability
        (JNIEnv *env, jobject obj , jint len , jbyteArray caps){
    uchar buf[len];
    memset(buf,0, sizeof(buf));
    // 返回一个长度为 9 的 buffer,依次表示是否支持:统计,锁,外卡,打印,射频,IC卡,磁卡,密码键盘,CBC大数据
    int ret = NDK_SysGetCapability(len,buf);
    if(ret == 0 && caps != NULL){
        (*env)->SetByteArrayRegion(env, caps, 0, len, buf);
    }
    return ret;
}

/*
 * Class:     com_newland_nsdk_module_jni_NSDKJni
 * Method:    operateLight
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_operateLight
        (JNIEnv *env, jobject obj, jint status,jint color){
    return FLight_SetStatus(status,color);
}

/**
 * 此方法适用于 P300 上有 5 个灯的，每个灯可以控制亮红、绿、蓝颜色和状态
 * @param env
 * @param thiz
 * @param light_params
 * @return
 */
JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_operateLightLT1118(JNIEnv *env, jobject thiz,
                                                                   jintArray light_params,
                                                                   jint params_len, jint light_count) {

    int *light = (*env)->GetIntArrayElements(env,light_params,0);
    int ret = FLight_SetStatusLT1118(light, params_len, light_count);
    (*env)->ReleaseIntArrayElements(env,light_params,light,0);
    return ret;
}

/*
 * Class:     com_newland_nsdk_module_jni_NSDKJni
 * Method:    blinkLight
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_blinkLight
        (JNIEnv *env, jobject obj , jint times,jint lightColor,jint interval){
    return FLight_Blink(times,lightColor,interval);
}

/*
 * Class:     com_newland_nsdk_module_jni_NSDKJni
 * Method:    blinkVirtualLight
 * SignatureL (III)I
 */
JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_blinkVirtualLight(JNIEnv *env, jobject thiz,
                                                                  jint count, jint color,
                                                                  jint interval) {
    return FLight_Blink_Virtual(count, color, interval);
}

/*
 * Class:     com_newland_nsdk_module_jni_NSDKJni
 * Method:    RFM0Authenticate
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_RFM0Authenticate
        (JNIEnv *env, jobject obj, jbyteArray send){
    int sendLen  = (*env)->GetArrayLength(env,send);
    uchar *sendbuf = (*env)->GetByteArrayElements(env,send,0);
    int ret = FRfid_M0AuthKey(sendbuf,sendLen);
    (*env)->ReleaseByteArrayElements(env,send,sendbuf,0);
    return ret;
}

/*
 * Class:     com_newland_nsdk_module_jni_NSDKJni
 * Method:    RFM0ReadBlockData
 * Signature: (I[B[I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_RFM0ReadBlockData
        (JNIEnv *env, jobject obj, jint blockNo, jbyteArray recv, jintArray recvLen){
    uchar recvBuf[4096];int rlen;
    memset(recvBuf, 0, sizeof(recvBuf));
    int ret = FRfid_M0ReadBlock(blockNo,recvBuf,&rlen);
    if(ret == 0){
        (*env)->SetIntArrayRegion(env,recvLen,0,1,&rlen);
        (*env)->SetByteArrayRegion(env,recv,0,rlen,recvBuf);
    }
    return ret;
}

/*
 * Class:     com_newland_nsdk_module_jni_NSDKJni
 * Method:    RFM0WriteBlockData
 * Signature: (I[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_RFM0WriteBlockData
        (JNIEnv *env, jobject obj, jint blockNo, jbyteArray send){
    int sendLen  = (*env)->GetArrayLength(env,send);
    uchar *sendbuf = (*env)->GetByteArrayElements(env,send,0);
    int ret = FRfid_M0WriteBlock(blockNo,sendbuf,sendLen);
    (*env)->ReleaseByteArrayElements(env,send,sendbuf,0);
    return ret;
}

/*
 * Class:     com_newland_nsdk_module_jni_NSDKJni
 * Method:    RFM1Authenticate
 * Signature: (I[BI[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_RFM1Authenticate
        (JNIEnv *env, jobject obj, jint rfKeyMode, jbyteArray uid, jint blockNo, jbyteArray key){
    uchar *sendbuf = (*env)->GetByteArrayElements(env,uid,0);
    uchar *keybuf = (*env)->GetByteArrayElements(env,key,0);
    int ret = FRfid_M1AuthKey(rfKeyMode,sendbuf,blockNo,keybuf);
    (*env)->ReleaseByteArrayElements(env,uid,sendbuf,0);
    (*env)->ReleaseByteArrayElements(env,key,keybuf,0);
    return ret;
}

/*
 * Class:     com_newland_nsdk_module_jni_NSDKJni
 * Method:    RFM1ReadBlockData
 * Signature: (I[B[I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_RFM1ReadBlockData
        (JNIEnv *env, jobject obj, jint blockNo, jbyteArray recv, jintArray recvLen){
    uchar recvBuf[4096];int rlen;
    memset(recvBuf, 0, sizeof(recvBuf));
    int ret = FRfid_M1ReadBlock(blockNo,recvBuf,&rlen);
    if(ret == 0){
        (*env)->SetIntArrayRegion(env,recvLen,0,1,&rlen);
        (*env)->SetByteArrayRegion(env,recv,0,rlen,recvBuf);
    }
    return ret;
}

/*
 * Class:     com_newland_nsdk_module_jni_NSDKJni
 * Method:    RFM1WriteBlockData
 * Signature: (I[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_RFM1WriteBlockData
        (JNIEnv *env, jobject obj, jint blockNo, jbyteArray data){
    int sendLen  = (*env)->GetArrayLength(env,data);
    uchar *sendbuf = (*env)->GetByteArrayElements(env,data,0);
    int ret = FRfid_M1WriteBlock(blockNo,sendbuf,sendLen);
    (*env)->ReleaseByteArrayElements(env,data,sendbuf,0);
    return ret;
}

/*
 * Class:     com_newland_nsdk_module_jni_NSDKJni
 * Method:    RFM1Increment
 * Signature: (I[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_RFM1Increment
        (JNIEnv *env, jobject obj, jint blockNo, jbyteArray data){
    uchar *sendbuf = (*env)->GetByteArrayElements(env,data,0);
    int ret = FRfid_M1Increment(blockNo,sendbuf);
    (*env)->ReleaseByteArrayElements(env,data,sendbuf,0);
    return ret;
}

/*
 * Class:     com_newland_nsdk_module_jni_NSDKJni
 * Method:    RFM1Decrement
 * Signature: (I[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_RFM1Decrement
        (JNIEnv *env, jobject obj, jint blockNo, jbyteArray data){
    uchar *sendbuf = (*env)->GetByteArrayElements(env,data,0);
    int ret = FRfid_M1Decrement(blockNo,sendbuf);
    (*env)->ReleaseByteArrayElements(env,data,sendbuf,0);
    return ret;
}

/*
 * Class:     com_newland_nsdk_module_jni_NSDKJni
 * Method:    NDK_PrnModuleInit
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1PrnModuleInit
        (JNIEnv *env, jobject obj){
    return NDK_PrnModuleInit();
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1PrnSetGreyScale(JNIEnv *env, jobject thiz,
                                                                           jint un_grey) {
    return NDK_PrnSetGreyScale(un_grey);
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_TTF_1PrnSetPaperSize(JNIEnv *env, jobject thiz,
                                                                           jint size) {
    return TTF_PrnSetPaperSize(size);
}

#define INDEX_SYS_EVENT_MAGCARD     0
#define INDEX_SYS_EVENT_ICCARD      1
#define INDEX_SYS_EVENT_RFID        2
#define INDEX_SYS_EVENT_PIN         3
#define INDEX_SYS_EVENT_PRNTER      4
#define INDEX_SYS_EVENT_MAX         5

typedef int (*NotifyEvent)(EM_SYS_EVENT eventNum,int msgLen, char * msg);

typedef struct{
    jobject eventObj;
    jmethodID eventMid;
}ST_SYS_EVENT_CTL;

ST_SYS_EVENT_CTL sysEventCtls[5];

void SysEventInit(){
    int i = 0;
    for(;i<INDEX_SYS_EVENT_MAX;i++){
        sysEventCtls[i].eventObj = NULL;
        sysEventCtls[i].eventMid = NULL;
    }
}

static void __doEventCallBack(int index,int eventNum){
    LOGD_FMT("");
    if(index < 0 || index >= INDEX_SYS_EVENT_MAX){
        LOGD_FMT("index[%d] Err.",index);
        return;
    }
    JNIEnv *env = NULL;
    jboolean isAttached = JNI_FALSE;
    if(gJavaVM == NULL){
        LOGD_FMT("gJavaVM[%d]",gJavaVM);
        return;
    }
    int ret = (*gJavaVM)->GetEnv(gJavaVM,(void **) &env, JNI_VERSION_1_4);
    if(ret < 0 ) {
        ret =  (*gJavaVM)->AttachCurrentThread(gJavaVM,(JNIEnv **) &env, NULL);
        if (ret < 0) {
            LOGD_FMT(">>>AttachCurrentThread error.");
            return;
        }
        isAttached = JNI_TRUE;
    }
    if(sysEventCtls[index].eventObj == NULL || sysEventCtls[index].eventMid == NULL) {
        LOGD_FMT("eventObj[%d] eventMid[%d]",sysEventCtls[index].eventObj,sysEventCtls[index].eventMid);
        return;
    }
    (*env)->CallVoidMethod(env,sysEventCtls[index].eventObj,sysEventCtls[index].eventMid,(jint)eventNum,0,NULL);
    if(isAttached)
        (*gJavaVM)->DetachCurrentThread(gJavaVM);
    LOGD_FMT("succ");
}
static int __notifyPinEvent(EM_SYS_EVENT eventNum, int msgLen, char * msg){
    LOGD_FMT("eventNum[%d]",eventNum);
    if(eventNum == SYS_EVENT_PIN||eventNum == SYS_EVENT_NONE){
        __doEventCallBack(INDEX_SYS_EVENT_PIN,eventNum);
    }
}
static int __notifyPrnEvent(EM_SYS_EVENT eventNum, int msgLen, char * msg){
    LOGD_FMT("eventNum[%d]",eventNum);
    if(eventNum == SYS_EVENT_PRNTER||eventNum == SYS_EVENT_NONE){
        __doEventCallBack(INDEX_SYS_EVENT_PRNTER,eventNum);
    }
}

static int __notifyICEvent(EM_SYS_EVENT eventNum, int msgLen, char *msg) {
    LOGD_FMT("eventNum[%d]", eventNum);
    if (eventNum == SYS_EVENT_ICCARD || eventNum == SYS_EVENT_NONE) {
        __doEventCallBack(INDEX_SYS_EVENT_ICCARD, eventNum);
    }
}

static int __notifyMAGEvent(EM_SYS_EVENT eventNum, int msgLen, char *msg) {
    LOGD_FMT("eventNum[%d]", eventNum);
    if (eventNum == SYS_EVENT_MAGCARD || eventNum == SYS_EVENT_NONE) {
        __doEventCallBack(INDEX_SYS_EVENT_MAGCARD, eventNum);
    }
}

static int __notifyRFIDEvent(EM_SYS_EVENT eventNum, int msgLen, char *msg) {
    LOGD_FMT("eventNum[%d]", eventNum);
    if (eventNum == SYS_EVENT_RFID || eventNum == SYS_EVENT_NONE) {
        __doEventCallBack(INDEX_SYS_EVENT_RFID, eventNum);
    }
}

static int getEventInfo(int event, int *event2, int *index,NotifyEvent *notifyEvent, int *eventNum){
    int eventNumber = 0;
    if ((event & SYS_EVENT_PIN) == SYS_EVENT_PIN) {
        *index = INDEX_SYS_EVENT_PIN;
        index++;
        *notifyEvent = __notifyPinEvent;
        notifyEvent++;
        *event2 = SYS_EVENT_PIN;
        event2++;
        eventNumber++;
    }
    if ((event & SYS_EVENT_PRNTER) == SYS_EVENT_PRNTER) {
        *index = INDEX_SYS_EVENT_PRNTER;
        index++;
        *notifyEvent = __notifyPrnEvent;
        notifyEvent++;
        *event2 = SYS_EVENT_PRNTER;
        event2++;
        eventNumber++;
    }
    if ((event & SYS_EVENT_MAGCARD) == SYS_EVENT_MAGCARD) {
        *index = INDEX_SYS_EVENT_MAGCARD;
        index++;
        *notifyEvent = __notifyMAGEvent;
        notifyEvent++;
        *event2 = SYS_EVENT_MAGCARD;
        event2++;
        eventNumber++;
    }
    if ((event & SYS_EVENT_ICCARD) == SYS_EVENT_ICCARD) {
        *index = INDEX_SYS_EVENT_ICCARD;
        index++;
        *notifyEvent = __notifyICEvent;
        notifyEvent++;
        *event2 = SYS_EVENT_ICCARD;
        event2++;
        eventNumber++;
    }
    if ((event & SYS_EVENT_RFID) == SYS_EVENT_RFID) {
        *index = INDEX_SYS_EVENT_RFID;
        index++;
        *notifyEvent = __notifyRFIDEvent;
        notifyEvent++;
        *event2 = SYS_EVENT_RFID;
        event2++;
        eventNumber++;
    }
    *eventNum = eventNumber;
    return NDK_OK;
}
static int unregisterEvent(int event) {
    int ret = 0;
    if ((event & SYS_EVENT_PIN) == SYS_EVENT_PIN) {
        ret = NDK_SYS_UnRegisterEvent(SYS_EVENT_PIN);
        if (ret != NDK_OK) {
            return ret;
        }
    }
    if ((event & SYS_EVENT_PRNTER) == SYS_EVENT_PRNTER) {
        ret = NDK_SYS_UnRegisterEvent(SYS_EVENT_PRNTER);
        if (ret != NDK_OK) {
            return ret;
        }
    }
    if ((event & SYS_EVENT_RFID) == SYS_EVENT_RFID) {
        ret = NDK_SYS_UnRegisterEvent(SYS_EVENT_RFID);
        if (ret != NDK_OK) {
            return ret;
        }
    }
    if ((event & SYS_EVENT_ICCARD) == SYS_EVENT_ICCARD) {
        ret = NDK_SYS_UnRegisterEvent(SYS_EVENT_ICCARD);
        if (ret != NDK_OK) {
            return ret;
        }
    }
    if ((event & SYS_EVENT_MAGCARD) == SYS_EVENT_MAGCARD) {
        ret = NDK_SYS_UnRegisterEvent(SYS_EVENT_MAGCARD);
        if (ret != NDK_OK) {
            return ret;
        }
    }
    return NDK_OK;
}

JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1SYS_1RegisterEvent(JNIEnv *env, jobject jo, jint event, jint tiomeOutms, jobject callback){
    int index[6] = {0};
    int eventNum = 0;
    int events[6] = {0};
    NotifyEvent notifyEvent[6];
    memset(notifyEvent, 0x00, sizeof(notifyEvent));
    getEventInfo(event, events, index, notifyEvent, &eventNum);
    for (int i = 0; i < eventNum; i++) {
        jobject eventObj = sysEventCtls[index[i]].eventObj;
        LOGD_FMT("event[0x%x] tiomeOutms[%d] index[%d] eventObj[%d] notifyEvent[%d]",events[i],tiomeOutms,index[i],eventObj,notifyEvent[i]);
        if(eventObj != NULL) {
            (*env)->DeleteGlobalRef(env, eventObj);
        }
        sysEventCtls[index[i]].eventObj = (*env)->NewGlobalRef(env, callback);
        jclass cls=  (*env)->GetObjectClass(env, callback);
        sysEventCtls[index[i]].eventMid = (*env)->GetMethodID(env,cls,"callback","(II[B)V");
        int ret = NDK_SYS_RegisterEvent(events[i], tiomeOutms, notifyEvent[i]);
        if (ret != 0) {
            return ret;
        }
    }

    return NDK_OK;
}

JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1SYS_1UnRegisterEvent(JNIEnv *env, jobject jo, jint event){
    int ret = unregisterEvent(event);
    LOGD_FMT(">>>NDK_SYS_UnRegisterEvent ret[%d], event[%d]", ret, event);
    return ret;
}


jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    DEBUG_INIT;

    JNIEnv *env = NULL;
    gJavaVM = vm;
    int status = (*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_4);
    if (status != JNI_OK) {
        LOGD_FMT("GetEnv failed!");
        return JNI_ERR;
    }


    if (Ndk_Dlload() != 0) {
        LOGD_FMT(">>>");
    }
    THREAD_MUTEX_CTLS_CREATE;
    THREAD_COND_CTLS_CREATE;
//    CardReader_GetMethodID(env);
    return JNI_VERSION_1_4;
}


JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_RFM1Transfer(JNIEnv *env, jobject thiz, jint blockNum) {
    int ret = FRfid_M1Transfer(blockNum);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_RFM1Restore(JNIEnv *env, jobject thiz, jint blockNum) {
    int ret = FRfid_M1Restore(blockNum);
    return ret;
}

JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_RFFelicaTransmit
        (JNIEnv *env, jobject obj, jbyteArray send, jbyteArray recv, jintArray recvLen){
    int sendLen  = (*env)->GetArrayLength(env,send);
    uchar *sendbuf = (*env)->GetByteArrayElements(env,send,0);

    uchar recvBuf[4096];int rlen;
    memset(recvBuf, 0, sizeof(recvBuf));
    int ret = FRfid_FelicaApdu(sendbuf,sendLen,recvBuf,&rlen);
    if(ret == 0){
        (*env)->SetIntArrayRegion(env,recvLen,0,1,&rlen);
        (*env)->SetByteArrayRegion(env,recv,0,rlen,recvBuf);
    }
    (*env)->ReleaseByteArrayElements(env,send,sendbuf,0);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1Beep(JNIEnv *env, jobject thiz, jint frequency,
                                                                 jint duration) {
    int ret = NAPI_SysBeepIt(frequency, duration);
    LOGD_FMT(">>> NAPI_SysBeepIt result: %d", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1PortOpen(JNIEnv *env, void *thiz,
                                                                jint com_number,
                                                                jstring config_str) {
    char *buf = (*env)->GetStringUTFChars(env, config_str, 0);
    int ret = NDK_PortOpen(com_number, buf);
    (*env)->ReleaseStringUTFChars(env, config_str, buf);
    LOGD_FMT(">>>NDK_PortOpen ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1PortClose(JNIEnv *env, jobject thiz,
                                                                     jint com_number) {
    int ret = NDK_PortClose(com_number);
    LOGD_FMT(">>>NDK_PortClose ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1PortRead(JNIEnv *env, jobject thiz,
                                                                    jint com_number, jint max_len,
                                                                    jint timeout, jbyteArray out_data,
                                                                    jintArray out_data_len) {
    uchar outBuf[max_len];
    int outLen;
    memset(outBuf, 0, sizeof(outBuf));
    int ret = NDK_PortRead(com_number, max_len, outBuf, timeout, &outLen);

    LOGD_FMT(">>>outLen[%d]", outLen);

    /**
     * NDK串口数据小于maxlen时，会返回-10，并返回实际接收到的数据
     */
    if (ret == 0 || ret == -10) {
        (*env)->SetIntArrayRegion(env, out_data_len, 0, 1, &outLen);
        (*env)->SetByteArrayRegion(env, out_data, 0, outLen, outBuf);
    }
    LOGD_FMT(">>>NDK_PortRead ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1PortWrite(JNIEnv *env, jobject thiz,
                                                                     jint com_number, jint length,
                                                                     jbyteArray data) {
    uchar *pData = NULL;
    if (data != NULL) {
        pData = (uchar *) (*env)->GetByteArrayElements(env, data, NULL);
    }

    int ret = NDK_PortWrite(com_number, length, pData);

    if (data != NULL) {
        (*env)->ReleaseByteArrayElements(env, data, pData, NULL);
    }

    LOGD_FMT(">>>NDK_PortWrite ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1PortClrBuf(JNIEnv *env, jobject thiz,
                                                                      jint com_number) {
    int ret = NDK_PortClrBuf(com_number);
    LOGD_FMT(">>>NDK_PortClrBuf ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1GetDeviceSN
        (JNIEnv *env, jobject thiz, jbyteArray sn) {
    uchar snStr[30];
    int snLen = 0;
    memset(snStr,0, sizeof(snStr));
    int ret = NDK_GetSN(snStr);
    if(sn!=NULL && ret > 0){
        (*env)->SetByteArrayRegion(env, sn, 0, ret, snStr);
    }
    LOGD_FMT(">>>NDK_GetSN ret[%d]",ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1SysGetPosInfo(JNIEnv *env, jobject thiz,
                                                                   jint info_key, jbyteArray sn) {
    char snStr[50];
    uint snLen = 0;
    memset(snStr,0, sizeof(snStr));
    int ret = NDK_SysGetPosInfo(info_key,&snLen,snStr);
    LOGD_FMT(">>>NDK_SysGetPosInfo[%d] ret[%d] len[%d]",info_key, ret, snLen);
    if(sn!=NULL && ret == 0 && snLen > 0){
        (*env)->SetByteArrayRegion(env, sn, 0, snLen, snStr);
    }
    return snLen;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_getTamperStatus(JNIEnv *env, jobject thiz,
                                                                jintArray status) {
    int temp;
    int ret = NDK_SecGetTamperStatus(&temp);
    LOGD_FMT(">>>NDK_SecGetTamperStatus ret[%d], status[%d]", ret, temp);
    if(ret == 0){
        (*env)->SetIntArrayRegion(env,status,0,1,&temp);
    }
    return ret;
}


JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_getErrorMsg(JNIEnv *env, jobject thiz,
                                                            jint error_code, jbyteArray error_msg) {
    uchar *errorMsg;
    errorMsg = getErrMsg(error_code);
    if(errorMsg != NULL) {
        (*env)->SetByteArrayRegion(env, error_msg, 0, 128, errorMsg);
        LOGD_FMT(">>>getErrMsg ret[%d], ErrMsg[%s]", error_code, errorMsg);
    }
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1SecGetDrySR(JNIEnv *env, jobject thiz,
                                                                 jintArray value) {
    // TODO: implement NDK_SecGetDrySR()
    int v = 0;
    int ret = NDK_SecGetDrySR(&v);
    LOGD_FMT(">>>NDK_SecGetDrySR ret[%d], value[%02x]", ret, v);
    if (ret == 0) {
        (*env)->SetIntArrayRegion(env, value, 0, 1, &v);
    }
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1ScrBacklight(JNIEnv *env, jobject thiz,
                                                                  jint status) {
    int ret = NDK_ScrBackLight(status);
    LOGD_FMT(">>>NDK_ScrBackLight ret[%d], status[%d]", ret, status);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1ScrDispString(JNIEnv *env, jobject thiz,
                                                                   jint start_x, jint start_y,
                                                                   jstring display_string,
                                                                   jint character_size) {
    const char *content = (*env)->GetStringUTFChars(env, display_string, NULL);
    int ret = NDK_ScrDispString(start_x, start_y, content, character_size);
    LOGD_FMT(">>>NDK_ScrDispString ret[%d]", ret);
    (*env)->ReleaseStringUTFChars(env, display_string, content);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1ScrDrawBitmapV(JNIEnv *env, jobject thiz,
                                                                    jint x, jint y, jint width,
                                                                    jint height,
                                                                    jbyteArray bitmap_data) {
    const unsigned char *bitmapData = (*env)->GetByteArrayElements(env, bitmap_data, NULL);

    LOGD_FMT("x[%d], y[%d], width[%d], height[%d]", x, y, width, height);
    int ret = NDK_ScrDrawBitmapV(x, y, width, height, bitmapData);
    LOGD_FMT(">>>NDK_ScrDrawBitmapV ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1ScrClrs(JNIEnv *env, jobject thiz) {
    int ret = NDK_ScrClrs();
    LOGD_FMT(">>>NDK_ScrClrs ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_RFFelicaSetTimeout(JNIEnv *env, jobject thiz,
                                                                   jint timeout) {
    int ret = NDK_FelicaSetTimeout(timeout);
    LOGD_FMT(">>>NDK_FelicaSetTimeout ret[%d], timeout[%d]", ret, timeout);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_RFFelicaPolling(JNIEnv *env, jobject thiz,
                                                                jbyteArray system_code,
                                                                jbyte request_code,
                                                                jbyte timeslot,
                                                                jbyteArray receive_data,
                                                                jintArray receive_data_len) {

    felica_param_t felicaParam;
    memset(&felicaParam, 0x00, sizeof(felica_param_t));
    unsigned char *systemCode = (*env)->GetByteArrayElements(env, system_code, NULL);
    memcpy(felicaParam.systemcode, systemCode, 2);
    felicaParam.request_code = request_code;
    felicaParam.timeslot = timeslot;
    unsigned char receiveData[128];
    int receiveDataLen = 0;
    int ret = NDK_FelicaPoll(felicaParam, receiveData, &receiveDataLen);
    LOGD_FMT(">>>NDK_FelicaPoll ret[%d], receiceDataLen[%d]", ret, receiveDataLen);
    if (ret == 0) {
        (*env)->SetIntArrayRegion(env, receive_data_len, 0, 1, &receiveDataLen);
        (*env)->SetByteArrayRegion(env, receive_data, 0, receiveDataLen, receiveData);
    }
    (*env)->ReleaseByteArrayElements(env, system_code, systemCode, 0);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_RFFelicaTransmit2(JNIEnv *env, jobject thiz,
                                                                  jbyteArray send, jbyteArray recv,
                                                                  jintArray recvLen, jint times,
                                                                  jint timeout) {
    int sendLen  = (*env)->GetArrayLength(env,send);
    uchar *sendbuf = (*env)->GetByteArrayElements(env,send,0);

    uchar recvBuf[4096];int rlen;
    memset(recvBuf, 0, sizeof(recvBuf));
    int ret = FRfid_FelicaApdu_retry(sendbuf, sendLen, recvBuf, &rlen, timeout, times);
    if(ret == 0){
        (*env)->SetIntArrayRegion(env,recvLen,0,1,&rlen);
        (*env)->SetByteArrayRegion(env,recv,0,rlen,recvBuf);
    }
    (*env)->ReleaseByteArrayElements(env,send,sendbuf,0);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecGetDeviceStatus(JNIEnv *env, jobject thiz,
                                                                         jintArray status) {
    uint32_t nStatus = 0;
    int ret = NAPI_SecGetDeviceStatus(&nStatus);
    LOGD_FMT(">>> NAPI_SecGetDeviceStatus ret[%d], status[%d]", ret, nStatus);
    if (ret == NAPI_OK) {
        (*env)->SetIntArrayRegion(env, status, 0, 1, &nStatus);
    }
    return ret;
}


JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecSetDeviceStatus(JNIEnv *env, jobject thiz,
                                                                         jint status) {
    int ret = NAPI_SecSetDeviceStatus(status);
    LOGD_FMT(">>> NAPI_SecSetDeviceStatus ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1SysTimeBeep_1Ex(JNIEnv *env, jobject thiz,
                                                                     jint frequency, jint duration,
                                                                     jint volume) {
    int ret = NDK_SysTimeBeep_Ex(frequency, duration, volume);
    LOGD_FMT(">>> NDK_SysTimeBeep_Ex ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1PrnSetParam(JNIEnv *env, jobject thiz,
                                                                 jint type, jint value) {
    LOGD_FMT("type[%d], value[%d]", type, value);
    int ret = NDK_PrnSetParam(type, value);
    LOGD_FMT(">>> NDK_PrnSetParam ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1RfidFuncisSupport(JNIEnv *env, jobject thiz,
                                                                       jint type,
                                                                       jintArray result) {
    uchar isSupportResult[1] = {0};
    int ret = NDK_RfidFunisSupport(type, isSupportResult);
    LOGD_FMT(">>> NDK_RfidFunisSupport type[%d], ret[%d]", type, ret);
    if (ret == NDK_OK) {
        int isSupport = isSupportResult[0];
        (*env)->SetIntArrayRegion(env, result, 0, 1, &isSupport);
    }
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1SysSetBeepVol(JNIEnv *env, jobject thiz,
                                                                   jint volume) {
    int ret = NDK_SysSetBeepVol(volume);
    LOGD_FMT(">>> NDK_SysSetBeepVol ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_blinkVirtual(JNIEnv *env, jobject thiz, jint x,
                                                             jint y, jint horizontal,
                                                             jint always_display_background,
                                                             jint count, jint color,
                                                             jint on_duration, jint off_duration) {
    int ret = FLight_blink_Virtual_Advanced(x, y, horizontal, always_display_background, count, color, on_duration, off_duration);
    LOGD_FMT("FLight_blink_Virtual_Advanced ret = %d", ret);
    return ret;
}

void validateFormat(char *buf) {
    buf[strcspn(buf, "\n")] = '\0';
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1SysGetBatteryProperty(JNIEnv *env,
                                                                           jobject thiz,
                                                                           jbyteArray is_support_get_battery_temp,
                                                                           jintArray support_get_battery_temp_len,
                                                                           jbyteArray is_support_get_charge_current,
                                                                           jintArray support_get_charge_current_len,
                                                                           jbyteArray battery_temp,
                                                                           jintArray battery_temp_len,
                                                                           jbyteArray adapter_voltage,
                                                                           jintArray adapter_voltage_len,
                                                                           jbyteArray charge_current,
                                                                           jintArray charge_current_len) {
    int ret = 0;
    char value[1024] = {0};
    int valueLen = 2048;
    int actualLen = 0;
    ret = NDK_SysGetBatteryProperty(IS_GET_BATTRRY_TEMP_SUPPORT, valueLen, value);
    LOGD_FMT("NDK_SysGetBatteryProperty IS_GET_BATTRRY_TEMP_SUPPORT ret[%d]", ret);
    if (ret != NDK_OK) {
        return ret;
    }
    validateFormat(value);
    actualLen = strlen(value);
    (*env)->SetIntArrayRegion(env, support_get_battery_temp_len, 0, 1, &actualLen);
    (*env)->SetByteArrayRegion(env, is_support_get_battery_temp, 0, actualLen, value);

    ret = NDK_SysGetBatteryProperty(IS_GET_CHARGE_CURRENT_SUPPORT, valueLen, value);
    LOGD_FMT("NDK_SysGetBatteryProperty IS_GET_CHARGE_CURRENT_SUPPORT[%d]", ret);
    if (ret != NDK_OK) {
        return ret;
    }
    validateFormat(value);
    actualLen = strlen(value);
    (*env)->SetIntArrayRegion(env, support_get_charge_current_len, 0, 1, &actualLen);
    (*env)->SetByteArrayRegion(env, is_support_get_charge_current, 0, actualLen, value);

    ret = NDK_SysGetBatteryProperty(BATTERY_TEMP, valueLen, value);
    LOGD_FMT("NDK_SysGetBatteryProperty BATTERY_TEMP[%d]", ret);
    if (ret != NDK_OK) {
        char *tempValue = "-1";
        int tempLen = strlen(tempValue);
        (*env)->SetIntArrayRegion(env, battery_temp_len, 0, 1, &tempLen);
        (*env)->SetByteArrayRegion(env, battery_temp, 0, tempLen, tempValue);
    } else {
        validateFormat(value);
        actualLen = strlen(value);
        (*env)->SetIntArrayRegion(env, battery_temp_len, 0, 1, &actualLen);
        (*env)->SetByteArrayRegion(env, battery_temp, 0, actualLen, value);
    }


    ret = NDK_SysGetBatteryProperty(ADAPTER_VOLTAGE, valueLen, value);
    LOGD_FMT("NDK_SysGetBatteryProperty ADAPTER_VOLTAGE[%d]", ret);
    if (ret != NDK_OK) {
        char *tempValue = "-1";
        int tempLen = strlen(tempValue);
        (*env)->SetIntArrayRegion(env, adapter_voltage_len, 0, 1, &tempLen);
        (*env)->SetByteArrayRegion(env, adapter_voltage, 0, tempLen, tempValue);
    } else {
        validateFormat(value);
        actualLen = strlen(value);
        (*env)->SetIntArrayRegion(env, adapter_voltage_len, 0, 1, &actualLen);
        (*env)->SetByteArrayRegion(env, adapter_voltage, 0, actualLen, value);
    }


    ret = NDK_SysGetBatteryProperty(CHARGE_CURRENT, valueLen, value);
    LOGD_FMT("NDK_SysGetBatteryProperty CHARGE_CURRENT[%d]", ret);
    if (ret != NDK_OK) {
        char *tempValue = "-1";
        int tempLen = strlen(tempValue);
        (*env)->SetIntArrayRegion(env, charge_current_len, 0, 1, &tempLen);
        (*env)->SetByteArrayRegion(env, charge_current, 0, tempLen, tempValue);
    } else {
        validateFormat(value);
        actualLen = strlen(value);
        (*env)->SetIntArrayRegion(env, charge_current_len, 0, 1, &actualLen);
        (*env)->SetByteArrayRegion(env, charge_current, 0, actualLen, value);
    }

    return ret;
}
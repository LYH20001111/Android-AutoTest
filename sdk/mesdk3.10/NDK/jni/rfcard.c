#include <jni.h>
#include "ndk.h"
#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <unistd.h>
#include <android/log.h>
#include "__log.h"

#define LOG_TAG "IntelligentLib"
#define LOGI(...) __android_log_print(ANDROID_LOG_VERBOSE,LOG_TAG,__VA_ARGS__)
/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_RfidVersion
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1RfidVersion
        (JNIEnv *env, jobject jo, jbyteArray version) {
    int ret = -1;
    char ver[100] = {0};
    ret = NDK_RfidVersion(ver);
    if (ret == 0)
        (*env)->SetByteArrayRegion(env, version, 0, strlen(ver), ver);
    return ret;
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_RfidInit
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1RfidInit
        (JNIEnv *env, jobject jo, jbyteArray type) {
    return NDK_RfidInit(NULL);
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_RfidOpenRf
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1RfidOpenRf
        (JNIEnv *env, jobject jo) {
    return NDK_RfidOpenRf();
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_RfidCloseRf
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1RfidCloseRf
        (JNIEnv *env, jobject jo) {
    return NDK_RfidCloseRf();
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_RfidPiccState
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1RfidPiccState
        (JNIEnv *env, jobject jo) {
    return NDK_RfidPiccState();
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_RfidSuspend
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1RfidSuspend
        (JNIEnv *env, jobject jo) {
    return NDK_RfidSuspend();
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_RfidResume
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1RfidResume
        (JNIEnv *env, jobject jo) {
    return NDK_RfidResume();
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_RfidPiccType
 * Signature: (B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1RfidPiccType
        (JNIEnv *env, jobject jo, jbyte type) {
    char mtype = type;
    return NDK_RfidPiccType(mtype);
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_RfidPiccDetect
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1RfidPiccDetect
        (JNIEnv *env, jobject jo, jbyteArray type) {
    char mtype = 0;
    int ret = -1;
    ret = NDK_RfidPiccDetect(&mtype);
    if (ret == 0)
        (*env)->SetByteArrayRegion(env, type, 0, 1, &mtype);
    return ret;
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_RfidPiccActivate
 * Signature: ([B[I[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1RfidPiccActivate
        (JNIEnv *env, jobject jo, jbyteArray psPicctype, jintArray pnDatalen,
         jbyteArray psDatabuf) {
    char mtype = 0;
    int len = 0;
    char buf[1024] = {0};
    int ret = -1;
    ret = NDK_RfidPiccActivate(&mtype, &len, buf);
    if (ret == 0) {
        (*env)->SetIntArrayRegion(env, pnDatalen, 0, 1, &len);
        (*env)->SetByteArrayRegion(env, psPicctype, 0, 1, &mtype);
        (*env)->SetByteArrayRegion(env, psDatabuf, 0, len, buf);
    }
    return ret;
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_RfidPiccDeactivate
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1RfidPiccDeactivate
        (JNIEnv *env, jobject jo, jint time) {
    return NDK_RfidPiccDeactivate(time);
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_RfidPiccApdu
 * Signature: (I[B[I[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1RfidPiccApdu
        (JNIEnv *env, jobject jo, jint nSendlen, jbyteArray psSendbuf, jintArray pnRecvlen,
         jbyteArray psRecebuf) {
    int ret = -1;
    int len = 0;
    char buf[1024] = {0};
    char *sbuf = (*env)->GetByteArrayElements(env, psSendbuf, JNI_FALSE);
    ret = NDK_RfidPiccApdu(nSendlen, sbuf, &len, buf);
    if (ret == 0) {
        (*env)->SetIntArrayRegion(env, pnRecvlen, 0, 1, &len);
        (*env)->SetByteArrayRegion(env, psRecebuf, 0, len, buf);
    }
    (*env)->ReleaseByteArrayElements(env, psSendbuf, sbuf, 0);
    return ret;
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_M1Request
 * Signature: (B[I[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1M1Request
        (JNIEnv *env, jobject jo, jbyte type, jintArray pnDatalen, jbyteArray psDatabuf) {
    char mtype = type;
    int ret = -1;
    int len = 0;
    char buf[1024] = {0};
    ret = NDK_M1Request(mtype, &len, buf);
    if (ret == 0) {
        (*env)->SetIntArrayRegion(env, pnDatalen, 0, 1, &len);
        (*env)->SetByteArrayRegion(env, psDatabuf, 0, len, buf);
    }
    return ret;
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_M1Anti
 * Signature: ([I[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1M1Anti
        (JNIEnv *env, jobject jo, jintArray pnDataLen, jbyteArray psDataBuf) {
    int len = 0;
    int buf[256] = {0};
    int ret = -1;
    ret = NDK_M1Anti(&len, buf);
    if (ret == 0) {
        (*env)->SetIntArrayRegion(env, pnDataLen, 0, 1, &len);
        (*env)->SetByteArrayRegion(env, psDataBuf, 0, len, buf);
    }
    return ret;
};

JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1M1Anti_1SEL
        (JNIEnv *env, jobject jo, jint ucSelCode, jintArray pnDataLen, jbyteArray psDataBuf) {
    int len = 0;
    uchar buf[512] = {0};
    int ret = -1;
    ret = NDK_M1Anti_SEL(ucSelCode, &len, buf);
    if (ret == 0 && len > 0) {
        (*env)->SetIntArrayRegion(env, pnDataLen, 0, 1, &len);
        (*env)->SetByteArrayRegion(env, psDataBuf, 0, len, buf);
    }

    return ret;
};
/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_M1Select
 * Signature: (I[B[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1M1Select
        (JNIEnv *env, jobject jo, jint nUidLen, jbyteArray psUidBuf, jbyteArray psSakBuf) {
    char *sbuf = (*env)->GetByteArrayElements(env, psUidBuf, JNI_FALSE);
    char rbuf[2] = {0};
    int ret = -1;
    ret = NDK_M1Select(nUidLen, sbuf, rbuf);
    if (ret == 0)
        (*env)->SetByteArrayRegion(env, psSakBuf, 0, 1, rbuf);
    return ret;
};

JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1M1Select_1SEL
        (JNIEnv *env, jobject jo, jint ucSelCode, jint nUidLen, jbyteArray psUidBuf,
         jbyteArray psSakBuf) {
    char *sbuf = (*env)->GetByteArrayElements(env, psUidBuf, JNI_FALSE);
    char rbuf[2] = {0};
    int ret = -1;
    ret = NDK_M1Select_SEL(ucSelCode, nUidLen, sbuf, rbuf);
    if (ret == 0)
        (*env)->SetByteArrayRegion(env, psSakBuf, 0, 1, rbuf);

    return ret;
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_M1KeyStore
 * Signature: (BB[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1M1KeyStore
        (JNIEnv *env, jobject jo, jbyte type, jbyte ucKeyNum, jbyteArray psKeyData) {
    char *key = (*env)->GetByteArrayElements(env, psKeyData, JNI_FALSE);
    int ret = -1;
    char mtype = type;
    char num = ucKeyNum;
    ret = NDK_M1KeyStore(mtype, num, key);
    (*env)->ReleaseByteArrayElements(env, psKeyData, key, 0);
    return ret;
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_M1KeyLoad
 * Signature: (BB)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1M1KeyLoad
        (JNIEnv *env, jobject jo, jbyte ucKeyType, jbyte ucKeyNum) {
    char type = ucKeyType;
    char keynum = ucKeyNum;
    return NDK_M1KeyLoad(type, keynum);
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_M1InternalAuthen
 * Signature: (I[BBB)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1M1InternalAuthen
        (JNIEnv *env, jobject jo, jint nUidLen, jbyteArray psUidBuf, jbyte ucKeyType,
         jbyte ucBlockNum) {
    char keytype = ucKeyType;
    char blocknum = ucBlockNum;
    char *buf = (*env)->GetByteArrayElements(env, psUidBuf, JNI_FALSE);
    int ret = NDK_M1InternalAuthen(nUidLen, buf, keytype, blocknum);
    (*env)->ReleaseByteArrayElements(env, psUidBuf, buf, 0);
    return ret;
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_M1ExternalAuthen
 * Signature: (I[BB[BB)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1M1ExternalAuthen
        (JNIEnv *env, jobject jo, jint nUidLen, jbyteArray psUidBuf, jbyte ucKeyType,
         jbyteArray psKeyData, jbyte ucBlockNum) {
    char keytype = ucKeyType;
    char blocknum = ucBlockNum;
    char *buf = (*env)->GetByteArrayElements(env, psUidBuf, JNI_FALSE);
    char *key = (*env)->GetByteArrayElements(env, psKeyData, JNI_FALSE);
    int ret = NDK_M1ExternalAuthen(nUidLen, buf, keytype, key, blocknum);
    (*env)->ReleaseByteArrayElements(env, psUidBuf, buf, 0);
    (*env)->ReleaseByteArrayElements(env, psKeyData, key, 0);
    return ret;
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_M1Read
 * Signature: (B[I[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1M1Read
        (JNIEnv *env, jobject jo, jbyte ucBlockNum, jintArray pnDataLen, jbyteArray psBlockData) {
    int len = 16;
    char buf[256] = {0};
    char blocknum = ucBlockNum;
    int ret = NDK_M1Read(blocknum, &len, buf);
    if (ret == 0) {
        (*env)->SetIntArrayRegion(env, pnDataLen, 0, 1, &len);
        (*env)->SetByteArrayRegion(env, psBlockData, 0, len, buf);
    }
    return ret;
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_M1Write
 * Signature: (B[I[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1M1Write
        (JNIEnv *env, jobject jo, jbyte ucBlockNum, jintArray pnDataLen, jbyteArray psBlockData) {
    char blocknum = ucBlockNum;
    int len = 16;
    char *buf = (*env)->GetByteArrayElements(env, psBlockData, JNI_FALSE);
    int ret = -1;
    ret = NDK_M1Write(blocknum, &len, buf);
    (*env)->ReleaseByteArrayElements(env, psBlockData, buf, 0);
    return ret;
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_M1Increment
 * Signature: (BI[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1M1Increment
        (JNIEnv *env, jobject jo, jbyte ucBlockNum, jint nDataLen, jbyteArray psDataBuf) {
    char blocknum = ucBlockNum;
    char *buf = (*env)->GetByteArrayElements(env, psDataBuf, JNI_FALSE);
    int ret = NDK_M1Increment(blocknum, nDataLen, buf);
    (*env)->ReleaseByteArrayElements(env, psDataBuf, buf, 0);
    return ret;
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_M1Decrement
 * Signature: (BI[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1M1Decrement
        (JNIEnv *env, jobject jo, jbyte ucBlockNum, jint nDataLen, jbyteArray psDataBuf) {
    char blocknum = ucBlockNum;
    char *buf = (*env)->GetByteArrayElements(env, psDataBuf, JNI_FALSE);
    int ret = NDK_M1Decrement(blocknum, nDataLen, buf);
    (*env)->ReleaseByteArrayElements(env, psDataBuf, buf, 0);
    return ret;
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_M1Transfer
 * Signature: (B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1M1Transfer
        (JNIEnv *env, jobject jo, jbyte ucBlockNum) {
    char blocknum = ucBlockNum;
    return NDK_M1Transfer(blocknum);
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_M1Restore
 * Signature: (B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1M1Restore
        (JNIEnv *env, jobject jo, jbyte ucBlockNum) {
    char blocknum = ucBlockNum;
    return NDK_M1Restore(blocknum);
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_PiccQuickRequest
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1PiccQuickRequest
        (JNIEnv *env, jobject jo, jint nModeCode) {
    return NDK_PiccQuickRequest(nModeCode);
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_SetIgnoreProtocol
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1SetIgnoreProtocol
        (JNIEnv *env, jobject jo, jint nModeCode) {
    return NDK_SetIgnoreProtocol(nModeCode);
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_GetIgnoreProtocol
 * Signature: ([I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1GetIgnoreProtocol
        (JNIEnv *env, jobject jo, jintArray pnModeCode) {
    int mode = 0;
    int ret = -1;
    ret = NDK_GetIgnoreProtocol(&mode);
    if (ret == 0)
        (*env)->SetIntArrayRegion(env, pnModeCode, 0, 1, &mode);
    return ret;
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_GetRfidType
 * Signature: ([I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1GetRfidType
        (JNIEnv *env, jobject jo, jintArray type) {
    int mtype = 0;
    int ret = -1;
    ret = NDK_GetRfidType(&mtype);
    if (ret == 0)
        (*env)->SetIntArrayRegion(env, type, 0, 1, &mtype);
    return ret;
};

/*
 * Class:     com_newland_ndk_RfCard
 * Method:    NDK_RfidTypeARats
 * Signature: (B[I[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1RfidTypeARats
        (JNIEnv *env, jobject jo, jbyte ucCid, jintArray pnDatalen, jbyteArray psDatabuf) {
    char cid = ucCid;
    int len = 0;
    char buf[1024] = {0};
    int ret = -1;
    ret = NDK_RfidTypeARats(cid, &len, buf);
    if (ret == 0) {
        (*env)->SetIntArrayRegion(env, pnDatalen, 0, 1, &len);
        (*env)->SetByteArrayRegion(env, psDatabuf, 0, len, buf);
    }
    return ret;
};

JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1RfidFelicaPoll
        (JNIEnv *env, jobject jo, jbyteArray psRecebuf, jintArray pnRecvlen) {

    int ret = -1;
    char recvBuf[512];
    int recvLen = 0;
    memset(recvBuf, 0, sizeof(recvBuf));
    ret = NDK_RfidFelicaPoll(recvBuf, &recvLen);
    if (ret == 0 && recvLen > 0) {
        (*env)->SetByteArrayRegion(env, psRecebuf, 0, recvLen, recvBuf);
        (*env)->SetIntArrayRegion(env, pnRecvlen, 0, 1, &recvLen);
    }
    return ret;
};

JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1FelicaPoll
        (JNIEnv *env, jobject jo, jbyteArray paramData, jbyteArray psRecebuf, jintArray pnRecvlen) {

    int ret = -1, recvLen = 0;
    char recvBuf[512];
    felica_param_t felicaParam;
    memset(&felicaParam, 0, sizeof(felica_param_t));
    memset(recvBuf, 0, sizeof(recvBuf));

    char *paramBuf = (*env)->GetByteArrayElements(env, paramData, JNI_FALSE);
    memcpy(&felicaParam, paramBuf, sizeof(felica_param_t));
    LOGD_STR("felicaParam", &felicaParam, sizeof(felica_param_t));
    ret = NDK_FelicaPoll(felicaParam, recvBuf, &recvLen);
    if (ret == 0 && recvLen > 0) {
        (*env)->SetByteArrayRegion(env, psRecebuf, 0, recvLen, recvBuf);
        (*env)->SetIntArrayRegion(env, pnRecvlen, 0, 1, &recvLen);
    }
    (*env)->ReleaseByteArrayElements(env, paramData, paramBuf, 0);

    return ret;
};

JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1FelicaSetTimeout1
        (JNIEnv *env, jobject jo, jint timeout) {
    uchar time = timeout;
    int ret = NDK_FelicaSetTimeout(time);
    LOGD_FMT("NDK_FelicaSetTimeout ret[%d] time[%d]", ret, time);
    return ret;
};

JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1RfidFelicaApdu
        (JNIEnv *env, jobject jo, jint nSendlen, jbyteArray psSendbuf, jintArray pnRecvlen,
         jbyteArray psRecebuf) {
    int recvlen = 0;
    uchar recvbuf[512];

    uchar *sendBuf = (*env)->GetByteArrayElements(env, psSendbuf, JNI_FALSE);
    LOGD_STR("sendBuf", sendBuf, nSendlen);
    int ret = NDK_RfidFelicaApdu(nSendlen, sendBuf, &recvlen, recvbuf);
    if (ret == 0 && recvlen > 0) {
        (*env)->SetIntArrayRegion(env, pnRecvlen, 0, 1, &recvlen);
        (*env)->SetByteArrayRegion(env, psRecebuf, 0, recvlen, recvbuf);
    }
    (*env)->ReleaseByteArrayElements(env, psSendbuf, sendBuf, 0);
    return ret;
};

JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1MifareActive
        (JNIEnv *env, jobject jo, jbyte ucReqCode, jbyteArray psUID, jintArray pnUIDLen,
         jbyteArray psSak) {
    int ret = -1;
    uchar uid[512];
    int uidLen = 0;
    uchar sak = 0xFF;
    memset(uid, 0, sizeof(uid));
    ret = NDK_MifareActive(ucReqCode, uid, &uidLen, &sak);
    if (ret == 0 && uidLen > 0) {
        (*env)->SetIntArrayRegion(env, pnUIDLen, 0, 1, &uidLen);
        (*env)->SetByteArrayRegion(env, psUID, 0, uidLen, uid);
        (*env)->SetByteArrayRegion(env, psSak, 0, 1, &sak);
    }

    return ret;
}

JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1M0Read
        (JNIEnv *env, jobject jo, jbyte ucBlockNum, jintArray pnDataLen, jbyteArray psBlockData) {
    int len = 0;
    char buf[512];
    char blocknum = ucBlockNum;
    memset(buf, 0, sizeof(buf));
    int ret = NDK_M0Read(blocknum, &len, buf);
    if (ret == 0 && len > 0) {
        (*env)->SetIntArrayRegion(env, pnDataLen, 0, 1, &len);
        (*env)->SetByteArrayRegion(env, psBlockData, 0, len, buf);
    }
    return ret;
}


JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1M0Write
        (JNIEnv *env, jobject jo, jbyte ucBlockNum, jint pnDataLen, jbyteArray psBlockData) {
    char *buf = (*env)->GetByteArrayElements(env, psBlockData, JNI_FALSE);
    int ret = -1;
    ret = NDK_M0Write(ucBlockNum, pnDataLen, buf);
    (*env)->ReleaseByteArrayElements(env, psBlockData, buf, 0);
    return ret;
}

JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1M0Authen
        (JNIEnv *env, jobject jo, jbyteArray psKey) {
    uchar *keyBuf = (*env)->GetByteArrayElements(env, psKey, JNI_FALSE);
    int ret = -1;
    ret = NDK_M0Authen(keyBuf);
    (*env)->ReleaseByteArrayElements(env, psKey, keyBuf, 0);
    return ret;
}

JNIEXPORT jint JNICALL Java_com_newland_ndk_RfCard_NDK_1M0Authen_1Release
        (JNIEnv *env, jobject jo, jint nSendlen, jbyteArray psSendbuf, jintArray pnRecvlen,
         jbyteArray psRecebuf) {
    int recvlen = 0;
    uchar recvbuf[512];

    uchar *sendBuf = (*env)->GetByteArrayElements(env, psSendbuf, JNI_FALSE);

    int ret = NDK_M0Authen_Release(nSendlen, sendBuf, &recvlen, recvbuf);
    if (ret == 0 && recvlen > 0) {
        (*env)->SetIntArrayRegion(env, pnRecvlen, 0, 1, &recvlen);
        (*env)->SetByteArrayRegion(env, psRecebuf, 0, recvlen, recvbuf);
    }
    (*env)->ReleaseByteArrayElements(env, psSendbuf, sendBuf, 0);
    return ret;
}


JNIEXPORT jint JNICALL
Java_com_newland_ndk_RfCard_NDK_1RfidSetPiccParam(JNIEnv *env, jobject thiz, jbyte uc_piccparamtype,
                                                  jint pn_paramlen, jbyteArray ps_parambuf) {
    uchar *sendBuf = (*env)->GetByteArrayElements(env, ps_parambuf, JNI_FALSE);
    int ret = NDK_RfidSetPiccParam(uc_piccparamtype, pn_paramlen, sendBuf);
    (*env)->ReleaseByteArrayElements(env, ps_parambuf, sendBuf, 0);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_RfCard_NDK_1RfidPiccApduInTransMode(JNIEnv *env, jobject thiz,
                                                         jbyteArray psSendbuf,
                                                         jint nSendlen, jbyteArray psRecebuf,
                                                         jintArray pnRecvlen, jint timeout) {
    int recvlen = 0;
    uchar recvbuf[1024];
    memset(recvbuf, 0, sizeof(recvbuf));

    uchar *sendBuf = (*env)->GetByteArrayElements(env, psSendbuf, JNI_FALSE);
    LOGD_STR("sendBuf", sendBuf, nSendlen);
    int ret = NDK_RfidPiccApduInTransMode(sendBuf, nSendlen, recvbuf, &recvlen, timeout);

    if (ret == 0 && recvlen > 0) {
        (*env)->SetIntArrayRegion(env, pnRecvlen, 0, 1, &recvlen);
        (*env)->SetByteArrayRegion(env, psRecebuf, 0, recvlen, recvbuf);
    }
    (*env)->ReleaseByteArrayElements(env, psSendbuf, sendBuf, 0);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_RfCard_NDK_1RfidConfig(JNIEnv *env, jobject thiz, jint rfmode) {
    LOGI(">>>NDK_RfidConfig rfmode[%d]", rfmode);
    return NDK_RfidConfig(rfmode);
}
JNIEXPORT jint JNICALL
Java_com_newland_ndk_RfCard_NDK_1RfidEMVTest(JNIEnv *env, jobject jo,jint option, jint nSendlen, jbyteArray psSendbuf, jintArray pnRecvlen,
                                             jbyteArray psRecebuf) {
    // TODO: implement NDK_RfidEMVTest()
    int ret = -1;
    int len = 0;
    char buf[1024] = {0};
    char *sbuf = (*env)->GetByteArrayElements(env, psSendbuf, JNI_FALSE);
    ret = NDK_RfidEMVTest(option,nSendlen, sbuf, &len, buf);
    if (ret == 0) {
        (*env)->SetIntArrayRegion(env, pnRecvlen, 0, 1, &len);
        (*env)->SetByteArrayRegion(env, psRecebuf, 0, len, buf);
    }
    (*env)->ReleaseByteArrayElements(env, psSendbuf, sbuf, 0);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_RfCard_NDK_1RfidSetDetectType(JNIEnv *env, jobject thiz, jint uc_picctype) {
    uint type = uc_picctype;
    LOGI(">>>NDK_RfidSetDetectType type[0x%x]", type);
    return NDK_RfidSetDetectType(type);
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_RfCard_NDK_1RfidDetectWithCardType(JNIEnv *env, jobject thiz,
                                                        jintArray ps_picctype, jintArray pnDatalen,
                                                        jbyteArray ps_databuf) {
    uint mtype = 0;
    int len = 0;
    char buf[1024] = {0};
    int ret = -1;
    ret = NDK_RfidDetectWithCardType(&mtype, &len, buf);
    LOGI(">>>NDK_RfidDetectWithCardType type[0x%x]", mtype);
    if (ret == 0) {
        (*env)->SetIntArrayRegion(env, pnDatalen, 0, 1, &len);
        (*env)->SetIntArrayRegion(env, ps_picctype, 0, 1, &mtype);
        (*env)->SetByteArrayRegion(env, ps_databuf, 0, len, buf);
    }
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_RfCard_NDK_1CEInit(JNIEnv *env, jobject thiz) {
    return NDK_CEInit();
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_RfCard_NDK_1CEParamSet(JNIEnv *env, jobject thiz, jint type, jint len,
                                            jbyteArray buf) {
    uint32_t sendLen = len;
    uchar *sendBuf = (*env)->GetByteArrayElements(env, buf, JNI_FALSE);
    int ret = NDK_CEParamSet(type, &sendLen, sendBuf);
    (*env)->ReleaseByteArrayElements(env, buf, sendBuf, 0);
    return ret;
}


JNIEXPORT jint JNICALL
Java_com_newland_ndk_RfCard_NDK_1CEDataSync(JNIEnv *env, jobject thiz, jint mode, jint type,
                                            jbyteArray buf, jintArray len) {
    // TODO: implement NDK_CEDataSync()
    //mode:0:read,1:write.
    if(mode == 0){
        uchar recvbuf[2048];
        memset(recvbuf, 0, sizeof(recvbuf));
        int readLen = 0;
        int ret = NDK_CEDataSync(type,recvbuf,&readLen);
        if (ret == 0 && readLen > 0) {
            (*env)->SetIntArrayRegion(env, len, 0, 1, &readLen);
            (*env)->SetByteArrayRegion(env, buf, 0, readLen, recvbuf);
        }
        return ret;
    }else if(mode == 1){
        uchar *sendBuf = (*env)->GetByteArrayElements(env, buf, NULL);

        jsize length = (*env)->GetArrayLength(env, len);
        if (length == 0) {
            LOGI(">>>NDK_CEDataSync length==0");
            return -6;
        }
        jint *elements = (*env)->GetIntArrayElements(env, len, NULL);
        if (elements == NULL){
            LOGI(">>>NDK_CEDataSync elements==null");
            return -6;
        }
        jint firstInt = elements[0];
        int ret = NDK_CEDataSync(type,sendBuf,&firstInt);
        (*env)->ReleaseByteArrayElements(env, buf, sendBuf, 0);
        (*env)->ReleaseIntArrayElements(env, len, elements, 0);
        return ret;
    }else{
        LOGI(">>>NDK_CEDataSync mode err.");
        return -6;
    }
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_RfCard_NDK_1CEGetState(JNIEnv *env, jobject thiz, jint type, jintArray state) {
    // TODO: implement NDK_CEGetState()
    int recvlen = 0;
    int ret = NDK_CEGetState(type,&recvlen);
    if (ret == 0 && recvlen > 0) {
        (*env)->SetIntArrayRegion(env, state, 0, 1, &recvlen);
    }
    return ret;
}

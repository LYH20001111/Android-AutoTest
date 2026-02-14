/**
 * Author by wuhh, Date on 2020/2/17.
 */

#include <log.h>
#include <jni.h>
#include <string.h>
#include <malloc.h>
#include <android/log.h>
#include "crypto.h"
#include "ndk.h"

#define MODE_ENCRYPT 1
#define MODE_DECRYPT 2

jint setResponse(JNIEnv const *env, jbyteArray data, jint length, jbyteArray err_msg, jintArray err_msg_len, int commandType);

jint getRequest(JNIEnv const *env, jbyteArray out_info, jintArray out_info_len, jbyteArray err_msg, jintArray err_msg_len, int commandType);

CSR_HANDLE gHandle;

JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecGenerateKey
        (JNIEnv *env, jobject obj, jint method, jobject keyDataObj, jobject kcvDataObj) {

    jclass keyDataCls = (*env)->GetObjectClass(env, keyDataObj);
    if (keyDataCls == NULL) {
        LOGD_FMT(">>>keyDataCls[%d]", keyDataCls);
        return NDK_ERR_PARA;
    }
    ST_SEC_KEYIN_DATA stDataIn;
    memset(&stDataIn, 0, sizeof(ST_SEC_KEYIN_DATA));

    stDataIn.ucKEKIdx = (*env)->GetIntField(env, keyDataObj,
                                            (*env)->GetFieldID(env, keyDataCls, "ucKEKIdx", "I"));
    stDataIn.KEKType = (*env)->GetIntField(env, keyDataObj,
                                           (*env)->GetFieldID(env, keyDataCls, "KEKType", "I"));
    stDataIn.KEKUsage = (*env)->GetIntField(env, keyDataObj,
                                            (*env)->GetFieldID(env, keyDataCls, "KEKUsage", "I"));

    stDataIn.ucKeyIdx = (*env)->GetIntField(env, keyDataObj,
                                            (*env)->GetFieldID(env, keyDataCls, "ucKeyIdx", "I"));
    stDataIn.KeyType = (*env)->GetIntField(env, keyDataObj,
                                           (*env)->GetFieldID(env, keyDataCls, "KeyType", "I"));
    stDataIn.KeyUsage = (*env)->GetIntField(env, keyDataObj,
                                            (*env)->GetFieldID(env, keyDataCls, "KeyUsage", "I"));

    stDataIn.CipherMode = (*env)->GetIntField(env, keyDataObj,
                                              (*env)->GetFieldID(env, keyDataCls, "CipherMode",
                                                                 "I"));
    stDataIn.PadingMode = (*env)->GetIntField(env, keyDataObj,
                                              (*env)->GetFieldID(env, keyDataCls, "PaddingMode",
                                                                 "I"));

    stDataIn.nKeyLen = (*env)->GetIntField(env, keyDataObj,
                                           (*env)->GetFieldID(env, keyDataCls, "nKeyLen", "I"));
    stDataIn.nKeyDataLen = (*env)->GetIntField(env, keyDataObj,
                                               (*env)->GetFieldID(env, keyDataCls, "nKeyDataLen",
                                                                  "I"));

    uchar *pkeyData;
    jbyteArray keyData = (jbyteArray) (*env)->GetObjectField(env, keyDataObj,
                                                             (*env)->GetFieldID(env, keyDataCls,
                                                                                "pKeyData", "[B"));
    if (keyData != NULL && stDataIn.nKeyDataLen > 0) {
        pkeyData = (*env)->GetByteArrayElements(env, keyData, NULL);
        stDataIn.pKeyData = pkeyData;
//        LOGD_STR("keyData", pkeyData, stDataIn.nKeyDataLen);
    }
    uchar *pIV;
    jbyteArray IV = (jbyteArray) (*env)->GetObjectField(env, keyDataObj,
                                                        (*env)->GetFieldID(env, keyDataCls, "psIV",
                                                                           "[B"));
    if (IV != NULL) {
        pIV = (*env)->GetByteArrayElements(env, IV, NULL);
        stDataIn.psIV = pIV;
        LOGD_STR("IV", pIV, (*env)->GetArrayLength(env, IV));
    }

    stDataIn.nKsnLen = (*env)->GetIntField(env, keyDataObj,
                                           (*env)->GetFieldID(env, keyDataCls, "nKsnLen", "I"));

    uchar *pksnData;
    jbyteArray ksnData = (jbyteArray) (*env)->GetObjectField(env, keyDataObj,
                                                             (*env)->GetFieldID(env, keyDataCls,
                                                                                "psKsn", "[B"));
    if (ksnData != NULL && stDataIn.nKsnLen > 0) {
        pksnData = (*env)->GetByteArrayElements(env, ksnData, NULL);
        stDataIn.psKsn = pksnData;
        LOGD_STR("ksnData", pksnData, stDataIn.nKsnLen);
    }

    uchar *pAD;
    stDataIn.nADSize = (*env)->GetIntField(env, keyDataObj,
                                           (*env)->GetFieldID(env, keyDataCls, "nADSize", "I"));
    jbyteArray AD = (jbyteArray) (*env)->GetObjectField(env, keyDataObj,
                                                        (*env)->GetFieldID(env, keyDataCls, "pAD",
                                                                           "[B"));
    if (AD != NULL && stDataIn.nADSize > 0) {
        pAD = (*env)->GetByteArrayElements(env, AD, NULL);
        stDataIn.pAD = pAD;
//        LOGD_STR("AD", pAD, stDataIn.nADSize);
    }

    LOGD_FMT(
            ">>>method[%d] ucKEKIdx[%d] KEKType[%d] KEKUsage[%d] ucKeyIdx[%d] KeyType[%d] KeyUsage[%d] CipherMode[%d] PadingMode[%d] nKeyLen[%d] nKeyDataLen[%d]", \
             method, stDataIn.ucKEKIdx, stDataIn.KEKType, stDataIn.KEKUsage, stDataIn.ucKeyIdx,
            stDataIn.KeyType, stDataIn.KeyUsage, \
             stDataIn.CipherMode, stDataIn.PadingMode, stDataIn.nKeyLen, stDataIn.nKeyDataLen);
//    LOGD_FMT(">>>keyData[%d] IV[%d] nKsnLen[%d] ksnData[%d] nADSize[%d] AD[%d]", keyData, IV,
//             stDataIn.nKsnLen, ksnData, stDataIn.nADSize, AD);

    jclass kcvDataCls = (*env)->GetObjectClass(env, kcvDataObj);
    if (kcvDataCls == NULL) {
        LOGD_FMT(">>>kcvDataCls[%d]", kcvDataCls);
        return NDK_ERR_PARA;
    }
    ST_SEC_KCV_DATA stKcvData;
    memset(&stKcvData, 0, sizeof(ST_SEC_KCV_DATA));

    stKcvData.nCheckMode = (*env)->GetIntField(env, kcvDataObj,
                                               (*env)->GetFieldID(env, kcvDataCls, "nCheckMode",
                                                                  "I"));
    stKcvData.nLen = (*env)->GetIntField(env, kcvDataObj,
                                         (*env)->GetFieldID(env, kcvDataCls, "nLen", "I"));
    if (stKcvData.nLen > 8) {
        LOGD_FMT(">>>nLen[%d]", stKcvData.nLen);
        return NDK_ERR_PARA;
    }
    jbyteArray sCheckBuf = (jbyteArray) (*env)->GetObjectField(env, kcvDataObj,
                                                               (*env)->GetFieldID(env, kcvDataCls,
                                                                                  "sCheckBuf",
                                                                                  "[B"));
    LOGD_FMT(">>>nCheckMode[%d] nLen[%d] sCheckBuf[%d]", stKcvData.nCheckMode, stKcvData.nLen,
             sCheckBuf);
    uchar *pCheckBuf;
    if (sCheckBuf != NULL) {
        pCheckBuf = (*env)->GetByteArrayElements(env, sCheckBuf, NULL);
        memcpy(stKcvData.sCheckBuf, pCheckBuf, stKcvData.nLen);
        LOGD_STR("kcvValue", pCheckBuf, stKcvData.nLen);
    }

    int ret = NAPI_SecGenerateKey(method, &stDataIn, &stKcvData);

    if (keyData != NULL)
        (*env)->ReleaseByteArrayElements(env, keyData, pkeyData, NULL);

    if (IV != NULL)
        (*env)->ReleaseByteArrayElements(env, IV, pIV, NULL);

    if (ksnData != NULL)
        (*env)->ReleaseByteArrayElements(env, ksnData, pksnData, NULL);

    if (AD != NULL)
        (*env)->ReleaseByteArrayElements(env, AD, pAD, NULL);

    if (sCheckBuf != NULL)
        (*env)->ReleaseByteArrayElements(env, sCheckBuf, pCheckBuf, NULL);

    LOGD_FMT(">>>NAPI_SecGenerateKey ret[%d]", ret);
    return ret;
}
/*
 * Class:     com_newland_nsdk_module_jni_NSDKJni
 * Method:    NAPI_SecDeleteKey
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecDeleteKey
        (JNIEnv *env, jobject obj, jint keyId, jint keyType, jint keyUsage) {
    LOGD_FMT(">>>keyId[%d] keyType[%d] keyUsage[%d]", keyId, keyType, keyUsage);
    int ret = NAPI_SecDeleteKey(keyId, keyType, keyUsage);
    LOGD_FMT(">>>NAPI_SecDeleteKey ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecSymmKeyErase(JNIEnv *env, jobject thiz) {
    int ret = NAPI_SecSymmKeyErase();
    LOGD_FMT(">>>NAPI_SecSymmKeyErase ret[%d]", ret);
    return ret;
}

/*
 * Class:     com_newland_nsdk_module_jni_NSDKJni
 * Method:    NAPI_SecGetKeyInfo
 * Signature: (IIII[BI[B[I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecGetKeyInfo
        (JNIEnv *env, jobject obj, jint infoID, jint keyId, jint keyType, jint keyUsage,
         jbyteArray AD, jint adSize, jbyteArray outInfo, jintArray outInfoLen) {
    uchar *pAD = NULL;
    if (AD != NULL) {
        pAD = (*env)->GetByteArrayElements(env, AD, 0);
    }
    uchar outBuf[4096];
    int outLen;
    memset(outBuf, 0, sizeof(outBuf));
    LOGD_FMT(">>>infoID[%d] keyId[%d] keyType[%d] keyUsage[%d] pAD[%d] adSize[%d]", infoID, keyId,
             keyType, keyUsage, pAD, adSize);
    int ret = NAPI_SecGetKeyInfo(infoID, keyId, keyType, keyUsage, pAD, adSize, outBuf, &outLen);
    LOGD_FMT(">>>outLen[%d]", outLen);
    if (ret == 0) {
        (*env)->SetIntArrayRegion(env, outInfoLen, 0, 1, &outLen);
        (*env)->SetByteArrayRegion(env, outInfo, 0, outLen, outBuf);
    }
    if (AD != NULL) {
        (*env)->ReleaseByteArrayElements(env, AD, pAD, 0);
    }
    LOGD_FMT(">>>NAPI_SecGetKeyInfo ret[%d]", ret);
    return ret;
}

/*
 * Class:     com_newland_nsdk_module_jni_NSDKJni
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecSetKeyOwner
        (JNIEnv *env, jobject obj, jstring name) {
    char *buf = NULL;
    if(name != NULL) {
        buf = (*env)->GetStringUTFChars(env, name, 0);
    }
    int ret = NAPI_SecSetKeyOwner(buf);
    if(name != NULL) {
        (*env)->ReleaseStringUTFChars(env, name, buf);
    }
    LOGD_FMT(">>>NAPI_SecSetKeyOwner ret[%d]", ret);
    return ret;
}

/*
 * Class:     com_newland_nsdk_module_jni_NSDKJni
 * Method:    NAPI_SecGetKeyOwner
 * Signature: (I[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecGetKeyOwner
        (JNIEnv *env, jobject obj, jint maxLen, jbyteArray owner) {
    char outBuf[1024];
    memset(outBuf, 0, sizeof(outBuf));
    LOGD_FMT(">>>maxLen[%d]", maxLen);
    int ret = NAPI_SecGetKeyOwner(maxLen, outBuf);
    if (ret == 0) {
        (*env)->SetByteArrayRegion(env, owner, 0, maxLen, outBuf);
    }
    LOGD_FMT(">>>NAPI_SecGetKeyOwner ret[%d]", ret);
    return ret;
}

/*
 * Class:     com_newland_nsdk_module_jni_NSDKJni
 * Method:    NAPI_SecGenerateMAC
 * Signature: (II[BI[BI[BI[B[I[B[I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecGenerateMAC
        (JNIEnv *env, jobject obj, jint MacType, jint ucKeyID, jbyteArray IV, jint unIVSize,
         jbyteArray dataIn, jint nDataInLen, jbyteArray AD, jint unADSize,
         jbyteArray psMacOut, jintArray pnOutLen, jbyteArray psKsnOut, jintArray nOutKsnLen) {
    uchar *pIV = NULL;
    if (IV != NULL) {
        pIV = (*env)->GetByteArrayElements(env, IV, 0);
    }
    uchar *pDataIn = NULL;
    if (dataIn != NULL) {
        pDataIn = (*env)->GetByteArrayElements(env, dataIn, 0);
    }

    uchar *pAD = NULL;
    if (AD != NULL) {
        pAD = (*env)->GetByteArrayElements(env, AD, 0);
    }

    uchar outData[256], ksnData[32];
    int outDataLen = 0, ksnDataLen = 0;

    memset(outData, 0, sizeof(outData));
    memset(ksnData, 0, sizeof(ksnData));
    int ret = NAPI_SecGenerateMAC(MacType, ucKeyID, pIV, unIVSize, pDataIn, nDataInLen, pAD,
                                  unADSize, outData, &outDataLen, ksnData, &ksnDataLen);
    if (ret == 0) {
        if (outDataLen > 0) {
            (*env)->SetIntArrayRegion(env, pnOutLen, 0, 1, &outDataLen);
            (*env)->SetByteArrayRegion(env, psMacOut, 0, outDataLen, outData);
        }
        if (ksnDataLen > 0) {
            (*env)->SetIntArrayRegion(env, nOutKsnLen, 0, 1, &ksnDataLen);
            (*env)->SetByteArrayRegion(env, psKsnOut, 0, ksnDataLen, ksnData);
        }
    }
    if (IV != NULL) {
        (*env)->ReleaseByteArrayElements(env, IV, pIV, 0);
    }
    if (dataIn != NULL) {
        (*env)->ReleaseByteArrayElements(env, dataIn, pDataIn, 0);
    }
    if (AD != NULL) {
        (*env)->ReleaseByteArrayElements(env, AD, pAD, 0);
    }
    LOGD_FMT(">>>NAPI_SecGenerateMAC ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecGenerateMAC_1DerivateKey(JNIEnv *env,
                                                                                  jobject thiz,
                                                                                  jint mac_type,
                                                                                  jint uc_key_id,
                                                                                  jbyteArray iv,
                                                                                  jint iv_len,
                                                                                  jbyteArray dataIn,
                                                                                  jint length,
                                                                                  jobject dukpt_derivate_data,
                                                                                  jbyteArray out_data,
                                                                                  jintArray out_data_len,
                                                                                  jbyteArray ksn_data,
                                                                                  jintArray ksn_data_len) {
    ST_SEC_DUKPT_DERIVATE_DATA  stDerivateData;
    memset(&stDerivateData, 0x00, sizeof(ST_SEC_DUKPT_DERIVATE_DATA));

    uchar *pIV = NULL;
    if (iv != NULL) {
        pIV = (*env)->GetByteArrayElements(env, iv, 0);
    }
    uchar *pDataIn = NULL;
    if (dataIn != NULL) {
        pDataIn = (*env)->GetByteArrayElements(env, dataIn, 0);
    }

    jclass derivateDataCls = (*env)->GetObjectClass(env, dukpt_derivate_data);
    if (derivateDataCls == NULL) {
        LOGD_FMT(">>>derivateDataCls[%d]", derivateDataCls);
        return NDK_ERR_PARA;
    }

    stDerivateData.KeyType = (*env)->GetIntField(env, dukpt_derivate_data,
                                                 (*env)->GetFieldID(env, derivateDataCls, "derivateKeyType",
                                                                    "I"));
    stDerivateData.nKeyLen = (*env)->GetIntField(env, dukpt_derivate_data,
                                                 (*env)->GetFieldID(env, derivateDataCls, "derivateKeyLen",
                                                                    "I"));
    stDerivateData.DerivateUsage = (*env)->GetIntField(env, dukpt_derivate_data,
                                                 (*env)->GetFieldID(env, derivateDataCls, "derivateKeyUsage",
                                                                    "I"));

    uchar outData[256], ksnData[32];
    int outDataLen = 0, ksnDataLen = 0;

    memset(outData, 0, sizeof(outData));
    memset(ksnData, 0, sizeof(ksnData));
    int ret = NAPI_SecGenerateMAC(mac_type, uc_key_id, pIV, iv_len, pDataIn, length, &stDerivateData, sizeof(ST_SEC_DUKPT_DERIVATE_DATA), outData, &outDataLen, ksnData, &ksnDataLen);
    if (ret == 0) {
        if (outDataLen > 0) {
            (*env)->SetIntArrayRegion(env, out_data_len, 0, 1, &outDataLen);
            (*env)->SetByteArrayRegion(env, out_data, 0, outDataLen, outData);
        }
        if (ksnDataLen > 0) {
            (*env)->SetIntArrayRegion(env, ksn_data_len, 0, 1, &ksnDataLen);
            (*env)->SetByteArrayRegion(env, ksn_data, 0, ksnDataLen, ksnData);
        }
    }
    if (iv != NULL) {
        (*env)->ReleaseByteArrayElements(env, iv, pIV, 0);
    }
    if (dataIn != NULL) {
        (*env)->ReleaseByteArrayElements(env, dataIn, pDataIn, 0);
    }

    LOGD_FMT(">>>NAPI_SecGenerateMAC ret[%d]", ret);
    return ret;
}

static int calculate(int mode, JNIEnv *env, jobject obj, jobject dataInObj, jbyteArray psDataOut,
                     jintArray pnOutLen, jbyteArray psKsnOut, jintArray pnOutKsnLen) {
    ST_SEC_ENCRYPTION_DATA encryptionData;
    ST_SEC_DUKPT_DERIVATE_DATA  stDerivateData;

    memset(&stDerivateData, 0x00, sizeof(ST_SEC_DUKPT_DERIVATE_DATA));
    memset(&encryptionData, 0, sizeof(encryptionData));

    jclass dataInCls = (*env)->GetObjectClass(env, dataInObj);
    if (dataInCls == NULL) {
        LOGD_FMT(">>>dataInCls[%d]", dataInCls);
        return NDK_ERR_PARA;
    }
    encryptionData.ucKeyID = (*env)->GetIntField(env, dataInObj,
                                                 (*env)->GetFieldID(env, dataInCls, "ucKeyID",
                                                                    "I"));
    encryptionData.CipherType = (*env)->GetIntField(env, dataInObj,
                                                    (*env)->GetFieldID(env, dataInCls, "CipherType",
                                                                       "I"));
    encryptionData.KeyUsage = (*env)->GetIntField(env, dataInObj,
                                                  (*env)->GetFieldID(env, dataInCls, "KeyUsage",
                                                                     "I"));
    encryptionData.PaddingMode = (*env)->GetIntField(env, dataInObj,
                                                     (*env)->GetFieldID(env, dataInCls,
                                                                        "PaddingMode", "I"));
    encryptionData.unIVSize = (*env)->GetIntField(env, dataInObj,
                                                  (*env)->GetFieldID(env, dataInCls, "unIVSize",
                                                                     "I"));

    jobject derivateData = (*env)->GetObjectField(env, dataInObj,(*env)->GetFieldID(env, dataInCls, "dukptDerivateData", "Lcom/newland/nsdk/core/internal/crypto/ST_SEC_DUKPT_DERIVATE_DATA;"));
    if (derivateData != NULL) {
        jclass  class_derivateData = (*env)->GetObjectClass(env, derivateData);

        stDerivateData.KeyType = (*env)->GetIntField(env, derivateData,
                                                     (*env)->GetFieldID(env, class_derivateData, "derivateKeyType",
                                                                        "I"));
        stDerivateData.nKeyLen = (*env)->GetIntField(env, derivateData,
                                                     (*env)->GetFieldID(env, class_derivateData, "derivateKeyLen",
                                                                        "I"));
        stDerivateData.DerivateUsage = (*env)->GetIntField(env, derivateData,
                                                           (*env)->GetFieldID(env, class_derivateData, "derivateKeyUsage",
                                                                              "I"));
    }

    uchar *pIV;
    jbyteArray IV = (jbyteArray) (*env)->GetObjectField(env, dataInObj,
                                                        (*env)->GetFieldID(env, dataInCls, "psIV",
                                                                           "[B"));
    if (IV != NULL && encryptionData.unIVSize > 0) {
        pIV = (*env)->GetByteArrayElements(env, IV, NULL);
        encryptionData.psIV = pIV;
    }

    encryptionData.unDataInLen = (*env)->GetIntField(env, dataInObj,
                                                     (*env)->GetFieldID(env, dataInCls,
                                                                        "unDataInLen", "I"));
    uchar *pDataIn;
    jbyteArray dataIn = (jbyteArray) (*env)->GetObjectField(env, dataInObj,
                                                            (*env)->GetFieldID(env, dataInCls,
                                                                               "psDataIn", "[B"));
    if (dataIn != NULL && encryptionData.unDataInLen > 0) {
        pDataIn = (*env)->GetByteArrayElements(env, dataIn, NULL);
        encryptionData.psDataIn = pDataIn;
    }

    encryptionData.unADSize = (*env)->GetIntField(env, dataInObj,
                                                  (*env)->GetFieldID(env, dataInCls, "unADSize",
                                                                     "I"));
    uchar *pAD;
    jbyteArray AD = (jbyteArray) (*env)->GetObjectField(env, dataInObj,
                                                        (*env)->GetFieldID(env, dataInCls, "pAD",
                                                                           "[B"));

    if (derivateData != NULL) {
        encryptionData.pAD = &stDerivateData;
        encryptionData.unADSize = sizeof(ST_SEC_DUKPT_DERIVATE_DATA);
        LOGD_FMT(
                ">>>derivate key type[%d] derivate key usage[%d] derivate key len[%d]",
                stDerivateData.KeyType, stDerivateData.DerivateUsage, stDerivateData.nKeyLen);
    } else if (AD != NULL && encryptionData.unADSize > 0) {
        pAD = (*env)->GetByteArrayElements(env, AD, NULL);
        encryptionData.pAD = pAD;
    }

    LOGD_FMT(
            ">>>mode[%d] ucKeyID[%d] CipherType[%d] KeyUsage[%d] PaddingMode[%d] unIVSize[%d] unDataInLen[%d] unADSize[%d]",
            mode, encryptionData.ucKeyID, encryptionData.CipherType, encryptionData.KeyUsage,
            encryptionData.PaddingMode, encryptionData.unIVSize, encryptionData.unDataInLen,
            encryptionData.unADSize);

    uchar outData[4096], ksnData[32];
    int outDataLen = 0, ksnDataLen = 0;

    memset(outData, 0, sizeof(outData));
    memset(ksnData, 0, sizeof(ksnData));
    int ret = NDK_ERR;
    if (mode == MODE_ENCRYPT) {
        ret = NAPI_SecEncryption(&encryptionData, outData, &outDataLen, ksnData, &ksnDataLen);
        LOGD_FMT(">>>NAPI_SecEncryption ret[%d], outDataLen[%d]", ret, outDataLen);
    } else {
        ret = NAPI_SecDecryption(&encryptionData, outData, &outDataLen, ksnData, &ksnDataLen);
        LOGD_FMT(">>>NAPI_SecDecryption ret[%d], outDataLen[%d]", ret, outDataLen);
    }
    if (ret == 0) {
        if (outDataLen > 0) {
            (*env)->SetIntArrayRegion(env, pnOutLen, 0, 1, &outDataLen);
            (*env)->SetByteArrayRegion(env, psDataOut, 0, outDataLen, outData);
        }
        if (ksnDataLen > 0) {
            (*env)->SetIntArrayRegion(env, pnOutKsnLen, 0, 1, &ksnDataLen);
            (*env)->SetByteArrayRegion(env, psKsnOut, 0, ksnDataLen, ksnData);
        }
    }
    if (IV != NULL) {
        (*env)->ReleaseByteArrayElements(env, IV, pIV, 0);
    }
    if (dataIn != NULL && encryptionData.unDataInLen > 0) {
        (*env)->ReleaseByteArrayElements(env, dataIn, pDataIn, 0);
    }
    if (AD != NULL && encryptionData.unADSize > 0) {
        (*env)->ReleaseByteArrayElements(env, AD, pAD, 0);
    }
    return ret;
}

static int calculateAsym(int mode, JNIEnv *env, jobject thiz,
                         jbyte key_id, jbyte key_type,
                         jbyte key_usage,
                         jint message_digest_type,
                         jint encoding_mode,
                         jint crypto_mode,
                         jint data_in_len,
                         jbyteArray data_in,
                         jintArray out_data_len,
                         jbyteArray out_data) {
    ST_SEC_ASYM_KEY_INFO asymKeyInfo;

    memset(&asymKeyInfo, 0, sizeof(asymKeyInfo));
    asymKeyInfo.KeyIdx = key_id;
    asymKeyInfo.KeytType = key_type;
    asymKeyInfo.KeyUsage = key_usage;

    uchar *pDataOut = NULL;
    if (out_data != NULL) {
        pDataOut = (uchar *) (*env)->GetByteArrayElements(env, out_data, NULL);
    }

    int *pDataOutLen= NULL;
    if (out_data_len != NULL) {
        pDataOutLen = (int *) (*env)->GetIntArrayElements(env, out_data_len, NULL);
    }

    uchar *pDataIn;
    if (data_in != NULL && data_in_len > 0) {
        pDataIn = (*env)->GetByteArrayElements(env, data_in, NULL);
    }

    LOGD_FMT(
            ">>>mode[%d] ucKeyID[%d] KeyType[%d] KeyUsage[%d] EncodingMode[%d] CryptoMode[%d] MessageDigestType[%d] unDataInLen[%d]",
            mode, asymKeyInfo.KeyIdx, asymKeyInfo.KeytType, asymKeyInfo.KeyUsage,
            encoding_mode, crypto_mode, message_digest_type, data_in_len);

    int ret = NDK_ERR;
    if (mode == MODE_ENCRYPT) {
        ret = NAPI_SecAsymEncryption(&asymKeyInfo, message_digest_type, encoding_mode, crypto_mode, data_in_len, pDataIn, pDataOutLen, pDataOut);
        LOGD_FMT(">>>NAPI_SecAsymEncryption ret[%d]", ret);
    } else {
        ret = NAPI_SecAsymDecryption(&asymKeyInfo, message_digest_type, encoding_mode, crypto_mode, data_in_len, pDataIn, pDataOutLen, pDataOut);
        LOGD_FMT(">>>NAPI_SecAsymDecryption ret[%d]", ret);
    }

    if (data_in != NULL) {
        (*env)->ReleaseByteArrayElements(env, data_in, pDataIn, 0);
    }
    if (out_data_len != NULL) {
        (*env)->ReleaseIntArrayElements(env, out_data_len, (jint *) pDataOutLen, NULL);
    }
    if (out_data != NULL) {
        (*env)->ReleaseByteArrayElements(env, out_data, pDataOut, NULL);
    }
    return ret;
}
/*
 * Class:     com_newland_nsdk_module_jni_NSDKJni
 * Method:    NAPI_SecEncryption
 * Signature: (Ljava/lang/Object;[B[I[B[I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecEncryption
        (JNIEnv *env, jobject obj, jobject dataInObj, jbyteArray psDataOut, jintArray pnOutLen,
         jbyteArray psKsnOut, jintArray pnOutKsnLen) {
    return calculate(MODE_ENCRYPT, env, obj, dataInObj, psDataOut, pnOutLen, psKsnOut, pnOutKsnLen);
}

/*
 * Class:     com_newland_nsdk_module_jni_NSDKJni
 * Method:    NAPI_SecDecryption
 * Signature: (Ljava/lang/Object;[B[I[B[I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecDecryption
        (JNIEnv *env, jobject obj, jobject dataInObj, jbyteArray psDataOut, jintArray pnOutLen,
         jbyteArray psKsnOut, jintArray pnOutKsnLen) {
    return calculate(MODE_DECRYPT, env, obj, dataInObj, psDataOut, pnOutLen, psKsnOut, pnOutKsnLen);
}

/*
 * Class:     com_newland_nsdk_module_jni_NSDKJni
 * Method:    NAPI_SecVppTpInit
 * Signature: ([B[B[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecVppTpInit
        (JNIEnv *env, jobject obj, jbyteArray numBtn, jbyteArray funcKey, jbyteArray outSeq,
         jint keyboardType) {
    uchar *btn = NULL;
    if (numBtn != NULL) {
        btn = (*env)->GetByteArrayElements(env, numBtn, JNI_FALSE);
        LOGD_FMT(">>>numBtn len[%d]", (*env)->GetArrayLength(env, numBtn));
        LOGD_STR("numBtn", btn, (*env)->GetArrayLength(env, numBtn));
    }
    uchar *key = NULL;
    if (funcKey != NULL) {
        key = (*env)->GetByteArrayElements(env, funcKey, JNI_FALSE);
        LOGD_FMT(">>>funcKey len[%d]", (*env)->GetArrayLength(env, funcKey));
        LOGD_STR("funcKey", key, (*env)->GetArrayLength(env, funcKey));
    }
    uchar keyBuf[20];
    uchar *keySeq = NULL;
    memset(keyBuf, 0, sizeof(keyBuf));
    // keyboard type == 1: 乱序键盘; keyboard type == 0: 正序键盘
    if (keyboardType == 1) {
        // Random keyboard
        keySeq = keyBuf;
    }
    int ret = NAPI_SecVppTpInit(btn, key, keySeq);
    if (ret == 0 && keyboardType == 1) {
        LOGD_STR("key", keyBuf, strlen(keyBuf));
        (*env)->SetByteArrayRegion(env, outSeq, 0, strlen(keyBuf), keyBuf);
    }
    if (numBtn != NULL) {
        (*env)->ReleaseByteArrayElements(env, numBtn, btn, 0);
    }
    if (funcKey != NULL) {
        (*env)->ReleaseByteArrayElements(env, funcKey, key, 0);
    }
    LOGD_FMT(">>>NAPI_SecVppTpInit ret[%d]", ret);
    return ret;
}

/*
 * Class:     com_newland_nsdk_module_jni_NSDKJni
 * Method:    NAPI_SecVPPInit
 * Signature: (III[BIILjava/lang/Object;[BI)I
 */
JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecVPPInit
        (JNIEnv *env, jobject obj, jint SessionType, jint KeyType, jint ucKeyIdx, jstring pan,
         jint PINBlockFmt, jint unTimeOut, jobject RSAKeyObj, jbyteArray AD, jint unADSize) {
    char *pPan = NULL;
    if (pan != NULL) {
        pPan = (*env)->GetStringUTFChars(env, pan, 0);
    }
    uchar *pAD = NULL;
    if (AD != NULL) {
        pAD = (*env)->GetByteArrayElements(env, AD, JNI_FALSE);
    }
    ST_NAPI_RSA_KEY RSAKey;
    memset(&RSAKey, 0, sizeof(ST_NAPI_RSA_KEY));
    ST_NAPI_RSA_KEY *pRSAKey = NULL;

    jclass RSAKeyCls;
    if (RSAKeyObj != NULL) {
        RSAKeyCls = (*env)->GetObjectClass(env, RSAKeyObj);
        pRSAKey = &RSAKey;
        RSAKey.usBits = (*env)->GetIntField(env, RSAKeyObj,
                                            (*env)->GetFieldID(env, RSAKeyCls, "usBits", "I"));
        LOGD_FMT(">>>usBits[%d]", RSAKey.usBits);
        uchar *psModulus;
        jbyteArray Modulus = (jbyteArray) (*env)->GetObjectField(env, RSAKeyObj,
                                                                 (*env)->GetFieldID(env, RSAKeyCls,
                                                                                    "sModulus",
                                                                                    "[B"));
        if (Modulus != NULL) {
            psModulus = (*env)->GetByteArrayElements(env, Modulus, NULL);
            int len = (*env)->GetArrayLength(env, Modulus);
            LOGD_FMT(">>>Modulus len[%d]", len);
            if (len > 0 && len <= MAX_RSA_MODULUS_LEN) {
                memcpy(RSAKey.sModulus, psModulus, len);
                LOGD_STR("Modulus", RSAKey.sModulus, len);
            }
            (*env)->ReleaseByteArrayElements(env, Modulus, psModulus, 0);
        }

        uchar *psExponent;
        jbyteArray Exponent = (jbyteArray) (*env)->GetObjectField(env, RSAKeyObj,
                                                                  (*env)->GetFieldID(env, RSAKeyCls,
                                                                                     "sExponent",
                                                                                     "[B"));
        if (Exponent != NULL) {
            psExponent = (*env)->GetByteArrayElements(env, Exponent, NULL);
            int len = (*env)->GetArrayLength(env, Exponent);
            LOGD_FMT(">>>Exponent len[%d]", len);
            if (len > 0 && len <= MAX_RSA_MODULUS_LEN) {
                memcpy(RSAKey.sExponent, psExponent, len);
                LOGD_STR("Exponent", RSAKey.sExponent, len);
            }
            (*env)->ReleaseByteArrayElements(env, Exponent, psExponent, 0);
        }
    }
    LOGD_FMT(
            ">>>SessionType[%d] KeyType[%d] ucKeyIdx[%d] PINBlockFmt[%d] unTimeOut[%d] pAD[%d] unADSize[%d]",
            SessionType, KeyType, ucKeyIdx, PINBlockFmt, unTimeOut, pAD, unADSize);
    int ret = NAPI_SecVPPInit(SessionType, KeyType, ucKeyIdx, pPan, PINBlockFmt, unTimeOut, pRSAKey,
                              pAD, unADSize);
    if (pan != NULL) {
        (*env)->ReleaseStringUTFChars(env, pan, pPan);
    }
    if (AD != NULL) {
        (*env)->ReleaseByteArrayElements(env, AD, pAD, JNI_FALSE);
    }
    LOGD_FMT(">>>NAPI_SecVPPInit ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecVPPInit_1DerivateKey(JNIEnv *env,
                                                                              jobject thiz,
                                                                              jint session_type,
                                                                              jint key_type,
                                                                              jint key_id,
                                                                              jstring pan,
                                                                              jint pinblock_fmt,
                                                                              jint timeout,
                                                                              jobject dukpt_derivate_data) {
    char *pPan = NULL;
    if (pan != NULL) {
        pPan = (*env)->GetStringUTFChars(env, pan, 0);
    }

    ST_SEC_DUKPT_DERIVATE_DATA  stDerivateData;
    memset(&stDerivateData, 0x00, sizeof(ST_SEC_DUKPT_DERIVATE_DATA));

    jclass derivateDataCls = (*env)->GetObjectClass(env, dukpt_derivate_data);
    if (derivateDataCls == NULL) {
        LOGD_FMT(">>>derivateDataCls[%d]", derivateDataCls);
        return NDK_ERR_PARA;
    }

    stDerivateData.KeyType = (*env)->GetIntField(env, dukpt_derivate_data,
                                                 (*env)->GetFieldID(env, derivateDataCls, "derivateKeyType",
                                                                    "I"));
    stDerivateData.nKeyLen = (*env)->GetIntField(env, dukpt_derivate_data,
                                                 (*env)->GetFieldID(env, derivateDataCls, "derivateKeyLen",
                                                                    "I"));
    stDerivateData.DerivateUsage = (*env)->GetIntField(env, dukpt_derivate_data,
                                                       (*env)->GetFieldID(env, derivateDataCls, "derivateKeyUsage",
                                                                          "I"));

    LOGD_FMT(
            ">>>SessionType[%d] KeyType[%d] ucKeyIdx[%d] PINBlockFmt[%d] unTimeOut[%d] derivateKeyType[%d] derivateKeyUsage[%d] derivateKeyLen[%d]",
            session_type, key_type, key_id, pinblock_fmt, timeout, stDerivateData.KeyType, stDerivateData.DerivateUsage, stDerivateData.nKeyLen);
    int ret = NAPI_SecVPPInit(session_type, key_type, key_id, pPan, pinblock_fmt, timeout, NULL,
                              &stDerivateData, sizeof(ST_SEC_DUKPT_DERIVATE_DATA));
    if (pan != NULL) {
        (*env)->ReleaseStringUTFChars(env, pan, pPan);
    }
    LOGD_FMT(">>>NAPI_SecVPPInit ret[%d]", ret);
    return ret;
}

/*
 * Class:     com_newland_nsdk_module_jni_NSDKJni
 * Method:    NAPI_SecVPPGetEvent
 * Signature: ([I[B[I[B[I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecVPPGetEvent
        (JNIEnv *env, jobject obj, jintArray nEvent, jbyteArray psPinBlock, jintArray pnOutPinLen,
         jbyteArray psKsn, jintArray pnOutKsnLen) {
    uint event;
    uchar pinBlock[32];
    int pinBlockLen = 0;
    uchar ksn[32];
    int ksnLen = 0;
    memset(pinBlock, 0, sizeof(pinBlock));
    memset(ksn, 0, sizeof(ksn));
    int ret = NAPI_SecVPPGetEvent(&event, pinBlock, &pinBlockLen, ksn, &ksnLen);
    LOGD_FMT(">>>NAPI_SecVPPGetEvent ret: %d", ret);
    if (ret == NDK_OK) {
//        if(event != SEC_VPP_KEY_NULL){
        LOGD_FMT(">>>event[%d] pinBlockLen[%d] ksnLen[%d]", event, pinBlockLen, ksnLen);
//        LOGD_STR("pinBlock", pinBlock, pinBlockLen);
//        LOGD_STR("ksn", ksn, ksnLen);
//        }
        if (nEvent != NULL) {
            (*env)->SetIntArrayRegion(env, nEvent, 0, 1, &event);
        }
        if (psPinBlock != NULL && pnOutPinLen != NULL) {
            (*env)->SetIntArrayRegion(env, pnOutPinLen, 0, 1, &pinBlockLen);
            (*env)->SetByteArrayRegion(env, psPinBlock, 0, pinBlockLen, &pinBlock);
        }
        if (psKsn != NULL && pnOutKsnLen != NULL) {
            (*env)->SetIntArrayRegion(env, pnOutKsnLen, 0, 1, &ksnLen);
            (*env)->SetByteArrayRegion(env, psKsn, 0, ksnLen, &ksn);
        }
    }
    return ret;
}

/*
 * Class:     com_newland_nsdk_module_jni_NSDKJni
 * Method:    NAPI_SecVPPSetEvent
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecVPPSetEvent
        (JNIEnv *env, jobject obj, jint key) {
    LOGD_FMT(">>>key[%d]", key);
    int ret = NAPI_SecVPPSetEvent(key);
    LOGD_FMT(">>>NAPI_SecVPPSetEvent ret[%d]", ret);
    return ret;
}


/*
 * Class:     com_newland_nsdk_module_jni_NSDKJni
 * Method:    NAPI_SecVPPSetExpPinLenIn
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecVPPSetExpPinLenIn
        (JNIEnv *env, jobject obj, jstring pinLenIn) {
    char *pPinLenIn = NULL;
    if (pinLenIn != NULL) {
        pPinLenIn = (*env)->GetStringUTFChars(env, pinLenIn, 0);
        LOGD_FMT(">>>pinLenIn[%s]", pPinLenIn);
    }
    int ret = NAPI_SecVPPSetExpPinLenIn(pPinLenIn);
    if (pinLenIn != NULL) {
        (*env)->ReleaseStringUTFChars(env, pinLenIn, pPinLenIn);
    }
    LOGD_FMT(">>>NAPI_SecVPPSetExpPinLenIn ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecGetRandom(JNIEnv *env, jobject thiz,
                                                                         jint n_rand_len,
                                                                         jbyteArray pv_random) {
    int ret = -1;
    jbyte *prandom = NULL;

    prandom = (*env)->GetByteArrayElements(env, pv_random, 0);
    ret = NAPI_SecGetRandom(n_rand_len, prandom);
    (*env)->ReleaseByteArrayElements(env, pv_random, prandom, 0);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1SecVerifyPIN(JNIEnv *env, jobject thiz, jint key_id,
                                                           jint key_type, jbyteArray ps_tsk,
                                                           jbyteArray ps_pan,
                                                           jbyteArray pin_block, jobject p_rsakey,
                                                           jbyteArray ps_icc_resp_out,
                                                           jintArray out_len) {
    int ret = -1;
    jbyte *pan = NULL;
    jbyte *pinBlock = NULL;
    jbyte *tsk = NULL;
    int pinBlockLen = 0;
    char *resp = (char *) malloc(32);
    if (ps_pan == NULL || pin_block == NULL || ps_tsk == NULL) {
        return ret;
    }

    pan = (*env)->GetByteArrayElements(env, ps_pan, 0);
    pinBlock = (*env)->GetByteArrayElements(env, pin_block, 0);
    tsk = (*env)->GetByteArrayElements(env, ps_tsk, 0);
    pinBlockLen = strlen(pinBlock);
    __android_log_print(ANDROID_LOG_INFO, "crypto", ">>>tsk len[%d]", sizeof(tsk));

    ST_NAPI_RSA_KEY RSAKey;
    memset(&RSAKey, 0, sizeof(ST_NAPI_RSA_KEY));
    ST_NAPI_RSA_KEY *pRSAKey = NULL;
    jclass RSAKeyCls;
    if (p_rsakey != NULL) {
        RSAKeyCls = (*env)->GetObjectClass(env, p_rsakey);
        pRSAKey = &RSAKey;
        RSAKey.usBits = (*env)->GetIntField(env, p_rsakey,
                                            (*env)->GetFieldID(env, RSAKeyCls, "usBits", "I"));
        __android_log_print(ANDROID_LOG_INFO, "crypto", ">>>usBits[%d]", RSAKey.usBits);
        uchar *psModulus;
        jbyteArray Modulus = (jbyteArray) (*env)->GetObjectField(env, p_rsakey,
                                                                 (*env)->GetFieldID(env, RSAKeyCls,
                                                                                    "sModulus",
                                                                                    "[B"));
        if (Modulus != NULL) {
            psModulus = (*env)->GetByteArrayElements(env, Modulus, NULL);
            int len = (*env)->GetArrayLength(env, Modulus);
            __android_log_print(ANDROID_LOG_INFO, "crypto", ">>>Modulus len[%d]", len);
            if (len > 0 && len <= MAX_RSA_MODULUS_LEN) {
                memcpy(RSAKey.sModulus, psModulus, len);
            }
            (*env)->ReleaseByteArrayElements(env, Modulus, psModulus, 0);
        }

        uchar *psExponent;
        jbyteArray Exponent = (jbyteArray) (*env)->GetObjectField(env, p_rsakey,
                                                                  (*env)->GetFieldID(env, RSAKeyCls,
                                                                                     "sExponent",
                                                                                     "[B"));
        if (Exponent != NULL) {
            psExponent = (*env)->GetByteArrayElements(env, Exponent, NULL);
            int len = (*env)->GetArrayLength(env, Exponent);
            __android_log_print(ANDROID_LOG_INFO, "crypto", ">>>Exponent len[%d]", len);
            if (len > 0 && len <= MAX_RSA_MODULUS_LEN) {
                memcpy(RSAKey.sExponent, psExponent, len);
            }
            (*env)->ReleaseByteArrayElements(env, Exponent, psExponent, 0);
        }

        ret = NDK_SecVerifyPIN(key_id, key_type, sizeof(tsk), tsk, pan, pinBlockLen, pinBlock,
                               &RSAKey, resp, 1);
    } else {
        ret = NDK_SecVerifyPIN(key_id, key_type, sizeof(tsk), tsk, pan, pinBlockLen, pinBlock, NULL,
                               resp, 0);
    }
    __android_log_print(ANDROID_LOG_INFO, "crypto", ">>>ret [%d]", ret);
    if (ret == NDK_OK) {
        out_len = strlen(resp);
        (*env)->SetByteArrayRegion(env, ps_icc_resp_out, 0, out_len, (jbyte *) resp);
        (*env)->SetIntArrayRegion(env, out_len, 0, 1, out_len);
    }

    free(resp);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecIncreaseKsn(JNIEnv *env, jobject thiz, jint id) {
    int ret = NAPI_ERR;
    EM_SEC_KEY_INFO_ID KeyInfoId = SEC_KEY_INFO_KSN;
    uchar keyId = id;
    EM_SEC_CRYPTO_KEY_TYPE KeyType = KEY_TYPE_DES;
    EM_SEC_KEY_USAGE KeyUsage = KEY_USE_DUKPT;
    uchar *pAD = NULL;
    uint unADSize = 0;
    uchar ksnBefore[11] = {0};
    uchar ksnAfter[11] = {0};
    int ksnLen = 0;

    ret = NAPI_SecGetKeyInfo(KeyInfoId, keyId, KeyType, KeyUsage, pAD, unADSize, ksnBefore,
                             &ksnLen);
    if (ret != NAPI_OK) {
        LOGD_FMT(">>>Increase KSN, NAPI_SecGetKeyInfo ret[%d]", ret);
        return ret;
    }

    EM_SEC_KEYIN_METHOD method = SEC_KIM_DUKPT_DERIVE;
    ST_SEC_KEYIN_DATA stKeyData;
    ST_SEC_KCV_DATA stKcvData;

    //Initialise Structure
    memset(&stKeyData, 0x0, sizeof(ST_SEC_KEYIN_DATA));
    memset(&stKcvData, 0x0, sizeof(ST_SEC_KCV_DATA));

    stKeyData.ucKeyIdx = id;
    stKeyData.KeyType = KEY_TYPE_DES;
    stKeyData.KeyUsage = KEY_USE_DUKPT;
    stKeyData.CipherMode = SEC_CIPHER_MODE_ECB;

    stKeyData.nKsnLen = ksnLen;
    stKeyData.psKsn = ksnBefore;
    //Without KCV Verification
    stKcvData.nCheckMode = NAPI_SEC_KCV_NONE;
    stKcvData.nLen = 0;

    ret = NAPI_SecGenerateKey(method, &stKeyData, &stKcvData);
    LOGD_FMT(">>>Increase KSN, NAPI_SecGenerateKey ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecIncreaseAESKSN(JNIEnv *env, jobject thiz,
                                                                        jint id) {
    int ret = NAPI_ERR;
    EM_SEC_KEY_INFO_ID KeyInfoId = SEC_KEY_INFO_KSN;
    uchar keyId = id;
    EM_SEC_CRYPTO_KEY_TYPE KeyType = KEY_TYPE_AES;
    EM_SEC_KEY_USAGE KeyUsage = KEY_USE_DUKPT;
    uchar *pAD = NULL;
    uint unADSize = 0;
    uchar ksnBefore[12] = {0};
    uchar ksnAfter[12] = {0};
    int ksnLen = 0;

    ret = NAPI_SecGetKeyInfo(KeyInfoId, keyId, KeyType, KeyUsage, pAD, unADSize, ksnBefore,
                             &ksnLen);
    if (ret != NAPI_OK) {
        LOGD_FMT(">>>Increase AES KSN, NAPI_SecGetKeyInfo ret[%d]", ret);
        return ret;
    }

    EM_SEC_KEYIN_METHOD method = SEC_KIM_DUKPT_DERIVE;
    ST_SEC_KEYIN_DATA stKeyData;
    ST_SEC_KCV_DATA stKcvData;

    //Initialise Structure
    memset(&stKeyData, 0x0, sizeof(ST_SEC_KEYIN_DATA));
    memset(&stKcvData, 0x0, sizeof(ST_SEC_KCV_DATA));

    stKeyData.ucKeyIdx = id;
    stKeyData.KeyType = KEY_TYPE_AES;
    stKeyData.KeyUsage = KEY_USE_DUKPT;
    stKeyData.CipherMode = SEC_CIPHER_MODE_ECB;
    stKeyData.nKsnLen = ksnLen;
    stKeyData.psKsn = ksnBefore;
    //Without KCV Verification
    stKcvData.nCheckMode = NAPI_SEC_KCV_NONE;
    stKcvData.nLen = 0;

    ret = NAPI_SecGenerateKey(method, &stKeyData, &stKcvData);
    LOGD_FMT(">>>Increase AES KSN, NAPI_SecGenerateKey ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecAsymGenerateKey(JNIEnv *env, jobject thiz,
                                                                  jint method,
                                                                  jobject keyDataObj,
                                                                  jobject kcvDataObj,
                                                                  jintArray randomKeyLen,
                                                                  jbyteArray randomKey) {
    jclass keyDataCls = (*env)->GetObjectClass(env, keyDataObj);
    if (keyDataCls == NULL) {
        LOGD_FMT(">>>keyDataCls[%d]", keyDataCls);
        return NDK_ERR_PARA;
    }

    uchar *pRandomKey = NULL;
    if (randomKey != NULL) {
        pRandomKey = (uchar *) (*env)->GetByteArrayElements(env, randomKey, NULL);
    }

    int *pRandomKeyLen = NULL;
    if (randomKeyLen != NULL) {
        pRandomKeyLen = (int *) (*env)->GetIntArrayElements(env, randomKeyLen, NULL);
    }

    ST_SEC_ASYM_KEYIN_DATA stDataIn;
    memset(&stDataIn, 0, sizeof(ST_SEC_ASYM_KEYIN_DATA));

    stDataIn.ucKEKIdx = (*env)->GetIntField(env, keyDataObj,
                                            (*env)->GetFieldID(env, keyDataCls, "ucKEKIdx", "I"));
    stDataIn.KEKType = (*env)->GetIntField(env, keyDataObj,
                                           (*env)->GetFieldID(env, keyDataCls, "KEKType", "I"));
    stDataIn.KEKUsage = (*env)->GetIntField(env, keyDataObj,
                                            (*env)->GetFieldID(env, keyDataCls, "KEKUsage", "I"));

    stDataIn.ucKeyIdx = (*env)->GetIntField(env, keyDataObj,
                                            (*env)->GetFieldID(env, keyDataCls, "ucKeyIdx", "I"));
    stDataIn.KeyType = (*env)->GetIntField(env, keyDataObj,
                                           (*env)->GetFieldID(env, keyDataCls, "KeyType", "I"));
    stDataIn.KeyUsage = (*env)->GetIntField(env, keyDataObj,
                                            (*env)->GetFieldID(env, keyDataCls, "KeyUsage", "I"));

    stDataIn.MdAlg = (*env)->GetIntField(env, keyDataObj,
                                         (*env)->GetFieldID(env, keyDataCls, "MdAlg",
                                                            "I"));
    stDataIn.EncodingMode = (*env)->GetIntField(env, keyDataObj,
                                                (*env)->GetFieldID(env, keyDataCls, "EncodingMode",
                                                                   "I"));

    stDataIn.nKeyLen = (*env)->GetIntField(env, keyDataObj,
                                           (*env)->GetFieldID(env, keyDataCls, "nKeyLen", "I"));

    uchar *pkeyData = NULL;
    jbyteArray keyData = (jbyteArray) (*env)->GetObjectField(env, keyDataObj,
                                                             (*env)->GetFieldID(env, keyDataCls,
                                                                                "pKeyData", "[B"));
    if (method == SEC_KIM_RANDOM_OUT) {
        stDataIn.pKeyData = pRandomKey;
        stDataIn.pAD = pRandomKeyLen;
    } else {
        if (keyData != NULL) {
            pkeyData = (*env)->GetByteArrayElements(env, keyData, NULL);
            stDataIn.pKeyData = pkeyData;
        }
    }

    stDataIn.nKsnLen = (*env)->GetIntField(env, keyDataObj,
                                           (*env)->GetFieldID(env, keyDataCls, "nKsnLen", "I"));

    uchar *pksnData;
    jbyteArray ksnData = (jbyteArray) (*env)->GetObjectField(env, keyDataObj,
                                                             (*env)->GetFieldID(env, keyDataCls,
                                                                                "psKsn", "[B"));
    if (ksnData != NULL && stDataIn.nKsnLen > 0) {
        pksnData = (*env)->GetByteArrayElements(env, ksnData, NULL);
        stDataIn.psKsn = pksnData;
        LOGD_STR("ksnData", pksnData, stDataIn.nKsnLen);
    }

    LOGD_FMT(
            ">>>method[%d] ucKEKIdx[%d] KEKType[%d] KEKUsage[%d] ucKeyIdx[%d] KeyType[%d] KeyUsage[%d] EncodingMode[%d] MdAlg[%d] nKeyLen[%d] ", \
             method, stDataIn.ucKEKIdx, stDataIn.KEKType, stDataIn.KEKUsage, stDataIn.ucKeyIdx,
            stDataIn.KeyType, stDataIn.KeyUsage, \
             stDataIn.EncodingMode, stDataIn.MdAlg, stDataIn.nKeyLen);
//    LOGD_FMT(">>>keyData[%d] nKsnLen[%d] ksnData[%d] nADSize[%d]", keyData,
//             stDataIn.nKsnLen, ksnData, stDataIn.nADSize);

    jclass kcvDataCls = (*env)->GetObjectClass(env, kcvDataObj);
    if (kcvDataCls == NULL) {
        LOGD_FMT(">>>kcvDataCls[%d]", kcvDataCls);
        return NDK_ERR_PARA;
    }
    ST_SEC_KCV_DATA stKcvData;
    memset(&stKcvData, 0, sizeof(ST_SEC_KCV_DATA));

    stKcvData.nCheckMode = (*env)->GetIntField(env, kcvDataObj,
                                               (*env)->GetFieldID(env, kcvDataCls, "nCheckMode",
                                                                  "I"));
    stKcvData.nLen = (*env)->GetIntField(env, kcvDataObj,
                                         (*env)->GetFieldID(env, kcvDataCls, "nLen", "I"));
    if (stKcvData.nLen > 8) {
        LOGD_FMT(">>>nLen[%d]", stKcvData.nLen);
        return NDK_ERR_PARA;
    }
    jbyteArray sCheckBuf = (jbyteArray) (*env)->GetObjectField(env, kcvDataObj,
                                                               (*env)->GetFieldID(env, kcvDataCls,
                                                                                  "sCheckBuf",
                                                                                  "[B"));
    LOGD_FMT(">>>nCheckMode[%d] nLen[%d] sCheckBuf[%d]", stKcvData.nCheckMode, stKcvData.nLen,
             sCheckBuf);
    uchar *pCheckBuf;
    if (sCheckBuf != NULL) {
        pCheckBuf = (*env)->GetByteArrayElements(env, sCheckBuf, NULL);
        memcpy(stKcvData.sCheckBuf, pCheckBuf, stKcvData.nLen);
        LOGD_STR("kcvValue", pCheckBuf, stKcvData.nLen);
    }

    int ret = NAPI_SecAsymGenerateKey(method, &stDataIn, &stKcvData);

    if (method != SEC_KIM_RANDOM_OUT && keyData != NULL) {
        (*env)->ReleaseByteArrayElements(env, keyData, pkeyData, NULL);
    }

    if (ksnData != NULL) {
        (*env)->ReleaseByteArrayElements(env, ksnData, pksnData, NULL);
    }

    if (randomKeyLen != NULL) {
        (*env)->ReleaseIntArrayElements(env, randomKeyLen, (jint *) pRandomKeyLen, NULL);
    }
    if (randomKey != NULL) {
        (*env)->ReleaseByteArrayElements(env, randomKey, pRandomKey, NULL);
    }

    if (sCheckBuf != NULL) {
        (*env)->ReleaseByteArrayElements(env, sCheckBuf, pCheckBuf, NULL);
    }

    LOGD_FMT(">>>NAPI_SecAsymGenerateKey ret[%d]", ret);
    return ret;
}

jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecLoadTrustedCert(JNIEnv *env, jobject thiz,
                                                                               jboolean is_ca, jint length,
                                                                               jbyteArray cert,
                                                                               jintArray pub_key_len,
                                                                               jbyteArray pub_key) {
    int *lenvalue = NULL;
    int ret = -1;
    uchar *ppubkey = NULL;
    uchar *pcetdata = NULL;
    unsigned char cetdata[6144] = {0};

    if (pub_key_len != NULL) {
        lenvalue = (int *) (*env)->GetIntArrayElements(env, pub_key_len, NULL);
    }

    if (pub_key != NULL) {
        ppubkey = (uchar *) (*env)->GetByteArrayElements(env, pub_key, NULL);
    }
    if (cert != NULL) {
        pcetdata = (uchar *) (*env)->GetByteArrayElements(env, cert, NULL);
    }

    memcpy(cetdata, pcetdata, length);

    LOGD_FMT(">>>load CA cert, isCA[%d], certLen[%d]", is_ca, length);
    LOGD_STR("cert data", cetdata, length);
    if (is_ca) {
        ret = NAPI_SecLoadTrustedCert(1, (char *) cetdata, length, ppubkey, lenvalue);
    } else {
        ret = NAPI_SecLoadTrustedCert(0, (char *) cetdata, length, ppubkey, lenvalue);
    }
    LOGD_FMT("NAPI_SecLoadTrustedCert ret = %d", ret);

    if (ppubkey != NULL) {
        (*env)->ReleaseByteArrayElements(env, pub_key, (jbyte *) ppubkey, 0);
    }
    if (pcetdata != NULL) {
        (*env)->ReleaseByteArrayElements(env, cert, (jbyte *) pcetdata, 0);
    }

    if (lenvalue != NULL) {
        (*env)->ReleaseIntArrayElements(env, pub_key_len, (jint *) lenvalue, 0);
    }

    return (jint) ret;

}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecResetCertStatus(JNIEnv *env, jobject thiz) {
    int ret = NAPI_SecResetCertStatus();
    LOGD_FMT(">>>NAPI_SecResetCertStatus ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecInitAtomic(JNIEnv *env, jobject thiz) {
    int ret = NAPI_SecInitAtomic();
    LOGD_FMT(">>>NAPI_SecInitAtomic ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecCommitAtomic(JNIEnv *env, jobject thiz,
                                                                            jboolean is_successful) {
    int ret = NAPI_SecCommitAtomic(is_successful);
    LOGD_FMT(">>>NAPI_SecCommitAtomic ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecAsymEncryption(JNIEnv *env, jobject thiz,
                                                                              jbyte key_id, jbyte key_type,
                                                                              jbyte key_usage,
                                                                              jint message_digest_type,
                                                                              jint encoding_mode,
                                                                              jint crypto_mode,
                                                                              jint data_in_len,
                                                                              jbyteArray data_in,
                                                                              jintArray out_data_len,
                                                                              jbyteArray out_data) {
    return calculateAsym(MODE_ENCRYPT, env, thiz, key_id, key_type, key_usage, message_digest_type, encoding_mode, crypto_mode, data_in_len, data_in, out_data_len, out_data);
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecAsymDecryption(JNIEnv *env, jobject thiz,
                                                                              jbyte key_id, jbyte key_type,
                                                                              jbyte key_usage,
                                                                              jint message_digest_type,
                                                                              jint encoding_mode,
                                                                              jint crypto_mode,
                                                                              jint data_in_len,
                                                                              jbyteArray data_in,
                                                                              jintArray out_data_len,
                                                                              jbyteArray out_data) {
    return calculateAsym(MODE_DECRYPT, env, thiz, key_id, key_type, key_usage, message_digest_type, encoding_mode, crypto_mode, data_in_len, data_in, out_data_len, out_data);
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecAsymSign(JNIEnv *env, jobject thiz, jbyte key_id,
                                                                        jbyte key_type, jbyte key_usage,
                                                                        jint message_digest_type,
                                                                        jint encoding_mode, jint hash_len,
                                                                        jbyteArray hash,
                                                                        jintArray sig_data_len, jbyteArray sig_data) {
    ST_SEC_ASYM_KEY_INFO asymKeyInfo;

    memset(&asymKeyInfo, 0, sizeof(asymKeyInfo));
    asymKeyInfo.KeyIdx = key_id;
    asymKeyInfo.KeytType = key_type;
    asymKeyInfo.KeyUsage = key_usage;

    uchar *pDataOut = NULL;
    if (sig_data != NULL) {
        pDataOut = (uchar *) (*env)->GetByteArrayElements(env, sig_data, NULL);
    }

    int *pDataOutLen= NULL;
    if (sig_data_len != NULL) {
        pDataOutLen = (int *) (*env)->GetIntArrayElements(env, sig_data_len, NULL);
    }

    uchar *pDataIn;
    if (hash != NULL) {
        pDataIn = (*env)->GetByteArrayElements(env, hash, NULL);
        LOGD_FMT("hashLen[%d]", hash_len);
    }

    LOGD_FMT(
            ">>>ucKeyID[%d] KeyType[%d] KeyUsage[%d] EncodingMode[%d] MessageDigestType[%d]",
            asymKeyInfo.KeyIdx, asymKeyInfo.KeytType, asymKeyInfo.KeyUsage,
            encoding_mode, message_digest_type);

    int ret = NDK_ERR;
    ret = NAPI_SecAsymSign(&asymKeyInfo, message_digest_type, encoding_mode, hash_len, pDataIn, pDataOutLen, pDataOut);
    LOGD_FMT(">>>NAPI_SecAsymSign ret[%d]", ret);

    if (hash != NULL) {
        (*env)->ReleaseByteArrayElements(env, hash, pDataIn, 0);
    }
    if (sig_data_len != NULL) {
        (*env)->ReleaseIntArrayElements(env, sig_data_len, (jint *) pDataOutLen, NULL);
    }
    if (sig_data != NULL) {
        (*env)->ReleaseByteArrayElements(env, sig_data, pDataOut, NULL);
    }
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecAsymVerify(JNIEnv *env, jobject thiz,
                                                                          jbyte key_id, jbyte key_type,
                                                                          jbyte key_usage,
                                                                          jint message_digest_type,
                                                                          jint encoding_mode, jint hash_length,
                                                                          jbyteArray hash,
                                                                          jint signed_data_length,
                                                                          jbyteArray signed_data) {
    ST_SEC_ASYM_KEY_INFO asymKeyInfo;

    memset(&asymKeyInfo, 0, sizeof(asymKeyInfo));
    asymKeyInfo.KeyIdx = key_id;
    asymKeyInfo.KeytType = key_type;
    asymKeyInfo.KeyUsage = key_usage;

    uchar *pSignedData = NULL;
    if (signed_data != NULL) {
        pSignedData = (uchar *) (*env)->GetByteArrayElements(env, signed_data, NULL);
        LOGD_FMT("signed data len[%d]", signed_data_length);
    }

    uchar *pHash;
    if (hash != NULL) {
        pHash = (*env)->GetByteArrayElements(env, hash, NULL);
        LOGD_FMT("hashLen[%d]", hash_length);
    }

    LOGD_FMT(
            ">>>ucKeyID[%d] KeyType[%d] KeyUsage[%d] EncodingMode[%d] MessageDigestType[%d]",
            asymKeyInfo.KeyIdx, asymKeyInfo.KeytType, asymKeyInfo.KeyUsage,
            encoding_mode, message_digest_type);

    int ret = NDK_ERR;
    ret = NAPI_SecAsymVerify(&asymKeyInfo, message_digest_type, encoding_mode, hash_length, pHash, signed_data_length, pSignedData);
    LOGD_FMT(">>>NAPI_SecAsymVerify ret[%d]", ret);

    if (hash != NULL) {
        (*env)->ReleaseByteArrayElements(env, hash, pHash, NULL);
    }
    if (signed_data != NULL) {
        (*env)->ReleaseByteArrayElements(env, signed_data, pSignedData, NULL);
    }
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_verifyOfflinePIN(JNIEnv *env, jobject thiz,
                                                                 jint pin_session_type,
                                                                 jint key_type_code, jint key_id,
                                                                 jint key_usage_code, jstring pan,
                                                                 jint pin_block_format,
                                                                 jbyteArray pin_block,
                                                                 jobject jni_rsakey,
                                                                 jbyteArray ext_key,
                                                                 jbyteArray data_out,
                                                                 jintArray data_out_len) {
    char *pPan = NULL;
    if (pan != NULL) {
        pPan = (*env)->GetStringUTFChars(env, pan, 0);
    }

    ST_SEC_VERIFY_PIN_AD pAD;
    memset(&pAD, 0, sizeof(ST_SEC_VERIFY_PIN_AD));

    uchar *pPINBlock = NULL;
    int pinBlockLen = 0;
    if (pin_block != NULL) {
        pPINBlock = (uchar *) (*env)->GetByteArrayElements(env, pin_block, NULL);
        pinBlockLen = (*env)->GetArrayLength(env, pin_block);
        memcpy(pAD.psPinBlock, pPINBlock, pinBlockLen);
        pAD.unPinBlockLen = pinBlockLen;
        LOGD_FMT("PIN block len[%d] ", pinBlockLen);
        (*env)->ReleaseByteArrayElements(env, pin_block, pPINBlock, 0);
    }

    uchar *pTSK = NULL;
    int tskLen = 0;
    if (ext_key != NULL) {
        pTSK = (uchar *) (*env)->GetByteArrayElements(env, ext_key, NULL);
        tskLen = (*env)->GetArrayLength(env, ext_key);
        memcpy(pAD.psTSK, pTSK, tskLen);
        pAD.unTSKLen = tskLen;
        LOGD_FMT("TSKLen[%d] ", tskLen);
        (*env)->ReleaseByteArrayElements(env, ext_key, pTSK, 0);
    }
    pAD.KeyUsage = key_usage_code;

    ST_NAPI_RSA_KEY RSAKey;
    memset(&RSAKey, 0, sizeof(ST_NAPI_RSA_KEY));

    jclass RSAKeyCls;
    if (jni_rsakey != NULL) {
        RSAKeyCls = (*env)->GetObjectClass(env, jni_rsakey);
        RSAKey.usBits = (*env)->GetIntField(env, jni_rsakey,
                                            (*env)->GetFieldID(env, RSAKeyCls, "usBits", "I"));
        LOGD_FMT(">>>usBits[%d]", RSAKey.usBits);
        uchar *psModulus;
        jbyteArray Modulus = (jbyteArray) (*env)->GetObjectField(env, jni_rsakey,
                                                                 (*env)->GetFieldID(env, RSAKeyCls,
                                                                                    "sModulus",
                                                                                    "[B"));
        if (Modulus != NULL) {
            psModulus = (*env)->GetByteArrayElements(env, Modulus, NULL);
            int len = (*env)->GetArrayLength(env, Modulus);
            LOGD_FMT(">>>Modulus len[%d]", len);
            if (len > 0 && len <= MAX_RSA_MODULUS_LEN) {
                memcpy(RSAKey.sModulus, psModulus, len);
                LOGD_STR("Modulus", RSAKey.sModulus, len);
            }
            (*env)->ReleaseByteArrayElements(env, Modulus, psModulus, 0);
        }

        uchar *psExponent;
        jbyteArray Exponent = (jbyteArray) (*env)->GetObjectField(env, jni_rsakey,
                                                                  (*env)->GetFieldID(env, RSAKeyCls,
                                                                                     "sExponent",
                                                                                     "[B"));
        if (Exponent != NULL) {
            psExponent = (*env)->GetByteArrayElements(env, Exponent, NULL);
            int len = (*env)->GetArrayLength(env, Exponent);
            LOGD_FMT(">>>Exponent len[%d]", len);
            if (len > 0 && len <= MAX_RSA_MODULUS_LEN) {
                memcpy(RSAKey.sExponent, psExponent, len);
                LOGD_STR("Exponent", RSAKey.sExponent, len);
            }
            (*env)->ReleaseByteArrayElements(env, Exponent, psExponent, 0);
        }
    }
    LOGD_FMT(
            ">>>SessionType[%d] KeyType[%d] ucKeyIdx[%d] keyUsage[%d] PINBlockFmt[%d] pRSAKey[%d] pAD[%d] unADSize[%d]",
            pin_session_type, key_type_code, key_id, key_usage_code, pin_block_format,  &RSAKey, &pAD, sizeof(ST_SEC_VERIFY_PIN_AD));
    // 1. NAPI_SecVPPInit 主要是把参数传下去
    int ret = NAPI_SecVPPInit(pin_session_type, key_type_code, key_id, pPan, pin_block_format, 200, &RSAKey,
                              &pAD, sizeof(ST_SEC_VERIFY_PIN_AD));
    if (pan != NULL) {
        (*env)->ReleaseStringUTFChars(env, pan, pPan);
    }
    LOGD_FMT(">>>NAPI_SecVPPInit ret[%d]", ret);

    if (ret != NAPI_OK) {
        return ret;
    }

    int nEvent;
    int pnOutPinLen = 0;
    uchar psIccRespOut[100] = {0};

    // 2. NAPI_SecVPPGetEvent 进行加解密等运算，验证脱机 PIN，最后返回跟卡片验证的结果
    ret = NAPI_SecVPPGetEvent(&nEvent, psIccRespOut, &pnOutPinLen, NULL, NULL);
    LOGD_FMT(">>>NAPI_SecVPPGetEvent ret[%d]", ret);
    LOGD_FMT(">>>psIccRespOut[%s], len[%d]", psIccRespOut, pnOutPinLen);

    if (ret == 0) {
        (*env)->SetIntArrayRegion(env, data_out_len, 0, 1, &pnOutPinLen);
        (*env)->SetByteArrayRegion(env, data_out, 0, pnOutPinLen, psIccRespOut);
    }
    return ret;
}

JNIEXPORT jint
getRequest(JNIEnv const *env, jbyteArray out_info, jintArray out_info_len, jbyteArray err_msg, jintArray err_msg_len, int commandType) {
    char outBuf[6 * 1024] = {0};
    int outLen = 0;
    char* szErrorMsg = NULL;
    int ret = -1;

    if (commandType == COMMAND_PEDI) {
        ret = NDK_KmlRkiGetPediRequest(outBuf, &outLen, &szErrorMsg);
        LOGD_FMT(">>>NDK_KmlRkiGetPediRequest ret[%d], len[%d]", ret, outLen);
    } else if (commandType == COMMAND_PEDK) {
        ret = NDK_KmlRkiGetPedkInitialRequest(outBuf, &outLen, &szErrorMsg);
        LOGD_FMT(">>>NDK_KmlRkiGetPedkInitialRequest ret[%d], len[%d]", ret, outLen);
    } else if (commandType == COMMAND_PEDV) {
        ret = NDK_KmlRkiGetPedvRequest(outBuf, &outLen, &szErrorMsg);
        LOGD_FMT(">>>NDK_KmlRkiGetPedvRequest ret[%d], len[%d]", ret, outLen);
    }
    if (ret == 0) {
        (*env)->SetIntArrayRegion(env, out_info_len, 0, 1, &outLen);
        (*env)->SetByteArrayRegion(env, out_info, 0, outLen, outBuf);
    } else {
        LOGD_FMT(">>>Get request error message: %s", szErrorMsg);
        if (szErrorMsg != NULL) {
            int errLen = strlen(szErrorMsg);
            if (errLen > 0) {
                (*env)->SetIntArrayRegion(env, err_msg_len, 0, 1, &errLen);
                (*env)->SetByteArrayRegion(env, err_msg, 0, errLen, szErrorMsg);
            }
        }
    }

    return ret;
}

JNIEXPORT jint setResponse(JNIEnv const *env, jbyteArray data, jint length, jbyteArray err_msg, jintArray err_msg_len, int commandType) {
    int nRespLen = 0;
    char *pData = NULL;
    char* szErrorMsg = NULL;
    int ret = -1;
    char arry[5*1024] = {0};

//    pData = (*env)->GetByteArrayElements(env, data, NULL);
    (*env)->GetByteArrayRegion(env, data, 0, length, arry);
//    LOGD_FMT(">>>setResponse length[%d]", length);
//    LOGD_FMT(">>>setResponse strlen[%d]", strlen(pData));
    if (commandType == COMMAND_PEDI) {
        ret = NDK_KmlRkiSetPediResponse(arry, length, &nRespLen, &szErrorMsg);
        LOGD_FMT(">>>NDK_KmlRkiSetPediResponse ret[%d]", ret);
    } else if (commandType == COMMAND_PEDK) {
        ret = NDK_KmlRkiSetPedkResponse(arry, length, &nRespLen, &szErrorMsg);
        LOGD_FMT(">>>NDK_KmlRkiSetPedkResponse ret[%d]", ret);
    } else if (commandType == COMMAND_PEDV) {
        ret = NDK_KmlRkiSetPedvResponse(arry, length, &nRespLen, &szErrorMsg);
        LOGD_FMT(">>>NDK_KmlRkiSetPedvResponse ret[%d]", ret);
    }

    if (ret != 0) {
        LOGD_FMT(">>>Set response error message: %s", szErrorMsg);
        if (szErrorMsg != NULL) {
            int errLen = strlen(szErrorMsg);
            if (errLen > 0) {
                (*env)->SetIntArrayRegion(env, err_msg_len, 0, 1, &errLen);
                (*env)->SetByteArrayRegion(env, err_msg, 0, errLen, szErrorMsg);
            }
        }
    }

//    (*env)->ReleaseByteArrayElements(env, data, pData, NULL);

    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1KmlRkiGetPediRequest(JNIEnv *env, jobject thiz,
                                                                          jbyteArray out_info,
                                                                          jintArray out_info_len,
                                                                          jbyteArray err_msg,
                                                                          jintArray err_msg_len) {
    return getRequest(env, out_info, out_info_len, err_msg, err_msg_len, COMMAND_PEDI);
}

jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1KmlRkiSetPediResponse(JNIEnv *env,
                                                                           jobject thiz,
                                                                           jbyteArray data,
                                                                           jint length,
                                                                           jbyteArray err_msg,
                                                                           jintArray err_msg_len) {
    return setResponse(env, data, length, err_msg, err_msg_len, COMMAND_PEDI);
}

jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1KmlRkiGetPedkInitialRequest(JNIEnv *env,
                                                                                 jobject thiz,
                                                                                 jbyteArray out_info,
                                                                                 jintArray out_info_len,
                                                                                 jbyteArray err_msg,
                                                                                 jintArray err_msg_len) {
    return getRequest(env, out_info, out_info_len, err_msg, err_msg_len, COMMAND_PEDK);

}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1KmlRkiSetPedkResponse(JNIEnv *env,
                                                                           jobject thiz,
                                                                           jbyteArray data,
                                                                           jint length,
                                                                           jbyteArray err_msg,
                                                                           jintArray err_msg_len) {
    return setResponse(env, data, length, err_msg, err_msg_len, COMMAND_PEDK);
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1KmlRkiGetPedvRequest(JNIEnv *env, jobject thiz,
                                                                          jbyteArray out_info,
                                                                          jintArray out_info_len,
                                                                          jbyteArray err_msg,
                                                                          jintArray err_msg_len) {
    return getRequest(env, out_info, out_info_len, err_msg, err_msg_len, COMMAND_PEDV);
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1KmlRkiSetPedvResponse(JNIEnv *env,
                                                                           jobject thiz,
                                                                           jbyteArray data,
                                                                           jint length,
                                                                           jbyteArray err_msg,
                                                                           jintArray err_msg_len) {
    return setResponse(env, data, length, err_msg, err_msg_len, COMMAND_PEDV);
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1KmlRkiGetInstallKeyNum(JNIEnv *env,
                                                                            jobject thiz,
                                                                            jintArray out) {
    int outLen;
    int ret = -1;

    ret = NDK_KmlRkiGetInstallKeyNum(&outLen);
    if (ret == 0) {
        (*env)->SetIntArrayRegion(env, out, 0, 1, &outLen);
    }

    LOGD_FMT(">>>NDK_KmlRkiGetInstallKeyNum ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1KmlRkiGetInstalledKeyInfo(JNIEnv *env,
                                                                               jobject thiz,
                                                                               jintArray len,
                                                                               jbyteArray key_info_data) {
    int outLen;
    int i;
    int dataLen;
    ST_RKL_KEY_INFO *keyInfoList = NULL;
    int offset = 0;
    int ret = -1;
    uchar outBuf[512] = {0};

    ret = NDK_KmlRkiGetInstallKeyNum(&outLen);
    LOGD_FMT(">>>NDK_KmlRkiGetInstallKeyNum ret[%d], num[%d]", ret, outLen);
    if (ret != 0) {
        return ret;
    }

    dataLen = outLen * sizeof(ST_RKL_KEY_INFO);
    keyInfoList = (ST_RKL_KEY_INFO *)malloc(dataLen);
    LOGD_FMT(">>>data len[%d]", dataLen);
    ret = NDK_KmlRkiGetInstalledKeyInfo(dataLen, keyInfoList);
    LOGD_FMT(">>>NDK_KmlRkiGetInstalledKeyInfo ret[%d]", ret);
    LOGD_STR(">>>ST_RKL_KEY_INFO: ", keyInfoList, dataLen);
    if (ret != 0) {
        return ret;
    }

    for (i=0; i<outLen; i++){
        outBuf[offset++] = (keyInfoList+i)->index;
        outBuf[offset++] = (keyInfoList+i)->type;
        outBuf[offset++] = (keyInfoList+i)->usage;
        outBuf[offset++] = (keyInfoList+i)->kcvLen;
        memcpy(outBuf+offset, (keyInfoList+i)->kcv, (keyInfoList+i)->kcvLen);
        offset += (keyInfoList+i)->kcvLen;
    }

    (*env)->SetIntArrayRegion(env, len, 0, 1, &offset);
    (*env)->SetByteArrayRegion(env, key_info_data, 0, offset, outBuf);
    LOGD_STR("Installed key info data: ", outBuf, offset);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1KmlRkiSetDeviceSignCertIndex(JNIEnv *env,
                                                                                  jobject thiz,
                                                                                  jbyte index) {
    int ret = -1;
    ret = NDK_KmlRkiSetDeviceSignCertIndex(index);
    LOGD_FMT(">>>NDK_KmlRkiSetDeviceSignCertIndex ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1KmlRkiSetDeviceGroup(JNIEnv *env, jobject thiz,
                                                                          jstring name) {
    char *buf = (*env)->GetStringUTFChars(env, name, 0);
    int ret = -1;
    ret = NDK_KmlRkiSetDeviceGroup(buf);
    (*env)->ReleaseStringUTFChars(env, name, buf);
    LOGD_FMT(">>>NDK_KmlRkiSetDeviceGroup ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1KmlRkiSetWorkDirectory(JNIEnv *env,
                                                                            jobject thiz,
                                                                            jstring directory) {
    char *buf = (*env)->GetStringUTFChars(env, directory, 0);
    int ret = -1;
    ret = NDK_KmlRkiSetWorkDirectory(buf);
    (*env)->ReleaseStringUTFChars(env, directory, buf);
    LOGD_FMT(">>>NDK_KmlRkiSetDeviceGroup ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecECDHEInit(JNIEnv *env, jobject thiz,
                                                                   jlongArray handle) {
    unsigned long *h = NULL;
    if (handle != NULL) {
        h = (unsigned long*) (*env)->GetLongArrayElements(env, handle, NULL);
    }
    int ret = NAPI_SecECDHEInit(h);

    LOGD_FMT(">>>NAPI_SecECDHEInit ret[%d], handle[%d]", ret, *h);

    if (ret == 0) {
        (*env)->SetLongArrayRegion(env, handle, 0, 1, h);
    }
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecECDHERelease(JNIEnv *env, jobject thiz, jlong handle) {
    int ret = NAPI_SecECDHERelease(handle);
    LOGD_FMT(">>>NAPI_SecECDHERelease ret=%d", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecECDHEGenerateKeyPair(JNIEnv *env,
                                                                              jobject thiz,
                                                                              jlong handle,
                                                                              jint curve_type,
                                                                              jbyteArray public_key,
                                                                              jintArray out_data_len) {
    int ret = 0;
    uchar *pDataOut = NULL;
    if (public_key != NULL) {
        pDataOut = (uchar *) (*env)->GetByteArrayElements(env, public_key, NULL);
    }

    int *pDataOutLen= NULL;
    if (out_data_len != NULL) {
        pDataOutLen = (int *) (*env)->GetIntArrayElements(env, out_data_len, NULL);
    }
    ret = NAPI_SecECDHEGenKeyPair(handle, curve_type, pDataOutLen, pDataOut);
    LOGD_FMT(">>>NAPI_SecECDHEGenKeyPair curve type=%d, ret=%d", curve_type, ret);
    if (out_data_len != NULL) {
        (*env)->ReleaseIntArrayElements(env, out_data_len, (jint *) pDataOutLen, NULL);
    }
    if (public_key != NULL) {
        (*env)->ReleaseByteArrayElements(env, public_key, pDataOut, NULL);
    }
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecECDHEGenSK(JNIEnv *env, jobject thiz, jlong handle,
                                                                    jobject key_in_data,
                                                                    jobject hkdf_info,
                                                                    jint public_key_len,
                                                                    jbyteArray public_key) {

    int ret = 0;
    uchar *pSaltData = NULL;
    uchar *pInfoData = NULL;
    uchar *pPublicKey = NULL;

    ST_SEC_ECDHE_KEY_INFO sessionKey;
    memset(&sessionKey, 0, sizeof(ST_SEC_ECDHE_KEY_INFO));
    ST_SEC_ECDHE_KDF_INFO hkdfInfo;
    memset(&hkdfInfo, 0, sizeof(ST_SEC_ECDHE_KDF_INFO));

    jclass sessionKeyCls = (*env)->GetObjectClass(env, key_in_data);
    jclass hkdfInfoCls = (*env)->GetObjectClass(env, hkdf_info);

    sessionKey.ucKeyID = (*env)->GetIntField(env, key_in_data, (*env)->GetFieldID(env, sessionKeyCls, "ucKeyIdx", "I"));
    sessionKey.nKeyLen = (*env)->GetIntField(env, key_in_data, (*env)->GetFieldID(env, sessionKeyCls, "nKeyLen", "I"));
    sessionKey.KeyType = (*env)->GetIntField(env, key_in_data, (*env)->GetFieldID(env, sessionKeyCls, "KeyType", "I"));
    sessionKey.KeyUsage = (*env)->GetIntField(env, key_in_data, (*env)->GetFieldID(env, sessionKeyCls, "KeyUsage", "I"));

    hkdfInfo.KDFType = (*env)->GetIntField(env, hkdf_info, (*env)->GetFieldID(env, hkdfInfoCls, "kdfType", "I"));
    hkdfInfo.MdAlg = (*env)->GetIntField(env, hkdf_info, (*env)->GetFieldID(env, hkdfInfoCls, "mdAlg", "I"));
    hkdfInfo.nSaltLen = (*env)->GetIntField(env, hkdf_info, (*env)->GetFieldID(env, hkdfInfoCls, "saltLen", "I"));
    jbyteArray saltData = (jbyteArray) (*env)->GetObjectField(env, hkdf_info, (*env)->GetFieldID(env, hkdfInfoCls, "salt", "[B"));
    if (saltData != NULL && hkdfInfo.nSaltLen > 0) {
        pSaltData = (uchar *)(*env)->GetByteArrayElements(env, saltData, NULL);
        hkdfInfo.pSalt = pSaltData;
        LOGD_FMT("saltLen[%d] ", hkdfInfo.nSaltLen);
    }
    hkdfInfo.nInfoLen = (*env)->GetIntField(env, hkdf_info, (*env)->GetFieldID(env, hkdfInfoCls, "infoLen", "I"));
    jbyteArray infoData = (jbyteArray) (*env)->GetObjectField(env, hkdf_info, (*env)->GetFieldID(env, hkdfInfoCls, "info", "[B"));
    if (infoData != NULL && hkdfInfo.nInfoLen > 0) {
        pInfoData = (uchar *)(*env)->GetByteArrayElements(env, infoData, NULL);
        hkdfInfo.psInfo = pInfoData;
        LOGD_FMT("infoLen[%d] ", hkdfInfo.nInfoLen);
    }

    if (public_key != NULL) {
        pPublicKey = (uchar *) (*env)->GetByteArrayElements(env, public_key, NULL);
        LOGD_FMT("public key len[%d] ", public_key_len);
    }

    LOGD_FMT(
            ">>>session key id[%d] key len[%d] key type[%d] key usage[%d] kdf type[%d] mdalg[%d]", \
             sessionKey.ucKeyID, sessionKey.nKeyLen, sessionKey.KeyType, sessionKey.KeyUsage, hkdfInfo.KDFType,hkdfInfo.MdAlg);

    ret = NAPI_SecECDHEGenSK(handle, &sessionKey, &hkdfInfo, public_key_len, pPublicKey);
    LOGD_FMT(">>>NAPI_SecECDHEGenSK ret=%d", ret);

    if (saltData != NULL) {
        (*env)->ReleaseByteArrayElements(env, saltData, pSaltData, NULL);
    }

    if (infoData != NULL) {
        (*env)->ReleaseByteArrayElements(env, infoData, pInfoData, NULL);
    }

    if (public_key != NULL) {
        (*env)->ReleaseByteArrayElements(env, public_key, pPublicKey, NULL);
    }

    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecGenerateTR34Random(JNIEnv *env,
                                                                            jobject thiz, jint len,
                                                                            jbyteArray random_data) {
    unsigned char RandomData[len];
    int ret = NAPI_SecTR34GenerateRandom(len, RandomData);
    LOGD_FMT(">>>NAPI_SecGenerateTR34KRDRandom ret = %d", ret)
    if (ret == NAPI_OK) {
        (*env)-> SetByteArrayRegion(env, random_data, 0, len, RandomData);
    }
    return ret;
}


JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecGetSymmKeyNum(JNIEnv *env, jobject thiz,
                                                                       jintArray total_key_num,
                                                                       jobjectArray st_sec_keynum_infos,
                                                                       jintArray key_num_counts) {
    int nTotalKeyNum = 0;
    int nArrayCounts = 0;
    ST_SEC_KEYNUM_INFO stSecKeynumInfos[255];
    jclass keyNumInfoCls = (*env)->FindClass(env, "com/newland/nsdk/core/common/keymanager/ST_SEC_KEYNUM_INFO");

    int ret = NAPI_SecGetSymmKeyNum(&nTotalKeyNum, stSecKeynumInfos, &nArrayCounts);
    LOGD_FMT(">>>NAPI_SecGetSymmKeyNum ret = %d", ret);
    LOGD_FMT("nArrayCounts = %d", nArrayCounts)
    if (ret == 0) {
        (*env)->SetIntArrayRegion(env, total_key_num, 0, 1, &nTotalKeyNum);
        (*env)->SetIntArrayRegion(env, key_num_counts, 0, 1, &nArrayCounts);
        for (int i = 0; i < nArrayCounts; i++) {
            jobject keyNumInfoObject = (*env)->NewObject(env, keyNumInfoCls, (*env)->GetMethodID(env, keyNumInfoCls, "<init>", "()V"));
            (*env)->SetByteField(env, keyNumInfoObject, (*env)->GetFieldID(env, keyNumInfoCls, "keyId", "B"), stSecKeynumInfos[i].ucKeyID);
            (*env)->SetIntField(env, keyNumInfoObject, (*env)->GetFieldID(env, keyNumInfoCls, "keyNum", "I"), stSecKeynumInfos[i].nNum);
            (*env)->SetObjectArrayElement(env, st_sec_keynum_infos, i, keyNumInfoObject);
            (*env)->DeleteLocalRef(env, keyNumInfoObject);
        }
    }
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecGetSymmKeyInfoById(JNIEnv *env,
                                                                            jobject thiz,
                                                                            jbyte key_id,
                                                                            jobjectArray st_sec_symm_keyid_infos,
                                                                            jintArray key_num_counts) {
    int nArrayCounts = 0;
    ST_SEC_SYMM_KEYID_INFO stSecSymmKeyidInfos[255];
    jclass symmKeyInfoCls = (*env)->FindClass(env, "com/newland/nsdk/core/common/keymanager/ST_SEC_SYMM_KEYID_INFO");

    int ret = NAPI_SecGetSymmKeyInfoByID(key_id, stSecSymmKeyidInfos, &nArrayCounts);
    LOGD_FMT(">>>NAPI_SecGetSymmKeyInfoByID ret = %d", ret)
    if (ret == 0) {
        (*env)->SetIntArrayRegion(env, key_num_counts, 0, 1, &nArrayCounts);
        for (int i = 0; i < nArrayCounts; i++) {
            jobject symmKeyInfoObject = (*env)->NewObject(env, symmKeyInfoCls, (*env)->GetMethodID(env, symmKeyInfoCls, "<init>", "()V"));
            (*env)->SetIntField(env, symmKeyInfoObject, (*env)->GetFieldID(env, symmKeyInfoCls, "keyUsage", "I"), (jint) stSecSymmKeyidInfos[i].KeyUsage);
            (*env)->SetIntField(env, symmKeyInfoObject, (*env)->GetFieldID(env, symmKeyInfoCls, "keyType", "I"), (jint) stSecSymmKeyidInfos[i].KeyType);
            jbyteArray checkBuf = (jbyteArray) (*env)->GetObjectField(env, symmKeyInfoObject, (*env)->GetFieldID(env, symmKeyInfoCls, "sCheckBuf", "[B"));
            (*env)->SetByteArrayRegion(env, checkBuf, 0, stSecSymmKeyidInfos[i].nKcvLen, stSecSymmKeyidInfos[i].sKcvBuf);
            (*env)->DeleteLocalRef(env, checkBuf);
            (*env)->SetIntField(env, symmKeyInfoObject, (*env)->GetFieldID(env, symmKeyInfoCls, "checkLen", "I"), stSecSymmKeyidInfos[i].nKcvLen);
            (*env)->SetObjectArrayElement(env, st_sec_symm_keyid_infos, i, symmKeyInfoObject);
            (*env)->DeleteLocalRef(env, symmKeyInfoObject);
        }
    }
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecTR34ProcessKeyBlock(JNIEnv *env,
                                                                             jobject thiz,
                                                                             jint encoding_mode,
                                                                             jobject asym_keyin_data,
                                                                             jstring tr34data) {
    ST_SEC_TR34_BLOCK_PARAMS  stSecTr34BlockParams;
    memset(&stSecTr34BlockParams, 0, sizeof(ST_SEC_TR34_BLOCK_PARAMS));
    stSecTr34BlockParams.keyBlockMode = encoding_mode;

    ST_SEC_TR34_KEY_INFO stSecTr34KeyInfo;
    memset(&stSecTr34KeyInfo, 0, sizeof(ST_SEC_TR34_KEY_INFO));
    jclass asymKeyCls = (*env)->GetObjectClass(env, asym_keyin_data);
    stSecTr34KeyInfo.asymKeyIdx = (*env)->GetIntField(env, asym_keyin_data, (*env)->GetFieldID(env, asymKeyCls,"ucKEKIdx", "I"));
    stSecTr34KeyInfo.knIdx = (*env)->GetIntField(env, asym_keyin_data, (*env)-> GetFieldID(env, asymKeyCls, "ucKeyIdx", "I"));
    stSecTr34KeyInfo.keyType = (*env)->GetIntField(env, asym_keyin_data, (*env)->GetFieldID(env, asymKeyCls, "KeyType", "I"));
    stSecTr34KeyInfo.keyUsage = (*env)->GetIntField(env, asym_keyin_data, (*env)->GetFieldID(env, asymKeyCls, "KeyUsage", "I"));

    unsigned char * pKeyData;
    jclass strCls = (*env)->FindClass(env, "java/lang/String");
    jstring encode = (*env)->NewStringUTF(env, "ASCII");
    jmethodID mid = (*env)->GetMethodID(env, strCls, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray keyData = (*env)->CallObjectMethod(env, tr34data, mid, encode);
    jsize size = (*env)->GetArrayLength(env, keyData);
    jbyte *b = (*env)->GetByteArrayElements(env, keyData, 0);
    if (size > 0) {
        pKeyData = (unsigned char *)malloc(size);
        memcpy(pKeyData, b, size);
    }

    int keyDataLen = (*env)->GetIntField(env, asym_keyin_data, (*env)->GetFieldID(env, asymKeyCls, "nKeyLen", "I"));
    stSecTr34BlockParams.keyBlock = pKeyData;
    stSecTr34BlockParams.keyBlockLen = keyDataLen;
    LOGD_FMT("asymKeyIdx[%d], knIdx[%d], knKeyType[%d], knKeyUsage[%d], TR34_EncodingMode[%d], TR34KeyBlockLen[%d]",
             stSecTr34KeyInfo.asymKeyIdx, stSecTr34KeyInfo.knIdx, stSecTr34KeyInfo.keyType, stSecTr34KeyInfo.keyUsage, stSecTr34BlockParams.keyBlockMode, stSecTr34BlockParams.keyBlockLen)
    int ret = NAPI_SecTR34ProcessKeyBlock(&stSecTr34BlockParams, &stSecTr34KeyInfo, NULL, 0);
    LOGD_FMT(">>>NAPI_SecTR34ProcessKeyBlock ret = %d", ret)
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecGenerateAsymKey(JNIEnv *env, jobject thiz,
                                                                         jlongArray handle,
                                                                         jobject st_sec_asym_keyin_data, jobject st_sec_asym_alg_info) {
    unsigned long* h = NULL;
    if (handle != NULL) {
        h = (unsigned long*)(*env)->GetLongArrayElements(env, handle, NULL);
    }

    ST_SEC_ASYM_KEY_INFO stSecAsymKeyInfo;
    memset(&stSecAsymKeyInfo, 0, sizeof(ST_SEC_ASYM_KEY_INFO));
    jclass asymKeyInDataCls = (*env)->GetObjectClass(env, st_sec_asym_keyin_data);
    stSecAsymKeyInfo.KeyUsage = (*env)->GetIntField(env, st_sec_asym_keyin_data, (*env)->GetFieldID(env, asymKeyInDataCls, "KeyUsage", "I"));
    stSecAsymKeyInfo.KeytType = (*env)->GetIntField(env, st_sec_asym_keyin_data, (*env)->GetFieldID(env, asymKeyInDataCls, "KeyType", "I"));
    stSecAsymKeyInfo.KeyIdx = (*env)->GetIntField(env, st_sec_asym_keyin_data, (*env)->GetFieldID(env, asymKeyInDataCls, "ucKeyIdx", "I"));

    ST_SEC_ASYM_ALG_INFO stSecAsymAlgInfo;
    memset(&stSecAsymAlgInfo, 0, sizeof(ST_SEC_ASYM_ALG_INFO));
    jclass asymAlgInfoCls = (*env)->GetObjectClass(env, st_sec_asym_alg_info);
    stSecAsymAlgInfo.unBit = (*env)->GetIntField(env, st_sec_asym_alg_info, (*env)->GetFieldID(env, asymAlgInfoCls, "unBit", "I"));
    jbyteArray rsaPubExp = (*env)->GetObjectField(env, st_sec_asym_alg_info, (*env)->GetFieldID(env, asymAlgInfoCls, "ucRSAPubExp", "[B"));
    if (stSecAsymAlgInfo.unBit > 0 && rsaPubExp != NULL) {
        unsigned char *RSAPubExp = (*env)->GetByteArrayElements(env, rsaPubExp, NULL);
        memcpy(stSecAsymAlgInfo.ucRsaPubExp, RSAPubExp, 5);
    }
    int ret = 0;
    LOGD_FMT("keyUsage[%d], keyType[%d], keyID[%d]", stSecAsymKeyInfo.KeyUsage, stSecAsymKeyInfo.KeytType, stSecAsymKeyInfo.KeyIdx);
    ret = NAPI_SecGenerateAsymKey(h, &stSecAsymKeyInfo, sizeof(ST_SEC_ASYM_ALG_INFO), &stSecAsymAlgInfo);
    LOGD_FMT(">>>NAPI_SecGenerateAsymKey ret = %d", ret);
    if (ret == 0) {
        (*env)->SetLongArrayRegion(env, handle, 0, 1, h);
    }
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecGenerateAsymKeyState(JNIEnv *env,
                                                                              jobject thiz,
                                                                              jlong handle) {
    int ret = NAPI_SecGenerateAsymKeyState(handle);
    LOGD_FMT(">>>NAPI_SecGenerateAsymKeyState ret = %d", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecCancelGenerateAsymKey(JNIEnv *env,
                                                                               jobject thiz,
                                                                               jlong handle) {
    int ret = NAPI_SecCancelGenerateAsymKey(handle);
    LOGD_FMT(">>>NAPI_SecCancelGenerateAsymKey ret = %d", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecTR34ProcessKeyBlockRevolut(JNIEnv *env,
                                                                                    jobject thiz,
                                                                                    jint encoding_mode,
                                                                                    jobject asym_keyin_data,
                                                                                    jbyteArray tr34data) {
    ST_SEC_TR34_BLOCK_PARAMS  stSecTr34BlockParams;
    memset(&stSecTr34BlockParams, 0, sizeof(ST_SEC_TR34_BLOCK_PARAMS));
    stSecTr34BlockParams.keyBlockMode = encoding_mode;

    ST_SEC_TR34_KEY_INFO stSecTr34KeyInfo;
    memset(&stSecTr34KeyInfo, 0, sizeof(ST_SEC_TR34_KEY_INFO));
    jclass asymKeyCls = (*env)->GetObjectClass(env, asym_keyin_data);
    stSecTr34KeyInfo.asymKeyIdx = (*env)->GetIntField(env, asym_keyin_data, (*env)->GetFieldID(env, asymKeyCls,"ucKEKIdx", "I"));
    stSecTr34KeyInfo.knIdx = (*env)->GetIntField(env, asym_keyin_data, (*env)-> GetFieldID(env, asymKeyCls, "ucKeyIdx", "I"));
    stSecTr34KeyInfo.keyType = (*env)->GetIntField(env, asym_keyin_data, (*env)->GetFieldID(env, asymKeyCls, "KeyType", "I"));
    stSecTr34KeyInfo.keyUsage = (*env)->GetIntField(env, asym_keyin_data, (*env)->GetFieldID(env, asymKeyCls, "KeyUsage", "I"));

    unsigned char * pKeyData = (*env)->GetByteArrayElements(env, tr34data, NULL);


    int keyDataLen = (*env)->GetIntField(env, asym_keyin_data, (*env)->GetFieldID(env, asymKeyCls, "nKeyLen", "I"));
    stSecTr34BlockParams.keyBlock = pKeyData;
    stSecTr34BlockParams.keyBlockLen = keyDataLen;
    LOGD_FMT("asymKeyIdx[%d], knIdx[%d], knKeyType[%d], knKeyUsage[%d], TR34_EncodingMode[%d], TR34KeyBlockLen[%d]",
             stSecTr34KeyInfo.asymKeyIdx, stSecTr34KeyInfo.knIdx, stSecTr34KeyInfo.keyType, stSecTr34KeyInfo.keyUsage, stSecTr34BlockParams.keyBlockMode, stSecTr34BlockParams.keyBlockLen)
    int ret = NAPI_SecTR34ProcessKeyBlock(&stSecTr34BlockParams, &stSecTr34KeyInfo, NULL, 0);
    LOGD_FMT(">>>NAPI_SecTR34ProcessKeyBlock ret = %d", ret)
    (*env)->ReleaseByteArrayElements(env, tr34data, pKeyData, 0);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecVppRNIBTpInit(JNIEnv *env, jobject thiz,
                                                                       jintArray coordination,
                                                                       jintArray area_coordination,
                                                                       jintArray key_pad_coordination,
                                                                       jint key_number) {
    uint8_t numserial[13] = {0x31,0x32,0x33,0x34,0x35,0x36,0x37,0x38,0x39,0x30,0x1B,0x0A,0x0D};
    uint8_t numserial2[12] = {0x31,0x32,0x33,0x34,0x35,0x36,0x37,0x38,0x39,0x30,0x1B,0x0D};
    uint8_t numserial3[15] = {0x31,0x32,0x33,0x1B,0x34,0x35,0x36,0x0A,0x37,0x38,0x39,0x0D,0x00,0x30,0x00};
    vpp_key vppKeys[20];
    memset(vppKeys, 0, sizeof(vppKeys));
    int offset = 0;

    int *buttonCoordinations = (*env)->GetIntArrayElements(env, coordination, NULL);
    if (key_number == 13) {
        for (int i = 0; i < 13; i++) {
            LOGD_FMT("i = %d", i);
            vppKeys[i].key = numserial[i];
            vppKeys[i].btn.l_top.x = *(buttonCoordinations + offset);
            offset++;
            vppKeys[i].btn.l_top.y = *(buttonCoordinations + offset);
            offset++;
            vppKeys[i].btn.r_bottom.x = *(buttonCoordinations + offset);
            offset++;
            vppKeys[i].btn.r_bottom.y = *(buttonCoordinations + offset);
            offset++;
            LOGD_FMT("vppKeys[%d].btn.l_top.x[%d], vppKeys[%d].btn.l_top.y[%d], vppKeys[%d].btn.r_bottom.x[%d], vppKeys[%d].btn.r_bottom.x[%d], vppKeys[%d].key[%d]",
                     i, vppKeys[i].btn.l_top.x, i, vppKeys[i].btn.l_top.y, i, vppKeys[i].btn.r_bottom.x, i, vppKeys[i].btn.r_bottom.y, i, vppKeys[i].key);
        }
    } else if (key_number == 12){
        for (int i = 0; i < 12; i++) {
            LOGD_FMT("i = %d", i);
            vppKeys[i].key = numserial2[i];
            vppKeys[i].btn.l_top.x = *(buttonCoordinations + offset);
            offset++;
            vppKeys[i].btn.l_top.y = *(buttonCoordinations + offset);
            offset++;
            vppKeys[i].btn.r_bottom.x = *(buttonCoordinations + offset);
            offset++;
            vppKeys[i].btn.r_bottom.y = *(buttonCoordinations + offset);
            offset++;
            LOGD_FMT("vppKeys[%d].btn.l_top.x[%d], vppKeys[%d].btn.l_top.y[%d], vppKeys[%d].btn.r_bottom.x[%d], vppKeys[%d].btn.r_bottom.x[%d], vppKeys[%d].key[%d]",
                     i, vppKeys[i].btn.l_top.x, i, vppKeys[i].btn.l_top.y, i, vppKeys[i].btn.r_bottom.x, i, vppKeys[i].btn.r_bottom.y, i, vppKeys[i].key);
        }
    } else if (key_number == 15) {
        for (int i = 0; i < 15; i++) {
            LOGD_FMT("i = %d", i);
            vppKeys[i].key = numserial3[i];
            vppKeys[i].btn.l_top.x = *(buttonCoordinations + offset);
            offset++;
            vppKeys[i].btn.l_top.y = *(buttonCoordinations + offset);
            offset++;
            vppKeys[i].btn.r_bottom.x = *(buttonCoordinations + offset);
            offset++;
            vppKeys[i].btn.r_bottom.y = *(buttonCoordinations + offset);
            offset++;
            LOGD_FMT("vppKeys.key[%02x], vppKeys[%d].btn.l_top.x[%d], vppKeys[%d].btn.l_top.y[%d], vppKeys[%d].btn.r_bottom.x[%d], vppKeys[%d].btn.r_bottom.x[%d], vppKeys[%d].key[%d]", vppKeys[i].key,
                     i, vppKeys[i].btn.l_top.x, i, vppKeys[i].btn.l_top.y, i, vppKeys[i].btn.r_bottom.x, i, vppKeys[i].btn.r_bottom.y, i, vppKeys[i].key);
        }
    }


    offset = 0;
    vpp_button tsArea;
    memset(&tsArea, 0, sizeof(tsArea));
    int *areaCoordination = (*env)->GetIntArrayElements(env, area_coordination, NULL);
    tsArea.l_top.x = *(areaCoordination + offset);
    offset++;
    tsArea.l_top.y = *(areaCoordination + offset);
    offset++;
    tsArea.r_bottom.x = *(areaCoordination + offset);
    offset++;
    tsArea.r_bottom.y = *(areaCoordination + offset);
    LOGD_FMT("tsArea, l_top.x[%d], l_top.y[%d], r_bottom.x[%d], r_bottom.y[%d]", tsArea.l_top.x, tsArea.l_top.y, tsArea.r_bottom.x, tsArea.r_bottom.y);
    offset = 0;

    vpp_button tsKeypad;
    memset(&tsKeypad, 0, sizeof(tsKeypad));
    int *keyPadCoordination = (*env)->GetIntArrayElements(env, key_pad_coordination, NULL);
    tsKeypad.l_top.x = *(keyPadCoordination + offset);
    offset++;
    tsKeypad.l_top.y = *(keyPadCoordination + offset);
    offset++;
    tsKeypad.r_bottom.x = *(keyPadCoordination + offset);
    offset++;
    tsKeypad.r_bottom.y = *(keyPadCoordination + offset);
    offset = 0;
    LOGD_FMT("tsKeypad, l_top.x[%d], l_top.y[%d], r_bottom.x[%d], r_bottom.y[%d]", tsKeypad.l_top.x, tsKeypad.l_top.y, tsKeypad.r_bottom.x, tsKeypad.r_bottom.y);

    int ret = NAPI_SecVppRNIBTpInit(vppKeys, key_number, &tsArea, &tsKeypad);
    LOGD_FMT(">>> NAPI_SecVppRNIBTpInit ret = %d", ret);
    (*env)->ReleaseIntArrayElements(env, coordination, buttonCoordinations, 0);
    (*env)->ReleaseIntArrayElements(env, area_coordination, areaCoordination, 0);
    (*env)->ReleaseIntArrayElements(env, key_pad_coordination, keyPadCoordination, 0);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecCSRInit(JNIEnv *env, jobject thiz) {
    CSR_HANDLE h;

    int ret = NAPI_SecCSRInit(&h);

    LOGD_FMT(">>>NAPI_SecCSRInit ret[%d], handle[%p]", ret, h);
    gHandle = h;

    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecCSRSetExtension(JNIEnv *env, jobject thiz,
                                                                         jbyteArray oid,
                                                                         jint oid_len,
                                                                         jbyteArray value,
                                                                         jint value_len) {
    if (gHandle == NULL) {
        return -11;
    }

    unsigned char *baOid = (*env)->GetByteArrayElements(env, oid, NULL);
    unsigned char *baValue = (*env)->GetByteArrayElements(env, value, NULL);

    int ret = NAPI_SecCSRSetExtension(gHandle, baOid, oid_len, baValue, value_len);
    if (ret != NDK_OK) {
        NAPI_SecCSRRelease(gHandle);
        gHandle = NULL;
    }
    LOGD_FMT(">>>NAPI_SecCSRSetExtension ret[%d]", ret);
    (*env)->ReleaseByteArrayElements(env, oid, baOid, 0);
    (*env)->ReleaseByteArrayElements(env, value, baValue, 0);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecCSRGen(JNIEnv *env, jobject thiz,
                                                                jint type,
                                                                jbyteArray data,
                                                                jintArray data_len) {
    if (gHandle == NULL) {
        return -11;
    }
    unsigned char baData[4096] = {0};
    int dataLen = 0;

    int ret;
    if (type == 0) {
        ret = NAPI_SecCSRGenPem(gHandle, &dataLen, baData);
        LOGD_FMT(">>>NAPI_SecCSRGenPem ret[%d] outLen[%d]", ret, dataLen);
    } else {
        ret = NAPI_SecCSRGenDer(gHandle, &dataLen, baData);
        LOGD_FMT(">>>NAPI_SecCSRGenDer ret[%d] outLen[%d]", ret, dataLen);
    }

    if (ret == 0) {
        (*env)->SetIntArrayRegion(env, data_len, 0, 1, &dataLen);
        (*env)->SetByteArrayRegion(env, data, 0, dataLen, baData);
    }
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecCSRRelease(JNIEnv *env, jobject thiz) {
    if (gHandle == NULL) {
        return -11;
    }

    int ret = NAPI_SecCSRRelease(gHandle);
    gHandle = NULL;

    LOGD_FMT(">>>NAPI_SecCSRRelease ret[%d]", ret);

    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecCSRSetParameters(JNIEnv *env, jobject thiz,
                                                                          jobject asym_keyin_data,
                                                                          jbyte cert_type,
                                                                          jboolean is_ca,
                                                                          jstring user_name) {
    if (gHandle == NULL) {
        return -11;
    }
    jclass asymKeyInDataCls = (*env)->GetObjectClass(env, asym_keyin_data);
    jfieldID fid_keyId = (*env)->GetFieldID(env, asymKeyInDataCls, "ucKeyIdx", "I");
    jfieldID fid_keyUsage = (*env)->GetFieldID(env, asymKeyInDataCls, "KeyUsage", "I");
    jfieldID fid_keyType = (*env)->GetFieldID(env, asymKeyInDataCls, "KeyType", "I");
    jfieldID fid_mdAlg = (*env)->GetFieldID(env, asymKeyInDataCls, "MdAlg", "I");
    jfieldID fid_csrKeyUsage = (*env)->GetFieldID(env, asymKeyInDataCls, "KEKUsage", "I");

    //Set Subject Name
    unsigned char *userName = (*env)->GetStringUTFChars(env, user_name, NULL);
    int ret = NAPI_SecCSRSetSubjectName(gHandle, userName);
    LOGD_FMT(">>> NAPI_SecCSRSetSubjectName ret[%d]", ret);
    if (ret != 0) {
        NAPI_SecCSRRelease(gHandle);
        gHandle = NULL;
        return ret;
    }
    //Set Key
    int keyId = (*env)->GetIntField(env, asym_keyin_data, fid_keyId);
    int keyType = (*env)->GetIntField(env, asym_keyin_data, fid_keyType);
    int keyUsage = (*env)->GetIntField(env, asym_keyin_data, fid_keyUsage);
    ST_SEC_ASYM_KEY_INFO asymKeyInfo;
    memset(&asymKeyInfo, 0x00, sizeof(ST_SEC_ASYM_KEY_INFO));
    asymKeyInfo.KeyIdx = keyId;
    asymKeyInfo.KeytType = keyType;
    asymKeyInfo.KeyUsage = keyUsage;

    ret = NAPI_SecCSRSetKey(gHandle, &asymKeyInfo);
    LOGD_FMT(">>> NAPI_SecCSRSetKey ret[%d]", ret);
    if (ret != 0) {
        NAPI_SecCSRRelease(gHandle);
        gHandle = NULL;
        return ret;
    }
    //Set NS Cert Type
    ret = NAPI_SecCSRSetNSCertType(gHandle, cert_type);
    LOGD_FMT(">>> NAPI_SecCSRSetNSCertType ret[%d]", ret);
    if (ret != 0) {
        NAPI_SecCSRRelease(gHandle);
        gHandle = NULL;
        return ret;
    }
    //Set isCA
    ret = NAPI_SecCSRSetIsCA(gHandle, is_ca);
    LOGD_FMT(">>> NAPI_SecCSRSetIsCA ret[%d]", ret);
    if (ret != 0) {
        NAPI_SecCSRRelease(gHandle);
        gHandle = NULL;
        return ret;
    }
    //Set Md Alg
    int mdType = (*env)->GetIntField(env, asym_keyin_data, fid_mdAlg);
    ret = NAPI_SecCSRSetMdAlg(gHandle, mdType);
    LOGD_FMT(">>> NAPI_SecCSRSetMdAlg ret[%d]", ret);
    if (ret != 0) {
        NAPI_SecCSRRelease(gHandle);
        gHandle = NULL;
        return ret;
    }
    //Set KeyUsage
    unsigned int csrKeyUsage = (*env)->GetIntField(env, asym_keyin_data, fid_csrKeyUsage);
    ret = NAPI_SecCSRSetKeyUsage(gHandle, csrKeyUsage);
    LOGD_FMT(">>> NAPI_SecCSRSetKeyUsage ret[%d]", ret);
    if (ret != 0) {
        NAPI_SecCSRRelease(gHandle);
        gHandle = NULL;
        return ret;
    }
    return 0;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_generateKeyWithSymmKey(JNIEnv *env, jobject thiz,
                                                                       jint method,
                                                                       jobject keyDataObj,
                                                                       jobject kcvDataObj,
                                                                       jbyteArray out_data,
                                                                       jintArray out_data_len) {
    jclass keyDataCls = (*env)->GetObjectClass(env, keyDataObj);
    if (keyDataCls == NULL) {
        LOGD_FMT(">>>keyDataCls[%d]", keyDataCls);
        return NDK_ERR_PARA;
    }
    ST_SEC_KEYIN_DATA stDataIn;
    memset(&stDataIn, 0, sizeof(ST_SEC_KEYIN_DATA));

    stDataIn.ucKEKIdx = (*env)->GetIntField(env, keyDataObj,
                                            (*env)->GetFieldID(env, keyDataCls, "ucKEKIdx", "I"));
    stDataIn.KEKType = (*env)->GetIntField(env, keyDataObj,
                                           (*env)->GetFieldID(env, keyDataCls, "KEKType", "I"));
    stDataIn.KEKUsage = (*env)->GetIntField(env, keyDataObj,
                                            (*env)->GetFieldID(env, keyDataCls, "KEKUsage", "I"));

    stDataIn.ucKeyIdx = (*env)->GetIntField(env, keyDataObj,
                                            (*env)->GetFieldID(env, keyDataCls, "ucKeyIdx", "I"));
    stDataIn.KeyType = (*env)->GetIntField(env, keyDataObj,
                                           (*env)->GetFieldID(env, keyDataCls, "KeyType", "I"));
    stDataIn.KeyUsage = (*env)->GetIntField(env, keyDataObj,
                                            (*env)->GetFieldID(env, keyDataCls, "KeyUsage", "I"));

    stDataIn.CipherMode = (*env)->GetIntField(env, keyDataObj,
                                              (*env)->GetFieldID(env, keyDataCls, "CipherMode",
                                                                 "I"));
    stDataIn.PadingMode = (*env)->GetIntField(env, keyDataObj,
                                              (*env)->GetFieldID(env, keyDataCls, "PaddingMode",
                                                                 "I"));

    stDataIn.nKeyLen = (*env)->GetIntField(env, keyDataObj,
                                           (*env)->GetFieldID(env, keyDataCls, "nKeyLen", "I"));
    stDataIn.nKeyDataLen = (*env)->GetIntField(env, keyDataObj,
                                               (*env)->GetFieldID(env, keyDataCls, "nKeyDataLen",
                                                                  "I"));

    uchar *pkeyData;
    jbyteArray keyData = (jbyteArray) (*env)->GetObjectField(env, keyDataObj,
                                                             (*env)->GetFieldID(env, keyDataCls,
                                                                                "pKeyData", "[B"));
    if (keyData != NULL && stDataIn.nKeyDataLen > 0) {
        pkeyData = (*env)->GetByteArrayElements(env, keyData, NULL);
        stDataIn.pKeyData = pkeyData;
    }
    if (method == SEC_KIM_RANDOM_OUT) {
        pkeyData = (*env)->GetByteArrayElements(env, out_data, NULL);
        stDataIn.pKeyData = pkeyData;
        int pKeyDataLen = (*env)->GetArrayLength(env, out_data);
        stDataIn.pAD = &pKeyDataLen;
    }
    uchar *pIV;
    jbyteArray IV = (jbyteArray) (*env)->GetObjectField(env, keyDataObj,
                                                        (*env)->GetFieldID(env, keyDataCls, "psIV",
                                                                           "[B"));
    if (IV != NULL) {
        pIV = (*env)->GetByteArrayElements(env, IV, NULL);
        stDataIn.psIV = pIV;
        LOGD_STR("IV", pIV, (*env)->GetArrayLength(env, IV));
    }

    stDataIn.nKsnLen = (*env)->GetIntField(env, keyDataObj,
                                           (*env)->GetFieldID(env, keyDataCls, "nKsnLen", "I"));

    uchar *pksnData;
    jbyteArray ksnData = (jbyteArray) (*env)->GetObjectField(env, keyDataObj,
                                                             (*env)->GetFieldID(env, keyDataCls,
                                                                                "psKsn", "[B"));
    if (ksnData != NULL && stDataIn.nKsnLen > 0) {
        pksnData = (*env)->GetByteArrayElements(env, ksnData, NULL);
        stDataIn.psKsn = pksnData;
        LOGD_STR("ksnData", pksnData, stDataIn.nKsnLen);
    }

    uchar *pAD;
    stDataIn.nADSize = (*env)->GetIntField(env, keyDataObj,
                                           (*env)->GetFieldID(env, keyDataCls, "nADSize", "I"));
    jbyteArray AD = (jbyteArray) (*env)->GetObjectField(env, keyDataObj,
                                                        (*env)->GetFieldID(env, keyDataCls, "pAD",
                                                                           "[B"));
    if (AD != NULL && stDataIn.nADSize > 0) {
        pAD = (*env)->GetByteArrayElements(env, AD, NULL);
        stDataIn.pAD = pAD;
    }

    LOGD_FMT(
            ">>>method[%d] ucKEKIdx[%d] KEKType[%d] KEKUsage[%d] ucKeyIdx[%d] KeyType[%d] KeyUsage[%d] CipherMode[%d] PadingMode[%d] nKeyLen[%d] nKeyDataLen[%d]", \
             method, stDataIn.ucKEKIdx, stDataIn.KEKType, stDataIn.KEKUsage, stDataIn.ucKeyIdx,
            stDataIn.KeyType, stDataIn.KeyUsage, \
             stDataIn.CipherMode, stDataIn.PadingMode, stDataIn.nKeyLen, stDataIn.nKeyDataLen);

    jclass kcvDataCls = (*env)->GetObjectClass(env, kcvDataObj);
    if (kcvDataCls == NULL) {
        LOGD_FMT(">>>kcvDataCls[%d]", kcvDataCls);
        return NDK_ERR_PARA;
    }
    ST_SEC_KCV_DATA stKcvData;
    memset(&stKcvData, 0, sizeof(ST_SEC_KCV_DATA));

    stKcvData.nCheckMode = (*env)->GetIntField(env, kcvDataObj,
                                               (*env)->GetFieldID(env, kcvDataCls, "nCheckMode",
                                                                  "I"));
    stKcvData.nLen = (*env)->GetIntField(env, kcvDataObj,
                                         (*env)->GetFieldID(env, kcvDataCls, "nLen", "I"));
    if (stKcvData.nLen > 8) {
        LOGD_FMT(">>>nLen[%d]", stKcvData.nLen);
        return NDK_ERR_PARA;
    }
    jbyteArray sCheckBuf = (jbyteArray) (*env)->GetObjectField(env, kcvDataObj,
                                                               (*env)->GetFieldID(env, kcvDataCls,
                                                                                  "sCheckBuf",
                                                                                  "[B"));
    LOGD_FMT(">>>nCheckMode[%d] nLen[%d] sCheckBuf[%d]", stKcvData.nCheckMode, stKcvData.nLen,
             sCheckBuf);
    uchar *pCheckBuf;
    if (sCheckBuf != NULL) {
        pCheckBuf = (*env)->GetByteArrayElements(env, sCheckBuf, NULL);
        memcpy(stKcvData.sCheckBuf, pCheckBuf, stKcvData.nLen);
        LOGD_STR("kcvValue", pCheckBuf, stKcvData.nLen);
    }

    int ret = NAPI_SecGenerateKey(method, &stDataIn, &stKcvData);

    if (method == SEC_KIM_RANDOM_OUT && ret == NAPI_OK && *stDataIn.pAD > 0) {
        int keyDataLen = stDataIn.nKeyDataLen;
        (*env)->SetIntArrayRegion(env, out_data_len, 0, 1, stDataIn.pAD);
        (*env)->SetByteArrayRegion(env, out_data, 0, keyDataLen, stDataIn.pKeyData);
        (*env)->ReleaseByteArrayElements(env, out_data, pkeyData, 0);
    }

    if (keyData != NULL)
        (*env)->ReleaseByteArrayElements(env, keyData, pkeyData, NULL);

    if (IV != NULL)
        (*env)->ReleaseByteArrayElements(env, IV, pIV, NULL);

    if (ksnData != NULL)
        (*env)->ReleaseByteArrayElements(env, ksnData, pksnData, NULL);

    if (AD != NULL)
        (*env)->ReleaseByteArrayElements(env, AD, pAD, NULL);

    if (sCheckBuf != NULL)
        (*env)->ReleaseByteArrayElements(env, sCheckBuf, pCheckBuf, NULL);

    LOGD_FMT(">>>NAPI_SecGenerateKey ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecVppSetButtonFunc(JNIEnv *env, jobject thiz,
                                                                          jint button,
                                                                          jint func_type) {
    int ret = NAPI_SecVPPSetButtonFunc(button, func_type);
    LOGD_FMT(">>> NAPI_SecVppSetButtonFunc ret[%d]", ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecTR34ProcessKeyBlockWithPad(JNIEnv *env,
                                                                                    jobject thiz,
                                                                                    jint encoding_mode,
                                                                                    jobject asym_keyin_data,
                                                                                    jbyteArray p_ad,
                                                                                    jintArray p_adlen) {
    ST_SEC_TR34_BLOCK_PARAMS  stSecTr34BlockParams;
    memset(&stSecTr34BlockParams, 0, sizeof(ST_SEC_TR34_BLOCK_PARAMS));
    stSecTr34BlockParams.keyBlockMode = encoding_mode;

    ST_SEC_TR34_KEY_INFO stSecTr34KeyInfo;
    memset(&stSecTr34KeyInfo, 0, sizeof(ST_SEC_TR34_KEY_INFO));
    jclass asymKeyCls = (*env)->GetObjectClass(env, asym_keyin_data);
    stSecTr34KeyInfo.asymKeyIdx = (*env)->GetIntField(env, asym_keyin_data, (*env)->GetFieldID(env, asymKeyCls,"ucKEKIdx", "I"));
    stSecTr34KeyInfo.knIdx = (*env)->GetIntField(env, asym_keyin_data, (*env)-> GetFieldID(env, asymKeyCls, "ucKeyIdx", "I"));
    stSecTr34KeyInfo.keyType = (*env)->GetIntField(env, asym_keyin_data, (*env)->GetFieldID(env, asymKeyCls, "KeyType", "I"));
    stSecTr34KeyInfo.keyUsage = (*env)->GetIntField(env, asym_keyin_data, (*env)->GetFieldID(env, asymKeyCls, "KeyUsage", "I"));

    jbyteArray ba_keyBlock= (*env)->GetObjectField(env, asym_keyin_data, (*env)->GetFieldID(env, asymKeyCls, "pKeyData", "[B"));
    uchar *keyblock = (*env)->GetByteArrayElements(env, ba_keyBlock, NULL);
    int keyDataLen = (*env)->GetIntField(env, asym_keyin_data, (*env)->GetFieldID(env, asymKeyCls, "nKeyLen", "I"));
    stSecTr34BlockParams.keyBlock = keyblock;
    stSecTr34BlockParams.keyBlockLen = keyDataLen;
    LOGD_FMT("asymKeyIdx[%d], knIdx[%d], knKeyType[%d], knKeyUsage[%d], TR34_EncodingMode[%d], TR34KeyBlockLen[%d]",
             stSecTr34KeyInfo.asymKeyIdx, stSecTr34KeyInfo.knIdx, stSecTr34KeyInfo.keyType, stSecTr34KeyInfo.keyUsage, stSecTr34BlockParams.keyBlockMode, stSecTr34BlockParams.keyBlockLen);
    int ret = 0;
    if (encoding_mode == TR34_BLOCK_ENCODING_RAW3) {
        unsigned char pad[1024] = {0};
        ret = NAPI_SecTR34ProcessKeyBlock(&stSecTr34BlockParams, &stSecTr34KeyInfo, pad, 1024);
        if (ret == 0) {
            int padLen = strlen(pad);
            (*env)->SetIntArrayRegion(env, p_adlen, 0, 1, &padLen);
            (*env)->SetByteArrayRegion(env, p_ad, 0, padLen, pad);
        }
    } else {
        if (p_ad != NULL) {
            uchar *pAD = (*env)->GetByteArrayElements(env, p_ad, NULL);
            int pADLen = (*env)->GetArrayLength(env, p_ad);
            ret = NAPI_SecTR34ProcessKeyBlock(&stSecTr34BlockParams, &stSecTr34KeyInfo, pAD, pADLen);
            (*env)->ReleaseByteArrayElements(env, p_ad, pAD, 0);
        } else {
            ret = NAPI_SecTR34ProcessKeyBlock(&stSecTr34BlockParams, &stSecTr34KeyInfo, NULL, 0);
        }
    }
    (*env)->ReleaseByteArrayElements(env, ba_keyBlock, keyblock, 0);
    LOGD_FMT(">>>NAPI_SecTR34ProcessKeyBlock ret = %d", ret)
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecGeneratePubKeyCert(JNIEnv *env,
                                                                            jobject thiz,
                                                                            jobject ca_key,
                                                                            jobject dst_key,
                                                                            jbyteArray cipher_cert, jintArray cipher_cert_len) {
    uchar outInfo[4096] = {0};
    int outInfoLen = 0;
    jclass stSecAsymKeyInfoCls = (*env)->FindClass(env, "com/newland/nsdk/core/common/keymanager/ST_SEC_ASYM_KEY_INFO");

    ST_SEC_ASYM_KEY_INFO stCAInfo, keyInfo;
    memset(&stCAInfo, 0x00, sizeof(ST_SEC_ASYM_KEY_INFO));
    memset(&keyInfo, 0x00, sizeof(ST_SEC_ASYM_KEY_INFO));
    jfieldID fid_keyIndex = (*env)->GetFieldID(env, stSecAsymKeyInfoCls, "keyIdx", "I");
    jfieldID fid_keyType = (*env)->GetFieldID(env, stSecAsymKeyInfoCls, "keyType", "I");
    jfieldID fid_keyUsage = (*env)->GetFieldID(env, stSecAsymKeyInfoCls, "keyUsage", "I");

    stCAInfo.KeyIdx = (*env)->GetIntField(env, ca_key, fid_keyIndex);
    stCAInfo.KeytType = (*env)->GetIntField(env, ca_key, fid_keyType);
    stCAInfo.KeyUsage = (*env)->GetIntField(env, ca_key, fid_keyUsage);
    keyInfo.KeyIdx = (*env)->GetIntField(env, dst_key, fid_keyIndex);
    keyInfo.KeytType = (*env)->GetIntField(env, dst_key, fid_keyType);
    keyInfo.KeyUsage = (*env)->GetIntField(env, dst_key, fid_keyUsage);
    LOGD_FMT(">>> CAInfo.KeyIdx[%d], CAInfo.KeyType[%d], CAInfo.KeyUsage[%d], key.KeyIdx[%d], key.KeyType[%d], key.KeyUsage[%d]",
             stCAInfo.KeyIdx, stCAInfo.KeytType, stCAInfo.KeyUsage, keyInfo.KeyIdx, keyInfo.KeytType, keyInfo.KeyUsage);
    int ret = NAPI_SecGeneratePubkeyCert(&keyInfo, &stCAInfo);
    LOGD_FMT("NAPI_SecGeneratePubkeyCert ret[%d]", ret);
    if (ret == NAPI_OK) {
        ret = NAPI_SecGetKeyInfo(SEC_KEY_INFO_CERT, keyInfo.KeyIdx, keyInfo.KeytType, keyInfo.KeyUsage, NULL, NULL, outInfo, &outInfoLen);
        if (ret == NAPI_OK && outInfoLen > 0) {
            (*env)->SetIntArrayRegion(env, cipher_cert_len, 0, 1, & outInfoLen);
            (*env)->SetByteArrayRegion(env, cipher_cert, 0, outInfoLen, outInfo);
        }
    }
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecPINBlockConvert(JNIEnv *env, jobject thiz,
                                                                         jstring pan,
                                                                         jint pin_convert_mode,
                                                                         jint session_pin_block_format,
                                                                         jint convert_pin_block_format,
                                                                         jint session_key_id,
                                                                         jobject session_key_info,
                                                                         jint pin_key_id,
                                                                         jobject pin_key_info,
                                                                         jobject rsa_key,
                                                                         jbyteArray pin_block,
                                                                         jint pin_block_len,
                                                                         jbyteArray out_pin_block,
                                                                         jintArray out_pin_block_len) {
    ST_SEC_PINBLOCK_INFO sessionKeyInfo, pinKeyInfo;
    memset(&sessionKeyInfo, 0x00, sizeof(ST_SEC_PINBLOCK_INFO));
    memset(&pinKeyInfo, 0x00, sizeof(ST_SEC_PINBLOCK_INFO));

    ST_SEC_PINCONVERT_INFO dstConvertInfo;
    memset(&dstConvertInfo, 0x00, sizeof(ST_SEC_PINCONVERT_INFO));
    dstConvertInfo.type = pin_convert_mode;

    jclass keyInfoCls = (*env)->GetObjectClass(env, session_key_info);
    jfieldID fid_keyUsage = (*env)->GetFieldID(env, keyInfoCls, "keyUsage", "I");
    jfieldID fid_keyType = (*env)->GetFieldID(env, keyInfoCls, "keyType", "I");

    sessionKeyInfo.keyIndex = session_key_id;
    sessionKeyInfo.keyType = (*env)->GetIntField(env, session_key_info, fid_keyType);
    sessionKeyInfo.keyUsage = (*env)->GetIntField(env, session_key_info, fid_keyUsage);
    sessionKeyInfo.pinBlockFmt = session_pin_block_format;

    //只进行 PIN block 转化
    dstConvertInfo.type = pin_convert_mode;
    if (pin_convert_mode == 0) {
        pinKeyInfo.keyIndex = pin_key_id;
        pinKeyInfo.keyType = (*env)->GetIntField(env, pin_key_info, fid_keyType);
        pinKeyInfo.keyUsage = (*env)->GetIntField(env, pin_key_info, fid_keyUsage);
        pinKeyInfo.ad = (void *)&dstConvertInfo;
        pinKeyInfo.adSize = sizeof(dstConvertInfo);
        pinKeyInfo.pinBlockFmt = convert_pin_block_format;
    } else if (pin_convert_mode == 1){
        pinKeyInfo.keyIndex = session_key_id;
        pinKeyInfo.keyType = (*env)->GetIntField(env, session_key_info, fid_keyType);
        pinKeyInfo.keyUsage = (*env)->GetIntField(env, session_key_info, fid_keyUsage);
        pinKeyInfo.pinBlockFmt = session_pin_block_format;
        if (rsa_key != NULL) {
            jclass rsaKeyCls = (*env)->GetObjectClass(env, rsa_key);
            jfieldID fid_modulus = (*env)->GetFieldID(env, rsaKeyCls, "modulus", "[B");
            jfieldID fid_exponent = (*env)->GetFieldID(env, rsaKeyCls, "exponent", "[B");
            uchar *exponent = (*env)->GetObjectField(env, rsa_key, fid_exponent);
            uchar *modulus = (*env)->GetObjectField(env, rsa_key, fid_modulus);
            dstConvertInfo.RsaKey.usBits = strlen(modulus) * 8;
            memcpy(dstConvertInfo.RsaKey.sModulus, modulus, strlen(modulus));
            memcpy(dstConvertInfo.RsaKey.sExponent, exponent, strlen(exponent));
        }
        pinKeyInfo.ad = (void *)&dstConvertInfo;
        pinKeyInfo.adSize = sizeof(dstConvertInfo);
    }
    uchar *PAN = (*env)->GetStringUTFChars(env, pan, NULL);
    int panLen = (*env)->GetStringUTFLength(env, pan);
    if (panLen > 0) {
        sessionKeyInfo.panLen = panLen;
        memcpy(sessionKeyInfo.pan, PAN, panLen);
        pinKeyInfo.panLen = panLen;
        memcpy(pinKeyInfo.pan, PAN, panLen);
    }

    uchar *pinBlock = (*env)->GetByteArrayElements(env, pin_block, NULL);
    uchar outPinBlock[2048] = {0};
    int outPinBlockLen = 0;
    int ret = NAPI_SecPINBlockConvert(&sessionKeyInfo, pin_block_len, pinBlock, &pinKeyInfo, &outPinBlockLen, outPinBlock);
    LOGD_FMT("NAPI_SecPINBlockConvert ret[%d]", ret);
    if (ret == NAPI_OK) {
        (*env)->SetIntArrayRegion(env, out_pin_block_len, 0, 1, &outPinBlockLen);
        (*env)->SetByteArrayRegion(env, out_pin_block, 0, outPinBlockLen, outPinBlock);
    }
    (*env)->ReleaseByteArrayElements(env, pin_block, pinBlock, 0);
    return ret;
}
// 检查是否为数字键 (0-9)
bool checkDigitsButton(jint keyValue) {
    if(keyValue < 0x30 || keyValue > 0x39) {
        LOGE_FMT("Invalid key value: %d", keyValue);
        return false;
    }
    return true;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecVPPAAInit(
        JNIEnv *env,
        jobject thiz,
        jintArray key_values,           // 按键键值数组（输入输出）
        jintArray buttons_coordination, // 按键坐标数组
        jint button_count,             // 按键数量
        jintArray screen_area,         // 屏幕区域
        jintArray pinpad_area,         // 键盘区域
        jobject keyboard_parameters    // 键盘参数对象
) {

    // 1. 获取键值数组
    jint* keyVals = (*env)->GetIntArrayElements(env, key_values, NULL);

    // 2. 获取坐标数组
    jint* coordData = (*env)->GetIntArrayElements(env, buttons_coordination, NULL);

    // 3. 创建vpp_key数组
    vpp_key* keys = (vpp_key*)malloc(sizeof(vpp_key) * button_count);

    // 4. 填充按键结构
    for (int i = 0; i < button_count; i++) {
        keys[i].key = keyVals[i];  // 键值

        // 坐标处理
        int offset = i * 4;
        keys[i].btn.l_top.x = (uint16_t)coordData[offset];      // left x
        keys[i].btn.l_top.y = (uint16_t)coordData[offset + 1];  // top y
        keys[i].btn.r_bottom.x = (uint16_t)coordData[offset + 2]; // right x
        keys[i].btn.r_bottom.y = (uint16_t)coordData[offset + 3]; // bottom y

    }

    // 5. 获取屏幕区域
    jint* screenArr = (*env)->GetIntArrayElements(env, screen_area, NULL);

    vpp_button screen = {
            .l_top = {
                    .x = (uint16_t)screenArr[0],
                    .y = (uint16_t)screenArr[1]
            },
            .r_bottom = {
                    .x = (uint16_t)screenArr[2],
                    .y = (uint16_t)screenArr[3]
            }
    };

    // 6. 获取键盘区域
    jint* padArr = (*env)->GetIntArrayElements(env, pinpad_area, NULL);

    vpp_button keypad = {
            .l_top = {
                    .x = (uint16_t)padArr[0],
                    .y = (uint16_t)padArr[1]
            },
            .r_bottom = {
                    .x = (uint16_t)padArr[2],
                    .y = (uint16_t)padArr[3]
            }
    };


    // 7. 转换键盘参数
    vppAAConfig_st config = {0};
    jclass paramsClass = (*env)->GetObjectClass(env, keyboard_parameters);

    // 获取字段ID
    jfieldID swipeDistanceField = (*env)->GetFieldID(env, paramsClass, "swipeDistance", "I");
    jfieldID clickIntervalField = (*env)->GetFieldID(env, paramsClass, "clickInterval", "I");
    jfieldID pressTimeField = (*env)->GetFieldID(env, paramsClass, "pressTime", "I");
    jfieldID clickModeField = (*env)->GetFieldID(
            env,
            paramsClass,
            "clickMode",
            "Lcom/newland/nsdk/core/api/internal/pinentry/ClickMode;"
    );
    jfieldID effectModeField = (*env)->GetFieldID(
            env,
            paramsClass,
            "effectMode",
            "Lcom/newland/nsdk/core/api/internal/pinentry/EffectMode;"
    );
    jfieldID isRandomPinpadField = (*env)->GetFieldID(env, paramsClass, "isRandomPinpad", "Z");

    // 基本参数
    config.swipeDistance = (uint32_t)(*env)->GetIntField(env, keyboard_parameters, swipeDistanceField);
    config.clickInterval = (uint32_t)(*env)->GetIntField(env, keyboard_parameters, clickIntervalField);
    config.pressTime = (uint32_t)(*env)->GetIntField(env, keyboard_parameters, pressTimeField);

    // 点击模式
    jobject clickModeObj = (*env)->GetObjectField(env, keyboard_parameters, clickModeField);
    if (clickModeObj != NULL) {
        jclass clickModeClass = (*env)->GetObjectClass(env, clickModeObj);
        jmethodID ordinalMethod = (*env)->GetMethodID(env, clickModeClass, "ordinal", "()I");
        config.clickMode = (uint8_t)(*env)->CallIntMethod(env, clickModeObj, ordinalMethod);
        (*env)->DeleteLocalRef(env, clickModeObj);
    } else {
        LOGE_FMT("Failed to get clickMode field");
    }

    // 效果模式
    jobject effectModeObj = (*env)->GetObjectField(env, keyboard_parameters, effectModeField);
    if (effectModeObj != NULL) {
        jclass effectModeClass = (*env)->GetObjectClass(env, effectModeObj);
        jmethodID ordinalMethod = (*env)->GetMethodID(env, effectModeClass, "ordinal", "()I");
        config.effectMode = (uint8_t)(*env)->CallIntMethod(env, effectModeObj, ordinalMethod);
        (*env)->DeleteLocalRef(env, effectModeObj);
    } else {
        LOGE_FMT("Failed to get effectMode field");
    }

    // 随机键盘
    config.isRandomKeypad = (*env)->GetBooleanField(env, keyboard_parameters, isRandomPinpadField) ? 1 : 0;

    // 保留字段清零
    memset(config.rev, 0, sizeof(config.rev));

    // 8. 调用C层函数
    int result = NAPI_SecVPPAAInit(
            keys,                   // keyInfo (输入输出)
            (uint32_t)button_count, // keyNum
            &config,                // config
            &screen,                // tsArea
            &keypad,                // keypadArea
            NULL,                   // pad保留
            0                       // adSize保留
    );

    if (result != NAPI_OK) {
        LOGE_FMT("NAPI_SecVPPAAInit failed with error: %d", result);
    } else {
        LOGD_FMT("NAPI_SecVPPAAInit succeeded");

        // 9. 将修改后的键值复制回Java数组
        int j = 0;
        for (int i = 0; i < button_count; i++) {
            if (checkDigitsButton(keys[i].key)){
                keyVals[j] = (jint)keys[i].key;
                j++;
            }
        }
    }

    // 10. 释放资源
    free(keys);

    // 复制回Java数组并释放
    (*env)->ReleaseIntArrayElements(env, key_values, keyVals, 0);
    (*env)->ReleaseIntArrayElements(env, buttons_coordination, coordData, JNI_ABORT);
    (*env)->ReleaseIntArrayElements(env, screen_area, screenArr, JNI_ABORT);
    (*env)->ReleaseIntArrayElements(env, pinpad_area, padArr, JNI_ABORT);

    return result;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecVPPAASetMap(
        JNIEnv *env,
        jobject thiz,
        jintArray event_values,   // 事件值数组
        jintArray action_values,  // 行为值数组
        jint count,               // 映射数量
        jint set_mode             // 设置模式
) {

    // 1. 如果是重置或设置默认，则直接调用底层函数，不需要创建映射结构
    if (count == 0 || set_mode == VPP_MAP_SETDEFAULT) {
        LOGD_FMT("Resetting event action map");
        int result = NAPI_SecVPPAASetMap(NULL, 0, (uint8_t)set_mode);
        if (result != NAPI_OK) {
            LOGE_FMT("Failed to reset or set default event action map, error: %d", result);
        }
        return result;
    }

    // 2. 获取事件值数组
    jint* eventVals = (*env)->GetIntArrayElements(env, event_values, NULL);

    // 3. 获取行为值数组
    jint* actionVals = (*env)->GetIntArrayElements(env, action_values, NULL);

    // 4. 创建事件-行为映射结构数组
    vppEventActionMap_st* mapList = (vppEventActionMap_st*)malloc(count * sizeof(vppEventActionMap_st));

    // 5. 填充映射结构
    for (int i = 0; i < count; i++) {
        mapList[i].event = (vppEvent_em)eventVals[i];
        mapList[i].action = (vppAction_em)actionVals[i];
    }

    // 6. 调用底层函数
    int result = NAPI_SecVPPAASetMap(mapList, (uint32_t)count, (uint8_t)set_mode);
    if (result != NAPI_OK) {
        LOGE_FMT("NAPI_SecVPPAASetMap failed with error: %d", result);
    } else {
        LOGD_FMT("NAPI_SecVPPAASetMap succeeded");
    }

    // 7. 释放资源
    free(mapList);
    (*env)->ReleaseIntArrayElements(env, event_values, eventVals, JNI_ABORT);
    (*env)->ReleaseIntArrayElements(env, action_values, actionVals, JNI_ABORT);

    return result;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecVPPAAGetPin(
        JNIEnv *env,
        jobject thiz,
        jintArray nEvent,        // PIN输入事件
        jintArray extendedEvent, // 扩展事件类型
        jintArray touchState,    // 触点状态
        jbyteArray pinBlock,     // PIN数据
        jintArray pinLen,        // PIN数据长度
        jbyteArray ksn,          // KSN数据
        jintArray ksnLen         // KSN数据长度
) {

    // 1. 输出变量
    uint32_t vppkey = 0;
    uint32_t vppEvent = 0;
    uint32_t state = 0;
    uint8_t pinblock[32] = {0};
    uint32_t outPinLen = 0;
    uint8_t ksnBuffer[32] = {0};
    uint32_t ksnBufferLen = 0;

    // 2. 调用底层函数
    int result = NAPI_SecVPPAAGetPin(
            &vppkey,
            &vppEvent,
            &state,
            pinblock,
            &outPinLen,
            ksnBuffer,
            &ksnBufferLen
    );
    LOGD_FMT("NAPI_SecVppAAGetPin ret[%d]", result);

    if (result != NAPI_OK) {
        LOGE_FMT("NAPI_SecVPPAAGetPin failed with error: %d", result);
        return result;
    }

    LOGD_FMT("NAPI_SecVPPAAGetPin succeeded: vppkey=%u, vppEvent=%u, state=%u, outPinLen=%u, ksnBufferLen=%u",
             vppkey, vppEvent, state, outPinLen, ksnBufferLen);

    // 3. 将结果复制回Java数组
    // 3.1 设置nEvent
    jint nEventValue = (jint)vppkey;
    (*env)->SetIntArrayRegion(env, nEvent, 0, 1, &nEventValue);

    // 3.2 设置extendedEvent
    jint extendedEventValue = (jint)vppEvent;
    (*env)->SetIntArrayRegion(env, extendedEvent, 0, 1, &extendedEventValue);

    // 3.3 设置touchState
    jint touchStateValue = (jint)state;
    (*env)->SetIntArrayRegion(env, touchState, 0, 1, &touchStateValue);

    // 3.4 设置pinLen
    jint pinLenValue = (jint)outPinLen;
    (*env)->SetIntArrayRegion(env, pinLen, 0, 1, &pinLenValue);

    // 3.5 设置ksnLen
    jint ksnLenValue = (jint)ksnBufferLen;
    (*env)->SetIntArrayRegion(env, ksnLen, 0, 1, &ksnLenValue);

    // 3.6 设置pinBlock
    if (pinBlock != NULL) {
        // 复制实际长度的数据
        size_t copyLen = (outPinLen < 32) ? outPinLen : 32;
        (*env)->SetByteArrayRegion(env, pinBlock, 0, copyLen, (jbyte*)pinblock);

        // 如果是在pin的输入过程中，第一个字节表示当前PIN码长度
        if (outPinLen == 0 && copyLen > 0) {
            LOGD_FMT("Current PIN length: %d", pinblock[0]);
        }
    }

    // 3.7 设置ksn
    if (ksn != NULL) {
        // 复制实际长度的数据
        size_t copyLen = (ksnBufferLen < 32) ? ksnBufferLen : 32;
        (*env)->SetByteArrayRegion(env, ksn, 0, copyLen, (jbyte*)ksnBuffer);
    }

    return result;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecKeyExport(JNIEnv *env, jobject thiz,
                                                                   jint mode,
                                                                   jobject st_sec_keyin_data,
                                                                   jbyteArray out_data,
                                                                   jintArray out_data_len) {
    uchar outData[4096] = {0};
    int outDataLen = 4096;
    int ret = 0;
    ST_SEC_KEYINFO kekInfo, keyInfo;
    memset(&kekInfo, 0x00, sizeof(ST_SEC_KEYINFO));
    memset(&keyInfo, 0x00, sizeof(ST_SEC_KEYINFO));
    jclass stSecKeyInDataCls = (*env)->GetObjectClass(env, st_sec_keyin_data);
    jfieldID fid_kekIdx = (*env)->GetFieldID(env, stSecKeyInDataCls, "ucKEKIdx", "I");
    jfieldID fid_kekType = (*env)->GetFieldID(env, stSecKeyInDataCls, "KEKType", "I");
    jfieldID fid_kekUsage = (*env)->GetFieldID(env, stSecKeyInDataCls, "KEKUsage", "I");
    jfieldID fid_keyIdx = (*env)->GetFieldID(env, stSecKeyInDataCls, "ucKeyIdx", "I");
    jfieldID fid_keyType = (*env)->GetFieldID(env, stSecKeyInDataCls, "KeyType", "I");
    jfieldID fid_keyUsage = (*env)->GetFieldID(env, stSecKeyInDataCls, "KeyUsage", "I");
    jfieldID fid_ad = (*env)->GetFieldID(env, stSecKeyInDataCls, "pAD", "[B");
    kekInfo.KeyIdx = (*env)->GetIntField(env, st_sec_keyin_data, fid_kekIdx);
    kekInfo.KeytType = (*env)->GetIntField(env, st_sec_keyin_data, fid_kekType);
    kekInfo.KeyUsage = (*env)->GetIntField(env, st_sec_keyin_data, fid_kekUsage);
    keyInfo.KeyIdx = (*env)->GetIntField(env, st_sec_keyin_data, fid_keyIdx);
    keyInfo.KeytType = (*env)->GetIntField(env, st_sec_keyin_data, fid_keyType);
    keyInfo.KeyUsage = (*env)->GetIntField(env, st_sec_keyin_data, fid_keyUsage);
    jbyteArray pad = (*env)->GetObjectField(env, st_sec_keyin_data, fid_ad);
    uchar *ad = NULL;
    if (pad != NULL) {
        ad = (*env)->GetByteArrayElements(env, pad, NULL);
        int adSize = (*env)->GetIntField(env, st_sec_keyin_data, (*env)->GetFieldID(env, stSecKeyInDataCls, "nADSize", "I"));
        ret = NAPI_SecKeyExport(mode, &kekInfo, &keyInfo, outData, &outDataLen, ad, adSize);
    } else {
        ret = NAPI_SecKeyExport(mode, &kekInfo, &keyInfo, outData, &outDataLen, NULL, 0);
    }
    LOGD_FMT("NAPI_SecKeyExport ret = %d", ret);
    if (ret == 0) {
        (*env)->SetIntArrayRegion(env, out_data_len, 0, 1, &outDataLen);
        (*env)->SetByteArrayRegion(env, out_data, 0, outDataLen, outData);
    }

    if (pad != NULL) {
        (*env)->ReleaseByteArrayElements(env, pad, ad, 0);
    }
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecCreateCryptogram(JNIEnv *env, jobject thiz,
                                                                          jobject crypto_key,
                                                                          jobject sec_keyin_data,
                                                                          jobject cryptogram_info,
                                                                          jbyteArray out_data,
                                                                          jintArray out_data_len) {
    ST_SEC_ASYM_KEY_INFO cryptoKey, sessionKey, componentSecretKey;
    memset(&cryptoKey, 0x00, sizeof(ST_SEC_ASYM_KEY_INFO));
    memset(&sessionKey, 0x00, sizeof(ST_SEC_ASYM_KEY_INFO));
    memset(&componentSecretKey, 0x00, sizeof(ST_SEC_ASYM_KEY_INFO));

    ST_SEC_CRYPTOINFO cryptogramInfo;
    memset(&cryptogramInfo, 0x00, sizeof(ST_SEC_CRYPTOINFO));

    jclass asymmetricKeyCls = (*env)->GetObjectClass(env, crypto_key);
    jclass stSecKeyInDataCls = (*env)->GetObjectClass(env, sec_keyin_data);
    jclass cryptogramInfoCls = (*env)->GetObjectClass(env, cryptogram_info);

    jfieldID fid_keyID = (*env)->GetFieldID(env, asymmetricKeyCls, "keyID", "B");
    cryptoKey.KeyIdx = (*env)->GetByteField(env, crypto_key, fid_keyID);

    jfieldID fid_keyUsage = (*env)->GetFieldID(env, asymmetricKeyCls, "keyUsage", "Lcom/newland/nsdk/core/api/common/keymanager/AsymKeyUsage;");
    jobject asymKeyUsageObj = (*env)->GetObjectField(env, crypto_key, fid_keyUsage);
    jclass asymKeyUsageCls = (*env)->FindClass(env, "com/newland/nsdk/core/api/common/keymanager/AsymKeyUsage");
    jmethodID mid_asymKeyUsage_getCode = (*env)->GetMethodID(env, asymKeyUsageCls, "getCode", "()B");
    cryptoKey.KeyUsage = (*env)->CallByteMethod(env, asymKeyUsageObj, mid_asymKeyUsage_getCode);

    jfieldID fid_keyType = (*env)->GetFieldID(env, asymmetricKeyCls, "keyType", "Lcom/newland/nsdk/core/api/common/keymanager/AsymKeyType;");
    jobject  asymKeyTypeObj = (*env)->GetObjectField(env, crypto_key, fid_keyType);
    jclass asymKeyTypeCls = (*env)->FindClass(env, "com/newland/nsdk/core/api/common/keymanager/AsymKeyType");
    jmethodID mid_asymKeyType_getCode = (*env)->GetMethodID(env, asymKeyTypeCls, "getCode", "()B");
    cryptoKey.KeytType = (*env)->CallByteMethod(env, asymKeyTypeObj, mid_asymKeyType_getCode);


    jfieldID fid_kekIdx = (*env)->GetFieldID(env, stSecKeyInDataCls, "ucKEKIdx", "I");
    sessionKey.KeyIdx = (*env)->GetIntField(env, sec_keyin_data, fid_kekIdx);
    jfieldID fid_kekType = (*env)->GetFieldID(env, stSecKeyInDataCls, "KEKType", "I");
    sessionKey.KeytType = (*env)->GetIntField(env, sec_keyin_data, fid_kekType);
    jfieldID fid_kekUsage = (*env)->GetFieldID(env, stSecKeyInDataCls, "KEKUsage", "I");
    sessionKey.KeyUsage = (*env)->GetIntField(env, sec_keyin_data, fid_kekUsage);
    jfieldID fid_keyIdx = (*env)->GetFieldID(env, stSecKeyInDataCls, "ucKeyIdx", "I");
    componentSecretKey.KeyIdx = (*env)->GetIntField(env, sec_keyin_data, fid_keyIdx);
    jfieldID fid_ucKeyType = (*env)->GetFieldID(env, stSecKeyInDataCls, "KeyType", "I");
    componentSecretKey.KeytType = (*env)->GetIntField(env, sec_keyin_data, fid_ucKeyType);
    jfieldID fid_ucKeyUsage = (*env)->GetFieldID(env, stSecKeyInDataCls, "KeyUsage", "I");
    componentSecretKey.KeyUsage = (*env)->GetIntField(env, sec_keyin_data, fid_ucKeyUsage);
    jfieldID fid_ad = (*env)->GetFieldID(env, stSecKeyInDataCls, "pAD", "[B");
    jbyteArray ba_pad = (*env)->GetObjectField(env, sec_keyin_data, fid_ad);
    uchar *pad = NULL;
    int adLen = 0;
    if (ba_pad != NULL) {
        pad = (*env)->GetByteArrayElements(env, ba_pad, NULL);
        adLen = (*env)->GetArrayLength(env, ba_pad);
    }

    uchar *prefix = NULL;
    uchar *suffix = NULL;
    int prefixLen = 0;
    int suffixLen = 0;

    jfieldID fid_prefixInfo = (*env)->GetFieldID(env, cryptogramInfoCls, "prefixInfo", "[B");
    jbyteArray prefixInfo = (*env)->GetObjectField(env, cryptogram_info, fid_prefixInfo);
    if (prefixInfo != NULL) {
        prefix = (*env)->GetByteArrayElements(env, prefixInfo, NULL);
        prefixLen = (*env)->GetArrayLength(env, prefixInfo);
    }
    cryptogramInfo.prefixLen = prefixLen;
    cryptogramInfo.prefix = prefix;

    jfieldID fid_suffixInfo = (*env)->GetFieldID(env, cryptogramInfoCls, "suffixInfo", "[B");
    jstring suffixInfo = (*env)->GetObjectField(env, cryptogram_info, fid_suffixInfo);
    if (suffixInfo != NULL) {
        suffix = (*env)->GetByteArrayElements(env, suffixInfo, NULL);
        suffixLen = (*env)->GetArrayLength(env, suffixInfo);
    }
    cryptogramInfo.suffixLen = suffixLen;
    cryptogramInfo.suffix = suffix;

    jfieldID fid_encodingMode = (*env)->GetFieldID(env, cryptogramInfoCls, "encodingMode", "Lcom/newland/nsdk/core/api/common/crypto/AsymEncodingMode;");
    jobject asymEncodingModeObj = (*env)->GetObjectField(env, cryptogram_info, fid_encodingMode);
    jclass asymEncodingModeCls = (*env)->FindClass(env, "com/newland/nsdk/core/api/common/crypto/AsymEncodingMode");
    jmethodID mid_asymEncodingMode_ordinal = (*env)->GetMethodID(env, asymEncodingModeCls, "ordinal", "()I");
    cryptogramInfo.EncodingMode = (*env)->CallIntMethod(env, asymEncodingModeObj, mid_asymEncodingMode_ordinal);

    jfieldID fid_messageDigestType = (*env)->GetFieldID(env, cryptogramInfoCls, "messageDigestType", "Lcom/newland/nsdk/core/api/common/crypto/MessageDigestType;");
    jobject messageDigestTypeObj = (*env)->GetObjectField(env, cryptogram_info, fid_messageDigestType);
    jclass messageDigestTypeCls = (*env)->FindClass(env, "com/newland/nsdk/core/api/common/crypto/MessageDigestType");
    jmethodID mid_messageDigestType_ordinal = (*env)->GetMethodID(env, messageDigestTypeCls, "ordinal", "()I");
    cryptogramInfo.MdAlg = (*env)->CallIntMethod(env, messageDigestTypeObj, mid_messageDigestType_ordinal);
//    jfieldID fid_additionalData = (*env)->GetFieldID(env, cryptogramInfoCls, "additionalData", "[B");
//    jbyteArray additionalData = (*env)->GetObjectField(env, cryptogram_info, fid_additionalData);

    uchar outData[4096] = {0};
    int outDataLen = 4096;
    int ret = NAPI_SecCreateCryptogram(&cryptoKey, &cryptogramInfo, &sessionKey, &componentSecretKey, outData, &outDataLen, pad, adLen);
    LOGD_FMT("NAPI_SecCreateCryptogram ret[%d], outDataLen[%d]", ret, outDataLen);
    if (ret == 0) {
        (*env)->SetIntArrayRegion(env, out_data_len, 0, 1, &outDataLen);
        (*env)->SetByteArrayRegion(env, out_data, 0, outDataLen, outData);
    }
    if (pad != NULL) {
        (*env)->ReleaseByteArrayElements(env, ba_pad, pad, 0);
    }
    if (prefix != NULL) {
        (*env)->ReleaseByteArrayElements(env, prefixInfo, prefix, 0);
    }
    if (suffix != NULL) {
        (*env)->ReleaseByteArrayElements(env, suffixInfo, suffix, 0);
    }
    (*env)->DeleteLocalRef(env, asymKeyUsageObj);
    (*env)->DeleteLocalRef(env, asymKeyTypeObj);
    (*env)->DeleteLocalRef(env, asymEncodingModeObj);
    (*env)->DeleteLocalRef(env, messageDigestTypeObj);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecEncryption_1GCM(JNIEnv *env, jobject thiz,
                                                                         jobject data_in,
                                                                         jbyteArray ps_data_out,
                                                                         jintArray pn_out_len,
                                                                         jbyteArray ps_ksn_out,
                                                                         jintArray pn_out_ksn_len,
                                                                         jbyteArray tag_data,
                                                                         jint tag_data_len,
                                                                         jbyteArray auth_data,
                                                                         jint auth_data_len) {

    ST_SEC_ENCRYPTION_DATA encryptionData;
    ST_SEC_GCM_AES_DUKPT_APPEND_DATA stDerivateData;
    ST_SEC_GCM_APPEND_DATA stSecGcmAppendData;
    memset(&stSecGcmAppendData, 0x00, sizeof(ST_SEC_GCM_APPEND_DATA));
    memset(&stDerivateData, 0x00, sizeof(ST_SEC_DUKPT_DERIVATE_DATA));
    memset(&encryptionData, 0, sizeof(encryptionData));

    jclass dataInCls = (*env)->GetObjectClass(env, data_in);
    if (dataInCls == NULL) {
        LOGD_FMT(">>>dataInCls[%d]", dataInCls);
        return NDK_ERR_PARA;
    }
    encryptionData.ucKeyID = (*env)->GetIntField(env, data_in,
                                                 (*env)->GetFieldID(env, dataInCls, "ucKeyID",
                                                                    "I"));
    encryptionData.CipherType = (*env)->GetIntField(env, data_in,
                                                    (*env)->GetFieldID(env, dataInCls, "CipherType",
                                                                       "I"));
    encryptionData.KeyUsage = (*env)->GetIntField(env, data_in,
                                                  (*env)->GetFieldID(env, dataInCls, "KeyUsage",
                                                                     "I"));
    encryptionData.PaddingMode = (*env)->GetIntField(env, data_in,
                                                     (*env)->GetFieldID(env, dataInCls,
                                                                        "PaddingMode", "I"));
    encryptionData.unIVSize = (*env)->GetIntField(env, data_in,
                                                  (*env)->GetFieldID(env, dataInCls, "unIVSize",
                                                                     "I"));

    jobject derivateData = (*env)->GetObjectField(env, data_in,(*env)->GetFieldID(env, dataInCls, "dukptDerivateData", "Lcom/newland/nsdk/core/internal/crypto/ST_SEC_DUKPT_DERIVATE_DATA;"));
    if (derivateData != NULL) {
        jclass  class_derivateData = (*env)->GetObjectClass(env, derivateData);

        stDerivateData.KeyType = (*env)->GetIntField(env, derivateData,
                                                     (*env)->GetFieldID(env, class_derivateData, "derivateKeyType",
                                                                        "I"));
        stDerivateData.nKeyLen = (*env)->GetIntField(env, derivateData,
                                                     (*env)->GetFieldID(env, class_derivateData, "derivateKeyLen",
                                                                        "I"));
        stDerivateData.DerivateUsage = (*env)->GetIntField(env, derivateData,
                                                           (*env)->GetFieldID(env, class_derivateData, "derivateKeyUsage",
                                                                              "I"));
    }

    uchar *pIV;
    jbyteArray IV = (jbyteArray) (*env)->GetObjectField(env, data_in,
                                                        (*env)->GetFieldID(env, dataInCls, "psIV",
                                                                           "[B"));
    if (IV != NULL && encryptionData.unIVSize > 0) {
        pIV = (*env)->GetByteArrayElements(env, IV, NULL);
        encryptionData.psIV = pIV;
    }

    encryptionData.unDataInLen = (*env)->GetIntField(env, data_in,
                                                     (*env)->GetFieldID(env, dataInCls,
                                                                        "unDataInLen", "I"));
    uchar *pDataIn;
    jbyteArray dataIn = (jbyteArray) (*env)->GetObjectField(env, data_in,
                                                            (*env)->GetFieldID(env, dataInCls,
                                                                               "psDataIn", "[B"));
    if (dataIn != NULL && encryptionData.unDataInLen > 0) {
        pDataIn = (*env)->GetByteArrayElements(env, dataIn, NULL);
        encryptionData.psDataIn = pDataIn;
    }

    jbyte *p_auth_data = NULL;
    uchar authTag[tag_data_len];
    memset(authTag, 0, sizeof(authTag));
    if (derivateData != NULL) {
        stDerivateData.adAuthDataLen = auth_data_len;
        stDerivateData.authTagLen = tag_data_len;
        stDerivateData.authTag = authTag;
        if (auth_data != NULL) {
            p_auth_data = (*env)->GetByteArrayElements(env, auth_data, NULL);
            stDerivateData.adAuthData = (uchar *)p_auth_data;
        }
        encryptionData.pAD = &stDerivateData;
        encryptionData.unADSize = sizeof(ST_SEC_GCM_AES_DUKPT_APPEND_DATA);
        LOGD_FMT(
                ">>>derivate key type[%d] derivate key usage[%d] derivate key len[%d]",
                stDerivateData.KeyType, stDerivateData.DerivateUsage, stDerivateData.nKeyLen);
    } else {
        stSecGcmAppendData.adAuthDataLen = auth_data_len;
        stSecGcmAppendData.authTagLen = tag_data_len;
        stSecGcmAppendData.authTag = authTag;
        if (auth_data != NULL) {
            p_auth_data = (*env)->GetByteArrayElements(env, auth_data, NULL);
            stSecGcmAppendData.adAuthData = (uchar *)p_auth_data;
        }
        encryptionData.pAD = (uchar *) &stSecGcmAppendData;
        encryptionData.unADSize = sizeof(ST_SEC_GCM_APPEND_DATA);
    }

    LOGD_FMT(
            ">>>ucKeyID[%d] CipherType[%d] KeyUsage[%d] PaddingMode[%d] unIVSize[%d] unDataInLen[%d] unADSize[%d]",
            encryptionData.ucKeyID, encryptionData.CipherType, encryptionData.KeyUsage,
            encryptionData.PaddingMode, encryptionData.unIVSize, encryptionData.unDataInLen,
            encryptionData.unADSize);
    uchar outData[4096], ksnData[32];
    int outDataLen = 0, ksnDataLen = 0;

    memset(outData, 0, sizeof(outData));
    memset(ksnData, 0, sizeof(ksnData));
    int ret = NDK_ERR;
    ret = NAPI_SecEncryption(&encryptionData, outData, &outDataLen, ksnData, &ksnDataLen);
    LOGD_FMT(">>>NAPI_SecEncryption ret[%d], outDataLen[%d]", ret, outDataLen);

    if (ret == 0) {
        if (outDataLen > 0) {
            (*env)->SetIntArrayRegion(env, pn_out_len, 0, 1, &outDataLen);
            (*env)->SetByteArrayRegion(env, ps_data_out, 0, outDataLen, outData);
        }
        if (ksnDataLen > 0) {
            (*env)->SetIntArrayRegion(env, pn_out_ksn_len, 0, 1, &ksnDataLen);
            (*env)->SetByteArrayRegion(env, ps_ksn_out, 0, ksnDataLen, ksnData);
        }
        if (tag_data_len > 0) {
            (*env)->SetByteArrayRegion(env, tag_data, 0, tag_data_len, authTag);
        }
    }
    if (IV != NULL) {
        (*env)->ReleaseByteArrayElements(env, IV, pIV, 0);
    }
    if (dataIn != NULL && encryptionData.unDataInLen > 0) {
        (*env)->ReleaseByteArrayElements(env, dataIn, pDataIn, 0);
    }
    if (p_auth_data != NULL) {
        (*env)->ReleaseByteArrayElements(env, auth_data, p_auth_data, 0);
    }
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecInjectPubKeys(JNIEnv *env, jobject thiz,
                                                                       jobjectArray st_sec_injectkey_infos,
                                                                       jint inject_key_info_list_count,
                                                                       jobject mac_info,
                                                                       jobject sign_verify_parameters,
                                                                       jbyteArray data,
                                                                       jint data_len,
                                                                       jbyteArray additional_data,
                                                                       jint additional_data_len) {
    ST_SEC_VERIFYKEY_INFO stSecVerifykeyInfo;
    memset(&stSecVerifykeyInfo, 0x00, sizeof(ST_SEC_VERIFYKEY_INFO));
    ST_SEC_VERIFYMAC_INFO stSecVerifymacInfo;
    memset(&stSecVerifymacInfo, 0x00, sizeof(ST_SEC_VERIFYMAC_INFO));
    uchar *macData = NULL;
    jbyteArray ba_macData = NULL;
    uchar *iv;
    jbyteArray ba_iv = NULL;
    //Msg data
    uchar *Data = (*env)->GetByteArrayElements(env, data, NULL);

    if (mac_info != NULL) {
        stSecVerifykeyInfo.mode = VERIFY_MAC;
        jclass macInfoCls = (*env)->GetObjectClass(env, mac_info);
        stSecVerifymacInfo.keyIndex = (*env)->GetIntField(env, mac_info, (*env)->GetFieldID(env, macInfoCls, "keyID", "I"));
        LOGD_FMT("keyID:%d", stSecVerifymacInfo.keyIndex);
        stSecVerifymacInfo.keyType = (*env)->GetIntField(env, mac_info, (*env)->GetFieldID(env, macInfoCls, "keyType", "I"));
        stSecVerifymacInfo.keyUsage = (*env)->GetIntField(env, mac_info, (*env)->GetFieldID(env, macInfoCls, "keyUsage", "I"));
        stSecVerifymacInfo.macMode = (*env)->GetIntField(env, mac_info, (*env)->GetFieldID(env, macInfoCls, "macMode", "I"));
        ba_iv = (*env)->GetObjectField(env, mac_info, (*env)->GetFieldID(env, macInfoCls, "iv", "[B"));
        if (ba_iv != NULL) {
            int ivLen = (*env)->GetIntField(env, mac_info, (*env)->GetFieldID(env, macInfoCls, "ivLen", "I"));
            stSecVerifymacInfo.ivLen = ivLen;
            iv = (*env)->GetByteArrayElements(env, ba_iv, NULL);
            memcpy(stSecVerifymacInfo.iv, iv, ivLen);
        }
        ba_macData = (*env)->GetObjectField(env, mac_info, (*env)->GetFieldID(env, macInfoCls, "macData", "[B"));
        if (ba_macData != NULL) {
            int macDataLen = (*env)->GetIntField(env, mac_info, (*env)->GetFieldID(env, macInfoCls, "macDataLen", "I"));
            stSecVerifymacInfo.macLen = macDataLen;
            macData = (*env)->GetByteArrayElements(env, ba_macData, NULL);
            stSecVerifymacInfo.macData = macData;
        }

        stSecVerifymacInfo.keyMsg = Data;
        stSecVerifymacInfo.msgLen = (*env)->GetArrayLength(env, data);
        stSecVerifykeyInfo.macInfo = stSecVerifymacInfo;
    }
    if (sign_verify_parameters != NULL) {
        stSecVerifykeyInfo.mode = VERIFY_SIGNATURE;
    }

    ST_SEC_INJECTKEY_INFO stSecInjectkeyInfo[inject_key_info_list_count];
    jclass injectKeyInfoCls = (*env)->FindClass(env, "com/newland/nsdk/core/internal/keymanager/ST_SEC_INJECTKEY_INFO");
    for (int i = 0; i < inject_key_info_list_count; i++) {
        memset(&stSecInjectkeyInfo[i], 0x00, sizeof(ST_SEC_INJECTKEY_INFO));
        jobject injectKeyInfoObj = (*env)->GetObjectArrayElement(env, st_sec_injectkey_infos, i);
        stSecInjectkeyInfo[i].pubKey.KeyIdx = (*env)->GetIntField(env, injectKeyInfoObj, (*env)->GetFieldID(env, injectKeyInfoCls, "keyID", "I"));
        stSecInjectkeyInfo[i].pubKey.KeytType = (*env)->GetIntField(env, injectKeyInfoObj, (*env)->GetFieldID(env, injectKeyInfoCls, "keyType", "I"));
        stSecInjectkeyInfo[i].pubKey.KeyUsage = (*env)->GetIntField(env, injectKeyInfoObj, (*env)->GetFieldID(env, injectKeyInfoCls, "keyUsage", "I"));
        jstring tagStr = (*env)->GetObjectField(env, injectKeyInfoObj, (*env)->GetFieldID(env, injectKeyInfoCls, "tag", "Ljava/lang/String;"));
        uchar *tag = (*env)->GetStringUTFChars(env, tagStr, NULL);
        stSecInjectkeyInfo[i].tag = tag;
        (*env)->DeleteLocalRef(env, injectKeyInfoObj);
        (*env)->ReleaseStringUTFChars(env, tagStr, tag);
    }

    uchar *pad = NULL;
    if (additional_data != NULL) {
        (*env)->GetByteArrayElements(env, additional_data, NULL);
    }

    int ret = NAPI_SecInjectPubKey(&stSecVerifykeyInfo, stSecInjectkeyInfo, inject_key_info_list_count, pad, additional_data_len);
    LOGD_FMT("NAPI_SecInjectPubKey ret = %d", ret);
    if (ret == 0) {
        for (int i = 0; i < inject_key_info_list_count; i++) {
            jobject injectKeyInfoObj = (*env)->GetObjectArrayElement(env, st_sec_injectkey_infos, i);
            (*env)->SetBooleanField(env, injectKeyInfoObj, (*env)->GetFieldID(env, injectKeyInfoCls, "injectResult", "Z"), (stSecInjectkeyInfo[i].result == 0));
            (*env)->DeleteLocalRef(env, injectKeyInfoObj);
        }
    }
    if (ba_macData != NULL) {
        (*env)->ReleaseByteArrayElements(env, ba_macData, macData, 0);
    }
    if (ba_iv != NULL) {
        (*env)->ReleaseByteArrayElements(env, ba_iv, iv, 0);
    }
    if (data != NULL) {
        (*env)->ReleaseByteArrayElements(env, data, Data, 0);
    }
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecVerifyCert(JNIEnv *env, jobject thiz,
                                                                    jint ca_type,
                                                                    jstring ca_cert_data,
                                                                    jint ca_cert_data_len,
                                                                    jbyteArray cert_data,
                                                                    jint cert_data_len,
                                                                    jbyteArray public_key,
                                                                    jintArray public_key_len) {
    uchar *caCertData = (*env)->GetStringUTFChars(env, ca_cert_data, NULL);
    LOGD_FMT("caCertData:%s", caCertData);
    uchar *certData = (*env)->GetByteArrayElements(env, cert_data, NULL);
    uchar publicKey[4096];
    int publicKeyLen = 0;
    int ret = NAPI_AlgRSAVerifyCert(ca_type, caCertData, ca_cert_data_len, certData, cert_data_len, publicKey, &publicKeyLen);
    LOGD_FMT("NAPI_SecVerifyCert ret = %d, publicKeyLen = %d", ret, publicKeyLen);
    if (ret == 0) {
        (*env)->SetIntArrayRegion(env, public_key_len, 0, 1, &publicKeyLen);
        (*env)->SetByteArrayRegion(env, public_key, 0, publicKeyLen, publicKey);
    }
    (*env)->ReleaseStringUTFChars(env, ca_cert_data, caCertData);
    (*env)->ReleaseByteArrayElements(env, cert_data, certData, 0);

    return ret;
}

    JNIEXPORT jint JNICALL
Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1SecDecryption_1GCM(JNIEnv *env, jobject thiz,
                                                                         jobject data_in,
                                                                         jbyteArray ps_data_out,
                                                                         jintArray pn_out_len,
                                                                         jbyteArray ps_ksn_out,
                                                                         jintArray pn_out_ksn_len,
                                                                         jbyteArray tag_data,
                                                                         jint tag_data_len,
                                                                         jbyteArray auth_data,
                                                                         jint auth_data_len) {
    ST_SEC_ENCRYPTION_DATA encryptionData;
    ST_SEC_GCM_AES_DUKPT_APPEND_DATA  stDerivateData;
    ST_SEC_GCM_APPEND_DATA stSecGcmAppendData;
    memset(&stSecGcmAppendData, 0x00, sizeof(ST_SEC_GCM_APPEND_DATA));
    memset(&stDerivateData, 0x00, sizeof(ST_SEC_DUKPT_DERIVATE_DATA));
    memset(&encryptionData, 0, sizeof(encryptionData));

    jclass dataInCls = (*env)->GetObjectClass(env, data_in);
    if (dataInCls == NULL) {
        LOGD_FMT(">>>dataInCls[%d]", dataInCls);
        return NDK_ERR_PARA;
    }
    encryptionData.ucKeyID = (*env)->GetIntField(env, data_in,
                                                 (*env)->GetFieldID(env, dataInCls, "ucKeyID",
                                                                    "I"));
    encryptionData.CipherType = (*env)->GetIntField(env, data_in,
                                                    (*env)->GetFieldID(env, dataInCls, "CipherType",
                                                                       "I"));
    encryptionData.KeyUsage = (*env)->GetIntField(env, data_in,
                                                  (*env)->GetFieldID(env, dataInCls, "KeyUsage",
                                                                     "I"));
    encryptionData.PaddingMode = (*env)->GetIntField(env, data_in,
                                                     (*env)->GetFieldID(env, dataInCls,
                                                                        "PaddingMode", "I"));
    encryptionData.unIVSize = (*env)->GetIntField(env, data_in,
                                                  (*env)->GetFieldID(env, dataInCls, "unIVSize",
                                                                     "I"));

    jobject derivateData = (*env)->GetObjectField(env, data_in,(*env)->GetFieldID(env, dataInCls, "dukptDerivateData", "Lcom/newland/nsdk/core/internal/crypto/ST_SEC_DUKPT_DERIVATE_DATA;"));
    if (derivateData != NULL) {
        jclass  class_derivateData = (*env)->GetObjectClass(env, derivateData);

        stDerivateData.KeyType = (*env)->GetIntField(env, derivateData,
                                                     (*env)->GetFieldID(env, class_derivateData, "derivateKeyType",
                                                                        "I"));
        stDerivateData.nKeyLen = (*env)->GetIntField(env, derivateData,
                                                     (*env)->GetFieldID(env, class_derivateData, "derivateKeyLen",
                                                                        "I"));
        stDerivateData.DerivateUsage = (*env)->GetIntField(env, derivateData,
                                                           (*env)->GetFieldID(env, class_derivateData, "derivateKeyUsage",
                                                                              "I"));
    }

    uchar *pIV;
    jbyteArray IV = (jbyteArray) (*env)->GetObjectField(env, data_in,
                                                        (*env)->GetFieldID(env, dataInCls, "psIV",
                                                                           "[B"));
    if (IV != NULL && encryptionData.unIVSize > 0) {
        pIV = (*env)->GetByteArrayElements(env, IV, NULL);
        encryptionData.psIV = pIV;
    }

    encryptionData.unDataInLen = (*env)->GetIntField(env, data_in,
                                                     (*env)->GetFieldID(env, dataInCls,
                                                                        "unDataInLen", "I"));
    uchar *pDataIn;
    jbyteArray dataIn = (jbyteArray) (*env)->GetObjectField(env, data_in,
                                                            (*env)->GetFieldID(env, dataInCls,
                                                                               "psDataIn", "[B"));
    if (dataIn != NULL && encryptionData.unDataInLen > 0) {
        pDataIn = (*env)->GetByteArrayElements(env, dataIn, NULL);
        encryptionData.psDataIn = pDataIn;
    }

    jbyte *p_auth_data = NULL;
    jbyte *p_tag_data = NULL;

    if (derivateData != NULL) {
        stDerivateData.authTagLen = tag_data_len;
        stDerivateData.adAuthDataLen = auth_data_len;
        if (auth_data != NULL) {
            p_auth_data = (*env)->GetByteArrayElements(env, auth_data, NULL);
            stDerivateData.adAuthData = (uchar *)p_auth_data;
        }
        if (tag_data != NULL) {
            p_tag_data = (*env)->GetByteArrayElements(env, tag_data, NULL);
            stDerivateData.authTag = (uchar *)p_tag_data;
        }
        encryptionData.pAD = &stDerivateData;
        encryptionData.unADSize = sizeof(ST_SEC_GCM_AES_DUKPT_APPEND_DATA);
        LOGD_FMT(
                ">>>derivate key type[%d] derivate key usage[%d] derivate key len[%d]",
                stDerivateData.KeyType, stDerivateData.DerivateUsage, stDerivateData.nKeyLen);
    } else {
        stSecGcmAppendData.authTagLen = tag_data_len;
        stSecGcmAppendData.adAuthDataLen = auth_data_len;
        if (auth_data != NULL) {
            p_auth_data = (*env)->GetByteArrayElements(env, auth_data, NULL);
            stSecGcmAppendData.adAuthData = (uchar *)p_auth_data;
        }
        if (tag_data != NULL) {
            p_tag_data = (*env)->GetByteArrayElements(env, tag_data, NULL);
            stSecGcmAppendData.authTag = (uchar *)p_tag_data;
        }

        encryptionData.pAD = (uchar *) &stSecGcmAppendData;
        encryptionData.unADSize = sizeof(ST_SEC_GCM_APPEND_DATA);
    }

    LOGD_FMT(
            ">>>ucKeyID[%d] CipherType[%d] KeyUsage[%d] PaddingMode[%d] unIVSize[%d] unDataInLen[%d] unADSize[%d]",
            encryptionData.ucKeyID, encryptionData.CipherType, encryptionData.KeyUsage,
            encryptionData.PaddingMode, encryptionData.unIVSize, encryptionData.unDataInLen,
            encryptionData.unADSize);

    uchar outData[4096], ksnData[32];
    int outDataLen = 0, ksnDataLen = 0;

    memset(outData, 0, sizeof(outData));
    memset(ksnData, 0, sizeof(ksnData));
    int ret = NDK_ERR;

    ret = NAPI_SecDecryption(&encryptionData, outData, &outDataLen, ksnData, &ksnDataLen);
    LOGD_FMT(">>>NAPI_SecDecryption ret[%d], outDataLen[%d]", ret, outDataLen);

    if (ret == 0) {
        if (outDataLen > 0) {
            (*env)->SetIntArrayRegion(env, pn_out_len, 0, 1, &outDataLen);
            (*env)->SetByteArrayRegion(env, ps_data_out, 0, outDataLen, outData);
        }
        if (ksnDataLen > 0) {
            (*env)->SetIntArrayRegion(env, pn_out_ksn_len, 0, 1, &ksnDataLen);
            (*env)->SetByteArrayRegion(env, ps_ksn_out, 0, ksnDataLen, ksnData);
        }
    }
    if (IV != NULL) {
        (*env)->ReleaseByteArrayElements(env, IV, pIV, 0);
    }
    if (dataIn != NULL && encryptionData.unDataInLen > 0) {
        (*env)->ReleaseByteArrayElements(env, dataIn, pDataIn, 0);
    }
    if (p_auth_data != NULL) {
        (*env)->ReleaseByteArrayElements(env, auth_data, p_auth_data, 0);
    }
    if (p_tag_data != NULL) {
        (*env)->ReleaseByteArrayElements(env, tag_data, p_tag_data, 0);
    }
    return ret;
}
/**
 * Author by wuhh, Date on 2020/2/17.
 */

#include <log.h>
#include <string.h>
#include "crypto.h"
#include "ndk.h"
#define MODE_ENCRYPT 1
#define MODE_DECRYPT 2
/*
 * Class:     com_newland_forth_module_jni_ForthJni
 * Method:    NAPI_SecGenerateKey
 * Signature: (ILjava/lang/Object;Ljava/lang/Object;)I
 */
JNIEXPORT jint JNICALL Java_com_newland_forth_module_jni_ForthJni_NAPI_1SecGenerateKey
        (JNIEnv *env, jobject obj, jint method, jobject keyDataObj, jobject kcvDataObj){

    jclass keyDataCls = (*env)->GetObjectClass(env, keyDataObj);
    if(keyDataCls == NULL){
        LOGD_FMT(">>>keyDataCls[%d]",keyDataCls);
        return NDK_ERR_PARA;
    }
    ST_SEC_KEYIN_DATA stDataIn;
    memset(&stDataIn, 0, sizeof(ST_SEC_KEYIN_DATA));

    stDataIn.ucKEKIdx = (*env)->GetIntField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "ucKEKIdx", "I"));
    stDataIn.KEKType = (*env)->GetIntField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "KEKType", "I"));
    stDataIn.KEKUsage = (*env)->GetIntField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "KEKUsage", "I"));

    stDataIn.ucKeyIdx = (*env)->GetIntField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "ucKeyIdx", "I"));
    stDataIn.KeyType = (*env)->GetIntField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "KeyType", "I"));
    stDataIn.KeyUsage = (*env)->GetIntField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "KeyUsage", "I"));

    stDataIn.CipherMode = (*env)->GetIntField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "CipherMode", "I"));
    stDataIn.PadingMode = (*env)->GetIntField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "PadingMode", "I"));

    stDataIn.nKeyLen = (*env)->GetIntField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "nKeyLen", "I"));
    stDataIn.nKeyDataLen = (*env)->GetIntField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "nKeyDataLen", "I"));

    uchar *pkeyData;
    jbyteArray keyData = (jbyteArray)(*env)->GetObjectField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "pKeyData", "[B"));
    if(keyData != NULL && stDataIn.nKeyDataLen > 0){
        pkeyData = (*env)->GetByteArrayElements(env,keyData,NULL);
        stDataIn.pKeyData = pkeyData;
        LOGD_STR("keyData",pkeyData,stDataIn.nKeyDataLen);
    }
    uchar *pIV;
    jbyteArray IV = (jbyteArray)(*env)->GetObjectField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "psIV", "[B"));
    if(IV != NULL){
        pIV = (*env)->GetByteArrayElements(env,IV,NULL);
        stDataIn.psIV = pIV;
        LOGD_STR("IV",pIV,(*env)->GetArrayLength(env,IV));
    }

    stDataIn.nKsnLen = (*env)->GetIntField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "nKsnLen", "I"));

    uchar *pksnData;
    jbyteArray ksnData = (jbyteArray)(*env)->GetObjectField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "psKsn", "[B"));
    if(ksnData != NULL && stDataIn.nKsnLen > 0){
        pksnData = (*env)->GetByteArrayElements(env,ksnData,NULL);
        stDataIn.psKsn = pksnData;
        LOGD_STR("ksnData",pksnData,stDataIn.nKsnLen);
    }

    uchar *pAD;
    stDataIn.nADSize = (*env)->GetIntField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "nADSize", "I"));
    jbyteArray AD = (jbyteArray)(*env)->GetObjectField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "pAD", "[B"));
    if(AD != NULL && stDataIn.nADSize > 0){
        pAD = (*env)->GetByteArrayElements(env,AD,NULL);
        stDataIn.pAD = pAD;
        LOGD_STR("AD",pAD,stDataIn.nADSize);
    }

    LOGD_FMT(">>>ucKEKIdx[%d] KEKType[%d] KEKUsage[%d] ucKeyIdx[%d] KeyType[%d] KeyUsage[%d] CipherMode[%d] PadingMode[%d] nKeyLen[%d] nKeyDataLen[%d]",\
             stDataIn.ucKEKIdx,stDataIn.KEKType,stDataIn.KEKUsage,stDataIn.ucKeyIdx,stDataIn.KeyType,stDataIn.KeyUsage,\
             stDataIn.CipherMode,stDataIn.PadingMode,stDataIn.nKeyLen,stDataIn.nKeyDataLen);
    LOGD_FMT(">>>keyData[%d] IV[%d] nKsnLen[%d] ksnData[%d] nADSize[%d] AD[%d]",keyData,IV,stDataIn.nKsnLen,ksnData,stDataIn.nADSize,AD);

    jclass kcvDataCls = (*env)->GetObjectClass(env, kcvDataObj);

    if(kcvDataCls == NULL){
        LOGD_FMT(">>>kcvDataCls[%d]",kcvDataCls);
        return NDK_ERR_PARA;
    }
    ST_SEC_KCV_DATA stKcvData;
    memset(&stKcvData, 0, sizeof(ST_SEC_KCV_DATA));

    stKcvData.nCheckMode = (*env)->GetIntField(env, kcvDataObj,(*env)->GetFieldID(env,kcvDataCls, "nCheckMode", "I"));
    stKcvData.nLen  = (*env)->GetIntField(env, kcvDataObj,(*env)->GetFieldID(env,kcvDataCls, "nLen", "I"));
    if (stKcvData.nLen > 3) {
        LOGD_FMT(">>>nLen[%d]",stKcvData.nLen);
        //return NDK_ERR_PARA;
    }
    jbyteArray sCheckBuf = (jbyteArray)(*env)->GetObjectField(env, kcvDataObj,(*env)->GetFieldID(env,kcvDataCls, "sCheckBuf", "[B"));
    LOGD_FMT(">>>nCheckMode[%d] nLen[%d] sCheckBuf[%d]",stKcvData.nCheckMode,stKcvData.nLen,sCheckBuf);
    uchar *pCheckBuf;
    if(sCheckBuf != NULL){
        pCheckBuf = (*env)->GetByteArrayElements(env,sCheckBuf,NULL);
        memcpy(stKcvData.sCheckBuf,pCheckBuf,stKcvData.nLen);
        LOGD_STR("kcvValue",pCheckBuf,stKcvData.nLen);
    }

    int ret = NAPI_SecGenerateKey(method,&stDataIn,&stKcvData);

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

    LOGD_FMT(">>>NAPI_SecGenerateKey ret[%d]",ret);
    return ret;
}
/*
 * Class:     com_newland_forth_module_jni_ForthJni
 * Method:    NAPI_SecDeleteKey
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_newland_forth_module_jni_ForthJni_NAPI_1SecDeleteKey
        (JNIEnv *env, jobject obj, jint keyId, jint keyType, jint keyUsage){
    LOGD_FMT(">>>keyId[%d] keyType[%d] keyUsage[%d]",keyId,keyType,keyUsage);
    int ret = NAPI_SecDeleteKey(keyId,keyType,keyUsage);
    LOGD_FMT(">>>NAPI_SecDeleteKey ret[%d]",ret);
    return ret;
}
/*
 * Class:     com_newland_forth_module_jni_ForthJni
 * Method:    NAPI_SecGetKeyInfo
 * Signature: (IIII[BI[B[I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_forth_module_jni_ForthJni_NAPI_1SecGetKeyInfo
        (JNIEnv *env, jobject obj, jint infoID, jint keyId, jint keyType, jint keyUsage, jbyteArray AD, jint adSize, jbyteArray outInfo, jintArray outInfoLen){
    uchar *pAD = NULL;
    if(AD != NULL){
        pAD = (*env)->GetByteArrayElements(env,AD,0);
    }
    uchar outBuf[512];int outLen;
    memset(outBuf, 0, sizeof(outBuf));
    LOGD_FMT(">>>infoID[%d] keyId[%d] keyType[%d] keyUsage[%d] pAD[%d] adSize[%d]",infoID, keyId, keyType, keyUsage, pAD, adSize);
    int ret = NAPI_SecGetKeyInfo(infoID,keyId,keyType,keyUsage,pAD,adSize,outBuf,&outLen);
    if(ret == 0){
        (*env)->SetIntArrayRegion(env,outInfoLen,0,1,&outLen);
        (*env)->SetByteArrayRegion(env,outInfo,0,outLen,outBuf);
    }
    if(AD!=NULL){
        (*env)->ReleaseByteArrayElements(env,AD,pAD,0);
    }
    LOGD_FMT(">>>NAPI_SecGetKeyInfo ret[%d]",ret);
    return ret;
}

/*
 * Class:     com_newland_forth_module_jni_ForthJni
 * Method:    NDK_SecSetKeyOwner
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_newland_forth_module_jni_ForthJni_NDK_1SecSetKeyOwner
        (JNIEnv *env, jobject obj, jstring name){
    char *buf = (*env)->GetStringUTFChars(env,name,0);
    int ret = NDK_SecSetKeyOwner(buf);
    (*env)->ReleaseStringUTFChars(env,name,buf);
    LOGD_FMT(">>>NDK_SecSetKeyOwner ret[%d]",ret);
    return ret;
}

/*
 * Class:     com_newland_forth_module_jni_ForthJni
 * Method:    NAPI_SecGetKeyOwner
 * Signature: (I[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_forth_module_jni_ForthJni_NAPI_1SecGetKeyOwner
        (JNIEnv *env, jobject obj, jint maxLen, jbyteArray owner){
    char outBuf[1024];
    memset(outBuf, 0, sizeof(outBuf));
    LOGD_FMT(">>>maxLen[%d]",maxLen);
    int ret = NAPI_SecGetKeyOwner(maxLen,outBuf);
    if(ret == 0){
        (*env)->SetByteArrayRegion(env,owner,0,maxLen,outBuf);
    }
    LOGD_FMT(">>>NAPI_SecGetKeyOwner ret[%d]",ret);
    return ret;
}

/*
 * Class:     com_newland_forth_module_jni_ForthJni
 * Method:    NAPI_SecGenerateMAC
 * Signature: (II[BI[BI[BI[B[I[B[I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_forth_module_jni_ForthJni_NAPI_1SecGenerateMAC
        (JNIEnv *env, jobject obj, jint MacType, jint ucKeyID, jbyteArray IV, jint unIVSize, jbyteArray dataIn, jint nDataInLen, jbyteArray AD, jint unADSize,
         jbyteArray psMacOut, jintArray pnOutLen, jbyteArray psKsnOut, jintArray nOutKsnLen){
    uchar *pIV = NULL;
    if(IV != NULL){
        pIV = (*env)->GetByteArrayElements(env,IV,0);
    }
    uchar *pDataIn = NULL;
    if(dataIn != NULL){
        pDataIn = (*env)->GetByteArrayElements(env,dataIn,0);
    }

    uchar *pAD = NULL;
    if(AD != NULL){
        pAD = (*env)->GetByteArrayElements(env,AD,0);
    }

    uchar outData[256],ksnData[32];
    int outDataLen = 0,ksnDataLen = 0;

    memset(outData,0, sizeof(outData));
    memset(ksnData,0, sizeof(ksnData));

    LOGD_FMT(">>>MacType[%d] ucKeyID[%d] unIVSize[%d] nDataInLen[%d] pAD[%d] unADSize[%d]",
             MacType, ucKeyID, unIVSize, nDataInLen, pAD, unADSize);
    LOGD_STR("pIV",pIV,unIVSize);
    LOGD_STR("pDataIn",pDataIn,nDataInLen);
    LOGD_STR("pAD",pAD,unADSize);
    int ret = NAPI_SecGenerateMAC(MacType,ucKeyID,pIV,unIVSize,pDataIn,nDataInLen,pAD,unADSize,outData,&outDataLen,ksnData,&ksnDataLen);
    if(ret == 0){
        if(outDataLen > 0){
            (*env)->SetIntArrayRegion(env,pnOutLen,0,1,&outDataLen);
            (*env)->SetByteArrayRegion(env,psMacOut,0,outDataLen,outData);
        }
        if(ksnDataLen > 0){
            (*env)->SetIntArrayRegion(env,nOutKsnLen,0,1,&ksnDataLen);
            (*env)->SetByteArrayRegion(env,psKsnOut,0,ksnDataLen,ksnData);
        }
    }
    if(IV != NULL){
        (*env)->ReleaseByteArrayElements(env,IV,pIV,0);
    }
    if(dataIn != NULL){
        (*env)->ReleaseByteArrayElements(env,dataIn,pDataIn,0);
    }
    if(AD != NULL ){
        (*env)->ReleaseByteArrayElements(env,AD,pAD,0);
    }
    LOGD_FMT(">>>NAPI_SecGenerateMAC ret[%d]",ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_forth_module_jni_ForthJni_NAPI_1SecGenerateMAC_1DerivateKey(JNIEnv *env,
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

static int calculate(int mode,JNIEnv *env, jobject obj, jobject dataInObj, jbyteArray psDataOut, jintArray pnOutLen, jbyteArray psKsnOut, jintArray pnOutKsnLen){
    ST_SEC_ENCRYPTION_DATA encryptionData;
    ST_SEC_DUKPT_DERIVATE_DATA  stDerivateData;

    memset(&stDerivateData, 0x00, sizeof(ST_SEC_DUKPT_DERIVATE_DATA));
    memset(&encryptionData, 0x00, sizeof(encryptionData));

    jclass dataInCls = (*env)->GetObjectClass(env, dataInObj);
    if(dataInCls == NULL){
        LOGD_FMT(">>>dataInCls[%d]",dataInCls);
        return NDK_ERR_PARA;
    }
    encryptionData.ucKeyID = (*env)->GetIntField(env, dataInObj,(*env)->GetFieldID(env,dataInCls, "ucKeyID", "I"));
    encryptionData.CipherType = (*env)->GetIntField(env, dataInObj,(*env)->GetFieldID(env,dataInCls, "CipherType", "I"));
    encryptionData.KeyUsage = (*env)->GetIntField(env, dataInObj,(*env)->GetFieldID(env,dataInCls, "KeyUsage", "I"));
    encryptionData.PaddingMode = (*env)->GetIntField(env, dataInObj,(*env)->GetFieldID(env,dataInCls, "PaddingMode", "I"));
    encryptionData.unIVSize = (*env)->GetIntField(env, dataInObj,(*env)->GetFieldID(env,dataInCls, "unIVSize", "I"));

    uchar *pIV;
    jbyteArray IV = (jbyteArray)(*env)->GetObjectField(env, dataInObj,(*env)->GetFieldID(env,dataInCls, "psIV", "[B"));
    if(IV != NULL && encryptionData.unIVSize > 0){
        pIV = (*env)->GetByteArrayElements(env,IV,NULL);
        encryptionData.psIV = pIV;
        LOGD_STR("IV",pIV,encryptionData.unIVSize);
    }

    encryptionData.unDataInLen = (*env)->GetIntField(env, dataInObj,(*env)->GetFieldID(env,dataInCls, "unDataInLen", "I"));
    uchar *pDataIn;
    jbyteArray dataIn = (jbyteArray)(*env)->GetObjectField(env, dataInObj,(*env)->GetFieldID(env,dataInCls, "psDataIn", "[B"));
    if(dataIn != NULL && encryptionData.unDataInLen > 0){
        pDataIn = (*env)->GetByteArrayElements(env,dataIn,NULL);
        encryptionData.psDataIn = pDataIn;
        LOGD_STR("dataIn",pDataIn,encryptionData.unDataInLen);
    }

    encryptionData.unADSize = (*env)->GetIntField(env, dataInObj,(*env)->GetFieldID(env,dataInCls, "unADSize", "I"));
    uchar *pAD;
    jbyteArray AD = (jbyteArray)(*env)->GetObjectField(env, dataInObj,(*env)->GetFieldID(env,dataInCls, "pAD", "[B"));

    jobject derivateData = (*env)->GetObjectField(env, dataInObj,(*env)->GetFieldID(env, dataInCls, "dukptDerivateData", "Lcom/newland/forth/module/crypto/cipher/ST_SEC_DUKPT_DERIVATE_DATA;"));
    if (derivateData != NULL) {
        jclass  class_derivateData = (*env)->GetObjectClass(env, derivateData);
        stDerivateData.KeyType = (*env)->GetIntField(env, derivateData,(*env)->GetFieldID(env, class_derivateData, "derivateKeyType","I"));
        stDerivateData.nKeyLen = (*env)->GetIntField(env, derivateData,(*env)->GetFieldID(env, class_derivateData, "derivateKeyLen","I"));
        stDerivateData.DerivateUsage = (*env)->GetIntField(env, derivateData,(*env)->GetFieldID(env, class_derivateData, "derivateKeyUsage","I"));
    }

    if (derivateData != NULL) {
        encryptionData.pAD = &stDerivateData;
        encryptionData.unADSize = sizeof(ST_SEC_DUKPT_DERIVATE_DATA);
        LOGD_FMT(">>>derivate key type.[%d] derivate key usage[%d] derivate key len[%d]",
                stDerivateData.KeyType, stDerivateData.DerivateUsage, stDerivateData.nKeyLen);
    } else if(AD != NULL && encryptionData.unADSize > 0){
        pAD = (*env)->GetByteArrayElements(env,AD,NULL);
        encryptionData.pAD = pAD;
        LOGD_STR("AD",pAD,encryptionData.unADSize);
    }
    LOGD_FMT(">>>mode[%d] ucKeyID[%d] CipherType[%d] KeyUsage[%d] PaddingMode[%d] unIVSize[%d] IV[%d] unDataInLen[%d] dataIn[%d] unADSize[%d] AD[%d]",
            mode,encryptionData.ucKeyID,encryptionData.CipherType,encryptionData.KeyUsage,encryptionData.PaddingMode,encryptionData.unIVSize,IV,encryptionData.unDataInLen,dataIn,encryptionData.unADSize,encryptionData.pAD);

    uchar outData[4096],ksnData[32];
    int outDataLen = 0,ksnDataLen = 0;

    memset(outData,0, sizeof(outData));
    memset(ksnData,0, sizeof(ksnData));
    int ret = NDK_ERR;
    if(mode == MODE_ENCRYPT){
        ret = NAPI_SecEncryption(&encryptionData,outData,&outDataLen,ksnData,&ksnDataLen);
    } else{
        ret = NAPI_SecDecryption(&encryptionData,outData,&outDataLen,ksnData,&ksnDataLen);
    }
    if(ret == 0){
        if(outDataLen > 0){
            (*env)->SetIntArrayRegion(env,pnOutLen,0,1,&outDataLen);
            (*env)->SetByteArrayRegion(env,psDataOut,0,outDataLen,outData);
        }
        if(ksnDataLen > 0){
            (*env)->SetIntArrayRegion(env,pnOutKsnLen,0,1,&ksnDataLen);
            (*env)->SetByteArrayRegion(env,psKsnOut,0,ksnDataLen,ksnData);
        }
    }
    if(IV != NULL){
        (*env)->ReleaseByteArrayElements(env,IV,pIV,0);
    }
    if(dataIn != NULL && encryptionData.unDataInLen > 0 ){
        (*env)->ReleaseByteArrayElements(env,dataIn,pDataIn,NULL);
    }
    if(AD != NULL && encryptionData.unADSize > 0){
        (*env)->ReleaseByteArrayElements(env,AD,pAD,NULL);
    }
    if(mode == MODE_ENCRYPT){
        LOGD_FMT(">>>NAPI_SecEncryption ret[%d]",ret);
    } else{
        LOGD_FMT(">>>NAPI_SecDecryption ret[%d]",ret);
    }
    return ret;
}
/*
 * Class:     com_newland_forth_module_jni_ForthJni
 * Method:    NAPI_SecEncryption
 * Signature: (Ljava/lang/Object;[B[I[B[I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_forth_module_jni_ForthJni_NAPI_1SecEncryption
        (JNIEnv *env, jobject obj, jobject dataInObj, jbyteArray psDataOut, jintArray pnOutLen, jbyteArray psKsnOut, jintArray pnOutKsnLen){
    return calculate(MODE_ENCRYPT,env,obj,dataInObj,psDataOut,pnOutLen,psKsnOut,pnOutKsnLen);
}

/*
 * Class:     com_newland_forth_module_jni_ForthJni
 * Method:    NAPI_SecDecryption
 * Signature: (Ljava/lang/Object;[B[I[B[I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_forth_module_jni_ForthJni_NAPI_1SecDecryption
        (JNIEnv *env, jobject obj, jobject dataInObj, jbyteArray psDataOut, jintArray pnOutLen, jbyteArray psKsnOut, jintArray pnOutKsnLen){
    return calculate(MODE_DECRYPT,env,obj,dataInObj,psDataOut,pnOutLen,psKsnOut,pnOutKsnLen);
}

/*
 * Class:     com_newland_forth_module_jni_ForthJni
 * Method:    NDK_SecVppTpInit
 * Signature: ([B[B[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_forth_module_jni_ForthJni_NDK_1SecVppTpInit
        (JNIEnv *env, jobject obj, jbyteArray numBtn, jbyteArray funcKey, jbyteArray outSeq){
    uchar *btn = NULL;
    if(numBtn != NULL){
        btn = (*env)->GetByteArrayElements(env,numBtn,JNI_FALSE);
        LOGD_FMT(">>>numBtn len[%d]",(*env)->GetArrayLength(env,numBtn));
        LOGD_STR("numBtn",btn,(*env)->GetArrayLength(env,numBtn));
    }
    uchar *key = NULL;
    if(funcKey != NULL){
        key = (*env)->GetByteArrayElements(env,funcKey,JNI_FALSE);
        LOGD_FMT(">>>funcKey len[%d]",(*env)->GetArrayLength(env,funcKey));
        LOGD_STR("funcKey",key,(*env)->GetArrayLength(env,funcKey));
    }
    uchar keyBuf[20];uchar *keySeq = NULL;
    memset(keyBuf,0, sizeof(keyBuf));
    if(outSeq != NULL){
        keySeq = keyBuf;
    }
    int ret = NDK_SecVppTpInit(btn,key,keySeq);
    if(ret == 0 && outSeq != NULL){
        LOGD_STR("key",keyBuf,strlen(keyBuf));
        (*env)->SetByteArrayRegion(env,outSeq,0,strlen(keyBuf),keyBuf);
    }
    if(numBtn != NULL) {
        (*env)->ReleaseByteArrayElements(env, numBtn, btn, 0);
    }
    if(funcKey != NULL){
        (*env)->ReleaseByteArrayElements(env,funcKey,key,0);
    }
    LOGD_FMT(">>>NDK_SecVppTpInit ret[%d]",ret);
    return ret;
}

/*
 * Class:     com_newland_forth_module_jni_ForthJni
 * Method:    NAPI_SecVPPInit
 * Signature: (III[BIILjava/lang/Object;[BI)I
 */
JNIEXPORT jint JNICALL Java_com_newland_forth_module_jni_ForthJni_NAPI_1SecVPPInit
        (JNIEnv *env, jobject obj, jint SessionType, jint KeyType, jint ucKeyIdx, jstring pan, jint PINBlockFmt, jint unTimeOut, jobject RSAKeyObj, jbyteArray AD, jint unADSize){
    char *pPan = NULL;
    if(pan != NULL){
        pPan = (*env)->GetStringUTFChars(env,pan,0);
    }
    uchar *pAD = NULL;
    if(AD != NULL){
        pAD = (*env)->GetByteArrayElements(env,AD,JNI_FALSE);
    }
    ST_NAPI_RSA_KEY RSAKey;
    memset(&RSAKey,0, sizeof(ST_NAPI_RSA_KEY));
    ST_NAPI_RSA_KEY *pRSAKey = NULL;

    jclass RSAKeyCls;
    if(RSAKeyObj != NULL){
        RSAKeyCls = (*env)->GetObjectClass(env, RSAKeyObj);
        pRSAKey = &RSAKey;
        RSAKey.usBits = (*env)->GetIntField(env, RSAKeyObj,(*env)->GetFieldID(env,RSAKeyCls, "usBits", "I"));
        LOGD_FMT(">>>usBits[%d]",RSAKey.usBits);
        uchar *psModulus;
        jbyteArray Modulus = (jbyteArray)(*env)->GetObjectField(env, RSAKeyObj,(*env)->GetFieldID(env,RSAKeyCls, "sModulus", "[B"));
        if(Modulus != NULL){
            psModulus = (*env)->GetByteArrayElements(env,Modulus,NULL);
            int len = (*env)->GetArrayLength(env,Modulus);
            LOGD_FMT(">>>Modulus len[%d]",len);
            if(len > 0 && len <= MAX_RSA_MODULUS_LEN){
                memcpy(RSAKey.sModulus,psModulus,len);
                LOGD_STR("Modulus",RSAKey.sModulus,len);
            }
            (*env)->ReleaseByteArrayElements(env, Modulus, psModulus, 0);
        }

        uchar *psExponent;
        jbyteArray Exponent = (jbyteArray)(*env)->GetObjectField(env, RSAKeyObj,(*env)->GetFieldID(env,RSAKeyCls, "sExponent", "[B"));
        if(Exponent != NULL){
            psExponent = (*env)->GetByteArrayElements(env,Exponent,NULL);
            int len = (*env)->GetArrayLength(env,Exponent);
            LOGD_FMT(">>>Exponent len[%d]",len);
            if(len > 0 && len <= MAX_RSA_MODULUS_LEN){
                memcpy(RSAKey.sExponent,psExponent,len);
                LOGD_STR("Exponent",RSAKey.sExponent,len);
            }
            (*env)->ReleaseByteArrayElements(env, Exponent, psExponent, 0);
        }
    }
    LOGD_FMT(">>>SessionType[%d] KeyType[%d] ucKeyIdx[%d] PINBlockFmt[%d] unTimeOut[%d] pRSAKey[%d] pAD[%d] unADSize[%d]",SessionType,KeyType,ucKeyIdx,PINBlockFmt,unTimeOut,pRSAKey,pAD,unADSize);
    LOGD_FMT(">>>Pan[%s]",pPan);
    int ret = NAPI_SecVPPInit(SessionType,KeyType,ucKeyIdx,pPan,PINBlockFmt,unTimeOut,pRSAKey,pAD,unADSize);
    if(pan!=NULL){
        (*env)->ReleaseStringUTFChars(env,pan,pPan);
    }
    if(AD !=NULL){
        (*env)->ReleaseByteArrayElements(env,AD,pAD,JNI_FALSE);
    }
    LOGD_FMT(">>>NAPI_SecVPPInit ret[%d]",ret);
    return ret;
}

/*
 * Class:     com_newland_forth_module_jni_ForthJni
 * Method:    NAPI_SecVPPGetEvent
 * Signature: ([I[B[I[B[I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_forth_module_jni_ForthJni_NAPI_1SecVPPGetEvent
        (JNIEnv *env, jobject obj, jintArray nEvent, jbyteArray psPinBlock, jintArray pnOutPinLen, jbyteArray psKsn, jintArray pnOutKsnLen){
    uint event;
    uchar pinBlock[32];int pinBlockLen = 0;
    uchar ksn[32];int ksnLen = 0;
    memset(pinBlock,0, sizeof(pinBlock));
    memset(ksn,0, sizeof(ksn));
    int ret = NAPI_SecVPPGetEvent(&event,pinBlock,&pinBlockLen,ksn,&ksnLen);
    if(ret == NDK_OK){
//        if(event != SEC_VPP_KEY_NULL){
            LOGD_FMT(">>>event[%d] pinBlockLen[%d] ksnLen[%d]",event,pinBlockLen,ksnLen);
            LOGD_STR("pinBlock",pinBlock,pinBlockLen);
            LOGD_STR("ksn",ksn,ksnLen);
//        }
        if(nEvent!=NULL){
            (*env)->SetIntArrayRegion(env,nEvent,0,1,&event);
        }
        if(psPinBlock!=NULL && pnOutPinLen!=NULL){
            (*env)->SetIntArrayRegion(env,pnOutPinLen,0,1,&pinBlockLen);
            (*env)->SetByteArrayRegion(env,psPinBlock,0,pinBlockLen,&pinBlock);
        }
        if(psKsn!=NULL && pnOutKsnLen!=NULL){
            (*env)->SetIntArrayRegion(env,pnOutKsnLen,0,1,&ksnLen);
            (*env)->SetByteArrayRegion(env,psKsn,0,ksnLen,&ksn);
        }
    }
    return ret;
}

/*
 * Class:     com_newland_forth_module_jni_ForthJni
 * Method:    NAPI_SecVPPSetEvent
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_forth_module_jni_ForthJni_NAPI_1SecVPPSetEvent
        (JNIEnv *env, jobject obj, jint key){
    LOGD_FMT(">>>key[%d]",key);
    int ret = NAPI_SecVPPSetEvent(key);
    LOGD_FMT(">>>NAPI_SecVPPSetEvent ret[%d]",ret);
    return ret;
}


/*
 * Class:     com_newland_forth_module_jni_ForthJni
 * Method:    NAPI_SecVPPSetExpPinLenIn
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_newland_forth_module_jni_ForthJni_NAPI_1SecVPPSetExpPinLenIn
        (JNIEnv *env, jobject obj, jstring pinLenIn){
    char *pPinLenIn = NULL;
    if(pinLenIn != NULL){
        pPinLenIn = (*env)->GetStringUTFChars(env,pinLenIn,0);
        LOGD_FMT(">>>pinLenIn[%s]",pPinLenIn);
    }
    int ret = NAPI_SecVPPSetExpPinLenIn(pPinLenIn);
    if(pinLenIn!=NULL){
        (*env)->ReleaseStringUTFChars(env,pinLenIn,pPinLenIn);
    }
    LOGD_FMT(">>>NAPI_SecVPPSetExpPinLenIn ret[%d]",ret);
    return ret;
}

/*
 * Class:     com_newland_forth_module_jni_ForthJni
 * Method:    isSupportNapi
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_newland_forth_module_jni_ForthJni_isSupportNapi
        (JNIEnv *env, jobject obj){
    return getSupportNapi();
}

JNIEXPORT jint JNICALL
Java_com_newland_forth_module_jni_ForthJni_NAPI_1SecResetCertStatus(JNIEnv *env, jobject thiz) {
    return NAPI_SecResetCertStatus();
}

JNIEXPORT jint JNICALL
Java_com_newland_forth_module_jni_ForthJni_NAPI_1SecLoadTrustedCert(JNIEnv *env, jobject thiz,
                                                                    jchar is_ca, jbyteArray cert,
                                                                    jint certlen, jbyteArray pubkey,
                                                                    jintArray pubkeylen) {
    unsigned char cetdata[2048] = {0};
    int ret = -1;
    uchar *ppubkey = NULL;
    int *lenvalue = NULL;
    uchar *pcetdata = NULL;

    if(pubkey != NULL){
        ppubkey = (uchar *) (*env)->GetByteArrayElements(env, pubkey,NULL);
    }
    if (pubkeylen != NULL){
        lenvalue = (int *) (*env)->GetIntArrayElements(env, pubkeylen, NULL);
    }
    if(cert != NULL){
        pcetdata = (uchar *) (*env)->GetByteArrayElements(env, cert,NULL);
    }
    memcpy(cetdata, pcetdata, certlen);
    ret = NAPI_SecLoadTrustedCert(is_ca, (char *) cetdata, certlen, ppubkey, lenvalue);

    if (lenvalue != NULL){
        (*env)->ReleaseIntArrayElements(env, pubkeylen, (jint *) lenvalue,0);
    }
    if (ppubkey != NULL){
        (*env)->ReleaseByteArrayElements(env, pubkey, (jbyte *) ppubkey,0);
    }
    if (pcetdata != NULL){
        (*env)->ReleaseByteArrayElements(env, cert, (jbyte *) pcetdata,0);
    }
    return (jint)ret;
}

//JNIEXPORT jint JNICALL
//Java_com_newland_forth_module_jni_ForthJni_NAPI_1SecVppRNIBTpInit(JNIEnv *env, jobject thiz,
//                                                                  jbyteArray key_info, jint key_num,
//                                                                  jbyteArray ts_area,
//                                                                  jbyteArray keypad_area) {
//    uchar *pkey_info = NULL;
//    if(key_info != NULL){
//        pkey_info = (*env)->GetByteArrayElements(env,key_info,JNI_FALSE);
//    }
//    uchar *pts_area = NULL;
//    if(ts_area != NULL){
//        pts_area = (*env)->GetByteArrayElements(env,ts_area,JNI_FALSE);
//    }
//    uchar *pkeypad_area = NULL;
//    if(keypad_area != NULL){
//        pkeypad_area = (*env)->GetByteArrayElements(env,keypad_area,JNI_FALSE);
//    }
//    int ret = NAPI_SecVppRNIBTpInit(pkey_info,key_num,ts_area,keypad_area);
//    if(key_info != NULL) {
//        (*env)->ReleaseByteArrayElements(env, key_info, pkey_info, 0);
//    }
//    if(ts_area != NULL) {
//        (*env)->ReleaseByteArrayElements(env, ts_area, pts_area, 0);
//    }
//    if(keypad_area != NULL) {
//        (*env)->ReleaseByteArrayElements(env, keypad_area, pkeypad_area, 0);
//    }
//
//    LOGD_FMT(">>>NDK_SecVppTpInit ret[%d]",ret);
//    return ret;
//}
JNIEXPORT jint JNICALL
Java_com_newland_forth_module_jni_ForthJni_NAPI_1SecVppRNIBTpInit(JNIEnv *env, jobject thiz,
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

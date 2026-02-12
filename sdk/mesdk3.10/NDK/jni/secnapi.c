/**
 * @Description
 * @Author wuhh
 * @Date 2020/11/26
 */
#include <jni.h>
#include <unistd.h>
#include <NDK.h>
#include "__log.h"
#include "napi_crypto.h"
#include "napi_crypto_extd.h"

static void msleep(int ms)
{
    usleep(ms*1000);
}
/*
 * Class:     com_newland_forth_module_jni_ForthJni
 * Method:    NAPI_SecGenerateKey
 * Signature: (ILjava/lang/Object;Ljava/lang/Object;)I
 */
JNIEXPORT jint JNICALL
Java_com_newland_ndk_napi_SecNapi_NAPI_1SecGenerateKey(JNIEnv *env, jobject obj, jint method,
                                                       jobject keyDataObj,
                                                       jobject kcvDataObj){

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
        LOGD_STR("keyData-",pkeyData,stDataIn.nKeyDataLen);
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

    if (keyData != NULL && stDataIn.nKeyDataLen > 0){
        (*env)->ReleaseByteArrayElements(env, keyData, pkeyData, NULL);
    }

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
 * Method:    NAPI_SecGetKeyInfo
 * Signature: (IIII[BI[B[I)I
 */
JNIEXPORT jint JNICALL
Java_com_newland_ndk_napi_SecNapi_NAPI_1SecGetKeyInfo(JNIEnv *env, jobject obj, jint infoID,
                                                      jint keyId, jint keyType, jint keyUsage,
                                                      jbyteArray AD, jint adSize, jbyteArray outInfo,
                                                      jintArray outInfoLen){
    uchar *pAD = NULL;
    if(AD != NULL){
        pAD = (*env)->GetByteArrayElements(env,AD,0);
    }
    uchar outBuf[6*1024+1];int outLen;
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

JNIEXPORT jint JNICALL
Java_com_newland_ndk_napi_SecNapi_NAPI_1SecGenerateAsymKey0(JNIEnv *env, jobject thiz,
                                                            jint key_type, jint key_usage,
                                                            jint key_idx, jint n_adsize,
                                                            jbyteArray AD) {
    LOGD_FMT("key_type[%d] key_usage[%d] key_idx[%d] n_adsize[%d] AD[%d]",key_type,key_usage,key_idx,n_adsize,AD);
    NAPI_HANDLE handle;
    ST_SEC_ASYM_KEY_INFO asymKeyInfo;
    memset(&asymKeyInfo,0, sizeof(ST_SEC_ASYM_KEY_INFO));
    uchar *pAD = NULL;
    if(AD != NULL){
        pAD = (*env)->GetByteArrayElements(env,AD,0);
    }
    asymKeyInfo.KeytType = key_type;
    asymKeyInfo.KeyUsage = key_usage;
    asymKeyInfo.KeyIdx = key_idx;

    ST_SEC_ASYM_ALG_INFO asymAlgInfo;
    memset(&asymAlgInfo,0, sizeof(ST_SEC_ASYM_ALG_INFO));
    if(n_adsize != 0 && pAD != NULL){
        memcpy(&asymAlgInfo,pAD,n_adsize);
        LOGD_FMT("asymAlgInfo.unBit[%d]",asymAlgInfo.unBit);
        LOGD_STR("asymAlgInfo.ucRsaPubExp",asymAlgInfo.ucRsaPubExp, sizeof(asymAlgInfo.ucRsaPubExp));
    }

    int ret = NAPI_SecGenerateAsymKey(&handle,&asymKeyInfo,n_adsize,pAD);
    LOGD_FMT("NAPI_SecGenerateAsymKey ret[%d]",ret);
    if(ret == NAPI_OK){
        while (1){
            int ret = NAPI_SecGenerateAsymKeyState(handle);
            LOGD_FMT("NAPI_SecGenerateAsymKeyState ret[%d]",ret);
            if(ret < NAPI_OK){
                break;
            }
            if(ret > NAPI_OK){
                msleep(100);
            }
            if(ret == NAPI_OK){
                break;
            }
        }
    }
    ret = NAPI_SecCancelGenerateAsymKey(handle);
    LOGD_FMT("NAPI_SecCancelGenerateAsymKey ret[%d]",ret);

    if(AD!=NULL){
        (*env)->ReleaseByteArrayElements(env,AD,pAD,0);
    }
    LOGD_FMT("NAPI_SecGenerateAsymKey ret[%d]",ret);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_napi_SecNapi_NAPI_1SecAsymEncryption0(JNIEnv *env, jobject thiz, jint key_type,
                                                           jint key_usage, jint key_idx,
                                                           jint md_type, jint encodingMode,
                                                           jint crypto_mode,
                                                           jint nDataInLen,
                                                           jbyteArray psDataIn,
                                                           jintArray pnDataOutLen,
                                                           jbyteArray psDataOut) {
    LOGD_FMT("key_type[%d] key_usage[%d] key_idx[%d] md_type[%d] encodingMode[%d] crypto_mode[%d] nDataInLen[%d]",
             key_type,key_usage,key_idx,md_type,encodingMode,crypto_mode,nDataInLen);
    char *pDataIn = NULL;
    if(psDataIn != NULL){
        pDataIn = (*env)->GetByteArrayElements(env,psDataIn,0);
    }
    ST_SEC_ASYM_KEY_INFO asymKeyInfo;
    memset(&asymKeyInfo,0, sizeof(ST_SEC_ASYM_KEY_INFO));
    asymKeyInfo.KeytType = key_type;
    asymKeyInfo.KeyUsage = key_usage;
    asymKeyInfo.KeyIdx = key_idx;
    int dataOutLen = 0;
    uchar dataOut[1024*4];
    int ret = NAPI_SecAsymEncryption(&asymKeyInfo,md_type,encodingMode,crypto_mode,nDataInLen,pDataIn,&dataOutLen,dataOut);
    LOGD_FMT("NAPI_SecAsymEncryption ret[%d]",ret);
    if(ret == NAPI_OK){
        (*env)->SetIntArrayRegion(env,pnDataOutLen,0,1,&dataOutLen);
        (*env)->SetByteArrayRegion(env,psDataOut,0,dataOutLen,&dataOut);
    }
    if(psDataIn != NULL){
        (*env)->ReleaseByteArrayElements(env,psDataIn,pDataIn,0);
    }
    return ret;
}


JNIEXPORT jint JNICALL
Java_com_newland_ndk_napi_SecNapi_NAPI_1SecAsymDecryption0(JNIEnv *env, jobject thiz, jint key_type,
                                                           jint key_usage, jint key_idx,
                                                           jint md_type, jint encodingMode,
                                                           jint crypto_mode,
                                                           jint nDataInLen,
                                                           jbyteArray psDataIn,
                                                           jintArray pnDataOutLen,
                                                           jbyteArray psDataOut) {
    LOGD_FMT("key_type[%d] key_usage[%d] key_idx[%d] md_type[%d] encodingMode[%d] crypto_mode[%d] nDataInLen[%d]",
            key_type,key_usage,key_idx,md_type,encodingMode,crypto_mode,nDataInLen);
    char *pDataIn = NULL;
    if(psDataIn != NULL){
        pDataIn = (*env)->GetByteArrayElements(env,psDataIn,0);
    }
    ST_SEC_ASYM_KEY_INFO asymKeyInfo;
    memset(&asymKeyInfo,0, sizeof(ST_SEC_ASYM_KEY_INFO));
    asymKeyInfo.KeytType = key_type;
    asymKeyInfo.KeyUsage = key_usage;
    asymKeyInfo.KeyIdx = key_idx;
    int dataOutLen = 0;
    uchar dataOut[1024*4];
    int ret = NAPI_SecAsymDecryption(&asymKeyInfo,md_type,encodingMode,crypto_mode,nDataInLen,pDataIn,&dataOutLen,dataOut);
    LOGD_FMT("NAPI_SecAsymDecryption ret[%d]",ret);
    if(ret == NAPI_OK){
        (*env)->SetIntArrayRegion(env,pnDataOutLen,0,1,&dataOutLen);
        (*env)->SetByteArrayRegion(env,psDataOut,0,dataOutLen,&dataOut);
    }
    if(psDataIn != NULL){
        (*env)->ReleaseByteArrayElements(env,psDataIn,pDataIn,0);
    }
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_napi_SecNapi_NAPI_1SecAsymSign0(JNIEnv *env, jobject thiz, jint key_type,
                                                    jint key_usage, jint key_idx, jint md_type,
                                                    jint encoding_mode, jint n_hash_len,
                                                    jbyteArray ps_hash, jintArray n_sig_len,
                                                    jbyteArray ps_sig) {
    LOGD_FMT("key_type[%d] key_usage[%d] key_idx[%d] md_type[%d] encoding_mode[%d] n_hash_len[%d]",
             key_type,key_usage,key_idx,md_type,encoding_mode,n_hash_len);
    char *pDataIn = NULL;
    if(ps_hash != NULL){
        pDataIn = (*env)->GetByteArrayElements(env,ps_hash,0);
    }
    ST_SEC_ASYM_KEY_INFO asymKeyInfo;
    memset(&asymKeyInfo,0, sizeof(ST_SEC_ASYM_KEY_INFO));
    asymKeyInfo.KeytType = key_type;
    asymKeyInfo.KeyUsage = key_usage;
    asymKeyInfo.KeyIdx = key_idx;
    int dataOutLen = 0;
    uchar dataOut[1024*4];
    int ret = NAPI_SecAsymSign(&asymKeyInfo,md_type,encoding_mode,n_hash_len,pDataIn,&dataOutLen,dataOut);
    LOGD_FMT("NAPI_SecAsymSign ret[%d]",ret);
    if(ret == NAPI_OK){
        (*env)->SetIntArrayRegion(env,n_sig_len,0,1,&dataOutLen);
        (*env)->SetByteArrayRegion(env,ps_sig,0,dataOutLen,&dataOut);
    }
    if(ps_hash != NULL){
        (*env)->ReleaseByteArrayElements(env,ps_hash,pDataIn,0);
    }
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_napi_SecNapi_NAPI_1SecAsymVerify0(JNIEnv *env, jobject thiz, jint key_type,
                                                       jint key_usage, jint key_idx, jint md_type,
                                                       jint encoding_mode, jint n_hash_len,
                                                       jbyteArray ps_hash, jint n_sig_len,
                                                       jbyteArray ps_sig) {
    LOGD_FMT("key_type[%d] key_usage[%d] key_idx[%d] md_type[%d] encoding_mode[%d] n_hash_len[%d] n_sig_len[%d]",
             key_type,key_usage,key_idx,md_type,encoding_mode,n_hash_len,n_sig_len);
    char *pDataIn = NULL;
    if(ps_hash != NULL){
        pDataIn = (*env)->GetByteArrayElements(env,ps_hash,0);
    }
    char *pSignData = NULL;
    if(ps_sig != NULL){
        pSignData = (*env)->GetByteArrayElements(env,ps_sig,0);
    }
    ST_SEC_ASYM_KEY_INFO asymKeyInfo;
    memset(&asymKeyInfo,0, sizeof(ST_SEC_ASYM_KEY_INFO));
    asymKeyInfo.KeytType = key_type;
    asymKeyInfo.KeyUsage = key_usage;
    asymKeyInfo.KeyIdx = key_idx;
    int ret = NAPI_SecAsymVerify(&asymKeyInfo,md_type,encoding_mode,n_hash_len,pDataIn,n_sig_len,pSignData);
    LOGD_FMT("NAPI_SecAsymVerify ret[%d]",ret);
    if(ps_hash != NULL){
        (*env)->ReleaseByteArrayElements(env,ps_hash,pDataIn,0);
    }
    if(ps_sig != NULL){
        (*env)->ReleaseByteArrayElements(env,ps_sig,pSignData,0);
    }
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_napi_SecNapi_NAPI_1SecAsymGenerateKey0(JNIEnv *env, jobject thiz, jint method,
                                                            jobject keyDataObj,
                                                            jobject kcvDataObj) {
    LOGD_FMT("method[%d]",method);
    jclass keyDataCls = (*env)->GetObjectClass(env, keyDataObj);
    if(keyDataCls == NULL){
        LOGD_FMT(">>>keyDataCls[%d]",keyDataCls);
        return NDK_ERR_PARA;
    }
    ST_SEC_ASYM_KEYIN_DATA stDataIn;
    memset(&stDataIn, 0, sizeof(ST_SEC_ASYM_KEYIN_DATA));
    stDataIn.ucKEKIdx = (*env)->GetIntField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "ucKEKIdx", "I"));
    stDataIn.KEKType = (*env)->GetIntField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "KEKType", "I"));
    stDataIn.KEKUsage = (*env)->GetIntField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "KEKUsage", "I"));
    stDataIn.ucKeyIdx = (*env)->GetIntField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "ucKeyIdx", "I"));
    stDataIn.KeyType = (*env)->GetIntField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "KeyType", "I"));
    stDataIn.KeyUsage = (*env)->GetIntField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "KeyUsage", "I"));
    stDataIn.MdAlg = (*env)->GetIntField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "MdAlg", "I"));
    stDataIn.EncodingMode = (*env)->GetIntField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "EncodingMode", "I"));
    stDataIn.nKeyLen = (*env)->GetIntField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "nKeyLen", "I"));

    uchar *pkeyData;
    jbyteArray keyData = (jbyteArray)(*env)->GetObjectField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "pKeyData", "[B"));
    if(keyData != NULL && stDataIn.nKeyLen > 0){
        pkeyData = (*env)->GetByteArrayElements(env,keyData,NULL);
        stDataIn.pKeyData = pkeyData;
        LOGD_STR("keyData",pkeyData,stDataIn.nKeyLen);
    }
     stDataIn.nKsnLen = (*env)->GetIntField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "nKsnLen", "I"));
    uchar *pksnData;
    jbyteArray ksnData = (jbyteArray)(*env)->GetObjectField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "psKsn", "[B"));
    if(ksnData != NULL && stDataIn.nKsnLen > 0){
        pksnData = (*env)->GetByteArrayElements(env,ksnData,NULL);
        stDataIn.psKsn = pksnData;
        LOGD_STR("ksnData",pksnData,stDataIn.nKsnLen);
    }
    stDataIn.nADSize = (*env)->GetIntField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "nADSize", "I"));
    uchar *pAD;
    stDataIn.nADSize = (*env)->GetIntField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "nADSize", "I"));
    jbyteArray AD = (jbyteArray)(*env)->GetObjectField(env, keyDataObj,(*env)->GetFieldID(env,keyDataCls, "pAD", "[B"));
    if(AD != NULL && stDataIn.nADSize > 0){
        pAD = (*env)->GetByteArrayElements(env,AD,NULL);
        stDataIn.pAD = pAD;
        LOGD_STR("AD",pAD,stDataIn.nADSize);
    }
    LOGD_FMT(">>>ucKEKIdx[%d] KEKType[%d] KEKUsage[%d] ucKeyIdx[%d] KeyType[%d] KeyUsage[%d] "\
             "MdAlg[%d] EncodingMode[%d] nKeyLen[%d] nKsnLen[%d] nADSize[%d]",\
             stDataIn.ucKEKIdx,stDataIn.KEKType,stDataIn.KEKUsage,stDataIn.ucKeyIdx,stDataIn.KeyType,stDataIn.KeyUsage,\
             stDataIn.MdAlg,stDataIn.EncodingMode,stDataIn.nKeyLen,stDataIn.nKsnLen,stDataIn.nADSize);

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

    int ret = NAPI_SecAsymGenerateKey(method,&stDataIn,&stKcvData);

    if (keyData != NULL)
        (*env)->ReleaseByteArrayElements(env, keyData, pkeyData, NULL);

    if (ksnData != NULL)
        (*env)->ReleaseByteArrayElements(env, ksnData, pksnData, NULL);

    if (AD != NULL)
        (*env)->ReleaseByteArrayElements(env, AD, pAD, NULL);

    if (sCheckBuf != NULL)
        (*env)->ReleaseByteArrayElements(env, sCheckBuf, pCheckBuf, NULL);

    LOGD_FMT(">>>NAPI_SecAsymGenerateKey ret[%d]",ret);
    return ret;
}
JNIEXPORT jint JNICALL
Java_com_newland_ndk_napi_SecNapi_NAPI_1SecResetCertStatus(JNIEnv *env, jobject thiz) {
    return NAPI_SecResetCertStatus();
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_napi_SecNapi_NAPI_1SecLoadTrustedCert(JNIEnv *env, jobject thiz, jchar is_ca,
                                                           jbyteArray cert, jint certlen,
                                                           jbyteArray pubkey, jintArray pubkeylen) {
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

JNIEXPORT jint JNICALL
Java_com_newland_ndk_napi_SecNapi_NAPI_1SecInitAtomic(JNIEnv *env, jobject thiz) {
    return NAPI_SecInitAtomic();
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_napi_SecNapi_NAPI_1SecCommitAtomic(JNIEnv *env, jobject thiz, jchar status) {
    return NAPI_SecCommitAtomic(status);
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_napi_SecNapi_NewlandV1SecAsymGenerateKey(JNIEnv *env, jclass clazz,
                                                              jint RDH_ENC_ID, jint RKLA_SK_ID,
                                                              jbyteArray sk_data, jint sk_type,
                                                              jint sk_len, jintArray ad_value) {
    int ret = -1;
    uchar *skOutData = NULL;
    int *skOutLen = NULL;

    if(sk_data != NULL){
        skOutData = (uchar *) (*env)->GetByteArrayElements(env, sk_data,NULL);
    }

    if (ad_value != NULL){
        skOutLen = (int *) (*env)->GetIntArrayElements(env, ad_value, NULL);
    }

    ST_SEC_KCV_DATA stKcvData;
    ST_SEC_ASYM_KEYIN_DATA stKGData;

    memset(&stKGData, 0x0, sizeof(ST_SEC_ASYM_KEYIN_DATA));
    memset(&stKcvData, 0x0, sizeof(ST_SEC_KCV_DATA));

    stKGData.ucKEKIdx = RDH_ENC_ID;
    stKGData.KEKType = KEY_TYPE_ASYM_RSA;
    stKGData.KEKUsage = KEY_USE_ASYM_KEY_DISTRIBUTION;

    stKGData.ucKeyIdx = RKLA_SK_ID;
    stKGData.KeyUsage = KEY_USE_TR31_KEK;
    stKGData.KeyType = (EM_SEC_CRYPTO_KEY_TYPE) sk_type;
    stKGData.pKeyData = skOutData;
    stKGData.nKeyLen = sk_len;
    stKGData.MdAlg = SEC_MD_SHA256;

    stKGData.EncodingMode = ASYM_RSA_PKCS_V21;
    stKGData.pAD = skOutLen;

    stKcvData.nCheckMode = SEC_KCV_NONE;
    stKcvData.nLen = 0;

    ret = NAPI_SecAsymGenerateKey(SEC_KIM_RANDOM_OUT, &stKGData, &stKcvData);

    if (skOutData != NULL){
        (*env)->ReleaseByteArrayElements(env, sk_data, (jbyte *) skOutData,0);
    }
    if (skOutLen != NULL){
        (*env)->ReleaseIntArrayElements(env, ad_value, (jint *) skOutLen,0);
    }
    return (jint)ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_napi_SecNapi_NAPI_1SecVPPSetButtonFunc(JNIEnv *env, jobject thiz, jint button,
                                                            jint fun) {
    return NAPI_SecVPPSetButtonFunc(button,fun);
}

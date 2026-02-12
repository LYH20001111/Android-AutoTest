#include <jni.h>
#include <stdint.h>
#include <string.h>
#include "log.h"
#include "extemv.h"
#include "emvl3.h"

/**
 * Author by bxy, Date on 2019/3/31 0022.
 */
JavaVM *gJavaVM = NULL;
jobject g_commLisObj;
jmethodID g_commLisMid;
static int init(JNIEnv *env, jobject obj, jbyteArray configuration,jobject listener) {
    LOGD_FMT("");
    uchar *config = (*env)->GetByteArrayElements(env,configuration,NULL);
    if(config == NULL){
        LOGD_FMT(">>>config[%d]",config);
        return -1;
    }
    int configLen = (*env)->GetArrayLength(env,configuration);
    if(configLen!=8){
        LOGD_FMT(">>>configLen[%d]",configLen);
        return -1;
    }
    LOGD_STR("config",config,configLen);
    if(g_commLisObj != NULL)
        (*env)->DeleteGlobalRef(env,g_commLisObj);
    g_commLisObj = (*env)->NewGlobalRef(env, listener);
    jclass cls=  (*env)->GetObjectClass(env, listener);
    g_commLisMid = (*env)->GetMethodID(env,cls,"Communication","([B)[B");

    int ret = NAPI_L3Init(NULL,config);
    LOGD_FMT("init ret[%d]",ret);
    (*env)->ReleaseByteArrayElements(env,configuration,config,NULL);
    return ret;
}

static int loadTerminalConfig(JNIEnv *env, jobject obj, jint cardIntf, jbyteArray tlvList, jint len, jint mode) {
    LOGD_FMT("");
    cardIntf = cardIntf+1;
    LOGD_FMT(">>>cardIntf[%d]",cardIntf);
    uchar *tlvData = (*env)->GetByteArrayElements(env,tlvList,NULL);
    if(tlvData == NULL){
        LOGD_FMT(">>>tlvData[%d]",tlvData);
        return -1;
    }
    int tlvLen = (*env)->GetArrayLength(env,tlvList);
    if(tlvData!=NULL&&tlvLen>0){
        LOGD_STR("tlvData",tlvData,tlvLen);
    }
    int ret = NAPI_L3LoadTerminalConfig(cardIntf,tlvData,&tlvLen,mode);
    LOGD_FMT("loadTerminalConfig ret[%d]",ret);
    (*env)->ReleaseByteArrayElements(env,tlvList,tlvData,NULL);
    return ret;
}

static int loadAIDConfig(JNIEnv *env, jobject obj, jint cardIntf, jobject aidObj, jbyteArray tlvList, jintArray len, jint mode) {
    LOGD_FMT("");
    cardIntf = cardIntf+1;
    LOGD_FMT(">>>cardIntf[%d] mode[%d]",cardIntf,mode);
    //upt     in:tlv
    //get     in:aid out:tlv
    //remove  in:aid
    //flush
    jbyteArray aid;jfieldID aidId;uchar *paid;
    jbyte aidLen;jfieldID aidLenId;
    jbyteArray kernelId;jfieldID kernelIdId;uchar *pkernelId;
    jbyte externCheckFlag;jfieldID externCheckFlagId;
    jbyte transactionType;jfieldID transactionTypeId;
    jstring externString;jfieldID externStringId;uchar *pexternString;
    jbyte  externStrLen;jfieldID externStrLenId;
    L3_AID_ENTRY l3AidEntry;
    memset(&l3AidEntry,0, sizeof(L3_AID_ENTRY));
    uchar *tlvData;uint *tlvLen;
    if(mode == CONFIG_UPT){
        tlvData = (*env)->GetByteArrayElements(env,tlvList,NULL);
        if(tlvData == NULL){
            LOGD_FMT(">>>tlvData[%d]",tlvData);
            return -1;
        }
        tlvLen = (*env)->GetIntArrayElements(env,len,NULL);
        if(tlvLen == NULL){
            LOGD_FMT(">>>tlvLen[%d]",tlvLen);
            return -1;
        }
        LOGD_STR("tlvData",tlvData,tlvLen[0]);

    }else if((mode == CONFIG_GET||mode == CONFIG_RMV) && aidObj != NULL){
        jclass aidCls = (*env)->GetObjectClass(env, aidObj);
        if(aidCls == NULL){
            LOGD_FMT(">>>aidCls[%d]",aidCls);
            return -1;
        }
        aid = (jbyteArray)(*env)->GetObjectField(env, aidObj, aidId = (*env)->GetFieldID(env, aidCls, "aid", "[B"));
        aidLen = (*env)->GetByteField(env, aidObj, aidLenId = (*env)->GetFieldID(env, aidCls, "aidLen", "B"));
        kernelId = (jbyteArray)(*env)->GetObjectField(env, aidObj, kernelIdId = (*env)->GetFieldID(env, aidCls, "kernelId", "[B"));
        externCheckFlag = (*env)->GetByteField(env, aidObj,externCheckFlagId = (*env)->GetFieldID(env, aidCls, "externCheckFlag", "B"));
        transactionType = (*env)->GetByteField(env, aidObj, transactionTypeId = (*env)->GetFieldID(env, aidCls, "transactionType", "B"));
        externString = (jstring)(*env)->GetObjectField(env, aidObj, externStringId = (*env)->GetFieldID(env, aidCls, "externString", "Ljava/lang/String;"));
        externStrLen = (*env)->GetByteField(env, aidObj, externStrLenId = (*env)->GetFieldID(env, aidCls, "externStrLen", "B"));

        if(aid != NULL){
            paid = (*env)->GetByteArrayElements(env, aid, NULL);
            memcpy(l3AidEntry.aid,paid,(*env)->GetArrayLength(env,aid));
            LOGD_STR("aid",l3AidEntry.aid,aidLen);
        }
        l3AidEntry.aidLen = aidLen;
        if(kernelId != NULL){
            pkernelId = (*env)->GetByteArrayElements(env, kernelId, NULL);
            memcpy(l3AidEntry.kernelId,pkernelId,(*env)->GetArrayLength(env,kernelId));
            LOGD_STR("kernelId",l3AidEntry.kernelId,(*env)->GetArrayLength(env,kernelId));
        }
        l3AidEntry.externCheckFlag = externCheckFlag;
        l3AidEntry.transactionType = transactionType;
        if(externString != NULL){
            pexternString = (*env)->GetStringUTFChars(env,externString,NULL);
            l3AidEntry.externString = pexternString;
        }
        l3AidEntry.externStrLen = externStrLen;

        LOGD_FMT(">>>aidLen[%d] externCheckFlag[%d] transactionType[%d] externStrLen[%d]",l3AidEntry.aidLen,l3AidEntry.externCheckFlag,l3AidEntry.transactionType,l3AidEntry.externStrLen);

    }
    int ret = -1;
    uchar getTlvData[2048];uint getTlvLen;
    if(mode == CONFIG_GET){
        ret = NAPI_L3LoadAIDConfig(cardIntf,&l3AidEntry,getTlvData,&getTlvLen,mode);
        if(ret > 0){
//        getTlvLen = 0x03;
//        getTlvData[0] = 0x11;
//        getTlvData[1] = 0x22;
//        getTlvData[2] = 0x33;
            (*env)->SetIntArrayRegion(env, len, 0, 1, &getTlvLen);
            (*env)->SetByteArrayRegion(env, tlvList, 0,getTlvLen,getTlvData);
        }
    }else{
        ret = NAPI_L3LoadAIDConfig(cardIntf,&l3AidEntry,tlvData,&(tlvLen[0]),mode);
    }
    LOGD_FMT("loadAIDConfig ret[%d]",ret);
    if(mode == CONFIG_UPT){
        (*env)->ReleaseByteArrayElements(env,tlvList,tlvData,NULL);
        (*env)->ReleaseIntArrayElements(env,len,tlvLen,NULL);
    }else if(mode == CONFIG_GET||mode == CONFIG_RMV){
        if(aid != NULL)
            (*env)->ReleaseByteArrayElements(env,aid,paid,NULL);
        if(kernelId != NULL)
            (*env)->ReleaseByteArrayElements(env,kernelId,pkernelId,NULL);
        if(externString != NULL)
            (*env)->ReleaseStringUTFChars(env,externString,pexternString);
    }
    return ret;
}

static int loadCAPK(JNIEnv *env, jobject obj, jobject capkObj, jint mode) {
    LOGD_FMT("");
    jbyteArray pkModulus;uchar *ppkModulus;jfieldID pkModulusId;
    uchar pkModulusLen;jfieldID pkModulusLenId;
    jbyteArray pkExponent;uchar *ppkExponent;jfieldID pkExponentId;
    jbyteArray hashValue;uchar *phashValue;jfieldID hashValueId;
    jbyteArray expiredDate;uchar *pexpiredDate;jfieldID expiredDateId;
    jbyteArray rid;uchar *prid;jfieldID ridId;
    uchar index;jfieldID indexId;
    uchar pkAlgorithmIndicator;jfieldID pkAlgorithmIndicatorId;
    uchar hashAlgorithmIndicator;jfieldID hashAlgorithmIndicatorId;
    jbyteArray rfu;uchar *prfu;jfieldID rfuId;

    L3_CAPK_ENTRY l3CapkEntry;
    memset(&l3CapkEntry,0, sizeof(L3_CAPK_ENTRY));
    if(mode == CONFIG_UPT || mode == CONFIG_GET || mode == CONFIG_RMV){
        jclass capkCls = (*env)->GetObjectClass(env, capkObj);
        if(capkCls == NULL){
            LOGD_FMT(">>>capkCls[%d]",capkCls);
            return -1;
        }
        pkModulus = (jbyteArray)(*env)->GetObjectField(env, capkObj,pkModulusId = (*env)->GetFieldID(env,capkCls, "pkModulus", "[B"));
        pkModulusLen = (*env)->GetByteField(env, capkObj,pkModulusLenId = (*env)->GetFieldID(env,capkCls, "pkModulusLen", "B"));
        pkExponent = (jbyteArray)(*env)->GetObjectField(env, capkObj,pkExponentId = (*env)->GetFieldID(env,capkCls, "pkExponent", "[B"));
        hashValue = (jbyteArray)(*env)->GetObjectField(env, capkObj,hashValueId = (*env)->GetFieldID(env,capkCls, "hashValue", "[B"));
        expiredDate = (jbyteArray)(*env)->GetObjectField(env, capkObj,expiredDateId = (*env)->GetFieldID(env,capkCls, "expiredDate", "[B"));
        rid = (jbyteArray)(*env)->GetObjectField(env, capkObj,ridId = (*env)->GetFieldID(env,capkCls, "rid", "[B"));
        index = (*env)->GetByteField(env, capkObj,indexId = (*env)->GetFieldID(env,capkCls, "index", "B"));
        pkAlgorithmIndicator = (*env)->GetByteField(env, capkObj, pkAlgorithmIndicatorId = (*env)->GetFieldID(env,capkCls, "pkAlgorithmIndicator", "B"));
        hashAlgorithmIndicator = (*env)->GetByteField(env, capkObj, hashAlgorithmIndicatorId = (*env)->GetFieldID(env,capkCls, "hashAlgorithmIndicator", "B"));
        rfu = (jbyteArray)(*env)->GetObjectField(env, capkObj,rfuId = (*env)->GetFieldID(env,capkCls, "rfu", "[B"));

        if(pkModulus != NULL){
            ppkModulus = (*env)->GetByteArrayElements(env,pkModulus,NULL);
            memcpy(l3CapkEntry.pkModulus,ppkModulus,(*env)->GetArrayLength(env,pkModulus));
            LOGD_STR("pkModulus",l3CapkEntry.pkModulus,(*env)->GetArrayLength(env,pkModulus));
        }
        l3CapkEntry.pkModulusLen =  pkModulusLen;
        if(pkExponent!= NULL){
            ppkExponent = (*env)->GetByteArrayElements(env,pkExponent,NULL);
            memcpy(l3CapkEntry.pkExponent,ppkExponent,(*env)->GetArrayLength(env,pkExponent));
            LOGD_STR("pkExponent",l3CapkEntry.pkExponent,(*env)->GetArrayLength(env,pkExponent));
        }
        if(hashValue!= NULL){
            phashValue = (*env)->GetByteArrayElements(env,hashValue,NULL);
            memcpy(l3CapkEntry.hashValue,phashValue,(*env)->GetArrayLength(env,hashValue));
            LOGD_STR("hashValue",l3CapkEntry.hashValue,(*env)->GetArrayLength(env,hashValue));
        }
        if(expiredDate!= NULL){
            pexpiredDate = (*env)->GetByteArrayElements(env,expiredDate,NULL);
            memcpy(l3CapkEntry.expiredDate,pexpiredDate,(*env)->GetArrayLength(env,expiredDate));
            LOGD_STR("expiredDate",l3CapkEntry.expiredDate,(*env)->GetArrayLength(env,expiredDate));
        }
        if(rid != NULL){
            prid = (*env)->GetByteArrayElements(env,rid,NULL);
            memcpy(l3CapkEntry.rid,prid,(*env)->GetArrayLength(env,rid));
            LOGD_STR("rid",l3CapkEntry.rid,(*env)->GetArrayLength(env,rid));
        }
        l3CapkEntry.index = index;
        l3CapkEntry.pkAlgorithmIndicator = pkAlgorithmIndicator;
        l3CapkEntry.hashAlgorithmIndicator = hashAlgorithmIndicator;
        if(rfu!= NULL){
            prfu = (*env)->GetByteArrayElements(env,rfu,NULL);
            memcpy(l3CapkEntry.rfu,prfu,(*env)->GetArrayLength(env,rfu));
            LOGD_STR("rfu",l3CapkEntry.rfu,(*env)->GetArrayLength(env,rfu));
        }
        LOGD_FMT(">>>pkModulusLen[%d] index[%d] pkAlgorithmIndicator[%d] hashAlgorithmIndicator[%d]",l3CapkEntry.pkModulusLen,l3CapkEntry.index,l3CapkEntry.pkAlgorithmIndicator,l3CapkEntry.hashAlgorithmIndicator);
    }
    int ret = NAPI_L3LoadCAPK(&l3CapkEntry,mode);
    if(mode == CONFIG_GET && ret > 0){
//        memset(&l3CapkEntry,0,sizeof(L3_CAPK_ENTRY));
//        l3CapkEntry.pkModulus[0] = 0x01;
//        l3CapkEntry.pkModulusLen = 0x02;
//        l3CapkEntry.pkExponent[0] = 0x03;
//        l3CapkEntry.hashValue[0] = 0x04;
//        l3CapkEntry.expiredDate[0] = 0x05;
//        l3CapkEntry.rid[0] = 0x06;
//        l3CapkEntry.index = 77;
//        l3CapkEntry.pkAlgorithmIndicator = 0x08;
//        l3CapkEntry.hashAlgorithmIndicator = 0x09;
//        l3CapkEntry.rfu[0] = 0x0A;

        (*env)->SetByteArrayRegion(env, pkModulus,0,248,l3CapkEntry.pkModulus);
        (*env)->SetByteField(env,capkObj,pkModulusLenId,l3CapkEntry.pkModulusLen);

        (*env)->SetByteArrayRegion(env, pkExponent,0,3,l3CapkEntry.pkExponent);
        (*env)->SetByteArrayRegion(env, hashValue,0,20,l3CapkEntry.hashValue);
        (*env)->SetByteArrayRegion(env, expiredDate,0,4,l3CapkEntry.expiredDate);
        (*env)->SetByteArrayRegion(env, rid,0,5,l3CapkEntry.rid);
        (*env)->SetByteField(env,capkObj,indexId,l3CapkEntry.index);

        (*env)->SetByteField(env,capkObj,pkAlgorithmIndicatorId,l3CapkEntry.pkAlgorithmIndicator);
        (*env)->SetByteField(env,capkObj,hashAlgorithmIndicatorId,l3CapkEntry.hashAlgorithmIndicator);

        (*env)->SetByteArrayRegion(env, rfu,0,4,l3CapkEntry.rfu);
        LOGD_FMT("");
        LOGD_FMT(">>>pkModulusLen[%d] index[%d] pkAlgorithmIndicator[%d] hashAlgorithmIndicator[%d]",l3CapkEntry.pkModulusLen,l3CapkEntry.index,l3CapkEntry.pkAlgorithmIndicator,l3CapkEntry.hashAlgorithmIndicator);
        LOGD_STR("pkModulus",l3CapkEntry.pkModulus,(*env)->GetArrayLength(env,pkModulus));
        LOGD_STR("pkExponent",l3CapkEntry.pkExponent,(*env)->GetArrayLength(env,pkExponent));
        LOGD_STR("hashValue",l3CapkEntry.hashValue,(*env)->GetArrayLength(env,hashValue));
        LOGD_STR("expiredDate",l3CapkEntry.expiredDate,(*env)->GetArrayLength(env,expiredDate));
        LOGD_STR("rid",l3CapkEntry.rid,(*env)->GetArrayLength(env,rid));
        LOGD_STR("rfu",l3CapkEntry.rfu,(*env)->GetArrayLength(env,rfu));
    }
    if(mode == CONFIG_UPT || mode == CONFIG_RMV){
        if (pkModulus != NULL)
            (*env)->ReleaseByteArrayElements(env, pkModulus, ppkModulus, NULL);
        if (pkExponent != NULL)
            (*env)->ReleaseByteArrayElements(env, pkExponent, ppkExponent, NULL);
        if (hashValue != NULL)
            (*env)->ReleaseByteArrayElements(env, hashValue, phashValue, NULL);
        if (expiredDate != NULL)
            (*env)->ReleaseByteArrayElements(env, expiredDate, pexpiredDate, NULL);
        if (rid != NULL)
            (*env)->ReleaseByteArrayElements(env, rid, prid, NULL);
        if (rfu != NULL)
            (*env)->ReleaseByteArrayElements(env, rfu, prfu, NULL);
    } else if(mode == CONFIG_GET) {
        (*env)->DeleteLocalRef(env, pkModulus);
        (*env)->DeleteLocalRef(env, pkExponent);
        (*env)->DeleteLocalRef(env, hashValue);
        (*env)->DeleteLocalRef(env, expiredDate);
        (*env)->DeleteLocalRef(env, rid);
        (*env)->DeleteLocalRef(env, rfu);
    }
    LOGD_FMT("loadCAPK ret[%d]",ret);
    return ret;
}

static int loadRevocationList(JNIEnv *env, jobject obj, jobject crl, jint mode) {
    LOGD_FMT(">>>mode[%d]",mode);
    L3_CRL_ENTRY crlEntry;
    memset(&crlEntry,0, sizeof(L3_CRL_ENTRY));
    jbyteArray rid;uchar *prid;
    jbyte index;jfieldID indexId;
    jbyteArray csn;uchar *pcsn;
    jbyteArray rfu;uchar *prfu;
    jclass crlCls = (*env)->GetObjectClass(env, crl);
    if(crlCls == NULL){
        LOGD_FMT(">>>crlCls[%d]",crlCls);
        return -1;
    }
    rid = (jbyteArray)(*env)->GetObjectField(env, crl, (*env)->GetFieldID(env,crlCls, "rid", "[B"));
    index = (*env)->GetByteField(env, crl, indexId = (*env)->GetFieldID(env,crlCls, "index", "B"));
    csn = (jbyteArray)(*env)->GetObjectField(env, crl, (*env)->GetFieldID(env,crlCls, "csn", "[B"));
    rfu = (jbyteArray)(*env)->GetObjectField(env, crl, (*env)->GetFieldID(env,crlCls, "rfu", "[B"));

    if(rid != NULL){
        prid = (*env)->GetByteArrayElements(env,rid,NULL);
        memcpy(crlEntry.rid,prid,(*env)->GetArrayLength(env,rid));
        LOGD_STR("rid",crlEntry.rid,(*env)->GetArrayLength(env,rid));
    }
    crlEntry.index = index;

    if(csn != NULL){
        pcsn = (*env)->GetByteArrayElements(env,csn,NULL);
        memcpy(crlEntry.csn,pcsn,(*env)->GetArrayLength(env,csn));
        LOGD_STR("csn",crlEntry.csn,(*env)->GetArrayLength(env,csn));
    }
    if(rfu != NULL){
        prfu = (*env)->GetByteArrayElements(env,rfu,NULL);
        memcpy(crlEntry.rfu,prfu,(*env)->GetArrayLength(env,rfu));
        LOGD_STR("rfu",crlEntry.rfu,(*env)->GetArrayLength(env,rfu));
    }

    int ret = NAPI_L3LoadRevocationList(&crlEntry,mode);
    if(rid != NULL){
        (*env)->ReleaseByteArrayElements(env, rid, rid, NULL);
    }
    if(csn != NULL){
        (*env)->ReleaseByteArrayElements(env, csn, pcsn, NULL);
    }
    if(rfu != NULL){
        (*env)->ReleaseByteArrayElements(env, rfu, prfu, NULL);
    }
    LOGD_FMT(">>>loadRevocationList ret[%d]",ret);
    return ret;
}

static int loadExceptionList(JNIEnv *env, jobject obj, jobject exceptionList, jint mode) {
    LOGD_FMT(">>>mode[%d]",mode);
    L3_EXCEPTION_FILE_ENTRY exceptionFileEntry;
    memset(&exceptionFileEntry,0, sizeof(L3_EXCEPTION_FILE_ENTRY));

    jbyteArray pan;uchar *ppan;
    jbyte panLen;jfieldID panLenId;
    jbyte panSN;jfieldID panSNId;
    jbyteArray rfu;uchar *prfu;


    jclass exceptionCls = (*env)->GetObjectClass(env, exceptionList);
    if(exceptionCls == NULL){
        LOGD_FMT(">>>exceptionCls[%d]",exceptionCls);
        return -1;
    }

    pan = (jbyteArray)(*env)->GetObjectField(env, exceptionList, (*env)->GetFieldID(env,exceptionCls, "pan", "[B"));
    panLen = (*env)->GetByteField(env, exceptionList, panLenId = (*env)->GetFieldID(env,exceptionCls, "panLen", "B"));
    panSN = (*env)->GetByteField(env, exceptionList, panSNId = (*env)->GetFieldID(env,exceptionCls, "panSN", "B"));
    rfu = (jbyteArray)(*env)->GetObjectField(env, exceptionList, (*env)->GetFieldID(env,exceptionCls, "rfu", "[B"));

    if(pan != NULL){
        ppan = (*env)->GetByteArrayElements(env,pan,NULL);
        memcpy(exceptionFileEntry.pan,ppan,(*env)->GetArrayLength(env,pan));
        LOGD_STR("pan",exceptionFileEntry.pan,(*env)->GetArrayLength(env,pan));
    }

    exceptionFileEntry.panLen = panLen;
    exceptionFileEntry.panSN = panSN;

    if(rfu != NULL){
        prfu = (*env)->GetByteArrayElements(env,rfu,NULL);
        memcpy(exceptionFileEntry.rfu,prfu,(*env)->GetArrayLength(env,rfu));
        LOGD_STR("rfu",exceptionFileEntry.rfu,(*env)->GetArrayLength(env,rfu));
    }
    int ret = NAPI_L3LoadExceptionList(&exceptionFileEntry,mode);

    if(pan != NULL) {
        (*env)->ReleaseByteArrayElements(env, pan, ppan, NULL);
    }
    if(rfu != NULL){
        (*env)->ReleaseByteArrayElements(env, rfu, prfu, NULL);
    }
    LOGD_FMT(">>>loadExceptionList ret[%d]",ret);
    return ret;
}

static int performTransaction(JNIEnv *env, jobject obj, jbyteArray data, jint dataLen, jintArray res) {
    LOGD_FMT("");
    uchar *tranData = (*env)->GetByteArrayElements(env,data,NULL);
    uint *resData = (*env)->GetIntArrayElements(env,res,NULL);
    if(data != NULL){
        LOGD_STR("value",data,dataLen);
    }
    L3_TXN_RES txnRes;
    int ret = NAPI_L3PerformTransaction(tranData,dataLen,&txnRes);
    (*env)->SetIntArrayRegion(env,res,0,1,resData);
    LOGD_FMT("performTransaction ret[%d]",ret);
    (*env)->ReleaseByteArrayElements(env,data,tranData,NULL);
    (*env)->ReleaseIntArrayElements(env,res,resData,NULL);
    return ret;
}

static int completeTransaction(JNIEnv *env, jobject obj, jbyteArray data, jint dataLen, jintArray res) {
    LOGD_FMT("");
    uchar *tranData = (*env)->GetByteArrayElements(env,data,NULL);
    uint *resData = (*env)->GetIntArrayElements(env,res,NULL);
    if(data != NULL){
        LOGD_STR("value",data,dataLen);
    }
    L3_TXN_RES txnRes;
    int ret = NAPI_L3CompleteTransaction(tranData,dataLen,&txnRes);
    (*env)->SetIntArrayRegion(env,res,0,1,resData);
    LOGD_FMT("completeTransaction ret[%d]",ret);
    (*env)->ReleaseByteArrayElements(env,data,tranData,NULL);
    (*env)->ReleaseIntArrayElements(env,res,resData,NULL);
    return ret;
}

static int terminateTransaction(JNIEnv *env, jobject obj) {
    LOGD_FMT("");
    int ret = NAPI_L3TerminateTransaction();
    LOGD_FMT("terminateTransaction ret[%d]",ret);
    return ret;
}

static int cancelTransaction(JNIEnv *env, jobject obj) {
    LOGD_FMT("");
    int ret = NAPI_L3CancelTransaction();
    LOGD_FMT("cancelTransaction ret[%d]",ret);
    return ret;
}

static int setData(JNIEnv *env, jobject obj, jint tag, jbyteArray data, jint len) {
    LOGD_FMT("");
    uchar *value = (*env)->GetByteArrayElements(env,data,NULL);
    LOGD_FMT(">>>tag[0x%x]",tag);
    LOGD_STR("value",value,len);
    int ret = NAPI_L3SetData(tag,value,len);
    LOGD_FMT("setData ret[%d]",ret);
    (*env)->ReleaseByteArrayElements(env,data,value,NULL);
    return ret;
}

static int getData(JNIEnv *env, jobject obj, jint type, jbyte keyIndex, jbyteArray data, jint maxlen,jintArray realLen) {
    LOGD_FMT("");
    uchar tempValue[152];
    memset(tempValue,0, sizeof(tempValue));
    int outLen = 0;
    LOGD_FMT(">>>type[%d]",type);
    int ret = NAPI_L3GetData(type,0,tempValue,maxlen,&outLen);
    (*env)->SetIntArrayRegion(env,realLen,0,1,&outLen);
    (*env)->SetByteArrayRegion(env,data,0,outLen,tempValue);
    LOGD_FMT("getData ret[%d]",ret);
    return ret;
}

static int setTLVData(JNIEnv *env, jobject obj, jbyteArray tlvList, jint len) {
    LOGD_FMT("");
    LOGD_STR("tlvList",tlvList,len);
    uchar *value = (*env)->GetByteArrayElements(env,tlvList,NULL);
    int ret = NAPI_L3SetTLVData(value,len);
    (*env)->ReleaseByteArrayElements(env,tlvList,value,NULL);
    LOGD_FMT("setTLVData ret[%d]",ret);
    return ret;
}

static int getTlvData(JNIEnv *env, jobject obj, jbyteArray tagList, jint tagNum, jbyte keyIndex, jbyteArray tlvData, jint manLen, jint ctl,jintArray realLen) {
    uchar *tags = (*env)->GetByteArrayElements(env,tagList,NULL);
    uchar tlvValue[1024];
    uint tlvLen=0;
    int ret = NAPI_L3GetTlvData(tags,tagNum,keyIndex,tlvValue,manLen,ctl,&tlvLen);
    (*env)->SetIntArrayRegion(env,realLen,0,1,&tlvLen);
    (*env)->SetByteArrayRegion(env,tlvData,0,tlvLen,tlvValue);
    (*env)->ReleaseByteArrayElements(env,tagList,tags,NULL);
    LOGD_FMT("getTlvData ret[%d]",ret);
    return ret;
}

static int setDebugMode(JNIEnv *env, jobject obj, jint level) {
    LOGD_FMT("");
    int ret = NAPI_L3SetDebugMode(level);
    LOGD_FMT("setDebugMode ret[%d]",ret);
    return ret;
}

static int getVersion(JNIEnv *env, jobject obj, jint module, jbyteArray version) {
    LOGD_FMT("");
    uchar ver[64];
    memset(ver,0,sizeof(ver));
    uchar *value = (*env)->GetByteArrayElements(env,version,NULL);
    int ret = NAPI_L3GetVersion(module,ver);
    (*env)->SetByteArrayRegion(env,version,0,strlen(ver),ver);
    LOGD_FMT("getVersion ret[%d]",ret);
    return ret;
}

static const JNINativeMethod methods[] = {
        {"NAPI_L3Init",                 "([BLcom/newland/sdk/module/extpinpademv/CommunicationListener;)I", (void *) init},
        {"NAPI_L3LoadTerminalConfig",   "(I[BII)I",                                                         (void *) loadTerminalConfig},
        {"NAPI_L3LoadAIDConfig",        "(ILcom/newland/sdk/module/extpinpademv/AIDEntry;[B[II)I",          (void *) loadAIDConfig},
        {"NAPI_L3LoadCAPK",             "(Lcom/newland/sdk/module/extpinpademv/CAPKEntry;I)I",              (void *) loadCAPK},
        {"NAPI_L3PerformTransaction",   "([BI[I)I",                                                         (void *) performTransaction},
        {"NAPI_L3CompleteTransaction",  "([BI[I)I",                                                         (void *) completeTransaction},
        {"NAPI_L3TerminateTransaction", "()I",                                                              (void *) terminateTransaction},
        {"NAPI_L3CancelTransaction",    "()I",                                                              (void *) cancelTransaction},
        {"NAPI_L3SetData",              "(I[BI)I",                                                          (void *) setData},
        {"NAPI_L3GetData",              "(IB[BI[I)I",                                                       (void *) getData},
        {"NAPI_L3SetTLVData",           "([BI)I",                                                           (void *) setTLVData},
        {"NAPI_L3GetTlvData",           "([BIB[BII[I)I",                                                    (void *) getTlvData},
        {"NAPI_L3SetDebugMode",         "(I)I",                                                             (void *) setDebugMode},
        {"NAPI_L3GetVersion",           "(I[B)I",                                                           (void *) getVersion},
        {"NAPI_L3LoadRevocationList",   "(Lcom/newland/sdk/module/extpinpademv/CRLEntry;I)I",               (void *) loadRevocationList},
        {"NAPI_L3LoadExceptionList",    "(Lcom/newland/sdk/module/extpinpademv/ExceptionEntry;I)I",         (void *) loadExceptionList},
};

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    DEBUG_INIT;

    JNIEnv *env = NULL;
    gJavaVM = vm;
    int status = (*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_4);
    if (status != JNI_OK) {
        LOGD_FMT("GetEnv failed!");
        return JNI_ERR;
    }
    jclass cls = (*env)->FindClass(env, "com/newland/sdk/module/extpinpademv/jniIntf/NapiEmvL3");
    if (cls == NULL)
        return JNI_ERR;
    if ((*env)->RegisterNatives(env, cls, methods, sizeof(methods) / sizeof(JNINativeMethod)) < 0)
        return JNI_ERR;

    return JNI_VERSION_1_4;
}

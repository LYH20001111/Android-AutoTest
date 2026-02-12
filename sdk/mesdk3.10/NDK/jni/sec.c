#include <jni.h>
#include "ndk.h"
#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <unistd.h>
#include <android/log.h>
#include <NDK.h>
#include "__log.h"
#include "napi_crypto.h"
#define LOG_TAG "IntelligentLib"
#define LOGI(...) __android_log_print(ANDROID_LOG_VERBOSE,LOG_TAG,__VA_ARGS__)
/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecGetVer
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecGetVer
  (JNIEnv *env, jobject jo, jbyteArray version){
	  char buf[100] = {0};
	  int ret = -1;
	  ret = NDK_SecGetVer(buf);
	  if(ret == 0)
		  (*env)->SetByteArrayRegion(env,version,0,strlen(buf),buf);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecGetRandom
 * Signature: (I[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecGetRandom
  (JNIEnv *env, jobject jo, jint num, jbyteArray val){
	  char buf[4096] = {0};
	  int ret = -1;
	  ret = NDK_SecGetRandom(num,buf);
	  if(ret == 0)
		  (*env)->SetByteArrayRegion(env,val,0,num,buf);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecSetCfg
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecSetCfg
  (JNIEnv *env, jobject jo, jint type){
	  return NDK_SecSetCfg(type);
  };

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecGetCfg
 * Signature: ([I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecGetCfg
  (JNIEnv *env, jobject jo, jintArray type){
	  int mtype = 0;
	  int ret = -1;
	  ret = NDK_SecGetCfg(&mtype);
	  if(ret == 0)
		  (*env)->SetIntArrayRegion(env,type,0,1,&mtype);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecGetKcv_m
 * Signature: (BBII[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecGetKcv_1m
  (JNIEnv *env, jobject jo, jbyte ucKeyType, jbyte ucKeyIdx, jint nCheckMode, jint nLen, jbyteArray sCheckBuf){
	  char type = ucKeyType;
	  char idx = ucKeyIdx;
	  ST_SEC_KCV_INFO stKcvInfoIn = {0};
	  stKcvInfoIn.nCheckMode = nCheckMode;
	  stKcvInfoIn.nLen = nLen;
	  int ret = -1;
	  ret = NDK_SecGetKcv(type,idx,&stKcvInfoIn);
	  if(ret == 0)
		  (*env)->SetByteArrayRegion(env,sCheckBuf,0,stKcvInfoIn.nLen,stKcvInfoIn.sCheckBuf);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecKeyErase
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecKeyErase
  (JNIEnv *env, jobject jo){
	  return NDK_SecKeyErase();
  };

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecLoadKey_m
 * Signature: (BBBBI[BII[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecLoadKey_1m
  (JNIEnv *env, jobject jo, jbyte ucScrKeyType, jbyte ucDstKeyType, jbyte ucScrKeyIdx, jbyte ucDstKeyIdx, jint nDstKeyLen, jbyteArray sDstKeyValue, jint nCheckMode, jint nLen, jbyteArray sCheckBuf){
	ST_SEC_KCV_INFO stKcvInfoIn = {0};
	ST_SEC_KEY_INFO stKeyInfo = {0};
	stKeyInfo.ucScrKeyType = ucScrKeyType;
	stKeyInfo.ucDstKeyType = ucDstKeyType;
	stKeyInfo.ucScrKeyIdx = ucScrKeyIdx;
	stKeyInfo.ucDstKeyIdx = ucDstKeyIdx;
	stKeyInfo.nDstKeyLen = nDstKeyLen;
	char *sbuf = (*env)->GetByteArrayElements(env,sCheckBuf,JNI_FALSE);
	int slen = (*env)->GetArrayLength(env,sCheckBuf);
	char *buf = (*env)->GetByteArrayElements(env,sDstKeyValue,JNI_FALSE);
	if(buf != NULL)
		memcpy(stKeyInfo.sDstKeyValue,buf,nDstKeyLen);
	(*env)->ReleaseByteArrayElements(env,sDstKeyValue,buf,0);
	stKcvInfoIn.nCheckMode = nCheckMode;
	stKcvInfoIn.nLen = nLen;
	if(slen > 0)
		memcpy(stKcvInfoIn.sCheckBuf,sbuf,slen);
	(*env)->ReleaseByteArrayElements(env,sCheckBuf,sbuf,0);
	int ret = NDK_SecLoadKey(&stKeyInfo,&stKcvInfoIn);
	if(ret == 0)
		(*env)->SetByteArrayRegion(env,sCheckBuf,0,stKcvInfoIn.nLen,stKcvInfoIn.sCheckBuf);
	return ret;
  };

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecSetIntervaltime
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecSetIntervaltime
  (JNIEnv *env, jobject jo, jint unTPKIntervalTimeMs, jint unTAKIntervalTimeMs){
	return NDK_SecSetIntervaltime(unTPKIntervalTimeMs,unTAKIntervalTimeMs);  
  };

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecSetFunctionKey
 * Signature: (B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecSetFunctionKey
  (JNIEnv *env, jobject jo, jbyte ucType){
	  char type = ucType;
	  return NDK_SecSetFunctionKey(type);
  };

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecGetMac
 * Signature: (B[BI[BB)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecGetMac
  (JNIEnv *env, jobject jo, jbyte ucKeyIdx, jbyteArray psDataIn, jint nDataInLen, jbyteArray psMacOut, jbyte ucMod){
	  char *buf = (*env)->GetByteArrayElements(env,psDataIn,JNI_FALSE);
	  char keyidx = ucKeyIdx;
	  char mode = ucMod;
	  int ret = -1;
	  char rbuf[100] = {0};
	  ret = NDK_SecGetMac(keyidx,buf,nDataInLen,rbuf,mode);
	  if(ret == 0)
		  (*env)->SetByteArrayRegion(env,psMacOut,0,8,rbuf);
	  (*env)->ReleaseByteArrayElements(env,psDataIn,buf,0);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecGetPin
 * Signature: (BLjava/lang/String;Ljava/lang/String;[BBI)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecGetPin
  (JNIEnv *env, jobject jo, jbyte ucKeyIdx, jstring pszExpPinLenIn, jstring pszDataIn, jbyteArray psPinBlockOut, jbyte ucMode, jint nTimeOutMs){
	  char *lenin = (pszExpPinLenIn == NULL ?NULL:(*env)->GetStringUTFChars(env, pszExpPinLenIn, 0));
	  char *datain = (pszDataIn == NULL ? NULL:(*env)->GetStringUTFChars(env, pszDataIn, 0));
	  char buf[10] = {0};
	  int ret = -1;
	  char keyid = ucKeyIdx;
	  char mode = ucMode;
	  if(psPinBlockOut == NULL)
	  	ret = NDK_SecGetPin(keyid,lenin,datain,NULL,mode,nTimeOutMs);
	  else
	  	ret = NDK_SecGetPin(keyid,lenin,datain,buf,mode,nTimeOutMs);
	  if(ret == 0 && psPinBlockOut != NULL)
		  (*env)->SetByteArrayRegion(env,psPinBlockOut,0,8,buf);
	  if(lenin != NULL)
	  	(*env)->ReleaseStringUTFChars(env,pszExpPinLenIn,lenin);
	  if(datain != NULL)
	 	(*env)->ReleaseStringUTFChars(env,pszDataIn,datain);
	  LOGI("2");
	  return ret;
  };

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecCalcDes
 * Signature: (BB[BI[BB)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecCalcDes
  (JNIEnv *env, jobject jo, jbyte ucKeyType, jbyte ucKeyIdx, jbyteArray psDataIn, jint nDataInLen, jbyteArray psDataOut, jbyte ucMode){
	  char *buf = (*env)->GetByteArrayElements(env,psDataIn,JNI_FALSE);
	  char type = ucKeyType;
	  char keyid = ucKeyIdx;
	  int ret = -1;
	  char mode = ucMode;
	  char rbuf[1024]= {0};
	  ret = NDK_SecCalcDes(type,keyid,buf,nDataInLen,rbuf,mode);
	  if(ret == 0)
		  (*env)->SetByteArrayRegion(env,psDataOut,0,nDataInLen,rbuf);
	  (*env)->ReleaseByteArrayElements(env,psDataIn,buf,0);
	  return ret;
  };
/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecLoadTIK_m
 * Signature: (BBB[B[BII[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecLoadTIK_1m
  (JNIEnv *env, jobject jo, jbyte ucGroupIdx, jbyte ucSrcKeyIdx, jbyte ucKeyLen, jbyteArray psKeyValueIn, jbyteArray psKsnIn, jint nCheckMode, jint nLen, jbyteArray sCheckBuf){
	  ST_SEC_KCV_INFO info = {0};
	  info.nCheckMode = nCheckMode;
	  info.nLen = nLen;
	  char *sbuf = (*env)->GetByteArrayElements(env,sCheckBuf,JNI_FALSE);
	  char *buf = (*env)->GetByteArrayElements(env,psKeyValueIn,JNI_FALSE);
	  char *sn = (*env)->GetByteArrayElements(env, psKsnIn,JNI_FALSE);
	  int slen = (*env)->GetArrayLength(env,sCheckBuf);
	  if(slen > 0)
		memcpy(info.sCheckBuf,sbuf,slen);
	  (*env)->ReleaseByteArrayElements(env,sCheckBuf,sbuf,0);
	  int ret = NDK_SecLoadTIK(ucGroupIdx,ucSrcKeyIdx,ucKeyLen,buf,sn,&info);
	  if(ret == 0)
		  (*env)->SetByteArrayRegion(env,sCheckBuf,0,info.nLen,info.sCheckBuf);
	  (*env)->ReleaseByteArrayElements(env,psKsnIn,sn,0);
	  (*env)->ReleaseByteArrayElements(env,psKeyValueIn,buf,0);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecGetDukptKsn
 * Signature: (B[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecGetDukptKsn
  (JNIEnv *env, jobject jo, jbyte ucGroupIdx, jbyteArray psKsnOut){
	  char buf[20] = {0};
	  int ret = NDK_SecGetDukptKsn(ucGroupIdx,buf);
	  if(ret == 0)
		  (*env)->SetByteArrayRegion(env,psKsnOut,0,10,buf);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecIncreaseDukptKsn
 * Signature: (B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecIncreaseDukptKsn
  (JNIEnv *env, jobject jo, jbyte ucGroupIdx){
	  return NDK_SecIncreaseDukptKsn(ucGroupIdx);
  };

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecGetPinDukpt
 * Signature: (BLjava/lang/String;Ljava/lang/String;[B[BBI)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecGetPinDukpt
  (JNIEnv *env, jobject jo, jbyte ucGroupIdx, jstring pszExpPinLenIn, jstring psDataIn, jbyteArray psKsnOut, jbyteArray psPinBlockOut, jbyte ucMode, jint unTimeoutMs){
	  char *lenin = (*env)->GetStringUTFChars(env,pszExpPinLenIn,0);
	  char *dataIn = (*env)->GetStringUTFChars(env,psDataIn,0);
	  char ksnbuf[20]={0};
	  char pinbuf[20]={0};
	  int ret = -1;
	  if(psPinBlockOut == NULL)
	  	ret = NDK_SecGetPinDukpt(ucGroupIdx,lenin,dataIn,ksnbuf,NULL,ucMode,unTimeoutMs);
	  else
	  	ret = NDK_SecGetPinDukpt(ucGroupIdx,lenin,dataIn,ksnbuf,pinbuf,ucMode,unTimeoutMs);
	  if(ret == 0)
	  {
		  if(psKsnOut != NULL){
			  jsize len = (*env)->GetArrayLength(env,psKsnOut);
			  (*env)->SetByteArrayRegion(env,psKsnOut,0,len,ksnbuf);
		  }
		  if(psPinBlockOut != NULL){
			  jsize len = (*env)->GetArrayLength(env,psPinBlockOut);
			  (*env)->SetByteArrayRegion(env,psPinBlockOut,0,len,pinbuf);
		  }
	  }
	  (*env)->ReleaseStringUTFChars(env,pszExpPinLenIn,lenin);
	  (*env)->ReleaseStringUTFChars(env,psDataIn,dataIn);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecGetMacDukpt
 * Signature: (B[BI[B[BB)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecGetMacDukpt
  (JNIEnv *env, jobject jo, jbyte ucGroupIdx, jbyteArray psDataIn, jint nDataInLen, jbyteArray psMacOut, jbyteArray psKsnOut, jbyte ucMode){
	  char *buf = (*env)->GetByteArrayElements(env,psDataIn,JNI_FALSE);
	  char rbuf[20] = {0};
	  char ksnbuf[20] = {0};
	  int ret = -1;
	  ret = NDK_SecGetMacDukpt(ucGroupIdx,buf,nDataInLen,rbuf,ksnbuf,ucMode);
	  if(ret == 0)
	  {
		  if(psMacOut != NULL){
			  jsize len = (*env)->GetArrayLength(env,psMacOut);
			  (*env)->SetByteArrayRegion(env,psMacOut,0,len,rbuf);
		  }

		  if(psKsnOut != NULL){
			  jsize len = (*env)->GetArrayLength(env,psKsnOut);
			  (*env)->SetByteArrayRegion(env,psKsnOut,0,len,ksnbuf);
		  }

	  }
	  (*env)->ReleaseByteArrayElements(env,psDataIn,buf,0);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecCalcDesDukpt
 * Signature: (BBLjava/lang/String;I[B[B[BB)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecCalcDesDukpt
  (JNIEnv *env, jobject jo, jbyte ucGroupIdx, jbyte ucKeyVarType, jstring psIV, jint usDataInLen, jbyteArray psDataIn, jbyteArray psDataOut, jbyteArray psKsnOut, jbyte ucMode){
	  if(usDataInLen%8!=0){
		 return NDK_ERR_PARA;
	  }
	  char ksnout[20] = {0};
	  char dataout[4096] = {0};
	  char *iv = NULL;
	  if(psIV != NULL){
		  iv = (*env)->GetStringUTFChars(env,psIV,0);
	  }
	  char *datain = (*env)->GetByteArrayElements(env,psDataIn,JNI_FALSE);
	  int ret = NDK_SecCalcDesDukpt(ucGroupIdx,ucKeyVarType,iv,usDataInLen,datain,dataout,ksnout,ucMode);
	  if(ret == 0)
	  {	  
		  (*env)->SetByteArrayRegion(env,psDataOut,0,usDataInLen,dataout);
		  (*env)->SetByteArrayRegion(env,psKsnOut,0,10,ksnout);
	  }
	  (*env)->ReleaseByteArrayElements(env,psDataIn,datain,0);
	  if(psIV != NULL)
	  	 (*env)->ReleaseStringUTFChars(env,psIV,iv);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecLoadRsaKey_m
 * Signature: (BI[B[B[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecLoadRsaKey_1m
  (JNIEnv *env, jobject jo, jbyte ucRsaKeyIndex, jint usBits, jbyteArray sModulus, jbyteArray sExponent, jbyteArray reverse){
	  return 0;
  };

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecRecover
 * Signature: (BBI[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecRecover
  (JNIEnv *, jobject, jbyte, jbyte, jint, jbyteArray);

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecGetPinResult
 * Signature: ([B[I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecGetPinResult
  (JNIEnv *env, jobject jo, jbyteArray psPinBlock, jintArray nStatus){

	uint *pStatus = (*env)->GetIntArrayElements(env,nStatus,NULL);
	LOGD_FMT(">>>pStatus[%d]",pStatus[0]);
	if(pStatus != NULL && pStatus[0] == (0x8000|0x1B)){
		char buf[32] = {0};
		int status = pStatus[0];
		int ret = NDK_SecGetPinResult(buf,&status);
		(*env)->ReleaseIntArrayElements(env,nStatus,pStatus,NULL);
		return ret;
	}
	int status = 0;
	char buf[32] = {0};
	int ret = NDK_SecGetPinResult(buf,&status);
	if(ret == 0)
	{
		if(psPinBlock != NULL){
			jsize len = (*env)->GetArrayLength(env,psPinBlock);
			(*env)->SetByteArrayRegion(env,psPinBlock,0,len,buf);
		}

		(*env)->SetIntArrayRegion(env,nStatus,0,1,&status);
	}


	return ret;
  };

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecSetKeyOwner
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecSetKeyOwner
  (JNIEnv *env, jobject jo, jstring name){
	  char *buf = (*env)->GetStringUTFChars(env,name,0);
	  int ret = NDK_SecSetKeyOwner(buf);
	  (*env)->ReleaseStringUTFChars(env,name,buf);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecGetTamperStatus
 * Signature: ([I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecGetTamperStatus
  (JNIEnv *env, jobject jo, jintArray pnStatus){
	  int status = 0;
	  int ret = NDK_SecGetTamperStatus(&status);
	  if(ret == 0)
		  (*env)->SetIntArrayRegion(env,pnStatus,0,1,&status);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecGetPinResultDukpt
 * Signature: ([B[B[I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecGetPinResultDukpt
  (JNIEnv *env, jobject jo, jbyteArray psPinBlock, jbyteArray psKsn, jintArray nStatus){

	uint *pStatus = (*env)->GetIntArrayElements(env,nStatus,NULL);
	LOGD_FMT(">>>pStatus[%d]",pStatus[0]);
	if(pStatus != NULL && pStatus[0] == (0x8000|0x1B)){
		char blockbuf[32] = {0};
		char ksnbuf[32] = {0};
		int status = pStatus[0];
		int ret = NDK_SecGetPinResultDukpt(blockbuf,ksnbuf,&status);
		(*env)->ReleaseIntArrayElements(env,nStatus,pStatus,NULL);
		return ret;
	}

	char blockbuf[32] = {0};
	char ksnbuf[32] = {0};
	int status = 0;
	int ret = -1;
	ret = NDK_SecGetPinResultDukpt(blockbuf,ksnbuf,&status);
	if(ret == 0)
	{
        if(psPinBlock != NULL){
            jsize len = (*env)->GetArrayLength(env,psPinBlock);
            (*env)->SetByteArrayRegion(env,psPinBlock,0,len,blockbuf);
        }

        if(psKsn != NULL){
            jsize len = (*env)->GetArrayLength(env,psKsn);
            (*env)->SetByteArrayRegion(env,psKsn,0,len,ksnbuf);
        }

		(*env)->SetIntArrayRegion(env,nStatus,0,1,&status);
	}
	return ret;
  };

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_GetTamperStatus
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1GetTamperStatus
  (JNIEnv *env, jobject jo){
	  return 0;//NDK_GetTamperStatus();
  };

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecKeyDelete
 * Signature: (BB)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecKeyDelete
  (JNIEnv *env, jobject jo, jbyte ucKeyIdx, jbyte ucKeyType){
	  LOGE_FMT("NDK_SecKeyDelete");
	  return NDK_SecKeyDelete(ucKeyIdx,ucKeyType);
  };


/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecGetDrySR
 * Signature: ([I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecGetDrySR
  (JNIEnv *env, jobject jo, jintArray type){
	  int len = 0;
	  int ret = NDK_SecGetDrySR(&len);
	  if(ret == 0)
		  (*env)->SetIntArrayRegion(env,type,0,1,&len);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecClear
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecClear
  (JNIEnv *env, jobject jo){
	  return NDK_SecClear();
  };

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecVppTpInit
 * Signature: ([B[B[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecVppTpInit
  (JNIEnv *env, jobject jo, jbyteArray num_btn, jbyteArray func_key, jbyteArray out_seq){
	  char *btn = (*env)->GetByteArrayElements(env,num_btn,JNI_FALSE);
	  char *key = (*env)->GetByteArrayElements(env,func_key,JNI_FALSE);
	  char buf[20] = {0};
	  char *pBuf = NULL;
	  if(out_seq != NULL)
	  	 pBuf = buf;
	  int ret = NDK_SecVppTpInit(btn,key,pBuf);
	  if(ret == 0 && out_seq != NULL){
		  (*env)->SetByteArrayRegion(env,out_seq,0,strlen(buf),buf);
	  }
	  (*env)->ReleaseByteArrayElements(env,num_btn,btn,0);
	  (*env)->ReleaseByteArrayElements(env,func_key,key,0);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecUserKeyDelete
 * Signature: ()I
 
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecUserKeyDelete
  (JNIEnv *env, jobject jo){
	  return NDK_SecUserKeyDelete();
  };*/



/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecVerifyPlainPin
 * Signature: (BLjava/lang/String;[BBI)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecVerifyPlainPin
		(JNIEnv *env, jobject jo, jbyte ucIccSlot, jstring pszExpPinLenIn, jbyteArray psIccRespOut,
		 jbyte ucMode, jint unTimeoutMs) {
    char iccRespOut[512];
    memset(iccRespOut,0,sizeof(iccRespOut));
	char *lenin = (pszExpPinLenIn == NULL ? NULL : (*env)->GetStringUTFChars(env, pszExpPinLenIn, JNI_FALSE));
    LOGD_FMT(">>>ucIccSlot[%d] ucMode[%d] unTimeoutMs[%d]",ucIccSlot,ucMode,unTimeoutMs);
    int ret = -1;
    if(psIccRespOut == NULL){
        ret = NDK_SecVerifyPlainPin(ucIccSlot, lenin, NULL, ucMode, unTimeoutMs);
    }else{
        ret = NDK_SecVerifyPlainPin(ucIccSlot, lenin, iccRespOut, ucMode, unTimeoutMs);
    }
    if(ret == 0 && psIccRespOut != NULL){
        jsize len = (*env)->GetArrayLength(env,psIccRespOut);
        (*env)->SetByteArrayRegion(env,psIccRespOut,0,len,iccRespOut);
    }
	if (lenin != NULL)
		(*env)->ReleaseStringUTFChars(env, pszExpPinLenIn, lenin);
    LOGD_FMT(">>>NDK_SecVerifyPlainPin ret[%d]",ret);
	return ret;
};

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecVerifyCipherPin_m
 * Signature: (BLjava/lang/String;I[B[B[B[BBI)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecVerifyCipherPin_1m
        (JNIEnv *env, jobject jo, jbyte ucIccSlot, jstring pszExpPinLenIn, jint usBits,
         jbyteArray sModulus, jbyteArray sExponent, jbyteArray reverse, jbyteArray psIccRespOut,
         jbyte ucMode, jint unTimeoutMs) {
    char *lenin = (pszExpPinLenIn == NULL ? NULL : (*env)->GetStringUTFChars(env, pszExpPinLenIn, JNI_FALSE));
    int ret = -1;
	char iccRespOut[512];
    memset(iccRespOut,0, sizeof(iccRespOut));
    ST_SEC_RSA_KEY stRsaKey;
    memset(&stRsaKey,0, sizeof(ST_SEC_RSA_KEY));
    stRsaKey.usBits = usBits;
    char *sbuf = (*env)->GetByteArrayElements(env, sModulus, JNI_FALSE);
    char *sexp = (*env)->GetByteArrayElements(env, sExponent, JNI_FALSE);
    char *rev = (*env)->GetByteArrayElements(env, reverse, JNI_FALSE);
    int slen = (*env)->GetArrayLength(env, sModulus);
    int sexplen = (*env)->GetArrayLength(env, sExponent);
    int revlen = (*env)->GetArrayLength(env, reverse);
    if (slen > 0)
        memcpy(stRsaKey.sModulus,sbuf,slen);
    if (sexplen > 0)
        memcpy(stRsaKey.sExponent,sexp,sexplen);
    if (revlen > 0)
        memcpy(stRsaKey.reverse,rev,revlen);
	if(psIccRespOut == NULL){
		ret = NDK_SecVerifyCipherPin(ucIccSlot, lenin, &stRsaKey, NULL, ucMode, unTimeoutMs);
	} else{
		ret = NDK_SecVerifyCipherPin(ucIccSlot, lenin, &stRsaKey, iccRespOut, ucMode, unTimeoutMs);
	}
	if(ret == 0 && psIccRespOut != NULL){
		jsize len = (*env)->GetArrayLength(env,psIccRespOut);
		(*env)->SetByteArrayRegion(env,psIccRespOut,0,len,iccRespOut);
	}
    (*env)->ReleaseByteArrayElements(env, sModulus, sbuf, 0);
    (*env)->ReleaseByteArrayElements(env, sExponent, sexp, 0);
    (*env)->ReleaseByteArrayElements(env, reverse, rev, 0);
	LOGD_FMT(">>>NDK_SecVerifyCipherPin ret[%d]");
    return ret;
};

/*
 * Class:     com_newland_ndk_SecN
 * Method:    NDK_SecVerifyPIN
 * Signature: (BBI[B[BI[BLjava/lang/Object;[BB)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NDK_1SecVerifyPIN
		(JNIEnv *env, jobject obj,
		 jbyte ucKeyID,
		 jbyte ucSKType,
		 jint nTSKLen,
		 jbyteArray psTSK,
		 jbyteArray psPan,
		 jint PinBlockLen,
		 jbyteArray psPinBlockIn,
		 jobject pstRsaPinKeyInObj,
		 jbyteArray psIccRespOut,
		 jbyte ucMode){
 	uchar *ppsTSK=NULL;
	if(psTSK!=NULL){
		ppsTSK = (*env)->GetByteArrayElements(env, psTSK, NULL);
	}
	uchar *ppsPan=NULL;
	if(psPan!=NULL){
		ppsPan = (*env)->GetByteArrayElements(env, psPan, NULL);
	}
	uchar *ppsPinBlockIn=NULL;
	if(psPinBlockIn!=NULL){
		ppsPinBlockIn = (*env)->GetByteArrayElements(env, psPinBlockIn, NULL);
	}

	jbyteArray sModulus;uchar *psModulus;
	jbyteArray sExponent;uchar *psExponent;
	jbyteArray reverse;uchar *preverse;
	ST_SEC_RSA_KEY rsaKey,*prsaKey=NULL;
    if(pstRsaPinKeyInObj!=NULL){
        jclass rsaCls = (*env)->GetObjectClass(env, pstRsaPinKeyInObj);
        int usBits = (*env)->GetIntField(env, pstRsaPinKeyInObj, (*env)->GetFieldID(env, rsaCls, "usBits", "I"));
		jbyteArray sModulus  = (jbyteArray)(*env)->GetObjectField(env, pstRsaPinKeyInObj,(*env)->GetFieldID(env, rsaCls, "sModulus", "[B"));
		jbyteArray sExponent = (jbyteArray)(*env)->GetObjectField(env, pstRsaPinKeyInObj,(*env)->GetFieldID(env, rsaCls, "sExponent", "[B"));
		jbyteArray reverse   = (jbyteArray)(*env)->GetObjectField(env, pstRsaPinKeyInObj,(*env)->GetFieldID(env, rsaCls, "reverse", "[B"));
		rsaKey.usBits = usBits;
		if(sModulus!=NULL){
			memcpy(rsaKey.sModulus,psModulus = (*env)->GetByteArrayElements(env, sModulus, NULL),(*env)->GetArrayLength(env,sModulus));
		}
		if(sExponent!=NULL){
			memcpy(rsaKey.sExponent,psExponent = (*env)->GetByteArrayElements(env, sExponent, NULL),(*env)->GetArrayLength(env,sExponent));
		}
		if(reverse!=NULL){
			memcpy(rsaKey.reverse,preverse = (*env)->GetByteArrayElements(env, reverse, NULL),(*env)->GetArrayLength(env,reverse));
		}
		prsaKey = &rsaKey;
    }
	uchar iccRespOut[16];
	memset(iccRespOut,0, sizeof(iccRespOut));
	LOGD_FMT(">>>ucKeyID[%d] ucSKType[%d] nTSKLen[%d] PinBlockLen[%d] ucMode[%d] prsaKey[%d]",ucKeyID,ucSKType,nTSKLen,PinBlockLen,ucMode,prsaKey);
	LOGD_STR("ppsTSK",ppsTSK,nTSKLen);
	LOGD_STR("ppsPinBlockIn",ppsPinBlockIn,PinBlockLen);
	if(psPan!=NULL){
		LOGD_STR("ppsPan",ppsPan,(*env)->GetArrayLength(env,psPan));
	}
	int ret = NDK_SecVerifyPIN(ucKeyID, ucSKType, nTSKLen, ppsTSK, ppsPan, PinBlockLen, ppsPinBlockIn, prsaKey, iccRespOut, ucMode);

	if(psIccRespOut!=NULL){
		int len = (*env)->GetArrayLength(env,psIccRespOut);
		if(len > sizeof(iccRespOut)){
			len = 16;
		}
		LOGD_STR("iccRespOut",iccRespOut,len);
		(*env)->SetByteArrayRegion(env,psIccRespOut,0,len,iccRespOut);
	}
	if(sModulus!=NULL){
		(*env)->ReleaseByteArrayElements(env,sModulus,psModulus,NULL);
	}
	if(sExponent!=NULL){
		(*env)->ReleaseByteArrayElements(env,sExponent,psExponent,NULL);
	}
	if(reverse!=NULL){
		(*env)->ReleaseByteArrayElements(env,reverse,preverse,NULL);
	}

	if(psTSK!=NULL){
		(*env)->ReleaseByteArrayElements(env,psTSK,ppsTSK,NULL);
	}
	if(psPan!=NULL){
		(*env)->ReleaseByteArrayElements(env,psPan,ppsPan,NULL);
	}
	if(psPinBlockIn!=NULL){
		(*env)->ReleaseByteArrayElements(env,psPinBlockIn,ppsPinBlockIn,NULL);
	}
	LOGD_FMT(">>>NDK_SecVerifyPIN ret[%d]",ret);
	return ret;
}



/*
 * Class:     Java_com_newland_ndk_SecN_NDK_1SecGenerateMAC
 * Method:    NAPI_SecGenerateMAC
 * Signature: (II[BI[BI[BI[B[I[B[I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SecN_NAPI_1SecGenerateMAC
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
Java_com_newland_ndk_SecN_NDK_1SecCalcDesDukpt2(JNIEnv *env, jobject jo,
												jbyte ucGroupIdx, jbyte ucKeyVarType,
												jbyteArray psIV, jint usDataInLen,
												jbyteArray psDataIn, jbyteArray
												psDataOut, jbyteArray psKsnOut, jbyte ucMode){
	char ksnout[20] = {0};
	char dataout[4096] = {0};
	char *iv = NULL;
	if(psIV != NULL){
		iv = (*env)->GetByteArrayElements(env,psIV,0);
	}
	char *datain = (*env)->GetByteArrayElements(env,psDataIn,JNI_FALSE);
	int ret = NDK_SecCalcDesDukpt(ucGroupIdx,ucKeyVarType,iv,usDataInLen,datain,dataout,ksnout,ucMode);
	if(ret == 0)
	{
		(*env)->SetByteArrayRegion(env,psDataOut,0,usDataInLen,dataout);
		(*env)->SetByteArrayRegion(env,psKsnOut,0,10,ksnout);
	}
	(*env)->ReleaseByteArrayElements(env,psDataIn,datain,0);

	if(psIV != NULL)
		(*env)->ReleaseByteArrayElements(env,psIV,iv, 0);
	return ret;

}
//AES DUKPT
JNIEXPORT jint JNICALL
Java_com_newland_ndk_SecN_NDK_1SecCalcAesDukpt(JNIEnv *env, jobject thiz, jbyte uc_group_idx,jint key_alg, jint derivate_usage, jint n_key_len,
											   jbyteArray ps_iv, jint us_data_in_len,
											   jbyteArray ps_data_in, jbyteArray ps_data_out,
											   jbyteArray ps_ksn_out, jint uc_cipher_mode,
											   jint uc_ciper_operation) {

	uchar *piv = NULL;
	if(ps_iv != NULL){
		piv = (*env)->GetByteArrayElements(env,ps_iv,0);
	}
	uchar *pps_data_in = (*env)->GetByteArrayElements(env,ps_data_in,JNI_FALSE);
	uchar buf[us_data_in_len];
	memset(buf,0,sizeof(buf));
	uchar ksn[12];
	memset(ksn,0,sizeof(ksn));
	ST_NDK_SEC_DUKPT_DERIVATE_DATA secDukptDerivateData;
	secDukptDerivateData.KeyAlg = key_alg;
	secDukptDerivateData.DerivateUsage = derivate_usage;
	secDukptDerivateData.nKeyLen = n_key_len;
	int ret = NDK_SecCalcAesDukpt(uc_group_idx,&secDukptDerivateData,piv,us_data_in_len,pps_data_in,buf,ksn,uc_cipher_mode,uc_ciper_operation);
	if(ret == 0){
		(*env)->SetByteArrayRegion(env,ps_data_out,0,us_data_in_len,buf);
		(*env)->SetByteArrayRegion(env,ps_ksn_out,0,sizeof(ksn),ksn);
	}
	if(ps_iv != NULL){
		(*env)->ReleaseByteArrayElements(env,ps_iv,piv, 0);
	}
	(*env)->ReleaseByteArrayElements(env,ps_data_in,pps_data_in, 0);
	return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_SecN_NDK_1SecGetPinAesDukpt(JNIEnv *env, jobject thiz, jbyte uc_group_idx,
												 jint key_alg, jint derivate_usage, jint n_key_len,
												 jbyteArray psz_exp_pin_len_in, jbyteArray ps_pan,
												 jbyteArray ps_ksn_out, jbyteArray ps_pin_block_out,
												 jint uc_mode, jint un_timeout_ms) {
	ST_NDK_SEC_DUKPT_DERIVATE_DATA secDukptDerivateData;
	secDukptDerivateData.KeyAlg = key_alg;
	secDukptDerivateData.DerivateUsage = derivate_usage;
	secDukptDerivateData.nKeyLen = n_key_len;

	uchar *ppsz_exp_pin_len_in = (*env)->GetByteArrayElements(env,psz_exp_pin_len_in,JNI_FALSE);
	uchar *pps_pan = NULL;
	if(ps_pan != NULL){
		pps_pan = (*env)->GetByteArrayElements(env,ps_pan,0);
	}
	uchar ksn[12];
	uchar pinblock[16];
	memset(ksn,0,sizeof(ksn));
	memset(pinblock,0,sizeof(pinblock));

	int ret = NDK_SecGetPinAesDukpt(uc_group_idx,&secDukptDerivateData,ppsz_exp_pin_len_in,pps_pan,ksn,pinblock,uc_mode,un_timeout_ms);
	if(ret == 0){
		(*env)->SetByteArrayRegion(env,ps_pin_block_out,0,sizeof(pinblock),pinblock);
		(*env)->SetByteArrayRegion(env,ps_ksn_out,0,sizeof(ksn),ksn);
	}
	if(ps_pan != NULL){
		(*env)->ReleaseByteArrayElements(env,ps_pan,pps_pan, 0);
	}
	(*env)->ReleaseByteArrayElements(env,psz_exp_pin_len_in,ppsz_exp_pin_len_in, 0);
	return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_SecN_NDK_1SecGetPinResultAesDukpt(JNIEnv *env, jobject thiz,
													   jbyteArray ps_pin_block, jbyteArray ps_ksn,
													   jintArray n_status) {

	uchar pinblock[32];
	uchar ksn[12];
	uint status[1]={0};

	memset(pinblock,0,sizeof(pinblock));
	memset(ksn,0,sizeof(ksn));

	int ret = NDK_SecGetPinResultAesDukpt(pinblock,ksn,&status[0]);
	if(ret == 0){
		(*env)->SetByteArrayRegion(env,ps_pin_block,0,sizeof(pinblock),pinblock);
		(*env)->SetByteArrayRegion(env,ps_ksn,0,sizeof(ksn),ksn);
		(*env)->SetIntArrayRegion(env,n_status,0,1,status);
	}
	return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_SecN_NDK_1SecGetMacAesDukpt(JNIEnv *env, jobject thiz, jbyte uc_group_idx,
												 jint key_alg, jint derivate_usage, jint n_key_len,
												 jbyteArray ps_data_in, jint n_data_in_len,
												 jbyteArray ps_mac_out, jbyteArray ps_ksn_out,
												 jint uc_mac_type) {
	ST_NDK_SEC_DUKPT_DERIVATE_DATA secDukptDerivateData;
	secDukptDerivateData.KeyAlg = key_alg;
	secDukptDerivateData.DerivateUsage = derivate_usage;
	secDukptDerivateData.nKeyLen = n_key_len;

	uchar buf[n_data_in_len];
	memset(buf,0,sizeof(buf));
	uchar ksn[12];
	memset(ksn,0,sizeof(ksn));
	uchar *pps_data_in = (*env)->GetByteArrayElements(env,ps_data_in,JNI_FALSE);
	int ret = NDK_SecGetMacAesDukpt(uc_group_idx,&secDukptDerivateData,pps_data_in,n_data_in_len,buf,ksn,uc_mac_type);
	if(ret == 0){
		(*env)->SetByteArrayRegion(env,ps_mac_out,0,sizeof(buf),buf);
		(*env)->SetByteArrayRegion(env,ps_ksn_out,0,sizeof(ksn),ksn);
	}
	(*env)->ReleaseByteArrayElements(env,ps_data_in,pps_data_in, 0);
	return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_SecN_NDK_1SecGetAesDukptKsn(JNIEnv *env, jobject thiz, jbyte uc_group_idx,
												 jbyteArray ps_ksn_out) {
	uchar ksn[12];
	memset(ksn,0,sizeof(ksn));
	int ret = NDK_SecGetAesDukptKsn(uc_group_idx,ksn);
	if(ret == 0){
		(*env)->SetByteArrayRegion(env,ps_ksn_out,0,sizeof(ksn),ksn);
	}
	return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_SecN_NDK_1SecIncreaseAesDukptKsn(JNIEnv *env, jobject thiz,
													  jbyte uc_group_idx) {
	int ret = NDK_SecIncreaseAesDukptKsn(uc_group_idx);
	return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_SecN_NDK_1SecLoadAesDukptKey(JNIEnv *env, jobject thiz, jbyte uc_src_key_idx,
												  jbyte uc_src_key_type, jbyte key_index,
												  jbyte key_type, jint ksnlen, jbyteArray ksn,
												  jint keylen, jint keydatalen, jbyteArray keyvalue,
												  jint n_check_mode, jint n_len,
												  jbyteArray s_check_buf) {
	uchar *pksn = (*env)->GetByteArrayElements(env,ksn,JNI_FALSE);
	uchar *pkeyvalue = (*env)->GetByteArrayElements(env,keyvalue,JNI_FALSE);
	ST_AESDUKPT_KEYINFO keyinfo;
	memset(&keyinfo, 0, sizeof(keyinfo));
	keyinfo.keyIndex = key_index;
	keyinfo.keyType = key_type;
	keyinfo.ksnlen = ksnlen;
	memcpy(keyinfo.ksn,pksn,(*env)->GetArrayLength(env,ksn));
	keyinfo.keylen = keylen;
	keyinfo.keydatalen = keydatalen;
	memcpy(keyinfo.keyvalue,pkeyvalue,(*env)->GetArrayLength(env,keyvalue));


	ST_SEC_KCV_INFO info;
	memset(&info, 0, sizeof(info));
	info.nCheckMode = n_check_mode;
	info.nLen = n_len;
	uchar *ps_check_buf = NULL;
	if(s_check_buf != NULL){
		ps_check_buf = (*env)->GetByteArrayElements(env,s_check_buf,JNI_FALSE);
		int slen = (*env)->GetArrayLength(env,s_check_buf);
		memcpy(info.sCheckBuf,ps_check_buf,slen);
	}
	int ret = NDK_SecLoadAesDukptKey(uc_src_key_idx,uc_src_key_type,&keyinfo,&info);
	(*env)->ReleaseByteArrayElements(env,ksn,pksn, 0);
	(*env)->ReleaseByteArrayElements(env,keyvalue,pkeyvalue, 0);
	if(s_check_buf != NULL){
		(*env)->ReleaseByteArrayElements(env,s_check_buf,ps_check_buf, 0);
	}
	return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_SecN_NDK_1SecLoadKeyRsa(JNIEnv *env, jobject thiz, jbyte uc_src_key_idx,
											 jbyte uc_src_key_type, jbyte uc_rsa_key_index,
											 jint us_bits, jbyteArray s_modulus,
											 jbyteArray s_exponent, jbyteArray reverse) {

	ST_SEC_RSA_KEY stRsaKey;
	memset(&stRsaKey, 0, sizeof(ST_SEC_RSA_KEY));
	stRsaKey.usBits = us_bits;
	uchar *ps_modulus = (*env)->GetByteArrayElements(env, s_modulus, JNI_FALSE);
	uchar *ps_exponent = (*env)->GetByteArrayElements(env, s_exponent, JNI_FALSE);
	uchar *preverse = (*env)->GetByteArrayElements(env, reverse, JNI_FALSE);
	int slen = (*env)->GetArrayLength(env, s_modulus);
	int sexplen = (*env)->GetArrayLength(env, s_exponent);
	int revlen = (*env)->GetArrayLength(env, reverse);
	memcpy(stRsaKey.sModulus,ps_modulus,slen);
	memcpy(stRsaKey.sExponent,ps_exponent,sexplen);
	memcpy(stRsaKey.reverse,preverse,revlen);
	int ret = NDK_SecLoadKeyRsa(uc_src_key_idx,uc_src_key_type,uc_rsa_key_index,&stRsaKey);

	return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_SecN_NDK_1SecRsaKeyRandomOut(JNIEnv *env, jobject thiz, jbyte uc_src_key_idx,
												  jbyte uc_key_type, jbyte uc_key_index,
												  jint keylen, jbyteArray key_data,
												  jintArray keyoutlen) {

	int len;
	uchar buf[2048];
	memset(buf,0,sizeof(buf));
	int ret =  NDK_SecRsaKeyRandomOut(uc_src_key_idx,uc_key_type,uc_key_index,keylen,buf,&len);
	if(ret == 0){
		(*env)->SetIntArrayRegion(env,keyoutlen,0,1,&len);
		(*env)->SetByteArrayRegion(env,key_data,0,len,buf);

        LOGE_FMT("NDK_SecRsaKeyRandomOut Len[%d]",len);
        LOGE_STR("NDK_SecRsaKeyRandomOut KeyOut",buf,len);
	}
	return ret;
}
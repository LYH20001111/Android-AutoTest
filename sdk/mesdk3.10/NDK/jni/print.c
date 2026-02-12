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
 * Class:     com_newland_ndk_Print
 * Method:    NDK_PrnInit
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_Print_NDK_1PrnInit
  (JNIEnv *env, jobject jo, jint type){
	 return NDK_PrnInit(type);
  };

/*
 * Class:     com_newland_ndk_Print
 * Method:    NDK_PrnStr_m
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_Print_NDK_1PrnStr_1m
  (JNIEnv *env, jobject jo, jbyteArray prnstr){
	  jbyte *str = (*env)->GetByteArrayElements(env, prnstr, JNI_FALSE);
	  int ret = 0;
	  int len = (*env)->GetArrayLength(env,prnstr);
	  char buf[4096] = {0};
	  if(len > 0)
		memcpy(buf,str,len);
	  ret = NDK_PrnStr(buf);
	  (*env)->ReleaseByteArrayElements(env, prnstr, str,0);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_Print
 * Method:    NDK_PrnStart
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_Print_NDK_1PrnStart
  (JNIEnv *env, jobject jo){
	  return NDK_PrnStart();
  };

/*
 * Class:     com_newland_ndk_Print
 * Method:    NDK_PrnImage
 * Signature: (III[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_Print_NDK_1PrnImage
  (JNIEnv *env, jobject jo, jint unXsize, jint unYsize, jint unXpos, jbyteArray psImgBuf){
	  char *buf = NULL;
	  int ret = 0;
	  int alen = (*env)->GetArrayLength(env,psImgBuf); //获取长度

     if(alen   >   0)
     {
	  	jbyte *ba  = (*env)->GetByteArrayElements(env,psImgBuf,JNI_FALSE); //jbyteArray转为jbyte* 
	      buf = (char*)malloc(alen+1);         //"\0"
	      memcpy(buf,ba,alen);
	      buf[alen]=0;
	  	(*env)->ReleaseByteArrayElements(env,psImgBuf,ba,0);  //释放掉
	  	ret = NDK_PrnImage(unXsize,unYsize,unXpos,buf);
	  	free(buf);
	  	buf = NULL;
	  return ret;
     }
	 
	  return -1;
  };

/*
 * Class:     com_newland_ndk_Print
 * Method:    NDK_PrnGetVersion
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_Print_NDK_1PrnGetVersion
  (JNIEnv *env, jobject jo, jbyteArray version){
	char ver[100]={0};
	int ret = 0;
	ret = NDK_PrnGetVersion(ver);
	(*env)->SetByteArrayRegion(env, version, 0, strlen(ver), ver);
	return ret;  
  };

/*
 * Class:     com_newland_ndk_Print
 * Method:    NDK_PrnSetFont
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_Print_NDK_1PrnSetFont
  (JNIEnv *env, jobject jo, jint hz, jint zm){
	  return NDK_PrnSetFont(hz,zm);
  };

/*
 * Class:     com_newland_ndk_Print
 * Method:    NDK_PrnGetStatus
 * Signature: ([I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_Print_NDK_1PrnGetStatus
  (JNIEnv *env, jobject jo, jintArray status){
	  int ret = 0;
	  int nstatus = 0;
	  ret = NDK_PrnGetStatus(&nstatus);
	  (*env)->SetIntArrayRegion(env, status, 0, 1, &nstatus);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_Print
 * Method:    NDK_PrnSetMode
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_Print_NDK_1PrnSetMode
  (JNIEnv *env, jobject jo, jint mode, jint unSigOrDou){
	  return NDK_PrnSetMode(mode,unSigOrDou);
  };

/*
 * Class:     com_newland_ndk_Print
 * Method:    NDK_PrnSetGreyScale
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_Print_NDK_1PrnSetGreyScale
  (JNIEnv *env, jobject jo, jint greyscale){
	  return NDK_PrnSetGreyScale(greyscale);
  };

/*
 * Class:     com_newland_ndk_Print
 * Method:    NDK_PrnSetForm
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_Print_NDK_1PrnSetForm
  (JNIEnv *env, jobject jo, jint unBorder, jint unColumn, jint unRow){
	  return NDK_PrnSetForm(unBorder,unColumn,unRow);
  };

/*
 * Class:     com_newland_ndk_Print
 * Method:    NDK_PrnFeedByPixel
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_Print_NDK_1PrnFeedByPixel
  (JNIEnv *env, jobject jo, jint unPixel){
	  return NDK_PrnFeedByPixel(unPixel);
  };

/*
 * Class:     com_newland_ndk_Print
 * Method:    NDK_PrnSetUnderLine
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_Print_NDK_1PrnSetUnderLine
  (JNIEnv *env, jobject jo, jint emStatus){
	  return NDK_PrnSetUnderLine(emStatus);
  };

JNIEXPORT jint JNICALL Java_com_newland_ndk_Print_NDK_1Script_1Print
 (JNIEnv *env, jobject jo, jbyteArray psData,jint nlen)
{
	
	if(psData == NULL ){
		return NDK_ERR_PARA;
	}
	char *psDataTemp = (char*)(*env)->GetByteArrayElements(env,psData,JNI_FALSE);
	if(psDataTemp == NULL){
		return NDK_ERR_PARA;
	}
	psDataTemp[nlen] = '\0';
  	int ret = NDK_Script_Print(psDataTemp,nlen);
	(*env)->ReleaseByteArrayElements(env,psData,psDataTemp,0);
	return ret;
}

JNIEXPORT jint JNICALL Java_com_newland_ndk_Print_NDK_1PrnModuleInit(JNIEnv *env, jobject jo)
{
	LOGD_FMT("");
	return NDK_PrnModuleInit();
}


JNIEXPORT jint JNICALL Java_com_newland_ndk_Print_NDK_1PrnCutterInit(JNIEnv *env, jobject jo)
{
	LOGD_FMT("");
	return NDK_PrnCutterInit();
}

JNIEXPORT jint JNICALL Java_com_newland_ndk_Print_NDK_1PrnCutterPerformance(JNIEnv *env, jobject jo)
{
	LOGD_FMT("");
	return NDK_PrnCutterPerformance();
}

JNIEXPORT jint JNICALL Java_com_newland_ndk_Print_NDK_1PrnSetParam(JNIEnv *env, jobject jo, jint type,jint value)
{
    LOGD_FMT("");
    return NDK_PrnSetParam(type,value);
}

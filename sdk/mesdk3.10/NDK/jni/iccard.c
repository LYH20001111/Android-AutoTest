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
 * Class:     com_newland_ndk_IcCard
 * Method:    NDK_IccGetVersion
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_IcCard_NDK_1IccGetVersion
  (JNIEnv *env, jobject jo, jbyteArray version){
	  char buf[100]={0};
	  int ret = -1;
	  ret = NDK_IccGetVersion(buf);
	  if(ret == 0)
		  (*env)->SetByteArrayRegion(env,version,0,strlen(buf),buf);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_IcCard
 * Method:    NDK_IccPowerUp
 * Signature: (I[B[I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_IcCard_NDK_1IccPowerUp
  (JNIEnv *env, jobject jo, jint type, jbyteArray buf, jintArray buflen){
	  char nbuf[1024]={0};
	  int ret = -1,len = 0;
	  ret = NDK_IccPowerUp(type,nbuf,&len);
	  if(ret == 0)
	  {
		  (*env)->SetByteArrayRegion(env,buf,0,len,nbuf);
		  (*env)->SetIntArrayRegion(env,buflen,0,1,&len);
	  }
	  return ret;
  };

/*
 * Class:     com_newland_ndk_IcCard
 * Method:    NDK_IccPowerDown
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_IcCard_NDK_1IccPowerDown
  (JNIEnv *env, jobject jo, jint type){
	  return NDK_IccPowerDown(type);
  };

/*
 * Class:     com_newland_ndk_IcCard
 * Method:    NDK_IccDetect
 * Signature: ([I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_IcCard_NDK_1IccDetect
  (JNIEnv *env, jobject jo, jintArray status){
	  int nstatus = 0;
	  int ret = -1;
	  ret = NDK_IccDetect(&nstatus);
	  if(ret == 0)
		  (*env)->SetIntArrayRegion(env,status,0,1,&nstatus);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_IcCard
 * Method:    NDK_Iccrw
 * Signature: (II[B[I[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_IcCard_NDK_1Iccrw
  (JNIEnv *env, jobject jo, jint type, jint sendlen, jbyteArray sendbuf, jintArray pnRecvLen, jbyteArray psRecvBuf){
	  int bytelen  = (*env)->GetArrayLength(env,sendbuf);
	  jbyte *bar = NULL;
	  char sbuf[4096] = {0};
	  char rbuf[4096] = {0};
	  int rlen = 0;
	  int ret = -1;
	  if(bytelen > 0)
	  {
		  bar = (*env)->GetByteArrayElements(env,sendbuf,JNI_FALSE);
		  memcpy(sbuf,bar,bytelen);
		  (*env)->ReleaseByteArrayElements(env,sendbuf,bar,0);
	  }
	  ret = NDK_Iccrw(type,sendlen,sbuf,&rlen,rbuf);
	  if(ret == 0){
		  (*env)->SetIntArrayRegion(env,pnRecvLen,0,1,&rlen);
		  (*env)->SetByteArrayRegion(env,psRecvBuf,0,rlen,rbuf);
	  }
	  
	  return ret;
  };
  
/*
 * Class:     com_newland_ndk_IcCard
 * Method:    NDK_IccSetPowerUpMode
 * Signature: (II[B[I[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_IcCard_NDK_1IccSetPowerUpMode(JNIEnv *env, jobject jo, jint mode, jint voltage)
{
   LOGI(">>>NDK_IccSetPowerUpMode");
   return NDK_IccSetPowerUpMode(mode, voltage);
}

JNIEXPORT jint JNICALL Java_com_newland_ndk_IcCard_NDK_1IccSetConfig(JNIEnv *env, jobject jo,jint ictype,jint cfgtype, jint value)
{
    LOGI(">>>NDK_IccSetConfig ictype[%d] cfgtype[%d] value[%d]",ictype,cfgtype,value);
    return NDK_IccSetConfig(ictype,cfgtype,value);
}


  /*
 * Class:     com_newland_ndk_IcCard
 * Method:    NDK_IccGetProtocol
 * Signature: (II[B[I[B)I
 */
  JNIEXPORT jint JNICALL Java_com_newland_ndk_IcCard_NDK_1IccGetProtocol(JNIEnv *env, jobject jo, jint icType, jintArray protocol)
{
	int ret = -1;
	int nProtocol = 0;
	ret = NDK_IccGetProtocol(icType, &nProtocol);
	if(ret == 0)
	{	  	
		(*env)->SetIntArrayRegion(env,protocol,0,1,&nProtocol);
	}
	LOGI(">>>NDK_IccGetProtocol ret[%d]",ret);
	return ret;
}

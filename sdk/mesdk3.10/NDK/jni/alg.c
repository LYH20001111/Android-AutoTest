#include <jni.h>
#include "ndk.h"
#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <unistd.h>
#include <android/log.h>
#define LOG_TAG "IntelligentLib"
#define LOGI(...) __android_log_print(ANDROID_LOG_VERBOSE,LOG_TAG,__VA_ARGS__)
/*
 * Class:     com_newland_ndk_AlgN
 * Method:    NDK_AlgTDes
 * Signature: ([B[B[BII)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_AlgN_NDK_1AlgTDes
  (JNIEnv *env, jobject jo, jbyteArray psDataIn, jbyteArray psDataOut, jbyteArray psKey, jint nKeyLen, jint nMode){
	  char *data = (*env)->GetByteArrayElements(env,psDataIn,JNI_FALSE);
	  char *key = (*env)->GetByteArrayElements(env,psKey,JNI_FALSE);
	  int ret = -1;
	  char buf[4096]= {0};
	  int len = (*env)->GetArrayLength(env,psDataIn);
	  ret = NDK_AlgTDes(data,buf,key,nKeyLen,nMode);
	  if(ret == 0)
		  (*env)->SetByteArrayRegion(env,psDataOut,0,len,buf);
	   (*env)->ReleaseByteArrayElements(env,psDataIn,data,0);
	   (*env)->ReleaseByteArrayElements(env,psKey,key,0);
	   LOGI("NDK_AlgTDes1 ret[%d]",ret);
	   return ret;
  };
/*
 * Class:     com_newland_ndk_AlgN
 * Method:    NDK_AlgSHA1
 * Signature: ([BI[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_AlgN_NDK_1AlgSHA1
  (JNIEnv *env, jobject jo, jbyteArray psDataIn, jint len, jbyteArray psDataOut){
	  char *data = (*env)->GetByteArrayElements(env,psDataIn,JNI_FALSE);
	  int ret = -1;
	  char buf[21]={0};
	  ret = NDK_AlgSHA1(data,len,buf);
	  if(ret == 0)
		  (*env)->SetByteArrayRegion(env,psDataOut,0,20,buf);
	  (*env)->ReleaseByteArrayElements(env,psDataIn,data,0);
	   LOGI("NDK_AlgSHA11 ret[%d]",ret);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_AlgN
 * Method:    NDK_AlgSHA256
 * Signature: ([BI[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_AlgN_NDK_1AlgSHA256
  (JNIEnv *env, jobject jo, jbyteArray psDataIn, jint len, jbyteArray psDataOut){
	 char *data = (*env)->GetByteArrayElements(env,psDataIn,JNI_FALSE);
	 int ret = -1;
     char buf[33]={0};
     ret = NDK_AlgSHA256(data,len,buf);
     if(ret == 0)
	   (*env)->SetByteArrayRegion(env,psDataOut,0,32,buf);
     (*env)->ReleaseByteArrayElements(env,psDataIn,data,0);
	 LOGI("NDK_AlgSHA2561 ret[%d]",ret);
     return ret; 
  };

/*
 * Class:     com_newland_ndk_AlgN
 * Method:    NDK_AlgSHA512
 * Signature: ([BI[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_AlgN_NDK_1AlgSHA512
  (JNIEnv *env, jobject jo, jbyteArray psDataIn, jint len, jbyteArray psDataOut){
	 char *data = (*env)->GetByteArrayElements(env,psDataIn,JNI_FALSE);
	 int ret = -1;
     char buf[65]={0};
    // ret = NDK_AlgSHA512(data,len,buf);
     if(ret == 0)
	   (*env)->SetByteArrayRegion(env,psDataOut,0,64,buf);
     (*env)->ReleaseByteArrayElements(env,psDataIn,data,0);
     return ret; 
  };

/*
 * Class:     com_newland_ndk_AlgN
 * Method:    NDK_AlgRSAKeyPairGen
 * Signature: (IILcom/newland/ndk/h/ST_RSA_PUBLIC_KEY;Lcom/newland/ndk/h/ST_RSA_PRIVATE_KEY;)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_AlgN_NDK_1AlgRSAKeyPairGen
  (JNIEnv *env, jobject jo, jint nProtoKeyBit, jint nPubEType, jobject pstPublicKeyOut, jobject pstPrivateKeyOut){
	  ST_RSA_PUBLIC_KEY   pubkey={0};
	  ST_RSA_PRIVATE_KEY  prikey={0};
	  int ret = -1;
	 // ret = NDK_AlgRSAKeyPairGen(nProtoKeyBit,nPubEType,&pubkey,&prikey);
	  if(ret == 0)
	  {
		 jclass pubcls = (*env)->GetObjectClass(env, pstPublicKeyOut); 
		 jfieldID bits = (*env)->GetFieldID(env, pubcls, "bits", "S");
		 jfieldID modulus = (*env)->GetFieldID(env, pubcls, "modulus", "[B");
		 jfieldID exponent = (*env)->GetFieldID(env, pubcls, "exponent", "[B");
		 
	  }
	  return 0;
  };

/*
 * Class:     com_newland_ndk_AlgN
 * Method:    NDK_AlgRSARecover
 * Signature: ([BIBBB)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_AlgN_NDK_1AlgRSARecover
  (JNIEnv *, jobject, jbyteArray, jint, jbyte, jbyte, jbyte);

/*
 * Class:     com_newland_ndk_AlgN
 * Method:    NDK_AlgRSAKeyPairVerify
 * Signature: (Lcom/newland/ndk/h/ST_RSA_PUBLIC_KEY;Lcom/newland/ndk/h/ST_RSA_PRIVATE_KEY;)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_AlgN_NDK_1AlgRSAKeyPairVerify
  (JNIEnv *, jobject, jobject, jobject);

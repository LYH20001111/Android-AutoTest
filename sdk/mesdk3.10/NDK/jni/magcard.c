#include <jni.h>
#include "ndk.h"
#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <unistd.h>

 /*
 * Class:     com_newland_ndk_MagCard
 * Method:    NDK_MagOpen
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_MagCard_NDK_1MagOpen
  (JNIEnv *env, jobject jo)
{
	return NDK_MagOpen();
}

/*
 * Class:     com_newland_ndk_MagCard
 * Method:    NDK_MagClose
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_MagCard_NDK_1MagClose
  (JNIEnv *env, jobject jo){
	return  NDK_MagClose();
}

/*
 * Class:     com_newland_ndk_MagCard
 * Method:    NDK_MagReset
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_MagCard_NDK_1MagReset
  (JNIEnv *env, jobject jo){
	  return NDK_MagReset();
  }

/*
 * Class:     com_newland_ndk_MagCard
 * Method:    NDK_MagSwiped
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_MagCard_NDK_1MagSwiped
  (JNIEnv *env, jobject jo, jbyteArray arr){
	  unsigned char g_ucMagSwiped = 0;
	  int ret;
	  ret = NDK_MagSwiped(&g_ucMagSwiped);
	  (*env)->SetByteArrayRegion(env, arr, 0, 1, &g_ucMagSwiped);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_MagCard
 * Method:    NDK_MagReadNormal
 * Signature: ([B[B[B[I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_MagCard_NDK_1MagReadNormal
  (JNIEnv *env, jobject jo, jbyteArray pszTk1, jbyteArray pszTk2, jbyteArray pszTk3, jintArray errcode){
	int ret = 0;
	char g_szTrack1[128];
	char g_szTrack2[200];
	char g_szTrack3[200];
	int nerrcode = 0;
	ret = NDK_MagReadNormal(g_szTrack1,g_szTrack2,g_szTrack3,&nerrcode);
	(*env)->SetIntArrayRegion(env, errcode, 0, 1, &nerrcode);
	(*env)->SetByteArrayRegion(env, pszTk1, 0, strlen(g_szTrack1), g_szTrack1);
	(*env)->SetByteArrayRegion(env, pszTk2, 0, strlen(g_szTrack2), g_szTrack2);
	(*env)->SetByteArrayRegion(env, pszTk3, 0, strlen(g_szTrack3), g_szTrack3);
	return ret; 
  };

/*
 * Class:     com_newland_ndk_MagCard
 * Method:    NDK_MagReadRaw
 * Signature: ([B[S[B[S[B[S)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_MagCard_NDK_1MagReadRaw
  (JNIEnv *env, jobject jo, jbyteArray pszTk1, jshortArray pszTk1len, jbyteArray pszTk2, jshortArray pszTk2len, jbyteArray pszTk3, jshortArray pszTk3len){
	unsigned char g_szTrack1[128];
	unsigned char g_szTrack2[200];
	unsigned char g_szTrack3[200]; 
	unsigned short pusTk1Len = 0,pusTk2Len = 0 ,pusTk3Len = 0;
	int ret = 0;
	ret = NDK_MagReadRaw(g_szTrack1, &pusTk1Len, g_szTrack2, &pusTk2Len,g_szTrack3, &pusTk3Len);
	(*env)->SetShortArrayRegion(env, pszTk1len, 0, 1, &pusTk1Len);	
	(*env)->SetShortArrayRegion(env, pszTk2len, 0, 1, &pusTk2Len);	
	(*env)->SetShortArrayRegion(env, pszTk3len, 0, 1, &pusTk3Len);
	(*env)->SetByteArrayRegion(env, pszTk1, 0, pusTk1Len, g_szTrack1);
	(*env)->SetByteArrayRegion(env, pszTk2, 0, pusTk2Len, g_szTrack2);
	(*env)->SetByteArrayRegion(env, pszTk3, 0, pusTk3Len, g_szTrack3);	
	return ret;
  };

/*
 * Class:     com_newland_ndk_MagCard
 * Method:    NDK_MagReadRawData
 * Signature: (IIII[B[I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_MagCard_NDK_1MagReadRawData
  (JNIEnv *env, jobject jo, jint type, jint track, jint off, jint len, jbyteArray data, jintArray datalen){
	unsigned int readlen = 0;
	unsigned char buf[4000] = {0};
	int ret = 0;
	ret = NDK_MagReadRawData(type, track, off, len, buf, &readlen);
	(*env)->SetIntArrayRegion(env, datalen, 0, 1, &readlen);	 
	(*env)->SetByteArrayRegion(env, data, 0, readlen, buf);
	return ret;
  };

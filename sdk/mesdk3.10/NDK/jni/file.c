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
 * Class:     com_newland_ndk_FileN
 * Method:    NDK_FsOpen
 * Signature: (Ljava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_FileN_NDK_1FsOpen
  (JNIEnv *env, jobject jo, jstring pszName, jstring pszMode){
	  char *name = (*env)->GetStringUTFChars(env, pszName, 0);
	  char *mode = (*env)->GetStringUTFChars(env, pszMode, 0);
	  int ret = NDK_FsOpen(name,mode);
	  (*env)->ReleaseStringUTFChars(env, pszName, name);
	  (*env)->ReleaseStringUTFChars(env, pszName, mode);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_FileN
 * Method:    NDK_FsClose
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_FileN_NDK_1FsClose
  (JNIEnv *env, jobject jo, jint handle){
	  return NDK_FsClose(handle);
  };

/*
 * Class:     com_newland_ndk_FileN
 * Method:    NDK_FsRead
 * Signature: (I[BI)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_FileN_NDK_1FsRead
  (JNIEnv *env, jobject jo, jint handle, jbyteArray readbuf, jint readlen){
	  char buf[4096]={0};
	  int ret = -1;
	  ret = NDK_FsRead(handle,buf,readlen);
	  if(ret > 0)
		(*env)->SetByteArrayRegion(env, readbuf, 0, ret, buf);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_FileN
 * Method:    NDK_FsWrite
 * Signature: (I[BI)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_FileN_NDK_1FsWrite
  (JNIEnv *env, jobject jo, jint handle, jbyteArray writebuf, jint writelen){
	  char *buf = (*env)->GetByteArrayElements(env,writebuf,JNI_FALSE);
	  int ret = NDK_FsWrite(handle,buf,writelen);
	  (*env)->ReleaseByteArrayElements(env,writebuf,buf,0);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_FileN
 * Method:    NDK_FsSeek
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_FileN_NDK_1FsSeek
  (JNIEnv *env, jobject jo, jint handle, jint ulDistance, jint unPosition){
	  return NDK_FsSeek(handle,ulDistance,unPosition);
  };

/*
 * Class:     com_newland_ndk_FileN
 * Method:    NDK_FsDel
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_FileN_NDK_1FsDel
  (JNIEnv *env, jobject jo, jstring pszName){
	  char *name = (*env)->GetStringUTFChars(env, pszName, 0);
	  int ret = NDK_FsDel(name);
	  (*env)->ReleaseStringUTFChars(env, pszName, name);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_FileN
 * Method:    NDK_FsFileSize
 * Signature: (Ljava/lang/String;[I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_FileN_NDK_1FsFileSize
  (JNIEnv *env, jobject jo, jstring pszName, jintArray filelen){
	  char *name = (*env)->GetStringUTFChars(env, pszName, 0);
	  int len = 0;
	  int ret = NDK_FsFileSize(name,&len);
	  (*env)->SetIntArrayRegion(env,filelen,0,1,&len);
	  (*env)->ReleaseStringUTFChars(env,pszName,name);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_FileN
 * Method:    NDK_FsExist
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_FileN_NDK_1FsExist
  (JNIEnv *env, jobject jo, jstring pszName){
	  char *name = (*env)->GetStringUTFChars(env, pszName, 0);
	  int ret = NDK_FsExist(name);
	  (*env)->ReleaseStringUTFChars(env,pszName,name);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_FileN
 * Method:    NDK_FsTruncate
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_FileN_NDK_1FsTruncate
  (JNIEnv *env, jobject jo, jstring pszName, jint filelen){
	  char *name = (*env)->GetStringUTFChars(env, pszName, 0);
	  int ret = NDK_FsTruncate(name,filelen);
	  (*env)->ReleaseStringUTFChars(env,pszName,name);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_FileN
 * Method:    NDK_FsTell
 * Signature: (I[I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_FileN_NDK_1FsTell
  (JNIEnv *env, jobject jo, jint handle, jintArray pulRet){
	  int len = 0;
	  int ret = NDK_FsTell(handle,&len);
	  (*env)->SetIntArrayRegion(env,pulRet,0,1,&len);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_FileN
 * Method:    NDK_FsRename
 * Signature: (Ljava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_FileN_NDK_1FsRename
  (JNIEnv *env, jobject jo, jstring pszSrcName, jstring pszDstName){
	 char *srcname = (*env)->GetStringUTFChars(env, pszSrcName, 0); 
	 char *desname = (*env)->GetStringUTFChars(env, pszDstName, 0); 
	 int ret = NDK_FsRename(srcname,desname);
	 (*env)->ReleaseStringUTFChars(env,pszSrcName,srcname);
	 (*env)->ReleaseStringUTFChars(env,pszDstName,desname);
	 return ret;
  };

/*
 * Class:     com_newland_ndk_FileN
 * Method:    NDK_FsFormat
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_FileN_NDK_1FsFormat
  (JNIEnv *env, jobject jo){
	  return NDK_FsFormat();
  };

/*
 * Class:     com_newland_ndk_FileN
 * Method:    NDK_CopyFileToSecMod
 * Signature: (Ljava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_FileN_NDK_1CopyFileToSecMod
  (JNIEnv *, jobject, jstring, jstring);

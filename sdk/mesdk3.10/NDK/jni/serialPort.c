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
 * Class:     com_newland_ndk_SerialPort
 * Method:    NDK_PortOpen_m
 * Signature: (I[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SerialPort_NDK_1PortOpen_1m(
		JNIEnv *env, jobject jo, jint emPort, jbyteArray buffer)
{	
	char *pszAttr = NULL,*pszAttr2;
	int ret = NDK_ERR;
	pszAttr = (*env)->GetByteArrayElements(env,buffer,NULL);	
	if(pszAttr == NULL){
	    LOGI("NDK_PortOpen get buffer fail !!!");
		return NDK_ERR;
	}
	int len = (*env)->GetArrayLength(env, buffer);
	pszAttr2 = malloc(sizeof(char) * len + 1);
	if (pszAttr2 == NULL)
	{
		(*env)->ReleaseByteArrayElements(env,buffer,pszAttr,NULL);
		return NDK_ERR;
	}
	memcpy(pszAttr2,pszAttr,len);
	pszAttr2[len] = '\0';
	LOGI("NDK_PortOpen emPort[%d] pszAttr2[%s]",emPort,pszAttr2);	
	ret = NDK_PortOpen(emPort, pszAttr2);
	(*env)->ReleaseByteArrayElements(env,buffer,pszAttr,NULL);
	if(pszAttr2)
		free(pszAttr2);
	return ret;
}

/*
 * Class:     com_newland_ndk_SerialPort
 * Method:    NDK_PortClose_m
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SerialPort_NDK_1PortClose_1m(
	JNIEnv *env, jobject obj, jint emPort)
{
	LOGI("NDK_PortClose emPort[%d]",emPort);	
	return NDK_PortClose(emPort);
}

/*
 * Class:     com_newland_ndk_SerialPort
 * Method:    NDK_PortRead_m
 * Signature: (II[BI[I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SerialPort_NDK_1PortRead_1m(
	JNIEnv *env, jobject obj, jint emPort, jint unLen, jbyteArray pszOutBuf, jint nTimeoutMs, jintArray pnReadLen)
{
	int ret = NDK_ERR;
	int  readLen  = 0;
	char outBuf[1024*4+1];
//	LOGI("NDK_PortRead emPort[%d] unLen[%d] nTimeoutMs[%d]",emPort,unLen,nTimeoutMs);
	memset(outBuf,0,sizeof(outBuf));
	ret = NDK_PortRead(emPort,unLen,outBuf,nTimeoutMs,&readLen);
	if(ret == NDK_OK || ret == NDK_ERR_TIMEOUT){
		(*env)->SetIntArrayRegion(env,pnReadLen,0,1,&readLen);
		(*env)->SetByteArrayRegion(env,pszOutBuf,0,readLen,outBuf);
	}
	return ret;
}

/*
 * Class:     com_newland_ndk_SerialPort
 * Method:    NDK_PortWrite_m
 * Signature: (II[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SerialPort_NDK_1PortWrite_1m(
	JNIEnv *env, jobject obj, jint emPort, jint unLen, jbyteArray pszInbuf)

{
	int ret = NDK_ERR;
//	LOGI("NDK_PortWrite emPort[%d] unLen[%d]",emPort,unLen);
	char *pInbuf = NULL;
	pInbuf = (*env)->GetByteArrayElements(env,pszInbuf,NULL);	
	if(pInbuf == NULL){
		return NDK_ERR;
	}
	ret = NDK_PortWrite(emPort,unLen,pInbuf);	
	(*env)->ReleaseByteArrayElements(env,pszInbuf,pInbuf,NULL);
	return ret;
}

/*
 * Class:     com_newland_ndk_SerialPort
 * Method:    NDK_PortTxSendOver_m
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SerialPort_NDK_1PortTxSendOver_1m(
	JNIEnv *env, jobject obj, jint emPort)
{
	int ret = NDK_ERR;
	LOGI("NDK_PortTxSendOver emPort[%d]",emPort);	
	ret = NDK_PortTxSendOver(emPort);
	return ret;
}

/*
 * Class:     com_newland_ndk_SerialPort
 * Method:    NDK_PortClrBuf_m
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SerialPort_NDK_1PortClrBuf_1m(
	JNIEnv *env, jobject obj, jint emPort)
{
	int ret = NDK_ERR;
	LOGI("NDK_PortClrBuf emPort[%d]",emPort);
	ret = NDK_PortClrBuf(emPort);
	return ret;
}

/*
 * Class:     com_newland_ndk_SerialPort
 * Method:    NDK_PortReadLen
 * Signature: (I[I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SerialPort_NDK_1PortReadLen(
	JNIEnv *env, jobject obj, jint emPort, jintArray pnReadLen)
{
	int  readLen = 0,ret = 0;
	LOGI("NDK_PortReadLen emPort[%d]",emPort);
	ret = NDK_PortReadLen(emPort,&readLen);
	(*env)->SetIntArrayRegion(env,pnReadLen,0,1,&readLen);
	return ret;
}

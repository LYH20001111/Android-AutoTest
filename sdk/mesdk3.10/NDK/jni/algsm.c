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
 * Class:     com_newland_smmanager_SmManager
 * Method:    NDK_Init
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_newland_smmanager_SmManager_NDK_1Init
  (JNIEnv *env, jobject obj)
{
	LOGI("NDK_Init In SmManager!");
	return NDK_Init();
}

/*
 * Class:     com_newland_smmanager_SmManager
 * Method:    NDK_SecGetCfg
 * Signature: ([I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_smmanager_SmManager_NDK_1SecGetCfg
  (JNIEnv *env, jobject jo, jintArray cfg){
	  int mcfg = 0;
	  int ret = -1;
	  ret = NDK_SecGetCfg(&mcfg);
	  if(ret == 0)
		  (*env)->SetIntArrayRegion(env,cfg,0,1,&mcfg);
	  LOGI("NDK_SecGetCfg ret[%d]",ret);
	  return ret;
  };

/*
 * Class:     com_newland_smmanager_SmManager
 * Method:    NDK_SecSetCfg
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_smmanager_SmManager_NDK_1SecSetCfg
  (JNIEnv *env, jobject jo, jint cfg){   		
	  int ret = -1;
	  ret = NDK_SecSetCfg(cfg);
	  LOGI("NDK_SecSetCfg sret[%d]",ret);
	  return ret;
  };

/*
 * Class:     com_newland_smmanager_SmManager
 * Method:    NDK_AlgSHA1
 * Signature: ([BI[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_smmanager_SmManager_NDK_1AlgSHA1
  (JNIEnv *env, jobject jo, jbyteArray psDataIn, jint len, jbyteArray psDataOut){
	  char *data = (*env)->GetByteArrayElements(env,psDataIn,JNI_FALSE);
	  int ret = -1;
	  char buf[21]={0};
	  ret = NDK_AlgSHA1(data,len,buf);
	  if(ret == 0)
		  (*env)->SetByteArrayRegion(env,psDataOut,0,20,buf);
	  (*env)->ReleaseByteArrayElements(env,psDataIn,data,0);
	  LOGI("NDK_AlgSHA1 ret[%d]",ret);
	  return ret;
  };

/*
 * Class:     com_newland_smmanager_SmManager
 * Method:    NDK_AlgSHA256
 * Signature: ([BI[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_smmanager_SmManager_NDK_1AlgSHA256
  (JNIEnv *env, jobject jo, jbyteArray psDataIn, jint len, jbyteArray psDataOut){
	 char *data = (*env)->GetByteArrayElements(env,psDataIn,JNI_FALSE);
	 int ret = -1;
     char buf[33]={0};
     ret = NDK_AlgSHA256(data,len,buf);
     if(ret == 0)
	   (*env)->SetByteArrayRegion(env,psDataOut,0,32,buf);
     (*env)->ReleaseByteArrayElements(env,psDataIn,data,0);
	 LOGI("NDK_AlgSHA256 ret[%d]",ret);
     return ret; 
  };

/*
 * Class:     com_newland_smmanager_SmManager
 * Method:    NDK_AlgSHA512
 * Signature: ([BI[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_smmanager_SmManager_NDK_1AlgSHA512
  (JNIEnv *env, jobject jo, jbyteArray psDataIn, jint len, jbyteArray psDataOut){
	 char *data = (*env)->GetByteArrayElements(env,psDataIn,JNI_FALSE);
	 int ret = -1;
     char buf[65]={0};
     ret = NDK_AlgSHA512(data,len,buf);
     if(ret == 0)
	   (*env)->SetByteArrayRegion(env,psDataOut,0,64,buf);
     (*env)->ReleaseByteArrayElements(env,psDataIn,data,0); 
	 LOGI("NDK_AlgSHA512 ret[%d]",ret);
     return ret; 
  };

  
/*
 * Class:     com_newland_smmanager_SmManager
 * Method:    NDK_AlgRSAKeyPairGen_m
 * Signature: (II[S[B[B[S[B[B[B[B[B[B[B[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_smmanager_SmManager_NDK_1AlgRSAKeyPairGen_1m
  (JNIEnv *env, jobject jo, jint nProtoKeyBit, jint nPubEType, jshortArray pbbits, jbyteArray pbmoud, jbyteArray pbex, jshortArray prbits, jbyteArray modulus, jbyteArray publicExponent, jbyteArray exponent, jbyteArray prime0, jbyteArray prime1, jbyteArray primeExponent0, jbyteArray primeExponent1, jbyteArray coefficient){
	  ST_RSA_PUBLIC_KEY   pubkey={0};
	  ST_RSA_PRIVATE_KEY  prikey={0};
	  int ret = -1;
	  ret = NDK_AlgRSAKeyPairGen(nProtoKeyBit,nPubEType,&pubkey,&prikey);
	  if(ret == 0)
	  {
		  (*env)->SetShortArrayRegion(env,pbbits,0,1,&pubkey.bits);
		  (*env)->SetByteArrayRegion(env,pbmoud,0,513,pubkey.modulus);
		  (*env)->SetByteArrayRegion(env,pbex,0,513,pubkey.exponent);
		  (*env)->SetShortArrayRegion(env,prbits,0,1,&prikey.bits);
		  (*env)->SetByteArrayRegion(env,modulus,0,513,prikey.modulus);
		  (*env)->SetByteArrayRegion(env,publicExponent,0,513,prikey.publicExponent);
		  (*env)->SetByteArrayRegion(env,exponent,0,513,prikey.exponent);
		  (*env)->SetByteArrayRegion(env,prime0,0,257,prikey.prime[0]);
		  (*env)->SetByteArrayRegion(env,prime1,0,257,prikey.prime[1]);
		  (*env)->SetByteArrayRegion(env,primeExponent0,0,257,prikey.primeExponent[0]);
		  (*env)->SetByteArrayRegion(env,primeExponent1,0,257,prikey.primeExponent[1]);
		  (*env)->SetByteArrayRegion(env,coefficient,0,257,prikey.coefficient);
	  }  
	  LOGI("NDK_AlgRSAKeyPairGen ret[%d]",ret);
	  return ret;
  };

/*
 * Class:     com_newland_smmanager_SmManager
 * Method:    NDK_AlgRSAKeyPairGen
 * Signature: (IILcom/newland/smmanager/assistant/ST_RSA_PUBLIC_KEY;Lcom/newland/smmanager/assistant/ST_RSA_PRIVATE_KEY;)I
 */
/*JNIEXPORT jint JNICALL Java_com_newland_smmanager_SmManager_NDK_1AlgRSAKeyPairGen
  (JNIEnv *env, jobject jo, jint nProtoKeyBit, jint nPubEType, jobject pstPublicKeyOut, jobject pstPrivateKeyOut){
	  ST_RSA_PUBLIC_KEY   pubkey={0};
	  ST_RSA_PRIVATE_KEY  prikey={0};
	  int ret = -1;
	  ret = NDK_AlgRSAKeyPairGen(nProtoKeyBit,nPubEType,&pubkey,&prikey);
	  if(ret == 0)
	  {
		 
		 jclass pubcls = (*env)->GetObjectClass(env, pstPublicKeyOut); 
		 jfieldID bits = (*env)->GetFieldID(env, pubcls, "bits", "S");
		 jshort bit = pubkey.bits;
		 (*env)->SetObjectField(env,pstPublicKeyOut,bits,bit);
		 jfieldID modulus = (*env)->GetFieldID(env, pubcls, "modulus", "[B");
		 jbyteArray m = (*env)->NewByteArray(env,513);
		 (*env)->SetByteArrayRegion(env,m,0,513,pubkey.modulus);
		 (*env)->SetObjectField(env,pstPublicKeyOut,modulus,m);
		 jfieldID exponent = (*env)->GetFieldID(env, pubcls, "exponent", "[B");
		 jbyteArray m = (*env)->NewByteArray(env,513);
		 (*env)->SetByteArrayRegion(env,m,0,513,pubkey.modulus);
		 (*env)->SetObjectField(env,pstPublicKeyOut,modulus,m);
		 
	  }
	  return ret;
  };
*/
/*
 * Class:     com_newland_smmanager_SmManager
 * Method:    NDK_AlgRSARecover
 * Signature: ([BI[B[B[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_smmanager_SmManager_NDK_1AlgRSARecover
  (JNIEnv *env, jobject jo, jbyteArray psModule, jint nModuleLen, jbyteArray psExp, jbyteArray psDataIn, jbyteArray psDataOut)
  {
	  jbyte *module = (*env)->GetByteArrayElements(env,psModule,JNI_FALSE);
	  jbyte *exp = (*env)->GetByteArrayElements(env,psExp,JNI_FALSE);
	  jbyte *data = (*env)->GetByteArrayElements(env,psDataIn,JNI_FALSE);
	  char mmoudle[513] = {0};
	  char mexp[513] = {0};
	  char mdata[513] = {0};
      int mlen = (*env)->GetArrayLength(env,psModule);
	  int mexplen = (*env)->GetArrayLength(env,psExp);
	  int mdatalen = (*env)->GetArrayLength(env,psDataIn);
	  int ret = -1;
	  char out[2048] = {0};
	  if(mlen > 0 && mexplen > 0 && mdatalen > 0)
	  {
		  memcpy(mmoudle,module,mlen);
		  memcpy(mexp,exp,mexplen);
		  memcpy(mdata,data,mdatalen);
		  ret = NDK_AlgRSARecover(mmoudle,nModuleLen,mexp,mdata,out);
	  }else
		  ret = -1;
	  
	  if(ret == 0)
		  (*env)->SetByteArrayRegion(env,psDataOut,0,nModuleLen,out);
	  (*env)->ReleaseByteArrayElements(env,psModule,module,0);
	  (*env)->ReleaseByteArrayElements(env,psExp,exp,0);
	  (*env)->ReleaseByteArrayElements(env,psDataIn,data,0);	  
	  LOGI("NDK_AlgRSARecover ret[%d]",ret);
	  return ret;
  };

 /*
 * Class:     com_newland_smmanager_SmManager
 * Method:    NDK_AlgRSAKeyPairVerify_m
 * Signature: (S[B[BS[B[B[B[B[B[B[B[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_smmanager_SmManager_NDK_1AlgRSAKeyPairVerify_1m
  (JNIEnv *env, jobject jo, jshort pbbits, jbyteArray pbmoud, jbyteArray pbex, jshort prbits, jbyteArray modulus, jbyteArray publicExponent, jbyteArray exponent, jbyteArray prime0, jbyteArray prime1, jbyteArray primeExponent0, jbyteArray primeExponent1, jbyteArray coefficient){
	  ST_RSA_PUBLIC_KEY   pubkey={0};
	  ST_RSA_PRIVATE_KEY  prikey={0};
	  pubkey.bits = pbbits;
	  prikey.bits = prbits;
	  jbyte *jb = NULL;
	  int len = 0;
	  int ret = -1;
	  
	  jb = (*env)->GetByteArrayElements(env,pbmoud,JNI_FALSE);
	  len = (*env)->GetArrayLength(env,pbmoud);
	  memcpy(pubkey.modulus,jb,len);
	  (*env)->ReleaseByteArrayElements(env,pbmoud,jb,0);
	  
	  jb = (*env)->GetByteArrayElements(env,pbex,JNI_FALSE);
	  len = (*env)->GetArrayLength(env,pbex);
	  memcpy(pubkey.exponent,jb,len);
	  (*env)->ReleaseByteArrayElements(env,pbex,jb,0);
	  
	  jb = (*env)->GetByteArrayElements(env,modulus,JNI_FALSE);
	  len = (*env)->GetArrayLength(env,modulus);
	  memcpy(prikey.modulus,jb,len);
	  (*env)->ReleaseByteArrayElements(env,modulus,jb,0);
	  
	  jb = (*env)->GetByteArrayElements(env,publicExponent,JNI_FALSE);
	  len = (*env)->GetArrayLength(env,publicExponent);
	  memcpy(prikey.publicExponent,jb,len);
	  (*env)->ReleaseByteArrayElements(env,publicExponent,jb,0);
	  
	  jb = (*env)->GetByteArrayElements(env,exponent,JNI_FALSE);
	  len = (*env)->GetArrayLength(env,exponent);
	  memcpy(prikey.exponent,jb,len);
	  (*env)->ReleaseByteArrayElements(env,exponent,jb,0);
	  
	  jb = (*env)->GetByteArrayElements(env,prime0,JNI_FALSE);
	  len = (*env)->GetArrayLength(env,prime0);
	  memcpy(prikey.prime[0],jb,len);
	  (*env)->ReleaseByteArrayElements(env,prime0,jb,0);
	  
	  jb = (*env)->GetByteArrayElements(env,prime1,JNI_FALSE);
	  len = (*env)->GetArrayLength(env,prime1);
	  memcpy(prikey.prime[1],jb,len);
	  (*env)->ReleaseByteArrayElements(env,prime1,jb,0);
	  
	  jb = (*env)->GetByteArrayElements(env,primeExponent0,JNI_FALSE);
	  len = (*env)->GetArrayLength(env,primeExponent0);
	  memcpy(prikey.primeExponent[0],jb,len);
	  (*env)->ReleaseByteArrayElements(env,primeExponent0,jb,0);
	  
	  jb = (*env)->GetByteArrayElements(env,primeExponent1,JNI_FALSE);
	  len = (*env)->GetArrayLength(env,primeExponent1);
	  memcpy(prikey.primeExponent[1],jb,len);
	  (*env)->ReleaseByteArrayElements(env,primeExponent1,jb,0);
	  
	  jb = (*env)->GetByteArrayElements(env,coefficient,JNI_FALSE);
	  len = (*env)->GetArrayLength(env,coefficient);
	  memcpy(prikey.coefficient,jb,len);
	  (*env)->ReleaseByteArrayElements(env,coefficient,jb,0);
	  
	  jb = NULL;
	  ret = NDK_AlgRSAKeyPairVerify(&pubkey,&prikey);
	  LOGI("NDK_AlgRSAKeyPairVerify ret[%d]",ret);
	  return ret;
  };

/*
 * Class:     com_newland_smmanager_SmManager
 * Method:    NDK_AlgRSAKeyPairVerify
 * Signature: (Lcom/newland/smmanager/assistant/ST_RSA_PUBLIC_KEY;Lcom/newland/smmanager/assistant/ST_RSA_PRIVATE_KEY;)I
 */
/*JNIEXPORT jint JNICALL Java_com_newland_smmanager_SmManager_NDK_1AlgRSAKeyPairVerify
  (JNIEnv *, jobject, jobject, jobject);*/

/*
 * Class:     com_newland_smmanager_SmManager
 * Method:    NDK_AlgSM2KeyPairGen
 * Signature: ([B[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_smmanager_SmManager_NDK_1AlgSM2KeyPairGen
  (JNIEnv *env, jobject jo, jbyteArray eccpubkey, jbyteArray eccprikey)
  {
	  char pubkey[65] = {0};
	  char prikey[33] = {0};
	  int ret = -1;
	  ret = NDK_AlgSM2KeyPairGen(pubkey,prikey);
	  if(ret == 0)
	  {
		  (*env)->SetByteArrayRegion(env,eccpubkey,0,64,pubkey);
		  (*env)->SetByteArrayRegion(env,eccprikey,0,32,prikey);
	  }	  
	  LOGI("NDK_AlgSM2KeyPairGen ret[%d]",ret);
	  return ret;
  };

/*
 * Class:     com_newland_smmanager_SmManager
 * Method:    NDK_AlgSM2Encrypt
 * Signature: ([B[BI[B[I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_smmanager_SmManager_NDK_1AlgSM2Encrypt
  (JNIEnv *env, jobject jo, jbyteArray eccpubkey, jbyteArray Message, jint MessageLen, jbyteArray Crypto, jintArray CryptoLen)
  {
	  char *pubkey = (*env)->GetByteArrayElements(env,eccpubkey,JNI_FALSE);
	  char *mess = (*env)->GetByteArrayElements(env,Message,JNI_FALSE);
	  int ret = -1;
	  int len = 0;
	  char out[1024] = {0};
	  ret = NDK_AlgSM2Encrypt(pubkey,mess,MessageLen,out,&len);
	  if(ret == 0)
	  {
		  (*env)->SetByteArrayRegion(env,Crypto,0,len,out);
		  (*env)->SetIntArrayRegion(env,CryptoLen,0,1,&len);
	  }
	  (*env)->ReleaseByteArrayElements(env,eccpubkey,pubkey,0);
	  (*env)->ReleaseByteArrayElements(env,Message,mess,0);
	  LOGI("NDK_AlgSM2Encrypt ret[%d]",ret);
	  return ret;
  };

/*
 * Class:     com_newland_smmanager_SmManager
 * Method:    NDK_AlgSM2Decrypt
 * Signature: ([B[BI[B[I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_smmanager_SmManager_NDK_1AlgSM2Decrypt
  (JNIEnv *env, jobject jo, jbyteArray eccprikey, jbyteArray Crypto, jint CryptoLen, jbyteArray Message, jintArray MessageLen)
  {
	  char *prikey = (*env)->GetByteArrayElements(env,eccprikey,JNI_FALSE);
	  char *cro = (*env)->GetByteArrayElements(env,Crypto,JNI_FALSE);
	  int ret = -1;
	  int len = 0;
	  char out[1024] = {0};
	  ret = NDK_AlgSM2Decrypt(prikey,cro,CryptoLen,out,&len);
	  if(ret == 0)
	  {
		  (*env)->SetByteArrayRegion(env,Message,0,len,out);
		  (*env)->SetIntArrayRegion(env,MessageLen,0,1,&len);
	  }
	  (*env)->ReleaseByteArrayElements(env,eccprikey,prikey,0);
	  (*env)->ReleaseByteArrayElements(env,Crypto,cro,0);
	  LOGI("NDK_AlgSM2Decrypt ret[%d]",ret);
	  return ret;
  };

/*
 * Class:     com_newland_smmanager_SmManager
 * Method:    NDK_AlgSM2Sign
 * Signature: ([B[B[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_smmanager_SmManager_NDK_1AlgSM2Sign
  (JNIEnv *env, jobject jo, jbyteArray eccprikey, jbyteArray e, jbyteArray output)
  {
	  char *prikey = (*env)->GetByteArrayElements(env,eccprikey,JNI_FALSE);
	  char *me = (*env)->GetByteArrayElements(env,e,JNI_FALSE);
	  char out[65] = {0};
	  int ret = -1;
	  ret = NDK_AlgSM2Sign(prikey,me,out);
	  if(ret == 0)
		  (*env)->SetByteArrayRegion(env,output,0,64,out);
	  (*env)->ReleaseByteArrayElements(env,eccprikey,prikey,0);
	  (*env)->ReleaseByteArrayElements(env,e,me,0);
	  LOGI("NDK_AlgSM2Sign ret[%d]",ret);
	  return ret;
  };

/*
 * Class:     com_newland_smmanager_SmManager
 * Method:    NDK_AlgSM2Verify
 * Signature: ([B[B[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_smmanager_SmManager_NDK_1AlgSM2Verify
  (JNIEnv *env, jobject jo, jbyteArray pPublicKey, jbyteArray e, jbyteArray pSignedData){
	  char *pubkey = (*env)->GetByteArrayElements(env,pPublicKey,JNI_FALSE);
	  char *me = (*env)->GetByteArrayElements(env,e,JNI_FALSE);
	  char *data = (*env)->GetByteArrayElements(env,pSignedData,JNI_FALSE);
	  int ret = -1;
	  ret = NDK_AlgSM2Verify(pubkey, me, data);
	  (*env)->ReleaseByteArrayElements(env,e,me,0);
	  (*env)->ReleaseByteArrayElements(env,pPublicKey,pubkey,0);
	  (*env)->ReleaseByteArrayElements(env,pSignedData,data,0);
	  LOGI("NDK_AlgSM2Verify ret[%d]",ret);
	  return ret;
  };

/*
 * Class:     com_newland_smmanager_SmManager
 * Method:    NDK_AlgSM2GenE
 * Signature: (I[BI[B[B[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_smmanager_SmManager_NDK_1AlgSM2GenE
  (JNIEnv *env, jobject jo, jint usID, jbyteArray pID, jint usM, jbyteArray pM, jbyteArray pubKey, jbyteArray pHashData){
	  char *pd = (*env)->GetByteArrayElements(env,pID,JNI_FALSE);
	  char *mpm = (*env)->GetByteArrayElements(env,pM,JNI_FALSE);
	  char *mpubkey = (*env)->GetByteArrayElements(env,pubKey,JNI_FALSE);
	  char out[65] = {0};
	  int ret = -1;
	  ret = NDK_AlgSM2GenE(usID,pd,usM,mpm,mpubkey,out);
	  if(ret == 0)
		  (*env)->SetByteArrayRegion(env,pHashData,0,32,out);
	  (*env)->ReleaseByteArrayElements(env,pID,pd,0);
	  (*env)->ReleaseByteArrayElements(env,pM,mpm,0);
	  (*env)->ReleaseByteArrayElements(env,pubKey,mpubkey,0);
	  LOGI("NDK_AlgSM2GenE ret[%d]",ret);
	  return ret;
  };

/*
 * Class:     com_newland_smmanager_SmManager
 * Method:    NDK_AlgSM3Start
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_newland_smmanager_SmManager_NDK_1AlgSM3Start
  (JNIEnv *env, jobject jo){
      int ret = NDK_AlgSM3Start();
	  LOGI("NDK_AlgSM3Start ret[%d]",ret);
	  return ret;
  };

/*
 * Class:     com_newland_smmanager_SmManager
 * Method:    NDK_AlgSM3Update
 * Signature: ([BI)I
 */
JNIEXPORT jint JNICALL Java_com_newland_smmanager_SmManager_NDK_1AlgSM3Update
  (JNIEnv *env, jobject jo, jbyteArray pDat, jint len){
	   char *data = (*env)->GetByteArrayElements(env,pDat,JNI_FALSE);
	   int ret = -1;
	   ret = NDK_AlgSM3Update(data,len);
	   (*env)->ReleaseByteArrayElements(env,pDat,data,0);
	   LOGI("NDK_AlgSM3Update ret[%d]",ret);
	   return ret;
  };

/*
 * Class:     com_newland_smmanager_SmManager
 * Method:    NDK_AlgSM3Final
 * Signature: ([BI[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_smmanager_SmManager_NDK_1AlgSM3Final
  (JNIEnv *env, jobject jo, jbyteArray pDat, jint len, jbyteArray pHashDat){
	  char *data  = (*env)->GetByteArrayElements(env,pDat,JNI_FALSE);
	  int ret = -1;
	  char out[33] = {0};
	  ret = NDK_AlgSM3Final(data,len,out);
	  if(ret == 0)
		  (*env)->SetByteArrayRegion(env,pHashDat,0,32,out);
	  (*env)->ReleaseByteArrayElements(env,pDat,data,0);
	  LOGI("NDK_AlgSM3Final ret[%d]",ret);
	  return ret;
  };

/*
 * Class:     com_newland_smmanager_SmManager
 * Method:    NDK_AlgSM3Compute
 * Signature: ([BI[B)I
 */
JNIEXPORT jint JNICALL Java_com_newland_smmanager_SmManager_NDK_1AlgSM3Compute
  (JNIEnv *env, jobject jo, jbyteArray pDat, jint len, jbyteArray pHashDat){
	  char *data  = (*env)->GetByteArrayElements(env,pDat,JNI_FALSE);
	  int ret = -1;
	  char out[33] = {0};
	  ret = NDK_AlgSM3Compute(data,len,out);
	  if(ret == 0)
		  (*env)->SetByteArrayRegion(env,pHashDat,0,32,out);
	  (*env)->ReleaseByteArrayElements(env,pDat,data,0);
	  
	  LOGI("NDK_AlgSM3Compute ret[%d]",ret);
	  return ret;
  };

/*
 * Class:     com_newland_smmanager_SmManager
 * Method:    NDK_AlgSM4Compute
 * Signature: ([B[BI[B[BB)I
 */
JNIEXPORT jint JNICALL Java_com_newland_smmanager_SmManager_NDK_1AlgSM4Compute
  (JNIEnv *env, jobject jo, jbyteArray pkey, jbyteArray pIVector, jint len, jbyteArray pSm4Input, jbyteArray pSm4Output, jbyte mode){
	  char *key = (*env)->GetByteArrayElements(env,pkey,JNI_FALSE);
	  char *piv = (*env)->GetByteArrayElements(env,pIVector,JNI_FALSE);
	  char *psm4 = (*env)->GetByteArrayElements(env,pSm4Input,JNI_FALSE);
	  char mmode = mode;
	  int ret = -1;
	  char out[4096] = {0};
	  ret = NDK_AlgSM4Compute(key,piv,len,psm4,out,mmode);
	  if(ret == 0)
		  (*env)->SetByteArrayRegion(env,pSm4Output,0,len,out);
	  (*env)->ReleaseByteArrayElements(env,pkey,key,0);
	  (*env)->ReleaseByteArrayElements(env,pIVector,piv,0);
	  (*env)->ReleaseByteArrayElements(env,pSm4Input,psm4,0);
	  
	  LOGI("NDK_AlgSM4Compute ret[%d]",ret);
	  return ret;
  };



JNIEXPORT jint JNICALL Java_com_newland_smmanager_SmManager_NDK_1AlgSM2Sign_1YS
		(JNIEnv *env, jobject jo, jint type, jbyteArray e, jbyteArray output)
{
	char *prikey = "\x29\xF8\x7F\xEF\x10\xB3\x1F\xF3\x0C\x9C\xEE\x60\xE1\x62\x0F\xA0\xBA\x14\x8C\xC9\x6F\xC0\x15\x6D\xCC\x3A\x86\xC2\xD3\xD2\x51\x25";
	char *prikey1= "\xA2\x68\xEF\x65\xED\xFC\x47\x14\x68\x6F\x6C\xCD\xAA\x1C\xD2\x6E\xB4\x74\x45\x85\x51\xC0\x1B\xFD\x80\x6B\xC2\xB1\x44\x7B\x5E\x0D";
	char *prikeyUse = NULL;
	if(type == 1){
		prikeyUse = prikey1;
	} else{
		prikeyUse = prikey;
	}
	char *me = (*env)->GetByteArrayElements(env,e,JNI_FALSE);
	char out[65] = {0};
	int ret = -1;
	ret = NDK_AlgSM2Sign(prikeyUse,me,out);
	if(ret == 0)
		(*env)->SetByteArrayRegion(env,output,0,64,out);
	(*env)->ReleaseByteArrayElements(env,e,me,0);
	LOGI("NDK_AlgSM2Sign_YS ret[%d]",ret);
	return ret;
};
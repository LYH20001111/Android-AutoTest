 /**
  * Created by hlh on 2020/8/26.
  */

#include <log.h>
#include <jni.h>
#include <string.h>
#include <malloc.h>
#include <android/log.h>
#include <ndk.h>
#include "printer.h"

JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1PrnOpenDev
        (JNIEnv *env, jobject objj) {
    int ret = NAPI_PrnOpenDev();
    return ret;
}

 JNIEXPORT jint JNICALL Java_com_newland_nsdk_core_internal_jni_NSDKJni_NAPI_1PrnCloseDev
         (JNIEnv *env, jobject objj) {
     int ret = NAPI_PrnCloseDev();
     return ret;
 }

 JNIEXPORT jint JNICALL
 Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1PrnGetStatus(JNIEnv *env, jobject thiz, jintArray status) {
     int status1;
//     int ret = NAPI_PrnGetStatus(&status1);
     int ret = NDK_PrnGetStatus(&status1);
     if(ret == 0){
         (*env)->SetIntArrayRegion(env,status,0,1,&status1);
     }
     return ret;
 }

 JNIEXPORT jint JNICALL
 Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1PrnFeedPaper(JNIEnv *env, jobject thiz) {
     int ret = NDK_PrnFeedPaper_Extern(NAPI_PRN_FEEDPAPER_AFTER);
     LOGD_FMT("NDK_PrnFeedPaper_Extern ret = %d", ret);
     return ret;
 }

 JNIEXPORT jint JNICALL
 Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1PrnFeedByPixels(JNIEnv *env, jobject thiz,
                                                                      jint pixels) {
     int ret = NDK_PrnFeedByPixel(pixels);
     LOGD_FMT("NDK_PrnFeedByPixel ret[%d]", ret);
     return ret;
 }

 JNIEXPORT jint JNICALL
 Java_com_newland_nsdk_core_internal_jni_NSDKJni_NDK_1PrnGetStatusValue(JNIEnv *env, jobject thiz,
                                                                        jintArray temperature) {
     int algValue = 0;
     int dgtAlgValue = 0;
     int ret = NDK_PrnGetStatusValue(PRN_GET_TEMP_VALUE, &algValue, &dgtAlgValue);
     LOGD_FMT("NDK_PrnGetStatusValue ret = %d, algValue[%d], dgtAlgValue[%d]", ret, algValue, dgtAlgValue);
     if (ret == NDK_OK) {
         (*env)->SetIntArrayRegion(env, temperature, 0, 1, &algValue);
     }
     return ret;
 }
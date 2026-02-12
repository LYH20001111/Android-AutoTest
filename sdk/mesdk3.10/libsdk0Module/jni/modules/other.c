#include <jni.h>
#include <stdint.h>
#include <string.h>
#include <log.h>
#include "ndk.h"

extern int g_ndkemvVerFun;
extern int g_emvspVerFun;
extern char ndkemvVersion[60];
extern char emvspVersion[60];
JNIEXPORT jint JNICALL
Java_com_newland_intelligent_jni_JniCmdInterface_getNDKEMVVersion0(JNIEnv *env, jobject thiz,
                                                                   jbyteArray ver) {
    if(g_ndkemvVerFun == 0){
        return -1;
    }
    if(ndkemvVersion != NULL){
        (*env)->SetByteArrayRegion(env,ver,0,sizeof(ndkemvVersion),ndkemvVersion);
    }
    LOGD_FMT("version.[%s]",ndkemvVersion);
    return 0;
}

JNIEXPORT jint JNICALL
Java_com_newland_intelligent_jni_JniCmdInterface_getEMVSpVersion0(JNIEnv *env, jobject thiz,
                                                                  jbyteArray ver) {
    if(g_emvspVerFun == 0){
        return -1;
    }
    if(emvspVersion != NULL){
        (*env)->SetByteArrayRegion(env,ver,0,sizeof(emvspVersion),emvspVersion);
    }
    LOGD_FMT("version.[%s]",emvspVersion);
    return 0;
}


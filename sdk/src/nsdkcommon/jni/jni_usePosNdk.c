
#define JAVA_CLASS_NAME    "com/newland/nsdk/core/common/uart3/SerialPortJni"
#define LOG_TAG "nsdkposndk"

#include <stdio.h>
#include <termios.h>
#include <fcntl.h>
#include <unistd.h>
#include <android/log.h>

#include <dlfcn.h>
#include "include/ndk.h"
#include "jni.h"
#include "util.h"

#define   _MASK(__n, __s)       (((1<<(__s))-1)<<(__n))
#define    KEY_TYPE_MASK      _MASK(0, 6)
#define    KEY_ALG_MASK       _MASK(6, 2)


#define _DEBUG 1
#ifdef _DEBUG

#define TEST_ver 1

#else
#endif

#ifndef NELEM
#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
#endif

#define INPUT_CHECK    1
#define JNI_INPUT_ERR        (-101)
#define ERRMSG_SIZE        (20)

#define JNI_OUTPUTBUF_MAX    (150)
#define    JNI_METHOD_NOT_FOUND    (64)

JNIEnv *getJNIEnv();

void *functionLib;         /*  Handle to shared lib file   */
char *dlError;        /*  Pointer to error string     */
static int rc = 0;
struct tm g_stTm;

//port
int (*NDK_PortOpen)(EM_PORT_NUM emPort, const char *pszAttr);
int (*NDK_PortClose)(EM_PORT_NUM emPort);
int (*NDK_PortRead)(EM_PORT_NUM emPort, unsigned int unLen, char *pszOutbuf,int nTimeoutMs, int *pnReadlen);
int (*NDK_PortReadLen)(EM_PORT_NUM emPort,int *pnReadLen);
int (*NDK_PortWrite)(EM_PORT_NUM emPort, unsigned int  unLen,const char *pszInbuf);
int (*NDK_PortTxSendOver)(EM_PORT_NUM emPort);
int (*NDK_PortClrBuf)(EM_PORT_NUM emPort);

int jniRegisterNativeMethods(JNIEnv *env, const char *className, const JNINativeMethod *gMethods,
                             int numMethods);

static JavaVM *sVm;

#define TAG LOG_TAG
static int NDK_Null()
{
    __android_log_print(ANDROID_LOG_INFO, TAG, "into NDK_Null %x", NDK_Null);
    return NDK_ERR_UNSUPPORT;
}

#define DLSYM(lib, foo) {        \
foo =dlsym( lib , #foo);         \
dlError = (char *)dlerror();     \
__android_log_print(ANDROID_LOG_INFO, TAG,"DLSYM "#foo" , = %x !",foo);\
if( NULL == foo ){\
foo = NDK_Null;\
rc -= 1;\
__android_log_print(ANDROID_LOG_INFO, TAG, "dlsym fail:  %s . "#foo"=%x ,ret will be %x\n", dlError,(int)foo,NDK_Null());    \
        }\
dlError = NULL;\
};                               \

void jstringToChar(JNIEnv* env, jstring jstr, char *rtn) {
    jclass clsstring = (*env)->FindClass(env, "java/lang/String");
    jstring strencode = (*env)->NewStringUTF(env, "GB2312");
    jmethodID mid = (*env)->GetMethodID(env, clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) (*env)->CallObjectMethod(env, jstr, mid, strencode);
    jsize alen = (*env)->GetArrayLength(env, barr);
    jbyte *ba = (*env)->GetByteArrayElements(env, barr, JNI_FALSE);
    if (alen > 0) {
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    (*env)->ReleaseByteArrayElements(env, barr, ba, 0);
}
jint Pos_openPort(JNIEnv *env, jobject obj,jint port, jint data1,jbyteArray buf) {

    uchar *KLA_FrameBuf = NULL;
    uint32_t bautrate = data1 & 0xFFFFFFFF;

    if (buf != NULL) {
        KLA_FrameBuf = (uchar *) (*env)->GetByteArrayElements(env, buf, NULL);
    }
    int nRet = port_init(port, bautrate, (char *) KLA_FrameBuf, NULL);

    if (KLA_FrameBuf != NULL) {
        (*env)->ReleaseByteArrayElements(env, buf, (jbyte *) KLA_FrameBuf, 0);
    }
    return (jint) nRet;
}

jint Pos_write(JNIEnv *env, jobject obj, jint fd, jbyteArray buf, jint count,jint timeout) {

    int nRet;
    LOGD_FMT("===== posz_write");

    uchar *KLA_FrameBuf = NULL;


    if (buf != NULL) {
        KLA_FrameBuf = (uchar *) (*env)->GetByteArrayElements(env, buf, NULL);
    }

    nRet = port_write(fd, (char *) KLA_FrameBuf, count,timeout);
    LOGD_FMT("Pos_Write ret = %d", nRet);
    if (KLA_FrameBuf != NULL) {
        (*env)->ReleaseByteArrayElements(env, buf, (jbyte *) KLA_FrameBuf, 0);
    }
    return (jint) nRet;
}

jint Pos_read(JNIEnv *env, jobject obj, int fd, jbyteArray buf, jint readLen,jint timeout) {

    int nRet;
    int rlen;
    uchar *KLA_FrameBuf = NULL;

    if (buf != NULL) {
        KLA_FrameBuf = (uchar *) (*env)->GetByteArrayElements(env, buf, NULL);
    }


    nRet = port_read(fd, (char *)KLA_FrameBuf, readLen,timeout);


    if (KLA_FrameBuf != NULL) {
        (*env)->ReleaseByteArrayElements(env, buf, (jbyte *) KLA_FrameBuf, 0);
    }
    return (jint) nRet;
}

jint Pos_clearBuf(JNIEnv *env, jobject obj, jint fd,jint type) {

    int nRet = -1;
    nRet = port_clearBuf(fd,type);
    return (jint) 0;
}
jint Pos_isBufferEmpty(JNIEnv *env, jobject obj, jint fd,jint type) {

    int nRet = -1;
    nRet = port_isBufferEmpty(fd,type);
    return (jint) nRet;
}

jint Pos_close(JNIEnv *env, jobject obj,jint fd) {

    int nRet = -1;
    nRet = port_close(fd);
    return (jint) nRet;
}

jint Pos_debug(JNIEnv *env, jobject obj,jint fd) {

    openDebug(fd);
    return (jint) 0;
}

jint Pos_ioctl(JNIEnv *env, jobject obj, jint fd, jint cmd, jbyteArray args) {
    int nRet = -1;
    if (args != NULL) {
        char *Args = (*env)->GetByteArrayElements(env, args, 0);
        nRet = port_ioctl(fd, cmd, Args);
        (*env)->ReleaseByteArrayElements(env, args, Args, NULL);
    } else {
        nRet = port_ioctl(fd, cmd, NULL);
    }
    return (jint) nRet;
}

jint Pos_readLen(JNIEnv *env, jobject obj, jint fd, jintArray len) {
    int readLen = 0;
    int ret = port_readLen(fd, &readLen);
    if (ret == 0) {
        (*env)->SetIntArrayRegion(env, len, 0, 1, &readLen);
    }
    return ret;
}

jint U2000_awakeExternalDevice() {
    int nRet = -1;
    nRet = u2000_awakeExternalDevice();
    return nRet;
}

jint U2000_getExternalPowerSupply() {
    int nRet = -1;
    nRet = u2000_getExternalPowerSupply();
    return nRet;
}

jint U2000_setRadarDetectionDistance(JNIEnv *env, jobject obj, jstring gain, jstring delta) {
    int nRet = -1;
    char radarGain[16] = {0};
    char strDelta[16] = {0};
    jstringToChar(env, gain, radarGain);
    jstringToChar(env, delta, strDelta);
    nRet = u2000_setRadarDetectionDistance(radarGain, strDelta);
    return nRet;
}

jint U2000_setRadarAndHeaterConfig(JNIEnv *env, jobject obj, jboolean isRadarEnable, jboolean isHeaterEnable){
    char radarConfig = '0';
    if (isRadarEnable) {
        radarConfig = '1';
    }
    char heaterConfig = '0';
    if (isHeaterEnable) {
        heaterConfig = '1';
    }
    int nRet = u2000_setRadarAndHeaterConfig(&radarConfig, &heaterConfig);
    return nRet;
}

jint U2000_setEthernetMode(JNIEnv *env, jobject obj, jint mode) {
    char ethernetMode = (mode == 1) ? '1' : (mode == 0) ? '0' : '2';
    int nRet = setEthernetMode(&ethernetMode);
    return nRet;
}

jint U2000_getEthernetMode(JNIEnv *env, jobject obj, jintArray mode) {
    int nRet = -1;
    nRet = getEthernetMode();
    if (nRet >= 0) {
        (*env)->SetIntArrayRegion(env, mode, 0, 1, &nRet);
    }
    return nRet;
}

jint portOpenWithNodeName(JNIEnv *env, jobject object, jstring port_name, jint baud_rate, jbyteArray config) {
    char nodeName[128];
    memset(nodeName, 0x00, sizeof(nodeName));
    jstringToChar(env, port_name, nodeName);
    int nRet = -1;
    int baudRate = baud_rate & 0xFFFFFFFF;
    char *pConfig = NULL;
    if (config != NULL) {
        pConfig = (*env)->GetByteArrayElements(env, config, NULL);
    }
    nRet = port_init(-1, baud_rate, pConfig, nodeName);
    return nRet;
}

jint setDebugMode(JNIEnv *env, jobject object, jint debug_mode) {
    openDebug(debug_mode);
    return 0;
}
/*
 * 从NDK_1PortOpen以下的函数都是N850PINPAD口的操作接口
 */
jint NDK_1PortOpen(JNIEnv *env, void *thiz,jint com_number,jstring config_str) {
    char *buf = (*env)->GetStringUTFChars(env, config_str, 0);
    int ret = NDK_PortOpen(com_number, buf);
    (*env)->ReleaseStringUTFChars(env, config_str, buf);
    LOGD_FMT(">>>NDK_PortOpen ret[%d]", ret);
    return ret;
}

jint NDK_1PortClose(JNIEnv *env, jobject thiz,jint com_number) {
    int ret = NDK_PortClose(com_number);
    LOGD_FMT(">>>NDK_PortClose ret[%d]", ret);
    return ret;
}

jint NDK_1PortRead(JNIEnv *env, jobject thiz,jint com_number, jint max_len,jint timeout, jbyteArray out_data,jintArray out_data_len) {
    uchar outBuf[max_len];
    int outLen = 0;
    memset(outBuf, 0, sizeof(outBuf));
    LOGD_FMT(">>>NDK_PortRead timeout[%d]", timeout);
    int ret = NDK_PortRead(com_number, max_len, outBuf, timeout, &outLen);

    LOGD_FMT(">>>outLen[%d]", outLen);

    /**
     * NDK串口数据小于maxlen时，会返回-10，并返回实际接收到的数据
     */
    if (outLen > 0) {
        (*env)->SetIntArrayRegion(env, out_data_len, 0, 1, &outLen);
        (*env)->SetByteArrayRegion(env, out_data, 0, outLen, outBuf);
    }
    LOGD_FMT(">>>NDK_PortRead ret[%d]", ret);
    return ret;
}

jint NDK_1PortReadLen(JNIEnv *env, jobject thiz, jint com_number, jintArray read_len) {
    int readLen = 0;
    int ret = NDK_PortReadLen(com_number, &readLen);
    LOGD_FMT(">>>NDK_PortReadLen ret[%d], readLen[%d]", ret, readLen);
    if (ret == 0) {
        (*env)->SetIntArrayRegion(env, read_len, 0, 1, &readLen);
    }
    return ret;
}

jint NDK_1PortWrite(JNIEnv *env, jobject thiz,jint com_number, jint length,jbyteArray data) {
    uchar *pData = NULL;
    if (data != NULL) {
        pData = (uchar *) (*env)->GetByteArrayElements(env, data, NULL);
    }

    int ret = NDK_PortWrite(com_number, length, pData);

    if (data != NULL) {
        (*env)->ReleaseByteArrayElements(env, data, pData, NULL);
    }

    LOGD_FMT(">>>NDK_PortWrite ret[%d]", ret);
    return ret;
}

jint NDK_1PortClrBuf(JNIEnv *env, jobject thiz,jint com_number) {
    int ret = NDK_PortClrBuf(com_number);
    LOGD_FMT(">>>NDK_PortClrBuf ret[%d]", ret);
    return ret;
}

/* JNI method table */
static const JNINativeMethod method_table[] = {
        {"portOpen", "(II[B)I",         (void *) Pos_openPort},
        {"portWrite",    "(I[BII)I",        (void *) Pos_write},
        {"portRead",     "(I[BII)I",        (void *) Pos_read},
        {"portClearBuf", "(II)I",           (void *) Pos_clearBuf},
        {"portIsBufferEmpty", "(II)I",      (void *) Pos_isBufferEmpty},
        {"portClose",    "(I)I",            (void *) Pos_close},
        {"portDebug",    "(I)I",            (void *) Pos_debug},
        {"portIOCTL",    "(II[B)I",         (void *) Pos_ioctl},
        {"portReadLen",  "(I[I)I",          (void *) Pos_readLen},
        {"awakeExternalDevice",   "()I",    (void *) U2000_awakeExternalDevice},
        {"getExternalPowerSupply", "()I",    (void *) U2000_getExternalPowerSupply},
        {"setRadarDetectionDistance",  "(Ljava/lang/String;Ljava/lang/String;)I",   (void *) U2000_setRadarDetectionDistance},
        {"setEthernetMode", "(I)I",         (void *) U2000_setEthernetMode},
        {"getEthernetMode", "([I)I",        (void *) U2000_getEthernetMode},
        {"portNDKOpen",    "(ILjava/lang/String;)I",            (void *) NDK_1PortOpen},
        {"portNDKClose",    "(I)I",            (void *) NDK_1PortClose},
        {"portNDKRead",    "(III[B[I)I",            (void *) NDK_1PortRead},
        {"portNDKReadLen",      "(I[I)I",        (void *) NDK_1PortReadLen},
        {"portNDKWrite",    "(II[B)I",            (void *) NDK_1PortWrite},
        {"portNDKClrBuf",    "(I)I",            (void *) NDK_1PortClrBuf},
        {"enableRadarAndHeater",     "(ZZ)I",   (void *) U2000_setRadarAndHeaterConfig},
        {"portOpenWithNodeName",    "(Ljava/lang/String;I[B)I",    (void *) portOpenWithNodeName},
        {"setDebugMode", "(I)I", (void *) setDebugMode},
};


int jniRegisterNativeMethods(JNIEnv *env,
                             const char *className,
                             const JNINativeMethod *gMethods,
                             int numMethods) {
    jclass clazz;

    __android_log_print(ANDROID_LOG_INFO, TAG, "Registering %s natives\n", className);
#ifdef __cplusplus
    clazz = env->FindClass(className);
    if (clazz == NULL) {
            __android_log_print(ANDROID_LOG_ERROR, TAG, "Native registration unable to find class '%s'\n", className);
            return -1;
        }
        if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
            __android_log_print(ANDROID_LOG_ERROR, TAG, "RegisterNatives failed for '%s'\n", className);
            return -1;
        }
        return 0;

#else
    clazz = (*env)->FindClass(env, className);

    if (clazz == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, TAG,
                            "Native registration unable to find class '%s'\n", className);
        return -1;
    }
    if ((*env)->RegisterNatives(env, clazz, gMethods, numMethods) < 0) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "RegisterNatives failed for '%s'\n", className);
        return -1;
    }
    return 0;
#endif
}


int register_JNILib_im81ndk(JNIEnv *env) {
    return jniRegisterNativeMethods(env, JAVA_CLASS_NAME, method_table, NELEM(method_table));
}

// JNI_OnLoad() will be runned first, when  VM load a Clib
jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    jint result = JNI_ERR;
    sVm = vm;

#ifdef __cplusplus

    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
#else
    if ((*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_4) != JNI_OK) {
#endif
        __android_log_print(ANDROID_LOG_ERROR, TAG, "GetEnv failed!");
        return result;
    }

    __android_log_print(ANDROID_LOG_INFO, TAG, "loading81 .123 . .");


    if (register_JNILib_im81ndk(env) != JNI_OK) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "can't load register_JNILib_im81ndk():  %s",
                            JAVA_CLASS_NAME);
        goto end;
    }
    __android_log_print(ANDROID_LOG_INFO, TAG, "loaded");

    // load pos-ndk
    result = 1;

    if (result < 0) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "JNI_OnLoad dllload  fail!!");
        // TODO  load fail..,   	 Error handling required
        //             goto end;
    }
    if (Ndk_Dlload() != 0) {
        LOGD_FMT(">>>");
    }
    __android_log_print(ANDROID_LOG_ERROR, TAG, "JNI_OnLoad succ.");
    result = JNI_VERSION_1_4;

    end:
    return result;
}

int Ndk_Dlload(){
    COMMON_DEBUG_INIT;
    functionLib = dlopen("libnlposapi.so",RTLD_LAZY);
    dlError = (char *)dlerror();
    __android_log_print(ANDROID_LOG_INFO, TAG, "dlopen libnlposapi.so dlError[%s] functionLib[0x%x]\n", dlError,functionLib);

    if(functionLib == NULL){
        functionLib = dlopen("libnlposapi.npt.so",RTLD_LAZY);
        dlError = (char *)dlerror();
        __android_log_print(ANDROID_LOG_INFO, TAG, "dlopen libnlposapi.npt.so dlError[%s] functionLib[0x%x]\n", dlError,functionLib);
    }

    //port
    DLSYM(functionLib,NDK_PortOpen);
    DLSYM(functionLib,NDK_PortClose);
    DLSYM(functionLib,NDK_PortRead);
    DLSYM(functionLib,NDK_PortWrite);
    DLSYM(functionLib,NDK_PortTxSendOver);
    DLSYM(functionLib,NDK_PortClrBuf);
    DLSYM(functionLib,NDK_PortReadLen);

    }

void newlandAPI_printf(char *fmt, ...) {
    char buffer[80];

    va_list args;
    va_start(args, fmt);
    vsprintf(buffer, fmt, args);
    LOGD_FMT("printf call from demoLib:  %s", buffer);
    va_end(args);

}






#include <jni.h>
#include <stdint.h>
#include <api.h>
#include "emv.h"
#include "threadtool.h"
#include "api.h"
#include "desc.h"
#include "prnttf.h"
#include "pin.h"
#include "rfid.h"
#include "log.h"
#include "card.h"
#include <sys/system_properties.h>
/**
 * Author by wuhh, Date on 2019/3/31 0022.
 */
JavaVM *gJavaVM = NULL;
jobject g_cmdRspLisObj;
jmethodID g_cmdRspLisMid;
int g_preCmd=0;

static const Command_Code_t Command_Code_Manage[] =
{
     {CARDREADER_OPEN,      CardReader_Open},
     {CARDREADER_CLOSE,     CardReader_Close},

     {MAG_READTRACKPLAIN,   Mag_ReadTrackPlain},
     {MAG_READTRACKENCRYPT, Mag_ReadTrackEncrypt},
     {MAG_CALCULATETRACK,   Mag_CalculateTrack},

     {ICC_DETECT,           Icc_Detect},
     {ICC_POWERON,          Icc_PowerOn},
     {ICC_POWEROFF,         Icc_PowerOff},
     {ICC_READWRITE,        Icc_ReadWrite},

     {RFID_POWERON,         Rfid_PowerOn},
     {RFID_POWEROFF,        Rfid_PowerOff},
     {RFID_APDU,            Rfid_Apdu},
     {RFID_FELICAAPDU,      Rfid_FelicaApdu},
     {RFID_M1AUTHKEY,       Rfid_M1AuthKey},
     {RFID_M1READBLOCK,     Rfid_M1ReadBlock},
     {RFID_M1WRITEBLOCK,    Rfid_M1WriteBlock},
     {RFID_M1INCREMENT,     Rfid_M1Increment},
     {RFID_M1DECREMENT,     Rfid_M1Decrement},
     {RFID_M0AUTHKEY,       Rfid_M0AuthKey},
     {RFID_M0READBLOCK,     Rfid_M0ReadBlock},
     {RFID_M0WRITEBLOCK,    Rfid_M0WriteBlock},
     {RFID_ISEXIST,         Rfid_IsExist},
     {RFID_ATS,             Rfid_ATS},

     {LIGHT_SETSTATUS,      Light_SetStatus},
     {LIGHT_BLINK,          Light_Blink},

     {PINPAD_INPUT,         Pinpad_Input},
     {PINPAD_LOADMKEY,      Pinpad_LoadMKey},
     {PINPAD_ENCORDEC,      Pinpad_EncOrDec},
     {PINPAD_DATAMAC,       Pinpad_DataMac},
     {PINPAD_LOADWKEY,      Pinpad_LoadWKey},
     {PINPAD_LOADDUKPT,     Pinpad_LoadDukpt},
     {PINPAD_DELKEY,        Pinpad_DelKey},
     {PINPAD_VPPINIT,       Pinpad_VppInit},
     {PINPAD_CHECKKEY,      Pinpad_CheckKey},
     {PINPAD_INCREASEKSN,   Pinpad_IncreaseKsn},
     {PINPAD_GETDUKPTKSN,   Pinpad_GetDukptKsn},

     {PRN_GETSTATUS,        Prn_GetStatus},
     {PRN_SETPAPERSIZE,     Prn_SetPaperSize},
     {PRN_PRINT,            Prn_Print},
     {PRN_CUTTERPAPER,      Prn_CutterPaper},

     {DEVICE_READINFO,      Device_ReadInfo},
     {DEVICE_GETRANDOMNUMBER,Device_GetRandomNumber},
     {DEVICE_GETTUSN,       Device_GetTusn},
     {DEVICE_SETSN,         Device_SetSN},
     {DEVICE_SETDATETIME,   Device_SetDateTime},
     {DEVICE_GETDATETIME,   Device_GetDateTime},

     {TERM_BUZZER,          Term_Buzzer},
     {TERM_CANCELRESET,     Term_CancelReset},
     {TERM_SHUTDOWN,        Term_ShutDown},
     {TERM_CONFIRMATION,    Term_Confirmation},
     {TERM_SETKEYVOL,       Term_SetKeyVol},
     {TERM_SETTAGDATA,      Term_SetTagData},
     {TERM_GETTAGDATA,      Term_GetTagData},

     {LED_GETVERSION,       Led_GetVersion},
     {LED_SETBRIGHTNESS,    Led_SetBrightness},
     {LED_TURNON,           Led_TurnOn},
     {LED_TURNOFF,          Led_TurnOff},

     {FILE_OPENRECORDS,     File_OpenRecords},
     {FILE_GETRECORDNUM,    File_GetRecordNum},
     {FILE_WRITERECORD,     File_WriteRecord},
     {FILE_MODIFYRECORD,    File_ModifyRecord},
     {FILE_GETRECORD,       File_GetRecord},
     {FILE_WRITEFILE,       File_WriteFile},
     {FILE_READFILE,        File_ReadFile},
     {FILE_DELETEFILE,      File_DeleteFile},

     {LOG_SETLEVEL,         Log_SetLevel},

     {GLOBAL_SETTING,       Global_Setting},
};

typedef enum{
    CANCEL_OPENCARD      = 1,
    CANCEL_PASSWORDINPUT = 2,
    CANCEL_DEVICERESET   = 4,
}EmCancelEvent;

int Global_Setting(unsigned char* pbuf,  int buf_len, unsigned char *pOut, int *outLen){
    LOGE_FMT("Global_Setting");
    setReadInfoFlag();
}

static int __deviceReset()
{
    LOGE_FMT("");
    Pin_Cancel();
    CardReader_Cancel(0);
    Rfid_Cancle();

    NDK_UnRegisterEvent(SYS_EVENT_MAGCARD|SYS_EVENT_ICCARD|SYS_EVENT_RFID|SYS_EVENT_PIN);
    EXEC_NDK("NDK_MagClose",NDK_MagClose(),NDK_OK,COMMAND_NONE);
    EXEC_NDK("NDK_IccPowerDown",NDK_IccPowerDown(ICTYPE_IC),NDK_OK,COMMAND_NONE);
    EXEC_NDK("NDK_RfidCloseRf",NDK_RfidCloseRf(),NDK_OK,COMMAND_NONE);
    return 0;
}

static jint __distributeCmd(char *szCmdIn,int iLenin,char *szCmdOut, int* piLenOut)
{
	LOGD_STR("request",szCmdIn,iLenin);
	int i = 0,iRet = 0;
	uchar mainCmd=0,subCmd=0;
    int  cmdsize = sizeof(Command_Code_Manage)/sizeof(Command_Code_Manage[0]);
	
	if (szCmdIn == NULL){
		return -1;
	}
	mainCmd = szCmdIn[0];subCmd = szCmdIn[1];

	int cmd = (mainCmd << 8) | subCmd;
    g_preCmd = cmd;

	for(i=0; i<cmdsize; i++){
		if(cmd == Command_Code_Manage[i].cmd){
            Cmd_PrintDesc(mainCmd,subCmd,1);
			iRet = (*Command_Code_Manage[i].func)(szCmdIn+2, iLenin-2, szCmdOut, piLenOut);
            Cmd_PrintDesc(mainCmd,subCmd,0);
			return iRet;
		}
	}
	return -1;
}


static jint __jniCmdCancel(JNIEnv *env, jobject obj, jint type)
{
    LOGE_FMT(">>>type[%d]",type);
    if(type == CANCEL_OPENCARD){
        CardReader_Cancel(2000);
    }else if(type == CANCEL_PASSWORDINPUT){
        Pin_Cancel();
    }else if(type == CANCEL_DEVICERESET){
        __deviceReset();
    }
}

static jint __jniCmd(JNIEnv *env, jobject obj, jbyteArray input, jint inlen, jbyteArray output,jintArray outlen) 
{
    jbyte *jb = (*env)->GetByteArrayElements(env,input,0);
 	if(jb == NULL){
		return -1;
	}
    char out1[4096],*c=NULL;
    int outlen1 = 0,i = 0;
    if(inlen > 0)
    {
        c = (char *)malloc(inlen+1);
        memcpy(c,jb,inlen);
        c[inlen]=0;
    }else{
        (*env)->ReleaseByteArrayElements(env,input,jb,0);
        return -1;
    }
    (*env)->ReleaseByteArrayElements(env,input,jb,0);
    int ret =  __distributeCmd(c,inlen,out1, &outlen1);
    if(c != NULL){
        free(c);
        c=NULL;
    }
    if(outlen!=NULL){
        (*env)->SetIntArrayRegion(env, outlen, 0, 1, &outlen1);
    }
    if(output!=NULL){
        (*env)->SetByteArrayRegion(env, output, 0, outlen1, out1);
    }
    return ret;
}

static jint __jniCmdListener(JNIEnv *env, jobject obj, jbyteArray input, jint inlen, jbyteArray output,jintArray outlen, jobject listener)
{
    jbyte *jb = (*env)->GetByteArrayElements(env,input,0);
    char *c=NULL,out1[4096];
    int outlen1 = 0,i = 0;
    if(inlen > 0)
    {
        c = (char *)malloc(inlen+1);
        memcpy(c,jb,inlen);
        c[inlen]=0;
    }else{
        (*env)->ReleaseByteArrayElements(env,input,jb,0);
        return -1;
    }
    if(listener!=NULL) {
        if(c[0]==0x1A && c[1]==0x01){//CMD_PINPAD_PASSWORDINPUT
            if(g_cmdRspLisObj != NULL)
                (*env)->DeleteGlobalRef(env,g_cmdRspLisObj);
            g_cmdRspLisObj = (*env)->NewGlobalRef(env, listener);
            jclass cls=  (*env)->GetObjectClass(env, listener);
            g_cmdRspLisMid = (*env)->GetMethodID(env,cls,"callback","(I[B)V");
        }
    }else if((c[0]==0x1A && c[1]==0x01) /*|| (c[0]==0xB1 && c[1]==0x03)*/)
        return -1;
    (*env)->ReleaseByteArrayElements(env,input,jb,0);
    int ret =  __distributeCmd(c,inlen,out1, &outlen1);
    if(c != NULL){
        free(c);
        c=NULL;
    }
    if(outlen!=NULL){
        (*env)->SetIntArrayRegion(env, outlen, 0, 1, &outlen1);
    }
    if(output!=NULL){
        (*env)->SetByteArrayRegion(env, output, 0, outlen1, out1);
    }
    return ret;
}

static jint __getErrInfo(JNIEnv *env, jobject obj,jint cmd,jbyteArray errCode,jbyteArray errMsg,jbyteArray otherMsg){
    return Log_GetErrInfo(env,cmd,errCode,errMsg,otherMsg);
}

static jint Pin_Encrypt(JNIEnv *env, jobject obj, jint keySys, jint alg, jint cipherMode, jint keyIndex,
                        jbyteArray inputData, jint inputDataLen, jbyteArray iv, jint ivLen,
                        jbyteArray outputData, jintArray outputDataLen, jbyteArray ksn, jintArray ksnLen){
    uchar *inData = (*env)->GetByteArrayElements(env,inputData,NULL);
    if(inData == NULL){
        LOGD_FMT(">>>inData[%d]",inData);
        return -1;
    }
    uchar *ivData = NULL;
    if(iv !=NULL){
        ivData = (*env)->GetByteArrayElements(env,iv,NULL);
    }
    uint outksnLen = 0;uchar outksnData[10];
    memset(outksnData,0, sizeof(outksnData));
    uint outputlen = (*env)->GetArrayLength(env,outputData);
    uchar outputdata[outputlen];
    memset(outputdata,0, sizeof(outputdata));
    int ret = Pinpad_Encrypt(keySys,alg,cipherMode,keyIndex,inData,inputDataLen,ivData,ivLen, outputdata,&outputlen,outksnData,&outksnLen);
    if(ret == NDK_OK){
        (*env)->SetIntArrayRegion(env, outputDataLen, 0, 1,&outputlen);
        (*env)->SetByteArrayRegion(env,outputData, 0,outputlen,outputdata);
        (*env)->SetIntArrayRegion(env, ksnLen, 0, 1,&outksnLen);
        (*env)->SetByteArrayRegion(env,ksn, 0,outksnLen,outksnData);
    }
    (*env)->ReleaseByteArrayElements(env,inputData,inData,NULL);
    if(iv!=NULL){
        (*env)->ReleaseByteArrayElements(env,iv,ivData,NULL);
    }
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_newland_intelligent_jni_JniCmdInterface_isProductDevice(JNIEnv *env, jobject thiz) {
    char adbConfig[12];
    memset(adbConfig,0,sizeof(adbConfig));
    __system_property_get("ro.epay.adb", adbConfig);
    LOGE_STR("adbConfig",adbConfig,12);
    if(adbConfig[0] == '0'){
        return 1;
    }
    return 0;
}

static const JNINativeMethod methods[] = {
        {"jniMposLibCmd0",       "([BI[B[I)I",                                             (void *) __jniCmd},
        {"jniMposLibCmd0",       "([BI[B[ILcom/newland/intelligent/jni/CmdRspListener;)I", (void *) __jniCmdListener},
        {"jniMposLibCmdCancel0", "(I)I",                                                   (void *) __jniCmdCancel},
        {"getErrInfo0",          "(I[B[B[B)I",                                             (void *) __getErrInfo},
        {"encrypt",              "(IIII[BI[BI[B[I[B[I)I",                                  (void *)Pin_Encrypt},
};

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    DEBUG_INIT;

    JNIEnv *env = NULL;
    gJavaVM = vm;
    int status = (*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_4);
    if (status != JNI_OK) {
        LOGD_FMT("GetEnv failed!");
        return JNI_ERR;
    }
    jclass cls = (*env)->FindClass(env, "com/newland/intelligent/jni/JniCmdInterface");
    if (cls == NULL)
        return JNI_ERR;
    if ((*env)->RegisterNatives(env, cls, methods, sizeof(methods) / sizeof(JNINativeMethod)) < 0)
    return JNI_ERR;

    if(registerNativesEmvL3(env)!=JNI_OK){
        return JNI_ERR;
    }

    if (Ndk_Dlload() != 0) {
        LOGD_FMT(">>>");
    }
    THREAD_MUTEX_CTLS_CREATE;
    THREAD_COND_CTLS_CREATE;
    Log_ErrInfoInit();
    CardReader_GetMethodID(env);
    Prn_ModuleInit();

    EXEC_NDK("NDK_RfidInit", NDK_RfidInit(NULL), NDK_ERR,COMMAND_NONE);

    LOGE_FMT(">>>VERSION[%s] IS_NAPI[%d] IS_EMVL3[%d]",VERSION,IS_NAPI,IS_EMVL3);
    return JNI_VERSION_1_4;
}

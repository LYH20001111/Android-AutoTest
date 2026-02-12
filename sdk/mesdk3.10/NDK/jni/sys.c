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
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysBeep
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysBeep
  (JNIEnv *env, jobject jo){
	  return NDK_SysBeep();
  };

/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_Getlibver
 * Signature: ([C)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1Getlibver
  (JNIEnv *env, jobject jo, jbyteArray version){
	  char ver[100]={0};
	  int ret = -1;
	  ret = NDK_Getlibver(ver);
	  LOGI("version = %s",ver);
	  if(ret == 0)
		  (*env)->SetByteArrayRegion(env,version,0,strlen(ver),ver);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysTimeBeep
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysTimeBeep
  (JNIEnv *env, jobject jo, jint unFrequency, jint unSeconds){
	  return NDK_SysTimeBeep(unFrequency,unSeconds);
  };

/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysStartWatch
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysStartWatch
  (JNIEnv *env, jobject jo){
	  return NDK_SysStartWatch();
  };

/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysStopWatch
 * Signature: ([I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysStopWatch
  (JNIEnv *env, jobject jo, jintArray punTime){
	  int time = 0;
	  int ret = -1;
	  ret = NDK_SysStopWatch(&time);
	  if(ret == 0)
		  (*env)->SetIntArrayRegion(env,punTime,0,1,&time);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysDelay
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysDelay
  (JNIEnv *env, jobject jo, jint time){
	  return NDK_SysDelay(time);
  };

/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysMsDelay
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysMsDelay
  (JNIEnv *env, jobject jo, jint time){
	  return NDK_SysMsDelay(time);
  };

/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysExit
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysExit
  (JNIEnv *env, jobject jo, jint code){
	  return  NDK_SysExit(code);
  };

/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysReboot
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysReboot
  (JNIEnv *env, jobject jo){
	  return NDK_SysReboot();
  };

/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysShutDown
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysShutDown
  (JNIEnv *env, jobject jo){
	  return NDK_SysShutDown();
  };

/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysSetBeepVol
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysSetBeepVol
  (JNIEnv *env, jobject jo, jint unVolNum){
	  return NDK_SysSetBeepVol(unVolNum);
  };

/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysGetBeepVol
 * Signature: ([I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysGetBeepVol
  (JNIEnv *env, jobject jo, jintArray unVolNum){
	  int volnum = 0;
	  int ret = -1;
	  ret = NDK_SysGetBeepVol(&volnum);
	  if(ret == 0)
		  (*env)->SetIntArrayRegion(env,unVolNum,0,1,&volnum);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysSetSuspend
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysSetSuspend
  (JNIEnv *env, jobject jo, jint unFlag){
	  return NDK_SysSetSuspend(unFlag);
  };

/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysGoSuspend
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysGoSuspend
  (JNIEnv *env, jobject jo){
	  return NDK_SysGoSuspend();
  };

/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysGetPowerVol
 * Signature: ([I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysGetPowerVol
  (JNIEnv *env, jobject jo, jintArray powrvol){
	  int vol = 0;
	  int ret = -1;
	  ret = NDK_SysGetPowerVol(&vol);
	  if(ret == 0)
		  (*env)->SetIntArrayRegion(env,powrvol,0,1,&vol);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_LedStatus
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1LedStatus
  (JNIEnv *env, jobject jo, jint status){
	  return NDK_LedStatus(status);
  };

JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1LedSetFlickParam
		(JNIEnv *env, jobject jo, jint status,jlong unFlickOn,jlong unFlickOff){
	ST_NDK_LED_FLICK flick;
	memset(&flick,0,sizeof(ST_NDK_LED_FLICK));
	flick.unFlickOn = unFlickOn;
	flick.unFlickOff = unFlickOff;
	return NDK_LedSetFlickParam(status,flick);
};
/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysReadWatch
 * Signature: ([I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysReadWatch
  (JNIEnv *env, jobject jo, jintArray time){
	  int mtime = 0;
	  int ret = -1;
	  ret = NDK_SysReadWatch(&mtime);
	  if(ret == 0)
		  (*env)->SetIntArrayRegion(env,time,0,1,&mtime);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysGetPosInfo_m
 * Signature: (I[I[C)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysGetPosInfo_1m
  (JNIEnv *env, jobject jo, jint flag, jintArray len, jbyteArray dbuf){
	  char buf[1024] = {0};
	  int rlen = 0;
	  int ret = -1;
	  ret = NDK_SysGetPosInfo(flag,&rlen,buf);
	  if(ret == 0)
	  {
		  (*env)->SetIntArrayRegion(env,len,0,1,&rlen);
		  (*env)->SetByteArrayRegion(env,dbuf,0,rlen,buf);
	  }
	  return ret;
  };

/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysGetConfigInfo_m
 * Signature: (I[I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysGetConfigInfo_1m
  (JNIEnv *env, jobject jo, jint flag, jintArray val){
	  int mval = 0;
	  int ret = -1;
	  ret = NDK_SysGetConfigInfo(flag,&mval);
	  if(ret == 0)
		  (*env)->SetIntArrayRegion(env,val,0,1,&mval);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysInitStatisticsData
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysInitStatisticsData
  (JNIEnv *env, jobject jo){
	  return NDK_SysInitStatisticsData();
  };

/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysGetStatisticsData
 * Signature: (I[J)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysGetStatisticsData
  (JNIEnv *env, jobject jo, jint devid ,jlongArray val){
	  long mval = 0;
	  int ret = -1;
	  ret = NDK_SysGetStatisticsData(devid,&mval);
	  if(ret == 0)
		  (*env)->SetLongArrayRegion(env,val,0,1,&mval);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysGetFirmwareInfo
 * Signature: ([I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysGetFirmwareInfo
  (JNIEnv *env, jobject jo, jintArray type){
	  int mtype = 0;
	  int ret = -1;
	  ret = NDK_SysGetFirmwareInfo(&mtype);
	  if(ret == 0)
		  (*env)->SetIntArrayRegion(env,type,0,1,&mtype);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysTime
 * Signature: ([I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysTime
  (JNIEnv *env, jobject jo, jintArray timenum){
	  int time = 0;
	  int ret = -1;
	  ret = NDK_SysTime(&time);
	//  LOGI("time = %ld",time);
	  if(ret == 0)
		  (*env)->SetIntArrayRegion(env,timenum,0,1,&time);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysSetSuspendDuration
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysSetSuspendDuration
  (JNIEnv *env, jobject jo, jint time){
	  return NDK_SysSetSuspendDuration(time);
  };

/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysEnterBoot
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysEnterBoot
  (JNIEnv *env, jobject jo){
	  return NDK_SysEnterBoot();
  };

  /*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysSetPosInfo_m
 * Signature: (ILjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysSetPosInfo_1m
  (JNIEnv *env, jobject jo, jint flag, jstring val){
	  char *mval = (*env)->GetStringUTFChars(env, val, 0);
	  int ret = -1;
	  ret = NDK_SysSetPosInfo(flag,mval);
	  (*env)->ReleaseStringUTFChars(env, val, mval);
	  return ret;
  };


/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDk_SysGetK21Version
 * Signature: ([C)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDk_1SysGetK21Version
  (JNIEnv *env, jobject jo, jbyteArray version){
	  char ver[100] = {0};
	  int ret = -1;
	  ret = NDk_SysGetK21Version(ver);
	  if(ret == 0)
		  (*env)->SetByteArrayRegion(env,version,0,strlen(ver),ver);
	  return ret;
  };

/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysWakeUp
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysWakeUp
  (JNIEnv *env, jobject jo){
	  return NDK_SysWakeUp();
  };
  
/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysGoSuspend_Extern
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysGoSuspend_1Extern
  (JNIEnv *env, jobject jo){
	  return NDK_SysGoSuspend_Extern();
  };

/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysSetPosTime_m
 * Signature: (IIIIII)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysSetPosTime_1m
  (JNIEnv *env, jobject jo , jint year, jint mon, jint day, jint hour, jint min, jint sec){
	struct tm stSetPosTime = {0};
	stSetPosTime.tm_year = year;
	stSetPosTime.tm_mon = mon;
	stSetPosTime.tm_mday = day;
	stSetPosTime.tm_hour = hour;
	stSetPosTime.tm_min = min;
	stSetPosTime.tm_sec = sec;
	return NDK_SysSetPosTime(stSetPosTime);	
};

/*
 * Class:     com_newland_ndk_SysN
 * Method:    NDK_SysGetPosTime_m
 * Signature: ([I[I[I[I[I[I)I
 */
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SysGetPosTime_1m
  (JNIEnv *env, jobject jo, jintArray year, jintArray mon, jintArray day, jintArray hour, jintArray min, jintArray sec){
	struct tm stGetPosTime = {0};
	int ret = -1;
	ret = NDK_SysGetPosTime(&stGetPosTime);
	if(ret == 0)
	{
		(*env)->SetIntArrayRegion(env,year,0,1,&stGetPosTime.tm_year);
		(*env)->SetIntArrayRegion(env,mon,0,1,&stGetPosTime.tm_mon);
		(*env)->SetIntArrayRegion(env,day,0,1,&stGetPosTime.tm_mday);
		(*env)->SetIntArrayRegion(env,hour,0,1,&stGetPosTime.tm_hour);
		(*env)->SetIntArrayRegion(env,min,0,1,&stGetPosTime.tm_min);
		(*env)->SetIntArrayRegion(env,sec,0,1,&stGetPosTime.tm_sec);
	}
	return ret;
};


#define INDEX_SYS_EVENT_MAGCARD     0
#define INDEX_SYS_EVENT_ICCAR       1
#define INDEX_SYS_EVENT_RFID        2
#define INDEX_SYS_EVENT_PIN         3
#define INDEX_SYS_EVENT_PRNTER      4
#define INDEX_SYS_EVENT_MAX         5

typedef int (*NotifyEvent)(EM_SYS_EVENT eventNum,int msgLen, char * msg);

typedef struct{
	jobject eventObj;
	jmethodID eventMid;
}ST_SYS_EVENT_CTL;

extern JavaVM *gJavaVM;
ST_SYS_EVENT_CTL sysEventCtls[5];

void SysEventInit(){
	int i = 0;
	for(;i<INDEX_SYS_EVENT_MAX;i++){
		sysEventCtls[i].eventObj = NULL;
		sysEventCtls[i].eventMid = NULL;
	}
}
static void __doEventCallBack(int index,int eventNum){
	LOGD_FMT("");
	if(index < 0 || index >= INDEX_SYS_EVENT_MAX){
		LOGD_FMT("index[%d] Err.",index);
		return;
	}
	JNIEnv *env = NULL;
	jboolean isAttached = JNI_FALSE;
	if(gJavaVM == NULL){
        LOGD_FMT("gJavaVM[%d]",gJavaVM);
        return;
	}
	int ret = (*gJavaVM)->GetEnv(gJavaVM,(void **) &env, JNI_VERSION_1_4);
	if(ret < 0 ) {
		ret =  (*gJavaVM)->AttachCurrentThread(gJavaVM,(JNIEnv **) &env, NULL);
		if (ret < 0) {
			LOGD_FMT(">>>AttachCurrentThread error.");
			return;
		}
		isAttached = JNI_TRUE;
	}
	if(sysEventCtls[index].eventObj == NULL || sysEventCtls[index].eventMid == NULL) {
		LOGD_FMT("eventObj[%d] eventMid[%d]",sysEventCtls[index].eventObj,sysEventCtls[index].eventMid);
		return;
	}
	(*env)->CallVoidMethod(env,sysEventCtls[index].eventObj,sysEventCtls[index].eventMid,(jint)eventNum,0,NULL);
	if(isAttached)
		(*gJavaVM)->DetachCurrentThread(gJavaVM);
	LOGD_FMT("succ");
}
static int __notifyMagEvent(EM_SYS_EVENT eventNum, int msgLen, char * msg)
{
	LOGD_FMT("eventNum[%d]",eventNum);
	if(eventNum == SYS_EVENT_MAGCARD||eventNum == SYS_EVENT_NONE){
		__doEventCallBack(INDEX_SYS_EVENT_MAGCARD,eventNum);
	}
}
static int __notifyIcEvent(EM_SYS_EVENT eventNum, int msgLen, char * msg){
	LOGD_FMT("eventNum[%d]",eventNum);
	if(eventNum == SYS_EVENT_ICCARD||eventNum == SYS_EVENT_NONE){
		__doEventCallBack(INDEX_SYS_EVENT_ICCAR,eventNum);
	}
}
static int __notifyRfidEvent(EM_SYS_EVENT eventNum, int msgLen, char * msg){
	LOGD_FMT("eventNum[%d]",eventNum);
	if(eventNum == SYS_EVENT_RFID||eventNum == SYS_EVENT_NONE){
		__doEventCallBack(INDEX_SYS_EVENT_RFID,eventNum);
	}
}
static int __notifyPinEvent(EM_SYS_EVENT eventNum, int msgLen, char * msg){
	LOGD_FMT("eventNum[%d]",eventNum);
	if(eventNum == SYS_EVENT_PIN||eventNum == SYS_EVENT_NONE){
		__doEventCallBack(INDEX_SYS_EVENT_PIN,eventNum);
	}
}
static int __notifyPrnEvent(EM_SYS_EVENT eventNum, int msgLen, char * msg){
	LOGD_FMT("eventNum[%d]",eventNum);
	if(eventNum == SYS_EVENT_PRNTER||eventNum == SYS_EVENT_NONE){
		__doEventCallBack(INDEX_SYS_EVENT_PRNTER,eventNum);
	}
}

static int getEventInfo(int event,int *index,NotifyEvent *notifyEvent){
	if(event == SYS_EVENT_MAGCARD){
		*index = INDEX_SYS_EVENT_MAGCARD;
		*notifyEvent = __notifyMagEvent;
	}else if(event == SYS_EVENT_ICCARD){
		*index = INDEX_SYS_EVENT_ICCAR;
		*notifyEvent = __notifyIcEvent;
	}else if(event == SYS_EVENT_RFID){
		*index = INDEX_SYS_EVENT_RFID;
		*notifyEvent = __notifyRfidEvent;
	}else if(event == SYS_EVENT_PIN){
		*index = INDEX_SYS_EVENT_PIN;
		*notifyEvent = __notifyPinEvent;
	}else if(event == SYS_EVENT_PRNTER) {
		*index = INDEX_SYS_EVENT_PRNTER;
		*notifyEvent = __notifyPrnEvent;
	}else{
		return NDK_ERR_PARA;
	}
	return NDK_OK;
}
JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SYS_1RegisterEvent(JNIEnv *env, jobject jo,jint event,jint tiomeOutms,jobject callback){
	int index;
	NotifyEvent notifyEvent;
	int ret = getEventInfo(event,&index,&notifyEvent);
	if(ret != NDK_OK){
		return ret;
	}
	jobject eventObj = sysEventCtls[index].eventObj;
    LOGD_FMT("event[0x%x] tiomeOutms[%d] index[%d] eventObj[%d] notifyEvent[%d]",event,tiomeOutms,index,eventObj,notifyEvent);
	if(eventObj != NULL) {
		(*env)->DeleteGlobalRef(env, eventObj);
	}
	sysEventCtls[index].eventObj = (*env)->NewGlobalRef(env, callback);
	jclass cls=  (*env)->GetObjectClass(env, callback);
	sysEventCtls[index].eventMid = (*env)->GetMethodID(env,cls,"callback","(II[B)V");
	return NDK_SYS_RegisterEvent(event,tiomeOutms,notifyEvent);
}

JNIEXPORT jint JNICALL Java_com_newland_ndk_SysN_NDK_1SYS_1UnRegisterEvent(JNIEnv *env, jobject jo,jint event){
	LOGD_FMT("event[%d]",event);
	return NDK_SYS_UnRegisterEvent(event);
}
JNIEXPORT jint JNICALL
Java_com_newland_ndk_SysN_NDK_1SysKeyVolSet(JNIEnv *env, jobject thiz, jint sel) {
	LOGD_FMT("KeyVolSet[%d]",sel);
	return NDK_SysKeyVolSet(sel);
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_SysN_NDK_1LedLt1118Status(JNIEnv *env, jobject thiz, jlong em_status) {
	return NDK_LedLt1118Status(em_status);
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_SysN_NDK_1SysDevBacklightCtrl(JNIEnv *env, jobject thiz, jint mdevice,
												   jint value) {
	return NDK_SysDevBacklightCtrl(mdevice,value);
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_SysN_NDK_1SysTimeBeep_1Ex(JNIEnv *env, jobject thiz, jint un_frequency,
                                               jint un_seconds, jint un_volumn) {
    return NDK_SysTimeBeep_Ex(un_frequency,un_seconds,un_volumn);
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_SysN_NDK_1SysSetBeepVol_1Extern(JNIEnv *env, jobject thiz, jint type,
                                                     jint un_volumn) {
    return NDK_SysSetBeepVol_Extern(type,un_volumn);
}

JNIEXPORT jint JNICALL
Java_com_newland_ndk_SysN_NDK_1SysGetBeepVol_1Extern(JNIEnv *env, jobject thiz, jint type,
                                                     jintArray un_volumn) {
    uint volumn = -1;
    int ret = NDK_SysGetBeepVol_Extern(type, &volumn);
    if(ret == 0)
        (*env)->SetIntArrayRegion(env,un_volumn,0,1,&volumn);
}
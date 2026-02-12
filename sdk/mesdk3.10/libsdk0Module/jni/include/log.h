/**
 * Author by wuhh, Date on 2019/3/31 0022.
 */
#ifndef __LOG_H_
#define __LOG_H_

#include "comm.h"
#include "desc.h"

#define VERSION        "V1.7.4"

extern void Log_DebugInit();
extern void Log_GetErrInfoFieldID(JNIEnv *env);
extern int Log_ExecNdkFun(char *tag,int funRet,int expectRet,const char*file,const char*function,long line,int cmd);
extern void Log_SetErrMsg(int errCode,const char*file,const char*function,long line,int cmd);
#define DEBUG_INIT  Log_DebugInit()

typedef struct {
    void (*DEBUG_Levelone)(char * fmt,...);
    void (*DEBUG_Leveltwo)(char * fmt,...);
    void (*DEBUG_string_Levelone)(char *data,int datalen);
    void (*DEBUG_string_Leveltwo)(char *data,int datalen);
    void (*ERROR_MSG_LOG)(char * fmt,...);
    void (*ERROR_MSG_LOG_String)(char *data,int datalen);
} ST_DEBUG_API;

extern ST_DEBUG_API Udebug;

#define FILE(x) strrchr(x,'/')?strrchr(x,'/')+1:x

#define LOGE_NDK(fun,ret,pbuf,buf_len){\
	Udebug.ERROR_MSG_LOG("[%s][NDK][%s][%s][%d][%s:%d]\n",VERSION,FILE(__FILE__),__FUNCTION__,__LINE__,fun,ret);\
	if(pbuf != NULL && buf_len != 0) \
		Udebug.ERROR_MSG_LOG_String(pbuf,buf_len);\
}

#define LOGD_FMT(fmt...){\
	Udebug.DEBUG_Levelone("[%s][DEG][%s][%s][%d]\n",VERSION,FILE(__FILE__),__FUNCTION__,__LINE__);\
	Udebug.DEBUG_Levelone(fmt);\
}
#define LOGD_STR(tag,data,len){\
	Udebug.DEBUG_Levelone("[%s][DEG][%s][%s][%s][%d]\n",VERSION,FILE(__FILE__),__FUNCTION__,tag,__LINE__);\
	if(data != NULL && len != 0 )\
		Udebug.DEBUG_string_Levelone(data,len);\
}
#define LOGE_FMT(fmt...){\
	Udebug.ERROR_MSG_LOG("[%s][ERR][%s][%s][%d]\n",VERSION,FILE(__FILE__),__FUNCTION__,__LINE__);\
	Udebug.ERROR_MSG_LOG(fmt);\
}
#define LOGE_STR(tag,data,len){\
	Udebug.ERROR_MSG_LOG("[%s][ERR][%s][%s][%s][%d]\n",VERSION,FILE(__FILE__),__FUNCTION__,tag,__LINE__);\
	if(data != NULL && len != 0 )\
        Udebug.ERROR_MSG_LOG_String(data,len);\
}

#define EXEC_NDK(tag,funRet,expectRet,cmd) Log_ExecNdkFun(tag,funRet,expectRet,FILE(__FILE__),__FUNCTION__,__LINE__,cmd)

#define ERRMSG(errCode,cmd) Log_SetErrMsg(errCode,FILE(__FILE__),__FUNCTION__,__LINE__,cmd)

typedef enum {
	SDK_ERR_BASE =  -10000,
	SDK_ERR_NDK_NOT_SUPPORT = (SDK_ERR_BASE-1),
	SDK_ERR_PARAM = (SDK_ERR_BASE-2),
	SDK_ERR_MALLOC_FAILED = (SDK_ERR_BASE-3),
	SDK_ERR_TIMEOUT = (SDK_ERR_BASE-4),
	SDK_ERR_CANCEL = (SDK_ERR_BASE-5),
	SDK_ERR_MODULE_CARD = (SDK_ERR_BASE-20),
	SDK_ERR_CARD_MAG_BUSY = (SDK_ERR_MODULE_CARD-1),
	SDK_ERR_CARD_IC_BUSY = (SDK_ERR_MODULE_CARD-2),
	SDK_ERR_CARD_RFID_BUSY = (SDK_ERR_MODULE_CARD-3),
	SDK_ERR_CARD_NO_EXPECT  = (SDK_ERR_MODULE_CARD-4),
	SDK_ERR_MODULE_MAG = (SDK_ERR_BASE-40),
	SDK_ERR_MAG_NO_SWIPED = (SDK_ERR_MODULE_MAG-1),
	SDK_ERR_MODULE_IC = (SDK_ERR_BASE-60),
	SDK_ERR_MODULE_RFID = (SDK_ERR_BASE-80),
	SDK_ERR_MODULE_PIN = (SDK_ERR_BASE-100),
	SDK_ERR_MODULE_LIGHT = (SDK_ERR_BASE-120),
	SDK_ERR_MODULE_DEVICE = (SDK_ERR_BASE-140),
	SDK_ERR_MODULE_PRN = (SDK_ERR_BASE-160),
	SDK_ERR_PRN_TTF = (SDK_ERR_MODULE_PRN-1),
	SDK_ERR_MODULE_TERM = (SDK_ERR_BASE-180),
	SDK_ERR_MODULE_LED = (SDK_ERR_BASE-200),
	SDK_ERR_MODULE_FILE = (SDK_ERR_BASE-220),
	SDK_ERR_FILE_OPEN = (SDK_ERR_BASE-221),
}SDK_ERR;

#define ERROR_INFO_MAX   5

typedef struct {
	int cmd;
	char errCode[64];
	char errMsg[128];
	char otherMsg[128];
}ErrorInfo,*pErrorInfo;
extern void Log_ErrInfoInit();
extern void SetErrInfo(int cmd,int errCode,char *otherMsg);
extern int Log_GetErrInfo(JNIEnv *env,int cmd,jbyteArray errCode,jbyteArray errMsg,jbyteArray otherMsg);

#define IS_NAPI              0
#define IS_EMVL3             1

#endif
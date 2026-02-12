/**
 * Author by wuhh, Date on 2019/3/31 0022.
 */
#ifndef __LOG_H_
#define __LOG_H_

#include "comm.h"

#define VERSION        "V1.0.2"

extern void Log_DebugInit();
extern char *getErrMsg(int errCode);

extern int
Log_ExecNdkFun(char *tag, int funRet, int expectRet, const char *file, const char *function,
               long line);

extern void Log_SetErrMsg(int errCode, const char *file, const char *function, long line);

#define DEBUG_INIT  Log_DebugInit()

typedef struct {
    void (*DEBUG_Levelone)(char *fmt, ...);

    void (*DEBUG_Leveltwo)(char *fmt, ...);

    void (*DEBUG_string_Levelone)(char *data, int datalen, char* lpszFormat, ...);

    void (*DEBUG_string_Leveltwo)(char *data, int datalen, char* lpszFormat, ...);

    void (*ERROR_MSG_LOG)(char *fmt, ...);

    void (*ERROR_MSG_LOG_String)(char *data, int datalen, char* lpszFormat, ...);
} ST_DEBUG_API;

extern ST_DEBUG_API Udebug;

#define FILE(x) strrchr(x,'/')?strrchr(x,'/')+1:x

#define LOGE_NDK(fun, ret, pbuf, buf_len){\
    Udebug.ERROR_MSG_LOG("[%s][NDK][%s][%s][%d][%s:%d]\n",VERSION,FILE(__FILE__),__FUNCTION__,__LINE__,fun,ret);\
    if(pbuf != NULL && buf_len != 0) \
        Udebug.ERROR_MSG_LOG_String(pbuf,buf_len);\
}

#define LOGD_FMT(fmt,args...){\
    Udebug.DEBUG_Levelone("[%s][DEG][%s][%s][%d]"fmt,VERSION,FILE(__FILE__),__FUNCTION__,__LINE__, ##args);\
}
#define LOGD_STR(tag, data, len){\
        Udebug.DEBUG_string_Levelone(data,len, "[%s][DEG][%s][%s][%d]%s",VERSION,FILE(__FILE__),__FUNCTION__,__LINE__, tag);\
}
#define LOGE_FMT(fmt,args...){\
    Udebug.ERROR_MSG_LOG("[%s][ERR][%s][%s][%d]"fmt,VERSION,FILE(__FILE__),__FUNCTION__,__LINE__, ##args);\
}
#define LOGE_STR(tag, data, len){\
    Udebug.ERROR_MSG_LOG("[%s][ERR][%s][%s][%s][%d]\n",VERSION,FILE(__FILE__),__FUNCTION__,tag,__LINE__);\
    if(data != NULL && len != 0 )\
        Udebug.ERROR_MSG_LOG_String(data,len);\
}

#define EXEC_NDK(tag, funRet, expectRet) Log_ExecNdkFun(tag,funRet,expectRet,FILE(__FILE__),__FUNCTION__,__LINE__)

#define ERRMSG(errCode) Log_SetErrMsg(errCode,FILE(__FILE__),__FUNCTION__,__LINE__)

#endif
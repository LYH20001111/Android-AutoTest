/**
 * Author by bxy, Date on 2019/3/31 0022.
 */
#ifndef __LOG_H_
#define __LOG_H_

#include "comm.h"

#define VERSION        "V1.0.0"

extern void Log_DebugInit();
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

#endif
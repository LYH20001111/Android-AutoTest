#ifndef __LOG_H_
#define __LOG_H_
#include <string.h>
#include "mpos_api_frame.h"

#define NDK_DEBUG    0
#define NDK_VERSION  "1.4.2"

/**
 * 1.0.7:add NDK_SecVerifyPIN
 * 1.0.8:NDK_SecVerifyPIN 修复返回值未置零问题.
 * 1.0.9:NAPI_SecGenerateMAC
 * 1.1.0:NDK_SecGetPinResult status esc.
 * 1.1.1:NDK_Script_Print 去掉4000判断
 * 1.1.2:修改日志级别为debug
 * 1.1.3:NDK_FelicaSetTimeout
 * 1.1.4:增加字符转点阵接口.
 * 1.1.5:新增非对称密钥接口.
 * 1.1.6:打印判断硬件配置码来决定是否调用ModuleInit
 * 1.1.7:NDK_RfidSetPiccParam
 * 1.1.8:NDK_RfidApduCustom (2021-05-20)
 * 1.1.9:NDK_RfidPiccApduInTransMode (2021-06-11)
 * 1.2.0:NDK_RfidConfig (2021-06-17)
 * 1.2.1:NDK_LedSetFlickParam (2021-10-09)
 * 1.2.2:NDK_AlgSM2Sign_YS(for yinsheng 2021-11-09)
 * 1.2.3:NDK_PortRead -10 (2021-11-11)
 * 1.2.4:NDK_Script_Print from libnlprnapi.so (2021-11-25)
 * 1.2.5:NDK_PrnSetParam 2021-11-30
 * 1.2.6:motify printFlag.
 * 1.2.7:NDK_RfidEMVTest (2022-2-25)
 * 1.2.8:add Async NAPI  (2022-4-13)
 * 1.2.9:close log
 * 1.3.0:NDK_SecCalcDesDukpt2 support CBC
 * 1.3.1:NDK_RfidSetDetectType NDK_RfidDetectWithCardType
 * 1.3.2:TLE
 * 1.3.3:NDK_SysKeyVolSet
 * 1.3.4:P300 light
 * 1.3.5: NDK_SysDevBacklightCtrl
 * 1.3.6: NDK_SecGetPinDukpt
 * 1.3.8 add CE.
 * 1.3.9 add aes dukpt/rsa.
 * 1.4.0 add NAPI_SecVPPSetButtonFunc
 * 1.4.1 add NDK_SysTimeBeep_Ex NDK_SysSetBeepVol_Extern
 * 1.4.2 add NDK_SysGetBeepVol_Extern
 */
#if 1
typedef struct {
	void (*DEBUG_Levelone)(char * fmt,...);
	void (*DEBUG_Leveltwo)(char * fmt,...);
	void (*DEBUG_string_Levelone)(char *data,int datalen);
	void (*DEBUG_string_Leveltwo)(char *data,int datalen);
	void (*ERROR_MSG_LOG)(char * fmt,...);
	void (*ERROR_MSG_LOG_String)(char *data,int datalen);
}debug_api;
#endif

extern debug_api Udebug;

#define FILE(x) strrchr(x,'/')?strrchr(x,'/')+1:x

#define LOGD_FMT(fmt...){\
	Udebug.DEBUG_Levelone("[%s][DEG][%s][%s][%d]\n",NDK_VERSION,FILE(__FILE__),__FUNCTION__,__LINE__);\
	Udebug.DEBUG_Levelone(fmt);\
}
#define LOGD_STR(tag,data,len){\
	Udebug.DEBUG_Levelone("[%s][DEG][%s][%s][%s][%d]\n",NDK_VERSION,FILE(__FILE__),__FUNCTION__,tag,__LINE__);\
	if(data != NULL && len != 0 )\
		Udebug.DEBUG_string_Levelone(data,len);\
}

#define LOGE_FMT(fmt...){\
	Udebug.ERROR_MSG_LOG("[%s][ERR][%s][%s][%d]\n",NDK_VERSION,FILE(__FILE__),__FUNCTION__,__LINE__);\
	Udebug.ERROR_MSG_LOG(fmt);\
}
#define LOGE_STR(tag,data,len){\
	Udebug.ERROR_MSG_LOG("[%s][ERR][%s][%s][%s][%d]\n",NDK_VERSION,FILE(__FILE__),__FUNCTION__,tag,__LINE__);\
	if(data != NULL && len != 0 )\
        Udebug.ERROR_MSG_LOG_String(data,len);\
}

#if 0
#define DO_NDK_FUN(tag,funRet,expectRet) do_ndk_fun(tag,funRet,expectRet,FILE(__FILE__),__FUNCTION__,__LINE__)
	
static inline int do_ndk_fun(char *tag,int funRet,int expectRet,const char*file,const char*function,long line){
	if(funRet != expectRet){
		Udebug.ERROR_MSG_LOG("[%s][NDK_ERR][%s][%s][%d] %s:ndk ret[%d]\n",
		MPOS_CMD_VERSION,file,function,line,tag,funRet);
		return (1==0);
	}
	return (1==1);
}
#endif
#endif


/**
 * Author by wuhh, Date on 2019/3/31 0022.
 */
#include "include/common_log.h"
#include <android/log.h>
#include <stdint.h>
#include <sys/system_properties.h>
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <unistd.h>
#include <stdlib.h>
#include <jni.h>

extern JavaVM *gJavaVM;
static char g_errMsg[128];
static char g_otherMsg[128];
int Udebugopen2 = 0;
int Udebuglevel2 = 0;
ST_DEBUG_API Udebug;
FILE *fp_debug2 = NULL;

void Common_initLog();

#define LOG_TAG   "libserialport"
#define LOGI(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

int property_get(const char *key, char *value, const char *default_value)
{
    int len;
    len = __system_property_get(key, value);
    if(len > 0) {
        return len;
    }
    if(default_value) {
        len = strlen(default_value);
        memcpy(value, default_value, len + 1);
    }
    return len;
}

static void Common_Log_DebugLevel(void)
{
    int ret = 0;
    char propBuf[5] = {0};
    ret = property_get("persist.sys.nl_lib_debug", propBuf, "0");
    LOGI("Udebugopen2=%d",Udebugopen2);
    LOGI("Udebuglevel2=%d",Udebuglevel2);
    if(ret < 0) {
        return;
    }
    LOGI("propBuf[0]=%d",propBuf[0]);
    LOGI("propBuf[1]=%d",propBuf[1]);
    LOGI("Udebugopen2=%d",Udebugopen2);
    LOGI("Udebuglevel2=%d",Udebuglevel2);
    return;
}
void printf_fmt(char * fmt,...)
{
	int len;
	va_list arg;
	char str[4096];
	va_start( arg, fmt );
	if((len = vsprintf(str, fmt, arg)) < 0) {
		return;
	}
	if(Udebugopen2 == 1)
	{
		if (fp_debug2 == NULL) {
			fp_debug2 = fopen("/Share/debug_mpos.log", "a+");
			if (fp_debug2 == NULL) {
				LOGI("fopen /Share/debug_mpos.log Err!\n");
				return;
			}
		}

		fseek(fp_debug2,0,SEEK_END);
		fwrite(str, len, sizeof(char), fp_debug2);
	}
	else{
        LOGI("%s\r\n", str);
	}
	va_end( arg );
	return;
}

void printf_string(char *BUF,int LEN, char* lpszFormat, ...){
	int i;
	int len = 0;
	int size = 0;
	int temp = 0;
	int offset = 0;
	char s[2048] = {0};
	int nTitleLen = 0;
    va_list args;

    va_start(args, lpszFormat);
    vsprintf(s, lpszFormat, args);
    va_end(args);

	nTitleLen = strlen(s);

	if ((LEN*3) + nTitleLen < sizeof(s)){
	    for (i = 0; i < LEN; i++){
	        sprintf(s+strlen(s), "%02X ", *(BUF+i));
	    }
        printf_fmt("%s", s);
	} else {
        printf_fmt("%s", s);
        size = LEN;
        for(i=0; i < LEN; ) {
            offset = 0;
            memset(s, 0, sizeof(s));
            len = (size > 256) ? 256 : size;
            for(temp=0; temp < len; temp++) {
                offset += sprintf(s + offset, "%02x ", BUF[temp+i]);
            }
            i += len;
            size -= len;
//		s[offset-1] = '\n';
            printf_fmt("%s", s);
        }

	}

}

void printf_null(char * fmt,...){
	return;
}
void printf_string_null(char *BUF,int LEN, char* lpszFormat, ...){
	return;
}

void Common_Log_DebugInit()
{
    Common_Log_DebugLevel();
    Common_initLog();
}

void Common_initLog() {
    if(Udebuglevel2 == 2){
        Udebug.DEBUG_Levelone = printf_fmt;
        Udebug.DEBUG_Leveltwo = printf_fmt;
        Udebug.DEBUG_string_Levelone = printf_string;
        Udebug.DEBUG_string_Leveltwo = printf_string;
    }
    else if(Udebuglevel2 == 1){
        Udebug.DEBUG_Levelone = printf_fmt;
        Udebug.DEBUG_Leveltwo = printf_null;
        Udebug.DEBUG_string_Levelone = printf_string;
        Udebug.DEBUG_string_Leveltwo = printf_string_null;
    }
    else{
        Udebug.DEBUG_Levelone = printf_null;
        Udebug.DEBUG_Leveltwo = printf_null;
        Udebug.DEBUG_string_Levelone = printf_string_null;
        Udebug.DEBUG_string_Leveltwo = printf_string_null;
    }
    Udebug.ERROR_MSG_LOG = printf_fmt;
    Udebug.ERROR_MSG_LOG_String = printf_string;
}

void Common_setDebugLevel(int level) {
    Udebuglevel2 = level;
    LOGD_FMT("Udebuglevel2[%d]", Udebuglevel2);
    Common_initLog();
}
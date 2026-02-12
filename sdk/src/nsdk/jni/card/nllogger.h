#ifndef NL_LOGGER_H
#define NL_LOGGER_H

#include <android/log.h>
#include <stdarg.h>
#define LOG_NDK_TAG "libnsdk"

#define LOGGER_DEBUG(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_NDK_TAG,__VA_ARGS__)
#define LOGGER_ERROR(...) __android_log_print(ANDROID_LOG_ERROR,LOG_NDK_TAG,__VA_ARGS__)
#define LOGGER_INFO(...)  __android_log_print(ANDROID_LOG_INFO,LOG_NDK_TAG,__VA_ARGS__)


#define LOGGER_TRACE_ERROR(...) do{LOGGER_ERROR("[Func:%s,ln=%d,F:%s]",__FUNCTION__,__LINE__,__FILE__);LOGGER_ERROR(__VA_ARGS__);}while(0)
#define LOGGER_TRACE(...) do{LOGGER_DEBUG("[Func:%s,ln=%d,F:%s]",__FUNCTION__,__LINE__,__FILE__);LOGGER_DEBUG(__VA_ARGS__);}while(0)
#define LOGGER_TRACE_I(...) do{LOGGER_INFO("[Func:%s,ln=%d,F:%s]",__FUNCTION__,__LINE__,__FILE__);LOGGER_INFO(__VA_ARGS__);}while(0)

#define FILE(x) strrchr(x,'/')?strrchr(x,'/')+1:x

static inline int _is_expect_ret(int function_ret, 
	int expect_ret,
	const char*file,
	const char*function,
	long line)
	{ 
		return (function_ret==expect_ret)
				 ?(1==1)
				 :(LOGGER_ERROR("@FILE=%s,line=%ld, %s ret=%d,but expect:%d",file,line,function,function_ret,expect_ret),(0==1));
	}

#define LOGGER_IS_EXPECT_RET(FUNCTION, EXPECT)   _is_expect_ret((FUNCTION),(EXPECT),FILE(__FILE__),#FUNCTION,__LINE__)

#define LOGGER_ASSERT_EXPRESSION(expression)	_is_expect_ret((expression),(1==1),FILE(__FILE__),#expression,__LINE__)

#define _IF_COND_TRUE_THEN_DO(CONDITION, DO_FUNCTION)				\
	do{                                                             \
		if((CONDITION)){											\
			LOGGER_ASSERT_EXPRESSION((DO_FUNCTION)==NDK_OK);			\
		}															\
	}while(0);
#endif
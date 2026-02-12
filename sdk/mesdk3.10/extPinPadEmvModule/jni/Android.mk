LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE  := libextpinpademv

LOCAL_SRC_FILES  := $(wildcard $(LOCAL_PATH)/*.c)          \

LOCAL_C_INCLUDES := $(LOCAL_PATH)                          \


LOCAL_LDFLAGS  += -pie -fPIE
LOCAL_LDFLAGS  += -llog

include $(BUILD_SHARED_LIBRARY)



LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE  := libserialport

LOCAL_SRC_FILES  := $(wildcard $(LOCAL_PATH)/*.c)

LOCAL_C_INCLUDES := $(LOCAL_PATH)/include                          \
                    $(LOCAL_PATH)/


LOCAL_LDFLAGS  += -pie -fPIE
LOCAL_LDFLAGS  += -llog

ifeq ($(TARGET_ARCH_ABI),arm64-v8a)
    LOCAL_LDFLAGS += -Wl,-z,max-page-size=16384
endif

include $(BUILD_SHARED_LIBRARY)



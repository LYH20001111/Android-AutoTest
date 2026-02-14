LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE  := libnsdk

LOCAL_SRC_FILES  := $(wildcard $(LOCAL_PATH)/*.c)                  \
                    $(wildcard $(LOCAL_PATH)/crypto/*.c)           \
                    $(wildcard $(LOCAL_PATH)/card/*.c)             \
                    $(wildcard $(LOCAL_PATH)/modules/*.c)          \
                    $(wildcard $(LOCAL_PATH)/printer/*.c)          \
                    $(wildcard $(LOCAL_PATH)/threadtool/*.c)

LOCAL_C_INCLUDES := $(LOCAL_PATH)/include                          \
                    $(LOCAL_PATH)/card                             \
                    $(LOCAL_PATH)/crypto                           \
                    $(LOCAL_PATH)/printer                          \
                    $(LOCAL_PATH)/threadtool


LOCAL_LDFLAGS  += -pie -fPIE
LOCAL_LDFLAGS  += -llog
ifeq ($(TARGET_ARCH_ABI),arm64-v8a)
    LOCAL_LDFLAGS += -Wl,-z,max-page-size=16384
endif

include $(BUILD_SHARED_LIBRARY)



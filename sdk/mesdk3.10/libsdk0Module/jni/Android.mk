LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE  := libintelligentLib

LOCAL_SRC_FILES  := $(wildcard $(LOCAL_PATH)/*.c)                  \
                    $(wildcard $(LOCAL_PATH)/card/*.c)             \
                    $(wildcard $(LOCAL_PATH)/modules/*.c)          \
                    $(wildcard $(LOCAL_PATH)/threadtool/*.c)       \
                    $(wildcard $(LOCAL_PATH)/crypto/*.c)           \
                    $(wildcard $(LOCAL_PATH)/emvl3/*.c)

LOCAL_C_INCLUDES := $(LOCAL_PATH)/include                          \
                    $(LOCAL_PATH)/card                             \
                    $(LOCAL_PATH)/threadtool                       \
                    $(LOCAL_PATH)/crypto                           \
                    $(LOCAL_PATH)/emvl3


LOCAL_LDFLAGS  += -pie -fPIE
LOCAL_LDFLAGS  += -llog
LOCAL_CFLAGS   := -D__STDC_CONSTANT_MACROS -Wl,-Map=test.map -g

include $(BUILD_SHARED_LIBRARY)



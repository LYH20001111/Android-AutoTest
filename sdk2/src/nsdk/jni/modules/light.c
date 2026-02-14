#include <memory.h>
#include <stdlib.h>
#include "api.h"
#include "comm.h"
#include "log.h"
#include "unistd.h"

int FLight_Blink(int count,int color,int interval) {
    uint status = 0,i;

    if (color == 0 || color > 0x1f) {
        return NDK_ERR_PARA;
    }

    for (i = 0; i < 8; i++) {
        switch (color & (0x01 << i)) {
            case 0x01:
                status |= LED_RFID_BLUE_ON;//蓝色灯
                break;
            case 0x02:
                status |= LED_RFID_GREEN_ON;//绿色灯
                break;
            case 0x04:
                status |= LED_RFID_YELLOW_ON;//黄色灯
                break;
            case 0x08:
                status |= LED_RFID_RED_ON;//红色灯
                break;
            case 0x10:
                status |= LED_MAG_ON;//磁卡指示灯
                break;
            default:
                break;
        }
    }

    for (i = 0; i < count; i++) {
        if(!EXEC_NDK("NDK_LedStatus",NDK_LedStatus(status),NDK_OK)){//亮
            return NDK_ERR;
        }
        if(!EXEC_NDK("NDK_SysMsDelay",NDK_SysMsDelay(interval),NDK_OK)){
            return NDK_ERR;
        }
        if(!EXEC_NDK("NDK_LedStatus",NDK_LedStatus(status << 1),NDK_OK)){//灭
            return NDK_ERR;
        }
        if(!EXEC_NDK("NDK_SysMsDelay",NDK_SysMsDelay(interval),NDK_OK)){
            return NDK_ERR;
        }
    }
    return NDK_OK;
}

int FLight_Blink_Virtual(int count, int color, int interval) {
    uint status = 0,i;
    uint offstatus = 0;

    if (color == 0 || color > 0x1f) {
        return NDK_ERR_PARA;
    }

    for (i = 0; i < 8; i++) {
        switch (color & (0x01 << i)) {
            case 0x01:
                status |= LED_RFID_BLUE_FLICK;//蓝色灯
                offstatus |= LED_RFID_BLUE_OFF;
                break;
            case 0x02:
                status |= LED_RFID_GREEN_FLICK;
                offstatus |= LED_RFID_GREEN_OFF;//绿色灯
                break;
            case 0x04:
                status |= LED_RFID_YELLOW_FLICK;//黄色灯
                offstatus |= LED_RFID_YELLOW_OFF;
                break;
            case 0x08:
                status |= LED_RFID_RED_FLICK;//红色灯
                offstatus |= LED_RFID_RED_OFF;
                break;
            case 0x10:
                status |= LED_MAG_FLICK;//磁卡指示灯
                offstatus |= LED_MAG_OFF;
                break;
            default:
                break;
        }
    }

    ST_NDK_LED_FLICK stNdkLedFlick;
    memset(&stNdkLedFlick, 0x00, sizeof(ST_NDK_LED_FLICK));
    long t = interval / 100l;
    LOGD_FMT("interval set to NDK:%l", t);
    stNdkLedFlick.unFlickOn = t;
    stNdkLedFlick.unFlickOff = t;
    if (!EXEC_NDK("NDK_LedSetFlickParam", NDK_LedSetFlickParam(status, stNdkLedFlick), NDK_OK)) {
        return NDK_ERR;
    }
    for (int j = 0; j < count * 2 - 1; j++) {
        if (!EXEC_NDK("NDK_SysMsDelay", NDK_SysMsDelay(interval), NDK_OK)) {
            return NDK_ERR;
        }
    }
    if (!EXEC_NDK("NDK_LedStatus", NDK_LedStatus(offstatus), NDK_OK)) {
        return NDK_ERR;
    }


    return NDK_OK;
}

int FLight_SetStatus(int mode,int color) {
    int i;
    uint status = 0;

    if (color == 0 || color > 0x1f) {
        return NDK_ERR_PARA;
    }

    for (i = 0; i < 8; i++) {
        switch (color & (0x01 << i)) {
            case 0x01:
                status |= LED_RFID_BLUE_ON;//蓝色灯
                break;
            case 0x02:
                status |= LED_RFID_GREEN_ON;//绿色灯
                break;
            case 0x04:
                status |= LED_RFID_YELLOW_ON;//黄色灯
                break;
            case 0x08:
                status |= LED_RFID_RED_ON;//红色灯
                break;
            case 0x10:
                status |= LED_MAG_ON;//磁卡指示灯
                break;
            default:
                break;
        }
    }

    if (mode == 0x00) {
        if(!EXEC_NDK("NDK_LedStatus",NDK_LedStatus(status << 1),NDK_OK)){//熄灭
            return NDK_ERR;
        }
    } else if (mode == 0x01) {
        if(!EXEC_NDK("NDK_LedStatus",NDK_LedStatus(status),NDK_OK)){//点亮
            return NDK_ERR;
        }
    } else if (mode == 0x02) {
        if(!EXEC_NDK("NDK_LedStatus",NDK_LedStatus((status) | (status << 1)),NDK_OK)){ //闪烁
            return NDK_ERR;
        }
    }
    return NDK_OK;
}

int FLight_SetStatusLT1118(int buf[], jint bufLen, jint lightCount) {
    int ret = 0;
    uint offset = 0;
    unsigned long long allStatus = 0x80000000000;

    // 按照灯的顺序进行遍历
    LOGD_FMT(">>>NDK_LedLt1118Status lightCount[%d], bufLen[%d]", lightCount, bufLen);
    for (int i = 0; i < lightCount; ++i) {
        unsigned long long temp = 0x00000000000;

        // 取出当前灯的颜色和状态
        int tempNumber = buf[offset];
        offset++;
        int tempColor = buf[offset];
        offset++;
        int tempStatus = buf[offset];
        offset++;
        LOGD_FMT(">>>NDK_LedLt1118Status tempNumber[%d], tempColor[%d], tempStatus[%d]", tempNumber, tempColor, tempStatus);

        // 先设置好状态。后续再根据灯的顺序和颜色进行位移
        if (tempStatus == 0) {
            temp |= LED_RFID_RED_FLICK;
        } else if (tempStatus == 1) {
            temp |= LED_RFID_RED_ON;
        } else if (tempStatus == 2) {
            temp |= LED_RFID_RED_OFF;
        }

        // 每个灯可以有三个颜色，按顺序：红色，绿色，蓝色，每个颜色 2 个位表示，一个灯总共用 6 位来表示，然后：
        // 红色：不用位移
        // 绿色：往左位移 2 位
        // 蓝色：往左位移 4 位
        int bitsToMove = 0;
        if (tempColor == 3) {
            // red
            bitsToMove = (tempNumber - 1) * 6;
        } else if (tempColor == 1) {
            // green
            bitsToMove = (tempNumber - 1) * 6 + 2;
        } else if (tempColor == 0) {
            // blue
            bitsToMove = (tempNumber - 1) * 6 + 4;
        }

        temp = temp << bitsToMove;

        LOGD_FMT(">>>NDK_LedLt1118Status current light status[%llX]", temp);

        allStatus |= temp;
    }

    LOGD_FMT(">>>NDK_LedLt1118Status all status[%llX]", allStatus);
    ret = NDK_LedLt1118Status(allStatus);
    LOGD_FMT(">>>NDK_LedLt1118Status ret[%d]", ret);

    return ret;
}

int FLight_blink_Virtual_Advanced(int x, int y, int horizontal, int alwaysDisplayBackground, int count, int color, int onDuration, int offDuration) {
    LOGD_FMT("x[%d], y[%d], horizontal[%d], alwaysDisplayBackground[%d], count[%d], onDuration[%d], offDuration[%d]", x, y, horizontal, alwaysDisplayBackground, count, onDuration, offDuration);
    ST_NDK_LED_FLICK stNdkLedFlick;
    memset(&stNdkLedFlick, 0x00, sizeof(ST_NDK_LED_FLICK));
    stNdkLedFlick.x = x;
    stNdkLedFlick.y = y;
    stNdkLedFlick.unFlickOn = onDuration;
    stNdkLedFlick.unFlickOff = offDuration;
    stNdkLedFlick.horizontal = horizontal;
    stNdkLedFlick.alwaysDisplayBackground = alwaysDisplayBackground;
    stNdkLedFlick.version = 0x4C454402;
    int ret = 0;

    if (count <= 0) {
        ret = NDK_LedSetFlickParam(color, stNdkLedFlick);
        LOGD_FMT("NDK_LedSetFlickParam ret = %d");
    } else {
        int colorOff = 0;
        if ((color & LED_RFID_BLUE_FLICK) == LED_RFID_BLUE_FLICK) {
            colorOff |= LED_RFID_BLUE_OFF;
        }
        if ((color & LED_RFID_GREEN_FLICK) == LED_RFID_GREEN_FLICK) {
            colorOff |= LED_RFID_GREEN_OFF;
        }
        if ((color & LED_RFID_YELLOW_FLICK) == LED_RFID_YELLOW_FLICK) {
            colorOff |= LED_RFID_YELLOW_OFF;
        }
        if ((color & LED_RFID_RED_FLICK) == LED_RFID_RED_FLICK) {
            colorOff |= LED_RFID_RED_OFF;
        }
        ret = NDK_LedSetFlickParam(color, stNdkLedFlick);
        for (int i = 0; i < count; i++) {
            if (!EXEC_NDK("NDK_SysMsDelay", NDK_SysMsDelay(onDuration * 100), NDK_OK)) {
                return NDK_ERR;
            }
            if (i == count - 1) {
                if (!EXEC_NDK("NDK_SysMsDelay", NDK_SysMsDelay(offDuration * 100 - 10), NDK_OK)) {
                    return NDK_ERR;
                }
            } else {
                if (!EXEC_NDK("NDK_SysMsDelay", NDK_SysMsDelay(offDuration * 100), NDK_OK)) {
                    return NDK_ERR;
                }
            }
        }
        if (!EXEC_NDK("NDK_LedStatus", NDK_LedStatus(colorOff), NDK_OK)) {
            return NDK_ERR;
        }
    }
    return ret;
}

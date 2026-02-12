#include <memory.h>
#include "api.h"
#include "comm.h"
#include "log.h"
#include <crypto.h>
#include "command.h"

int Light_Blink(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    uint count,i;
    uint color;
    uint status = 0;
    uint interval;
    char headCode[2];
    memcpy(headCode, CMD_OK, 2);
    count = nlMpos_Command.mpos_getvar(pbuf + MPOS_VARIABLE_OFFSET + 0, _VAR_BIT8);
    color = nlMpos_Command.mpos_getvar(pbuf + MPOS_VARIABLE_OFFSET + 1, _VAR_BIT8);
    interval = nlMpos_Command.mpos_getvar(pbuf + MPOS_VARIABLE_OFFSET + 2, _VAR_BIT16);

    interval = nlMpos_Command.mpos_endian_swab16(interval);

    if (color == 0 || color > 0x1f) {
        memcpy(headCode, CMD_ERR_PARAM, 2);
        goto ON_ACK;
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
        if(!EXEC_NDK("NDK_LedStatus",NDK_LedStatus(status),NDK_OK,LIGHT_BLINK)){//亮
            memcpy(headCode, CMD_ERR_OTHER, 2);
            goto ON_ACK;
        }
        if(!EXEC_NDK("NDK_SysMsDelay",NDK_SysMsDelay(interval),NDK_OK,LIGHT_BLINK)){
            memcpy(headCode, CMD_ERR_OTHER, 2);
            goto ON_ACK;
        }
        if(!EXEC_NDK("NDK_LedStatus",NDK_LedStatus(status << 1),NDK_OK,LIGHT_BLINK)){//灭
            memcpy(headCode, CMD_ERR_OTHER, 2);
            goto ON_ACK;
        }
        if(!EXEC_NDK("NDK_SysMsDelay",NDK_SysMsDelay(interval),NDK_OK,LIGHT_BLINK)){
            memcpy(headCode, CMD_ERR_OTHER, 2);
            goto ON_ACK;
        }
    }
    ON_ACK:
    responseCmd(pOut, 0, outLen, headCode);
    return 0;
}

int Light_SetStatus(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int i;
    uint mode;
    uint color;
    uint status = 0;
    char headCode[2];
    memcpy(headCode, CMD_OK, 2);

    mode = nlMpos_Command.mpos_getvar(pbuf + 0, _VAR_BIT8);
    color = nlMpos_Command.mpos_getvar(pbuf + 1, _VAR_BIT8);

    if (color == 0 || color > 0x1f) {
        memcpy(headCode, CMD_ERR_PARAM, 2);
        goto ON_ACK;
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
        if(!EXEC_NDK("NDK_LedStatus",NDK_LedStatus(status << 1),NDK_OK,LIGHT_SETSTATUS)){//熄灭
            memcpy(headCode, CMD_ERR_OTHER, 2);
            goto ON_ACK;
        }
    } else if (mode == 0x01) {
        if(!EXEC_NDK("NDK_LedStatus",NDK_LedStatus(status),NDK_OK,LIGHT_SETSTATUS)){//点亮
            memcpy(headCode, CMD_ERR_OTHER, 2);
            goto ON_ACK;
        }
    } else if (mode == 0x02) {
        if(!EXEC_NDK("NDK_LedStatus",NDK_LedStatus((status) | (status << 1)),NDK_OK,LIGHT_SETSTATUS)){ //闪烁
            memcpy(headCode, CMD_ERR_OTHER, 2);
            goto ON_ACK;
        }
    }
    ON_ACK:
    responseCmd(pOut, 0, outLen, headCode);
    return 0;
}

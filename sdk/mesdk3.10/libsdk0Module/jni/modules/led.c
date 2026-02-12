#include <stdio.h>
#include <string.h>

#include "led.h"
#include "log.h"
#include "api.h"
static int g_N550Fd = -1;

static int openGuestDisplayDev(int devType) {
    if (devType == GUESTDISPLAY_N550) {
        if (g_N550Fd < 0) {
            g_N550Fd = open("/dev/digled", O_RDWR);
            if (g_N550Fd < 0) {
                LOGD_FMT(">>>g_N550 open failed Fd[%d].", g_N550Fd);
                return -1;
            }
            return g_N550Fd;
        }
    }
    return -1;
}

static void closeGuestDisplayDev(int devType) {
    if (devType == GUESTDISPLAY_N550) {
        int ret = close(g_N550Fd);
        if (ret != 0) {
            LOGD_FMT(">>>N550 close failed  Fd[%d].", g_N550Fd);
        }
        g_N550Fd = -1;
    }
}

int Led_GetVersion(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int devType = -1, ret = -1, dealResult = DEALRESULT_ERR;
    char version[128];
    devType = nlMpos_Command.mpos_getvar(pbuf, _VAR_BIT8);
    LOGD_FMT(">>>ParseResult devType[%d]", devType);
    if (devType == GUESTDISPLAY_N550) {
        if (openGuestDisplayDev(devType) < 0)
            goto ERROR;
        memset(version, 0, sizeof(version));
        ret = ioctl(g_N550Fd, DLED_IOCG_VER, version);
        if (ret < 0) {
            LOGD_FMT(">>>N550 Fd[%d] ioctl get version error", g_N550Fd);
            goto ERROR;
        }
        int len = strlen(version);
        LOGD_FMT(">>>N550 Version[%s] len[%d]", version, len);
        LOGD_STR("Version", version, len);
        int offset = 2;
        nlMpos_Command.mpos_writelen(pOut + offset, len, _VAR_BIT16);
        offset += 2;
        memcpy(pOut + offset, version, len);
        offset += len;
        responseCmd(pOut, offset - 2, outLen, CMD_OK);
        dealResult = DEALRESULT_OK;
    }
    ERROR:
    closeGuestDisplayDev(devType);
    if (dealResult == DEALRESULT_ERR) {
        responseCmd(pOut, 0, outLen, CMD_ERR_OTHER);
    }
    return 0;
}

int Led_SetBrightness(puchar pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int devType = -1, brightValue = -1, ret = -1, dealResult = DEALRESULT_ERR;
    devType = nlMpos_Command.mpos_getvar(pbuf, _VAR_BIT8);
    brightValue = nlMpos_Command.mpos_getvar(pbuf + 1, _VAR_BIT8);
    LOGD_FMT(">>>ParseResult devType[%d] brightValue[%d]", devType, brightValue);
    if (devType == GUESTDISPLAY_N550) {
        if (brightValue < 0 || brightValue > 7)
            goto ERROR;
        if (openGuestDisplayDev(devType) < 0)
            goto ERROR;

        ret = ioctl(g_N550Fd, DLED_IOCS_BRIGHT, &brightValue);
        if (ret < 0) {
            LOGD_FMT(">>>N550 Fd[%d] ioctl set brightness error", g_N550Fd);
            goto ERROR;
        }
        dealResult = DEALRESULT_OK;
        int offset = 2;
        memcpy(pOut + offset, GUESTDISPLAY_OK, 2);
        offset += 2;
        responseCmd(pOut, offset - 2, outLen, CMD_OK);
    }
    ERROR:
    closeGuestDisplayDev(devType);
    if (dealResult == DEALRESULT_ERR) {
        responseCmd(pOut, 0, outLen, CMD_ERR_OTHER);
    }
    return 0;
}

int Led_TurnOn(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    int devType = -1, ret = -1, dealResult = DEALRESULT_ERR, valueLen;
    char *showValue;
    devType = nlMpos_Command.mpos_getvar(pbuf, _VAR_BIT8);
    valueLen = nlMpos_Command.mpos_readlen(pbuf + 1, _VAR_BIT16);
    showValue = pbuf + 3;
    LOGD_FMT(">>>ParseResult devType[%d] valueLen[%d]", devType, valueLen);
    LOGD_STR("showValue", showValue, valueLen);
    if (devType == GUESTDISPLAY_N550) {
        if (valueLen > 7) {
            goto ERROR;
        }
        if (openGuestDisplayDev(devType) < 0)
            goto ERROR;

        N550_DIGLED param;
        param.justify = 1;
        param.buf = showValue;
        ret = ioctl(g_N550Fd, DLED_IOCS_SHOW, &param);
        if (ret < 0) {
            LOGD_FMT(">>>N550 Fd[%d] ioctl error", g_N550Fd);
            goto ERROR;
        }
        dealResult = DEALRESULT_OK;
        int offset = 2;
        memcpy(pOut + offset, GUESTDISPLAY_OK, 2);
        offset += 2;
        responseCmd(pOut, offset - 2, outLen, CMD_OK);
    }
    ERROR:
    closeGuestDisplayDev(devType);
    if (dealResult == DEALRESULT_ERR) {
        responseCmd(pOut, 0, outLen, CMD_ERR_OTHER);
    }
    return 0;
}

int Led_TurnOff(unsigned char *pbuf, int buf_len, unsigned char *pOut, int *outLen) {
    LOGE_FMT("Led_TurnOff");
    int devType = -1, ret = -1, dealResult = DEALRESULT_ERR;
    devType = nlMpos_Command.mpos_getvar(pbuf, _VAR_BIT8);
    LOGD_FMT(">>>ParseResult devType[%d]", devType);
    if (devType == GUESTDISPLAY_N550) {
        if (openGuestDisplayDev(devType) < 0)
            goto ERROR;

        ret = ioctl(g_N550Fd, DLED_IOCS_CLR, NULL);
        if (ret < 0) {
            LOGD_FMT(">>>N550 Fd[%d] ioctl error", g_N550Fd);
            goto ERROR;
        }
        dealResult = DEALRESULT_OK;
        int offset = 2;
        memcpy(pOut + offset, GUESTDISPLAY_OK, 2);
        offset += 2;
        responseCmd(pOut, offset - 2, outLen, CMD_OK);

    }
    ERROR:
    closeGuestDisplayDev(devType);
    if (dealResult == DEALRESULT_ERR) {
        responseCmd(pOut, 0, outLen, CMD_ERR_OTHER);
    }
    return 0;
}

